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
import com.buzzware.monymarket.databinding.ItemsDesignFaqBinding;
import com.buzzware.monymarket.databinding.ItemsDesignMyAdsBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.ViewHolder> {

    private List<String> list;

    private Context mContext;


    int size = 0;

    public FaqAdapter(Context mContext, List<String> list) {

        this.list = list;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(ItemsDesignFaqBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        viewHolder.binding.nameTV.setText(list.get(i));

        viewHolder.binding.innerCL.setOnClickListener(v -> {

            if (viewHolder.binding.statusTV.getText().toString().equals("hide")){
                viewHolder.binding.answerTV.setVisibility(View.VISIBLE);
                viewHolder.binding.statusTV.setText("show");
            }

            else {

                viewHolder.binding.answerTV.setVisibility(View.GONE);
                viewHolder.binding.statusTV.setText("hide");
            }
        });

    }


    @Override
    public int getItemCount() {

        return list.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemsDesignFaqBinding binding;


        public ViewHolder(@NonNull ItemsDesignFaqBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }

    }

}
