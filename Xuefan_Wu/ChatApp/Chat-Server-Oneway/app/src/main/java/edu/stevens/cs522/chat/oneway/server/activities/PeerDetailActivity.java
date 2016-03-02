package edu.stevens.cs522.chat.oneway.server.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import edu.stevens.cs522.chat.oneway.server.R;
import edu.stevens.cs522.chat.oneway.server.adapters.MessageAdapter;
import edu.stevens.cs522.chat.oneway.server.contract.MessageContract;
import edu.stevens.cs522.chat.oneway.server.contract.PeerContract;
import edu.stevens.cs522.chat.oneway.server.entities.Message;
import edu.stevens.cs522.chat.oneway.server.entities.Peer;
import edu.stevens.cs522.chat.oneway.server.managers.IEntityCreator;
import edu.stevens.cs522.chat.oneway.server.managers.IQueryListener;
import edu.stevens.cs522.chat.oneway.server.managers.ISimpleQueryListener;
import edu.stevens.cs522.chat.oneway.server.managers.MessageManager;
import edu.stevens.cs522.chat.oneway.server.managers.PeerManager;
import edu.stevens.cs522.chat.oneway.server.managers.TypedCursor;

public class PeerDetailActivity extends ListActivity {

    private ListView listViewItems;
    private MessageAdapter mAdapter;
    private static final int PEER_LOADER_ID = 1;
    private static final int MESSAGE_LOADER_ID = 1;
    int column_id = 0;
    PeerManager peerManager;
    MessageManager messageManager;
    String[] projections = {PeerContract.ID,PeerContract.NAME,PeerContract.ADDRESS,
    PeerContract.PORT};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peer_detail);

        Intent intent = getIntent();
        column_id = intent.getIntExtra(PeerListActivity.PEER_DETAIL_ID_KEY,0);
        peerManager = new PeerManager(this, new IEntityCreator<Peer>() {
            @Override
            public Peer create(Cursor cursor) {
                return new Peer(cursor);
            }
        },PEER_LOADER_ID);
        peerManager.executeSimpleQuery(
                PeerContract.CONTENT_URI(String.valueOf(column_id)), projections, null, null,
                new ISimpleQueryListener<Peer>() {
                    @Override
                    public void handleResults(List<Peer> results) {
                        renderView(results.get(0));
                    }
                });


        messageManager = new MessageManager(this, new IEntityCreator<Message>() {
            @Override
            public Message create(Cursor cursor) {
                return new Message(cursor);
            }
        },MESSAGE_LOADER_ID);
        messageManager.executeQuery(
                MessageContract.CONTENT_URI(String.valueOf(column_id)), null, null, null,
                new IQueryListener<Message>() {
                    @Override
                    public void handleResult(TypedCursor<Message> results) {
                        mAdapter.swapCursor(results.getCursor());
                    }
                    @Override
                    public void closeResult() {
                        mAdapter.swapCursor(null);
                    }

                });
        listViewItems = getListView();
        mAdapter = new MessageAdapter(this, android.R.layout.simple_list_item_2,null);
        listViewItems.setAdapter(mAdapter);
    }

    private void renderView(Peer peer){
        String Peer = "Sender : " + peer.name;
        String Address = "\nAddress : " + peer.address.getHostAddress();
        String Port = "\nPort : " + peer.port + "\n\n\n";
        TextView tt;
        tt =(TextView)findViewById(R.id.name);
        tt.setText(Peer);
        tt =(TextView)findViewById(R.id.address);
        tt.setText(Address);
        tt =(TextView)findViewById(R.id.port);
        tt.setText(Port);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_peer_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.back) {
            setResult(RESULT_CANCELED, null);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
