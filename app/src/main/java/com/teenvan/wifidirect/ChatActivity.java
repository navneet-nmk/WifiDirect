package com.teenvan.wifidirect;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatActivity extends AppCompatActivity implements WifiP2pManager.ConnectionInfoListener {

    // Declaration of member variables
    private TextView mChatText;
    private Button mSendButton;
    private EditText mMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Referencing the UI elements
        mChatText = (TextView)findViewById(R.id.chatText);
        mSendButton = (Button)findViewById(R.id.sendMessage);
        mMessage = (EditText)findViewById(R.id.messageEditText);


        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mMessage.getText().toString();
                
            }
        });
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        InetAddress groupOwnerAddress = info.groupOwnerAddress;

        // After the group negotiation, we can determine the group owner.
        if (info.groupFormed && info.isGroupOwner) {
            // Do whatever tasks are specific to the group owner.
            // One common case is creating a server thread and accepting
            // incoming connections.

            // Owner Side - Create a server
            new FileServerAsyncTask(ChatActivity.this, )
                    .execute();

        } else if (info.groupFormed) {

            // Client Side




        }
    }


    public static class FileServerAsyncTask extends AsyncTask<Void, Void, String> {

        private Context context;
        private TextView statusText;

        /**
         * @param context
         * @param statusText
         */
        public FileServerAsyncTask(Context context, View statusText) {
            this.context = context;
            this.statusText = (TextView) statusText;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                ServerSocket serverSocket = new ServerSocket(8988);
                Log.d("WifiDirect", "Server: Socket opened");
                Socket client = serverSocket.accept();
                Log.d("WifiDirect", "Server: connection done");
                InputStream inputstream = client.getInputStream();
                serverSocket.close();
                return inputstream.toString();
            } catch (IOException e) {
                Log.e("WifiDirect", e.getMessage());
                return null;
            }
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                statusText.setText("File copied - " + result);

            }

        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            statusText.setText("Opening a server socket");
        }

    }
}
