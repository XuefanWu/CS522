package edu.stevens.cs522.bookstore.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.util.regex.Pattern;

import edu.stevens.cs522.bookstore.entities.Author;

/**
 * Created by Xuefan on 2/4/2016.
 */
public class BookContract {

    public static final String TITLE = "title";
    public static final String AUTHORS = "authors";
    public static final String ISBN = "isbn";
    public static final String PRICE = "price";
    public static final String COLUMN_ID = "_id";
    public static final String AUTHORITY = "edu.stevens.cs522.bookstore";
    public static final String PATH ="BookTable";
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


    public static String getTitle(Cursor cursor) {
        String s = null;
        if(cursor != null){
            s= cursor.getString(cursor.getColumnIndexOrThrow(TITLE));
        }
        return s;
    }

    public static long getColumnId(Cursor cursor) {
        long s = 0;
        if(cursor != null && cursor.moveToFirst()){
            s= cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
        }
        return s;
    }

    public static Author[] getAuthors(Cursor cursor) {
        String s = null;
        if(cursor != null && cursor.moveToFirst()){
            s= cursor.getString(cursor.getColumnIndexOrThrow(AUTHORS));
        }
        String[] string = readStringArray(s);
        Author[] authors = new Author[string.length];
        for(int i = 0;i<string.length;i++) {
            String[] subString = string[i].split(" ");

            Author author = new Author();
            author.firstName = subString[0];
            switch (subString.length) {
                case 1:
                    break;
                case 2:
                    author.lastName = subString[1];
                    break;
                case 3:
                    author.middleInitial = subString[1];
                    author.lastName = subString[2];
                    break;
            }
            authors[i] = author;

        }
        return authors;
        }
    public static String getAuthorName(Cursor cursor) {
        String s = null;
        if(cursor != null){
            s= cursor.getString(cursor.getColumnIndexOrThrow(AUTHORS));
        }
        return s;
    }

    public static String getIsbn(Cursor cursor) {
        String s = null;
        if(cursor != null && cursor.moveToFirst()){
            s= cursor.getString(cursor.getColumnIndexOrThrow(ISBN));
        }
        return s;
    }

    public static String getPrice(Cursor cursor) {
        String s = null;
        if(cursor != null && cursor.moveToFirst()){
            s= cursor.getString(cursor.getColumnIndexOrThrow(PRICE));
        }
        return s;
    }

    public static void putTitle(ContentValues values, String title) {

        values.put(TITLE, title);
    }
    public static void putAuthors(ContentValues values, String authors) {
        values.put(AUTHORS, authors);
    }
    public static void putIsbn(ContentValues values, String isbn) {

        values.put(ISBN,isbn);
    }
    public static void putPrice(ContentValues values, String price) {

        values.put(PRICE,price);
    }
    public static void putColumnID(ContentValues values, long id){
        values.put(COLUMN_ID,id);
    }


    public static final char SEPARATOR_CHAR = '|';
    private static final Pattern SEPARATOR =
            Pattern.compile(Character.toString(SEPARATOR_CHAR), Pattern.LITERAL);
    public static String[] readStringArray(String in) {
        return SEPARATOR.split(in);
    }

}
