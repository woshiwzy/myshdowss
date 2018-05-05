package com.vm.shadowsocks.tool;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.vm.shadowsocks.App;
import com.vm.shadowsocks.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

/**
 * Created by wangzy on 2017/11/22.
 */

public class Tool {


    public static String getSystemVersion(){
        return Build.VERSION.RELEASE;
    }

    public static String getCountryCode(){
        return Locale.getDefault().getCountry();
    }



    public static String getTimeZone(){

        TimeZone tz=TimeZone.getDefault();
        String s=tz.getDisplayName(false,TimeZone.SHORT)+"/"+tz.getID();
        return s;
    }

    public static String getLocalIpAddress() {

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address))
                    {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        }
        catch (SocketException ex){
            ex.printStackTrace();
        }
        return "";
    }


    private static final String marshmallowMacAddress = "02:00:00:00:00:00";
    private static final String fileAddressMac = "/sys/class/net/wlan0/address";

    public static String getAdresseMAC(Context context) {
        WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();

        if (wifiInf != null && marshmallowMacAddress.equals(wifiInf.getMacAddress())) {
            String result = null;
            try {
                result = getAdressMacByInterface();
                if (result != null) {
                    return result;
                } else {
                    result = getAddressMacByFile(wifiMan);
                    return result;
                }
            } catch (IOException e) {
                Log.e("MobileAccess", "Erreur lecture propriete Adresse MAC");
            } catch (Exception e) {
                Log.e("MobileAcces", "Erreur lecture propriete Adresse MAC ");
            }
        } else {
            if (wifiInf != null && wifiInf.getMacAddress() != null) {
                return wifiInf.getMacAddress();
            } else {
                return "";
            }
        }
        return marshmallowMacAddress;
    }

    private static String getAddressMacByFile(WifiManager wifiMan) throws Exception {
        String ret;
        int wifiState = wifiMan.getWifiState();

        wifiMan.setWifiEnabled(true);
        File fl = new File(fileAddressMac);
        FileInputStream fin = new FileInputStream(fl);
        ret = crunchifyGetStringFromStream(fin);
        fin.close();

        boolean enabled = WifiManager.WIFI_STATE_ENABLED == wifiState;
        wifiMan.setWifiEnabled(enabled);
        return ret;
    }


    private static String crunchifyGetStringFromStream(InputStream crunchifyStream) throws IOException {
        if (crunchifyStream != null) {
            Writer crunchifyWriter = new StringWriter();

            char[] crunchifyBuffer = new char[2048];
            try {
                Reader crunchifyReader = new BufferedReader(new InputStreamReader(crunchifyStream, "UTF-8"));
                int counter;
                while ((counter = crunchifyReader.read(crunchifyBuffer)) != -1) {
                    crunchifyWriter.write(crunchifyBuffer, 0, counter);
                }
            } finally {
                crunchifyStream.close();
            }
            return crunchifyWriter.toString();
        } else {
            return "No Contents";
        }
    }

    private static String getAdressMacByInterface() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (nif.getName().equalsIgnoreCase("wlan0")) {
                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        return "";
                    }

                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(String.format("%02X:", b));
                    }

                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }
                    return res1.toString();
                }
            }

        } catch (Exception e) {
            Log.e("MobileAcces", "Erreur lecture propriete Adresse MAC ");
        }
        return null;
    }


    public static void toast(String tips, Activity activity) {
        Toast.makeText(activity, tips, Toast.LENGTH_SHORT).show();
    }

    public static void toast(int tips, Activity activity) {
        Toast.makeText(activity, tips, Toast.LENGTH_SHORT).show();
    }

    public static boolean isZh() {
        Locale locale = App.instance.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        return language.endsWith("zh");
    }

    public static boolean isValidUrl(String url) {
        try {
            if (url == null || url.isEmpty())
                return false;

            if (url.startsWith("ss://")) {//file path
                return true;
            } else { //url
                Uri uri = Uri.parse(url);
                if (!"http".equals(uri.getScheme()) && !"https".equals(uri.getScheme()))
                    return false;
                if (uri.getHost() == null)
                    return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public static String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager == null) {
            return null;
        }

        try {
            return packageManager.getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }


    public static void hideShowSoftKeyboard(boolean show, Context context, View view) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);


        if (show) {
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        } else {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                return appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND;
            }
        }
        return false;
    }

    public static int getTotalRam(Context context) {//GB
        String path = "/proc/meminfo";
        String firstLine = null;
        int totalRam = 0;
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader br = new BufferedReader(fileReader, 8192);
            firstLine = br.readLine().split("\\s+")[1];
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (firstLine != null) {
            totalRam = (int) Math.ceil((new Float(Float.valueOf(firstLine) / (1024 * 1024)).doubleValue()));
        }

        return totalRam;//返回1GB/2GB/3GB/4GB
    }


    public static void openGpsSettignWithResultCode(Activity activity, int resultCode) {

        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        activity.startActivityForResult(intent, resultCode); //

    }

    public static boolean isOpenGPS(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps || network;

    }


    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        java.lang.reflect.Field field = null;
        int x = 0;
        int statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
            return statusBarHeight;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    public static int getScreenOrientation(Activity activity) {
        return activity.getResources().getConfiguration().orientation;
    }

    public static int getSigedHash(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            int hashcode = sign.hashCode();

            return hashcode;

//            return hashcode == -82892576 ? 1 : 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    private static final double EARTH_RADIUS = 6378137.0;

    public static double getDistance(double longitude1, double latitude1,
                                     double longitude2, double latitude2) {
        double Lat1 = rad(latitude1);
        double Lat2 = rad(latitude2);
        double a = Lat1 - Lat2;
        double b = rad(longitude1) - rad(longitude2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(Lat1) * Math.cos(Lat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }


    public static void openAppOnStore(Context context, String packageName) {
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + packageName));
        context.startActivity(viewIntent);
    }


    /**
     * Get address list from text
     *
     * @param context
     * @param strAddress
     * @param size
     * @return
     * @throws IOException
     */
    public static List<Address> getLocationFromAddress(Context context, String strAddress, int size) throws IOException {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        address = coder.getFromLocationName(strAddress, size);


        if (address == null) {
            return null;
        }
        return address;

    }


    /**
     * Get address list from text
     *
     * @param context
     * @param strAddress
     * @param size
     * @return
     * @throws IOException
     */
    public static List<Address> getLocationFromAddress(Context context, String strAddress, int size, Locale locale) throws IOException {
        Geocoder coder = new Geocoder(context, locale);
        List<Address> address;
        address = coder.getFromLocationName(strAddress, size);


        if (address == null) {
            return null;
        }
        return address;

    }


    public static String getFileName(String path) {

        try {
            String fname = path.substring(path.lastIndexOf("/") + 1, path.length());
            return fname;
        } catch (Exception e) {

        }

        return null;
    }


    public static boolean isInstallGMS(Context context) {

        return Tool.isAppInstalled(context, "com.google.android.gms");

    }


    public static boolean isAppInstalled(Context context, String packagename) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    public static String getSignature(String pkgname, Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo packageInfo = manager.getPackageInfo(pkgname, PackageManager.GET_SIGNATURES);
            Signature[] signatures = packageInfo.signatures;
            StringBuilder builder = new StringBuilder();
            for (Signature signature : signatures) {
                builder.append(signature.toCharsString());
            }
            String signature = builder.toString();
            return signature;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getNavigationBarHeight(Activity activity) {
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    public static String getDisplayMetricsText(Context cx) {

        StringBuffer sbf = new StringBuffer();
        DisplayMetrics dm = new DisplayMetrics();
        dm = cx.getApplicationContext().getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        float density = dm.density;
        float xdpi = dm.xdpi;
        float ydpi = dm.ydpi;

        sbf.append(android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL + "\n");

        sbf.append("\nThe absolute width:" + String.valueOf(screenWidth) + " pixels\n");
        sbf.append("The absolute heightin:" + String.valueOf(screenHeight)
                + " pixels\n");
        sbf.append("The logical density of the display.:" + String.valueOf(density)
                + "\n");
        sbf.append("X dimension :" + String.valueOf(xdpi) + "pixels per inch\n");
        sbf.append("Y dimension :" + String.valueOf(ydpi) + "pixels per inch\n");
        return sbf.toString();
    }

    public static void sendShareFile(Context context, File file) {
        Intent email = new Intent(android.content.Intent.ACTION_SEND);
        email.setType("application/octet-stream");
//        String[] emailReciver = new String[] {  };
//
//        String emailTitle = "wechat content title";
//        String emailContent = "content";
//        email.putExtra(android.content.Intent.EXTRA_EMAIL, emailReciver);
//        email.putExtra(android.content.Intent.EXTRA_SUBJECT, emailTitle);
//        email.putExtra(android.content.Intent.EXTRA_TEXT, emailContent);
        email.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        context.startActivity(Intent.createChooser(email, "Send with"));

    }


    public static void sendShare(Context context, String content) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        context.startActivity(Intent.createChooser(shareIntent, "Share to"));
    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if (listView == null) return;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * getListView realChild View
     *
     * @param listView
     * @param wantedPosition
     * @return
     */
    public static View getChildViwFromList(ListView listView, int wantedPosition) {

        int firstPosition = listView.getFirstVisiblePosition() - listView.getHeaderViewsCount(); // This is the same as child #0

        int wantedChild = wantedPosition - firstPosition;
        if (wantedChild < 0 || wantedChild >= listView.getChildCount()) {
            return null;
        }
        View wantedView = listView.getChildAt(wantedChild);

        return wantedView;
    }


    /**
     * @param activity
     * @param id
     * @return
     */
    public static String readStringFromStream(Activity activity, int id) {
        try {
            InputStream is = activity.getResources().openRawResource(id);
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = -1;
            byte[] buffer = new byte[512];
            while ((i = is.read(buffer)) != -1) {
                bo.write(buffer, 0, i);
            }
            bo.flush();
            byte[] bytes = bo.toByteArray();
            String ret = new String(bytes);
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

    }

    public static float getFontHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return Math.abs(fm.descent - fm.ascent);
    }

    public static float getFontLeading(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return Math.abs(fm.leading - fm.ascent);
    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static byte[] getFileContent(String path) {
        File file = new File(path);
        if (file.exists()) {
            try {
                BufferedInputStream bfi = new BufferedInputStream(new FileInputStream(file));
                ByteArrayOutputStream bro = new ByteArrayOutputStream();
                byte[] buffer = new byte[512];
                int ret = -1;
                while ((ret = bfi.read(buffer)) != -1) {
                    bro.write(buffer, 0, ret);
                }
                bro.flush();
                return bro.toByteArray();
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static String getFileType(String fileUri) {
        File file = new File(fileUri);
        String fileName = file.getName();
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        return fileType;
    }

    public static long getTotalSizeOfFilesInDir(final File file) {
        if (file.isFile())
            return file.length();
        final File[] children = file.listFiles();
        long total = 0;
        if (children != null)
            for (final File child : children)
                total += getTotalSizeOfFilesInDir(child);
        return total;
    }

    public static long getCacheSize(Context context) {
        File cacheDir = context.getCacheDir();
        return cacheDir.getTotalSpace() - cacheDir.getUsableSpace();
    }


    public static void startActivityForResult(Activity activity, Class claz, int requestCode) {
        Intent intent = new Intent(activity, claz);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startPhotoZoom(Activity activity, Uri uri, File outputFile, int outputX, int outputY, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("return-data", true);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startCropImg(Activity activity, Bitmap data, int outputX, int outputY, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        intent.putExtra("data", data);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void chosePicFromAlbum(Activity activity, int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void chosePicFromCamera(Activity activity, int requestCode) {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void showMessageDialog(int string, Context ativity) {
        if (null == ativity || ((Activity) ativity).isFinishing()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(ativity);


        builder.setMessage(ativity.getResources().getString(string));
//        builder.setTitle("Notification");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();

    }

    public static void showMessageDialog(String message, Context ativity) {


        if (null == ativity || ((Activity) ativity).isFinishing()) {
            return;
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(ativity);
        builder.setMessage(message);
//        builder.setTitle("Notification");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();

    }


    public static byte[] int2bytearray(int i) {

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        buf.write(i);
        byte[] b = buf.toByteArray();
        return b;

        // byte[] result = new byte[4];
        // result[3] = (byte) ((i >> 24) & 0xFF);
        // result[2] = (byte) ((i >> 16) & 0xFF);
        // result[1] = (byte) ((i >> 8) & 0xFF);
        // result[0] = (byte) (i & 0xFF);
        // return result;
    }

    public static void testCrash() {
        int x = 0;
        int y = 1 / x;
    }


    /**
     * @return
     */
    public static boolean existSDCard() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * @param fromContext
     */
    public static void startActivity(Context fromContext, Class claz) {
        Intent i = new Intent();
        i.setClass(fromContext, claz);
        fromContext.startActivity(i);
    }


    public static void startActivity(Context fromContext, Class claz, Intent i) {
        i.setClass(fromContext, claz);
        fromContext.startActivity(i);
    }

    /**
     * @param dir
     */
    public static void installApk(Context context, String dir) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(dir)), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * @param apkDir
     * @return{packageName,version
     */
    public static String[] getAppApkInfo(Context context, String apkDir) {

        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkDir, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            String packageName = appInfo.packageName;
            String version = info.versionName;
            String infos[] = {packageName, version};
            return infos;
        }
        return null;
    }

    /**
     * @param finsrc
     * @param dst
     * @return
     */
    public static boolean fileCopy(InputStream finsrc, File dst) {
        try {
            if (null == dst) {
                return false;
            } else {
                dst.delete();
                dst.createNewFile();
                FileOutputStream fout = new FileOutputStream(dst);
                byte[] buffer = new byte[512];
                int readsize = 0;
                while ((readsize = finsrc.read(buffer)) != -1) {
                    fout.write(buffer, 0, readsize);
                }
                // fout.flush();
                finsrc.close();
                fout.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static float getScreenDensity(Context context) {

        return context.getResources().getDisplayMetrics().density;
    }

    public int DipToPixels(Context context, int dip) {
        final float SCALE = context.getResources().getDisplayMetrics().density;
        float valueDips = dip;
        int valuePixels = (int) (valueDips * SCALE + 0.5f);
        return valuePixels;
    }

    public float PixelsToDip(Context context, int Pixels) {
        final float SCALE = context.getResources().getDisplayMetrics().density;

        float dips = Pixels / SCALE;

        return dips;

    }


    public static int getVersionCode(Context context) {
        int version = 0;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            version = packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    /**
     * @param ctx
     * @param clasz
     */
    public static void startOtherActivity(Activity ctx, Class clasz) {
        Intent intent = new Intent();
        intent.setClass(ctx, clasz);
        ctx.startActivity(intent);
        // ctx.overridePendingTransition(R.anim.activity_anim_in,
        // R.anim.activity_anim_out);
    }

    /**
     * @param ctx
     * @return
     */
    public static Point getDisplayMetrics(Context ctx) {
        DisplayMetrics metrcis = ctx.getResources().getDisplayMetrics();
        Point metricsPoint = new Point();
        metricsPoint.x = metrcis.widthPixels;
        metricsPoint.y = metrcis.heightPixels;
        return metricsPoint;
    }

    /**
     * 参数必须为：getDisplayMetrics返回值
     *
     * @param point
     * @return
     */
    public static Point getRandomPoint(Point point) {
        Random rand = new Random();
        int rx = rand.nextInt(point.x);
        int ry = rand.nextInt(point.y);
        return new Point(rx, ry);
    }

    /**
     * @param width
     * @param height
     * @return
     */
    public static Point getDimensionsByDimens(int width, int height) {
        Random rand = new Random();
        int rx = rand.nextInt(width);
        int ry = rand.nextInt(height);
        return new Point(rx, ry);
    }

    /**
     * 创建一个dlgbuilder
     *
     * @param context
     * @param view
     * @return
     */
    public static AlertDialog.Builder createADialig(Context context, View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setView(view);
        return alertDialog;
    }

    /**
     * @param context
     * @param intent
     */
    public static void sendBoardCast(Context context, Intent intent) {
        context.sendBroadcast(intent);
    }

    /**
     * @param url
     */
    public static void startUrl(Context mContext, String url) {

        if (!url.startsWith("http://") && !url.startsWith("https://")) {

            url = "http://" + url;
        }

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        mContext.startActivity(i);
    }

    /**
     * @param context
     * @return
     */
    public static int getAppRect(Activity context) {
        Rect frame = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;// 状态栏

        int contentTop = context.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentTop - statusBarHeight;
        return titleBarHeight;
    }

    /**
     * @param activity
     * @return
     */
    public static int geteAppUnVisibleHeight(Activity activity) {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        int contentTop = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentTop - statusBarHeight;
        return statusBarHeight + titleBarHeight;
    }

    /**
     * @param dir
     */
    public static void mkdir(String dir) {
        File file = new File(dir);
        file.mkdir();
    }

    /**
     * @param context
     * @param destFile
     * @throws IOException
     */
    public static void copyAssetFile2Sdcard(Context context, String fname, String destFile) throws IOException {
        AssetManager asm = context.getAssets();
        File df = new File(destFile);
        if (df.exists() == false) {
            df.mkdir();
        } else {
            InputStream os = asm.open(fname);
            byte buf[] = new byte[256];
            int b = -1;
            FileOutputStream fout = new FileOutputStream(destFile + fname);
            while ((b = os.read(buf)) != -1) {
                fout.write(buf, 0, buf.length);
            }
            fout.flush();
            os.close();
            fout.close();
        }

    }

    public static void ToastShow(Activity activity, int id) {
        if (!activity.isFinishing()) {
            Toast.makeText(activity, activity.getResources().getString(id), Toast.LENGTH_SHORT).show();
        }
    }

    public static void ToastShow(Activity activity, String message) {
        if (!activity.isFinishing()) {
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * @param times
     */
    public static void delay(long times) {
        try {
            Thread.sleep(times);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static boolean isRightLocation(String lat, String lng) {
        try {
            float lt = Math.abs(Float.parseFloat(lat));
            float lg = Math.abs(Float.parseFloat(lng));
            return (lg > 0 && lg <= 180) && (lt > 0 && lt <= 90);
        } catch (Exception e) {
            return false;
        }
    }

    public static void exitApp() {

        android.os.Process.killProcess(android.os.Process.myPid());

    }


}
