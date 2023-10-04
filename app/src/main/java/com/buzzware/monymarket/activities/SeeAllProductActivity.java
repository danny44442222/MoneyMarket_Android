package com.buzzware.monymarket.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.buzzware.monymarket.Models.ProductModel;
import com.buzzware.monymarket.adapters.ProductAdapter;
import com.buzzware.monymarket.classes.SessionManager;
import com.buzzware.monymarket.databinding.ActivitySeeAllProductBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SeeAllProductActivity extends AppCompatActivity {

    ActivitySeeAllProductBinding binding;
    List<ProductModel> productList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivitySeeAllProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListener();

        getProductsList();

    }

    private void setListener() {

        binding.backIV.setOnClickListener(v -> {
            finish();
        });

        binding.favoriteIV.setOnClickListener(v -> {
            startActivity(new Intent(SeeAllProductActivity.this, FavoriteActivity.class));
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

                                if (!product.userId.equals(SessionManager.getInstance().getUser(SeeAllProductActivity.this).userId))
                                    productList.add(product);


                            }

                            if (productList.size() > 0)
                                setProductAdapter();

                        } else {

                        }
                    }
                });
    }


    private void setProductAdapter() {

//        LinearLayoutManager layoutManager = new GridLayoutManager(SeeAllProductActivity.this, 2, RecyclerView.VERTICAL, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(SeeAllProductActivity.this, LinearLayoutManager.VERTICAL, false);
        binding.productRV.setLayoutManager(layoutManager);

        ProductAdapter adapter = new ProductAdapter(SeeAllProductActivity.this, productList);

        binding.productRV.setItemAnimator(new DefaultItemAnimator());

        binding.productRV.setAdapter(adapter);

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}