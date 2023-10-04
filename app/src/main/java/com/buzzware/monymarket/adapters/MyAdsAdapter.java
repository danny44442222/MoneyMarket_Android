package com.buzzware.monymarket.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.monymarket.Models.ProductModel;
import com.buzzware.monymarket.activities.EditAdActivity;
import com.buzzware.monymarket.activities.ProductDetailActivity;
import com.buzzware.monymarket.classes.Constants;
import com.buzzware.monymarket.classes.SessionManager;
import com.buzzware.monymarket.databinding.ItemsDesignMyAdsBinding;
import com.buzzware.monymarket.databinding.ItemsDesignMyCurrencyBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class MyAdsAdapter extends RecyclerView.Adapter<MyAdsAdapter.ViewHolder> {

    private List<ProductModel> list;

    private Context mContext;


    int size = 0;

    public MyAdsAdapter(Context mContext, List<ProductModel> list) {

        this.list = list;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(ItemsDesignMyAdsBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        ProductModel product = list.get(i);

        viewHolder.binding.nameTV.setText(product.proTitle);

        viewHolder.binding.descriptionTV.setText("Remaining " + product.proQuantity + " " + product.proCurrency);

        String proQty = product.proQuantity + "";

//        if (proQty.equals("0") || proQty.equals("0.0")) {
//            viewHolder.binding.deleteBtn.setVisibility(View.VISIBLE);
//        } else {
//            viewHolder.binding.deleteBtn.setVisibility(View.INVISIBLE);
//        }

        viewHolder.binding.deleteBtn.setOnClickListener(v -> {

            FirebaseFirestore.getInstance().collection("Products").document(product.proId).delete();
            removeAt(i);


        });
        viewHolder.binding.editBtn.setOnClickListener(v -> {

            Constants.editProductModel=list.get(i);
            mContext.startActivity(new Intent(mContext, EditAdActivity.class));


        });

        viewHolder.binding.mainLayout.setOnClickListener(v -> {

            String isFavorite = "no";

            if (SessionManager.getInstance().getUser(mContext).userLikedPro != null
                    && SessionManager.getInstance().getUser(mContext).userLikedPro.contains(list.get(i).proId))
                isFavorite="yes";
            else
                isFavorite="no";


            mContext.startActivity(new Intent(mContext, ProductDetailActivity.class)
                    .putExtra("product", list.get(i))
                    .putExtra("isFavorite", isFavorite)
                    .putExtra("type", "my"));


        });


    }

    public void removeAt(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }

    @Override
    public int getItemCount() {

        return list.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemsDesignMyAdsBinding binding;


        public ViewHolder(@NonNull ItemsDesignMyAdsBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }

    }

}
