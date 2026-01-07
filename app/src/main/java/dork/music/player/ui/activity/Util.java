package dork.music.player.ui.activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.content.res.XmlResourceParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlPullParser;
import android.content.res.AssetFileDescriptor;
import java.io.FilenameFilter;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.support.v4.view.ViewPager;
import android.support.v4.view.PagerAdapter;
import dork.music.player.R;

public class Util {
    public static String  defColor = "#00bcd4";
	static String defBgColor = "#5fffffff";
    public static void showMessage(Context context,Object obj){
		TextView text = new TextView(context);
		text.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
		text.setTextSize(12);
		text.setTextColor(Color.WHITE);
		GradientDrawable gd = new GradientDrawable();
		gd.setCornerRadius(25);
		gd.setColor(Color.parseColor(defColor));
		text.setText(new String(obj.toString()));
		text.setBackground(gd);
		text.setGravity(Gravity.CENTER);
		text.setPadding(8,8,8,8);
		Toast toast = new Toast(context);
		toast.setView(text);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
		toast.show();
	}
	public static int getColorBg(){
		return Color.parseColor(defBgColor);
	}
	public static int getColorDefault(){
		return Color.parseColor(defColor);
	}
	public static void setTheme(Context con, Window win, String path){
		try {
			
			win.setStatusBarColor(Color.TRANSPARENT);
		    InputStream inp = con.getAssets().open(path); 
			Bitmap blurred =  Util.blurImage(con, BitmapFactory.decodeStream(inp), 20);
			Drawable dr = new BitmapDrawable(blurred);
			win.getDecorView().setBackground(dr);
		} catch (Exception e) {
		
		}
	}
	public static View selectTheme(final Context con){
		
		final SharedPreferences.Editor editor = con.getSharedPreferences("theme",0).edit();
		LinearLayout lin = new LinearLayout(con);
		LinearLayout adder = new LinearLayout(con);
		HorizontalScrollView sv = new HorizontalScrollView(con);
		ViewPager vpager = new ViewPager(con);
		
	
		final String[] ls;
		final ArrayList<String> lpath = new ArrayList<>();
		final ArrayList<InputStream> ipath = new ArrayList<>();
		
		lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,1.0f));
		lin.setOrientation(1);
		GradientDrawable gd = new GradientDrawable();
		gd.setStroke(10, Color.TRANSPARENT);
		gd.setCornerRadius(30);
		lin.setBackground(gd);
		lin.setGravity(Gravity.CENTER);
		
		//ArrayList<Bitmap> hhhh = new ArrayList<>();
		try {
			String gg = "themes/";
			ls = con.getAssets().list(gg);
			 
			for( int i=0; i<ls.length; i++){
				String st = gg+ls[i];
				lpath.add(gg+ls[i]);
				final int ydd = i;
				InputStream ip = con.getAssets().open(gg+ls[i]);
				ipath.add(ip);
				
				//hhhh.add(round(ip, 20f));
				
				final View v = (Util.getView(con, ip, st, i));
				
				final Animation anim = AnimationUtils.makeInAnimation(con, true);
				v.setAnimation(anim);
				adder.addView(v);
				v.setOnClickListener(new View.OnClickListener(){

						@Override
						public void onClick(View p1) {
							
							final String path = lpath.get(v.getId()).toString();
							editor.putString("theme", path).commit();
							editor.apply();
							p1.setAnimation(anim);
							
						}
					});
				
				
			}
			
			
			
			
		} catch (IOException|Exception e) {
			e.printStackTrace();
			//Util.showMessage(con,e.toString());
		}
		
		adder.setOrientation(0);
		adder.setGravity(Gravity.CENTER_VERTICAL);
		adder.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,1.0f));
		//sv.canScrollHorizontally(ScrollView.SCROLL_AXIS_HORIZONTAL);
		
		//ViewPager pg = new ViewPager(con);
		//pg.setAdapter(new MusicPager(con, hhhh));
		////////////ADDDERRRR DAPAT
		sv.addView(adder);
		//sv.setScrollBarStyle(ScrollView.SCROLL_AXIS_HORIZONTAL);
		sv.setScrollBarSize(0);
		
		lin.setOrientation(0);
		lin.addView(sv);
		Animation anim = AnimationUtils.makeInAnimation(con, true);
		lin.setAnimation(anim);
		
		return lin;
	}
	private static LinearLayout getView(Context c, InputStream d, String label, int i){
		LinearLayout lin = new LinearLayout(c);
		RoundedCornerImageView img = new RoundedCornerImageView(c, "Theme "+(i+1));
		
		GradientDrawable gd = new GradientDrawable();
		gd.setStroke(5, Color.TRANSPARENT);
		gd.setCornerRadius(30);
		//gd.setColor(Util.getColorBg());
		lin.setBackground(gd);
		lin.setGravity(Gravity.CENTER);
		//lin.setPadding(16,16,16,16);
		
		
		img.setImageBitmap(round(d, 30.0f));
		img.setScaleType(ImageView.ScaleType.FIT_CENTER);
		int w = c.getResources().getDisplayMetrics().widthPixels;
		int h = c.getResources().getDisplayMetrics().heightPixels;
		int m = w-(w/10);
		setLayoutParams( lin, LinearLayout.LayoutParams.WRAP_CONTENT, h-m);
		setLayoutParams( img, LinearLayout.LayoutParams.WRAP_CONTENT, h-m);
		
		lin.addView(img);
		lin.setId(i);
		lin.setPadding(8,0,8,0);
		Animation anim = AnimationUtils.makeInChildBottomAnimation(c);
		lin.setAnimation(anim);
		return lin;
	}
	public static void setLayoutParams(View v, int l, int t){
		v.setLayoutParams(new LinearLayout.LayoutParams(l,t));
	}
	public static void setLayoutParams(View v, int l, int t, float w){
		v.setLayoutParams(new LinearLayout.LayoutParams(l,t,w));
	}
    private static class Adapter2 extends BaseAdapter{
		ArrayList<String> paths;
		ArrayList<InputStream> stream;
		Context c;
		public Adapter2(Context c,ArrayList<String> path, ArrayList<InputStream> stream){
			this.paths = path;
			this.stream = stream;
			this.c = c;
		}
		@Override
		public int getCount() {
			return paths.size();
		}

		@Override
		public Object getItem(int p1) {
			return paths.get(p1);
		}

		@Override
		public long getItemId(int p1) {
			return p1;
		}

		@Override
		public View getView(int p1, View p2, ViewGroup p3) {
			return Util.getView(c, stream.get(p1), paths.get(p1),p1);
		}

	
	}
	public static class RoundedCornerImageView extends ImageView {

		//private final static float CORNER_RADIUS = 30.0f;
		Paint paint;
		String Title;
		Context context;
		public RoundedCornerImageView(Context context, String Title) {
			super(context);
			this.context = context;
			this.Title = Title;
			paint = new Paint();
			paint.setTextSize(30);
			paint.setColor(Color.WHITE);
			paint.setTypeface(Typeface.SANS_SERIF);
			paint.setFakeBoldText(true);
		}

		public RoundedCornerImageView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		public RoundedCornerImageView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			int h = this.getMeasuredHeight();
			int w = this.getMeasuredWidth();
			canvas.drawText(Title, (w/2)-(w/7),h-(paint.getTextSize()*4), paint);
		}
	}
	public static Bitmap round(InputStream in, float round){
		
		Bitmap src = getBitmap(in);
		Bitmap bitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas2 = new Canvas(bitmap);

		final int color = Color.WHITE ;//0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, src.getWidth(), src.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = round;

		paint.setAntiAlias(true);
		canvas2.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas2.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas2.drawBitmap(src, rect, rect, paint);

		return bitmap;
		
	}
	public static Bitmap round(Context c, int id, float round){
		Bitmap src = BitmapFactory.decodeResource(c.getResources(), id);
		Bitmap bitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas2 = new Canvas(bitmap);

		final int color = Color.WHITE ;//0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, src.getWidth(), src.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = round;

		paint.setAntiAlias(true);
		canvas2.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas2.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas2.drawBitmap(src, rect, rect, paint);

		return bitmap;

	}
	public static void ViewOnClick(View v,View v1,Context context){
		v.setVisibility(View.GONE);
		v1.setVisibility(View.VISIBLE);
		v1.setAnimation(AnimationUtils.makeInChildBottomAnimation(context));
	}
	public static void setUpText(TextView tv, int size){
		tv.setTextSize(size);
		tv.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
		tv.setTextColor(Color.WHITE);
		tv.setGravity(Gravity.CENTER_HORIZONTAL);
	}
	public static Bitmap blurImage(Context con,Bitmap bmp, int blur){

		Bitmap bitmap = bmp;

		int b = (int) blur;

		Bitmap blurred = blurRenderScript(con,bitmap, b);
		return blurred;

	}



	public static Bitmap blurRenderScript(Context context,Bitmap smallBitmap, int radius) {
        try {
			smallBitmap = RGB565toARGB888(smallBitmap);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Bitmap bitmap = Bitmap.createBitmap(
			smallBitmap.getWidth(), smallBitmap.getHeight(),
			Bitmap.Config.ARGB_8888);


		android.renderscript.RenderScript renderScript = android.renderscript.RenderScript.create(context);

		android.renderscript.Allocation blurInput = android.renderscript.Allocation.createFromBitmap(renderScript, smallBitmap);
		android.renderscript.Allocation blurOutput = android.renderscript.Allocation.createFromBitmap(renderScript, bitmap);

		android.renderscript.ScriptIntrinsicBlur blur = android.renderscript.ScriptIntrinsicBlur.create(renderScript,
																										android.renderscript.Element.U8_4(renderScript));
		blur.setInput(blurInput);
		blur.setRadius(radius); // radius must be 0 < r <= 25
		blur.forEach(blurOutput);

		blurOutput.copyTo(bitmap);
		renderScript.destroy();

		return bitmap;

	}

	private static Bitmap RGB565toARGB888(Bitmap img) throws Exception {
		int numPixels = img.getWidth() * img.getHeight();
		int[] pixels = new int[numPixels];

		//Get JPEG pixels.  Each int is the color values for one pixel.
		img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

		//Create a Bitmap of the appropriate format.
		Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

		//Set RGB pixels.
		result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
		return result;
}

	private static Bitmap getBitmap(InputStream input) {
		
		return BitmapFactory.decodeStream(input);
	}
	public static class MusicPager extends PagerAdapter {
		ArrayList<Bitmap> bmp;
		Context c;
		public MusicPager(Context c,ArrayList<Bitmap> bmp){
			this.bmp = bmp;
			this.c = c;
		}
		@Override
		public int getCount() {
			return bmp.size();
		}

		@Override
		public boolean isViewFromObject(View _view, Object _object) {
			return _view == _object;
		}

		@Override
		public void destroyItem(ViewGroup _container, int _position, Object _object) {
			_container.removeView((View) _object);
		}

		@Override
		public int getItemPosition(Object _object) {
			return super.getItemPosition(_object);
		}

		@Override
		public CharSequence getPageTitle(int pos) {
			// use the activitiy event (onTabLayoutNewTabAdded) in order to use this method
			return "page " + String.valueOf(pos);
		}

		@Override
		public  Object instantiateItem(ViewGroup _container,  final int _position) {
			
			ImageView img = new ImageView(c);
			img.setImageBitmap(Util.round(c, R.drawable.music_icon,20.0f));

			_container.addView(img);
			return img;
		}
		
	}
	
}
