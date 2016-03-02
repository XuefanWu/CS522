package edu.stevens.cs522.bookstore.manager;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import edu.stevens.cs522.bookstore.contracts.AuthorContract;
import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.entities.Book;
import edu.stevens.cs522.bookstore.providers.BookProvider;

/**
 * Created by Xuefan on 2/26/2016.
 */
public class BookManager extends Manager<Book>{


    public BookManager(Context context, IEntityCreator creator, int loadID) {
        super(context, creator, loadID);
    }

    public void persistAsync(final Book book){
        ContentValues contentValues = new ContentValues();
        book.writeToProvider(contentValues,book);
        getAsyncContentResolver().insertAsync(BookContract.CONTENT_URI, contentValues,
                new IContinue<Uri>() {
                    @Override
                    public void kontinue(Uri value) {
                        for(int i = 0; i<book.authors.length;i++) {
                            ContentValues values = new ContentValues();
                            values.put(AuthorContract.AUTHOR_NAME, book.authors[i].toString());
                            values.put(AuthorContract.BOOK_FK, BookProvider.getBook_fk(book));
                            getAsyncContentResolver().insertAsync(AuthorContract.CONTENT_URI, values,null);
                        }


                    }
                }
        );

    }

    public void removeAsync(long ID) {
        getAsyncContentResolver().deleteAsync(BookContract.CONTENT_URI(String.valueOf(ID)));
    }
    public void removeAsync() {

        getAsyncContentResolver().deleteAsync(BookContract.CONTENT_URI);
    }



}
