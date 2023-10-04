package com.buzzware.monymarket.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.buzzware.monymarket.Models.OrderModel;
import com.buzzware.monymarket.Models.WalletModel;
import com.buzzware.monymarket.classes.Constants;
import com.buzzware.monymarket.classes.SessionManager;
import com.buzzware.monymarket.databinding.ActivityConverterBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;

public class ConverterActivity extends AppCompatActivity {

    ActivityConverterBinding binding;

    WalletModel walletModel;
    Long walletAmount = 0L;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityConverterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getDataFromIntent();

        setListener();

    }

    private void getDataFromIntent() {

        Intent intent = getIntent();

        walletModel = intent.getParcelableExtra("wallet");
        walletAmount = walletModel.proAmount;

        setView();

    }

    private void setView() {

        binding.amountWithCurrencyTV.setText(walletModel.proAmount + " " + walletModel.proCurrency);

    }

    private void setListener() {

        binding.backIV.setOnClickListener(v -> {
            finish();
        });

        binding.firstET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!binding.firstET.getText().toString().isEmpty()) {

                    Long a = walletAmount;
                    Long b = Long.parseLong(binding.firstET.getText().toString());

                    if (b > a) {
                        binding.firstET.setText("");
                    }

                }

            }
        });

        binding.convertBtn.setOnClickListener(v -> {
            if (!binding.firstET.getText().toString().isEmpty()) {

                Long b = Long.parseLong(binding.firstET.getText().toString());

                if (b != 0) {

                    String currency = walletModel.proCurrency;

                    String[] data;

                    if (currency.contains(" ")) {

                        data = currency.split(" ");

                        currency = data[1];

                    }

                    String userCurrency = SessionManager.getInstance().getUser(ConverterActivity.this).userCurrency;

                    String[] userData;

                    if (userCurrency.contains(" ")) {

                        userData = userCurrency.split(" ");

                        userCurrency = userData[1];

                    }

                    try {
                        binding.secondET.setText(Constants.convert(Integer.parseInt(b + ""), currency, userCurrency));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }
        });

        binding.proceedTV.setOnClickListener(v->{
            if (!binding.firstET.getText().toString().isEmpty()) {

                Long b = Long.parseLong(binding.firstET.getText().toString());

                if (b != 0) {
                    OrderModel orderModel=new OrderModel();
                    orderModel.proImage=walletModel.imageUrl;
                    orderModel.proAmount=b;
                    orderModel.proCurrency=walletModel.proCurrency;
                    orderModel.proStatus="Withdraw";
                    orderModel.proName=walletModel.proName;
                    orderModel.userID=SessionManager.getInstance().getUser(ConverterActivity.this).userId ;
                    FirebaseFirestore.getInstance().collection("Order").document().set(orderModel);


                    if (binding.firstET.getText().toString().equals(walletAmount + "")){
                        FirebaseFirestore.getInstance().collection("Wallet")
                                .document(walletModel.walletId).delete();
                        finish();
                    }else{

                        WalletModel wallet=walletModel;
                        wallet.proAmount=walletAmount-b;

                        FirebaseFirestore.getInstance().collection("Wallet")
                                .document(walletModel.walletId).set(wallet);

                        finish();

                    }


                }
            }

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}