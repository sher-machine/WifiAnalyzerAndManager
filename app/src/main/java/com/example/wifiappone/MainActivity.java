package com.example.wifiappone;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
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
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import static android.net.wifi.WifiManager.WIFI_STATE_DISABLED;

public class MainActivity extends AppCompatActivity {

    private static final String TAG ="MAINACTIVITY" ;
    private Element [] nets;
    private WifiManager wifiManager;
    private List<ScanResult> wifiList;
    private WifiConfiguration config;
    private ListView list;


    private static final int REQUEST_STATE = 1;
    private final int REQUEST_ENABLE_LOCATION_SYSTEM_SETTINGS = 101;
    private boolean isHotspot26apiPlusEnabled = false;
    private WifiManager.LocalOnlyHotspotReservation mReservation;
    WifiConfiguration currentConfig;

    Button tochka, btn3,btn4,btn5,button;
    ToggleButton toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},1);
        }


        list = findViewById(R.id.listItem);
        button = findViewById(R.id.button);
        toggle  = findViewById(R.id.wifi_switcher);
        tochka = findViewById(R.id.button2);
        btn3 = findViewById(R.id.button3);
        btn4 = findViewById(R.id.button4);
        btn5 = findViewById(R.id.button5);


        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);


        if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED ) {
            toggle.setChecked(true);
        }

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivityForResult(intent, REQUEST_STATE);
            }
        });


        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), infoAboutConnection(), Toast.LENGTH_LONG).show();


                if (wifiManager.getWifiState() == WIFI_STATE_DISABLED)
                {
                    Toast.makeText(getApplicationContext(), "Включите Wi-Fi", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (!isWifiApEnabled()) {
                        Intent intent = new Intent(getApplicationContext(), DirectMainActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Для корректной работы отключите точку доступа", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });


        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wifiManager.getWifiState() == WIFI_STATE_DISABLED)
                {
                    Toast.makeText(getApplicationContext(), "Включите Wi-Fi", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), infoAboutWifiSupported(), Toast.LENGTH_LONG).show();
                }

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
                    }, 5000);
                }
            }
        });


        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (!isWifiApEnabled()) {
                    if (isChecked) {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        toggleWiFi(true);
                        Toast.makeText(getApplicationContext(), "Wi-Fi Включен!", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        toggleWiFi(false);
                        Toast.makeText(getApplicationContext(), "Wi-Fi Выключен!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Для корректной работы отключите точку доступа", Toast.LENGTH_LONG).show();
                }

            }
        });


        tochka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               hotSpot(true);
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


    public String infoAboutWifiSupported () {
        String info = "Mac-address:" + getMacAddr()
                +"\n Wi-Fi State - " + wifiManager.getWifiState();

               if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                   info = info + "\nSupport 5GHz - " + wifiManager.is5GHzBandSupported()
                           + "\nSupport Wi-Fi Direct - " + wifiManager.isP2pSupported()
                           + "\nSupport Tdls - " + wifiManager.isTdlsSupported()
                           + "\nSupport always scan Wi-Fi  - " + wifiManager.isScanAlwaysAvailable()
                           + "\nDeviceToApRttSupported - " + wifiManager.isDeviceToApRttSupported();
               }
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





    public WIFI_AP_STATE getWifiApState() {
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApState");

            int tmp = ((Integer) method.invoke(wifiManager));

            // Fix for Android 4
            if (tmp > 10) {
                tmp = tmp - 10;
            }

            return WIFI_AP_STATE.class.getEnumConstants()[tmp];
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
            return WIFI_AP_STATE.WIFI_AP_STATE_FAILED;
        }
    }

    public boolean isWifiApEnabled() {
        return getWifiApState() == WIFI_AP_STATE.WIFI_AP_STATE_ENABLED;
    }




    public void toggleWiFi(boolean status) {
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        if (!isWifiApEnabled()) {
            //Включаем WiFi помощью команды wifiManager.setWifiEnabled(true):
            if (status == true && !wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            //Выключаем WiFi с помощью команды wifiManager.setWifiEnabled(false):
            else if (status == false && wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);

            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Для корректной работы отключите точку доступа", Toast.LENGTH_LONG).show();
        }
    }

    private void scanSuccess()
    {
        wifiList = wifiManager.getScanResults();
        this.nets = new Element[wifiList.size()];
        //Toast.makeText(getApplicationContext(), "size" + wifiList.size(), Toast.LENGTH_SHORT).show();

        /*
        for (int i = 0; i<wifiList.size(); i++) {
            String item = wifiList.get(i).toString();
            Log.d(TAG,item);
        }

         */

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

        /////////////
        WifiReceiver wifiScanReceiver = new WifiReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean success = false;
                success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);


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
            //scanSuccess();
        }
        else {
            scanSuccess();
        }



    }


    public void hotSpot(boolean enable) {
        //после 28api (android 8 )
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {

            enableLocationSettings();

            //////////////////      OPEN SETTINGS FOR CONFIGURATE AND ON/OFF HOTSPOT        //////////////////////////
            /*
            final Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.TetherSettings");
            intent.setComponent(cn);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
             */
            /////////////////////////////////////////////////////////////////////////////////////////////////////////
        }
        else{
            startHotSpot16_api(enable);
        }
    }

    public void startHotSpot16_api(boolean enable) {


        String SSID="Hello";
        String PASS="123321123321";
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if(enable){
            wifiManager.setWifiEnabled(!enable);    // Disable all existing WiFi Network
        }else {
            if (!wifiManager.isWifiEnabled())
                wifiManager.setWifiEnabled(!enable);
        }
        Method[] methods = wifiManager.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals("setWifiApEnabled")) {
                WifiConfiguration netConfig = new WifiConfiguration();
                if(!SSID.isEmpty() || !PASS.isEmpty()){
                    netConfig.SSID=SSID;
                    netConfig.preSharedKey = PASS;
                    netConfig.hiddenSSID = false;
                    netConfig.status = WifiConfiguration.Status.ENABLED;              netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);              netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);           netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                    netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);              netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);     netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                }
                try {
                    method.invoke(wifiManager, netConfig, enable);
                    Log.e(TAG,"set hotspot enable method");
                } catch (Exception ex) {}
                break;}}}





    /////////////////////////////////////////////////////////////////////////////////////   LOCAL
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void turnOn26apiPlusHotspot(){
        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        manager.startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback(){

            @Override
            public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                super.onStarted(reservation);
                Log.d(TAG, "Wifi Hotspot is on now");
                mReservation = reservation;
                isHotspot26apiPlusEnabled = true;
                currentConfig = mReservation.getWifiConfiguration();

                Log.v("DANG", "THE PASSWORD IS: "
                        + currentConfig.preSharedKey
                        + " \n SSID is : "
                        + currentConfig.SSID);

                Toast.makeText(getApplicationContext(),  "THE PASSWORD IS: "
                        + currentConfig.preSharedKey
                        + " \n SSID is : "
                        + currentConfig.SSID, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onStopped() {
                super.onStopped();
                Log.d(TAG, "onStopped: ");
                isHotspot26apiPlusEnabled = false;
            }

            @Override
            public void onFailed(int reason) {
                super.onFailed(reason);
                Log.d(TAG, "onFailed: ");
                isHotspot26apiPlusEnabled = false;
            }
        },new Handler());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void turnOff26apiPlusHotspot() {
        if (mReservation != null) {
            mReservation.close();
            isHotspot26apiPlusEnabled = false;
        }
    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    private void findMethods() {

        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String str = "";
        Class<?> cls = wm.getClass();
        Method[] methods = cls.getMethods();
        for (Method method : methods) {
            str = str + " METHOD:" + method.getName()+"\n";
        }

        //hw.setText(str+ "q");
        Log.d(TAG,str);


        String str2="DECLARED ";
        Class<?> cls2 = wm.getClass();
        Method[] methods2 = cls2.getDeclaredMethods();
        for (Method method2 : methods2) {
            str2 = str2 + " METHOD:" + method2.getName()+"\n";
        }

        Log.d(TAG,str2);

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void toggleHotspot26apiPlus() {
        if (!isHotspot26apiPlusEnabled) {
            turnOn26apiPlusHotspot();
        } else {
            turnOff26apiPlusHotspot();
        }
    }



    ////////////////////


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void enableLocationSettings() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10);
        mLocationRequest.setSmallestDisplacement(10);
        mLocationRequest.setFastestInterval(10);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest)
                .setAlwaysShow(false); // Show dialog

        Task<LocationSettingsResponse> task= LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        task.addOnCompleteListener(task1 -> {
            try {
                LocationSettingsResponse response = task1.getResult(ApiException.class);
                // All location settings are satisfied. The client can initialize location
                // requests here.
                toggleHotspot26apiPlus();

            } catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(MainActivity.this, REQUEST_ENABLE_LOCATION_SYSTEM_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        } catch (ClassCastException e) {
                            // Ignore, should be an impossible error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_ENABLE_LOCATION_SYSTEM_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        toggleHotspot26apiPlus();
                        Toast.makeText(MainActivity.this, states.isLocationPresent() + "", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(MainActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    //////////////////////////////////////////////////////////////////      LOCAL




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
            tvLevel.setText(nets[position].getLevel());

            TextView tvChannel = (TextView)item.findViewById(R.id.tvChanell);
            tvChannel.setText(nets[position].getChannel());

            TextView tvDistance = (TextView)item.findViewById(R.id.tvDistance);
            tvDistance.setText(nets[position].getDistance());

            TextView tvFreq = (TextView)item.findViewById(R.id.tvFreq);
            tvFreq.setText(nets[position].getFreq());


            ImageView tvImage= (ImageView)findViewById(R.id.tvImage);
            //tvImage.setImageResource(R.drawable.ic_signal_wifi_2_bar);


            //if (Integer.parseInt(nets[position].getLevel()) < 55) img.setImageResource(R.drawable.ic_signal_wifi_4_bar);
            //else if (Integer.parseInt(nets[position].getLevel()) > 90) img.setImageResource(R.drawable.ic_signal_wifi_0_bar);
            //else if (Integer.parseInt(nets[position].getLevel()) < 65) img.setImageResource(R.drawable.ic_signal_wifi_3_bar);
            //else if (Integer.parseInt(nets[position].getLevel()) < 75) img.setImageResource(R.drawable.ic_signal_wifi_2_bar);
            //else if (Integer.parseInt(nets[position].getLevel()) < 85) img.setImageResource(R.drawable.ic_signal_wifi_1_bar);

            return item;
        }
    }
}