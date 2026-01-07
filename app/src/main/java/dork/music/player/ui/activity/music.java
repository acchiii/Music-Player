package dork.music.player.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import dork.music.player.R;
import dork.music.player.common.activity.BaseActivity;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import android.graphics.drawable.ShapeDrawable;

public class music extends BaseActivity {
	
	boolean isShowed = false;
	SharedPreferences isShuffle;
	SharedPreferences.Editor isShuffleEditor;
	LLL async;
	View MusicList, MusicPlay;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(LoadingView());
		isShuffle = getSharedPreferences("isShuffle", 0);
		isShuffleEditor = isShuffle.edit();
		try{
		async = new LLL();
		async.execute();
		
		}catch(Exception err){
			Util.showMessage(getBaseContext(), err.toString());
			return;
		}
		
	}
	
	
	@Override
	public void onBackPressed()
{
	if(isShowed){
		
			MusicPlay.setAnimation(AnimationUtils.makeOutAnimation(getApplicationContext(), false));
			Util.ViewOnClick(MusicPlay,MusicList, getApplicationContext());
			isShowed = false;
		
	}else{
		finishAffinity();
	}

}
    public class LLL extends AsyncTask<String,String,String> {
		StringBuilder errors;
		MediaPlayer mp;
		ArrayList<String> lstr, paths,sizes,artists;
		ListView lv, searchList;
		int sizez;
		int mpadding = 16;
		int position = -1;
		TextView bottomTitle, title;
		LinearLayout MainBackground,music_main,lin, bar_bg, bottomNavLin, divider, divider2,barsearchbg, bardivider, top2btn, list_bg;
		ImageView img_bar, bottomImg, bottomPlayPause,bottomNext,search_ic, bar_setting,pausePlay;
		EditText edit_bar;
		MediaPlayer.OnCompletionListener compLis;
		SharedPreferences searchitems,lastplayedPrefs,theme;
		boolean refresher = false;
		SharedPreferences.Editor LPlayedEdit;
		int matchParent,wrapContent,screenHeight,screenWidth;
		TextView minprog,maxprog;
		SeekBar progress;
		Handler handler;
		TimerTask tm;
		Timer timer = new Timer();
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			getActionBar().hide();
			sizez = getResources().getDisplayMetrics().widthPixels/8;
			handler = new Handler();
			isShowed = false;
		}
		
		
		@Override
		protected String doInBackground(String[] p1) {
			wrapContent = LinearLayout.LayoutParams.WRAP_CONTENT;
			matchParent = LinearLayout.LayoutParams.MATCH_PARENT;
			screenHeight = getResources().getDisplayMetrics().heightPixels;
			screenWidth = getResources().getDisplayMetrics().widthPixels;
			float weight = 1.0f;
			errors = new StringBuilder();
			title = new TextView(music.this);
			pausePlay = new ImageView(music.this);
			Util.setLayoutParams(title, wrapContent,wrapContent, weight);
			Util.setLayoutParams(pausePlay, sizez, sizez);
			pausePlay.setScaleType(ImageView.ScaleType.FIT_CENTER);
			
			searchitems = getSharedPreferences("search", Activity.MODE_PRIVATE);
			lastplayedPrefs = getSharedPreferences("last_played", Activity.MODE_PRIVATE);
			theme = getSharedPreferences("theme", 0);
			loadTheme();
			LPlayedEdit = lastplayedPrefs.edit();
			searchList = new ListView(music.this);
			mp = new MediaPlayer();
			lin = new LinearLayout(music.this);
			bar_bg = new LinearLayout(music.this);
			img_bar = new ImageView(music.this);
			edit_bar = new EditText(music.this);
			search_ic = new ImageView(music.this);
			bottomNavLin = new LinearLayout(music.this);
			MainBackground = new LinearLayout(music.this);
			music_main = new LinearLayout(music.this);
			list_bg = new LinearLayout(music.this);
			top2btn = new LinearLayout(music.this);
			barsearchbg = new LinearLayout(music.this);
			bardivider = new LinearLayout(music.this);
			bottomImg = new ImageView(music.this);
			bottomPlayPause = new ImageView(music.this);
			bottomNext = new ImageView(music.this);
			divider = new LinearLayout(music.this);
			divider2 = new LinearLayout(music.this);
			bottomTitle = new TextView(music.this);
			bar_setting = new ImageView(music.this);
			minprog = new TextView(music.this);
			maxprog = new TextView(music.this);
			progress = new SeekBar(music.this);
			
			lv = new ListView(music.this);

			//Aref

			lstr = getIntent().getStringArrayListExtra("Labels");
			paths = getIntent().getStringArrayListExtra("Paths");
			sizes = getIntent().getStringArrayListExtra("Sizes");
			artists = getIntent().getStringArrayListExtra("Artists");
			
			lv.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.activity_list_item, lstr){
					@Override
					public View getView(int i, View v, ViewGroup vg){
						return getItemView(lstr.get(i),artists.get(i), i);
					}
				});
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
						p2.setAnimation(AnimationUtils.makeInAnimation(getBaseContext(), true));
						if(position==p3 && mp.isPlaying()) return;
						try{
							if(!play(p3,true)){
								errors.append("Failed to Play!\n");
							}
						}catch(Exception err){
							errors.append(err.getMessage()+"\n");
						}

					}
				});

			compLis = new MediaPlayer.OnCompletionListener(){

				@Override
				public void onCompletion(MediaPlayer p1) {
					int psize = paths.size();

					if(position < (psize-1)){
						position = position+1;
						play(position, true);

					}else
					if(position >= psize){
						position = 0;
						if (position < psize)play(position, true);
					}
		

				}
			};
	
			
			
			
			/////PROGRESS SEEKBAR
			progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
						if (fromUser) {
							mp.seekTo(progress);
							SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
							String prog = timeFormat.format(new Date(mp.getCurrentPosition()));
							//progress.setProgress(mp.getCurrentPosition());
							minprog.setText(prog);
						}
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
					}
				});

			// PREPARE VIEW
		
			divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 1));
			divider.setBackgroundColor(Color.WHITE);
			divider2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 1));
			divider2.setBackgroundColor(Color.WHITE);
			bardivider.setLayoutParams(new LinearLayout.LayoutParams(2, mpadding*2));
			bardivider.setBackgroundColor(Util.getColorBg());

			bar_bg.addView(img_bar);
			bar_bg.addView(barsearchbg);
			bar_bg.addView(bar_setting);
			bar_bg.setOrientation(0);
			bar_bg.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
			bar_bg.setGravity(Gravity.CENTER_VERTICAL);
			bar_bg.setPadding(0,0,mpadding,0);

			GradientDrawable gd = new GradientDrawable();
			gd.setCornerRadius(180);
			gd.setColor(Util.getColorBg());
			barsearchbg.setBackground(gd);
			barsearchbg.setOrientation(0);
			barsearchbg.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT,1.0f));
			barsearchbg.setGravity(Gravity.CENTER_VERTICAL);
			barsearchbg.addView(search_ic);
			barsearchbg.addView(bardivider);
			barsearchbg.addView(edit_bar);

			img_bar.setImageResource(R.drawable.list);
			int barsizeimg = sizez-(sizez/8);
			img_bar.setLayoutParams(new LinearLayout.LayoutParams(barsizeimg,barsizeimg));
			img_bar.setPadding(mpadding+8,mpadding+8,mpadding+8,mpadding+8);
			img_bar.setScaleType(ImageView.ScaleType.FIT_CENTER);

			bar_setting.setImageResource(R.drawable.ic_theme_24);
			bar_setting.setLayoutParams(new LinearLayout.LayoutParams(barsizeimg,barsizeimg));
			bar_setting.setPadding(mpadding+8,mpadding+8,mpadding+8,mpadding+8);
			bar_setting.setScaleType(ImageView.ScaleType.FIT_CENTER);
			bar_setting.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View p1) {

						new AsyncTask<String,String,String>(){
							Intent i;
							@Override
							protected String doInBackground(String[] p1) {
								i = new Intent();
								lin.setAnimation(AnimationUtils.makeOutAnimation(getApplicationContext(), true));
								i.setClass(music.this, Themes.class);
								return "";
							}
							@Override
							protected void onPostExecute(String u){
								startActivity(i);
							}

						}.execute();


					}
				});

			search_ic.setImageResource(R.drawable.search);
			search_ic.setLayoutParams(new LinearLayout.LayoutParams(barsizeimg-(barsizeimg/4), barsizeimg - (barsizeimg/4)));
			search_ic.setPadding(mpadding, mpadding/2, mpadding, mpadding/2);
			search_ic.setScaleType(ImageView.ScaleType.FIT_CENTER);


			edit_bar.setTextColor(Color.WHITE);
			edit_bar.setHint(" Search Music...");
			edit_bar.setTextSize(14);
			edit_bar.setSingleLine();
			edit_bar.setCursorVisible(false);
			edit_bar.setEnabled(true);
			edit_bar.setBackgroundColor(Color.TRANSPARENT);
			edit_bar.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
			edit_bar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
			edit_bar.setPadding(mpadding,mpadding,mpadding,mpadding);
			edit_bar.addTextChangedListener(new TextWatcher(){

					@Override
					public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
					}

					@Override
					public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {


					}

					@Override
					public void afterTextChanged(Editable p1) {
						if(p1.toString().length()>0){
							searchList.setVisibility(View.VISIBLE);
							lv.setVisibility(View.GONE);
						}else{
							searchList.setVisibility(View.GONE);
							lv.setVisibility(View.VISIBLE);
						}

						search(p1.toString(), new ArrayList<String>());
					}
				});

			top2btn.setOrientation(0);
			top2btn.setGravity(Gravity.CENTER);
			
			final boolean isShuffleBool = isShuffle.getBoolean("isShuffle", true);
			
			final LinearLayout l1 = (createImgbutton("Favorites", R.drawable.fav));
			final LinearLayout l2 = isShuffleBool ? (createImgbutton("Shuffle", R.drawable.shuffle)) : createImgbutton("Repeat" , R.drawable.repeat);
			//setLayoutParams(l1, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
			setLayoutParams(top2btn, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			top2btn.addView(l1);
			top2btn.addView(l2);
			l1.setPadding(0,0,mpadding,0);
			l2.setPadding(mpadding,0,0,0);
			
			
			
			
			///////      FAV         SHUFFLE          REPEAT
			l1.setOnClickListener(new View.OnClickListener(){

					@Override
					public void onClick(View p1) {
						
						Util.showMessage(getApplicationContext(), "THIS ITEM IS UNDER DEVELOPMENT");
						
						
					}
				});
			l2.setOnClickListener(new View.OnClickListener(){

					@Override
					public void onClick(View p1) {
						final boolean isShuffleBool2 = isShuffle.getBoolean("isShuffle", true);
						
						View p2 = isShuffleBool2 ? createImgbutton("Repeat",  R.drawable.repeat): createImgbutton("Shuffle" , R.drawable.shuffle);
						if(isShuffleBool2){
							isShuffleEditor.putBoolean("isShuffle", false);
						}else{
							isShuffleEditor.putBoolean("isShuffle", true);
						}
						isShuffleEditor.commit();
						isShuffleEditor.apply();
						
						
						
						GradientDrawable gd = new GradientDrawable();
						gd.setCornerRadius(15);
						gd.setColor(Util.getColorBg());
						gd.setStroke(mpadding, Color.TRANSPARENT);
						
						l2.setBackground(gd);
						p2.setBackgroundColor(Color.TRANSPARENT);
						l2.removeAllViews();
						l2.addView(p2, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
						
					}
				});
				
				
			
			top2btn.setPadding(mpadding,mpadding,mpadding,mpadding);

			lv.setDividerHeight(0);
			lv.setHeaderDividersEnabled(true);
			lv.setPadding(0,mpadding/2,0,0);
			lv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));

			searchList.setDividerHeight(0);
			searchList.setHeaderDividersEnabled(true);
			searchList.setPadding(0,mpadding/2,0,0);
			searchList.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
			searchList.setVisibility(View.GONE);
			searchList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
						int i = searchitems.getInt(p1.getItemAtPosition(p3).toString(), 0);
						int i2 = (paths.size()-1)-i;
						try{
						
							p2.setAnimation(AnimationUtils.makeInAnimation(getBaseContext(), true));
							if(i2==position && mp.isPlaying()) return;
							if(i2 < paths.size() && i2 >= 0){
								if(!play(i2,true)){
									errors.append("Failed to Play!\n");
								}
							}

						}catch(Exception err){
							errors.append(err.getMessage()+"\n");
						}
					}
				});

			bottomNavLin.setOrientation(0);
			bottomNavLin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			bottomNavLin.setGravity(Gravity.CENTER_VERTICAL);
			bottomNavLin.addView(bottomImg);
			bottomNavLin.addView(bottomTitle);
			bottomNavLin.addView(bottomPlayPause);
			bottomNavLin.addView(bottomNext);
			bottomNavLin.setPadding(0,0,0,mpadding);
			int bottomsizes = sizez;

			bottomImg.setLayoutParams(new LinearLayout.LayoutParams(bottomsizes,bottomsizes));
			bottomImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
			bottomImg.setPadding(mpadding,mpadding,mpadding,mpadding);
			bottomImg.setImageResource(R.drawable.music_ic);
			if(position>=0){
				bottomTitle.setText(lstr.get(position));
			}else{
				bottomTitle.setText("Click item to play!");
			}
			bottomTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f));
			bottomTitle.setTextColor(Color.WHITE);
			bottomTitle.setTextSize(12);
			bottomTitle.setTypeface(Typeface.MONOSPACE);
			bottomTitle.setSingleLine();
			bottomTitle.setGravity(Gravity.CENTER_VERTICAL);
			bottomTitle.setEllipsize(TextUtils.TruncateAt.END);
			
			bottomPlayPause.setImageResource(R.drawable.play);
			bottomPlayPause.setPadding(mpadding,mpadding,mpadding,mpadding);
			bottomPlayPause.setLayoutParams(new LinearLayout.LayoutParams(bottomsizes, bottomsizes));
			bottomPlayPause.setScaleType(ImageView.ScaleType.FIT_CENTER);
			
			pausePlay.setImageResource(R.drawable.play);
			pausePlay.setPadding(mpadding,mpadding,mpadding,mpadding);
			pausePlay.setLayoutParams(new LinearLayout.LayoutParams(bottomsizes, bottomsizes));
			pausePlay.setScaleType(ImageView.ScaleType.FIT_CENTER);
			
			
			bottomNext.setImageResource(R.drawable.ic_skip_next_round_white_32dp);
			bottomNext.setPadding(mpadding,mpadding,mpadding,mpadding);
			bottomNext.setLayoutParams(new LinearLayout.LayoutParams(bottomsizes, bottomsizes));
			bottomNext.setScaleType(ImageView.ScaleType.FIT_CENTER);
			
			

			list_bg.setOrientation(0);
			list_bg.setPadding(0,0,0,0);
			list_bg.addView(lv);
			list_bg.addView(searchList);
			setLayoutParams(list_bg, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);

			lin.addView(bar_bg);
			lin.addView(top2btn);
			lin.addView(divider2);
			lin.addView(list_bg);
			lin.addView(divider);
			lin.addView(bottomNavLin);

			lin.setOrientation(1);
			lin.setGravity(Gravity.LEFT);
			
			MainBackground.addView(lin);
			MainBackground.addView(music_main);
			MainBackground.setOrientation(1);
			bottomTitle.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View p1) {
						lin.setAnimation(AnimationUtils.makeOutAnimation(getApplicationContext(), false));
						Util.ViewOnClick(lin, music_main, getApplicationContext());
						isShowed = true;
					}
				});
			bottomNext.setOnClickListener(new View.OnClickListener(){

					@Override
					public void onClick(View p1) {
						next(position);
						return;
					}
				});
			bottomPlayPause.setOnClickListener(new View.OnClickListener(){

					@Override
					public void onClick(View p1) {
						if(mp.isPlaying()){
							pause();

						}else{
							start();

						}
					}
				});

			
			Util.setLayoutParams(music_main, screenWidth, screenHeight);
			return "";
		}

		@Override
		protected void onProgressUpdate(String[] values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			refresh();
			MusicList = lin;
			MusicPlay = music_main;
			music_main.addView( loadMusicView());
			setContentView(MainBackground, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));

			//update
			
			
		}
		
		public boolean play(int ps, boolean play){
			if(mp != null){mp.stop();}
			mp = MediaPlayer.create(getBaseContext(), Uri.fromFile(new File(paths.get(ps))));
			LPlayedEdit.putInt("last", ps).commit();
			LPlayedEdit.apply();
			if(mp == null) return false;
			position = ps;
			int duration = mp.getDuration();
			progress.setMax(duration);
			if (play)start();
			String ggg = lv.getAdapter().getItem(position).toString();
			bottomTitle.setText(ggg);
			title.setText(ggg);
			refresher = true;
			mp.setOnCompletionListener(compLis);
			
			
			SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
			String progress = timeFormat.format(new Date(mp.getCurrentPosition()));
			String dur = timeFormat.format(new Date(mp.getDuration()));
			maxprog.setText(dur);
			maxprog.setSingleLine();
			minprog.setSingleLine();
			//progress.setProgress(mp.getCurrentPosition());
			minprog.setText(progress);
			
			return true;
		}
		public void next(int pos){
			boolean isShuffled = isShuffle.getBoolean("isShuffle", true);
			try{
				
				if(isShuffled){

				int nxt = pos+1;
				int psize = paths.size();
				if(nxt < psize){
					play(nxt,true);
					return;
				}

				if(nxt >= psize){
					pos = 0;
					if( pos < psize){
						play(pos,true);
						return;
					}
				}
				
				}else{
					int nxt = new Random().nextInt(paths.size()-1);
					int psize = paths.size();
					if(nxt < psize){
						play(nxt,true);
						return;
					}

					if(nxt >= psize){
						pos = 0;
						if( pos < psize){
							play(pos,true);
							return;
						}
					}
					
				}
				
			}catch(Exception err){
				errors.append( err.getMessage()+"\n");
			}
		}
		public void prev(int pos){
			try{

				int nxt = pos-1;
				int psize = paths.size();
				if(nxt < psize){
					play(nxt,true);
					return;
				}

				if(nxt <= psize){
					pos = 0;
					if( pos < psize){
						play(pos,true);
						return;
					}
				}
			}catch(Exception err){
				errors.append( err.getMessage()+"\n");
			}
		}
		public void start(){
			try {
				mp.start();
				update();
				
				
				
				
				//////// UPDATE PROGRESS
				
				tm = new TimerTask() {
					@Override
					public void run() {
						runOnUiThread(new Runnable() {
								@Override
								public void run() {
									SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
									String prog = timeFormat.format(new Date(mp.getCurrentPosition()));
									minprog.setText(prog);
									progress.setProgress(mp.getCurrentPosition()+1);
								}
							});
					}
				};
			    timer.scheduleAtFixedRate(tm, (int)(1000), (int)(1000));
					
					
					
			
				
			} catch (IllegalStateException e) {
				errors.append(e.toString()+"\n");
			} catch (Exception e) {
				errors.append(e.toString()+"\n");
			}
		}
		public void pause(){
			try{
				if(mp.isPlaying())mp.pause();
				if(timer != null){
					timer.purge();
				}
				update();
			}catch(IllegalStateException|Exception err){
				errors.append( err.getMessage()+"\n");
			}
		}
		public void update(){

			if(mp.isPlaying()){
				bottomPlayPause.setImageResource(R.drawable.ic_pause_round_white_36dp);
				pausePlay.setImageResource(R.drawable.ic_pause_round_white_36dp);
			}else{
				pausePlay.setImageResource(R.drawable.ic_play_round_white_36dp);
				bottomPlayPause.setImageResource(R.drawable.ic_play_round_white_36dp);
			}
		//	pausePlay = bottomPlayPause;

		}
		public LinearLayout createImgbutton(String txt, int Res){
			LinearLayout linw = new LinearLayout(music.this);
			GradientDrawable gd = new GradientDrawable();
			gd.setCornerRadius(15);
			gd.setColor(Util.getColorBg());
			gd.setStroke(mpadding, Color.TRANSPARENT);
			linw.setBackground(gd);
			linw.setOrientation(0);
			linw.setGravity(Gravity.CENTER);
			TextView tvs = new TextView(music.this);
			tvs.setText(txt);
			tvs.setTextSize(20);
			tvs.setTextColor(Color.WHITE);
			tvs.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
			ImageView imgs = new ImageView(music.this);
			imgs.setImageResource(Res);
			imgs.setPadding(mpadding,mpadding,mpadding,mpadding);
			setLayoutParams(imgs, sizez-(sizez/8), sizez-(sizez/8));
			imgs.setScaleType(ImageView.ScaleType.FIT_CENTER);
			linw.addView(imgs);
			linw.addView(tvs);
			linw.setPadding(mpadding,mpadding,mpadding,mpadding);
			setLayoutParams(linw, (getResources().getDisplayMetrics().widthPixels/2)-(mpadding*7), (getResources().getDisplayMetrics().widthPixels/3)-(mpadding*8),1.0f);
			
			return linw;
		}
		public void setLayoutParams(View v, int l, int t){
			v.setLayoutParams(new LinearLayout.LayoutParams(l,t));
		}
		public void setLayoutParams(View v, int l, int t, float w){
			v.setLayoutParams(new LinearLayout.LayoutParams(l,t,w));
		}


		public View getItemView(String tilllwe, String atit, int i){
			ImageView img = new ImageView(music.this);
			ImageView favorite = new ImageView(music.this);
			LinearLayout lin = new LinearLayout(music.this);
			LinearLayout textbg = new LinearLayout(music.this);
			TextView tv = new TextView(music.this);
			TextView astisan = new TextView(music.this);

			img.setLayoutParams(new LinearLayout.LayoutParams(sizez,sizez));
			lin.setPadding(2,2,2,2);
			img.setImageBitmap(Util.round(getApplicationContext(), R.drawable.music_blue, 20));
			img.setPadding(mpadding,mpadding,mpadding,mpadding);
			tv.setTextSize(sizez/6);
			astisan.setTextSize(sizez/8);
			tv.setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
			astisan.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
			/**/tv.setText(tilllwe);
			tv.setEllipsize(TextUtils.TruncateAt.END);
			astisan.setEllipsize(TextUtils.TruncateAt.END);
			astisan.setSingleLine();
			/**/astisan.setText("by "+atit);
			img.setScaleType(ImageView.ScaleType.FIT_CENTER);

			tv.setPadding(10,0,30,0);
			astisan.setPadding(10, 5, 30, 0);
			tv.setGravity(Gravity.CENTER_VERTICAL);
			tv.setSingleLine();
			tv.setTextColor(Color.WHITE);
			tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
			lin.setGravity(Gravity.CENTER_VERTICAL);

			textbg.setGravity(Gravity.CENTER_HORIZONTAL);
			lin.setOrientation(0);
			textbg.setOrientation(1);
			lin.addView(img);
			lin.addView(textbg);
			textbg.addView(tv);
			textbg.addView(astisan);

			lin.setAnimation(AnimationUtils.makeInChildBottomAnimation(getBaseContext()));

			if(position == i)	
			{
				lin.setBackgroundColor(Color.parseColor(new Util().defColor));
			}else{
				lin.setBackgroundColor(Color.TRANSPARENT);
			}
			return lin;
		}
		public void search(final String str, final ArrayList<String> stry){

		 new AsyncTask<String, String, String>(){
				ListView newList = searchList;
			
				@Override
				protected String doInBackground(String[] p1) {
				
					for(int i =0; i<lstr.size(); i++){
						String hhh = lstr.get(i);
						if(hhh.replace(" ","").toLowerCase().contains(str.toLowerCase().replace(" ",""))){
							stry.add(hhh);
						}
					}
					return str;
				}
				@Override 
				protected void onProgressUpdate(String[] str){

				}
				@Override
				protected void onPostExecute(String str){
		
					searchList.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.activity_list_item, stry){
							@Override
							public View getView(int iu, View V, ViewGroup vg){
								int i = searchitems.getInt(this.getItem(iu).toString(), 0);
								int i2 = (paths.size()-1)-i;

								return getItemView(getItem(iu).toString(), "<unknown>" , i2);
							}
						});
				}

			}.execute();
		

		}
		public void refresh(){
			try{

				if(lastplayedPrefs == null) return;
				int ion = lastplayedPrefs.getInt("last",0);
				if(ion < paths.size()){
					play(ion, false);
				}

			}catch(Exception|NullPointerException err){
				if(paths.size() > 0){
					play(0, false);
				}
				errors.append(err.toString()+"\n");
			}
		}
		public void loadTheme(){
			Util.setTheme(getApplicationContext(), music.this.getWindow(), theme.getString("theme", "").toString());
		}
		public View loadMusicView(){
			
			LinearLayout music_bg = new LinearLayout(music.this);
			LinearLayout music_top_bg = new LinearLayout(music.this);
			LinearLayout music_artist_icon_bg = new LinearLayout(music.this);
			ImageView artist_icon = new ImageView(music.this);
		//	LinearLayout music_additional_button_bg = new LinearLayout(music.this);
			LinearLayout music_progress_bg = new LinearLayout(music.this);
			LinearLayout music_controller_bg = new LinearLayout(music.this);
			
			ImageView slider = new ImageView(music.this);
			
			ImageView options = new ImageView(music.this);
			ImageView next = new ImageView(music.this);
			ImageView prev = new ImageView(music.this);
		
			int matchParent = LinearLayout.LayoutParams.MATCH_PARENT;
			int wrapContent = LinearLayout.LayoutParams.WRAP_CONTENT;
			float weight = 1.0f;
			int padding = 16;

			int ee = sizez/10;
			int sizz = (sizez/2)-(sizez/6);
			int bitsizz = sizz+(sizez/3);
			
			Util.setLayoutParams(slider, sizz+ee, sizz+ee);
			slider.setScaleType(ImageView.ScaleType.FIT_CENTER);
			Util.setLayoutParams(options, sizz+ee, sizz+ee);
			options.setScaleType(ImageView.ScaleType.FIT_CENTER);
			options.setOnClickListener(new View.OnClickListener(){

					@Override
					public void onClick(View p1) {
						
						new AsyncTask<String,String,String>(){
							Intent intent;
							@Override
							protected String doInBackground(String[] p1) {
								File file = new File(paths.get(position));
								intent = new Intent(Intent.ACTION_SEND);
								intent.setType("audio/mp3");
								Uri fileUri = FileProvider.getUriForFile(getApplicationContext(), "dork.music.player.ui.activity.fileprovider", file);
								intent.putExtra(Intent.EXTRA_STREAM, fileUri);
								intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
								startActivity(Intent.createChooser(intent, "Share Music File"));
								return null;
							}
							@Override
							protected void onPostExecute(String str){
								//music.this.startActivity(intent);
							}
						}.execute();
						
					}
				});
			Util.setLayoutParams(next, bitsizz+(bitsizz*2), bitsizz);
			next.setScaleType(ImageView.ScaleType.FIT_CENTER);
			Util.setLayoutParams(pausePlay, bitsizz*2, bitsizz*2);
			pausePlay.setScaleType(ImageView.ScaleType.FIT_CENTER);
			Util.setLayoutParams(prev, bitsizz+(bitsizz*2), bitsizz);
			prev.setScaleType(ImageView.ScaleType.FIT_CENTER);
			Util.setLayoutParams(progress, matchParent,wrapContent,weight);
			
			Util.setUpText(title,14);
			title.setPadding(padding, 0, padding, 0);
			Util.setUpText(minprog,14);
			//minprog.setText("00:00");
			Util.setUpText(maxprog,14);
			//maxprog.setText("02:39");
			slider.setImageResource(R.drawable.down);
			options.setImageResource(R.drawable.ic_share_outline_24);
			next.setImageResource(R.drawable.ic_skip_next_round_white_32dp);
			prev.setImageResource(R.drawable.ic_skip_previous_round_white_32dp);
			
			prev.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View p1) {
						prev(position);
					}
				});
			
			next.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View p1) {
						next(position);
					}
				});
			
			pausePlay.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View p1)
					{
						if(mp.isPlaying()){
							pause();

						}else{
							start();

						}
						}
					});
			slider.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View p1) {
						isShowed = false;
						music_main.setAnimation(AnimationUtils.makeOutAnimation(getApplicationContext(), false));
						Util.ViewOnClick(music_main, lin, getApplicationContext());
					}
				});
			
			Util.setLayoutParams(music_bg,matchParent,matchParent,1.0f);
			music_bg.setOrientation(1);
			music_bg.setGravity(Gravity.CENTER_VERTICAL);
			
			
			Util.setLayoutParams(music_top_bg, matchParent,wrapContent);
			music_top_bg.setOrientation(0);
			music_top_bg.setGravity(Gravity.CENTER_HORIZONTAL);
			
			
			Util.setLayoutParams(music_artist_icon_bg, matchParent,wrapContent,weight);
			music_artist_icon_bg.setGravity(Gravity.CENTER);
			music_artist_icon_bg.setPadding(padding,padding,padding,padding);
			
			artist_icon.setImageBitmap(Util.round(getApplicationContext(), R.drawable.music_icon, 5.0f));
			artist_icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
			Util.setLayoutParams(artist_icon, matchParent,matchParent, weight);
			
			/*
			Util.setLayoutParams(music_additional_button_bg, matchParent,wrapContent);
			music_additional_button_bg.setOrientation(0);
			music_additional_button_bg.setGravity(Gravity.CENTER_HORIZONTAL);
			*/
			
			Util.setLayoutParams(music_progress_bg, matchParent,wrapContent);
			music_progress_bg.setOrientation(0);
			music_progress_bg.setGravity(Gravity.CENTER_HORIZONTAL);
		
			
			Util.setLayoutParams(music_controller_bg, matchParent,wrapContent);
			music_controller_bg.setOrientation(0);
			music_controller_bg.setGravity(Gravity.CENTER);
			
			title.setEllipsize(TextUtils.TruncateAt.END);
			title.setSingleLine();
			title.setPadding(padding, 0,padding,0);
			music_top_bg.addView(slider);
			music_top_bg.addView(title);
			music_top_bg.addView(options);
			
			music_artist_icon_bg.addView(artist_icon);
			
			music_progress_bg.addView(minprog);
			music_progress_bg.addView(progress);
			music_progress_bg.addView(maxprog);
			
			music_controller_bg.addView(prev);
			music_controller_bg.addView(pausePlay);
			music_controller_bg.addView(next);
			
			music_bg.addView(music_top_bg);
			music_bg.addView(music_artist_icon_bg);
			music_bg.addView(music_progress_bg);
			music_progress_bg.setGravity(Gravity.CENTER);
			music_bg.addView(music_controller_bg);
			
			music_bg.setPadding(padding*2,padding*3,padding*2,padding*4);
			
			music_bg.setLayoutParams(new ViewGroup.LayoutParams(screenWidth,screenHeight));
			return music_bg;
		}
		public void PlayView(){
			Util.ViewOnClick(lin, music_main, getApplicationContext());
			music_main.setAnimation(AnimationUtils.makeInChildBottomAnimation(getBaseContext()));
			isShowed = true;
		}
		
		
		
		
	}
	public View LoadingView(){
		LinearLayout view = new LinearLayout(this);
		RotatingImageView vr = new RotatingImageView(this);
		
		int w = getResources().getDisplayMetrics().widthPixels;
		int h = getResources().getDisplayMetrics().heightPixels;
		vr.setImageResource(R.drawable.loading);
		vr.setScaleType(ImageView.ScaleType.FIT_CENTER);
		
		Util.setLayoutParams(view,w,h,1.0f);
		Util.setLayoutParams(vr, w/8, w/8);
		view.setGravity(Gravity.CENTER);
		view.addView(vr);
		return view;
	}
    public static class RotatingImageView extends ImageView{
		Context c;
		int i = 0;
		public RotatingImageView(Context c){
			super(c);
			this.c = c;
		}

		@Override
		protected void onDraw(Canvas canvas) {
			
			i+=4;
			if(i>=360){
				i=0;
			}
		//	canvas.rotate(i);
			canvas.rotate(i, this.getMeasuredWidth(), this.getMeasuredHeight());
			super.onDraw(canvas);
		}
		
	
	
	}

	@Override
	protected void onResume() {
		Util.setTheme(getApplicationContext(), music.this.getWindow(), getSharedPreferences("theme",0).getString("theme", "").toString());
		super.onResume();
	}
	
    
}
