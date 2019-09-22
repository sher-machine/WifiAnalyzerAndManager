package com.example.wifiappone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
}
