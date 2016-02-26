package edu.stevens.cs522.chat.oneway.server.activities;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.sql.SQLException;

import edu.stevens.cs522.chat.oneway.server.R;
import edu.stevens.cs522.chat.oneway.server.contract.MessageContract;
import edu.stevens.cs522.chat.oneway.server.database.PeerDbAdapter;
import edu.stevens.cs522.chat.oneway.server.entities.Peer;

public class PeerDetailActivity extends ListActivity {

    private ListView listViewItems;
    private PeerDbAdapter db;
    private SimpleCursorAdapter mAdapter;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peer_detail);

        Bundle b =getIntent().getExtras();
        Peer peer_detail = b.getParcelable(ChatServer.EXTRA_MESSAGE);
        String Peer = "Sender : " + peer_detail.name;
        String Address = "\nAddress : " + peer_detail.address.getHostAddress();
        String Port = "\nPort : " + peer_detail.port + "\n\n\n";

        TextView tt;
        tt =(TextView)findViewById(R.id.name);
        tt.setText(Peer);
        tt =(TextView)findViewById(R.id.address);
        tt.setText(Address);
        tt =(TextView)findViewById(R.id.port);
        tt.setText(Port);

        listViewItems = getListView();

        db = new PeerDbAdapter(this);
        try {
            db.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mCursor = db.fetchMessages(peer_detail.id);
        startManagingCursor(mCursor);

        String[] columns = new String[] {MessageContract.MESSAGE};
        int[] to = new int[] { R.id.name };

        mAdapter = new SimpleCursorAdapter(this, R.layout.message_name, mCursor, columns, to);
        listViewItems.setAdapter(mAdapter);


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
