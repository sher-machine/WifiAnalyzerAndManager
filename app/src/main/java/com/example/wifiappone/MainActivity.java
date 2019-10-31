package com.example.wifiappone;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG ="MAINACTIVITY" ;
    private Element [] nets;
    private WifiManager wifiManager;
    private List<ScanResult> wifiList;
    private WifiConfiguration config;
    private ListView list;

    Button tochka, button;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},1);


        list = findViewById(R.id.listItem);
        button = findViewById(R.id.button);
        final ToggleButton toggle = findViewById(R.id.wifi_switcher);
        tochka = findViewById(R.id.button2);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(), "list ->" + position , Toast.LENGTH_SHORT).show();
                connectToWifi(nets[position].getTitle());
            }
        });



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                toggleWiFi(true);
                detectWifi();
                }
            });


        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    toggleWiFi(true);
                    Toast.makeText(getApplicationContext(), "Wi-Fi Включен!", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        Thread.sleep(1000);
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
                Toast.makeText(getApplicationContext(), "mac:" + getMacAddr(), Toast.LENGTH_SHORT).show();
            }
        });
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




    private void connectToWifi(final String wifiSSID) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.connect);
        dialog.setTitle("Connect to Network");
        TextView textSSID = dialog.findViewById(R.id.textSSID1);
        Button dialogButton =  dialog.findViewById(R.id.okButton);
        final EditText pass1 =  dialog.findViewById(R.id.textPassword);
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
        Toast.makeText(getApplicationContext(), "FAIL ", Toast.LENGTH_SHORT).show();
    }


    public void detectWifi() {

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
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
            Thread.sleep(1000);
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
            Double level = calculateDistance(Double.parseDouble(nets[position].getLevel()),Double.parseDouble(nets[position].getFreq()));
            tvLevel.setText(level.toString());
            return item;
        }
    }


}
