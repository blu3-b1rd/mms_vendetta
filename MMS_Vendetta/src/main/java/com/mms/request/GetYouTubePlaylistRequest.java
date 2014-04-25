package com.mms.request;

import org.apache.http.message.BasicNameValuePair;

public class GetYouTubePlaylistRequest extends BaseRequest {
	
	private static final String PARAM_COOKIE = "cookie";
	
	public GetYouTubePlaylistRequest(String cookie){
		super("artist/playlists", RequestType.HTTP_GET);
		this.paramsDictionary.add(new BasicNameValuePair(PARAM_COOKIE, cookie));
	}

}
