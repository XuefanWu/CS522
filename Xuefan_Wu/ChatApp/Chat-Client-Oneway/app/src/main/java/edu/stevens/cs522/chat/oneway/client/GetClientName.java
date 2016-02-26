package edu.stevens.cs522.chat.oneway.client;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class GetClientName extends Activity {


    EditText editText;
    SharedPreference sharedpreference;


    public void sendMessage(View view){
        editText = (EditText)findViewById(R.id.editText1);
        String clientName = editText.getText().toString();
        //save(clientName);
        Context context = getApplicationContext();
        sharedpreference = new SharedPreference();
        sharedpreference.save(context, clientName);
        Intent intent = new Intent(this,ChatClient.class);
        intent.putExtra(ChatClient.CLIENT_NAME_KEY, clientName);
        startActivity(intent);


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_client_name);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_get_client_name, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
