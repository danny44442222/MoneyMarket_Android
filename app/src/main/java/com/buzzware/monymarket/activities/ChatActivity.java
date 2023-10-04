package com.buzzware.monymarket.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.monymarket.adapters.ChatAdapter;
import com.buzzware.monymarket.adapters.ProductAdapter;
import com.buzzware.monymarket.databinding.ActivityChatBinding;
import com.buzzware.monymarket.databinding.ActivitySeeAllProductBinding;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setChatAdapter();

        setListener();

    }

    private void setListener() {

        binding.backIV.setOnClickListener(v -> {
            finish();
        });

    }


    private void setChatAdapter() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this, RecyclerView.VERTICAL, false);

        binding.chatRV.setLayoutManager(layoutManager);

        ChatAdapter adapter = new ChatAdapter(ChatActivity.this, null, null);

        binding.chatRV.setItemAnimator(new DefaultItemAnimator());

        binding.chatRV.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}