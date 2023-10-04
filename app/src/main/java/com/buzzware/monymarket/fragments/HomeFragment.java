package com.buzzware.monymarket.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.monymarket.Models.ProductModel;
import com.buzzware.monymarket.Models.WalletModel;
import com.buzzware.monymarket.R;
import com.buzzware.monymarket.activities.AddFundsActivity;
import com.buzzware.monymarket.activities.FavoriteActivity;
import com.buzzware.monymarket.activities.HomeActivity;
import com.buzzware.monymarket.activities.MyOrderActivity;
import com.buzzware.monymarket.activities.NotificationActivity;
import com.buzzware.monymarket.activities.SeeAllProductActivity;
import com.buzzware.monymarket.activities.WalletActivity;
import com.buzzware.monymarket.adapters.ProductAdapter;
import com.buzzware.monymarket.classes.SessionManager;
import com.buzzware.monymarket.databinding.FragmentHomeBinding;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;

    List<ProductModel> productList;

    Long sumOfAll = 0L;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater);

        setView();
        getWalletList();
        setListener();
        getProductsList("");

        return binding.getRoot();
    }

    private void setView() {

        try {
            if (SessionManager.getInstance().getUser(getContext()).imageUrl != null)
                Glide.with(getContext()).load(SessionManager.getInstance().getUser(getContext()).imageUrl)
                        .apply(new RequestOptions().centerCrop().placeholder(R.drawable.dummy_product_image1))
                        .into(binding.userIV);
            binding.nameTV.setText(SessionManager.getInstance().getUser(getContext()).fullname);

        } catch (Exception e) {

        }


    }

    private void setListener() {

        binding.seeTitleTV.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), SeeAllProductActivity.class));
        });
        binding.favoriteIV.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), FavoriteActivity.class));
        });
        binding.notificationIV.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), NotificationActivity.class));
        });

        binding.searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                getProductsList(binding.searchET.getText().toString().trim());

            }
        });

        binding.showIV.setOnClickListener(v -> {
            binding.hideIV.setVisibility(View.VISIBLE);
            binding.showIV.setVisibility(View.GONE);
            binding.amountTV.setText(sumOfAll + "");
        });

        binding.hideIV.setOnClickListener(v -> {
            binding.hideIV.setVisibility(View.GONE);
            binding.showIV.setVisibility(View.VISIBLE);
            binding.amountTV.setText("Tap To Show Balance");

        });
        binding.addFundBtn.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), AddFundsActivity.class));
        });
        binding.manageWalletBtn.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), WalletActivity.class));
        });


    }

    private void getWalletList() {

        FirebaseFirestore.getInstance().collection("Wallet")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            sumOfAll = 0L;

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                WalletModel walletModel = document.toObject(WalletModel.class);

                                walletModel.walletId = document.getId();

                                if (walletModel.userID.equals(SessionManager.getInstance().getUser(getContext()).userId)) {
                                    sumOfAll = sumOfAll + walletModel.proAmount;
                                }


                            }

                        }
                    }
                });
    }


    private void getProductsList(String searchText) {

        productList = new ArrayList<ProductModel>();

        FirebaseFirestore.getInstance().collection("Products")
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            productList.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                ProductModel product = document.toObject(ProductModel.class);

                                product.proId = document.getId();

                                if (!product.userId.equals(SessionManager.getInstance().getUser(getContext()).userId)) {
                                    if (searchText.equals("")) {
                                        productList.add(product);
                                    } else if (product.proCurrency.toLowerCase().contains(searchText.toLowerCase())
                                            || product.proexchangeCurr.toLowerCase().contains(searchText.toLowerCase())) {
                                        productList.add(product);

                                    }
                                }


                            }

                            if (productList.size() > 0)
                                setProductAdapter();

                        } else {

                        }
                    }
                });
    }


    private void setProductAdapter() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        binding.productRV.setLayoutManager(layoutManager);

        ProductAdapter adapter = new ProductAdapter(getContext(), productList);

        binding.productRV.setItemAnimator(new DefaultItemAnimator());

        binding.productRV.setAdapter(adapter);

    }

    @Override
    public void onResume() {
        super.onResume();

        getProductsList("");
    }
}