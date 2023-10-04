package com.buzzware.monymarket.activities;

import static com.buzzware.monymarket.activities.BaseActivity.getUserId;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anggastudio.spinnerpickerdialog.SpinnerPickerDialog;
import com.bumptech.glide.Glide;
import com.buzzware.monymarket.Models.UserModel;
import com.buzzware.monymarket.R;
import com.buzzware.monymarket.adapters.ProductAdapter;
import com.buzzware.monymarket.classes.Constants;
import com.buzzware.monymarket.classes.SessionManager;
import com.buzzware.monymarket.databinding.ActivityChatBinding;
import com.buzzware.monymarket.databinding.ActivityEditProfileBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    ActivityEditProfileBinding binding;

    String phoneNumber;

    boolean correctPhoneNo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setCarrierPhone();
        setListener();


        setViews();
    }

    private void setCarrierPhone() {
        binding.countryCodePicker.registerCarrierNumberEditText(binding.phoneTV);

        binding.countryCodePicker.setPhoneNumberValidityChangeListener(isValidNumber -> {
            if (isValidNumber) {
                correctPhoneNo = true;
                binding.phoneTV.setTextColor(Color.parseColor("#000000"));
            } else {
                binding.phoneTV.setTextColor(Color.parseColor("#FF0000"));
                correctPhoneNo = false;
            }
        });

        binding.countryCodePicker.registerCarrierNumberEditText(binding.phoneTV);
    }

    private void setViews() {

        binding.nameET.setText(SessionManager.getInstance().getUser(EditProfileActivity.this).firstName);
        binding.lastNameET.setText(SessionManager.getInstance().getUser(EditProfileActivity.this).lastName);
        binding.addressET.setText(SessionManager.getInstance().getUser(EditProfileActivity.this).address);
        binding.emailET.setText(SessionManager.getInstance().getUser(EditProfileActivity.this).email);
        binding.countryCodePicker.setFullNumber(SessionManager.getInstance().getUser(EditProfileActivity.this).userPhone);
        binding.dateTV.setText(SessionManager.getInstance().getUser(EditProfileActivity.this).dateOfBirth);
        String c = SessionManager.getInstance().getUser(EditProfileActivity.this).userCurrency;
        binding.currencySpinner.setSelection(Constants.spinnerCurrency.indexOf(c));
        binding.bioET.setText(SessionManager.getInstance().getUser(EditProfileActivity.this).userBio);
    }

    private void performValidation() {
        if (TextUtils.isEmpty(binding.nameET.getText().toString())) {
            binding.nameET.setError("Field Required");
            return;
        } if (TextUtils.isEmpty(binding.lastNameET.getText().toString())) {
            binding.nameET.setError("Field Required");
            return;
        }if (TextUtils.isEmpty(binding.addressET.getText().toString())) {
            binding.nameET.setError("Field Required");
            return;
        }
        if (TextUtils.isEmpty(binding.phoneTV.getText().toString())) {
            binding.phoneTV.setError("Field Required");
            return;
        }

        if (TextUtils.isEmpty(binding.bioET.getText().toString())) {
            binding.bioET.setError("Field Required");
            return;
        }

        if (!correctPhoneNo) {
            binding.phoneTV.setError("Input valid phone number");
            return;
        }

        performUpdate();
    }

    private void performUpdate() {
        Map<String, Object> userData = new HashMap<>();

        phoneNumber = binding.countryCodePicker.getSelectedCountryCodeWithPlus() + binding.phoneTV.getText().toString();

        userData.put("fullname", binding.nameET.getText().toString()+" "+binding.lastNameET.getText().toString());
        userData.put("firstName", binding.nameET.getText().toString());
        userData.put("lastName", binding.lastNameET.getText().toString());
        userData.put("address", binding.addressET.getText().toString());
        userData.put("userPhone", phoneNumber);
        userData.put("dateOfBirth", binding.dateTV.getText().toString());
        userData.put("userBio", binding.bioET.getText().toString());
        userData.put("userCurrency", binding.currencySpinner.getSelectedItem().toString());


        UserModel userModel = SessionManager.getInstance().getUser(EditProfileActivity.this);

        userModel.fullname = binding.nameET.getText().toString();
        userModel.userPhone = phoneNumber;
        userModel.dateOfBirth = binding.dateTV.getText().toString();
        userModel.userBio = binding.bioET.getText().toString();
        userModel.userCurrency = binding.currencySpinner.getSelectedItem().toString();

        SessionManager.getInstance().setUser(EditProfileActivity.this, userModel);

        FirebaseFirestore.getInstance().collection("users")
                .document(getUserId())
                .update(userData)
                .addOnCompleteListener(task -> {

                    Toast.makeText(EditProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                    getUser(getUserId());
                });
    }

    private void setListener() {

        binding.backIV.setOnClickListener(v -> {
            finish();
        });

        binding.dateTV.setOnClickListener(v -> {

            showDatePicker();

        });


        binding.updateBtn.setOnClickListener(view -> performValidation());
    }

    @SuppressLint("WrongConstant")
    void showDatePicker() {

        final SpinnerPickerDialog spinnerPickerDialog = new SpinnerPickerDialog();
        spinnerPickerDialog.setContext(this);
        spinnerPickerDialog.setTitleText("Date of Birth");
        spinnerPickerDialog.setStyle(R.style.SheetDialog, R.style.DialogTheme);
        spinnerPickerDialog.setAllColor(ContextCompat.getColor(this, R.color.white));
        spinnerPickerDialog.setmTextColor(Color.WHITE);
        spinnerPickerDialog.setArrowButton(true);
        spinnerPickerDialog.setOnDialogListener(new SpinnerPickerDialog.OnDialogListener() {

            @Override
            public void onSetDate(int month, int day, int year) {
                // "  (Month selected is 0 indexed {0 == January})"

                String dateString = (month + 1) + "-" + day + "-" + year;
                int sDay = month;
                int sYear = year;
                int sMonth = (month + 1);

                binding.dateTV.setText(dateString);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onDismiss() {

            }

        });
        spinnerPickerDialog.show(this.getSupportFragmentManager(), "");

    }

    ListenerRegistration snapshotListener=null;

    private void getUser(String uid) {



        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        DocumentReference reference = firebaseFirestore.collection("users").document(uid);

        snapshotListener = reference.addSnapshotListener((value, error) -> {



            UserModel userModel = value.toObject(UserModel.class);

            userModel.userId = uid;

            SessionManager.getInstance().setUser(EditProfileActivity.this, userModel);

            snapshotListener.remove();

            setViews();

        });


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}