package com.buzzware.monymarket.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.buzzware.monymarket.Models.BankDetailModel;
import com.buzzware.monymarket.Models.ProductModel;
import com.buzzware.monymarket.Models.TranferDataModel;
import com.buzzware.monymarket.Models.WalletModel;
import com.buzzware.monymarket.classes.Constants;
import com.buzzware.monymarket.classes.SessionManager;
import com.buzzware.monymarket.databinding.ActivityPreviewAndWithdrawBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;

public class PreviewAndWithdrawActivity extends AppCompatActivity {

    ActivityPreviewAndWithdrawBinding binding;


    WalletModel walletModel;
    ProductModel productModel;

    Long walletAmount = 0L;

    BankDetailModel bankDetailModel = null;

    String convertedAmount = "";
    Long remainingAmount = 0L;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPreviewAndWithdrawBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        getBankDetail();


    }

    private void getBankDetail() {

        FirebaseFirestore.getInstance().collection("Bank Detail")
                .whereEqualTo("userID", SessionManager.getInstance().getUser(PreviewAndWithdrawActivity.this).userId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            return;
                        } else {
                            for (DocumentSnapshot document : value.getDocuments()) {


                                BankDetailModel detail = document.toObject(BankDetailModel.class);

                                if (detail.userID.equals(SessionManager.getInstance().getUser(PreviewAndWithdrawActivity.this).userId)) {
                                    bankDetailModel = detail;
                                    bankDetailModel.userID = document.getId();
                                }


                            }

                            if (bankDetailModel == null) {
                                Toast.makeText(PreviewAndWithdrawActivity.this, "Please Add your bank detail first!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                setBankDetailView();
                            }


                        }

                    }
                });
    }

    private void setBankDetailView() {

        binding.nameET.setText(bankDetailModel.bankName);
        binding.holderET.setText(bankDetailModel.accountName);
        binding.accountET.setText(bankDetailModel.ibanNumber);

        getDataFromIntent();

    }


    private void getDataFromIntent() {

        Intent intent = getIntent();


        if (intent.getParcelableExtra("wallet") != null) {
            walletModel = intent.getParcelableExtra("wallet");
            walletAmount = walletModel.proAmount;

            productModel = null;

            setListener();

        } else {
            productModel = intent.getParcelableExtra("product");
            walletModel = null;

            setListener2();
        }





    }



    private void setListener() {

        binding.qtyTV.setText(walletModel.proAmount + " " + walletModel.proCurrency);


        binding.backIV.setOnClickListener(v -> {
            finish();
        });

        binding.convertBtn.setOnClickListener(v -> {


            if (binding.firstET.getText().toString().isEmpty()) {
                Toast.makeText(PreviewAndWithdrawActivity.this, "Please enter amount first!", Toast.LENGTH_SHORT).show();
                return;
            } else {

                Long cQty = walletModel.proAmount;
                Long nQty = Long.parseLong(binding.firstET.getText().toString());

                if (nQty > cQty) {
                    Toast.makeText(PreviewAndWithdrawActivity.this, "Please enter valid amount!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    try {
                        String amount = "";
                        amount = Constants.convert(Integer.parseInt(binding.firstET.getText().toString()),
                                walletModel.proCurrency,
                                SessionManager.getInstance().getUser(PreviewAndWithdrawActivity.this).userCurrency);

                        convertedAmount = amount;
                        remainingAmount = cQty - nQty;
                        binding.secondET.setText(amount + " " + SessionManager.getInstance().getUser(PreviewAndWithdrawActivity.this).userCurrency);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        convertedAmount = "";
                    }
                }

            }

        });

        binding.withdrawNowBtn.setOnClickListener(v -> {

            if (convertedAmount != "") {
                TranferDataModel tranferDataModel = new TranferDataModel();

                tranferDataModel.accountName = binding.holderET.getText().toString();
                tranferDataModel.bankName = binding.nameET.getText().toString();
                tranferDataModel.ibanNumber = binding.accountET.getText().toString();
                tranferDataModel.proId = walletModel.walletId;
                tranferDataModel.timeStamp = System.currentTimeMillis() + "";
                tranferDataModel.tranferAmount = convertedAmount;
                tranferDataModel.transferCurrency = SessionManager.getInstance().getUser(PreviewAndWithdrawActivity.this).userCurrency;
                tranferDataModel.transferStatus = "Pending";
                tranferDataModel.userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                FirebaseFirestore.getInstance().collection("TranferData").add(tranferDataModel);
                FirebaseFirestore.getInstance().collection("Wallet")
                        .document(walletModel.walletId)
                        .update("proAmount", remainingAmount);

                startActivity(new Intent(PreviewAndWithdrawActivity.this, ReceiptActivity.class)
                        .putExtra("transferData", tranferDataModel));

                finish();

            }

        });

    }

    private void setListener2() {
        binding.qtyTV.setText(productModel.proQuantity + " " + productModel.proCurrency);

        binding.backIV.setOnClickListener(v -> {
            finish();
        });

        binding.convertBtn.setOnClickListener(v -> {


            if (binding.firstET.getText().toString().isEmpty()) {
                Toast.makeText(PreviewAndWithdrawActivity.this, "Please enter amount first!", Toast.LENGTH_SHORT).show();
                return;
            } else {

                Long cQty = productModel.proQuantity;
                Long nQty = Long.parseLong(binding.firstET.getText().toString());

                if (nQty > cQty) {
                    Toast.makeText(PreviewAndWithdrawActivity.this, "Please enter valid amount!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    try {
                        String amount = "";
                        amount = Constants.convert(Integer.parseInt(binding.firstET.getText().toString()),
                                productModel.proCurrency,
                                SessionManager.getInstance().getUser(PreviewAndWithdrawActivity.this).userCurrency);

                        convertedAmount = amount;
                        remainingAmount = cQty - nQty;
                        binding.secondET.setText(amount + " " + SessionManager.getInstance().getUser(PreviewAndWithdrawActivity.this).userCurrency);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        convertedAmount = "";
                    }
                }

            }

        });

        binding.withdrawNowBtn.setOnClickListener(v -> {

            if (convertedAmount != "") {
                TranferDataModel tranferDataModel = new TranferDataModel();

                tranferDataModel.accountName = binding.holderET.getText().toString();
                tranferDataModel.bankName = binding.nameET.getText().toString();
                tranferDataModel.ibanNumber = binding.accountET.getText().toString();
                tranferDataModel.proId = productModel.proId;
                tranferDataModel.timeStamp = System.currentTimeMillis() + "";
                tranferDataModel.tranferAmount = convertedAmount;
                tranferDataModel.transferCurrency = SessionManager.getInstance().getUser(PreviewAndWithdrawActivity.this).userCurrency;
                tranferDataModel.transferStatus = "Pending";
                tranferDataModel.userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                FirebaseFirestore.getInstance().collection("TranferData").add(tranferDataModel);
                FirebaseFirestore.getInstance().collection("Products")
                        .document(productModel.proId)
                        .update("proQuantity", remainingAmount);

                startActivity(new Intent(PreviewAndWithdrawActivity.this, ReceiptActivity.class)
                        .putExtra("transferData", tranferDataModel));

                finish();

            }

        });

    }

}