package com.vm.shadowsocks.domain;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class History {

    @Id(autoincrement = true)
    private Long id;

    @Unique
    private String host;

    private Long count;

    @Generated(hash = 2031170635)
    public History(Long id, String host, Long count) {
        this.id = id;
        this.host = host;
        this.count = count;
    }

    @Generated(hash = 869423138)
    public History() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
