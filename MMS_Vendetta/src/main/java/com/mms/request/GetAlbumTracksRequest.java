package com.mms.request;

import org.apache.http.message.BasicNameValuePair;

public class GetAlbumTracksRequest extends BaseRequest {

	private static final String PARAM_COOKIE = "cookie";
	private static final String PARAM_ALBUM_ID = "albumId";
	
	public GetAlbumTracksRequest(String cookie, int albumId) {
		super("album/tracks", RequestType.HTTP_GET);
		this.paramsDictionary.add(new BasicNameValuePair(PARAM_COOKIE, cookie));
		this.paramsDictionary.add(new BasicNameValuePair(PARAM_ALBUM_ID, String.valueOf(albumId)));
	}

}
