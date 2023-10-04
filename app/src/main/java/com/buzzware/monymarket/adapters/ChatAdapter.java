package com.buzzware.monymarket.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.monymarket.activities.ChatActivity;
import com.buzzware.monymarket.databinding.ItemsDesignChatBinding;
import com.buzzware.monymarket.interfaces.CartFragmentCallback;

import java.util.List;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<String> list;

    private Context mContext;

//    CartFragmentCallback cartFragmentCallback;

    int size = 0;

    public ChatAdapter(Context mContext, List<String> list, CartFragmentCallback cartFragmentCallback) {

        this.list = list;

        this.mContext = mContext;

//        this.cartFragmentCallback = cartFragmentCallback;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(ItemsDesignChatBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {



    }


    @Override
    public int getItemCount() {

        return 4;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemsDesignChatBinding binding;


        public ViewHolder(@NonNull ItemsDesignChatBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }

    }

}
