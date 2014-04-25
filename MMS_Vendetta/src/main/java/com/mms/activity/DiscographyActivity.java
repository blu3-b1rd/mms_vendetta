package com.mms.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.mms.app.MMSApplication;
import com.mms.app.MMSPreferences;
import com.mms.fragment.AlbumFragment;
import com.mms.model.MMSAlbum;
import com.mms.model.MMSList;
import com.mms.model.MMSModel;
import com.mms.model.MMSResponse;
import com.mms.request.GetDiscographyRequest;
import com.mms.request.MMSAsyncRequest;
import com.mms.request.MMSAsyncRequest.OnMMSRequestFinishedListener;
import com.mms.util.MMSUtils;
import com.mms.vendetta.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class DiscographyActivity extends ActionBarActivity
	implements OnMMSRequestFinishedListener {

	//private static final String SAVED_ALBUMS = "saved_albums";
	private static final String TAG_ALBUM_FRAGMENT = "album_fragment";
	
	private MMSList albums;
	private ListView discographyList;
	private TextView emptyLabel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_discography);
		this.configure();
		
		//if(savedInstanceState == null){
			String cookie = MMSPreferences.loadString(MMSPreferences.COOKIE, null);
			new MMSAsyncRequest(this).execute(
					new GetDiscographyRequest(cookie));
		//} else {
			//this.displayAlbums((MMSList) savedInstanceState
				//	.getSerializable(SAVED_ALBUMS));
		//}

        ((MMSApplication) this.getApplication()).getTracker(MMSApplication.TrackerName.APP_TRACKER)
                .send(new HitBuilders.AppViewBuilder().build());
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			//Intent intent = new Intent(this, HomeActivity.class);
			//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			//this.startActivity(intent);
			this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		//outState.putSerializable(SAVED_ALBUMS, this.albums);
		if(this.findViewById(R.id.layout_album) != null){
			AlbumFragment albumFragment = (AlbumFragment) this
					.getSupportFragmentManager().findFragmentByTag(TAG_ALBUM_FRAGMENT);
			if(albumFragment != null){
				this.getSupportFragmentManager().beginTransaction()
					.remove(albumFragment).commit();
			}
		}
		super.onSaveInstanceState(outState);
	}
	
	private void configure(){
		this.discographyList = (ListView) this.findViewById(android.R.id.list);
		this.emptyLabel = (TextView) this.findViewById(android.R.id.empty);
		
		ActionBar actionbar = this.getSupportActionBar();
		actionbar.setTitle(R.string.discography_title);
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setHomeButtonEnabled(true);
	}
	
	@Override
	public void onMMSRequestFinished(MMSResponse response) {
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
		} catch(Exception e){
			e.printStackTrace();
			this.onRequestFailed();
		}
	}
	
	protected void onRequestFailed() {
		this.findViewById(R.id.progress).setVisibility(View.GONE);
		this.emptyLabel.setVisibility(View.VISIBLE);
	}
	
	protected void onErrorResponse(MMSResponse response) {
		this.findViewById(R.id.progress).setVisibility(View.GONE);
		this.emptyLabel.setVisibility(View.VISIBLE);
	}
	
	protected void onSuccessfulResponse(MMSResponse response) {
		this.displayAlbums((MMSList) response.getContent());
	}
	
	private void displayAlbums(MMSList albums){
		this.albums = albums;
		
		this.findViewById(R.id.progress).setVisibility(View.GONE);
		this.findViewById(R.id.discography_container).setVisibility(View.VISIBLE);
		this.discographyList.setVisibility(View.VISIBLE);
		this.discographyList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,
					long id) {
				onAlbumClicked(position);
			}
		});
		this.discographyList.setAdapter(new DiscographyAdapter(this,
				albums));
		
		if(this.findViewById(R.id.layout_album) != null
				&& !this.albums.isEmpty()) {
			this.displaySelectedAlbum(0);
		}
	}
	
	private void displaySelectedAlbum(int position){
		this.findViewById(R.id.layout_album).setVisibility(View.VISIBLE);
		
		AlbumFragment albumFragment = (AlbumFragment) 
				this.getSupportFragmentManager()
				.findFragmentByTag(TAG_ALBUM_FRAGMENT);
		
		if(albumFragment == null){
			albumFragment = new AlbumFragment();
			
			Bundle args = new Bundle();
			args.putSerializable(AlbumFragment.META_ALBUM,
					this.albums.get(position));
			albumFragment.setArguments(args);
			this.getSupportFragmentManager().beginTransaction()
				.add(R.id.layout_album, albumFragment, TAG_ALBUM_FRAGMENT)
				.commit();
		} else {
			albumFragment.updateAlbum((MMSAlbum)
					this.albums.get(position));
		}
	}
	
	protected void onWrongCookie(){
		Toast.makeText(this, R.string.error_wrong_cookie,
				Toast.LENGTH_SHORT).show();
		((MMSApplication) this.getApplication())
			.logout(this);
	}
	
	private void onAlbumClicked(int position){
		if(this.findViewById(R.id.layout_album) == null){
			Intent albumIntent = new Intent(this, AlbumActivity.class);
			Bundle metaData = new Bundle();
			
			metaData.putSerializable(AlbumActivity.META_ALBUM,
					this.albums.get(position));
			albumIntent.putExtras(metaData);
			
			this.startActivity(albumIntent);
		} else {
			this.displaySelectedAlbum(position);
		}
	}
	
	private class DiscographyAdapter extends ArrayAdapter<MMSModel> {
		
		private MMSList discography;
		private DisplayImageOptions options;
		private ImageLoader imageLoader = ImageLoader.getInstance();
		
		private class ViewHolder {
			public ImageView albumArt;
			public TextView albumTitle;
			public TextView tracksCount;
			public TextView releaseDate;
			public TextView label;
		}
		
		public DiscographyAdapter(Context context, MMSList discography) {
			super(context, R.layout.list_item_album, discography);
			this.discography = discography;
			this.options = new DisplayImageOptions.Builder()
				.displayer(new FadeInBitmapDisplayer(800))
				.showStubImage(R.drawable.default_album_art)
				.showImageForEmptyUri(R.drawable.default_album_art)
				.showImageOnFail(R.drawable.default_album_art)
				.cacheInMemory()
				.build();
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			final ViewHolder holder;
			
			if(convertView == null){
				view = LayoutInflater.from(this.getContext()).inflate(R.layout.list_item_album,  parent, false);
				holder = new ViewHolder();
				holder.albumArt = (ImageView) view.findViewById(R.id.img_album_art);
				holder.albumTitle = (TextView) view.findViewById(R.id.lbl_album_name);
				holder.tracksCount = (TextView) view.findViewById(R.id.lbl_tracks_count);
				holder.releaseDate = (TextView) view.findViewById(R.id.lbl_release_date);
				holder.label = (TextView) view.findViewById(R.id.label);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			
			MMSAlbum current = (MMSAlbum) this.discography.get(position);
			
			this.imageLoader.displayImage(
					MMSUtils.scaledImageUrl(current.getCoverUrl(), 500),
					holder.albumArt, this.options);
			holder.albumTitle.setText(current.getName());
			holder.tracksCount.setText("(" + current.getNumberOfTracks() + ")");
			holder.releaseDate.setText(this.getContext().getString(R.string.released_on)
					+ " - " + MMSUtils.formatedDate(current.getReleaseDate()));
			holder.label.setText(current.getLabel());
			
			return view;
		}
		
		@Override
		public MMSModel getItem(int position) {
			return this.discography.get(position);
		}
		
		@Override
		public int getCount() {
			return this.discography.size();
		}
		
	}
	
}
