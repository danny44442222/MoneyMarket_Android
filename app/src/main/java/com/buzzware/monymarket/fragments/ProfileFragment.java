package com.buzzware.monymarket.fragments;

import static com.buzzware.monymarket.activities.BaseActivity.getUserId;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.buzzware.monymarket.Models.UserModel;
import com.buzzware.monymarket.R;
import com.buzzware.monymarket.activities.ContactUsActivity;
import com.buzzware.monymarket.activities.EditBankDetailActivity;
import com.buzzware.monymarket.activities.EditProfileActivity;
import com.buzzware.monymarket.activities.FaqActivity;
import com.buzzware.monymarket.activities.FavoriteActivity;
import com.buzzware.monymarket.activities.LoginActivity;
import com.buzzware.monymarket.activities.MyOrderActivity;
import com.buzzware.monymarket.activities.MyTransactionsActivity;
import com.buzzware.monymarket.activities.NotificationActivity;
import com.buzzware.monymarket.activities.StartUpActivity;
import com.buzzware.monymarket.activities.WalletActivity;
import com.buzzware.monymarket.activities.messages.chat.AdminChat;
import com.buzzware.monymarket.activities.messages.chat.Chat;
import com.buzzware.monymarket.activities.messages.chat.mo.ParcelableChat;
import com.buzzware.monymarket.activities.messages.chatList.mo.LastMessageModel;
import com.buzzware.monymarket.adapters.CartAdapter;
import com.buzzware.monymarket.classes.FirebaseInstances;
import com.buzzware.monymarket.classes.ImageCapture;
import com.buzzware.monymarket.classes.SessionManager;
import com.buzzware.monymarket.databinding.BottomSheetDialogConversionBinding;
import com.buzzware.monymarket.databinding.BottomSheetDialogDeleteAccountBinding;
import com.buzzware.monymarket.databinding.BottomSheetDialogPaymentOptionBinding;
import com.buzzware.monymarket.databinding.BottomSheetDialogRemoveIteBinding;
import com.buzzware.monymarket.databinding.FragmentCartBinding;
import com.buzzware.monymarket.databinding.FragmentProfileBinding;
import com.buzzware.monymarket.generic.GenericModelLiveData;
import com.buzzware.monymarket.interfaces.CartFragmentCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;
import java.util.UUID;

public class ProfileFragment extends BaseFragment {

    FragmentProfileBinding binding;

