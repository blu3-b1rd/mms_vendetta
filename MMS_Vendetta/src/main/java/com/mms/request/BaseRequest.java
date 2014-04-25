package com.mms.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.mms.app.MMSConfig;
import com.mms.model.MMSModelFactory;
import com.mms.model.MMSResponse;

public abstract class BaseRequest {
	
	public enum RequestType {
		HTTP_GET, HTTP_POST;
	}
	
	public enum ResponseStatus {
		MMS_SUCCESS, MMS_WRONG_COOKIE, MMS_ERROR;
	}

	protected List<NameValuePair> paramsDictionary;
	private String requestUrl;
	private RequestType requestType;
	
	private static final String PARAM_ALIAS = "alias";

	protected BaseRequest(String methodName,
			RequestType requestType) {
		this.requestUrl = MMSConfig.MMS_BASE_API_URL + methodName;
		this.requestType = requestType;
		this.paramsDictionary = new ArrayList<NameValuePair>();
		this.paramsDictionary.add(new BasicNameValuePair(PARAM_ALIAS, MMSConfig.ARTIST_ALIAS));
	}

	public MMSResponse perform() throws Exception {
		HttpUriRequest request = null;
		
		switch (this.requestType) {
		case HTTP_GET:
			request = this.buildGetRequest();
			break;
		case HTTP_POST:
			request = this.buildPostRequest();
			break;
		}

		return this.getResponse(request);
	}

	private HttpUriRequest buildGetRequest() {
		HttpGet httpGet = new HttpGet(this.requestUrl + "?"
				+ URLEncodedUtils.format(this.paramsDictionary, "utf-8"));
		return httpGet;
	}
	
	private HttpUriRequest buildPostRequest() throws Exception {
		HttpPost httpPost = new HttpPost(this.requestUrl);
		httpPost.setEntity(new UrlEncodedFormEntity(this.paramsDictionary, "utf-8"));
		return httpPost;
	}
	
	private MMSResponse getResponse(HttpUriRequest request) throws Exception {
		InputStream content = null;
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(request);
		content = response.getEntity().getContent();
		return (MMSResponse) MMSModelFactory.getFactory()
				.getModel("response", this.streamToString(content));
	}

	private String streamToString(InputStream input) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {
			br = new BufferedReader(new InputStreamReader(input, "utf-8"));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();
	}

}
