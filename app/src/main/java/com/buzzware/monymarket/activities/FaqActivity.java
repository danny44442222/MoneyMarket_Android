package com.buzzware.monymarket.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.buzzware.monymarket.R;
import com.buzzware.monymarket.adapters.FaqAdapter;
import com.buzzware.monymarket.adapters.HistoryAdapter;
import com.buzzware.monymarket.databinding.ActivityFaqBinding;

import java.util.ArrayList;
import java.util.List;

public class FaqActivity extends AppCompatActivity {

    ActivityFaqBinding binding;

    List<String> question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFaqBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        addDataInList();

    }

    private void addDataInList() {


        question=new ArrayList<>();
        question.add("Is MonyMarket secure ?");
        question.add("How does MM work ?");
        question.add("where/ who can I buy from?");
        question.add("Where/ who can I sell to?");
        question.add("How do I fund Wallet?");
        question.add("How much money can I sell or buy?");
        question.add("Can I Increase my daily limit?");
        question.add("Do I need to verify myself before being able to transact?");
        question.add("How do I delete/deactivate my account?");
        question.add("How do I stop receiving notifications?");
        question.add("How to complete transactions (buying or selling)");
        question.add("How to use this app safely.");



        LinearLayoutManager layoutManager = new LinearLayoutManager(FaqActivity.this, LinearLayoutManager.VERTICAL, false);

        binding.mainRV.setLayoutManager(layoutManager);

        FaqAdapter adapter = new FaqAdapter(FaqActivity.this, question);

        binding.mainRV.setItemAnimator(new DefaultItemAnimator());

        binding.mainRV.setAdapter(adapter);
    }
}