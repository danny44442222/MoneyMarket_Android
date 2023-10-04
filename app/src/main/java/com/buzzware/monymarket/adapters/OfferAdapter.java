package com.buzzware.monymarket.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.monymarket.Models.CartModelForViewCart;
import com.buzzware.monymarket.Models.OfferModel;
import com.buzzware.monymarket.databinding.ItemsDesignCartBinding;
import com.buzzware.monymarket.interfaces.CartFragmentCallback;
import com.buzzware.monymarket.interfaces.OfferCallback;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ViewHolder> {

    private List<OfferModel> list;

    private Context mContext;

    OfferCallback offerCallback;

    public OfferAdapter(Context mContext, List<OfferModel> list ,OfferCallback offerCallback) {

        this.list = list;

        this.mContext = mContext;

        this.offerCallback=offerCallback;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(ItemsDesignCartBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));


    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        OfferModel offerModel=list.get(i);

        viewHolder.binding.receiveTitleTV.setText("You have receive a "+offerModel.proCurrency+" offer from "+offerModel.senderName);

        viewHolder.binding.adPriceTV.setText(offerModel.proAmount+" "+offerModel.proCurrency);
        viewHolder.binding.exPriceTV.setText(offerModel.exchangeAmount+" "+offerModel.exchangeCurrency);


        viewHolder.binding.rejectBtn.setOnClickListener(v->{


            offerCallback.onItemClick("rejected",offerModel);


        });
        viewHolder.binding.acceptBtn.setOnClickListener(v->{


            offerCallback.onItemClick("accepted",offerModel);


        });


    }




    @Override
    public int getItemCount() {

        return list.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemsDesignCartBinding binding;


        public ViewHolder(@NonNull ItemsDesignCartBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

        }

    }

}
