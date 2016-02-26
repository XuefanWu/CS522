/*********************************************************************

 Chat server: accept chat messages from clients.

 Sender name and GPS coordinates are encoded
 in the messages, and stripped off upon receipt.

 Copyright (c) 2012 Stevens Institute of Technology

 **********************************************************************/
package edu.stevens.cs522.chat.oneway.server.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.SQLException;

import edu.stevens.cs522.chat.oneway.server.R;
import edu.stevens.cs522.chat.oneway.server.contract.MessageContract;
import edu.stevens.cs522.chat.oneway.server.database.PeerDbAdapter;
import edu.stevens.cs522.chat.oneway.server.entities.Message;
import edu.stevens.cs522.chat.oneway.server.entities.Peer;

public class ChatServer extends Activity implements View.OnClickListener {

    final static public String TAG = ChatServer.class.getCanonicalName();

    /*
     * Socket used both for sending and receiving
     */
    public final static String EXTRA_MESSAGE
            = "edu.stevens.cs522.chat.oneway.server.activities.ChatServer";
    private DatagramSocket serverSocket;

    /*
     * True as long as we don't get socket errors
     */
    private boolean socketOK = true;
    static final private int SENDER_REQUEST = 1;

    ListView listViewItems;
    PeerDbAdapter db;
    SimpleCursorAdapter mAdapter;
    Cursor mCursor;
    Button next;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        /**
         * Let's be clear, this is a HACK to allow you to do network communication on the main thread.
         * This WILL cause an ANR, and is only provided to simplify the pedagogy.  We will see how to do
         * this right in a future assignment (using a Service managing background threads).
         */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            int port = Integer.parseInt(this.getString(R.string.app_port));
            serverSocket = new DatagramSocket(port);
        } catch (Exception e) {
            Log.e(TAG, "Cannot open socket" + e.getMessage());
            return;
        }

        listViewItems = (ListView) findViewById(R.id.msgList);


        try {
            db = new PeerDbAdapter(this);
            db.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        mCursor = db.fetchAllMessages();
        startManagingCursor(mCursor);

        String[] columns = new String[]{MessageContract.TXT};
        int[] to = new int[]{R.id.row_message};

        mAdapter = new SimpleCursorAdapter(this, R.layout.message, mCursor, columns, to);
        listViewItems.setAdapter(mAdapter);

    }

    public void onClick(View v) {


        byte[] receiveData = new byte[1024];

        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        try {

            serverSocket.receive(receivePacket);
            Log.i(TAG, "Received a packet");

            InetAddress sourceIPAddress = receivePacket.getAddress();
            Log.i(TAG, "Source IP Address: " + sourceIPAddress);

            byte data[] = receivePacket.getData();
            String str = new String(data, "UTF-8");

            String[] result = str.split(":");
            String sender = result[0], messageTxt = result[1];
            int port_num = Integer.parseInt(this.getString(R.string.app_port));

            Peer peer = new Peer(sender,sourceIPAddress,port_num);
            Message message_txt = new Message(messageTxt, sender);
            db.persist(peer,message_txt);

            mCursor = db.fetchAllMessages();
            startManagingCursor(mCursor);
            mAdapter.swapCursor(mCursor);
			/*
			 * End Todo
			 */

        } catch (Exception e) {
            Log.e(TAG, "Problems receiving packet: " + e.getMessage());
            socketOK = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_server_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId())
        {
            case R.id.sender:
                Intent addIntent = new Intent(this, PeerListActivity.class);
                startActivityForResult(addIntent, SENDER_REQUEST);
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        // TODO Handle results from the Search and Checkout activities.

        // Use SEARCH_REQUEST and CHECKOUT_REQUEST codes to distinguish the cases.
       // if(requestCode == SENDER_REQUEST && resultCode == RESULT_OK) {
            // In case need more operation in future
       // }
    }

    /*
     * Close the socket before exiting application
     */
    public void closeSocket() {
        serverSocket.close();
    }

    /*
     * If the socket is OK, then it's running
     */
    boolean socketIsOK() {
        return socketOK;
    }

}