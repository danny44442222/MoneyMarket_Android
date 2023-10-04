package com.buzzware.monymarket.activities.messages.chatList.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.monymarket.R;
import com.buzzware.monymarket.activities.messages.chat.Chat;
import com.buzzware.monymarket.activities.messages.chat.mo.ParcelableChat;
import com.buzzware.monymarket.activities.messages.chatList.adapter.holder.ChatListViewHolder;
import com.buzzware.monymarket.activities.messages.chatList.mo.ConversationModel;
import com.buzzware.monymarket.databinding.ItemsDesignChatListBinding;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListViewHolder> {

    Activity c;

    List<ConversationModel> conversations;

    public ChatListAdapter(List<ConversationModel> conversations, Activity c) {

        this.c = c;

        this.conversations = conversations;

    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemsDesignChatListBinding binding = ItemsDesignChatListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ChatListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder holder, int position) {

        ConversationModel conversation = conversations.get(position);

        holder.binding.nameTV.setText(conversation.getName());

        holder.binding.descriptionTV.setText(conversation.getLastMessage());

//        holder.binding.carModelTV.setText("");

        if(conversation.isActive)
            holder.binding.activeDot.setVisibility(View.VISIBLE);
        else
            holder.binding.activeDot.setVisibility(View.INVISIBLE);

        Glide.with(c).load(conversation.getImage()).apply(new RequestOptions().centerCrop()
        .placeholder(R.drawable.user_dummy_profile))
                .into(holder.binding.productIV);

        holder.binding.getRoot().setOnClickListener(view -> moveToChatScreen(conversation));

    }

    private void moveToChatScreen(ConversationModel conversation) {

        Intent intent = new Intent(c, Chat.class);

        ParcelableChat parcelableChat = new ParcelableChat();

        parcelableChat.setConversationID(conversation.getConversationID());

        parcelableChat.setSelectedUserId(conversation.getToID());

        parcelableChat.setTypeStatus("false");
        parcelableChat.setSelectedUserName(conversation.getName());
        parcelableChat.setProId(conversation.productId);

        intent.putExtra("parcelableChat", parcelableChat);

        c.startActivity(intent);
    }

    @Override
    public int getItemCount() {

        return conversations.size();

    }

}

