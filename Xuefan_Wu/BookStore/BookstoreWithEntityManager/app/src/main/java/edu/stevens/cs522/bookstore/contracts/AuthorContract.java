package edu.stevens.cs522.bookstore.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by Xuefan on 2/25/2016.
 */
public class AuthorContract {

    public static final String BOOK_FK = "book_fk";
    public static final String AUTHOR_NAME = "author_name";
    public static final String AUTHOR_COLUMN_ID = "__id";
    public static final String AUTHORITY = "edu.stevens.cs522.bookstore";
    public static final String PATH ="AuthorTable";
    public static final Uri CONTENT_URI = CONTENT_URI(AUTHORITY,PATH);
    public static final String CONTENT_PATH = CONTENT_PATH(CONTENT_URI);
    public static final String CONTENT_PATH_ITEM = CONTENT_PATH(CONTENT_URI("#"));


    public static Uri CONTENT_URI(String authority,String path){
        return new Uri.Builder().scheme("content")
                .authority(authority)
                .path(path)
                .build();
    }


    public static Uri withExtendedPath(Uri uri, String... path){
        Uri.Builder builder = uri.buildUpon();
        for(String p:path)
            builder.appendPath(p);
        return builder.build();
    }
    //override
    public static Uri CONTENT_URI(String id){
        return withExtendedPath(CONTENT_URI, id);
    }
    public static String CONTENT_PATH(Uri uri){
        return uri.getPath().substring(1);
    }


    public static String getBookFk(Cursor cursor) {
        String s = null;
        if(cursor != null && cursor.moveToFirst()){
            s= cursor.getString(cursor.getColumnIndexOrThrow(BOOK_FK));
        }
        return s;
    }

    public static long getAuthorColumnID(Cursor cursor) {
        long s = 0;
        if(cursor != null && cursor.moveToFirst()){
            s= cursor.getLong(cursor.getColumnIndexOrThrow(AUTHOR_COLUMN_ID));
        }
        return s;
    }





    public static String getAuthorName(Cursor cursor) {
        String s = null;
        if(cursor != null){
            s= cursor.getString(cursor.getColumnIndexOrThrow(AUTHOR_NAME));
        }
        return s;
    }


    public static void putBookFK(ContentValues values, String book_fk) {

        values.put(BOOK_FK, book_fk);
    }
    public static void putAuthorName(ContentValues values, String authorName) {
        values.put(AUTHOR_NAME, authorName);
    }

    public static void putAuthorColumnID(ContentValues values, long id){
        values.put(AUTHOR_COLUMN_ID,id);
    }



}
