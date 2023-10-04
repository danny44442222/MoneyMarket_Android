package com.buzzware.monymarket.activities.messages.chat.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.monymarket.R;
import com.buzzware.monymarket.activities.HomeActivity;
import com.buzzware.monymarket.activities.messages.chat.mo.MessageModel;
import com.buzzware.monymarket.classes.OfferMessageEvent;
import com.buzzware.monymarket.databinding.MessagesItemLayBinding;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    List<MessageModel> list;

    Context mContext;

    String userId;

    String myImageUrl;

    String otherUserImageUrl;

    public MessagesAdapter(Context mContext, List<MessageModel> list, String userId, String myImageUrl, String otherUserImageUrl) {

        this.list = list;

        this.mContext = mContext;

        this.userId = userId;

        this.myImageUrl = myImageUrl;

        this.otherUserImageUrl = otherUserImageUrl;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(MessagesItemLayBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        final MessageModel messageModel = list.get(i);

        if (messageModel.getFromID().equals(userId)) {
            //my message layout

            Glide.with(mContext).load(myImageUrl)
                    .apply(new RequestOptions().centerCrop().placeholder(R.drawable.app_icon))
                    .into(viewHolder.binding.myMessageLay.userImageIV);

            viewHolder.binding.othersMessageLay.getRoot().setVisibility(View.GONE);

            viewHolder.binding.myMessageLay.getRoot().setVisibility(View.VISIBLE);

            if (messageModel.getType().equals("text")) {

                viewHolder.binding.myMessageLay.messageTV.setText(messageModel.getContent());

                viewHolder.binding.myMessageLay.timeTV.setText(convertFormat(String.valueOf(messageModel.getTimestamp())));

            } else if (messageModel.getType().equals("offer")) {

                viewHolder.binding.myMessageLay.messageTV.setText(messageModel.getContent());

                viewHolder.binding.myMessageLay.timeTV.setText(convertFormat(String.valueOf(messageModel.getTimestamp())));


            } else {

                viewHolder.binding.myMessageLay.messageTV.setText("");

                viewHolder.binding.myMessageLay.imageIV.setVisibility(View.VISIBLE);

                viewHolder.binding.myMessageLay.timeTV.setText("");

                Glide.with(mContext).load(messageModel.getContent())
                        .apply(new RequestOptions().centerCrop().placeholder(R.drawable.app_icon))
                        .into(viewHolder.binding.myMessageLay.imageIV);


            }


        } else {
            ///other message layout

            Glide.with(mContext).load(otherUserImageUrl)
                    .apply(new RequestOptions().centerCrop().placeholder(R.drawable.app_icon))
                    .into(viewHolder.binding.othersMessageLay.userImageIV);

            viewHolder.binding.myMessageLay.getRoot().setVisibility(View.GONE);

            viewHolder.binding.othersMessageLay.getRoot().setVisibility(View.VISIBLE);

            if (messageModel.getType().equals("text")) {

                viewHolder.binding.othersMessageLay.messageTV.setText(messageModel.getContent());

                viewHolder.binding.othersMessageLay.timeTV.setText(convertFormat(String.valueOf(messageModel.getTimestamp())));
            } else if (messageModel.getType().equals("offer")) {

                viewHolder.binding.othersMessageLay.messageTV.setText(messageModel.getContent());
                viewHolder.binding.othersMessageLay.payBtn.setVisibility(View.VISIBLE);

                viewHolder.binding.othersMessageLay.payBtn.setOnClickListener(v -> {

                    HomeActivity.isOffer = true;
                    ((Activity) mContext).finish();

                });
                viewHolder.binding.othersMessageLay.timeTV.setText(convertFormat(String.valueOf(messageModel.getTimestamp())));
            } else {

                viewHolder.binding.othersMessageLay.messageTV.setText("");

                viewHolder.binding.othersMessageLay.timeTV.setText("");
                viewHolder.binding.othersMessageLay.imageIV.setVisibility(View.VISIBLE);

                Glide.with(mContext).load(messageModel.getContent())
                        .apply(new RequestOptions().centerCrop().placeholder(R.drawable.app_icon))
                        .into(viewHolder.binding.othersMessageLay.imageIV);


            }

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        MessagesItemLayBinding binding;

        public ViewHolder(@NonNull MessagesItemLayBinding binding) {

            super(binding.getRoot());

            this.binding = binding;
        }
    }

    public static String convertFormat(String inputDate) {
        Date date = null;

        date = new Date(Long.parseLong(inputDate));

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dF = new SimpleDateFormat("hh:mm a");

        return dF.format(date);
    }

}