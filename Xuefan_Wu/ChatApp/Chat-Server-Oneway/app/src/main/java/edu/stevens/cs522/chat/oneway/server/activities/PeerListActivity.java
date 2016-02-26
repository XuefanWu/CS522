package edu.stevens.cs522.chat.oneway.server.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.sql.SQLException;

import edu.stevens.cs522.chat.oneway.server.R;
import edu.stevens.cs522.chat.oneway.server.contract.PeerContract;
import edu.stevens.cs522.chat.oneway.server.database.PeerDbAdapter;
import edu.stevens.cs522.chat.oneway.server.entities.Peer;

public class PeerListActivity extends ListActivity {

    static final private int DETAIL_REQUEST = 1;
    private ListView listViewItems;
    private PeerDbAdapter db;
    private SimpleCursorAdapter mAdapter;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peer_list);

        listViewItems = getListView();
        db = new PeerDbAdapter(this);
        try {
            db.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        mCursor = db.fetchAllPeers();
        startManagingCursor(mCursor);
        String[] columns = new String[] { PeerContract.NAME };
        int[] to = new int[] { R.id.name };
        mAdapter = new SimpleCursorAdapter(this, R.layout.peer_name, mCursor, columns, to);
        listViewItems.setAdapter(mAdapter);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Cursor cursor = (Cursor)mAdapter.getItem(position);
        Intent detailIntent = new Intent(this,PeerDetailActivity.class);
        Peer p = new Peer(cursor);
        detailIntent.putExtra(ChatServer.EXTRA_MESSAGE,p);
        startActivityForResult(detailIntent,DETAIL_REQUEST);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_peer_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.back) {
            setResult(RESULT_CANCELED, null);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
