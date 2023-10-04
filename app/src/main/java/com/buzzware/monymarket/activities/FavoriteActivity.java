package com.buzzware.monymarket.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.buzzware.monymarket.Models.ProductModel;
import com.buzzware.monymarket.R;
import com.buzzware.monymarket.adapters.ProductAdapter;
import com.buzzware.monymarket.classes.SessionManager;
import com.buzzware.monymarket.databinding.ActivityFavoriteBinding;
import com.buzzware.monymarket.databinding.ActivitySeeAllProductBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    ActivityFavoriteBinding binding;

    List<ProductModel> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityFavoriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListener();
        getProductsList();
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

                                if (SessionManager.getInstance().getUser(FavoriteActivity.this).userLikedPro != null
                                        && SessionManager.getInstance().getUser(FavoriteActivity.this).userLikedPro.contains(product.proId))
                                    productList.add(product);


                            }

                            setProductAdapter();

                        } else {

                        }
                    }
                });
    }


    private void setProductAdapter() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(FavoriteActivity.this, LinearLayoutManager.VERTICAL, false);

        binding.productRV.setLayoutManager(layoutManager);

        ProductAdapter adapter = new ProductAdapter(FavoriteActivity.this, productList);

        binding.productRV.setItemAnimator(new DefaultItemAnimator());

        binding.productRV.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProductsList();
    }
}