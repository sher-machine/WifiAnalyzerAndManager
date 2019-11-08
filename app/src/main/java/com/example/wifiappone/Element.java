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

       return level + "dBm";
    }

    public String getDistance() {
        double lvl = calculateDistance(Double.parseDouble(level),Double.parseDouble(freq));
        String rlvl = "≈"+String.format("%.2f",lvl).toString()+ "meters";
        return rlvl;
    }

    public String getBSSID() {
        return  BSSID;
    }

    public String getFreq() {
        return  freq + " MHz";
    }

    public String getChannel() {
        return "channel "+ setChannel(this.freq);
    }

    public double calculateDistance(double signalLevelInDb, double freqInMHz) {
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(signalLevelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }

    public String setChannel(String freq){
        String ch;
        switch (freq){
            case "2412":
                ch="1";
                break;

            case "2417":
                ch="2";
                break;

            case "2422":
                ch="3";
                break;
            case "2427":
                ch = "4";
                break;
            case "2432":
                ch = "5";
                break;
            case "2437":
                ch = "6";
                break;
            case "2442":
                ch = "7";
                break;
            case "2447":
                ch = "8";
                break;
            case "2452":
                ch = "9";
                break;
            case "2457":
                ch = "10";
                break;
            case "2462":
                ch = "11";
                break;
            case "2467":
                ch = "12";
                break;
            case "2472":
                ch = "13";
                break;
            case "2477":
                ch = "14";
                break;

            default:
                //throw new IllegalStateException("Unexpected value: " + freq);
                Integer c =  (Integer.parseInt(freq)-5000)/5;
                ch = c.toString();
        }
        return ch;
    }
}
