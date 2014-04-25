package com.mms.request;

import org.apache.http.message.BasicNameValuePair;

public class GetArtistGallery extends BaseRequest {

	private static final String PARAM_COOKIE = "cookie";
	
	public GetArtistGallery(String cookie) {
		super("artist/images", RequestType.HTTP_GET);
		this.paramsDictionary.add(new BasicNameValuePair(PARAM_COOKIE, cookie));
	}

}
