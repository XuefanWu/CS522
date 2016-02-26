package edu.stevens.cs522.bookstore.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;

import edu.stevens.cs522.bookstore.entities.Book;


/**
 * Created by Xuefan on 2/4/2016.
 */
public class CartDbAdapter  {
    public static final String KEY_TITLE_COLUMN = "title";
    public static final String KEY_ISBN_COLUMN = "isbn";
    public static final String KEY_PRICE_COLUMN = "price";
    public static final String KEY_AUTHORS_COLUMN = "authors";
    public static final String KEY_BOOK_FK_COLUMN = "book_fk";
    public static final String KEY_AUTHOR_NAME_COLUMN = "author_name";
    public static final String KEY_AUTHORSBOOK_INDEX = "AuthorsBookIndex";

    public static final String DATABASE_NAME = "BookStoreDB.db";
    private static final int DATABASE_VERSION = 1;
    public static final String COLUMN_ID = "_id";
    public static final String AUTHOR_COLUMN_ID = "__id";
    private SQLiteDatabase db;
    DatabaseHelper dbHelper;
    private final Context context;


    public CartDbAdapter(Context _context){
        context = _context;
        dbHelper = new DatabaseHelper(context,DATABASE_NAME,null,DATABASE_VERSION);

    }

    public CartDbAdapter open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() { db.close(); }


    public Book fetchBook(long rowId){
        String where = COLUMN_ID + "=" +rowId;
        String query = "SELECT BookTable._id, BookTable.title, BookTable.price, BookTable.isbn," +
                " GROUP_CONCAT("+KEY_AUTHOR_NAME_COLUMN+", '|') as "
                +KEY_AUTHORS_COLUMN+" FROM "+DatabaseHelper.BOOK_TABLE +
                " LEFT OUTER JOIN AuthorTable ON BookTable._id = AuthorTable.book_fk " +
                "WHERE "+COLUMN_ID+" =?"+
                " GROUP BY BookTable._id, BookTable.title, BookTable.price, BookTable.isbn";
        Cursor c;
        c = db.rawQuery(query,new String[]{String.valueOf(rowId)});
        //if(c.getInt(c.getColumnIndexOrThrow(COLUMN_ID)) != )

        //TODO
        Book book = new Book(c);
        return book;
    }

    public Cursor fetchAllBooks(){
        db = dbHelper.getWritableDatabase();
        return db.query(DatabaseHelper.BOOK_TABLE,
                new String[] {COLUMN_ID,KEY_TITLE_COLUMN,KEY_ISBN_COLUMN
                ,KEY_PRICE_COLUMN,KEY_AUTHORS_COLUMN},
                null,null,null,null,null);
    }

    public void persist(Book book) throws SQLException{
        ContentValues contentValues = new ContentValues();
        book.writeToProvider(contentValues,book);
        /*contentValues.put(KEY_TITLE_COLUMN, book.title);
        contentValues.put(KEY_ISBN_COLUMN, book.isbn);
        contentValues.put(KEY_PRICE_COLUMN,book.price);
        contentValues.put(KEY_AUTHORS_COLUMN, book.authors[0].toString());*/

        db.insert(DatabaseHelper.BOOK_TABLE, null, contentValues);
        return;
    }

    public boolean delete(long id){
        db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.BOOK_TABLE,COLUMN_ID + "="+id,null)>0;
    }

    public boolean deleteAll(){
        db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.BOOK_TABLE, null, null)>0;
    }

    public int cartSize(){
        String query = "SELECT ROWID from BookTable";
        Cursor c = db.rawQuery(query, null);
        int count = 0;
        while (c!=null &&c.moveToNext()) {
            count++;
        }
        c.close();
        return count;
    }

    public int getBook_fk(Book book){
        db = dbHelper.getWritableDatabase();
        Cursor c =db.query(DatabaseHelper.BOOK_TABLE,
                new String[] {COLUMN_ID,KEY_TITLE_COLUMN,KEY_ISBN_COLUMN
                        ,KEY_PRICE_COLUMN,KEY_AUTHORS_COLUMN},
               KEY_TITLE_COLUMN + " =? ",new String[]{book.title},null,null,null,null);

        int id = 0;
        while (c!=null &&c.moveToNext()) {
        id = c.getInt(c.getColumnIndexOrThrow(COLUMN_ID));}
        return id;
    }

    public void addAuthor(Book book) throws SQLException {
        for(int i = 0; i<book.authors.length;i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_AUTHOR_NAME_COLUMN, book.authors[i].toString());
            contentValues.put(KEY_BOOK_FK_COLUMN, getBook_fk(book));
            db.insert(DatabaseHelper.AUTHOR_TABLE, null, contentValues);
        }
        return;
    }


    private class DatabaseHelper extends SQLiteOpenHelper{

        private static final String BOOK_TABLE = "BookTable";
        private static final String AUTHOR_TABLE = "AuthorTable";

        private static final String DATABASE_CREATE = "create table "+
                BOOK_TABLE + " (" + COLUMN_ID + " integer primary key autoincrement, "
                + KEY_TITLE_COLUMN +", "
                + KEY_ISBN_COLUMN + ", "
                +KEY_PRICE_COLUMN+", "
                +KEY_AUTHORS_COLUMN+")";

        public static final String DATABASE_AUTHOR_CREATE = "create table "+
                AUTHOR_TABLE + " (" + AUTHOR_COLUMN_ID + " integer primary key autoincrement, "
                +KEY_AUTHOR_NAME_COLUMN+" integer not null, "
                +KEY_BOOK_FK_COLUMN + ", "
                + "foreign key ("+KEY_BOOK_FK_COLUMN
                +") references BookTable(_id) on delete cascade)";


        public DatabaseHelper(Context context, String name,
                              SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
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
                    +  newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + BOOK_TABLE);
            onCreate(db);
        }
    }
}
