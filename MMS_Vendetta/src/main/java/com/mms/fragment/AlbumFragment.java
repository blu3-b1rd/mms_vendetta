package com.mms.fragment;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mms.util.MMSUtils;
import com.mms.vendetta.R;
import com.mms.app.MMSApplication;
import com.mms.app.MMSPreferences;
import com.mms.model.MMSAlbum;
import com.mms.model.MMSList;
import com.mms.model.MMSModel;
import com.mms.model.MMSResponse;
import com.mms.model.MMSTrack;
import com.mms.model.MMSUrl;
import com.mms.request.GetAlbumTracksRequest;
import com.mms.request.MMSAsyncRequest;
import com.mms.request.MMSAsyncRequest.OnMMSRequestFinishedListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class AlbumFragment extends TrackedFragment implements
		OnMMSRequestFinishedListener {

	public static final String META_ALBUM = "meta_album";
	private static final String TAG_ALBUM_ART_DIALOG = "album_art_dialog";

	private MMSAlbum mAlbum;
	private DisplayImageOptions options;

	private ImageView albumArt;
	private TextView releaseDate;
	private TextView label;
	private TextView albumName;
	private ListView tracksList;
	private TrackListAdapter adapter;
	private DialogFragment demoDialog = null;
	private DialogFragment albumArtDialog = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//if(savedInstanceState == null){
			this.mAlbum = (MMSAlbum) this.getArguments()
					.getSerializable(META_ALBUM);
		//} else {
			//this.mAlbum = (MMSAlbum) savedInstanceState
				//	.getSerializable(META_ALBUM);
		//}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_album, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		this.configure(view);
		
		String cookie = MMSPreferences.loadString(MMSPreferences.COOKIE, null);
		new MMSAsyncRequest(this).execute(new GetAlbumTracksRequest(cookie,
				this.mAlbum.getId()));
		this.setData();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		//outState.putSerializable(META_ALBUM, this.mAlbum);
		super.onSaveInstanceState(outState);
	}

	private void configure(View view) {
		this.albumArt = (ImageView) view.findViewById(R.id.img_album_art);
		this.releaseDate = (TextView) view.findViewById(R.id.lbl_release_date);
		this.label = (TextView) view.findViewById(R.id.label);
		this.albumName = (TextView) view.findViewById(R.id.lbl_album_name);
		this.tracksList = (ListView) view.findViewById(android.R.id.list);
		view.findViewById(R.id.layout_album_art).setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) {
				onAlbumArtClicked(v);
			}
		});

		this.options = new DisplayImageOptions.Builder()
				.displayer(new FadeInBitmapDisplayer(800))
				.showStubImage(R.drawable.default_album_art)
				.showImageForEmptyUri(R.drawable.default_album_art)
				.showImageOnFail(R.drawable.default_album_art)
				.cacheInMemory()
				.build();
	}
	
	public void updateAlbum(MMSAlbum album){
		if(this.mAlbum.getId() == album.getId()){
			return;
		}
		
		this.getView().findViewById(R.id.progress).setVisibility(View.VISIBLE);
		this.tracksList.setVisibility(View.GONE);
		
		this.mAlbum = album;
		String cookie = MMSPreferences.loadString(MMSPreferences.COOKIE, null);
		new MMSAsyncRequest(this).execute(new GetAlbumTracksRequest(cookie,
				this.mAlbum.getId()));
		this.setData();
	}

	private void setData() {
		ImageLoader.getInstance().displayImage(
				MMSUtils.scaledImageUrl(this.mAlbum.getCoverUrl(), 500),
				this.albumArt, this.options);
		this.releaseDate.setText(MMSUtils.formatedDate(
				this.mAlbum.getReleaseDate()));
		this.label.setText(this.mAlbum.getLabel());
		this.albumName.setText(this.mAlbum.getName());
	}

	private void onTrackClicked(int position) {
		MMSTrack mTrack = (MMSTrack) this.adapter.getItem(position);
		if (mTrack.getDemoUrl() != null && !mTrack.getDemoUrl().isEmpty()) {
			Bundle args = new Bundle();
			args.putSerializable(DemoPlayerDialogFragment.META_TRACK, mTrack);
			args.putString(DemoPlayerDialogFragment.META_ALBUM_NAME,
					this.mAlbum.getName());
			args.putString(DemoPlayerDialogFragment.META_ALBUM_ART,
					this.mAlbum.getCoverUrl());
			this.demoDialog = new DemoPlayerDialogFragment();
			this.demoDialog.setArguments(args);
			this.demoDialog.show(this.getFragmentManager(), "");
		}
	}

	public void onAlbumArtClicked(View view) {
		FragmentTransaction transaction = this.getFragmentManager()
				.beginTransaction();
		Fragment prevDialog = this.getFragmentManager()
				.findFragmentByTag(TAG_ALBUM_ART_DIALOG);

		if (prevDialog != null) {
			transaction.remove(prevDialog);
		}
		transaction.commit();

		Bundle metaData = new Bundle();
		metaData.putSerializable(AlbumArtDialogFragment.META_ALBUM, this.mAlbum);
		this.albumArtDialog = new AlbumArtDialogFragment();
		this.albumArtDialog.setArguments(metaData);
		this.albumArtDialog.show(this.getFragmentManager(), TAG_ALBUM_ART_DIALOG);
	}

	@Override
	public void onMMSRequestFinished(MMSResponse response) {
		try {
			switch (response.getStatus()) {
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
		} catch (Exception e) {
			e.printStackTrace();
			this.onRequestFailed();
		}
	}

	public void onSuccessfulResponse(MMSResponse response) {
		this.getView().findViewById(R.id.progress).setVisibility(View.GONE);
		this.tracksList.setVisibility(View.VISIBLE);

		this.adapter = new TrackListAdapter(
				this.getActivity().getApplicationContext(),
				(MMSList) response.getContent());
		this.tracksList.setAdapter(this.adapter);
		this.tracksList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				onTrackClicked(position);
			}
		});
	}

	protected void onRequestFailed() {
		Toast.makeText(this.getActivity().getApplicationContext(),
				R.string.error_connection_failed, Toast.LENGTH_SHORT).show();
	}

	protected void onWrongCookie() {
		Toast.makeText(this.getActivity().getApplicationContext(),
				R.string.error_wrong_cookie, Toast.LENGTH_SHORT)
				.show();
		((MMSApplication) this.getActivity().getApplication()).logout(
				this.getActivity());
	}

	protected void onErrorResponse(MMSResponse response) {
		Toast.makeText(this.getActivity().getApplicationContext(),
				response.getMessage(), Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onDestroy() {
		if(this.demoDialog != null){this.demoDialog.dismiss();}
		if(this.albumArtDialog != null){this.albumArtDialog.dismiss();}
		super.onDestroy();
	}

	private class TrackListAdapter extends ArrayAdapter<MMSModel> {

		private MMSList tracksList;

		private class ViewHolder {
			public TextView lblNumber;
			public TextView lblName;
			public TextView lblDuration;
			public TextView lblDemo;
			public ImageButton more;
		}

		public TrackListAdapter(Context context, MMSList tracksList) {
			super(context, R.layout.list_item_track, tracksList);
			this.tracksList = tracksList;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view = convertView;
			final ViewHolder holder;

			if (view == null) {
				view = LayoutInflater.from(this.getContext()).inflate(
						R.layout.list_item_track, parent, false);
				holder = new ViewHolder();
				holder.lblNumber = (TextView) view
						.findViewById(R.id.track_number);
				holder.lblName = (TextView) view.findViewById(R.id.track_name);
				holder.lblDuration = (TextView) view
						.findViewById(R.id.track_duration);
				holder.lblDemo = (TextView) view.findViewById(R.id.lbl_demo);
				holder.more = (ImageButton) view.findViewById(R.id.btn_more);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			MMSTrack current = (MMSTrack) this.tracksList.get(position);

			holder.lblNumber.setText(current.getTrackNumber() + ". ");
			holder.lblName.setText(current.getName());
			holder.lblDuration.setText(current.getDuration());

			if (current.getDemoUrl() == null || current.getDemoUrl().equals("")) {
				holder.lblDemo.setVisibility(View.GONE);
			} else {
				holder.lblDemo.setVisibility(View.VISIBLE);
			}

			holder.more.setFocusableInTouchMode(false);
			if (current.getUrls().isEmpty()) {
				holder.more.setVisibility(View.GONE);
			} else {
				holder.more.setVisibility(View.VISIBLE);
				holder.more.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						showPopupMore(v, position);
					}
				});
			}

			return view;
		}

		@Override
		public int getCount() {
			return this.tracksList.size();
		}

		@Override
		public MMSModel getItem(int position) {
			return this.tracksList.get(position);
		}

		private void showPopupMore(View v, int position) {
			PopupMenu popup = new PopupMenu(this.getContext(), v);
			Menu menu = popup.getMenu();
			List<MMSUrl> urls = ((MMSTrack) this.tracksList.get(position))
					.getUrls();
			int i = 0;
			for (MMSUrl url : urls) {
				menu.add(0, Menu.FIRST + i, Menu.NONE, url.getUrlType());
			}
			popup.show();
		}

	}
}
