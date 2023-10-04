package com.buzzware.monymarket.retrofit.currencyRes;

import com.google.gson.annotations.SerializedName;

public class GesmesSender{

	@SerializedName("gesmes:name")
	private String gesmesName;

	@SerializedName("content")
	private String content;

	public String getGesmesName(){
		return gesmesName;
	}

	public String getContent(){
		return content;
	}
}