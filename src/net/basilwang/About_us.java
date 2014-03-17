package net.basilwang;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class About_us extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_us);
		Drawable dr = getResources().getDrawable(R.drawable.actionbar_bgc);
		getSupportActionBar().setBackgroundDrawable(dr);
		getSupportActionBar().setIcon(R.drawable.ic_menu_cancel_holo_light);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==android.R.id.home)
			About_us.this.finish();
		return super.onOptionsItemSelected(item);
	}
	

}
