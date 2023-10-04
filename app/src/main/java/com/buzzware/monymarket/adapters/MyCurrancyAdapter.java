package com.buzzware.monymarket.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.monymarket.Models.ProductModel;
import com.buzzware.monymarket.Models.UserModel;
import com.buzzware.monymarket.R;
import com.buzzware.monymarket.activities.PreviewAndWithdrawActivity;
import com.buzzware.monymarket.activities.ProductDetailActivity;
import com.buzzware.monymarket.classes.SessionManager;
import com.buzzware.monymarket.databinding.ItemsDesignMyCurrencyBinding;
import com.buzzware.monymarket.databinding.ItemsDesignProductBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class MyCurrancyAdapter extends RecyclerView.Adapter<MyCurrancyAdapter.ViewHolder> {

    private List<ProductModel> list;

    private Context mContext;


    int size = 0;

    public MyCurrancyAdapter(Context mContext, List<ProductModel> list) {

        this.list = list;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(ItemsDesignMyCurrencyBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        ProductModel product = list.get(i);

        viewHolder.binding.nameTV.setText(product.proCurrency);

        viewHolder.binding.amountTV.setText(product.proQuantity+"");

        viewHolder.binding.withDrawBtn.setOnClickListener(v->{
            mContext.startActivity(new Intent(mContext, PreviewAndWithdrawActivity.class)
                    .putExtra("product",product));
        });

        }


        @Override
        public int getItemCount () {

            return list.size();

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ItemsDesignMyCurrencyBinding binding;


            public ViewHolder(@NonNull ItemsDesignMyCurrencyBinding binding) {
                super(binding.getRoot());

                this.binding = binding;

            }

        }

    }
