package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.contracts.AuthorContract;
import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.entities.Author;

public class BookDetailActivity extends Activity
        implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int BOOK_LOADER_ID = 1;
    private static final int AUTHORS_LOADER_ID = 2;
    SimpleCursorAdapter mAdapter;
    int Column_id;
    ArrayAdapter AuthorAdapter;
    ArrayList<String> author_list = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_detail);
        Intent intent = getIntent();
        Column_id=intent.getIntExtra(BookStoreActivity.BOOK_DETAIL_ID_KEY, 0);
        //Book book = intent.getParcelableExtra(BookStoreActivity.BOOK_DETAIL_KEY);
        final ListView listView = (ListView)findViewById(R.id.detail_view);
        final ListView listViewAuthor = (ListView)findViewById(R.id.detail_author_view);
        String[] columns = new String[] { BookContract.TITLE,
                BookContract.PRICE,BookContract.ISBN };
        int[] to = new int[] { R.id.detail_title, R.id.detail_price,R.id.detail_isbn};

        //mAdapter = new BookAdapter(this, android.R.layout.simple_list_item_2,null);
        mAdapter = new SimpleCursorAdapter(this, R.layout.book_detail_list, null, columns, to);

        listView.setAdapter(mAdapter);


        //lm.initLoader(AUTHORS_LOADER_ID,null,this);
        //author[0]="gdsgfdsfdfdsfds";
        AuthorAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, android.R.id.text1,author_list);
        listViewAuthor.setAdapter(AuthorAdapter);

        LoaderManager lm = getLoaderManager();
        lm.initLoader(BOOK_LOADER_ID,null,this);

    }

//    private void renderTextView(Book book){
//        TextView textView;
//        textView = (TextView) findViewById(R.id.book_title);
//        textView.setText(book.title);
//        textView = (TextView) findViewById(R.id.book_author);
//        String authorString = "";
//        for(int i = 0; i<book.authors.length;i++){
//            authorString+=book.authors[i].toString();
//            if(i!=book.authors.length-1){
//                authorString+=" | ";
//            }
//        }
//        textView.setText(authorString);
//        textView = (TextView) findViewById(R.id.book_isbn);
//        textView.setText(book.isbn);
//        textView = (TextView) findViewById(R.id.book_price);
//        textView.setText(book.price);
//    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case BOOK_LOADER_ID:
                String[] projections = {BookContract.COLUMN_ID, BookContract.TITLE,
                        BookContract.ISBN,BookContract.PRICE,
                        "GROUP_CONCAT("+AuthorContract.AUTHOR_NAME+", '|') as "+ BookContract.AUTHORS };
                return new CursorLoader(this,
                        BookContract.CONTENT_URI(String.valueOf(Column_id)),
                        projections,null,null,null);

            case AUTHORS_LOADER_ID:
                String select2 = "(" + AuthorContract.AUTHOR_COLUMN_ID + " NOTNULL) AND ("
                        + AuthorContract.BOOK_FK  + "="+id+" )";
                String[] projections2 = {AuthorContract.AUTHOR_COLUMN_ID, AuthorContract.AUTHOR_NAME
                            ,AuthorContract.BOOK_FK};
                return new CursorLoader(this,
                        AuthorContract.CONTENT_URI,
                        projections2,select2,null,null);

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Author[] authors = BookContract.getAuthors(data);
        for(int i = 0; i<authors.length;i++){
            author_list.add(authors[i].toString());
        }
        this.mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.mAdapter.swapCursor(null);
    }
}