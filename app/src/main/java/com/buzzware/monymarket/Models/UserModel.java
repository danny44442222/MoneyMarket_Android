package com.buzzware.monymarket.Models;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class UserModel implements Parcelable {

    public String userId;
    public String dateOfBirth = "";
    public String email = "";
    public String fullname = "";
    public String firstName = "";
    public String lastName = "";
    public String imageUrl = "";
    public String password = "";
    public String userBio = "";
    public String userCurrency = "";
    public List<String> userLikedPro;
    public String userPhone="";
    public String userToken="";
    public String address="";
    public boolean onlineStatus=false;


    public UserModel() {
    }


    protected UserModel(Parcel in) {
        userId = in.readString();
        dateOfBirth = in.readString();
        email = in.readString();
        fullname = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        imageUrl = in.readString();
        password = in.readString();
        userBio = in.readString();
        userCurrency = in.readString();
        userLikedPro = in.createStringArrayList();
        userPhone = in.readString();
        userToken = in.readString();
        address = in.readString();
        onlineStatus = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(dateOfBirth);
        dest.writeString(email);
        dest.writeString(fullname);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(imageUrl);
        dest.writeString(password);
        dest.writeString(userBio);
        dest.writeString(userCurrency);
        dest.writeStringList(userLikedPro);
        dest.writeString(userPhone);
        dest.writeString(userToken);
        dest.writeString(address);
        dest.writeByte((byte) (onlineStatus ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };
}