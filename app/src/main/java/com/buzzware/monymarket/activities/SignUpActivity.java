package com.buzzware.monymarket.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.anggastudio.spinnerpickerdialog.SpinnerPickerDialog;
import com.buzzware.monymarket.Models.CountryCodeWithCurrencyModel;
import com.buzzware.monymarket.Models.UserModel;
import com.buzzware.monymarket.R;
import com.buzzware.monymarket.classes.Constants;
import com.buzzware.monymarket.classes.SessionManager;
import com.buzzware.monymarket.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;
import com.williammora.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class SignUpActivity extends BaseActivity {

    ActivitySignUpBinding binding;

    private FirebaseAuth mAuth;

    public FirebaseDatabase rootNode;

    public DatabaseReference mDatabase;

    public String currentSelectedCCPCountry="";
    public String currentSelectedCurrency="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setView();

        inIt();

        setListener();

    }


    private void setView() {

        binding.includeBar.titleTV.setText("Create your Account");

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        }

        binding.ccp.registerCarrierNumberEditText(binding.phoneNumberET);

        binding.currencySpinner.setSelection(Constants.spinnerCurrency.indexOf("USD"));


    }

    private void inIt() {

        rootNode = FirebaseDatabase.getInstance();

        mDatabase = rootNode.getReference("users");

        mAuth = FirebaseAuth.getInstance();

    }

    private void setListener() {

        binding.includeBar.backIV.setOnClickListener(v -> {

            finish();

        });

        binding.loginBtn.setOnClickListener(v -> {

            if (isValid())
                signUpUserNow();
        });


        binding.signInTV.setOnClickListener(v -> {

            finish();

        });

        binding.dateTV.setOnClickListener(v -> {

            showDatePicker();

        });

        binding.ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {

                currentSelectedCCPCountry=binding.ccp.getSelectedCountryNameCode();
                currentSelectedCurrency="-1";
                for(int i=0;i<Constants.countryCodeWithCurrency.size();i++){
                    if(Constants.countryCodeWithCurrency.get(i).code.equalsIgnoreCase(currentSelectedCCPCountry)){
                        currentSelectedCurrency=Constants.countryCodeWithCurrency.get(i).currency;
                    }
                }

                if(currentSelectedCurrency!="-1"){
                    binding.currencySpinner.setSelection(Constants.spinnerCurrency.indexOf(currentSelectedCurrency));
                }else{
                    binding.currencySpinner.setSelection(Constants.spinnerCurrency.indexOf("USD"));
                }


            }
        });

    }

    private void signUpUserNow() {

        showLoader();

        UserModel user = new UserModel();

        user.fullname = binding.nameET.getText().toString()+" "+binding.lastNameET.getText().toString();
        user.firstName = binding.nameET.getText().toString().trim();
        user.lastName = binding.lastNameET.getText().toString().trim();
        user.email = binding.emailET.getText().toString().trim();
        user.userPhone = binding.ccp.getFullNumberWithPlus();
        user.dateOfBirth = binding.dateTV.getText().toString().trim();
        user.userCurrency = binding.currencySpinner.getSelectedItem().toString();
        user.address = binding.addressET.getText().toString().trim();
        user.password = binding.passwordET.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(user.email, user.password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    user.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    addUserDetails(user);

                } else {

                    hideLoader();

                    showMessage(task.getException().getLocalizedMessage() + " Or Try to Login!");

                }

            }
        });


    }

    private void addUserDetails(UserModel userModel) {

        FirebaseFirestore.getInstance().collection("users")
                .document(userModel.userId)
                .set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

               hideLoader();

                if (task.isSuccessful()) {

                    showMessage("Successfully Sign up!");
                    if (FirebaseAuth.getInstance().getCurrentUser() != null)
                        FirebaseFirestore.getInstance().collection("users")
                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("userToken", "");


                    FirebaseAuth.getInstance().signOut();
                    SessionManager.getInstance().setUser(SignUpActivity.this, null);
                    Intent intent = new Intent(SignUpActivity.this, StartUpActivity.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
//                    SessionManager.getInstance().setUser(SignUpActivity.this, userModel);
//
//
//
//                    Intent intent = new Intent(SignUpActivity.this, AddBankDetailActivity.class);
//
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//
//                    startActivity(intent);
//
//                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


                } else {
                    showMessage(task.getException().getLocalizedMessage());

                }

            }
        });

    }


    private boolean isValid() {

        boolean check = false;

        if (binding.nameET.getText().toString().trim().isEmpty()) {

            showMessage("First Name Required");

            return check;

        }

        if (binding.lastNameET.getText().toString().trim().isEmpty()) {

            showMessage("Last Name Required");

            return check;

        }

        if (binding.emailET.getText().toString().trim().isEmpty()) {

            showMessage("Email Required!");

            return check;

        }

        if (binding.ccp.getFullNumberWithPlus().isEmpty()) {

            showMessage("Phone Number Required!");

            return check;

        }

        if (binding.dateTV.getText().toString().isEmpty()) {

            showMessage("Date of Birth Required!");

            return check;

        }


        if (binding.addressET.getText().toString().trim().isEmpty()) {

            showMessage("Address Required!");

            return check;

        }
        if (binding.passwordET.getText().toString().trim().isEmpty()) {

            showMessage("Password Required!");

            return check;

        }

        check = true;

        return check;

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

                String dateString = (month + 1) + "-" + day + "-" + year;

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

    private void showMessage(String message) {

        Snackbar.with(SignUpActivity.this)
                .text(message)
                .show(SignUpActivity.this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}