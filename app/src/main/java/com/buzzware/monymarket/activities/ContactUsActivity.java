package com.buzzware.monymarket.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.buzzware.monymarket.R;
import com.buzzware.monymarket.databinding.ActivityContactUsBinding;

public class ContactUsActivity extends AppCompatActivity {

    ActivityContactUsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityContactUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setView();
    }

    private void setView() {
        binding.backIV.setOnClickListener(view -> {
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}