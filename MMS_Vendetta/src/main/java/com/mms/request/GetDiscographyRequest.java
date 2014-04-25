package com.mms.request;

import org.apache.http.message.BasicNameValuePair;

public class GetDiscographyRequest extends BaseRequest {

	private static final String PARAM_COOKIE = "cookie"; 
	
	public GetDiscographyRequest(String cookie) {
		super("artist/albums", RequestType.HTTP_GET);
		this.paramsDictionary.add(new BasicNameValuePair(PARAM_COOKIE, cookie));
	}

}
