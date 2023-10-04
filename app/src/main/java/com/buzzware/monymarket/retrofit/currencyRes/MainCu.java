package com.buzzware.monymarket.retrofit.currencyRes;

import com.google.gson.annotations.SerializedName;

public class MainCu{

	@SerializedName("SecCube")
	private SecCube secCube;

	@SerializedName("content")
	private String content;

	public SecCube getSecCube(){
		return secCube;
	}

	public String getContent(){
		return content;
	}
}