package com.buzzware.monymarket.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.monymarket.R;
import com.buzzware.monymarket.activities.ChatActivity;
import com.buzzware.monymarket.databinding.ItemsDesignChatBinding;
import com.buzzware.monymarket.databinding.ItemsDesignChatListBinding;
import com.buzzware.monymarket.interfaces.CartFragmentCallback;

import java.util.List;


public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private List<String> list;

    private Context mContext;

//    CartFragmentCallback cartFragmentCallback;

    int size = 0;

    public ChatListAdapter(Context mContext, List<String> list, CartFragmentCallback cartFragmentCallback) {

        this.list = list;

        this.mContext = mContext;

//        this.cartFragmentCallback = cartFragmentCallback;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(ItemsDesignChatListBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        viewHolder.binding.getRoot().setOnClickListener(view -> mContext.startActivity(new Intent(mContext, ChatActivity.class)));
        if (i % 2 == 0)
            viewHolder.binding.productIV.setImageResource(R.drawable.user_dummy_profile);
        if (i % 3 == 0)
            viewHolder.binding.productIV.setImageResource(R.drawable.dummy_product_image1);

    }


    @Override
    public int getItemCount() {

        return 7;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemsDesignChatListBinding binding;


        public ViewHolder(@NonNull ItemsDesignChatListBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }

    }

}
