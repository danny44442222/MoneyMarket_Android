package com.buzzware.monymarket.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.monymarket.Models.ProductModel;
import com.buzzware.monymarket.Models.UserModel;
import com.buzzware.monymarket.R;
import com.buzzware.monymarket.activities.ProductDetailActivity;
import com.buzzware.monymarket.classes.SessionManager;
import com.buzzware.monymarket.databinding.ItemsDesignProductBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<ProductModel> list;

    private Context mContext;


    int size = 0;

    public ProductAdapter(Context mContext, List<ProductModel> list) {

        this.list = list;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(ItemsDesignProductBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        ProductModel product = list.get(i);

        String currency = product.proCurrency;

        String[] data;

        if (currency.contains(" ")) {

            data = currency.split(" ");

            currency = data[1];

        }

        viewHolder.binding.youGetTV.setText(product.proQuantity + " " + currency);
        viewHolder.binding.youPayTV.setText(product.proExchnagePrice + " " + product.proexchangeCurr);


        Double d = Double.parseDouble(product.proExchnagePrice + "")/Double.parseDouble(product.proQuantity + "");
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        viewHolder.binding.exTV.setText(product.proexchangeCurr + " " + decimalFormat.format(d) + "/" + currency);


        viewHolder.binding.mainLayout.setOnClickListener(v -> {

            String isFavorite = "no";

            if (SessionManager.getInstance().getUser(mContext).userLikedPro != null
                    && SessionManager.getInstance().getUser(mContext).userLikedPro.contains(list.get(i).proId))
                isFavorite = "yes";
            else
                isFavorite = "no";


            mContext.startActivity(new Intent(mContext, ProductDetailActivity.class)
                    .putExtra("product", list.get(i))
                    .putExtra("isFavorite", isFavorite)
                    .putExtra("type", "other"));

        });


    }


    @Override
    public int getItemCount() {

        return list.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemsDesignProductBinding binding;


        public ViewHolder(@NonNull ItemsDesignProductBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }

    }

}
