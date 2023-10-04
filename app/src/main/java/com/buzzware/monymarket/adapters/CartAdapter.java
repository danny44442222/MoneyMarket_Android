package com.buzzware.monymarket.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.monymarket.Models.CartModelForViewCart;
import com.buzzware.monymarket.databinding.ItemsDesignCartBinding;
import com.buzzware.monymarket.interfaces.CartFragmentCallback;

import java.util.List;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<CartModelForViewCart> list;

    private Context mContext;

    CartFragmentCallback cartFragmentCallback;

    int size = 0;

    public CartAdapter(Context mContext, List<CartModelForViewCart> list, CartFragmentCallback cartFragmentCallback) {

        this.list = list;

        this.mContext = mContext;

        this.cartFragmentCallback = cartFragmentCallback;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(ItemsDesignCartBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));


    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {


        CartModelForViewCart cartModelForAdapter=new CartModelForViewCart();

//        viewHolder.binding.nameTV.setText(cartModelForAdapter.productModel.proTitle);
//        viewHolder.binding.descriptionTV.setText(cartModelForAdapter.productModel.proQuantity+" "+cartModelForAdapter.productModel.proCurrency);
//        viewHolder.binding.countTV.setText(cartModelForAdapter.cartModel.proQuanity+"");
//
//        viewHolder.binding.trashIV.setOnClickListener(v->{
//            cartFragmentCallback.onItemClick("delete");
//        });
//
//        viewHolder.binding.descriptionTV.setOnClickListener(v->{
//            cartFragmentCallback.onItemClick("conversion");
//        });

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
