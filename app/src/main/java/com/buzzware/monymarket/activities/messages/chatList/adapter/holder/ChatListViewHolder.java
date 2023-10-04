package com.buzzware.monymarket.activities.messages.chatList.adapter.holder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.monymarket.databinding.ItemsDesignChatListBinding;

public class ChatListViewHolder extends RecyclerView.ViewHolder {

    public ItemsDesignChatListBinding binding;

    public ChatListViewHolder(@NonNull ItemsDesignChatListBinding binding) {

        super(binding.getRoot());

        this.binding = binding;
    }
}
