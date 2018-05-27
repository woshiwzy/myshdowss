package com.vm.shadowsocks

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import android.text.TextUtils
import com.avos.avoscloud.*
import com.taobao.sophix.PatchStatus
import com.taobao.sophix.SophixManager
import com.vm.greendao.db.DaoMaster
import com.vm.greendao.db.DaoSession
import com.vm.greendao.db.HistoryDao
import com.vm.shadowsocks.constant.Constant.TAG
import com.vm.shadowsocks.core.LocalVpnService
import com.vm.shadowsocks.domain.History
import com.vm.shadowsocks.domain.Log
import com.vm.shadowsocks.domain.Server
import com.vm.shadowsocks.greendao.Helper
import com.vm.shadowsocks.tool.LogUtil
import com.vm.shadowsocks.tool.SharePersistent
import com.vm.shadowsocks.tool.SystemUtil
import com.vm.shadowsocks.tool.Tool
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import java.util.*


class App : MultiDexApplication() {


    private var mHelper: Helper? = null
    //private DaoMaster.DevOpenHelper mHelper;  //基本使用
    private var db: SQLiteDatabase? = null
    private var mDaoMaster: DaoMaster? = null
    private var mDaoSession: DaoSession? = null

    companion object {
        var tag = "ss"
        lateinit var instance: App
        val password: String = "666666"
    }

    lateinit var hostList: MutableList<Server>

    override fun onCreate() {
        super.onCreate()
        instance = this
        SophixManager.getInstance().queryAndLoadNewPatch()

        try {
            PushService.setDefaultChannelId(this, "google")
            AVOSCloud.initialize(this, "jadP41WoqD4mptx79gok48JY-gzGzoHsz", "VbupgDD0dyLX3pHuxgV8QAp7")
        } catch (e: Exception) {
            LogUtil.e(App.tag, "init error")
        }

        AVAnalytics.enableCrashReport(this, true)
        LogUtil.DEBUG = BuildConfig.LOG
        setUpDataBase()
        loginDevice()


        try {
            AVInstallation.getCurrentInstallation().saveInBackground()
        } catch (e: Exception) {
        }

    }

    fun setTakePush() {
        var avUser = AVUser.getCurrentUser()
        if (null != avUser && null == avUser.get("installationId") && null != AVInstallation.getCurrentInstallation().installationId) {
            avUser.put("installationId", AVInstallation.getCurrentInstallation().installationId)
            avUser.saveEventually()
        }
    }

    fun loginDevice() {

        try {


            AVUser.logInInBackground(getUserName(), getPassword(), object : LogInCallback<AVUser>() {
                override fun done(p0: AVUser?, p1: AVException?) {
                    if (p0 == null) {
                        LogUtil.d(tag, "login fail")
                        registerDevice()
                    } else {
                        LogUtil.d(tag, "login success")
                        setTakePush()
                    }
                }
            })


        } catch (e: Exception) {
            LogUtil.e(App.tag, "login fail:" + e.localizedMessage)
        }


    }

    fun udpateUsedByte(totalbyte: Long) {

        launch(CommonPool) {
            //band reset
            var total = SharePersistent.getlong(App.instance, "totalbyte")
            var allTotal = total + totalbyte
            SharePersistent.savePreference(App.instance, "totalbyte", allTotal)
            try {
                val avUser = AVUser.getCurrentUser()
                var calendar = Calendar.getInstance()

                if (null != avUser && calendar.get(Calendar.DAY_OF_WEEK) == 2) {

                    var usedByte = avUser.getLong("used_bytes")
                    avUser.put("used_bytes", (usedByte + allTotal))
                    avUser.isFetchWhenSave = true
                    avUser.saveEventually(object : SaveCallback() {
                        override fun done(e: AVException?) {
                            if (null == e) {
                                LocalVpnService.m_SentBytes = 0
                                LocalVpnService.m_ReceivedBytes = 0
                                SharePersistent.savePreference(App.instance, "totalbyte", 0)
                            }
                        }
                    })
                }
            } catch (e: Exception) {
                LogUtil.e(App.tag, "本地流量清0")
            }

        }

    }

    /**
     * save log
     */
    fun saveLog(port: Int, method: String) {

        launch(CommonPool) {

            try {
                var log = Log()
                log.init()
                log.port = port
                log.method = method
                log.time = System.currentTimeMillis()
                getDaoSession().logDao.save(log)
            } catch (e: Exception) {
                e.printStackTrace()
                LogUtil.e(TAG, "exmsg" + e.localizedMessage)
            } finally {
                var count = getDaoSession().logDao.queryBuilder().count()
                if (count >= 10) {
                    var loglist = getDaoSession().logDao.queryBuilder().list()
                    saveLog2n(loglist)
                }

            }
        }

    }

