package dork.music.player.ui.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import dork.music.player.R;
import dork.music.player.common.activity.BaseActivity;
import dork.music.player.ui.activity.music;
import java.util.ArrayList;


public class MainActivity extends BaseActivity { 

Load ld;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
		getActionBar().hide();
int x=0;
x + 1 = x;
		//System.out.println("Starting...");
		WebView web = new WebView(getApplicationContext());
		web.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
		web.loadUrl("file:///android_asset/loader/musload.html");
		web.setBackgroundColor(Util.getColorDefault());
		web.setClickable(false);
        setContentView(web);
		web.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageFinished(WebView web, String str){
				Load();
			}
		});
    }
public void move(final ArrayList<String> Labels, final ArrayList<String> Paths, final ArrayList<String> size, final ArrayList<String> artist){
	final Intent intent = new Intent();
	
	new AsyncTask<String, String,String>(){

		@Override
		protected String doInBackground(String[] p1) {
			intent.setClass(MainActivity.this, music.class);
			intent.putStringArrayListExtra("Labels", Labels);
			intent.putStringArrayListExtra("Paths", Paths);
			intent.putStringArrayListExtra("Sizes", size);
			intent.putStringArrayListExtra("Artists", artist);
			return "Done";
		}
		@Override
		protected void onPostExecute(String str){
			try {
				new Handler().postDelayed(new Runnable(){
						@Override
						public void run() {
							startActivity(intent);
						}
					}, 3000);

			} catch (Exception e) {
				Util.showMessage(getApplicationContext(), e.toString());
			}
			
		}
	}.execute();
	
	
	
}
public void Load(){
	new AsyncTask<String,String, Boolean>(){

		@Override
		protected Boolean doInBackground(String[] p1) {

			try{
				ld = new Load(MainActivity.this.getApplicationContext(), MainActivity.this.getContentResolver(),MainActivity.this);
				ld.execute();
				return true;
			}catch(Exception|IllegalArgumentException err){
				return false;
			}
		}
		@Override
		protected void onPostExecute(Boolean bool){
			if(!bool){
				setContentView(new LinearLayout(MainActivity.this));
				Util.showMessage(MainActivity.this.getApplicationContext(), "Error");
			}
		}

	}.execute();
	
}

}
