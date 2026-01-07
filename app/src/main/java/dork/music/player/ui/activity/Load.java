package dork.music.player.ui.activity;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.widget.SimpleCursorAdapter.CursorToStringConverter;
import android.content.CursorLoader;
import android.content.Intent;
import java.util.ArrayList;
import android.media.MediaMetadata;
import android.widget.ProgressBar;
import android.widget.LinearLayout;
import android.view.Gravity;
import android.widget.ImageView;
import dork.music.player.R;
import android.graphics.Typeface;
import android.graphics.Color;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.text.TextUtils;
import android.view.animation.Interpolator;
import android.graphics.Matrix;
import android.content.SharedPreferences;
import android.webkit.WebView;

public class Load extends AsyncTask<String, String, String>{ 
	Context context;
	ContentResolver resolver;
	ArrayList<String> Labels,Paths,size,artist;
	MainActivity main;
	SharedPreferences Musix, search;
	SharedPreferences.Editor editor;
	
    public Load(final Context contexts, final ContentResolver resolvers, final MainActivity main){
		
		this.main = main;
		this.context = contexts;
		this.resolver = resolvers;
		Musix = contexts.getSharedPreferences("Musix", Activity.MODE_PRIVATE);
		search = contexts.getSharedPreferences("search",Activity.MODE_PRIVATE);
		editor = search.edit();
		Labels = new ArrayList<>();
	    Paths = new ArrayList<>();
		size = new ArrayList<>();
		artist = new ArrayList<>();
		
	
	}
		@Override
		protected void onPreExecute(){
			
			super.onPreExecute();
		}
			@Override
			protected String doInBackground(String[] p1) {
				Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,null);
				int i = 0;
				if(cursor.getCount() >= 0){
					editor.clear().commit();
					editor.apply();
					while( (!cursor.isLast())  ){
						
						if(cursor.moveToNext()){
							try{
							int title_ind = cursor.getColumnIndex("title");// _size, title
							int size_ind = cursor.getColumnIndex("_size");
							int path_ind = cursor.getColumnIndex("_data");//relative_path, _data 
							int artist_ind = cursor.getColumnIndex("artist");
							
					
							Labels.add(0,new String(cursor.getString(title_ind)));
							Paths.add(0,new String(cursor.getString(path_ind)));
							size.add(0,new String(cursor.getString(size_ind)));
							artist.add(0,new String(cursor.getString(artist_ind)));
							
							editor.putInt(new String(cursor.getString(title_ind)), i).commit();
							editor.apply();
							
							}catch(Exception err){
								continue;
							}
							i++;
						}
						
						
					}
					
				
					
				}else{
					Labels.add("No Music Found!");
				}
				return "";
			
			}
			@Override
			protected void onProgressUpdate(String[] str){
				
				
				
	}
			@Override
			protected void onPostExecute(String res){
				
				main.move(Labels,Paths,size,artist);
			}
			
	}
