package com.buzzware.monymarket.activities.messages.chat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buzzware.monymarket.Models.ProductModel;
import com.buzzware.monymarket.activities.BaseActivity;
import com.buzzware.monymarket.activities.CreateOfferActivity;
import com.buzzware.monymarket.activities.messages.chat.adapter.MessagesAdapter;
import com.buzzware.monymarket.activities.messages.chat.mo.MessageModel;
import com.buzzware.monymarket.activities.messages.chat.mo.ParcelableChat;
import com.buzzware.monymarket.activities.messages.chat.vm.AdminChatViewModel;
import com.buzzware.monymarket.activities.messages.chat.vm.ChatViewModel;
import com.buzzware.monymarket.classes.Constants;
import com.buzzware.monymarket.classes.FirebaseInstances;
import com.buzzware.monymarket.databinding.ActivityChatBinding;
import com.buzzware.monymarket.generic.GenericModelLiveData;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AdminChat extends BaseActivity {

    ActivityChatBinding mBinding;

    ParcelableChat parcelableChat;

    AdminChatViewModel model;

    public static String chatOfferMessage = "";
    public static String type = "";
    public static boolean isChatOfferMessage = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mBinding = ActivityChatBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());

        init();
        getDataFromExtra();


    }


    private void getDataFromExtra() {


        parcelableChat = getIntent().getParcelableExtra("parcelableChat");
        type = getIntent().getStringExtra("type");

        if (parcelableChat.getTypeStatus().equals("admin")) {
            if (!type.equals("0"))
                model.getConversation(parcelableChat.getConversationID(), FirebaseInstances.adminChatCollection, parcelableChat);
        } else if (parcelableChat.getTypeStatus().equalsIgnoreCase("false")) {

            model.getConversation(parcelableChat.getConversationID(), FirebaseInstances.chatCollection, parcelableChat);

        } else {

            if (parcelableChat.getConversationID() == null)

                parcelableChat.setConversationID(UUID.randomUUID().toString());

            model.checkAlreadyExists(parcelableChat);

        }

        if (parcelableChat.getProId() != null && !parcelableChat.getProId().isEmpty())
            getProductDetail(parcelableChat.getProId());


        setListener();

    }

    ListenerRegistration snapshotListener = null;

    private void getProductDetail(String id) {


        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        DocumentReference reference = firebaseFirestore.collection("Products").document(id);

        snapshotListener = reference.addSnapshotListener((value, error) -> {


            ProductModel productModel = value.toObject(ProductModel.class);

            if (productModel.userId.equals(getUserId())) {
                mBinding.offerIV.setVisibility(View.VISIBLE);
            } else {
                mBinding.offerIV.setVisibility(View.GONE);
            }

        });


    }

    private void getParcelableChatModel(GenericModelLiveData genericModelLiveData) {

        switch (genericModelLiveData.status) {

            case error:

                hideLoader();

                showErrorAlert(genericModelLiveData.errorMsg);

                break;

            case loading:

                showLoader();

                break;

            case success:

                hideLoader();

                parcelableChat = (ParcelableChat) genericModelLiveData.object;

                if (parcelableChat.getTypeStatus().equals("admin")) {

                    model.loadMessages(FirebaseInstances.adminChatCollection, parcelableChat.getConversationID());

                } else {

                    model.loadMessages(FirebaseInstances.adminChatCollection, parcelableChat.getConversationID());

                }

                break;
        }
    }


    private void handleMessagesList(GenericModelLiveData genericModelLiveData) {

        switch (genericModelLiveData.status) {

            case error:

                hideLoader();

                showErrorAlert(genericModelLiveData.errorMsg);

                break;

            case loading:


                showLoader();

                break;

            case success:

                hideLoader();

                List<MessageModel> messages = (List<MessageModel>) genericModelLiveData.object;

                setAdapter(messages);

                break;
        }
    }

    private void setAdapter(List<MessageModel> messages) {

        LinearLayoutManager manager = new LinearLayoutManager(AdminChat.this);
        manager.setStackFromEnd(true);
        mBinding.chatRV.setLayoutManager(manager);

        mBinding.chatRV.setAdapter(new MessagesAdapter(AdminChat.this, messages, getUserId(), parcelableChat.getMyImageUrl(), parcelableChat.getOtherUserImageUrl()));

//        mBinding.chatRV.scrollToPosition(messages.size()-1);

    }


    private void setListener() {

        mBinding.titleTV.setText("Chat");

        mBinding.sendIV.setOnClickListener(view -> {

            if (!mBinding.sendMessageET.getText().toString().isEmpty()) {

                sendMessage(mBinding.sendMessageET.getText().toString(), "text");

            }
        });

        mBinding.offerIV.setOnClickListener(v -> {
            Constants.selectedUserId = parcelableChat.getSelectedUserId();
            Constants.selectedProId = parcelableChat.getProId();
            Constants.selectedConId = parcelableChat.getConversationID();
            startActivity(new Intent(AdminChat.this, CreateOfferActivity.class));
        });

        mBinding.backIV.setOnClickListener(view -> finish());

//        mBinding.galleryIV.setOnClickListener(view -> checkPermissions());
    }

    private void sendMessage(String s, String type) {

        mBinding.sendMessageET.setText("");

        model.sendMessage(s, type, parcelableChat);

    }


    @SuppressLint("SetTextI18n")
    private void init() {

        mBinding.titleTV.setText("Messages");

//        mBinding.include.mainMenuIV.setVisibility(View.GONE);
        model = new ViewModelProvider(this).get(AdminChatViewModel.class);

        model.getParcelableChatModel().observe(AdminChat.this, this::getParcelableChatModel);

        model.getMessagesDataModel().observe(AdminChat.this, this::handleMessagesList);

        model.getAlreadyExistsCheckData().observe(AdminChat.this, this::handleAlreadyExistsResponse);


    }


    AlertDialog messagesDeletedPopup;


    private void handleAlreadyExistsResponse(GenericModelLiveData genericModelLiveData) {

        switch (genericModelLiveData.status) {

            case error:

                hideLoader();
//
                showErrorAlert(genericModelLiveData.errorMsg);
//
                break;

            case loading:

                showLoader();

                break;

            case success:

//                hideLoader();

                List<MessageModel> messages = (List<MessageModel>) genericModelLiveData.object;

                setAdapter(messages);

                break;
        }
    }










}
