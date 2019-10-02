package com.example.wifiappone;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.Toolbar;

import java.util.List;
//5789
public class MainActivity extends AppCompatActivity {

    private Element [] nets;
    private WifiManager wifiManager;
    private List<ScanResult> wifiList;

    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //////////////////////
        list = findViewById(R.id.listItem);
        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                detectWifi();
                //Toast.makeText(                // метод создания всплывающего окна
                //        MainActivity.this, "Scanning...", Toast.LENGTH_SHORT
                //).show();                   // метод, который покажет всплвающее окно
            }
        });
        //////////////////////

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







    public void detectWifi(){
        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        ScanResult scanResult = null;
        boolean f = wifiManager.startScan();
        wifiList = wifiManager.getScanResults();

        for (final ScanResult ap : wifiList) //по приколу
            Toast.makeText(MainActivity.this, ap.SSID, Toast.LENGTH_LONG).show();

        Log.d("TAG", wifiList.toString());

        //Toast.makeText(getApplicationContext(), "Scanning", Toast.LENGTH_SHORT).show();

        //String bssid;
        //if (!f)
         //   Toast.makeText(getApplicationContext(), "!f", Toast.LENGTH_SHORT).show();

        this.nets = new Element[wifiList.size()];

        //wifiList size = 0 ???
        Toast.makeText(getApplicationContext(), "size" + wifiList.size(), Toast.LENGTH_SHORT).show();

        for (int i = 0; i<wifiList.size(); i++){
            String item = wifiList.get(i).toString();
            String[] vector_item = item.split(",");
            String item_essid = vector_item[0];
            String item_capabilities = vector_item[2];
            String item_level = vector_item[3];
            String ssid = item_essid.split(": ")[1];
            String security = item_capabilities.split(": ")[1];
            String level = item_level.split(":")[1];
            nets[i] = new Element(ssid, security, level);
        }


        AdapterElements adapterElements = new AdapterElements(this);
        ListView netList = (ListView) findViewById(R.id.listItem);
        netList.setAdapter(adapterElements);
    }


    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = findViewById(R.id.listItem);
        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                detectWifi();
                Toast.makeText(                // метод создания всплывающего окна
                        MainActivity.this, "Scanning...", Toast.LENGTH_SHORT
                ).show();                   // метод, который покажет всплвающее окно
            }
        });

    }
*/

    //внутренний класс
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

            TextView tvLevel = (TextView)item.findViewById(R.id.tvLevel);
            String level = nets[position].getLevel();

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
            return item;
        }
    }
}
