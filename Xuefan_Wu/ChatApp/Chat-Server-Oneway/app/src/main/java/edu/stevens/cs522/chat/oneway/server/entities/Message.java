package edu.stevens.cs522.chat.oneway.server.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import edu.stevens.cs522.chat.oneway.server.contract.MessageContract;

/**
 * Created by Xuefan on 2/14/2016.
 */
public class Message implements Parcelable {
    public long id;
    public String messageText;
    public String sender;

    public Message(String messageText,String sender){
        this.messageText = messageText;
        this.sender = sender;
    }

    protected Message(Parcel in) {
        id = in.readLong();
        messageText = in.readString();
        sender = in.readString();
    }

    public Message(Cursor cursor){
        this.id = MessageContract.getId(cursor);
        this.messageText = MessageContract.getMessage(cursor);

    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {

        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }


        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(messageText);
        parcel.writeString(sender);
    }

    public void writeToProvider(ContentValues values,Message message) {
        MessageContract.putMessage(values,message.messageText);
        MessageContract.putId(values, message.id);
    }
    @Override
    public String toString(){
        return this.sender + " : " + this.messageText;}

}