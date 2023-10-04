package com.buzzware.monymarket.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import com.buzzware.monymarket.Models.OfferModel;
import com.buzzware.monymarket.Models.WalletModel;
import com.buzzware.monymarket.classes.Constants;
import com.buzzware.monymarket.classes.SessionManager;
import com.buzzware.monymarket.databinding.ActivityAddFundsBinding;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RaveUiManager;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class AddFundsActivity extends AppCompatActivity {

    ActivityAddFundsBinding binding;

    String convertedAmount = "";

    List<WalletModel> walletModelList;

    WalletModel walletModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddFundsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.currencySpinner.setText(SessionManager.getInstance().getUser(AddFundsActivity.this).userCurrency);

        setListener();

    }

    private void setListener() {

        binding.backIV.setOnClickListener(v -> {
            finish();
        });
        binding.secondET.setText("Final Amount: 0" +
                binding.currencySpinner.getText().toString());
        binding.firstET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (binding.firstET.getText().toString().trim().isEmpty())
                    binding.secondET.setText("Final Amount: 0" +
                            binding.currencySpinner.getText().toString());
                else

                    binding.secondET.setText("Final Amount: " + binding.firstET.getText().toString() + "" +
                            binding.currencySpinner.getText().toString());

            }
        });
//        binding.convertBtn.setOnClickListener(v -> {
//
//
//            if (binding.firstET.getText().toString().isEmpty()) {
//                Toast.makeText(AddFundsActivity.this, "Please enter amount first!", Toast.LENGTH_SHORT).show();
//                return;
//            } else {
//
//
//                try {
//
//                    String amount = "";
//
//                    amount = Constants.convert(Integer.parseInt(binding.firstET.getText().toString()),
//                            binding.currencySpinner.getText().toString(),
//                            SessionManager.getInstance().getUser(AddFundsActivity.this).userCurrency);
//
//                    convertedAmount = amount;
//                    convertedAmount = binding.firstET.getText().toString();
//
//                    if(convertedAmount.contains(".")){
//                        String[] data=convertedAmount.split("\\.");
//                        convertedAmount=data[0];
//                    }
//
//
//                    binding.secondET.setText(amount + " " + SessionManager.getInstance().getUser(AddFundsActivity.this).userCurrency);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    convertedAmount = "";
//                }
//
//
//            }
//
//        });

        binding.payBtn.setOnClickListener(v -> {

            if (binding.firstET.getText().toString().isEmpty()) {
                Toast.makeText(AddFundsActivity.this, "Please enter amount first!", Toast.LENGTH_SHORT).show();
                return;
            }


            convertedAmount = binding.firstET.getText().toString();

            if (convertedAmount.contains(".")) {
                String[] data = convertedAmount.split("\\.");
                convertedAmount = data[0];
            }

            new RaveUiManager(AddFundsActivity.this).setAmount(30)
                    .setCurrency("USD")
                    .setEmail(SessionManager.getInstance().getUser(AddFundsActivity.this).email)
                    .setfName(SessionManager.getInstance().getUser(AddFundsActivity.this).fullname)
                    .setlName("")
                    .setPublicKey("FLWPUBK_TEST-bd23e7007057117d17daf712192ba5fe-X")
                    .setEncryptionKey("FLWSECK_TEST4826feba256a")
                    .setTxRef("txRef")
                    .setPhoneNumber(SessionManager.getInstance().getUser(AddFundsActivity.this).userPhone, false)
                    .acceptAccountPayments(true)
                    .acceptCardPayments(true)
                    .acceptMpesaPayments(true)
                    .acceptAchPayments(true)
                    .acceptGHMobileMoneyPayments(true)
                    .acceptUgMobileMoneyPayments(true)
                    .acceptZmMobileMoneyPayments(true)
                    .acceptRwfMobileMoneyPayments(true)
                    .acceptSaBankPayments(true)
                    .acceptUkPayments(true)
                    .acceptBankTransferPayments(true)
                    .acceptUssdPayments(true)
                    .acceptBarterPayments(true)
                    .acceptFrancMobileMoneyPayments(false, "")
                    .allowSaveCardFeature(false)
                    .onStagingEnv(false)
                    .setMeta(null)
                    .isPreAuth(false)
                    .setSubAccounts(null)
                    .shouldDisplayFee(false)
                    .showStagingLabel(true)
                    .initialize();
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
         *  We advise you to do a further verification of transaction's details on your server to be
         *  sure everything checks out before providing service or goods.
         */
        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            String message = data.getStringExtra("response");
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {

                checkSelectedAmountExist();
            } else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Toast.makeText(this, "ERROR " + message, Toast.LENGTH_SHORT).show();
            } else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                Toast.makeText(this, "CANCELLED " + message, Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void checkSelectedAmountExist() {

        walletModelList = new ArrayList<WalletModel>();

        FirebaseFirestore.getInstance().collection("Wallet")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            walletModelList.clear();


                            for (QueryDocumentSnapshot document : task.getResult()) {

                                WalletModel walletModel = document.toObject(WalletModel.class);

                                walletModel.walletId = document.getId();

                                if (walletModel.userID.equals(SessionManager.getInstance().getUser(AddFundsActivity.this).userId)) {
                                    walletModelList.add(walletModel);
                                }


                            }

                            if (walletModelList.size() <= 0) {

                                WalletModel walletModel = new WalletModel();
                                walletModel.clientSecret = "";
                                walletModel.imageUrl = "";
                                walletModel.proAmount = Long.parseLong(convertedAmount);
                                walletModel.proCurrency = binding.currencySpinner.getText().toString();
                                walletModel.proId = "";
                                walletModel.proName = binding.currencySpinner.getText().toString();
                                walletModel.timeStamp = System.currentTimeMillis();
                                walletModel.userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                FirebaseFirestore.getInstance().collection("Wallet").add(walletModel);

                                Toast.makeText(AddFundsActivity.this, "Successfully add to Wallet!", Toast.LENGTH_LONG).show();

                                finish();
                                return;


                            } else {

                                for (int i = 0; i < walletModelList.size(); i++) {

                                    if (walletModelList.get(i).proCurrency.equals(binding.currencySpinner.getText().toString())) {
                                        walletModel = walletModelList.get(i);
                                        Long pq = walletModel.proAmount;
                                        Long pwa = Long.parseLong(convertedAmount);
                                        Long fAm = pwa + pq;
                                        //  walletModelSeller.proAmount = walletModelSeller.proAmount + product.proExchnagePrice;

//                                        FirebaseFirestore.getInstance().collection("Wallet")
//                                                .document(walletModelSeller.walletId).set(walletModelSeller);

                                        FirebaseFirestore.getInstance().collection("Wallet")
                                                .document(walletModel.walletId).update("proAmount", fAm);
                                        Toast.makeText(AddFundsActivity.this, "Successfully add to Wallet!", Toast.LENGTH_LONG).show();

                                        finish();
                                        return;
                                    }

                                }
                                WalletModel walletModel = new WalletModel();
                                walletModel.clientSecret = "";
                                walletModel.imageUrl = "";
                                walletModel.proAmount = Long.parseLong(convertedAmount);
                                walletModel.proCurrency = binding.currencySpinner.getText().toString();
                                walletModel.proId = "";
                                walletModel.proName = binding.currencySpinner.getText().toString();
                                walletModel.timeStamp = System.currentTimeMillis();
                                walletModel.userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                FirebaseFirestore.getInstance().collection("Wallet").add(walletModel);

                                Toast.makeText(AddFundsActivity.this, "Successfully add to Wallet!", Toast.LENGTH_LONG).show();

                                finish();

                            }


                        }
                    }
                });
    }


}