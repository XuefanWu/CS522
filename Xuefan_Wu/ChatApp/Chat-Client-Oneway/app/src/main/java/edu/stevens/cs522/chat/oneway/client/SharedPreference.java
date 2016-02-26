package edu.stevens.cs522.chat.oneway.client;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Xuefan on 2/16/2016.
 */
public class SharedPreference {
    public static final String PREFS_NAME = "AOP_PREFS";
    public static final String PREFS_KEY = "AOP_PREFS_String";
    SharedPreferences sharedpreferences;

    public SharedPreference() {
        super();
    }

    public void save(Context context ,String text){
        sharedpreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PREFS_KEY,text);
        editor.commit();
    }

    public String getValue(Context context) {
        SharedPreferences settings;
        String text;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        text = settings.getString(PREFS_KEY, null);
        return text;
    }

}
