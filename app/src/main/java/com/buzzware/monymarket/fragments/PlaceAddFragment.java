package com.buzzware.monymarket.fragments;

import static com.buzzware.monymarket.activities.BaseActivity.getUserId;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buzzware.monymarket.Models.ProductModel;
import com.buzzware.monymarket.Models.UserModel;
import com.buzzware.monymarket.activities.MyOrderActivity;
import com.buzzware.monymarket.activities.SignUpActivity;
import com.buzzware.monymarket.classes.AddPlaceEvent;
import com.buzzware.monymarket.classes.ImageCapture;
import com.buzzware.monymarket.classes.SessionManager;
import com.buzzware.monymarket.databinding.FragmentPlaceAddBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.williammora.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;

import java.util.UUID;

public class PlaceAddFragment extends BaseFragment {


    FragmentPlaceAddBinding binding;

    String imageURL = "";

    Uri imageUri = null;

    ImageCapture imageCapture = new ImageCapture();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPlaceAddBinding.inflate(inflater);

        setListener();

        return binding.getRoot();

    }

    private void setListener() {

        binding.confirmBtn.setOnClickListener(v -> {

            if (isValid()) {
                ProductModel productModel = new ProductModel();
                productModel.proTitle = binding.nameET.getText().toString().trim();
                productModel.proCurrency = binding.currencySpinner.getSelectedItem().toString();
                productModel.proexchangeCurr = binding.exCurrencySpinner.getSelectedItem().toString();
                productModel.proExchnagePrice = Long.parseLong(binding.exPriceET.getText().toString().trim());
                productModel.proQuantity = Long.parseLong(binding.adQuantityET.getText().toString().trim());
                productModel.proDescription = binding.descriptionET.getText().toString().trim();
                productModel.timeStamp = System.currentTimeMillis();
                productModel.userId = SessionManager.getInstance().getUser(getContext()).userId;
                //  productModel.proImageUrls=imageURL;

                FirebaseFirestore.getInstance().collection("Products")
                        .document()
                        .set(productModel);

                showMessage("Product has been added!");
                EventBus.getDefault().post(new AddPlaceEvent());

               // startActivity(new Intent(getContext(), MyOrderActivity.class));


                binding.nameET.setText("");
                binding.adQuantityET.setText("");
                binding.exPriceET.setText("");
                binding.descriptionET.setText("");
                imageURL = "";

            }

        });

//        binding.addImageBtn.setOnClickListener(v -> {
//            showImagePicker();
//        });

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

        showLoader();

        String randomKey = UUID.randomUUID().toString();

        StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("userThumbnail/" + randomKey);

        reference.
                putFile(imageUri).addOnSuccessListener(taskSnapshot -> {

            reference.getDownloadUrl().addOnSuccessListener(uri1 -> {

                hideLoader();

//                UserModel userModel = SessionManager.getInstance().getUser(getContext());
//                userModel.imageUrl = uri1.toString();
//                SessionManager.getInstance().setUser(getContext(), userModel);
//                FirebaseFirestore.getInstance().collection("users").document(getUserId()).update("imageUrl", uri1.toString());

                imageURL = uri1.toString();

            });
        }).addOnFailureListener(e -> {

            hideLoader();

            showErrorAlert(e.getLocalizedMessage());

        });
    }


    private boolean isValid() {

        boolean check = false;

        if (binding.nameET.getText().toString().trim().isEmpty()) {

            showMessage("Name Required");

            return check;

        }

        if (binding.exPriceET.getText().toString().trim().isEmpty()) {

            showMessage("Price Required!");

            return check;

        }

        if (binding.adQuantityET.getText().toString().trim().isEmpty()) {

            showMessage("Quantity Required!");

            return check;

        }

        if (binding.descriptionET.getText().toString().isEmpty()) {

            showMessage("Description Required!");

            return check;

        }

//        if (imageURL.isEmpty()) {
//
//            showMessage("Product Image Required!");
//
//            return check;
//
//        }

        check = true;

        return check;

    }

    private void showMessage(String message) {

        Snackbar.with(getContext())
                .text(message)
                .show(getActivity());

    }


}