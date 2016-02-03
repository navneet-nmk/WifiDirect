package com.teenvan.wifidirect;

import android.bluetooth.BluetoothClass;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import com.teenvan.wifidirect.model.DeviceClass;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by navneet on 03/02/16.
 */
public class WifiBroadcastReceiver extends BroadcastReceiver {

    //Declaration of member variables
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private MainActivity activity;
    private WifiP2pManager.PeerListListener mPeerListListener;
    private ArrayList<WifiP2pDevice> mDeviceList;

    // EventBus Declaration
    private EventBus bus = EventBus.getDefault();

    public WifiBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                 MainActivity activity, WifiP2pManager.PeerListListener peerListListener) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
        mPeerListListener = peerListListener;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {

                Log.d("Wifi P2P","Enabled");
            } else {
                Log.d("Wifi P2P", "Disabled");
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // The peer list has changed!  We should probably do something about
            // that.
            mDeviceList = new ArrayList<>();
            manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
                @Override
                public void onPeersAvailable(WifiP2pDeviceList peerList) {
                    List peers = new ArrayList();
                    peers.addAll(peerList.getDeviceList());
                    mDeviceList.addAll(peerList.getDeviceList());
                    ArrayList<DeviceClass> devices = new ArrayList<DeviceClass>();

                    for(WifiP2pDevice device : mDeviceList){
                        DeviceClass dev = new DeviceClass();
                        dev.setDeviceName(device.deviceName);
                        dev.setDeviceId(device.deviceAddress);
                        devices.add(dev);
                    }

                    // Post the devices array to post
                    if(!devices.isEmpty())
                    bus.post(devices);

                    Log.d("Peers Size", peers.size()+"");
//                    Log.d("Peer 1 name", peers.get(0).toString()+ "");

                }
            });


        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            // Connection state changed!  We should probably do something about
            // that.

            if (manager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {

                // We are connected with the other device, request connection
                // info to find group owner IP

                manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                        InetAddress groupOwnerAddress = info.groupOwnerAddress;

                        // After the group negotiation, we can determine the group owner.
                        if (info.groupFormed && info.isGroupOwner) {
                            // Do whatever tasks are specific to the group owner.
                            // One common case is creating a server thread and accepting
                            // incoming connections.
                            Toast.makeText(context,"Connected as Group Owner",Toast.LENGTH_SHORT).show();
                        } else if (info.groupFormed) {
                            Toast.makeText(context,"Connected as client",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

        }
    }
}
