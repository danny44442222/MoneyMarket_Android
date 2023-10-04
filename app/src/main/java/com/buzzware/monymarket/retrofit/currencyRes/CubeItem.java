package com.buzzware.monymarket.retrofit.currencyRes;

import com.google.gson.annotations.SerializedName;

public class CubeItem{

	@SerializedName("rate")
	private String rate;

	@SerializedName("currency")
	private String currency;

	public String getRate(){
		return rate;
	}

	public String getCurrency(){
		return currency;
	}
}