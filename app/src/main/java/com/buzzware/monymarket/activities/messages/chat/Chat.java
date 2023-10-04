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
import com.buzzware.monymarket.Models.UserModel;
import com.buzzware.monymarket.activities.BaseActivity;
import com.buzzware.monymarket.activities.CreateOfferActivity;
import com.buzzware.monymarket.activities.EditProfileActivity;
import com.buzzware.monymarket.activities.messages.chat.adapter.MessagesAdapter;
import com.buzzware.monymarket.activities.messages.chat.mo.MessageModel;
import com.buzzware.monymarket.activities.messages.chat.mo.ParcelableChat;
import com.buzzware.monymarket.activities.messages.chat.vm.ChatViewModel;
import com.buzzware.monymarket.classes.Constants;
import com.buzzware.monymarket.classes.FirebaseInstances;
import com.buzzware.monymarket.classes.OfferMessageEvent;
import com.buzzware.monymarket.classes.SessionManager;
import com.buzzware.monymarket.databinding.ActivityChatBinding;
import com.buzzware.monymarket.generic.GenericModelLiveData;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Chat extends BaseActivity {

    ActivityChatBinding mBinding;

    ParcelableChat parcelableChat;

    ChatViewModel model;

    public static String chatOfferMessage = "";
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

        if (parcelableChat.getTypeStatus().equals("admin")){
//            model.getConversation(parcelableChat.getConversationID(), FirebaseInstances.adminChatCollection, parcelableChat);
        }

        else if (parcelableChat.getTypeStatus().equalsIgnoreCase("false")) {

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

                    model.loadMessages(FirebaseInstances.chatCollection, parcelableChat.getConversationID());

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

        LinearLayoutManager manager = new LinearLayoutManager(Chat.this);
        manager.setStackFromEnd(true);
        mBinding.chatRV.setLayoutManager(manager);

        mBinding.chatRV.setAdapter(new MessagesAdapter(Chat.this, messages, getUserId(), parcelableChat.getMyImageUrl(), parcelableChat.getOtherUserImageUrl()));

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
            Constants.selectedProId=parcelableChat.getProId();
            Constants.selectedConId=parcelableChat.getConversationID();
            startActivity(new Intent(Chat.this, CreateOfferActivity.class));
        });

        mBinding.backIV.setOnClickListener(view -> finish());

//        mBinding.galleryIV.setOnClickListener(view -> checkPermissions());
    }

    private void sendMessage(String s, String type) {

        mBinding.sendMessageET.setText("");

        model.sendMessage(s, type, parcelableChat);

    }


    private void sendImageMessage(String s) {

        mBinding.sendMessageET.setText("");

        model.sendImageMessage(s, parcelableChat);

    }

    @SuppressLint("SetTextI18n")
    private void init() {

        mBinding.titleTV.setText("Messages");

//        mBinding.include.mainMenuIV.setVisibility(View.GONE);
        model = new ViewModelProvider(this).get(ChatViewModel.class);

        model.getParcelableChatModel().observe(Chat.this, this::getParcelableChatModel);

        model.getMessagesDataModel().observe(Chat.this, this::handleMessagesList);

        model.getAlreadyExistsCheckData().observe(Chat.this, this::handleAlreadyExistsResponse);

        model.getRideListenerData().observe(Chat.this, this::rideListenerResponse);

    }

    private void rideListenerResponse(GenericModelLiveData genericModelLiveData) {


        switch (genericModelLiveData.status) {

            case error:

                break;

            case loading:

//                showLoader();

                break;

            case success:

                showChatDeletedPopup();

                break;
        }

    }

    AlertDialog messagesDeletedPopup;

    private void showChatDeletedPopup() {

        if (messagesDeletedPopup != null && messagesDeletedPopup.isShowing())

            return;
        messagesDeletedPopup = new AlertDialog.Builder(Chat.this)
                .setCancelable(false)
                .setTitle("Alert")
                .setMessage("Ride is completed or cancelled. This chat has been deleted")
                .setPositiveButton("OK", (dialogInterface, i) -> {

                    dialogInterface.dismiss();
                    finish();

                }).create();

        messagesDeletedPopup.show();
    }

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


    private void checkPermissions() {

        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};


        Permissions.check(this/*context*/, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {

                showImagePickerDialog();

            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {

//                showPermissionsDeniedError(getString(R.string.camera_permissions_denied_string));

            }
        });
    }

    public void showImagePickerDialog() {

        // setup the alert builder

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Choose");

        // add a list

        String[] animals = {"Camera", "Gallery"};

        builder.setItems(animals, (dialog, which) -> {
            switch (which) {

                case 0:

                    dispatchTakePictureIntent();
                    break;

                case 1:

                    openGallery();
                    break;

            }
        });

        // create and show the alert dialog

        androidx.appcompat.app.AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        dispatchTakePictureLauncher.launch(takePictureIntent);

    }

    private void openGallery() {

        Intent intent = new Intent();

        intent.setType("image/*");

        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select Image"), ACCESS_Gallery);

    }

    Uri imageUri;

    ActivityResultLauncher<Intent> dispatchTakePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {

                    Bitmap photo = null;

                    if (result.getData() != null) {

                        photo = (Bitmap) result.getData().getExtras().get("data");

                        imageUri = getImageUri(Chat.this, photo);

                        UploadImage();

                    }

                }
            });

    int ACCESS_Gallery = 5000;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACCESS_Gallery && resultCode == Activity.RESULT_OK) {

            imageUri = data.getData();

            UploadImage();
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, new Date().toString(), null);

        return Uri.parse(path);
    }


    private void UploadImage() {

        showLoader();

        String randomKey = UUID.randomUUID().toString();

        StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("userThumbnail/" + randomKey);

        reference.
                putFile(imageUri).addOnSuccessListener(taskSnapshot -> {

            reference.getDownloadUrl().addOnSuccessListener(uri1 -> {

                hideLoader();

                sendImageMessage(uri1.toString());

                imageUri = null;

            });
        }).addOnFailureListener(e -> {

            hideLoader();

            showErrorAlert(e.getLocalizedMessage());

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isChatOfferMessage) {
            isChatOfferMessage = false;
            sendMessage(chatOfferMessage, "offer");
        }
    }
}
