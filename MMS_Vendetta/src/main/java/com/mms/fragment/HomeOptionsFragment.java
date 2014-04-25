package com.mms.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mms.activity.DrawerActivity;
import com.mms.app.MMSPreferences;
import com.mms.util.HomeSection;
import com.mms.vendetta.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class HomeOptionsFragment extends TrackedListFragment {
	
	private ImageLoader imageLoader;
	private DisplayImageOptions displayOptions;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.imageLoader = ImageLoader.getInstance();
		this.displayOptions = new DisplayImageOptions.Builder()
			.displayer(new FadeInBitmapDisplayer(800))
			.showStubImage(R.drawable.img_default_display_img)
			.showImageForEmptyUri(R.drawable.img_default_display_img)
			.showImageOnFail(R.drawable.img_default_display_img)
			.cacheInMemory()
			.build();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_home_sections, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		this.getListView().setAdapter(new HomeOptionsAdapter(this.getActivity()));
		this.updateUserData(view);
	}
	
	private void updateUserData(View view){
		/*
		view.findViewById(R.id.layout_me).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onMeClicked();
			}
		});
		*/
		
		this.imageLoader.displayImage(MMSPreferences.loadString(
				MMSPreferences.DISPLAY_IMG, ""), (ImageView) view.findViewById(R.id.img_display_img),
				this.displayOptions);
		((TextView) view.findViewById(R.id.lbl_user_name))
			.setText(MMSPreferences.loadString(MMSPreferences.DISPLAY_NAME, ""));
	}
	
	/*
	private void onMeClicked(){
		this.startActivity(new Intent(this.getActivity(), MeSectionActivity.class));
	}
	*/
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		((DrawerActivity) this.getActivity()).hideDrawer();
		this.getActivity().startActivity(new Intent(this.getActivity(),
				HomeSection.values()[position].getActivityClass()));
	}
	
	private static final class HomeOptionsAdapter extends ArrayAdapter<String> {
		
        public HomeOptionsAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
            	convertView = LayoutInflater.from(this.getContext())
            			.inflate(R.layout.list_item_section, parent, false);
            }
            
            HomeSection option = HomeSection.values()[position];
            ((ImageView) convertView.findViewById(R.id.option_icon)).setImageResource(
            		option.getIconId());
            ((TextView) convertView.findViewById(R.id.option_name)).setText(
            		option.getTitle());
            
            return convertView;
        }

        @Override
        public int getCount() {
        	return HomeSection.values().length;
        }

    }
	
}
