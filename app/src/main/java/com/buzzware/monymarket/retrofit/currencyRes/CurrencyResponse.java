package com.buzzware.monymarket.retrofit.currencyRes;

import com.google.gson.annotations.SerializedName;

public class CurrencyResponse{

	@SerializedName("gesmesEnvelope")
	private GesmesEnvelope gesmesEnvelope;

	public GesmesEnvelope getGesmesEnvelope(){
		return gesmesEnvelope;
	}
}