package com.example.wifiappone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


//https://github.com/MarcoNordio/WifiDirectConnection

public class DirectMainActivity extends AppCompatActivity implements WifiP2pManager.ConnectionInfoListener {

    private static final String TAG = "TEST ";
    android.app.ProgressDialog ProgressDialog = null;
    Button button;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    DirectWiFiBroadcastReceiver mReceiver;
    private final IntentFilter intentFilter = new IntentFilter();
    DirectDeviceList DeviceListModel;
    ArrayAdapter<String> adapter;
    ArrayList<WifiP2pDevice> DirectDeviceList = new ArrayList<>();
    ArrayList<String> DeviceListString = new ArrayList<>();
    ListView listView;
    TextView textViewList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.direct_activity_main);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new DirectWiFiBroadcastReceiver(mManager, mChannel, this);


        DeviceListModel= new DirectDeviceList(this);

        listView= findViewById(R.id.directlistView);
        textViewList = findViewById(R.id.directtextViewList);

        Log.d (TAG, String.valueOf(R.layout.directrow));
        Log.d (TAG, String.valueOf(R.id.directtextViewList));
        Log.d (TAG, String.valueOf(DeviceListString));

        adapter = new ArrayAdapter<>(getApplicationContext(),R.layout.directrow,R.id.directtextViewList,DeviceListString);
        listView.setAdapter(adapter);

        ///////

        ///////



        SetBtnSearch();
        SetDeviceListClick();
    }


    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, intentFilter);
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    private void SetDeviceListClick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = DirectDeviceList.get(position).deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                if (ProgressDialog != null && ProgressDialog.isShowing()) {
                    ProgressDialog.dismiss();
                }

                AlertDialog.Builder miaAlert = new AlertDialog.Builder(DirectMainActivity.this);
                miaAlert.setMessage("Connect?");
                miaAlert.setTitle("Server");

                miaAlert.setCancelable(false);
                miaAlert.setPositiveButton("Yes", (dialog, id) -> {
                    config.groupOwnerIntent=15;
                    Connect(config);
                });

                miaAlert.setNegativeButton("No", (dialog, id) -> {
                    config.groupOwnerIntent=0;
                    Connect(config);
                });

                AlertDialog alert = miaAlert.create();
                alert.show();
            }
        });
    }

    private void SetBtnSearch() {
        button = findViewById(R.id.button);
        DirectDeviceList.clear();
        DeviceListString.clear();
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(DirectMainActivity.this, "scan started", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(DirectMainActivity.this, "scan failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        if (info.groupFormed && info.isGroupOwner) {
            Toast.makeText(this, "SERVER", Toast.LENGTH_SHORT).show();
        } else if (info.groupFormed) {
            Toast.makeText(this, "CLIENT", Toast.LENGTH_SHORT).show();
        }
    }

    public void Connect(WifiP2pConfig config) {
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() { }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(getApplicationContext(), "Connect failed. Retry.",Toast.LENGTH_SHORT).show();
            }
        });
    }
}