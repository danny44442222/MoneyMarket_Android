package com.buzzware.monymarket.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.monymarket.Models.CartModel;
import com.buzzware.monymarket.Models.NotificationModel;
import com.buzzware.monymarket.Models.OrderModel;
import com.buzzware.monymarket.Models.ProductModel;
import com.buzzware.monymarket.Models.UserModel;
import com.buzzware.monymarket.Models.WalletModel;
import com.buzzware.monymarket.R;
import com.buzzware.monymarket.activities.messages.chat.Chat;
import com.buzzware.monymarket.activities.messages.chat.mo.ParcelableChat;
import com.buzzware.monymarket.activities.messages.chatList.mo.ConversationModel;
import com.buzzware.monymarket.activities.messages.chatList.mo.LastMessageModel;
import com.buzzware.monymarket.classes.Constants;
import com.buzzware.monymarket.classes.FirebaseInstances;
import com.buzzware.monymarket.classes.SessionManager;
import com.buzzware.monymarket.databinding.ActivityProductDetailBinding;
import com.buzzware.monymarket.generic.GenericModelLiveData;
import com.buzzware.monymarket.retrofit.NotificationController;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends BaseActivity {

    ActivityProductDetailBinding binding;

    private ProductModel product;
    WalletModel walletModel;
    WalletModel walletModelSeller;

    String isFavorite = "no";
    String type = "my";

    int qty = 0;

    List<WalletModel> walletModelList;
    List<WalletModel> walletModelListForSeller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getDataFromIntent();


    }

    private void getDataFromIntent() {

        Intent intent = getIntent();

        product = intent.getParcelableExtra("product");

        qty = Integer.parseInt(product.proQuantity + "");

        isFavorite = intent.getStringExtra("isFavorite");
        type = intent.getStringExtra("type");



        if(type.equals("other")){

            binding.favoriteIV.setVisibility(View.VISIBLE);
        }else{
            binding.favoriteIV.setVisibility(View.INVISIBLE);
        }

        setView();
        setListener();
    }

    private void setListener() {

        binding.backIV.setOnClickListener(v -> {
            finish();
        });


        binding.chatCL.setOnClickListener(v -> {
            //startActivity(new Intent(ProductDetailActivity.this, ChatActivity.class));
            if(type.equals("other")){
                checkAlreadyExist();
            }else {
                Toast.makeText(ProductDetailActivity.this, "You can't chat with your ad", Toast.LENGTH_SHORT).show();
            }

        });

        binding.buyNowCL.setOnClickListener(v -> {
            //startActivity(new Intent(ProductDetailActivity.this, ChatActivity.class));

            if(type.equals("other")){
                String q = qty + "";
                if (q.equals("0")) {
                    Toast.makeText(ProductDetailActivity.this, "Product is out of stock!", Toast.LENGTH_SHORT).show();
                } else
                    checkSelectedAmountExist();
            }else {
                Toast.makeText(ProductDetailActivity.this, "You can't buy your ad", Toast.LENGTH_SHORT).show();
            }


        });

        binding.favoriteIV.setOnClickListener(v -> {

            if (SessionManager.getInstance().getUser(ProductDetailActivity.this).userLikedPro != null
                    && SessionManager.getInstance().getUser(ProductDetailActivity.this).userLikedPro.contains(product.proId)) {


                UserModel userModel = SessionManager.getInstance().getUser(ProductDetailActivity.this);

                userModel.userLikedPro.remove(product.proId);

                SessionManager.getInstance().setUser(ProductDetailActivity.this, userModel);

                FirebaseFirestore.getInstance().collection("users").document(userModel.userId).set(userModel);

                binding.favoriteIV.setImageResource(R.drawable.favorite_icon_outline);

            } else {


                UserModel userModel = SessionManager.getInstance().getUser(ProductDetailActivity.this);

                if(userModel.userLikedPro==null){
                    userModel.userLikedPro=new ArrayList<>();
                }
                userModel.userLikedPro.add(product.proId);

                SessionManager.getInstance().setUser(ProductDetailActivity.this, userModel);

                FirebaseFirestore.getInstance().collection("users").document(userModel.userId).set(userModel);

                binding.favoriteIV.setImageResource(R.drawable.favorite_icon_sky_blue);

            }


        });



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

                                if (walletModel.userID.equals(SessionManager.getInstance().getUser(ProductDetailActivity.this).userId)) {
                                    walletModelList.add(walletModel);
                                }


                            }

                            if (walletModelList.size() <= 0) {
                                Toast.makeText(ProductDetailActivity.this, "Please Add Amount in wallet!", Toast.LENGTH_SHORT).show();
                            } else {

                                for (int i = 0; i < walletModelList.size(); i++) {

                                    if (walletModelList.get(i).proCurrency.equals(product.proexchangeCurr)) {
                                        if (walletModelList.get(i).proAmount >= product.proExchnagePrice) {

                                            walletModel = walletModelList.get(i);
                                            buyNow();
                                            return;


                                        } else {
                                            Toast.makeText(ProductDetailActivity.this, "Please Add Amount in wallet!", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }

                            }


                        }
                    }
                });
    }

    private void buyNow() {


        showLoader();
        Long pq = product.proQuantity;
        Long pex = product.proExchnagePrice;
        Long pwa = walletModel.proAmount;
        Long pfa = pwa - pex;


        FirebaseFirestore.getInstance().collection("Wallet").document(walletModel.walletId)
                .update("proAmount", pfa);

        FirebaseFirestore.getInstance().collection("Products").document(product.proId)
                .update("proExchnagePrice", 0);
        FirebaseFirestore.getInstance().collection("Products").document(product.proId)
                .update("proQuantity", 0);


        checkSelectedAmountExistInBuyerAndAddAmount();

    }

    private void checkSelectedAmountExistInBuyerAndAddAmount() {

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

                                if (walletModel.userID.equals(SessionManager.getInstance().getUser(ProductDetailActivity.this).userId)) {
                                    walletModelList.add(walletModel);
                                }


                            }

                            if (walletModelList.size() <= 0) {

                                WalletModel walletModel = new WalletModel();
                                walletModel.clientSecret = "";
                                walletModel.imageUrl = "";
                                walletModel.proAmount = product.proQuantity;
                                walletModel.proCurrency = product.proCurrency;
                                walletModel.proId = "";
                                walletModel.proName = product.proCurrency;
                                walletModel.timeStamp = System.currentTimeMillis();
                                walletModel.userID = SessionManager.getInstance().getUser(ProductDetailActivity.this).userId;

                                FirebaseFirestore.getInstance().collection("Wallet")
                                        .add(walletModel);

                                checkSelectedAmountExistInSellerAndAddAmount();

                                Log.d("amount", "onComplete:not exist " + walletModel.walletId + "/" + walletModel.proAmount + " + " + product.proQuantity + " = " + walletModel.proAmount + product.proQuantity);


                                return;


                            } else {

                                for (int i = 0; i < walletModelList.size(); i++) {

                                    if (walletModelList.get(i).proCurrency.equals(product.proCurrency)) {

                                        walletModel = walletModelList.get(i);

                                        Long pq = product.proQuantity;
                                        Long pwa = walletModel.proAmount;
                                        Long fAm = pwa + pq;


                                        Log.d("amount", "onComplete: exist " + walletModel.walletId + "/" + walletModel.proAmount + " / " + product.proQuantity + " = " + fAm);

                                        FirebaseFirestore.getInstance().collection("Wallet")
                                                .document(walletModel.walletId).update("proAmount", fAm);

                                        checkSelectedAmountExistInSellerAndAddAmount();

                                        return;

                                    }

                                }

                                WalletModel walletModel = new WalletModel();
                                walletModel.clientSecret = "";
                                walletModel.imageUrl = "";
                                walletModel.proAmount = product.proQuantity;
                                walletModel.proCurrency = product.proCurrency;
                                walletModel.proId = "";
                                walletModel.proName = product.proCurrency;
                                walletModel.timeStamp = System.currentTimeMillis();
                                walletModel.userID = SessionManager.getInstance().getUser(ProductDetailActivity.this).userId;
                                Log.d("amount", "onComplete:not exist2 " + walletModel.walletId + "/" + walletModel.proAmount + " + " + product.proQuantity + " = " + walletModel.proAmount + product.proQuantity);

                                FirebaseFirestore.getInstance().collection("Wallet")
                                        .add(walletModel);

                                checkSelectedAmountExistInSellerAndAddAmount();
                                return;
                            }


                        }
                    }
                });
    }

    private void checkSelectedAmountExistInSellerAndAddAmount() {

        walletModelListForSeller = new ArrayList<WalletModel>();

        FirebaseFirestore.getInstance().collection("Wallet")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            walletModelListForSeller.clear();


                            for (QueryDocumentSnapshot document : task.getResult()) {

                                WalletModel walletModel = document.toObject(WalletModel.class);

                                walletModel.walletId = document.getId();

                                if (walletModel.userID.equals(product.userId)) {
                                    walletModelListForSeller.add(walletModel);
                                }


                            }

                            if (walletModelListForSeller.size() <= 0) {

                                WalletModel walletModel = new WalletModel();
                                walletModel.clientSecret = "";
                                walletModel.imageUrl = "";
                                walletModel.proAmount = product.proExchnagePrice;
                                walletModel.proCurrency = product.proexchangeCurr;
                                walletModel.proId = "";
                                walletModel.proName = product.proexchangeCurr;
                                walletModel.timeStamp = System.currentTimeMillis();
                                walletModel.userID = product.userId;


                                checkSelectedAmountExistInSellerAndDeductAmount();
                                return;


                            } else {

                                for (int i = 0; i < walletModelListForSeller.size(); i++) {

                                    if (walletModelListForSeller.get(i).proCurrency.equals(product.proexchangeCurr)) {

                                        walletModelSeller = walletModelListForSeller.get(i);
                                        Long pq = walletModelSeller.proAmount;
                                        Long pwa = product.proExchnagePrice;
                                        Long fAm = pwa + pq;
                                        //  walletModelSeller.proAmount = walletModelSeller.proAmount + product.proExchnagePrice;

//                                        FirebaseFirestore.getInstance().collection("Wallet")
//                                                .document(walletModelSeller.walletId).set(walletModelSeller);

                                        FirebaseFirestore.getInstance().collection("Wallet")
                                                .document(walletModelSeller.walletId).update("proAmount", fAm);

                                        checkSelectedAmountExistInSellerAndDeductAmount();
                                        return;

                                    }

                                }

                                WalletModel walletModel = new WalletModel();
                                walletModel.clientSecret = "";
                                walletModel.imageUrl = "";
                                walletModel.proAmount = product.proExchnagePrice;
                                walletModel.proCurrency = product.proexchangeCurr;
                                walletModel.proId = "";
                                walletModel.proName = product.proexchangeCurr;
                                walletModel.timeStamp = System.currentTimeMillis();
                                walletModel.userID = product.userId;


                                FirebaseFirestore.getInstance().collection("Wallet")
                                        .add(walletModel);

                                checkSelectedAmountExistInSellerAndDeductAmount();
                                return;
                            }


                        }
                    }
                });
    }

    private void checkSelectedAmountExistInSellerAndDeductAmount() {

        walletModelListForSeller = new ArrayList<WalletModel>();

        FirebaseFirestore.getInstance().collection("Wallet")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            walletModelListForSeller.clear();


                            for (QueryDocumentSnapshot document : task.getResult()) {

                                WalletModel walletModel = document.toObject(WalletModel.class);

                                walletModel.walletId = document.getId();

                                if (walletModel.userID.equals(product.userId)) {
                                    walletModelListForSeller.add(walletModel);
                                }


                            }

                            if (walletModelListForSeller.size() <= 0) {

                                updateOrdersAndNotify();


                            } else {

                                for (int i = 0; i < walletModelListForSeller.size(); i++) {

                                    if (walletModelListForSeller.get(i).proCurrency.equals(product.proCurrency)) {

                                        walletModelSeller = walletModelListForSeller.get(i);

                                        Long pq = walletModelSeller.proAmount;
                                        Long pwa = product.proQuantity;
                                        Long fAm = pq - pwa;

                                        // walletModelSeller.proAmount = walletModelSeller.proAmount - product.proQuantity;

                                        FirebaseFirestore.getInstance().collection("Wallet")
                                                .document(walletModelSeller.walletId).update("proAmount", fAm);

                                        updateOrdersAndNotify();
                                        return;

                                    }
                                    updateOrdersAndNotify();
                                }

                            }


                        }
                    }
                });
    }


    public void updateOrdersAndNotify() {
        OrderModel orderModel = new OrderModel();
        orderModel.proAmount = product.proQuantity;
        orderModel.proCurrency = product.proCurrency;
        orderModel.proImage = "";
        orderModel.proName = product.proCurrency;
        orderModel.proStatus = "Purchased";
        orderModel.userID = SessionManager.getInstance().getUser(ProductDetailActivity.this).userId;

        FirebaseFirestore.getInstance().collection("Order").add(orderModel);

        NotificationModel notificationModel = new NotificationModel();

        notificationModel.setConversationID("");
        notificationModel.setImage("");
        notificationModel.setIsSent("0");
        notificationModel.setMessage("New purchase of " + product.proCurrency + " has been made.");
        notificationModel.setSenderId(SessionManager.getInstance().getUser(ProductDetailActivity.this).userId);
        notificationModel.setTime(System.currentTimeMillis() + "");
        notificationModel.setTitle(product.proCurrency + " Purchased");
        notificationModel.setUserID(product.userId);

        FirebaseFirestore.getInstance().collection("Notifications").add(notificationModel);


        getUserData(notificationModel);


    }

    ListenerRegistration snapshotListener=null;
    private void getUserData(NotificationModel notificationModel) {

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        DocumentReference reference = firebaseFirestore.collection("users").document(product.userId);

        snapshotListener = reference.addSnapshotListener((value, error) -> {


            UserModel userModel = value.toObject(UserModel.class);
            sendNotification(notificationModel, userModel.userToken);

            snapshotListener.remove();



        });



    }

    private void sendNotification(NotificationModel notificationModel, String token) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", notificationModel.getTitle())
                .addFormDataPart("description", notificationModel.getMessage())
                .addFormDataPart("token_id", token)
                .addFormDataPart("user_id", product.userId)
                .addFormDataPart("papi", product.userId)
                .addFormDataPart("cnid", "")

                .build();
        NotificationController.getApi().setNotification(requestBody)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        hideLoader();
                        finish();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        finish();
                    }
                });


    }

    public void checkAlreadyExist() {
        FirebaseInstances.chatCollection
                .whereEqualTo("participants." + product.userId, true)
                .get()
                .addOnCompleteListener(task -> {


                    if (task.isSuccessful()) {


                        for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {

                            LastMessageModel lastMessageModel = snapshot.get("lastMessage", LastMessageModel.class);

                            if (lastMessageModel != null) {

                                lastMessageModel.conversationId = snapshot.getId();

                                if (lastMessageModel.fromID.equalsIgnoreCase(getUserId())
                                        || lastMessageModel.toID.equals(getUserId())) {

                                    startChat(false, lastMessageModel.conversationId);

                                    return;

                                }
                            }
                        }

                        startChat(true, "");


                    } else {
                        startChat(true, "");

                    }
                });

    }

    private void startChat(boolean isFromNew, String cid) {

        Intent intent = new Intent(ProductDetailActivity.this, Chat.class);

        ParcelableChat parcelableChat = new ParcelableChat();

        if (isFromNew) {

            parcelableChat.setConversationID(null);
            parcelableChat.setSelectedUserId(product.userId);
            parcelableChat.setSelectedUserName("");
            parcelableChat.setTypeStatus("true");
            parcelableChat.setProId(product.proId);



        } else {

            parcelableChat.setConversationID(cid);
            parcelableChat.setSelectedUserId(product.userId);
            parcelableChat.setSelectedUserName("");
            parcelableChat.setTypeStatus("false");
            parcelableChat.setProId(product.proId);

        }

        intent.putExtra("parcelableChat", parcelableChat);

        startActivity(intent);

    }

    private void setView() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        }

        binding.nameTV.setText(product.proTitle);
        binding.quantityAndProCurrencyTV.setText(product.proQuantity + " " + product.proCurrency);
        binding.exCurrencyAndAmountTV.setText("Exchange with " + product.proexchangeCurr + " at " + product.proExchnagePrice);
        binding.descriptionTV.setText(product.proDescription);

        if (isFavorite.equals("yes"))

            binding.favoriteIV.setImageResource(R.drawable.favorite_icon_sky_blue);

        else

            binding.favoriteIV.setImageResource(R.drawable.favorite_icon_outline);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();

    }
}