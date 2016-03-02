package edu.stevens.cs522.chat.oneway.server.managers;

import android.content.ContentValues;
import android.content.Context;

import edu.stevens.cs522.chat.oneway.server.contract.MessageContract;
import edu.stevens.cs522.chat.oneway.server.entities.Message;
import edu.stevens.cs522.chat.oneway.server.entities.Peer;
import edu.stevens.cs522.chat.oneway.server.providers.PeerProvider;

/**
 * Created by Xuefan on 2/26/2016.
 */
public class MessageManager extends Manager<Message>{


    public MessageManager(Context context, IEntityCreator creator, int loadID) {
        super(context, creator, loadID);
    }

    public void persistAsync(final Peer peer,final Message message){
        int flag=0;
        while (flag ==0){
             flag = (int) PeerProvider.getPeer_fk(peer);

    }
    ContentValues contentValues = new ContentValues();
    MessageContract.putMessage(contentValues, message.messageText);
    MessageContract.putPeer_fk(contentValues, flag);
    getAsyncContentResolver().insertAsync(MessageContract.CONTENT_URI,
                                          contentValues, null);}
//    public void removeAsync(long ID) {
//        getAsyncContentResolver().deleteAsync(BookContract.CONTENT_URI(String.valueOf(ID)));
//    }
//    public void removeAsync() {
//
//        getAsyncContentResolver().deleteAsync(BookContract.CONTENT_URI);
//    }



}