    fun saveLog2n(loglist: MutableList<Log>) {

        try {

            var list = ArrayList<AVObject>()

            for (log in loglist) {

                var address = log.mac
                var ip = log.ip
                var brand = log.brand
                var model = log.model
                var imei = log.imei
                var avObject = AVObject("VPLOG");


                avObject.put("mac", address);
                avObject.put("ip", ip);
                avObject.put("brand", brand + "," + model);
                avObject.put("imei", imei);

                avObject.put("system_version", Tool.getSystemVersion())
                avObject.put("country", Tool.getCountryCode())
                avObject.put("app_version", Tool.getVersionName(App.instance))
                avObject.put("time_mm", log.time)
                avObject.put("method", log.method)
                avObject.put("port", log.port)

                var avUser = AVUser.getCurrentUser()

                if (null != avUser) {
                    avObject.put("user", avUser)
                    avObject.put("tag", avUser.get("alias_tag"))
                }

                list.add(avObject)
            }

            AVObject.saveAll(list)
            getDaoSession().logDao.deleteAll()

            LogUtil.e(App.tag, "save " + list.size + " success to ln & delete success")

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun incrementCount(host: String) {

        var history = getDaoSession().historyDao.queryBuilder().where(HistoryDao.Properties.Host.eq(host)).build().unique()
        if (null != history) {
            history.count = history.count + 1
            getDaoSession().historyDao.update(history)
        } else {
            var hisotry = History()
            hisotry.host = host
            hisotry.count = 1
            getDaoSession().historyDao.save(hisotry)
        }
    }

    /**
     * Get User name
     */
    fun getUserName(): String {

        try {
            var addressMac = Tool.getAdresseMAC(this)
            if (!TextUtils.isEmpty(addressMac)) {
                return addressMac
            }

            val imei = SystemUtil.getIMEI(this)
            if (!TextUtils.isEmpty(imei)) {
                return imei
            }
        } catch (e: Exception) {

        } finally {

        }

        return LocalVpnService.getAppInstallID(this)
    }

    fun getPassword(): String {
        return password
    }

    /**
     * 注册设备
     */
    fun registerDevice() {

        try {

            val address = Tool.getAdresseMAC(App.instance)
            val ip = Tool.getLocalIpAddress()
            val brand = SystemUtil.getDeviceBrand()
            val model = SystemUtil.getSystemModel()
            val imei = SystemUtil.getIMEI(this)


            var avUser = AVUser()
            avUser.username = getUserName()
            avUser.setPassword(getPassword())

            avUser.put("mac", address)
            avUser.put("ip", ip)
            avUser.put("brand", "$brand,$model")
            avUser.put("imei", imei)

            avUser.put("system_version", Tool.getSystemVersion())
            avUser.put("country", Tool.getCountryCode())
            avUser.put("app_version", Tool.getVersionName(this))

            avUser.signUpInBackground(object : SignUpCallback() {

                override fun done(p0: AVException?) {
                    if (null == p0) {
                        LogUtil.e(tag, "r-success")
                        setTakePush()
                    } else {
                        LogUtil.e(tag, "r-fail:" + p0?.localizedMessage)
                    }

                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.d(tag, "register error:" + e?.localizedMessage)
        }

    }


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)

        SophixManager.getInstance().setContext(this)
                .setAppVersion(Tool.getVersionName(this))
                .setAesKey(null)
                .setEnableDebug(true)
                .setPatchLoadStatusStub { mode, code, info, handlePatchVersion ->
                    // 补丁加载回调通知
                    if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                        // 表明补丁加载成功
                        com.vm.shadowsocks.tool.LogUtil.e(App.tag, "hot fix success======")
                    } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                        // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
                        // 建议: 用户可以监听进入后台事件, 然后调用killProcessSafely自杀，以此加快应用补丁，详见1.3.2.3
                        LogUtil.e(App.tag, "hot fix success======restart")
                        SophixManager.getInstance().killProcessSafely()
                    } else {
                        // 其它错误信息, 查看PatchStatus类说明
                    }
                }.initialize()
    }


    /**
     * 初始化数据库
     */
    private fun setUpDataBase() {

        // mHelper = new DaoMaster.DevOpenHelper(this, "notes-db", null);   //基本的
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
//        mHelper = Helper(GreenDaoUtils(this))
        mHelper = Helper((this))
        db = mHelper!!.getWritableDatabase()
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        if (null == mDaoMaster) {
            mDaoMaster = DaoMaster(db)
        }
        if (null == mDaoSession) {
            mDaoSession = mDaoMaster!!.newSession()
        }
    }

    /**
     * 获取daoSession
     */
    fun getDaoSession(): DaoSession {
        return mDaoSession!!
    }


}
