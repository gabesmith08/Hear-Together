package com.example.heartogether.fragments;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.heartogether.MainActivity;
import com.example.heartogether.R;
import com.example.heartogether.logic.WifiBroadcastReceiver;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class SessionListFragment extends androidx.fragment.app.Fragment {
    private MainActivity activity;
    private Button refreshBtn;
    private final String TAG = "SessionListFragment";
    private WifiBroadcastReceiver receiver;
    private List<WifiP2pDevice> peers = new ArrayList<>();
    private ListView sessionListView;

    public SessionListFragment() {
    }

    @Override
    public void onCreate(Bundle savedFragmentState) {
        super.onCreate(savedFragmentState);
        Log.d(TAG, "onCreate called");
        this.activity = (MainActivity) this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView called");
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        // Button buttonBluetooth = (Button) v.findViewById(R.id.buttonSayHi);
        // buttonBluetooth.setOnClickListener(this);
        this.sessionListView = v.findViewById(R.id.sessionList);

        this.receiver = (WifiBroadcastReceiver) activity.GetApplicationReceiver();
        this.updateView();

        refreshBtn = v.findViewById(R.id.refresh);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.WiFiPeerDiscovery();
                try {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.replace(R.id.fragmentLayout, new SessionListFragment());
                    // transaction.addToBackStack(null);
                    transaction.commit();
                } catch (Exception e) {
                    e.fillInStackTrace();
                    Log.d(TAG, "error");
                }
            }
        });

        return v;
    }

    private void updateView() {
        this.peers = this.receiver.GetPeers();
        List<String> stringList = new ArrayList<>();

        for (WifiP2pDevice item : this.peers) {
            stringList.add(item.deviceName);
        }

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_1, stringList);
        this.sessionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                WifiP2pDevice item = peers.get(i);
                activity.SetDeviceToJoin(item);
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = item.deviceAddress;
                // activity.connect(config);
            }
        });
        this.sessionListView.setAdapter(arrayAdapter);
    }
}
