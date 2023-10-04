package com.buzzware.monymarket.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.monymarket.Models.OrderModel;
import com.buzzware.monymarket.Models.ProductModel;
import com.buzzware.monymarket.databinding.ItemsDesignMyAdsBinding;
import com.buzzware.monymarket.databinding.ItemsDesignMyTransactionBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class MyTransactionAdapter extends RecyclerView.Adapter<MyTransactionAdapter.ViewHolder> {

    private List<OrderModel> list;

    private Context mContext;


    int size = 0;

    public MyTransactionAdapter(Context mContext, List<OrderModel> list) {

        this.list = list;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(ItemsDesignMyTransactionBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        OrderModel product = list.get(i);

        viewHolder.binding.nameTV.setText(product.proName);

        viewHolder.binding.descriptionTV.setText(product.proAmount+" "+product.proCurrency);

        viewHolder.binding.statusTV.setText(product.proStatus);



    }



    @Override
    public int getItemCount() {

        return list.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemsDesignMyTransactionBinding binding;


        public ViewHolder(@NonNull ItemsDesignMyTransactionBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }

    }

}
