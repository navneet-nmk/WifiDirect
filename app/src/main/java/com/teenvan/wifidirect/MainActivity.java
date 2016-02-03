package com.teenvan.wifidirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.teenvan.wifidirect.model.DeviceClass;

import java.net.InetAddress;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity implements WifiP2pManager.ConnectionInfoListener {

    // Declaration of member variables
    private final IntentFilter intentFilter = new IntentFilter();
    WifiP2pManager.Channel mChannel;
    private WifiP2pManager manager;
    private Button mDiscover;
    private BroadcastReceiver receiver = null;
    private WifiP2pManager.PeerListListener mPeerListListener;
    private RecyclerView mDevicesList;
    private ArrayList<DeviceClass> devs = new ArrayList<>();

    // EventBus Declaration
    private EventBus bus = EventBus.getDefault();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = manager.initialize(this,getMainLooper(),null);


        // Referencing the UI elements
        mDiscover = (Button)findViewById(R.id.discoverButton);
        mDevicesList = (RecyclerView)findViewById(R.id.devicesList);
        mDevicesList.setLayoutManager(new LinearLayoutManager(this));
        mDevicesList.setHasFixedSize(true);

        mDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        // Success
                        Log.d("Wifi", "Success");
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.d("Wifi", "Failure "+ reason);
                    }
                });
            }
        });

        mDevicesList.addOnItemTouchListener(new RecyclerItemClickListener(MainActivity.this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if(!devs.isEmpty()){
                            // Connect to device
                            WifiP2pConfig config = new WifiP2pConfig();
                            config.deviceAddress = devs.get(position).getDeviceId();
                            config.wps.setup = WpsInfo.PBC;
                            manager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(MainActivity.this,"Connected",Toast.LENGTH_SHORT)
                                            .show();
                                }

                                @Override
                                public void onFailure(int reason) {
                                    Toast.makeText(MainActivity.this, "Connect failed. Retry."+reason,
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }));
    }


    @Override
    protected void onStart() {
        super.onStart();
        bus.register(this);
        manager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // Success
                Log.d("Wifi", "Success");
            }

            @Override
            public void onFailure(int reason) {
                Log.d("Wifi", "Failure "+ reason);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        receiver = new WifiBroadcastReceiver(manager, mChannel, this, mPeerListListener);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        bus.unregister(this);
    }


    public void onEvent(ArrayList<DeviceClass> event){
        String deviceName = event.get(0).getDeviceName();
        devs = event;
        Log.d("Device Name", deviceName);
        DevicesAdapter adapter = new DevicesAdapter(event,MainActivity.this);
        mDevicesList.setAdapter(adapter);
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {

    }
}
