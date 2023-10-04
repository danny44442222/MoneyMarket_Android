package com.buzzware.monymarket.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.buzzware.monymarket.Models.OrderModel;
import com.buzzware.monymarket.Models.ProductModel;
import com.buzzware.monymarket.Models.WalletModel;
import com.buzzware.monymarket.R;
import com.buzzware.monymarket.adapters.HistoryAdapter;
import com.buzzware.monymarket.adapters.MyAdsAdapter;
import com.buzzware.monymarket.adapters.MyCurrancyAdapter;
import com.buzzware.monymarket.adapters.OrderAdapter;
import com.buzzware.monymarket.classes.SessionManager;
import com.buzzware.monymarket.databinding.ActivityMyOrderBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyOrderActivity extends AppCompatActivity {

    ActivityMyOrderBinding binding;

    List<ProductModel> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMyOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();


    }

    private void setListener() {

        binding.backIV.setOnClickListener(v -> {
            finish();
        });

    }

    private void getProductsList() {

        productList = new ArrayList<ProductModel>();


        FirebaseFirestore.getInstance().collection("Products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            productList.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                ProductModel product = document.toObject(ProductModel.class);

                                product.proId = document.getId();

                                if (product.userId.equals(SessionManager.getInstance().getUser(MyOrderActivity.this).userId))
                                {
                                    productList.add(product);
                                }

                            }

                            if (productList.size() > 0){
                                setMyCurrencyAdapter();

                            }else {
                            }


                        } else {

                        }
                    }
                });
    }


    private void setMyCurrencyAdapter() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(MyOrderActivity.this, LinearLayoutManager.VERTICAL, false);

        binding.orderRV.setLayoutManager(layoutManager);

        MyAdsAdapter adapter = new MyAdsAdapter(MyOrderActivity.this, productList);

        binding.orderRV.setItemAnimator(new DefaultItemAnimator());

        binding.orderRV.setAdapter(adapter);

    }


    @Override
    protected void onResume() {
        super.onResume();
        getProductsList();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}