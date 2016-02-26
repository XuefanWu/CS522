package edu.stevens.cs522.chat.oneway.server.contract;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by Xuefan on 2/14/2016.
 */
public class MessageContract {

    // Constructor
    public MessageContract() {
    }

    public static final String ID = "_id";
    public static final String MESSAGE = "message";
    public static final String SENDER = "sender";
    public static final String TXT = "txt";
    public static final String PEER_FK = "peer_fk";


    // accessor for Id
    public static long getId(Cursor cursor) {
        long a = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
        return a;
    }
    public static void putId(ContentValues values, long id) {
        values.put(ID, id);
    }

    // accessor for Message
    public static String getMessage(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE));
    }
    public static void putMessage(ContentValues values, String message) {
        values.put(MESSAGE, message);
    }

    // accessor for Txt
    public static String getTxt(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(TXT));
    }
    public static void putTxt(ContentValues values, String txt) {
        values.put(TXT, txt);
    }

    // accessor for Sender
    public static String getSender(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(SENDER));
    }
    public static void putSender(ContentValues values, String sender) {
        values.put(SENDER, sender);
    }


    // accessor for PEER_FK
    public static long getPeer_fk(Cursor cursor) {
        long a = cursor.getInt(cursor.getColumnIndexOrThrow(PEER_FK));
        return a;
    }
    public static void putPeer_fk(ContentValues values, long fk) {
        values.put(PEER_FK, fk);
    }

}
