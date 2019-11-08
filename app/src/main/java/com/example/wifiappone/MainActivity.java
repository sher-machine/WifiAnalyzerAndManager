package com.example.wifiappone;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import static android.net.wifi.WifiManager.WIFI_STATE_DISABLED;
import static android.net.wifi.WifiManager.WIFI_STATE_DISABLING;

public class MainActivity extends AppCompatActivity {

    private static final String TAG ="MAINACTIVITY" ;
    private Element [] nets;
    private WifiManager wifiManager;
    private List<ScanResult> wifiList;
    private WifiConfiguration config;
    private ListView list;


    private static final int REQUEST_STATE = 1;

    Button tochka, btn3;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},1);


        list = findViewById(R.id.listItem);
        final Button button = (Button) findViewById(R.id.button);
        final ToggleButton toggle = (ToggleButton) findViewById(R.id.wifi_switcher);
        tochka = findViewById(R.id.button2);
        btn3 = findViewById(R.id.button3);


        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);


        if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED ) {
            toggle.setChecked(true);
        }

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), infoAboutConnection(), Toast.LENGTH_LONG).show();


                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivityForResult(intent, REQUEST_STATE);


            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(), nets[position].getSecurity() , Toast.LENGTH_SHORT).show();

                if (nets[position].getSecurity().equals("[ESS]")) {
                    connectToWifi(nets[position].getTitle(), false);
                }
                else {
                    connectToWifi(nets[position].getTitle(), true);
                }
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //toggleWiFi(true);
                if (wifiManager.getWifiState() == WIFI_STATE_DISABLED)
                {
                    Toast.makeText(getApplicationContext(), "Включите Wi-Fi", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    detectWifi();

                    button.setEnabled(false);
                    button.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            button.setEnabled(true);
                        }
                    }, 6000);
                }
            }
        });


        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    toggleWiFi(true);
                    Toast.makeText(getApplicationContext(), "Wi-Fi Включен!", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    toggleWiFi(false);
                    Toast.makeText(getApplicationContext(), "Wi-Fi Выключен!", Toast.LENGTH_SHORT).show();
                }


            }
        });


        tochka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wifiManager.getWifiState() == WIFI_STATE_DISABLED)
                {
                    Toast.makeText(getApplicationContext(), "Включите Wi-Fi", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), infoAboutWifiSupported(), Toast.LENGTH_SHORT).show();
                }



                //////////////////      OPEN SETTINGS FOR CONFIGURATE AND ON/OFF HOTSPOT        //////////////////////////
                final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.TetherSettings");
                intent.setComponent(cn);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity( intent);
                /////////////////////////////////////////////////////////////////////////////////////////////////////////




            }
        });
    }


    public String infoAboutConnection() {
        return String.valueOf(wifiManager.getConnectionInfo());
    }

    public static String getMacAddr() {
        try {
            List <NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif: all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b: macBytes) {
                    //res1.append(Integer.toHexString(b & 0xFF) + ":");
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {}
        return "02:00:00:00:00:00";
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public String infoAboutWifiSupported ()
    {
        String info = "Mac-address:" + getMacAddr() +
                "\nSupport 5GHz - " + wifiManager.is5GHzBandSupported()
                +"\n Wi-Fi State - " + wifiManager.getWifiState()
                + "\nSupport Wi-Fi Direct - " + wifiManager.isP2pSupported()
                + "\nSupport Tdls - " + wifiManager.isTdlsSupported()
                + "\nSupport always scan Wi-Fi  - " + wifiManager.isScanAlwaysAvailable()
                + "\nDeviceToApRttSupported - " + wifiManager.isDeviceToApRttSupported();
        return info;
    }



    private void connectToWifi(final String wifiSSID, boolean pswd) {

        if(pswd == true) {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.connect);
            dialog.setTitle("Connect to Network");
            TextView textSSID = (TextView) dialog.findViewById(R.id.textSSID1);
            Button dialogButton = (Button) dialog.findViewById(R.id.okButton);
            final EditText pass1 = (EditText) dialog.findViewById(R.id.textPassword);
            textSSID.setText(wifiSSID);

            // if button is clicked, connect to the network;
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String checkPassword = pass1.getText().toString();
                    finallyConnect(checkPassword, wifiSSID);
                    dialog.cancel();
                }
            });
            dialog.show();
        }
        else
        {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.connectwithoutpswd);
            dialog.setTitle("Connect to Network");
            TextView textSSID = (TextView) dialog.findViewById(R.id.textSSID1);
            Button dialogButton = (Button) dialog.findViewById(R.id.okButton);

            textSSID.setText(wifiSSID);

            // if button is clicked, connect to the network;
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finallyConnect("", wifiSSID);
                    dialog.cancel();
                }
            });
            dialog.show();
        }


    }






    private void finallyConnect(String pwd, String ssid) {
        String mSSID = ssid;
        String mPWD = pwd;
        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        config = new WifiConfiguration();
        config.SSID = "\"" + mSSID + "\"";
        if(pwd.equals("")){
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        else{

            config.preSharedKey = "\"" + mPWD + "\"";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

            ////

            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.status = WifiConfiguration.Status.ENABLED;
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            ////
        }

        config.status = WifiConfiguration.Status.ENABLED;
        config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);

        if(!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }
        //wifiManager.startScan();
        int networkId = wifiManager.addNetwork(config);
        wifiManager.disconnect();
        wifiManager.enableNetwork(networkId, true);
        wifiManager.reconnect();

    }




    //Cам метод включения Wi-Fi:
    public void toggleWiFi(boolean status) {
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        //Включаем WiFi помощью команды wifiManager.setWifiEnabled(true):
        if (status == true && !wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        //Выключаем WiFi с помощью команды wifiManager.setWifiEnabled(false):
        else if (status == false && wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
    }

    private void scanSuccess()
    {
        wifiList = wifiManager.getScanResults();
        this.nets = new Element[wifiList.size()];
        //Toast.makeText(getApplicationContext(), "size" + wifiList.size(), Toast.LENGTH_SHORT).show();

        for (int i = 0; i<wifiList.size(); i++){
            String item = wifiList.get(i).toString();

            String[] vector_item = item.split(",");

            String item_essid = vector_item[0];
            String item_bssid = vector_item[1];
            String item_capabilities = vector_item[2];
            String item_level = vector_item[3];
            String item_freq = vector_item[4];


            String ssid = item_essid.split(": ")[1];
            String bssid = item_bssid.split(": ")[1];
            String security = item_capabilities.split(": ")[1];
            String level = item_level.split(": ")[1];
            String freq = item_freq.split(": ")[1];


            nets[i] = new Element(ssid, security, level, bssid, freq);
        }
        // Toast.makeText(getApplicationContext(), "INFO " + wifiList.get(0), Toast.LENGTH_SHORT).show();
        //for debug!

        AdapterElements adapterElements = new AdapterElements(this);
        ListView netList = (ListView) findViewById(R.id.listItem);
        netList.setAdapter(adapterElements);

    }

    private void scanFailure(){
        Toast.makeText(getApplicationContext(), "A lot of scanning, wait... ", Toast.LENGTH_SHORT).show();
    }


    public void detectWifi() {

       // wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);


        /////////////
        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);

                if (success) {
                    scanSuccess();
                } else {
                    scanFailure();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getApplicationContext().registerReceiver(wifiScanReceiver, intentFilter);


        boolean success = wifiManager.startScan();
        try {
            Thread.sleep(1700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!success) {
            scanSuccess();
        }
        else {
            scanSuccess();
        }



    }


/*
    public class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context c, Intent intent) {
            //сканируем вайфай точки и узнаем какие доступны
            List<ScanResult> results = wifiManager.getScanResults();
            //проходимся по всем возможным точкам
            for (final ScanResult ap : results) {
                //ищем нужную нам точку с помощью ифа, будет находить то которую вы ввели
                if(ap.SSID.toString().trim().equals(url.getText().toString().trim())) {
                    // дальше получаем ее MAC и передаем для коннекрта, MAC получаем из результата
                    //здесь мы уже начинаем коннектиться
                    wifiConfig.BSSID = ap.BSSID;
                    wifiConfig.priority = 1;
                    wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                    wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                    wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                    wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                    wifiConfig.status = WifiConfiguration.Status.ENABLED;
                    //получаем ID сети и пытаемся к ней подключиться,
                    int netId = wifiManager.addNetwork(wifiConfig);
                    wifiManager.saveConfiguration();
                    //если вайфай выключен то включаем его
                    wifiManager.enableNetwork(netId, true);
                    //если же он включен но подключен к другой сети то перегружаем вайфай.
                    wifiManager.reconnect();
                    break;
                }
            }
        }
    }
 */



/*
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

 */

    //внутренний класс для заполнения ListView
    class AdapterElements extends ArrayAdapter<Object> {
        Activity context;

        public AdapterElements(Activity context) {
            super(context, R.layout.items2, nets);
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = context.getLayoutInflater();
            View item = inflater.inflate(R.layout.items2, null);

            TextView tvSsid = (TextView) item.findViewById(R.id.tvSSID);
            tvSsid.setText(nets[position].getTitle());

            TextView tvSecurity = (TextView)item.findViewById(R.id.tvSecurity);
            tvSecurity.setText(nets[position].getSecurity());

            TextView tvBSSID = (TextView)item.findViewById(R.id.tvBSSID);
            tvBSSID.setText(nets[position].getBSSID());

            TextView tvLevel = (TextView)item.findViewById(R.id.tvLevel);
            tvLevel.setText(nets[position].getLevel());

            TextView tvChannel = (TextView)item.findViewById(R.id.tvChanell);
            tvChannel.setText(nets[position].getChannel());

            TextView tvDistance = (TextView)item.findViewById(R.id.tvDistance);
            tvDistance.setText(nets[position].getDistance());

            TextView tvFreq = (TextView)item.findViewById(R.id.tvFreq);
            tvFreq.setText(nets[position].getFreq());


            ImageView tvImage= (ImageView)findViewById(R.id.tvImage);
            //tvImage.setImageResource(R.drawable.cake);


            //if (Integer.parseInt(nets[position].getLevel()) < 55) img.setImageResource(R.drawable.ic_signal_wifi_4_bar);
            //else if (Integer.parseInt(nets[position].getLevel()) > 90) img.setImageResource(R.drawable.ic_signal_wifi_0_bar);
            //else if (Integer.parseInt(nets[position].getLevel()) < 65) img.setImageResource(R.drawable.ic_signal_wifi_3_bar);
            //else if (Integer.parseInt(nets[position].getLevel()) < 75) img.setImageResource(R.drawable.ic_signal_wifi_2_bar);
            //else if (Integer.parseInt(nets[position].getLevel()) < 85) img.setImageResource(R.drawable.ic_signal_wifi_1_bar);

            return item;
        }
    }
}