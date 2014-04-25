package com.mms.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mms.model.MMSNewsEntry;
import com.mms.vendetta.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class NewsDetailsDialogFragment extends TrackedDialogFragment {
	
	public static final String META_ENTRY = "meta_entry";
	
	private MMSNewsEntry mEntry;
	private DisplayImageOptions options;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.news_dialog);
		this.mEntry = (MMSNewsEntry) this.getArguments().getSerializable(META_ENTRY);
		this.options = new DisplayImageOptions.Builder()
			.displayer(new FadeInBitmapDisplayer(800))
			.showStubImage(R.drawable.img_band)
			.showImageForEmptyUri(R.drawable.img_band)
			.showImageOnFail(R.drawable.img_band)
			.cacheInMemory()
			.build();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.dialog_news_details, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		ImageLoader.getInstance().displayImage("",
				(ImageView) view.findViewById(R.id.img_news_entry), this.options);
		((TextView) view.findViewById(R.id.lbl_news_title)).setText(this.mEntry.getTitle());
		((TextView) view.findViewById(R.id.lbl_news_content)).setText(this.mEntry.getContent());
	}

}
