package com.example.wifiappone;

//Для описания полей пунктов списка
public class Element {
    private String title;
    private String security;
    private String level;
    private String BSSID;
    private String freq;

    public Element(String title, String security, String level, String BSSID, String freq) {
        this.title = title;
        this.security = security;
        this.level = level;
        this.BSSID = BSSID;
        this.freq = freq;
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

    public String getFreq() {
        return  freq;
    }
}
