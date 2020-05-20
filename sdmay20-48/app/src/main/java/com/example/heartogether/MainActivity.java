package com.example.heartogether;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.heartogether.fragments.HomeFragment;
import com.example.heartogether.fragments.ProfilesFragment;
import com.example.heartogether.fragments.SessionFragment;
import com.example.heartogether.fragments.SettingsFragment;
import com.example.heartogether.logic.SendFileClient;
import com.example.heartogether.logic.SendFileServer;
import com.example.heartogether.logic.WifiBroadcastReceiver;
import com.example.heartogether.models.Message;
import com.example.heartogether.threading.ClientInitThread;
import com.example.heartogether.threading.ServerInitThread;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private WifiBroadcastReceiver receiver;
    private IntentFilter intentFilter;
    private boolean isWifiP2PEnabled = false;
    private boolean retryChannel = false;
    int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION;

    private WifiP2pDevice selectedDevice;

    public static ServerInitThread server;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    FragmentManager fm = getSupportFragmentManager();
                    fm.beginTransaction()
                            .replace(R.id.fragmentLayout, new HomeFragment())
                            .addToBackStack(null)
                            .commit();
                    return true;
                case R.id.navigation_dashboard:
                    FragmentManager fm2 = getSupportFragmentManager();
                    fm2.beginTransaction()
                            .replace(R.id.fragmentLayout, new ProfilesFragment())
                            .addToBackStack(null)
                            .commit();
                    return true;
                case R.id.navigation_notifications:
                    FragmentManager fm3 = getSupportFragmentManager();
                    fm3.beginTransaction()
                            .replace(R.id.fragmentLayout, new SettingsFragment())
                            .addToBackStack(null)
                            .commit();
                    return true;
            }

            // Default to home
            FragmentManager fm = getSupportFragmentManager();
            if (fm.findFragmentById(R.id.fragmentLayout) == null) {
                fm.beginTransaction()
                        .add(R.id.fragmentLayout, new HomeFragment())
                        .commit();
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
        }
        //startActivity(new Intent(getApplicationContext(), FFTActivityTest.class));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        FragmentManager fm = getSupportFragmentManager();

        // Default fragment is home on app start
        fm.beginTransaction()
                .add(R.id.fragmentLayout, new HomeFragment())
                .commit();

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        receiver = new WifiBroadcastReceiver(manager, channel, this);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public void WiFiPeerDiscovery() {
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "discover success.",
                        Toast.LENGTH_SHORT);
            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(getApplicationContext(), "discover fail: " + reasonCode,
                        Toast.LENGTH_SHORT);
            }
        });
    }

    public BroadcastReceiver GetApplicationReceiver() {
        return this.receiver;
    }

    public void SetDeviceToJoin(WifiP2pDevice device) {
        this.selectedDevice = device;
    }

    public void connect(final WifiP2pConfig config, final InetAddress address) {
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
                // connect to this.selectedDevice, should/will be the host session
                Toast.makeText(MainActivity.this, "Connect succeeded. Hooray.",
                        Toast.LENGTH_SHORT).show();
                Message message = new Message(1, "Some String", null, "Some Name");
                if(receiver.isGroupOwner() == WifiBroadcastReceiver.IS_OWNER){
                    Log.e(TAG, "Message hydrated, start SendMessageServer AsyncTask");

                    new SendFileServer(MainActivity.this, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, message);
                }
                else if(receiver.isGroupOwner() == WifiBroadcastReceiver.IS_CLIENT){
                    Log.e(TAG, "Message hydrated, start SendMessageClient AsyncTask");

                    new SendFileClient(MainActivity.this, receiver.getOwnerAddr()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, message);
                }
            }
            @Override
            public void onFailure(int reason) {
                Toast.makeText(MainActivity.this, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void WiFiSessionConnect(){
        //Start the init process
        if(receiver.isGroupOwner() == WifiBroadcastReceiver.IS_OWNER){
            Toast.makeText(MainActivity.this, "I'm the group owner  " + receiver.getOwnerAddr().getHostAddress(), Toast.LENGTH_SHORT).show();
            server = new ServerInitThread();
            server.start();
        }
        else if(receiver.isGroupOwner() != WifiBroadcastReceiver.IS_CLIENT){
            Toast.makeText(MainActivity.this, "I'm the client", Toast.LENGTH_SHORT).show();
            ClientInitThread client = new ClientInitThread(receiver.getOwnerAddr());
            client.start();
        }

        //Open the Session Fragment
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragmentLayout, new SessionFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        }
    }


    public void disconnect() {
        manager.removeGroup(channel, new ActionListener() {
            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
            }
            @Override
            public void onSuccess() {

            }
        });
    }



    private void startRegistration(){
        //  Create a string map containing information about your service.
        Map record = new HashMap();
        record.put("listenport", String.valueOf(4));
        record.put("buddyname", "John Doe" + (int) (Math.random() * 1000));
        record.put("available", "visible");

        // Service information.  Pass it an instance name, service type
        // _protocol._transportlayer , and the map containing
        // information other devices will want once they connect to this one.
        WifiP2pDnsSdServiceInfo serviceInfo =
                WifiP2pDnsSdServiceInfo.newInstance("_test", "_presence._tcp", record);

        // Add the local service, sending the service info, network channel,
        // and listener that will be used to indicate success or failure of
        // the request.
        manager.addLocalService(channel, serviceInfo, new ActionListener() {
            @Override
            public void onSuccess() {
                // Command successful! Code isn't necessarily needed here,
                // Unless you want to update the UI or add logging statements.
            }

            @Override
            public void onFailure(int arg0) {
                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
            }
        });

    }
}
