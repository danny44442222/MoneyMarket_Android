package com.buzzware.monymarket.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.buzzware.monymarket.Models.BankDetailModel;
import com.buzzware.monymarket.classes.SessionManager;
import com.buzzware.monymarket.databinding.ActivityAddBankDetailBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddBankDetailActivity extends AppCompatActivity {

    ActivityAddBankDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddBankDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListener();


    }

    private void setListener() {

        binding.skipTV.setOnClickListener(v -> {



            FirebaseFirestore.getInstance().collection("users").document(SessionManager.getInstance().getUser(AddBankDetailActivity.this).userId)
                    .update("stripeStatus","false");

            Intent intent = new Intent(AddBankDetailActivity.this, HomeActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });

        binding.addNowBtn.setOnClickListener(v -> {
            if (valid()) {
                addBankDetail();
            }
        });

    }

    private void addBankDetail() {

        BankDetailModel bankDetailModel = new BankDetailModel();
        bankDetailModel.userID= SessionManager.getInstance().getUser(AddBankDetailActivity.this).userId;
        bankDetailModel.bankName=binding.nameET.getText().toString();
        bankDetailModel.accountName=binding.holderET.getText().toString();
        bankDetailModel.ibanNumber=binding.accountET.getText().toString();



        FirebaseFirestore.getInstance().collection("Bank Detail").add(bankDetailModel);

        FirebaseFirestore.getInstance().collection("users").document(SessionManager.getInstance().getUser(AddBankDetailActivity.this).userId)
                .update("stripeStatus","active");

        Intent intent = new Intent(AddBankDetailActivity.this, HomeActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    private boolean valid() {

        if (binding.nameET.getText().toString().trim().isEmpty()) {
            Toast.makeText(AddBankDetailActivity.this, "Please! Enter Bank Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.holderET.getText().toString().trim().isEmpty()) {
            Toast.makeText(AddBankDetailActivity.this, "Please! Enter Account Holder Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.accountET.getText().toString().trim().isEmpty()) {
            Toast.makeText(AddBankDetailActivity.this, "Please! Enter Account Number / IBAN", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(AddBankDetailActivity.this, HomeActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}