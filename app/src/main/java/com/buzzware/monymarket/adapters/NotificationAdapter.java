package com.buzzware.monymarket.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.buzzware.monymarket.Models.NotificationModel;
import com.buzzware.monymarket.R;
import com.buzzware.monymarket.activities.ChatActivity;
import com.buzzware.monymarket.databinding.ItemsDesignNotificationBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationModel> list;

    private Context mContext;

    public NotificationAdapter(Context mContext, List<NotificationModel> list) {
        this.list = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemsDesignNotificationBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {


        viewHolder.binding.nameTV.setText(list.get(i).getTitle());
        viewHolder.binding.descriptionTV.setText(list.get(i).getMessage());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(list.get(i).getTime()));
        Date date = calendar.getTime();

//        String time = new SimpleDateFormat("hh:mm a - MMM dd, yyyy").format(date);
//        viewHolder.binding.dateTV.setText(time);


//        viewHolder.binding.mainLayout.setOnClickListener(view -> {
//            Intent intent = new Intent(mContext, ChatActivity.class);
//            intent.putExtra("userId", list.get(i).getSenderId());
//            mContext.startActivity(intent);
//            ((Activity) mContext).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//        });
    }

    @Override
    public int getItemCount() {
        int arr = 0;
        try {
            if (list.size() == 0) {
                arr = 0;
            } else {

                arr = list.size();
            }
        } catch (Exception e) {
        }

        return arr;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemsDesignNotificationBinding binding;


        public ViewHolder(@NonNull ItemsDesignNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }
}
