package com.buzzware.monymarket.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.monymarket.Models.WalletModel;
import com.buzzware.monymarket.R;
import com.buzzware.monymarket.activities.ConverterActivity;
import com.buzzware.monymarket.activities.PreviewAndWithdrawActivity;
import com.buzzware.monymarket.activities.WalletActivity;
import com.buzzware.monymarket.databinding.ItemsDesignCartBinding;
import com.buzzware.monymarket.databinding.ItemsDesignHistoryBinding;
import com.buzzware.monymarket.interfaces.CartFragmentCallback;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<WalletModel> list;

    private Context mContext;


    public HistoryAdapter(Context mContext, List<WalletModel> list) {

        this.list = list;

        this.mContext = mContext;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(ItemsDesignHistoryBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        WalletModel walletModel=list.get(i);

//        Glide.with(mContext).load(walletModel.imageUrl)
//                .apply(new RequestOptions().centerCrop().placeholder(R.drawable.dummy_product_image1))
//                .into(viewHolder.binding.productIV);

        viewHolder.binding.nameTV.setText(walletModel.proName);
        viewHolder.binding.amountTV.setText(walletModel.proAmount+" "+walletModel.proCurrency);
        viewHolder.binding.descriptionTV.setText(getDate(walletModel.timeStamp));

        viewHolder.binding.mainLayout.setOnClickListener(v->{
            mContext.startActivity(new Intent(mContext, PreviewAndWithdrawActivity.class)
            .putExtra("wallet",walletModel));
        });

    }
    private String getDate(long time) {

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy HH:MM", cal).toString();
        return date;

    }

    @Override
    public int getItemCount() {

        return list.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemsDesignHistoryBinding binding;


        public ViewHolder(@NonNull ItemsDesignHistoryBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }

    }

}
