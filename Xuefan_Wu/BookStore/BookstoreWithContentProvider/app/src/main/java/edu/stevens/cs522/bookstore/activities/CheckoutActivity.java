package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.databases.CartDbAdapter;

public class CheckoutActivity extends Activity {
	CartDbAdapter db = new CartDbAdapter(this);
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checkout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// TODO display ORDER and CANCEL options.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.checkout_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		// TODO
		switch(item.getItemId()){
			case R.id.checkout_order:
				// ORDER: display a toast message of how many books have been ordered and return
				int cart_size = (int)getIntent().getIntExtra(BookStoreActivity.CART_SIZE_KEY,0);
				Log.v("sssssssssss",String.valueOf(cart_size));
				Toast.makeText(this,getSize(cart_size),Toast.LENGTH_SHORT).show();
				setResult(RESULT_OK);
				finish();
				return true;
			case R.id.checkout_cancel:
				// CANCEL: just return with REQUEST_CANCELED as the result code
				setResult(RESULT_CANCELED);
				finish();
				return true;
			//default:
			//	return super.onOptionsItemSelected(item);

		}
		return false;
	}

	private CharSequence getSize(int size){
		CharSequence message;
		if(size >= 2){
			message = "Dear customer: You've ordered "+String.valueOf(size)+" books.";
		}
		else{
			message = "Dear customer: You've ordered one book.";
		}
		return message;
	}
	
}