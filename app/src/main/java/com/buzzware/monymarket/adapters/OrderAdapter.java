package com.buzzware.monymarket.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.monymarket.Models.OrderModel;
import com.buzzware.monymarket.Models.WalletModel;
import com.buzzware.monymarket.R;
import com.buzzware.monymarket.activities.ConverterActivity;
import com.buzzware.monymarket.databinding.ItemsDesignHistoryBinding;
import com.buzzware.monymarket.databinding.ItemsDesignOrderBinding;

import java.util.List;


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private List<OrderModel> list;

    private Context mContext;


    int size = 0;

    public OrderAdapter(Context mContext, List<OrderModel> list) {

        this.list = list;

        this.mContext = mContext;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(ItemsDesignOrderBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        OrderModel orderModel = list.get(i);

        Glide.with(mContext).load(orderModel.proImage)
                .apply(new RequestOptions().centerCrop().placeholder(R.drawable.dummy_product_image1))
                .into(viewHolder.binding.productIV);

        viewHolder.binding.nameTV.setText(orderModel.proName);
        viewHolder.binding.descriptionTV.setText(orderModel.proAmount + " " + orderModel.proCurrency);
        viewHolder.binding.statusTV.setText(orderModel.proStatus);

    }


    @Override
    public int getItemCount() {

        return list.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemsDesignOrderBinding binding;


        public ViewHolder(@NonNull ItemsDesignOrderBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }

    }

}
