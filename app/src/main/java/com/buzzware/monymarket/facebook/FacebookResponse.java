package com.buzzware.monymarket.facebook;

public interface FacebookResponse {
    public void onFbSignTnFail();
    public void onFbSignInSuccess();
    public void onFbSignOut();
    public void onFbProfileRecieved(FacebookUser facebookUser);
}
