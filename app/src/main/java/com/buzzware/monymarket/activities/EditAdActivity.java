package com.buzzware.monymarket.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.buzzware.monymarket.Models.ProductModel;
import com.buzzware.monymarket.classes.AddPlaceEvent;
import com.buzzware.monymarket.classes.Constants;
import com.buzzware.monymarket.classes.SessionManager;
import com.buzzware.monymarket.databinding.ActivityEditAdBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.williammora.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;

public class EditAdActivity extends BaseActivity {

    ActivityEditAdBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityEditAdBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setView();
        setListener();

    }

    private void setView() {

        binding.nameET.setText(Constants.editProductModel.proTitle);
        binding.currencySpinner.setSelection(Constants.spinnerCurrency.indexOf(Constants.editProductModel.proCurrency));
        binding.adQuantityET.setText(Constants.editProductModel.proQuantity+"");
        binding.exCurrencySpinner.setSelection(Constants.spinnerCurrency.indexOf(Constants.editProductModel.proexchangeCurr));
        binding.exPriceET.setText(Constants.editProductModel.proExchnagePrice+"");
        binding.descriptionET.setText(Constants.editProductModel.proDescription);

    }



    private void setListener() {

        binding.backIV.setOnClickListener(v->{
            finish();
        });

        binding.confirmBtn.setOnClickListener(v -> {

            if (isValid()) {

                ProductModel productModel = new ProductModel();
                productModel.proTitle = binding.nameET.getText().toString().trim();
                productModel.proCurrency = binding.currencySpinner.getSelectedItem().toString();
                productModel.proexchangeCurr = binding.exCurrencySpinner.getSelectedItem().toString();
                productModel.proExchnagePrice = Long.parseLong(binding.exPriceET.getText().toString().trim());
                productModel.proQuantity = Long.parseLong(binding.adQuantityET.getText().toString().trim());
                productModel.proDescription = binding.descriptionET.getText().toString().trim();
                productModel.timeStamp = System.currentTimeMillis();
                productModel.userId = SessionManager.getInstance().getUser(EditAdActivity.this).userId;

                FirebaseFirestore.getInstance().collection("Products")
                        .document(Constants.editProductModel.proId)
                        .set(productModel);

                showMessage("Product has been Updated!");
                finish();

            }

        });



    }

    private boolean isValid() {

        boolean check = false;

        if (binding.nameET.getText().toString().trim().isEmpty()) {

            showMessage("Name Required");

            return check;

        }

        if (binding.exPriceET.getText().toString().trim().isEmpty()) {

            showMessage("Price Required!");

            return check;

        }

        if (binding.adQuantityET.getText().toString().trim().isEmpty()) {

            showMessage("Quantity Required!");

            return check;

        }

        if (binding.descriptionET.getText().toString().isEmpty()) {

            showMessage("Description Required!");

            return check;

        }

//        if (imageURL.isEmpty()) {
//
//            showMessage("Product Image Required!");
//
//            return check;
//
//        }

        check = true;

        return check;

    }

    private void showMessage(String message) {

        Snackbar.with(EditAdActivity.this)
                .text(message)
                .show(EditAdActivity.this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}