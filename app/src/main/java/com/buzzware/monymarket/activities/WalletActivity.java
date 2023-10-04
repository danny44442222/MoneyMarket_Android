package com.buzzware.monymarket.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.buzzware.monymarket.Models.ProductModel;
import com.buzzware.monymarket.Models.WalletModel;
import com.buzzware.monymarket.adapters.CartAdapter;
import com.buzzware.monymarket.adapters.HistoryAdapter;
import com.buzzware.monymarket.adapters.MyCurrancyAdapter;
import com.buzzware.monymarket.adapters.ProductAdapter;
import com.buzzware.monymarket.classes.SessionManager;
import com.buzzware.monymarket.databinding.ActivityWalletBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class WalletActivity extends AppCompatActivity {

    ActivityWalletBinding binding;

    List<WalletModel> walletModelList;

    Long sumOfAll = 0L;

    List<ProductModel> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityWalletBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWalletList();
        getProductsList();

        setListener();

    }

    private void setListener() {

        binding.convertBtn.setOnClickListener(v -> {
            startActivity(new Intent(WalletActivity.this, ConverterActivity.class));
        });

        binding.backIV.setOnClickListener(v -> {
            finish();
        });

        binding.addFundBtn.setOnClickListener(v -> {
            startActivity(new Intent(WalletActivity.this, AddFundsActivity.class));
        });

    }

    private void getWalletList() {

        walletModelList = new ArrayList<WalletModel>();

        FirebaseFirestore.getInstance().collection("Wallet")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            walletModelList.clear();
                            sumOfAll=0L;

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                WalletModel walletModel = document.toObject(WalletModel.class);

                                walletModel.walletId = document.getId();

                                if (walletModel.userID.equals(SessionManager.getInstance().getUser(WalletActivity.this).userId)) {
                                    sumOfAll = sumOfAll + walletModel.proAmount;
                                    walletModelList.add(walletModel);
                                }


                            }

                            binding.userNameTV.setText(SessionManager.getInstance().getUser(WalletActivity.this).fullname);
                            binding.amountTV.setText(sumOfAll+"");
                            setHistoryAdapter();

                        } else {

                        }
                    }
                });
    }

    private void setHistoryAdapter() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(WalletActivity.this, LinearLayoutManager.VERTICAL, false);

        binding.historyRV.setLayoutManager(layoutManager);

        HistoryAdapter adapter = new HistoryAdapter(WalletActivity.this, walletModelList);

        binding.historyRV.setItemAnimator(new DefaultItemAnimator());

        binding.historyRV.setAdapter(adapter);

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

                                if (product.userId.equals(SessionManager.getInstance().getUser(WalletActivity.this).userId))
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

        LinearLayoutManager layoutManager = new LinearLayoutManager(WalletActivity.this, LinearLayoutManager.VERTICAL, false);

        binding.myCurrencyRV.setLayoutManager(layoutManager);

        MyCurrancyAdapter adapter = new MyCurrancyAdapter(WalletActivity.this, productList);

        binding.myCurrencyRV.setItemAnimator(new DefaultItemAnimator());

        binding.myCurrencyRV.setAdapter(adapter);

    }


    @Override
    protected void onResume() {
        super.onResume();
        getWalletList();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}