    Uri imageUri = null;
    ImageCapture imageCapture = new ImageCapture();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater);

        setViews();

        setListener();

        return binding.getRoot();

    }

    private void setViews() {

        if (!SessionManager.getInstance().getUser(getContext()).imageUrl.equals(""))
            Glide.with(this)
                    .asBitmap()
                    .error(R.drawable.user_dummy_profile)
                    .placeholder(R.drawable.user_dummy_profile)
                    .load(SessionManager.getInstance().getUser(getContext()).imageUrl)
                    .into(binding.userIV);

        binding.userNameTV.setText(SessionManager.getInstance().getUser(getContext()).fullname);
        binding.userDesTV.setText(SessionManager.getInstance().getUser(getContext()).userBio);
        binding.currencyTV.setText(SessionManager.getInstance().getUser(getContext()).userCurrency);
    }

    @Override
    public void onResume() {
        super.onResume();
        setViews();
    }

    private void setListener() {

        binding.editIV.setOnClickListener(v -> {
            showImagePicker();
        });

        binding.deleteBtn.setOnClickListener(v -> {
            showConverterBottomSheetDialog();
        });

        binding.signOut.setOnClickListener(v -> {

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {


                FirebaseFirestore.getInstance().collection("users")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("userToken", "");

                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .update("onlineStatus", false);

            }

            FirebaseAuth.getInstance().signOut();

            SessionManager.getInstance().setUser(getContext(), null);

            Intent intent = new Intent(getContext(), StartUpActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);

        });

        binding.editProfileCL.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), EditProfileActivity.class));
        });
        binding.notificationCL.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), NotificationActivity.class));
        });
        binding.favoriteCL.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), FavoriteActivity.class));
        });
        binding.walletCL.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), WalletActivity.class));
        });

        binding.orderCL.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), MyOrderActivity.class));
        });
        binding.myTransactionCL.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), MyTransactionsActivity.class));
        });

        binding.editBankCL.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), EditBankDetailActivity.class));
        });
        binding.contactUsCL.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ContactUsActivity.class));
        });
        binding.faqCL.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), FaqActivity.class));
        });
        binding.liveSupportCL.setOnClickListener(v -> {
            checkAlreadyExists();
        });

    }

    String cID = System.currentTimeMillis() + "";
    String type = "0";
    boolean found = false;

    public void checkAlreadyExists() {

        cID = System.currentTimeMillis() + "";

        FirebaseInstances.adminChatCollection
                .whereEqualTo("participants." + "1", true)
                .get()
                .addOnCompleteListener(task -> {

                    final ArrayList<LastMessageModel> list = new ArrayList<>();

                    if (task.isSuccessful()) {

                        for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {

                            LastMessageModel lastMessageModel = snapshot.get("lastMessage", LastMessageModel.class);

                            if (lastMessageModel != null) {

                                lastMessageModel.conversationId = snapshot.getId();

                                list.add(lastMessageModel);
                            }
                        }


                        if (list.size() > 0) {

                            for (int i = 0; i < list.size(); i++) {

                                if (list.get(i).getToID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) || list.get(i).getFromID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                                    cID = list.get(i).conversationId;
                                    found = true;

                                }
                            }

                            if (found) {


                                move(true);
                            } else {
                                move(false);
                            }


                        }else {
                            move(false);
                        }


                    }else {
                        move(false);
                    }
                });


    }

    public void move(boolean found) {
        Intent intent = new Intent(getContext(), AdminChat.class);

        ParcelableChat parcelableChat = new ParcelableChat();

        parcelableChat.setConversationID(cID);

        parcelableChat.setSelectedUserId("1");

        parcelableChat.setTypeStatus("admin");
        parcelableChat.setSelectedUserName("Live Support");

        intent.putExtra("parcelableChat", parcelableChat);
        if (found)
            intent.putExtra("type", "1");
        else
            intent.putExtra("type", "0");
        startActivity(intent);
    }

    private void showImagePicker() {

        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        Permissions.check(getContext()/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {


                imageCapture.showImagePickerDialog(getActivity(), dispatchTakePictureLauncher, galleryIntentLauncher);

            }
        });

    }

    ActivityResultLauncher<Intent> galleryIntentLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {

                    if (result.getData() != null) {

                        Uri imageUri = result.getData().getData();

                        this.imageUri = imageUri;
                        UploadImage();

                    }

                }
            });

    ActivityResultLauncher<Intent> dispatchTakePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {

                    this.imageUri = imageCapture.getImageCapturedUri();
                    UploadImage();

                }
            });

    private void UploadImage() {

        binding.userIV.setImageURI(imageUri);

        showLoader();

        String randomKey = UUID.randomUUID().toString();

        StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("userThumbnail/" + randomKey);

        reference.
                putFile(imageUri).addOnSuccessListener(taskSnapshot -> {

            reference.getDownloadUrl().addOnSuccessListener(uri1 -> {

                hideLoader();

                UserModel userModel = SessionManager.getInstance().getUser(getContext());
                userModel.imageUrl = uri1.toString();
                SessionManager.getInstance().setUser(getContext(), userModel);
                FirebaseFirestore.getInstance().collection("users").document(getUserId()).update("imageUrl", uri1.toString());

                imageUri = null;

            });
        }).addOnFailureListener(e -> {

            hideLoader();

            showErrorAlert(e.getLocalizedMessage());

        });
    }

    private void showConverterBottomSheetDialog() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());

        BottomSheetDialogDeleteAccountBinding dialogTermsBinding = BottomSheetDialogDeleteAccountBinding.inflate(getLayoutInflater());

        bottomSheetDialog.setContentView(dialogTermsBinding.getRoot());

        dialogTermsBinding.deleteBtn.setOnClickListener(v -> {


            if (FirebaseAuth.getInstance().getCurrentUser() != null) {


                FirebaseFirestore.getInstance().collection("users")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("userToken", "");

                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .update("onlineStatus", false);

            }


            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                SessionManager.getInstance().setUser(getContext(), null);

                                Intent intent = new Intent(getContext(), StartUpActivity.class);

                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                startActivity(intent);
                            }
                        }
                    });
            bottomSheetDialog.dismiss();

        });

        dialogTermsBinding.cancelBtn.setOnClickListener(v -> {

            bottomSheetDialog.dismiss();

        });

        bottomSheetDialog.show();
    }


}