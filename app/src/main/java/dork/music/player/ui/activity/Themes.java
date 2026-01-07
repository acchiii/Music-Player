package dork.music.player.ui.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.Gravity;
import android.graphics.Typeface;
import android.view.View.OnClickListener;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;

public class Themes extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

	getActionBar().hide();
	getWindow().setStatusBarColor(Color.TRANSPARENT);
	getWindow().getDecorView().setBackgroundColor(Color.BLACK);
	Util.setTheme(getApplicationContext(), this.getWindow(), getSharedPreferences("theme",0).getString("theme", "").toString());
	
		new AsyncTask<String,String,String>()
		{
			LinearLayout lin;
			@Override
			protected String doInBackground(String[] p1) {
				lin = new LinearLayout(Themes.this);
				Button img = new Button(Themes.this);
				TextView t = new TextView(Themes.this);
				View select = Util.selectTheme(getBaseContext());
				Animation a = AnimationUtils.makeInChildBottomAnimation(getBaseContext());
				select.setAnimation(a);
				Util.setLayoutParams(select, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
				lin.setGravity(Gravity.CENTER_VERTICAL);
				t.setText("SELECT THEME");
				t.setTextColor(Color.WHITE);
				t.setTextSize(25);
				t.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
				t.setGravity(Gravity.CENTER);
				img.setText("DONE");
				img.setTextColor(Color.WHITE);
				img.setTextSize(20);
				img.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);

				lin.setOrientation(1);
				lin.addView(t);
				lin.addView(select);
				lin.addView(img);
				img.setPadding(8,8,8,8);
				GradientDrawable gd = new GradientDrawable();
				gd.setCornerRadius(30);
				gd.setStroke(3, Color.WHITE);
				img.setBackground(gd);
				img.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View p1) {
							lin.setAnimation(AnimationUtils.makeOutAnimation(getApplicationContext(), true));
							onBackPressed();
						}
					});
				return null;
			}
			
		@Override
		protected void onPreExecute(){
			
		}
		@Override 
		protected void onPostExecute(String r){
			setContentView(lin);
		}
	
			
			
	}.execute();
        
    }

	@Override
	public void onBackPressed() {
		
		super.onBackPressed();
	}
   
}
