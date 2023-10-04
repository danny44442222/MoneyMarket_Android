package com.buzzware.monymarket.retrofit.currencyRes;

import com.google.gson.annotations.SerializedName;

public class GesmesEnvelope{

	@SerializedName("xmlns")
	private String xmlns;

	@SerializedName("xmlns:gesmes")
	private String xmlnsGesmes;

	@SerializedName("MainCu")
	private MainCu mainCu;

	@SerializedName("gesmes:subject")
	private String gesmesSubject;

	@SerializedName("gesmes:Sender")
	private GesmesSender gesmesSender;

	@SerializedName("content")
	private String content;

	public String getXmlns(){
		return xmlns;
	}

	public String getXmlnsGesmes(){
		return xmlnsGesmes;
	}

	public MainCu getMainCu(){
		return mainCu;
	}

	public String getGesmesSubject(){
		return gesmesSubject;
	}

	public GesmesSender getGesmesSender(){
		return gesmesSender;
	}

	public String getContent(){
		return content;
	}
}