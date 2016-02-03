package com.teenvan.wifidirect;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.Settings;
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
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

public class ChatActivity extends AppCompatActivity  {

    // Declaration of member variables
    private TextView mChatText;
    private Button mSendButton;
    private EditText mMessage;
    private static final int SOCKET_TIMEOUT = 5000;
    private String host;
    private int port;
    private WifiP2pDevice device;
    private WifiP2pInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        host = getIntent().getStringExtra("Host");
//        Log.d("Host Address", host);

        // Referencing the UI elements
        mChatText = (TextView)findViewById(R.id.chatText);
        mSendButton = (Button)findViewById(R.id.sendMessage);
        mMessage = (EditText)findViewById(R.id.messageEditText);

//         host = info.groupOwnerAddress.getHostAddress();


        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mMessage.getText().toString();
                Intent serviceIntent = new Intent(ChatActivity.this, FileTransferService.class);
                serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, text);
                serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                       host);
                serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
                startService(serviceIntent);
            }
        });
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
