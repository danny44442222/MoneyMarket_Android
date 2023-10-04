package com.buzzware.monymarket.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.buzzware.monymarket.Models.TranferDataModel;
import com.buzzware.monymarket.databinding.ActivityReciptBinding;

public class ReceiptActivity extends AppCompatActivity {

    ActivityReciptBinding binding;

    TranferDataModel tranferDataModel = new TranferDataModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityReciptBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getDataFromIntent();

    }

    private void getDataFromIntent() {

        Intent intent = getIntent();

        tranferDataModel = intent.getParcelableExtra("transferData");

        setListener();
        setView();

    }

    private void setView() {

        binding.bankNameTV.setText(tranferDataModel.bankName);
        binding.ibanNumberTV.setText(tranferDataModel.ibanNumber);
        binding.currencyIdTV.setText(tranferDataModel.transferCurrency);
        binding.amountTV.setText(tranferDataModel.tranferAmount);
        binding.totalTV.setText(tranferDataModel.tranferAmount + " " + tranferDataModel.transferCurrency);

    }

    private void setListener() {

        binding.backIV.setOnClickListener(v -> {
            finish();
        });
        binding.doneBtn.setOnClickListener(v -> {
            finish();
        });

    }

}