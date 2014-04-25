package com.mms.fragment;

import java.io.Serializable;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mms.app.MMSApplication;
import com.mms.app.MMSPreferences;
import com.mms.model.MMSList;
import com.mms.model.MMSModel;
import com.mms.model.MMSNewsEntry;
import com.mms.model.MMSResponse;
import com.mms.request.GetNewsRequest;
import com.mms.request.MMSAsyncRequest;
import com.mms.request.MMSAsyncRequest.OnMMSRequestFinishedListener;
import com.mms.util.MMSUtils;
import com.mms.vendetta.R;
import com.mms.view.TrackableScrollView;
import com.mms.view.TrackableScrollView.OnTrackedViewVisibilityFactorChangeListener;

public class NewsFragment extends TrackedListFragment
	implements OnMMSRequestFinishedListener, OnTrackedViewVisibilityFactorChangeListener {

	private static final String TAG_NEWS_DETAILS_DIALOG = "news_details_dialog";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	this.setHasOptionsMenu(true);
    	super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	return inflater.inflate(R.layout.fragment_news_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((TrackableScrollView) view.findViewById(R.id.scroll))
        	.setOnTrackedViewVisibilityFactorChangeListener(this);
        this.loadNews();
    }
    
    private void loadNews(){
    	this.getListView().setVisibility(View.GONE);
    	this.getView().findViewById(android.R.id.empty)
    		.setVisibility(View.GONE);
    	this.getView().findViewById(R.id.progress_news)
    		.setVisibility(View.VISIBLE);
    	String cookie = MMSPreferences.loadString(MMSPreferences.COOKIE, null);
		new MMSAsyncRequest(this).execute(new GetNewsRequest(cookie));
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	inflater.inflate(R.menu.refresh, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case R.id.action_refresh:
			this.loadNews();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	FragmentTransaction transaction = this.getFragmentManager()
				.beginTransaction();
		Fragment prevDialog = this.getFragmentManager().findFragmentByTag(
				TAG_NEWS_DETAILS_DIALOG);

		if (prevDialog != null) {
			transaction.remove(prevDialog);
		}
		transaction.commit();

		Bundle metaData = new Bundle();
		metaData.putSerializable(NewsDetailsDialogFragment.META_ENTRY,
				(Serializable) this.getListAdapter().getItem(position));

		DialogFragment dialog = new NewsDetailsDialogFragment();
		dialog.setArguments(metaData);
		dialog.show(this.getFragmentManager(), TAG_NEWS_DETAILS_DIALOG);
    }
    
    @Override
	public void onMMSRequestFinished(MMSResponse response) {
    	this.getView().findViewById(R.id.progress_news)
			.setVisibility(View.GONE);
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

	protected void onSuccessfulResponse(MMSResponse response) {
        EntriesAdapter adapter = new EntriesAdapter(
        		this.getActivity(), (MMSList) response.getContent());
        this.setListAdapter(adapter);
	}

	protected void onRequestFailed() {
		Toast.makeText(this.getActivity(), R.string.error_connection_failed,
				Toast.LENGTH_SHORT).show();
	}

	protected void onWrongCookie() {
		Toast.makeText(this.getActivity(), R.string.error_wrong_cookie,
				Toast.LENGTH_SHORT).show();
		((MMSApplication) this.getActivity().getApplication()).logout(this
				.getActivity());
	}

	protected void onErrorResponse(MMSResponse response) {
		Toast.makeText(this.getActivity(), response.getMessage(),
				Toast.LENGTH_SHORT).show();
	}

    private static final class EntriesAdapter extends ArrayAdapter<MMSModel> {
    	
        private MMSList newsEntries;

        public EntriesAdapter(Context context, MMSList newsEntries) {
            super(context, R.layout.list_item_news_entry, newsEntries);
            this.newsEntries = newsEntries;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.list_item_news_entry, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            
            MMSNewsEntry entry = (MMSNewsEntry) this.getItem(position);
            holder.lblTitle.get().setText(entry.getTitle());
            holder.lblContent.get().setText(entry.getContent());
            holder.lblDate.get().setText(MMSUtils.formatedDate(entry
					.getDate()));
            
            return convertView;
        }

        @Override
        public int getCount() {
        	return this.newsEntries.size();
        }
        
        @Override
        public MMSModel getItem(int position) {
        	return this.newsEntries.get(position);
        }
    }

    private static final class ViewHolder {

        public WeakReference<TextView> lblTitle;
        public WeakReference<TextView> lblContent;
        public WeakReference<TextView> lblDate;

        public ViewHolder(View view) {
            this.lblTitle = new WeakReference<TextView>((TextView)
            		view.findViewById(R.id.lbl_news_title));
            this.lblContent = new WeakReference<TextView>((TextView)
            		view.findViewById(R.id.lbl_news_content));
            this.lblDate = new WeakReference<TextView>((TextView)
            		view.findViewById(R.id.lbl_date));
        }

    }

	@Override
	public void onTrackedViewVisibilityFactorChanged(float factor) {
		((ActionBarActivity) this.getActivity())
			.getSupportActionBar().setBackgroundDrawable(
					new ColorDrawable(MMSUtils.colorWithAlpha(
							0xffeeeeee, factor)));
	}

}
