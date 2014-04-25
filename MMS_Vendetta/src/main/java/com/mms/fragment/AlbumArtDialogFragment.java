package com.mms.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mms.util.MMSUtils;
import com.mms.vendetta.R;
import com.mms.model.MMSAlbum;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class AlbumArtDialogFragment extends TrackedDialogFragment {

	public static final String META_ALBUM = "meta_album";
	
	private MMSAlbum mAlbum;
	private DisplayImageOptions options;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.news_dialog);
		this.mAlbum = (MMSAlbum) this.getArguments().getSerializable(META_ALBUM);
		this.options = new DisplayImageOptions.Builder()
			.displayer(new FadeInBitmapDisplayer(800))
			.showStubImage(R.drawable.default_album_art)
			.showImageForEmptyUri(R.drawable.default_album_art)
			.showImageOnFail(R.drawable.default_album_art)
			.cacheInMemory()
			.build();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.dialog_album_art, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		ImageLoader.getInstance().displayImage(
				MMSUtils.imageUrl(this.mAlbum.getCoverUrl()),
				(ImageView) view.findViewById(R.id.img_album_art), this.options);
		((TextView) view.findViewById(R.id.lbl_album_name)).setText(this.mAlbum.getName());
	}

}