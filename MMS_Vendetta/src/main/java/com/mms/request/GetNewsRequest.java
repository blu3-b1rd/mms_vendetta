package com.mms.request;

import org.apache.http.message.BasicNameValuePair;

public class GetNewsRequest extends BaseRequest {

	private static final String PARAM_COOKIE = "cookie";
	
	public GetNewsRequest(String cookie) {
		super("artist/news", RequestType.HTTP_GET);
		this.paramsDictionary.add(new BasicNameValuePair(PARAM_COOKIE, cookie));
	}

}
