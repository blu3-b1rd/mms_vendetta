package com.mms.request;

import com.mms.model.MMSResponse;

import android.os.AsyncTask;

public class MMSAsyncRequest extends AsyncTask<BaseRequest, Integer, MMSResponse> {

	public interface OnMMSRequestFinishedListener {
		public void onMMSRequestFinished(MMSResponse response);
	}
	
	private OnMMSRequestFinishedListener listener;
	
	public MMSAsyncRequest(OnMMSRequestFinishedListener listener){
		this.listener = listener;
	}
	
	@Override
	protected MMSResponse doInBackground(BaseRequest... params) {
		BaseRequest request = params[0];
		if(request == null){
			return null;
		}
		try {
			MMSResponse response = request.perform();
			return response;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	protected void onPostExecute(MMSResponse result) {
		this.listener.onMMSRequestFinished(result);
	}
	
}
