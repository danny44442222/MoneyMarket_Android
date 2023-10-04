package com.buzzware.monymarket.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.buzzware.monymarket.Models.OrderModel;
import com.buzzware.monymarket.Models.ProductModel;
import com.buzzware.monymarket.R;
import com.buzzware.monymarket.adapters.MyAdsAdapter;
import com.buzzware.monymarket.adapters.MyTransactionAdapter;
import com.buzzware.monymarket.classes.SessionManager;
import com.buzzware.monymarket.databinding.ActivityMyTransactionsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyTransactionsActivity extends AppCompatActivity {

    ActivityMyTransactionsBinding binding;

    List<OrderModel> orderModelList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityMyTransactionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getProductsList();
        setListener();


    }

    private void setListener() {

        binding.backIV.setOnClickListener(v -> {
            finish();
        });

    }

    private void getProductsList() {

        orderModelList = new ArrayList<OrderModel>();


        FirebaseFirestore.getInstance().collection("Order")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            orderModelList.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                OrderModel product = document.toObject(OrderModel.class);

                                product.orderId = document.getId();

                                if (product.userID.equals(SessionManager.getInstance().getUser(MyTransactionsActivity.this).userId))
                                {
                                    orderModelList.add(product);
                                }

                            }

                            if (orderModelList.size() > 0){
                                setMyCurrencyAdapter();

                            }else {
                            }


                        } else {

                        }
                    }
                });
    }


    private void setMyCurrencyAdapter() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(MyTransactionsActivity.this, LinearLayoutManager.VERTICAL, false);

        binding.orderRV.setLayoutManager(layoutManager);

        MyTransactionAdapter adapter = new MyTransactionAdapter(MyTransactionsActivity.this, orderModelList);

        binding.orderRV.setItemAnimator(new DefaultItemAnimator());

        binding.orderRV.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}