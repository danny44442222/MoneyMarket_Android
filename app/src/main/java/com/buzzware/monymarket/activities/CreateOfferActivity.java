package com.buzzware.monymarket.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.buzzware.monymarket.Models.OfferModel;
import com.buzzware.monymarket.Models.ProductModel;
import com.buzzware.monymarket.activities.messages.chat.Chat;
import com.buzzware.monymarket.classes.Constants;
import com.buzzware.monymarket.classes.SessionManager;
import com.buzzware.monymarket.databinding.ActivityCreateOfferBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CreateOfferActivity extends AppCompatActivity {

    ActivityCreateOfferBinding binding;

    ProductModel productModel = new ProductModel();
    OfferModel offerModel = new OfferModel();

    List<ProductModel> productList;
    List<String> spinnerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCreateOfferBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getProductsList();

    }

    private void getProductsList() {

        productList = new ArrayList<ProductModel>();
        spinnerList = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("Products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            productList.clear();
                            spinnerList.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                ProductModel product = document.toObject(ProductModel.class);

                                product.proId = document.getId();

                                if (product.userId.equals(SessionManager.getInstance().getUser(CreateOfferActivity.this).userId)) {
                                    if (product.proId.equals(Constants.selectedProId)) {

                                        productList.add(product);
                                        spinnerList.add(product.proCurrency);

                                    }
                                }

                            }

                            if (productList.size() > 0) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateOfferActivity.this, android.R.layout.simple_spinner_item, spinnerList);
                                binding.currencySpinner.setAdapter(adapter);
                                setListener();


                            } else {
                                Toast.makeText(CreateOfferActivity.this, "You have no ads yet!", Toast.LENGTH_SHORT).show();
                                finish();
                            }


                        } else {

                        }
                    }
                });
    }


    private void setListener() {
        binding.createBtn.setOnClickListener(v -> {

            if (validate()) {

                if (Long.parseLong(binding.quantityET.getText().toString()) <= productModel.proQuantity)
                    addOfferInFirestore();
                else
                    Toast.makeText(CreateOfferActivity.this, "Quantity must be less than or equal to selected Product!", Toast.LENGTH_SHORT).show();

            }

        });
        binding.backIV.setOnClickListener(v -> {
            finish();
        });
        binding.currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                productModel = productList.get(i);
                binding.qtyTV.setText(productModel.proQuantity + " " + productModel.proCurrency);
                binding.exCurrencyTV.setText("Exchange rate offered " + productModel.proexchangeCurr);


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void addOfferInFirestore() {

        offerModel.exchangeAmount = Long.parseLong(binding.exPriceET.getText().toString());
        offerModel.exchangeCurrency = productModel.proexchangeCurr;
        offerModel.imageUrl = "";
        offerModel.proAmount = Long.parseLong(binding.quantityET.getText().toString());
        offerModel.proCurrency = productModel.proCurrency;
        offerModel.proId = productModel.proId;
        offerModel.proName = productModel.proTitle;
        offerModel.senderID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        offerModel.senderName = SessionManager.getInstance().getUser(CreateOfferActivity.this).fullname;
        offerModel.userID = Constants.selectedUserId;
        offerModel.ConversationID = Constants.selectedConId;



        Double d = Double.parseDouble(binding.exPriceET.getText().toString())/Double.parseDouble(binding.quantityET.getText().toString());
        DecimalFormat decimalFormat = new DecimalFormat("0.0");


        FirebaseFirestore.getInstance().collection("Offers").document().set(offerModel);
        Chat.isChatOfferMessage = true;
        Chat.chatOfferMessage = "I have sent you an offer for buy \n" + binding.quantityET.getText().toString()
                + " " + productModel.proCurrency + "\n" + binding.exPriceET.getText().toString() + "at\n" + binding.currencySpinner.getSelectedItem().toString()
                + "per the buying currency "+decimalFormat.format(d)+" "+productModel.proCurrency;

        finish();

    }

    private boolean validate() {

        if (binding.quantityET.getText().toString().isEmpty())
            return false;
        else if (binding.exPriceET.getText().toString().isEmpty())
            return false;
        else
            return true;
    }
}