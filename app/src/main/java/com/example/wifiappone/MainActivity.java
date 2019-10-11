package com.example.wifiappone;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.LauncherActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.List;
//5789
public class MainActivity extends AppCompatActivity {

    private Element [] nets;
    private WifiManager wifiManager;
    private List<ScanResult> wifiList;
    private WifiConfiguration config;
    private EditText url, pswd;
    private ListView list;

    /*
    public void init() {

       Button conect = (Button) findViewById(R.id.button2);
       url = (EditText) findViewById(R.id.editText1);

        // создаем новый объект для подключения к конкретной точке
        wifiConfig = new WifiConfiguration();
        // сканнер вайфая который нам будет помогать подключаться к нужной точке
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        //наш рессивер который будем подключать нас столько сколько нам понадобиться, пока не будет подключена нужная точка
        wifiResiver = new WifiReceiver();
    }

     */


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},1);

        list = findViewById(R.id.listItem);
        Button button = (Button) findViewById(R.id.button);
        Button button2 = (Button) findViewById(R.id.button2);
        url = (EditText) findViewById(R.id.editText1);
        pswd = (EditText) findViewById(R.id.editText2);


        ////// click on ListView

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "list ->" + position , Toast.LENGTH_SHORT).show();
                connectToWifi(nets[position].getTitle());
            }
        });


        //////

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleWiFi(true);
                detectWifi();
                }
            });


        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect(url.getText().toString().trim(),pswd.getText().toString().trim());
            }
        });


        ToggleButton toggle = (ToggleButton) findViewById(R.id.wifi_switcher);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    toggleWiFi(true);
                    Toast.makeText(getApplicationContext(), "Wi-Fi Включен!", Toast.LENGTH_SHORT).show();
                }
                else {
                    toggleWiFi(false);
                    Toast.makeText(getApplicationContext(), "Wi-Fi Выключен!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //////////////

    private void connectToWifi(final String wifiSSID) {
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
                dialog.dismiss();
            }
        });
        dialog.show();
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
        wifiManager.startScan();
        int networkId = wifiManager.addNetwork(config);
        wifiManager.disconnect();
        wifiManager.enableNetwork(networkId, true);
        wifiManager.reconnect();
    }

    ///////////////




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






    /*
    public enum WifiCipherType {
        WIFICIPHER_WEP,
        WIFICIPHER_WPA,
        WIFICIPHER_NOPASS,
        WIFICIPHER_INVALID
    }


    private WifiConfiguration exists(String SSID) {
        List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration config : configs) {
            if (config.SSID.equals(SSID)) {
                return config;
            }
        }
        return null;
    }

    private WifiConfiguration createWifiInfo(String SSID, String password, WifiCipherType type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        if (type == WifiCipherType.WIFICIPHER_NOPASS) {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (type == WifiCipherType.WIFICIPHER_WEP) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (type == WifiCipherType.WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
        } else {
            return null;
        }
        return config;
    }



    public boolean connectWifi(String SSID, String password, WifiCipherType type) {
        while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            SystemClock.sleep(100);
        }
        if (SSID == null || password == null || SSID.equals("")) {
            return false;
        }
        WifiConfiguration currentConfig = createWifiInfo(SSID, password, type);
        if (currentConfig == null) {
            return false;
        }
        WifiConfiguration tempConfig = exists(SSID);
        if (tempConfig == null) {
            wifiManager.removeNetwork(tempConfig.networkId);
        }
        int networkId = wifiManager.addNetwork(currentConfig);
        wifiManager.enableNetwork(networkId, true);
        return wifiManager.reconnect();
    }
*/

    private void scanSuccess()
    {
        wifiList = wifiManager.getScanResults();
        //next using results
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
        Toast.makeText(getApplicationContext(), "FAIL ", Toast.LENGTH_SHORT).show();
    }


    public void detectWifi() {
        //Thread.sleep(500);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //ScanResult scanResult = null;

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
        if (!success) {
            scanSuccess();
        }
        else {
            scanSuccess();
        }


        /////////////


       // boolean f = wifiManager.startScan();
        //wifiList = wifiManager.getScanResults();

        //Log.d("TAG", wifiList.toString());


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


    public void connect(String ssid, String pwd){

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
        wifiManager.startScan();
        int networkId = wifiManager.addNetwork(config);
        wifiManager.disconnect();
        wifiManager.enableNetwork(networkId, true);
        wifiManager.reconnect();
    }



    public double calculateDistance(double signalLevelInDb, double freqInMHz) {
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(signalLevelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }

    //внутренний класс для заполнения ListView
    class AdapterElements extends ArrayAdapter<Object> {
        Activity context;

        public AdapterElements(Activity context) {
            super(context, R.layout.items, nets);
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = context.getLayoutInflater();
            View item = inflater.inflate(R.layout.items, null);

            TextView tvSsid = (TextView) item.findViewById(R.id.tvSSID);
            tvSsid.setText(nets[position].getTitle());

            TextView tvSecurity = (TextView)item.findViewById(R.id.tvSecurity);
            tvSecurity.setText(nets[position].getSecurity());

            TextView tvBSSID = (TextView)item.findViewById(R.id.tvBSSID);
            tvBSSID.setText(nets[position].getBSSID());

            TextView tvLevel = (TextView)item.findViewById(R.id.tvLevel);
            Double lvl = Double.parseDouble(nets[position].getLevel());
            Double fr = Double.parseDouble(nets[position].getFreq());
            Double level = calculateDistance(lvl,fr);
            tvLevel.setText(level.toString());



            /*
            try{
                int i = Integer.parseInt(level);
                if (i>-50){
                    tvLevel.setText("Высокий");
                } else if (i<=-50 && i>-80){
                    tvLevel.setText("Средний");
                } else if (i<=-80){
                    tvLevel.setText("Низкий");
                }
            } catch (NumberFormatException e){
                Log.d("TAG", "Неверный формат строки");
            }

            */



            return item;
        }
    }
}
