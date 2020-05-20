package com.example.heartogether.logic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.heartogether.MainActivity;

import java.util.ArrayList;
import java.util.List;

import java.net.InetAddress;

public class WifiBroadcastReceiver extends BroadcastReceiver {

    public static final int IS_OWNER = 1;
    public static final int IS_CLIENT = 2;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private MainActivity activity;
    private int isGroupOwner;
    private List<WifiP2pDevice> peers = new ArrayList<>();
    private InetAddress ownerAddr;

    private WifiP2pManager.PeerListListener myPeerListListener;
    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;

    public WifiBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity activity) {
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    public InetAddress getOwnerAddr() { return ownerAddr; }
    public int isGroupOwner() { return isGroupOwner; }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not, alert the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi P2P is enabled
                // this.activity.setIsWifiP2PEnabled(true);
            } else {
                // Wi-Fi P2P is not enabled
                // this.activity.setIsWifiP2PEnabled(false);
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // The peer list has changed! We should probably do something about that.
            if (manager != null) {
                manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
                        List<WifiP2pDevice> refreshedPeers = new ArrayList<WifiP2pDevice>(wifiP2pDeviceList.getDeviceList());
                        if (!refreshedPeers.equals(peers)) {
                            peers.clear();
                            peers.addAll(refreshedPeers);


                            // If an AdapterView is backed by this data, notify it
                            // of the change. For instance, if you have a ListView of
                            // available peers, trigger an update.


                            // Perform any other updates needed based on the new list of
                            // peers connected to the Wi-Fi P2P network.
                        }

                        if (peers.size() == 0) {
                            Toast.makeText(activity, "no peers" , Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            if(manager == null){
                return;
            }
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if(networkInfo.isConnected()) {
                manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {

                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                        ownerAddr = info.groupOwnerAddress;

                        /******************************************************************
                         The GO : create a server thread and accept incoming connections
                         ******************************************************************/
                        if (info.groupFormed && info.isGroupOwner) {
                            isGroupOwner = IS_OWNER;

                            //activateGoToChat("server");
                        }

                        /******************************************************************
                         The client : create a client thread that connects to the group owner
                         ******************************************************************/
                        else if (info.groupFormed) {
                            isGroupOwner = IS_CLIENT;
                            //activateGoToChat("client");
                        }

                        // activity.connect(null, ownerAddr);
                        activity.WiFiSessionConnect();
                    }
                });
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            /*DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager().findFragmentById(R.id.frag_list);
            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));*/
        }
    }

    public List<WifiP2pDevice> GetPeers() {
        return this.peers;
    }

    /*@Override
    public void connect() {
        // Picking the first device found on the network.
        WifiP2pDevice device = peers.get(0);

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver notifies us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(activity, "Connect failed. Retry.", Toast.LENGTH_SHORT).show();
            }
        });
    }*/


    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

            List<WifiP2pDevice> refreshedPeers = (List<WifiP2pDevice>) peerList.getDeviceList();
            if (!refreshedPeers.equals(peers)) {
                peers.clear();
                peers.addAll(refreshedPeers);
                Toast.makeText(activity, "peer count: " + peers.size() , Toast.LENGTH_SHORT).show();

                // If an AdapterView is backed by this data, notify it
                // of the change. For instance, if you have a ListView of
                // available peers, trigger an update.


                // Perform any other updates needed based on the new list of
                // peers connected to the Wi-Fi P2P network.
            }

            if (peers.size() == 0) {
                Toast.makeText(activity, "no peers" , Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };

    /*private WifiP2pConfig config = new WifiP2pConfig() {


        @Override
        public void connect() {
            // Picking the first device found on the network.
            WifiP2pDevice device = peers.get(0);

            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = device.deviceAddress;
            config.wps.setup = WpsInfo.PBC;

            manager.connect(channel, config, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    // WiFiDirectBroadcastReceiver notifies us. Ignore for now.
                }

                @Override
                public void onFailure(int reason) {
                    Toast.makeText(activity, "Connect failed. Retry.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

    };*/

}
