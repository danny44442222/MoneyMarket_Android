package com.buzzware.monymarket.activities;

import static com.buzzware.monymarket.activities.BaseActivity.getUserId;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import com.buzzware.monymarket.databinding.ActivityStartUpBinding;


public class StartUpActivity extends AppCompatActivity {

    ActivityStartUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getUserId().isEmpty()) {

            startActivity(new Intent(StartUpActivity.this, HomeActivity.class));
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        } else {

            binding = ActivityStartUpBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            setView();

            setListener();

        }

    }

    private void setView() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        }

    }


    private void setListener() {

        binding.signInTV.setOnClickListener(v -> {

            startActivity(new Intent(StartUpActivity.this, LoginActivity.class));

        });


        binding.signUpTv.setOnClickListener(v -> {

            startActivity(new Intent(StartUpActivity.this, SignUpActivity.class));

        });

    }
}