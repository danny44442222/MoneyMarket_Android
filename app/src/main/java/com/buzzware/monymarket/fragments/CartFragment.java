package com.buzzware.monymarket.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buzzware.monymarket.Models.CartModel;
import com.buzzware.monymarket.Models.CartModelForViewCart;
import com.buzzware.monymarket.Models.OrderModel;
import com.buzzware.monymarket.Models.ProductModel;
import com.buzzware.monymarket.Models.WalletModel;
import com.buzzware.monymarket.activities.FavoriteActivity;
import com.buzzware.monymarket.classes.Constants;
import com.buzzware.monymarket.classes.SessionManager;
import com.buzzware.monymarket.databinding.BottomSheetDialogConversionBinding;
import com.buzzware.monymarket.databinding.BottomSheetDialogPaymentOptionBinding;
import com.buzzware.monymarket.databinding.BottomSheetDialogRemoveIteBinding;
import com.buzzware.monymarket.databinding.FragmentCartBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    FragmentCartBinding binding;

    List<CartModelForViewCart> cartList;

    CartModelForViewCart cartModelForViewCart;

    int qty = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCartBinding.inflate(inflater);

        getCartItem();
        
        binding.favoriteIV.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), FavoriteActivity.class));
        });

        return binding.getRoot();

    }

    private void setListener() {

        binding.proceedToPayBtn.setOnClickListener(v -> {
            showConverterBottomSheetDialog();
        });

        binding.minusIV.setOnClickListener(v -> {

            int selectedQty = Integer.parseInt(binding.countTV.getText().toString());

            if (selectedQty > 1) {

                binding.countTV.setText(selectedQty - 1 + "");

            }


        });

        binding.addIV.setOnClickListener(v -> {


            int selectedQty = Integer.parseInt(binding.countTV.getText().toString());

            if (selectedQty < qty) {

                binding.countTV.setText(selectedQty + 1 + "");

            }

        });

        binding.trashIV.setOnClickListener(v -> {
            showDeleteBottomSheetDialog();
        });


    }

    private void getCartItem() {

        cartList = new ArrayList<CartModelForViewCart>();

        cartModelForViewCart = new CartModelForViewCart();

        FirebaseFirestore.getInstance().collection("Cart")
                .document(SessionManager.getInstance().getUser(getContext()).userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        CartModel cartModel = task.getResult().toObject(CartModel.class);

                        if(cartModel!=null){
                            cartModel.cartId = task.getResult().getId();

                            cartModelForViewCart.cartModel = cartModel;

                            getProductItem(cartModel.proId);
                        }

                    }
                });
    }

    private void getProductItem(String proId) {

        FirebaseFirestore.getInstance().collection("Products")
                .document(proId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        ProductModel productModel = task.getResult().toObject(ProductModel.class);

                        productModel.proId = task.getResult().getId();

                        cartModelForViewCart.productModel = productModel;

                        setCartData();
                    }
                });
    }

    private void setCartData() {

        binding.cartItemCL.setVisibility(View.VISIBLE);

        String currency = cartModelForViewCart.productModel.proCurrency;

        String[] data;

        if (currency.contains(" ")) {

            data = currency.split(" ");

            currency = data[1];

        }

        binding.nameTV.setText(cartModelForViewCart.productModel.proTitle);
        binding.descriptionTV.setText(cartModelForViewCart.productModel.proQuantity + " " + currency);
        binding.countTV.setText(cartModelForViewCart.cartModel.proQuanity + "");
        qty = Integer.parseInt(cartModelForViewCart.cartModel.proQuanity + "");

        setListener();

    }


    private void showDeleteBottomSheetDialog() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());

        BottomSheetDialogRemoveIteBinding dialogRemoveIteBinding = BottomSheetDialogRemoveIteBinding.inflate(getLayoutInflater());

        bottomSheetDialog.setContentView(dialogRemoveIteBinding.getRoot());

        dialogRemoveIteBinding.nameTV.setText(cartModelForViewCart.productModel.proTitle);
        dialogRemoveIteBinding.descriptionTV.setText(binding.countTV.getText().toString());
        dialogRemoveIteBinding.countTV.setText(cartModelForViewCart.cartModel.proQuanity + "");
        qty = Integer.parseInt(cartModelForViewCart.cartModel.proQuanity + "");

        dialogRemoveIteBinding.cancelTV.setOnClickListener(v -> {

            bottomSheetDialog.dismiss();

        });

        dialogRemoveIteBinding.removeTV.setOnClickListener(v -> {

            FirebaseFirestore.getInstance().collection("Cart").document(cartModelForViewCart.cartModel.cartId).delete();

            binding.cartItemCL.setVisibility(View.GONE);

            bottomSheetDialog.dismiss();

        });

        bottomSheetDialog.show();
    }


    private void showConverterBottomSheetDialog() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());

        BottomSheetDialogConversionBinding dialogTermsBinding = BottomSheetDialogConversionBinding.inflate(getLayoutInflater());

        bottomSheetDialog.setContentView(dialogTermsBinding.getRoot());

        String currency = cartModelForViewCart.productModel.proCurrency;

        String[] data;

        if (currency.contains(" ")) {

            data = currency.split(" ");

            currency = data[1];

        }


        int proAmount = Integer.parseInt(cartModelForViewCart.productModel.proQuantity + "");
        int qty = Integer.parseInt(binding.countTV.getText().toString());
        int total = proAmount * qty;

        dialogTermsBinding.firstET.setText(total + " " + currency);

        String userCurrency = SessionManager.getInstance().getUser(getContext()).userCurrency;

        String[] userData;

        if (userCurrency.contains(" ")) {

            userData = userCurrency.split(" ");

            userCurrency = userData[1];

        }

      //  dialogTermsBinding.secondET.setText(Constants.convert(total, currency, userCurrency) + " " + userCurrency);

        dialogTermsBinding.proceedTV.setOnClickListener(v -> {

            showPaymentBottomSheetDialog();
            bottomSheetDialog.dismiss();

        });

        bottomSheetDialog.show();
    }

    private void showPaymentBottomSheetDialog() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());

        BottomSheetDialogPaymentOptionBinding dialogTermsBinding = BottomSheetDialogPaymentOptionBinding.inflate(getLayoutInflater());

        bottomSheetDialog.setContentView(dialogTermsBinding.getRoot());

        dialogTermsBinding.newCardTV.setOnClickListener(v -> {

            bottomSheetDialog.dismiss();

        });

        dialogTermsBinding.proceedTV.setOnClickListener(v -> {

            if (binding.countTV.getText().toString().equals(cartModelForViewCart.productModel.proQuantity + "")) {

                FirebaseFirestore.getInstance().collection("Products").document(cartModelForViewCart.productModel.proId).delete();

            } else {

                int selectedValue = Integer.parseInt(binding.countTV.getText().toString());
                int proValue = Integer.parseInt(cartModelForViewCart.productModel.proQuantity + "");

                int newValue = proValue - selectedValue;

                ProductModel productModel = cartModelForViewCart.productModel;
                productModel.proQuantity = Long.parseLong(newValue + "");

                FirebaseFirestore.getInstance().collection("Products")
                        .document(cartModelForViewCart.productModel.proId)
                        .set(productModel);
            }

            FirebaseFirestore.getInstance().collection("Cart").document(cartModelForViewCart.cartModel.cartId).delete();


            WalletModel walletModel=new WalletModel();
            walletModel.clientSecret="";
            walletModel.imageUrl=cartModelForViewCart.productModel.proImageUrls;
            walletModel.proAmount=Long.parseLong(binding.countTV.getText().toString());
            String currency = cartModelForViewCart.productModel.proCurrency;

            String[] data;

            if (currency.contains(" ")) {

                data = currency.split(" ");

                currency = data[1];

            }
            walletModel.proCurrency=currency;
            walletModel.proId=cartModelForViewCart.productModel.proId;
            walletModel.proName=cartModelForViewCart.productModel.proTitle;
            walletModel.timeStamp=System.currentTimeMillis();
            walletModel.userID=SessionManager.getInstance().getUser(getContext()).userId ;
            FirebaseFirestore.getInstance().collection("Wallet").document().set(walletModel);


            OrderModel orderModel=new OrderModel();
            orderModel.proImage=cartModelForViewCart.productModel.proImageUrls;
            orderModel.proAmount=Long.parseLong(binding.countTV.getText().toString());
            orderModel.proCurrency=currency;
            orderModel.proStatus="Purchased";
            orderModel.proName=cartModelForViewCart.productModel.proTitle;
            orderModel.userID=SessionManager.getInstance().getUser(getContext()).userId ;
            FirebaseFirestore.getInstance().collection("Order").document().set(orderModel);

            binding.cartItemCL.setVisibility(View.GONE);
            bottomSheetDialog.dismiss();

        });

        bottomSheetDialog.show();

    }


    private void showConversionBottomSheetDialog() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());

        BottomSheetDialogConversionBinding dialogTermsBinding = BottomSheetDialogConversionBinding.inflate(getLayoutInflater());

        bottomSheetDialog.setContentView(dialogTermsBinding.getRoot());

        dialogTermsBinding.proceedTV.setOnClickListener(v -> {

            bottomSheetDialog.dismiss();

        });

        bottomSheetDialog.show();
    }

}