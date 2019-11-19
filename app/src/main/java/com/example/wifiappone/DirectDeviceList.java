package com.example.wifiappone;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import java.util.Collection;

public class DirectDeviceList implements WifiP2pManager.PeerListListener {

    private DirectMainActivity dActivity;

    public DirectDeviceList(DirectMainActivity a){
        dActivity = a;
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        if(peers!= null && !peers.getDeviceList().isEmpty()) {
            Collection<WifiP2pDevice> list = peers.getDeviceList();
            dActivity.DirectDeviceList.clear();
            dActivity.DirectDeviceList.addAll(list);

            dActivity.DeviceListString.clear();
            for (WifiP2pDevice elem : peers.getDeviceList()) {
                dActivity.DeviceListString.add(elem.deviceName);
            }

            dActivity.listView.setAdapter(dActivity.adapter);
            dActivity.adapter.notifyDataSetChanged();
        }else{
            Toast.makeText(dActivity, "NO DEVICE FOUND", Toast.LENGTH_SHORT).show();
        }
    }
}
