package com.mms.model;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.mms.model.MMSModel.MMSBuilder;

public class MMSModelFactory {
	
	private static MMSModelFactory mmsModelFactory = null;
	private final Map<String, Class<? extends MMSBuilder>> modelDictionary;
	
	private MMSModelFactory(){
		this.modelDictionary = new HashMap<String, Class<? extends MMSBuilder>>();
		this.modelDictionary.put("response", MMSResponse.Builder.class);
		this.modelDictionary.put("user", MMSUser.Builder.class);
		this.modelDictionary.put("account", MMSAccount.Builder.class);
		this.modelDictionary.put("albumlist", MMSList.Builder.class);
		this.modelDictionary.put("album", MMSAlbum.Builder.class);
		this.modelDictionary.put("tracklist", MMSList.Builder.class);
		this.modelDictionary.put("track", MMSTrack.Builder.class);
		this.modelDictionary.put("newslist", MMSList.Builder.class);
		this.modelDictionary.put("news", MMSNewsEntry.Builder.class);
		this.modelDictionary.put("concertlist", MMSList.Builder.class);
		this.modelDictionary.put("concert", MMSConcert.Builder.class);
		this.modelDictionary.put("imagelist", MMSList.Builder.class);
		this.modelDictionary.put("image", MMSGalleryItem.Builder.class);
		this.modelDictionary.put("playlistlist", MMSList.Builder.class);
		this.modelDictionary.put("playlist", MMSPlaylist.Builder.class);
	}
	
	public static MMSModelFactory getFactory(){
		if(mmsModelFactory == null){
			mmsModelFactory = new MMSModelFactory();
		}
		return mmsModelFactory;
	}
	
	public MMSModel getModel(String modelType, String input) throws Exception {
		String modelTypeLower = modelType.toLowerCase(Locale.ENGLISH);
		Constructor<? extends MMSBuilder> constructor = 
				this.modelDictionary.get(modelTypeLower).getConstructor(String.class);
		MMSBuilder builder = constructor.newInstance(modelTypeLower);
		return builder.build(input);
	}
	
}
