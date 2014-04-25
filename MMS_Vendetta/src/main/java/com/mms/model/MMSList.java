package com.mms.model;

import java.util.ArrayList;

import org.json.JSONArray;

public class MMSList extends ArrayList<MMSModel> implements MMSModel {

	private static final long serialVersionUID = -5861775203285712414L;

	private MMSList(){}
	
	public static class Builder extends MMSBuilder {

		public Builder(String modelType) {
			super(modelType);
		}

		@Override
		public MMSModel build(String input) throws Exception {
			JSONArray jsonList = new JSONArray(input);
			MMSList list = new MMSList();
			
			int length = jsonList.length();
			for(int i =  0 ; i < length ; i++){
				list.add(MMSModelFactory.getFactory().getModel(this.modelType,
						jsonList.getJSONObject(i).toString()));
			}
			
			return list;
		}
	}
	
}
