package com.example.wifiappone;

//Для описания полей пунктов списка
public class Element {
    private String title;
    private String security;
    private String level;
    private String BSSID;

    public Element(String title, String security, String level, String BSSID) {
        this.title = title;
        this.security = security;
        this.level = level;
        this.BSSID = BSSID;
    }

    public String getTitle() {
        return title;
    }

    public String getSecurity() {
        return security;
    }

    public String getLevel() {
        return level;
    }

    public String getBSSID() {
        return  BSSID;
    }
}
