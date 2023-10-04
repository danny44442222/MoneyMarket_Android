package com.buzzware.monymarket.retrofit.currencyRes;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class SecCube{

	@SerializedName("Cube")
	private List<CubeItem> cube;

	@SerializedName("time")
	private String time;

	@SerializedName("content")
	private String content;

	public List<CubeItem> getCube(){
		return cube;
	}

	public String getTime(){
		return time;
	}

	public String getContent(){
		return content;
	}
}