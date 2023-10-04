package com.buzzware.monymarket.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.buzzware.monymarket.Models.BankDetailModel;
import com.buzzware.monymarket.Models.ProductModel;
import com.buzzware.monymarket.Models.UserModel;
import com.buzzware.monymarket.classes.SessionManager;
import com.buzzware.monymarket.databinding.ActivityEditBankDetailBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditBankDetailActivity extends AppCompatActivity {

    ActivityEditBankDetailBinding binding;

    BankDetailModel bankDetailModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditBankDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getBankDetail();
        
        setListener();

    }

    private void setListener() {

        binding.addNowBtn.setOnClickListener(v -> {
            if (valid()) {
                addBankDetail();
            }
        });
        binding.backIV.setOnClickListener(v->{
            finish();
        });
    }
    private boolean valid() {

        if (binding.nameET.getText().toString().trim().isEmpty()) {
            Toast.makeText(EditBankDetailActivity.this, "Please! Enter Bank Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.holderET.getText().toString().trim().isEmpty()) {
            Toast.makeText(EditBankDetailActivity.this, "Please! Enter Account Holder Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.accountET.getText().toString().trim().isEmpty()) {
            Toast.makeText(EditBankDetailActivity.this, "Please! Enter Account Number / IBAN", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }

    }

    private void getBankDetail() {

        FirebaseFirestore.getInstance().collection("Bank Detail")
                .whereEqualTo("userID", SessionManager.getInstance().getUser(EditBankDetailActivity.this).userId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            return;
                        } else {
                            for (DocumentSnapshot document : value.getDocuments()) {

                                
                               BankDetailModel detail = document.toObject(BankDetailModel.class);

                               if(detail.userID.equals(SessionManager.getInstance().getUser(EditBankDetailActivity.this).userId)){
                                   bankDetailModel=detail;
                                   bankDetailModel.userID = document.getId();
                               }
                                

                            }
                            
                            if(bankDetailModel!=null){
                                setView();
                            }
                            
                            
                            
                        }

                    }
                });
    }

    private void setView() {
        
        binding.nameET.setText(bankDetailModel.bankName);
        binding.holderET.setText(bankDetailModel.accountName);
        binding.accountET.setText(bankDetailModel.ibanNumber);
        
    }

    private void addBankDetail() {

        BankDetailModel bankDetail = new BankDetailModel();

        bankDetail.userID= SessionManager.getInstance().getUser(EditBankDetailActivity.this).userId;
        bankDetail.bankName=binding.nameET.getText().toString();
        bankDetail.accountName=binding.holderET.getText().toString();
        bankDetail.ibanNumber=binding.accountET.getText().toString();

        if(bankDetailModel==null){
            FirebaseFirestore.getInstance().collection("Bank Detail").add(bankDetail);
        }else{
            FirebaseFirestore.getInstance().collection("Bank Detail").document(bankDetailModel.userID).set(bankDetail);
        }


        FirebaseFirestore.getInstance().collection("users").document(SessionManager.getInstance().getUser(EditBankDetailActivity.this).userId)
                .update("stripeStatus","active");

        Toast.makeText(EditBankDetailActivity.this, "Bank Detail Updated successfully!", Toast.LENGTH_SHORT).show();
        finish();


    }


}