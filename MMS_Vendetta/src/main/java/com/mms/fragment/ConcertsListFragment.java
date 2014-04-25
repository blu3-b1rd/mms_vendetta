package com.mms.fragment;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mms.app.MMSApplication;
import com.mms.app.MMSPreferences;
import com.mms.model.MMSConcert;
import com.mms.model.MMSList;
import com.mms.model.MMSModel;
import com.mms.model.MMSResponse;
import com.mms.request.GetEventsRequest;
import com.mms.request.MMSAsyncRequest;
import com.mms.request.MMSAsyncRequest.OnMMSRequestFinishedListener;
import com.mms.util.MMSUtils;
import com.mms.vendetta.R;

public class ConcertsListFragment extends TrackedListFragment
	implements OnMMSRequestFinishedListener {

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_concerts_list, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		this.loadEvents();
	}
	
	private void loadEvents(){
		this.getListView().setVisibility(View.GONE);
		this.getView().findViewById(android.R.id.empty)
			.setVisibility(View.GONE);
		this.getView().findViewById(R.id.progress_concerts)
			.setVisibility(View.VISIBLE);
		
		String cookie = MMSPreferences.loadString(MMSPreferences.COOKIE, null);
		new MMSAsyncRequest(this).execute(new GetEventsRequest(cookie));
	}
	
	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	inflater.inflate(R.menu.refresh, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case R.id.action_refresh:
			this.loadEvents();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
    }
	
	@Override
	public void onMMSRequestFinished(MMSResponse response) {
		this.getView().findViewById(R.id.progress_concerts)
			.setVisibility(View.GONE);
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
		CarouselListAdapter adapter = new CarouselListAdapter(
				this.getActivity(), (MMSList) response.getContent());
		this.setListAdapter(adapter);
	}
	
	protected void onRequestFailed(){
		Toast.makeText(this.getActivity(), R.string.error_connection_failed,
				Toast.LENGTH_SHORT).show();
	}
	
	protected void onWrongCookie(){
		Toast.makeText(this.getActivity(), R.string.error_wrong_cookie,
				Toast.LENGTH_SHORT).show();
		((MMSApplication) this.getActivity().getApplication())
			.logout(this.getActivity());
	}
	
	protected void onErrorResponse(MMSResponse response) {
		Toast.makeText(this.getActivity(), response.getMessage(),
				Toast.LENGTH_SHORT).show();
	}
	
	private static final class CarouselListAdapter extends ArrayAdapter<MMSModel> {

        //private static final int ITEM_VIEW_TYPE_HEADER = 0;
        //private static final int ITEM_VIEW_TYPE_DATA = 1;
        //private static final int VIEW_TYPE_COUNT = 2;

        //private final View mHeader;
        private MMSList concerts;

        public CarouselListAdapter(Context context, MMSList concerts) {
            super(context, R.layout.list_item_event, concerts);
            //this.mHeader = LayoutInflater.from(context).inflate(R.layout.faux_carousel, null, false);
            this.concerts = concerts;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	/*
            if (position == 0) {
                return mHeader;
            }
           	*/

            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.list_item_event, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            
            MMSConcert concert = (MMSConcert) this.getItem(position);
            holder.lblVenue.get().setText(concert.getVenue());
            holder.lblAddress.get().setText(concert.getAddress());
            holder.lblDate.get().setText(MMSUtils.formatedDate(
            		concert.getDate()));
            return convertView;
        }

        /*
        @Override
        public boolean hasStableIds() {
            return true;
        }
        */

        @Override
        public int getCount() {
        	return this.concerts.size();
        }
        
        @Override
        public MMSModel getItem(int position) {
        	return this.concerts.get(position);
        }

        /*
        @Override
        public long getItemId(int position) {
            if (position == 0) {
                return -1;
            }
            return position - 1;
        }

        @Override
        public int getViewTypeCount() {
            return VIEW_TYPE_COUNT;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return ITEM_VIEW_TYPE_HEADER;
            }
            return ITEM_VIEW_TYPE_DATA;
        }
        */
        
    }

	private static final class ViewHolder {

        public WeakReference<TextView> lblVenue;
        public WeakReference<TextView> lblAddress;
        public WeakReference<TextView> lblDate;

        public ViewHolder(View view) {
            this.lblVenue = new WeakReference<TextView>((TextView)
            		view.findViewById(R.id.lbl_venue));
            this.lblAddress = new WeakReference<TextView>((TextView)
            		view.findViewById(R.id.lbl_address));
            this.lblDate = new WeakReference<TextView>((TextView)
            		view.findViewById(R.id.lbl_date));
        }

    }
	
}
