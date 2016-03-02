package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.contracts.AuthorContract;
import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.entities.Book;
import edu.stevens.cs522.bookstore.manager.BookManager;
import edu.stevens.cs522.bookstore.manager.IEntityCreator;
import edu.stevens.cs522.bookstore.manager.ISimpleQueryListener;

public class BookDetailActivity extends Activity {

    private static final int BOOK_LOADER_ID = 1;
    private static final int AUTHORS_LOADER_ID = 2;
    int Column_id;
    BookManager bookManager;
    String[] projections = {BookContract.COLUMN_ID, BookContract.TITLE,
            BookContract.ISBN,BookContract.PRICE,
            "GROUP_CONCAT("+AuthorContract.AUTHOR_NAME+", '|') as "+ BookContract.AUTHORS };


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_detail);
        Intent intent = getIntent();
        Column_id=intent.getIntExtra(BookStoreActivity.BOOK_DETAIL_ID_KEY, 0);
        bookManager = new BookManager(this,new IEntityCreator<Book>() {
            @Override
            public Book create(Cursor cursor) {
                return new Book(cursor);
            }
        },BOOK_LOADER_ID);
        bookManager.executeSimpleQuery(BookContract.CONTENT_URI(String.valueOf(Column_id)),projections,null,null,
                new ISimpleQueryListener<Book>(){

                    @Override
                    public void handleResults(List<Book> results) {
                        renderTextView(results.get(0));
                    }
                });
    }



        private void renderTextView(Book book){
        TextView textView;
        textView = (TextView) findViewById(R.id.book_title);
        textView.setText(book.title);
        textView = (TextView) findViewById(R.id.book_author);
        String authorString = "";
        for(int i = 0; i<book.authors.length;i++){
            authorString+=book.authors[i].toString();
            if(i!=book.authors.length-1){
                authorString+=" | ";
            }
        }
        textView.setText(authorString);
        textView = (TextView) findViewById(R.id.book_isbn);
        textView.setText(book.isbn);
        textView = (TextView) findViewById(R.id.book_price);
        textView.setText(book.price);
    }

}