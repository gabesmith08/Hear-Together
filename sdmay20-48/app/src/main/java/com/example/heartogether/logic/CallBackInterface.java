package com.example.heartogether.logic;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;

public interface CallBackInterface extends ConnectionInfoListener {
    void setIsWifiP2pEnabled(boolean enabled);

    void thisDeviceChanged(WifiP2pDevice device);
}