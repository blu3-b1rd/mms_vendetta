package com.mms.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.mms.util.MMSUtils;
import com.mms.vendetta.R;
import com.mms.app.MMSApplication;
import com.mms.app.MMSPreferences;
import com.mms.model.MMSGalleryItem;
import com.mms.model.MMSList;
import com.mms.model.MMSModel;
import com.mms.model.MMSResponse;
import com.mms.request.GetArtistGallery;
import com.mms.request.MMSAsyncRequest;
import com.mms.request.MMSAsyncRequest.OnMMSRequestFinishedListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

@SuppressWarnings("deprecation")
public class GalleryActivity extends ActionBarActivity
	implements OnMMSRequestFinishedListener, OnItemSelectedListener,
	ImageLoadingListener {
	
	private GalleryAdapter adapter;
	private DisplayImageOptions options;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_gallery);
		ActionBar actionbar = this.getSupportActionBar();
		actionbar.setTitle(R.string.gallery_title);
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setHomeButtonEnabled(true);
		
		this.options = new DisplayImageOptions.Builder()
			.displayer(new FadeInBitmapDisplayer(300))
			.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
			.cacheOnDisc()
			.build();

        ((MMSApplication) this.getApplication()).getTracker(MMSApplication.TrackerName.APP_TRACKER)
                .send(new HitBuilders.AppViewBuilder().build());
		
		this.loadGallery();
	}
	
	private void loadGallery(){
		this.findViewById(R.id.img_gallery_selection).setVisibility(View.GONE);
		this.findViewById(R.id.gallery).setVisibility(View.GONE);
		this.findViewById(R.id.progress).setVisibility(View.VISIBLE);
		String cookie = MMSPreferences.loadString(MMSPreferences.COOKIE, null);
		new MMSAsyncRequest(this).execute(new GetArtistGallery(cookie));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.refresh, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			this.finish();
			return true;
		case R.id.action_refresh:
			this.loadGallery();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onMMSRequestFinished(MMSResponse response) {
		this.findViewById(R.id.progress).setVisibility(View.GONE);
		this.findViewById(R.id.img_gallery_selection).setVisibility(View.GONE);
		this.findViewById(R.id.gallery).setVisibility(View.VISIBLE);
		try {
			switch(response.getStatus()){
			case MMS_SUCCESS:
				this.onSuccessfulResponse(response);
				break;
			case MMS_WRONG_COOKIE:
				this.onWrongCookie();
				break;
			default:
				this.onErrorResponse(response);
				break;
			}
		} catch(Exception e) {
			e.printStackTrace();
			this.onRequestFailed();
		}
	}
	
	protected void onSuccessfulResponse(MMSResponse response){
		MMSList content = (MMSList) response.getContent();
		if(!content.isEmpty()){
			this.displayGallery(content);
			for(MMSModel item : content){
				this.imageLoader.loadImage(
						MMSUtils.scaledImageUrl(((MMSGalleryItem) item).getUrl(), 800),
						this.options, null);
			}
		}
	}
	
	private void displayGallery(MMSList gallery){
		this.adapter = new GalleryAdapter(this, gallery);
		((Gallery) this.findViewById(R.id.gallery))
			.setAdapter(this.adapter);
		((Gallery) this.findViewById(R.id.gallery))
			.setOnItemSelectedListener(this);
		((Gallery) this.findViewById(R.id.gallery))
			.setSelection(gallery.size() / 2);
	}
	
	@Override
	public void onItemSelected(AdapterView<?> adapter, View view, int position,
			long id) {
		MMSGalleryItem item = (MMSGalleryItem) this.adapter.getItem(position);
		this.imageLoader.displayImage(
				MMSUtils.scaledImageUrl(item.getUrl(), 800),
				(ImageView) this.findViewById(R.id.img_gallery_selection),
				this.options, this);
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {}
	
	@Override
	public void onLoadingCancelled(String imageUri, View view) {}
	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		this.findViewById(R.id.progress).setVisibility(View.GONE);
		this.findViewById(R.id.img_gallery_selection).setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onLoadingFailed(String imageUri, View view,
			FailReason failReason) {}
	
	@Override
	public void onLoadingStarted(String imageUri, View view) {
		this.findViewById(R.id.img_gallery_selection).setVisibility(View.GONE);
		this.findViewById(R.id.progress).setVisibility(View.VISIBLE);
	}
	
	protected void onRequestFailed(){
		Toast.makeText(this, R.string.error_connection_failed,
				Toast.LENGTH_SHORT).show();
	}
	
	protected void onWrongCookie(){
		Toast.makeText(this, R.string.error_wrong_cookie,
				Toast.LENGTH_SHORT).show();
		((MMSApplication) this.getApplication()).logout(this);
	}
	
	protected void onErrorResponse(MMSResponse response) {
		Toast.makeText(this, response.getMessage(),
				Toast.LENGTH_SHORT).show();
	}
	
	private class GalleryAdapter extends ArrayAdapter<MMSModel> {
		
		private MMSList gallery;
		private DisplayImageOptions options;
		private ImageLoader imageLoader = ImageLoader.getInstance();
		
		public GalleryAdapter(Context context, MMSList gallery){
			super(context, R.layout.gallery_item, gallery);
			this.gallery = gallery;
			
			this.options = new DisplayImageOptions.Builder()
				.displayer(new FadeInBitmapDisplayer(300))
				.showStubImage(R.drawable.img_default_gallery)
				.showImageForEmptyUri(R.drawable.img_default_gallery)
				.showImageOnFail(R.drawable.img_default_gallery)
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.cacheOnDisc()
				.build();
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = LayoutInflater.from(this.getContext()).inflate(
					R.layout.gallery_item, parent, false);
			
			ImageView galleryItem = (ImageView) view
					.findViewById(R.id.img_gallery_item);
			
			MMSGalleryItem item = (MMSGalleryItem) this.getItem(position);
			this.imageLoader.displayImage(
					MMSUtils.scaledImageUrl(item.getUrl(), 200),
					galleryItem, this.options);
			
			return view;
		}
		
		@Override
		public int getCount() {
			return this.gallery.size();
		}
		
		@Override
		public MMSModel getItem(int position) {
			return this.gallery.get(position);
		}
		
	}
	
}
