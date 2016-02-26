package edu.stevens.cs522.bookstore.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.sql.SQLException;
import java.util.HashMap;

import edu.stevens.cs522.bookstore.contracts.AuthorContract;
import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.entities.Book;

/**
 * Created by Xuefan on 2/22/2016.
 */
public class BookProvider extends ContentProvider{

    //static final String PROVIDER_NAME = "edu.stevens.cs522.bookstore";
    //static final String URL = "content://" + PROVIDER_NAME + "/BookTable";
    public static final Uri CONTENT_URI = BookContract.CONTENT_URI;
    public static final Uri Author_CONTENT_URI = AuthorContract.CONTENT_URI;

//    public static final String KEY_TITLE_COLUMN = "title";
//    public static final String KEY_ISBN_COLUMN = "isbn";
//    public static final String KEY_PRICE_COLUMN = "price";
//    public static final String KEY_AUTHORS_COLUMN = "authors";
    public static final String KEY_BOOK_FK_COLUMN = "book_fk";
    public static final String KEY_AUTHOR_NAME_COLUMN = "author_name";
    public static final String KEY_AUTHORSBOOK_INDEX = "AuthorsBookIndex";

    public static final String DATABASE_NAME = "BookStoreDB.db";
    private static final int DATABASE_VERSION = 1;
    public static final String COLUMN_ID = "_id";
    public static final String AUTHOR_COLUMN_ID = "__id";
    private static SQLiteDatabase db;
    public DatabaseHelper dbHelper;

    private static final String BOOK_TABLE = "BookTable";
    private static final String AUTHOR_TABLE = "AuthorTable";


    private static HashMap<String, String> BOOK_PROJECTION_MAP;
    static final int BOOK = 1;
    static final int BOOK_ID = 2;
    static final int AUTHOR= 3;
    static final int AUTHOR_ID = 4;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(BookContract.AUTHORITY, BookContract.CONTENT_PATH, BOOK);
        uriMatcher.addURI(BookContract.AUTHORITY, BookContract.CONTENT_PATH_ITEM, BOOK_ID);
        uriMatcher.addURI(AuthorContract.AUTHORITY, AuthorContract.CONTENT_PATH, AUTHOR);
        uriMatcher.addURI(AuthorContract.AUTHORITY, AuthorContract.CONTENT_PATH_ITEM, AUTHOR_ID);
    }



    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return (db == null)? false:true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        //qb.setTables(BOOK_TABLE);
        qb.setTables(BOOK_TABLE+" LEFT OUTER JOIN AuthorTable ON (BookTable._id = AuthorTable.book_fk)");
        switch (uriMatcher.match(uri)){
            case BOOK:
                //qb.setProjectionMap(BOOK_PROJECTION_MAP);
                break;
            case BOOK_ID:
                qb.appendWhere(BookContract.COLUMN_ID+ "=" + uri.getPathSegments().get(1));
                break;
            case AUTHOR:

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
//        if (sortOrder == null || sortOrder == ""){
//            /**
//             * By default sort on student names
//             */
//            sortOrder = String.valueOf(BOOK_ID);
//        }
        Cursor c = qb.query(db, projection,  selection, selectionArgs, " BookTable._id, BookTable.title, BookTable.price, BookTable.isbn", null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case BOOK:
                return "";
            case BOOK_ID:
                return "";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri _uri = null;
        switch (uriMatcher.match(uri)){
            case BOOK:
                long rowId = db.insert(BOOK_TABLE,null,values);
                if(rowId>0){
                    _uri = ContentUris.withAppendedId(CONTENT_URI,rowId);
                    ContentResolver cr = getContext().getContentResolver();
                    cr.notifyChange(_uri, null);
                }
                break;
            case AUTHOR:
                long rowId1 = db.insert(AUTHOR_TABLE,null,values);
                if(rowId1>0){
                    _uri = ContentUris.withAppendedId(Author_CONTENT_URI,rowId1);
                    ContentResolver cr = getContext().getContentResolver();
                    cr.notifyChange(_uri, null);
                }
                break;
            default:
                try {
                    throw new SQLException("Failed to insert row into " + uri);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return _uri;
        }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int count = 0;
        switch (uriMatcher.match(uri)){
            case BOOK:
                count = db.delete(BOOK_TABLE,selection,selectionArgs);
                break;
            case BOOK_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete( BOOK_TABLE, BookContract.COLUMN_ID +  " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND ("
                                + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI"+uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)){
            case BOOK:
                count = db.update(BOOK_TABLE, values, selection, selectionArgs);
                break;
            case BOOK_ID:
                count = db.update(BOOK_TABLE, values, BookContract.COLUMN_ID +
                        " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND ("
                                + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    public static int getBook_fk(Book book){
        Cursor c =db.query(BOOK_TABLE,
                new String[] {BookContract.COLUMN_ID,BookContract.TITLE,BookContract.ISBN
                        ,BookContract.PRICE,BookContract.AUTHORS},
                BookContract.TITLE + " =? ",new String[]{book.title},null,null,null,null);

        int id = 0;
        while (c!=null &&c.moveToNext()) {
            id = c.getInt(c.getColumnIndexOrThrow(COLUMN_ID));}
        return id;
    }

    public static int cartSize(){
        String query = "SELECT ROWID from BookTable";
        Cursor c = db.rawQuery(query, null);
        int count = 0;
        while (c!=null &&c.moveToNext()) {
            count++;
        }
        return count;
    }


    //dbHelper
    private class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_CREATE = "create table "+
                BOOK_TABLE + " (" + BookContract.COLUMN_ID + " integer primary key autoincrement, "
                +BookContract.TITLE +", "
                + BookContract.ISBN+ ", "
                +BookContract.PRICE+", "
                +BookContract.AUTHORS+")";

        public static final String DATABASE_AUTHOR_CREATE = "create table "+
                AUTHOR_TABLE + " (" + AUTHOR_COLUMN_ID + " integer primary key autoincrement, "
                +KEY_AUTHOR_NAME_COLUMN+" integer not null, "
                +KEY_BOOK_FK_COLUMN + ", "
                + "foreign key ("+KEY_BOOK_FK_COLUMN
                +") references BookTable(_id) on delete cascade)";


        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("PRAGMA foreign_keys=ON;");
            db.execSQL(DATABASE_CREATE);
            db.execSQL(DATABASE_AUTHOR_CREATE);
            db.execSQL("create index "+KEY_AUTHORSBOOK_INDEX+" on AuthorTable("+KEY_BOOK_FK_COLUMN+")");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(DatabaseHelper.class.getName(),
                    "Upgrading database from version " + oldVersion + " to "
                            + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + BOOK_TABLE);
            onCreate(db);
        }
    }
}
