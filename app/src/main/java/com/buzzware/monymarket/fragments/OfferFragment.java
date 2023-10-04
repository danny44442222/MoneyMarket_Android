package com.buzzware.monymarket.fragments;

import static com.buzzware.monymarket.activities.BaseActivity.getUserId;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.buzzware.monymarket.Models.CartModel;
import com.buzzware.monymarket.Models.CartModelForViewCart;
import com.buzzware.monymarket.Models.NotificationModel;
import com.buzzware.monymarket.Models.OfferModel;
import com.buzzware.monymarket.Models.OrderModel;
import com.buzzware.monymarket.Models.ProductModel;
import com.buzzware.monymarket.Models.UserModel;
import com.buzzware.monymarket.Models.WalletModel;
import com.buzzware.monymarket.R;
import com.buzzware.monymarket.activities.HomeActivity;
import com.buzzware.monymarket.activities.LoginActivity;
import com.buzzware.monymarket.activities.ProductDetailActivity;
import com.buzzware.monymarket.activities.messages.chat.mo.ParcelableChat;
import com.buzzware.monymarket.activities.messages.chat.mo.SendConversationModel;
import com.buzzware.monymarket.activities.messages.chat.mo.SendLastMessageModel;
import com.buzzware.monymarket.adapters.OfferAdapter;
import com.buzzware.monymarket.adapters.ProductAdapter;
import com.buzzware.monymarket.classes.FirebaseInstances;
import com.buzzware.monymarket.classes.SessionManager;
import com.buzzware.monymarket.databinding.FragmentOfferBinding;
import com.buzzware.monymarket.interfaces.OfferCallback;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfferFragment extends BaseFragment implements OfferCallback {


    FragmentOfferBinding binding;
    List<OfferModel> offersList;


    List<WalletModel> walletModelList;
    List<WalletModel> walletModelListForSeller;

    private OfferModel product;
    WalletModel walletModel;
    WalletModel walletModelSeller;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentOfferBinding.inflate(inflater);

        getOffers();

        return binding.getRoot();

    }

    private void getOffers() {

        offersList = new ArrayList<OfferModel>();

        FirebaseFirestore.getInstance().collection("Offers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            offersList.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                OfferModel offerModel = document.toObject(OfferModel.class);

                                if (offerModel != null) {
                                    offerModel.id = document.getId();
                                    if (offerModel.userID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                        offersList.add(offerModel);
                                }
                            }

                            if (offersList.size() > 0)
                                setProductAdapter();

                        } else {

                        }
                    }
                });
    }


    private void setProductAdapter() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());

        binding.offerRV.setLayoutManager(layoutManager);

        OfferAdapter adapter = new OfferAdapter(getContext(), offersList, this);

        binding.offerRV.setItemAnimator(new DefaultItemAnimator());

        binding.offerRV.setAdapter(adapter);

    }


    @Override
    public void onItemClick(String type, OfferModel offerModel) {


        if (type.equals("rejected")) {

            //  FirebaseFirestore.getInstance().collection("Offers").document(offerModel.id).delete();


            android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(getContext())
                    .setMessage("Are you sure you want to reject this Offer?")
                    .setTitle("Alert")
                    .setPositiveButton("Reject", (dialog, which) -> {
                        dialog.dismiss();

                        getUserData(offerModel.senderID, offerModel);

                        sendMessage("Your "+offerModel.proAmount+" "+offerModel.proCurrency+" offer has been rejected" ,offerModel);


                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .setCancelable(false)
                    .create();



            alertDialog.show();


        } else {

            product = offerModel;
            checkSelectedAmountExist();

        }


    }

    ListenerRegistration snapshotListener = null;

    private void getUserData(String id, OfferModel offerModel) {


        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        DocumentReference reference = firebaseFirestore.collection("users").document(id);

        snapshotListener = reference.addSnapshotListener((value, error) -> {


            UserModel userModel = value.toObject(UserModel.class);

            sendNotification(userModel.userToken, id, offerModel);
            snapshotListener.remove();


        });


    }


    private void sendNotification(String token, String id, OfferModel offerModel) {


        NotificationModel notificationModel = new NotificationModel();

        notificationModel.setConversationID("");
        notificationModel.setImage("");
        notificationModel.setIsSent("0");
        notificationModel.setMessage("Your" + offerModel.proAmount + " " + offerModel.proCurrency + "offer has been cancelled.");
        notificationModel.setSenderId(SessionManager.getInstance().getUser(getContext()).userId);
        notificationModel.setTime(System.currentTimeMillis() + "");
        notificationModel.setTitle("Offer Cancelled");
        notificationModel.setUserID(id);

        //  FirebaseFirestore.getInstance().collection("Notifications").add(notificationModel);


        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", notificationModel.getTitle())
                .addFormDataPart("description", notificationModel.getMessage())
                .addFormDataPart("token_id", token)
                .addFormDataPart("user_id", id)
                .addFormDataPart("papi", id)
                .addFormDataPart("cnid", "")

                .build();
        NotificationController.getApi().setNotification(requestBody)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        Log.d(TAG, "onResponse: " + response + "/" + token);
                        getOffers();

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        getOffers();
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

                                if (walletModel.userID.equals(SessionManager.getInstance().getUser(getContext()).userId)) {
                                    walletModelList.add(walletModel);
                                }


                            }

                            if (walletModelList.size() <= 0) {
                                Toast.makeText(getContext(), "Please Add Amount in wallet!", Toast.LENGTH_SHORT).show();
                            } else {

                                for (int i = 0; i < walletModelList.size(); i++) {

                                    if (walletModelList.get(i).proCurrency.equals(product.exchangeCurrency)) {
                                        if (walletModelList.get(i).proAmount >= product.exchangeAmount) {

                                            walletModel = walletModelList.get(i);
                                            buyNow();

                                            sendMessage("Your "+product.proAmount+" "+product.proCurrency+" offer has been accepted" ,product);


                                            return;


                                        } else {
                                            Toast.makeText(getContext(), "Please Add Amount in wallet!", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }

                            }


                        }
                    }
                });
    }

    public void sendMessage(String s, OfferModel offerModel) {


            long currentTimeStamp = System.currentTimeMillis();

            SendLastMessageModel sendLastMessageModel = new SendLastMessageModel(s,

                    getUserId(), String.valueOf(currentTimeStamp), offerModel.senderID, "text", false, (int) currentTimeStamp,offerModel.proId);

            HashMap<String, Boolean> participents = new HashMap<>();

            participents.put(getUserId(), true);

            participents.put(offerModel.senderID, true);

            SendConversationModel sendConversationModel = new SendConversationModel(s,
                    getUserId(), String.valueOf(currentTimeStamp), "text", false, currentTimeStamp, offerModel.senderID,offerModel.proId);

            HashMap<String, Object> lasthashMap = new HashMap<>();

            lasthashMap.put("lastMessage", sendLastMessageModel);

            lasthashMap.put("participants", participents);

//            conversationID = UUID.randomUUID().toString();

       // Log.d("offerre", "sendMessage: "+offerModel.conversationID);

            FirebaseInstances.chatCollection.document(offerModel.ConversationID).collection("Conversations").document(String.valueOf(currentTimeStamp)).set(sendConversationModel);
            FirebaseInstances.chatCollection.document(offerModel.ConversationID).set(lasthashMap);

    }


    private void buyNow() {


        showLoader();
        Long pq = product.proAmount;
        Long pex = product.exchangeAmount;
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

                                if (walletModel.userID.equals(SessionManager.getInstance().getUser(getContext()).userId)) {
                                    walletModelList.add(walletModel);
                                }


                            }

                            if (walletModelList.size() <= 0) {

                                WalletModel walletModel = new WalletModel();
                                walletModel.clientSecret = "";
                                walletModel.imageUrl = "";
                                walletModel.proAmount = product.proAmount;
                                walletModel.proCurrency = product.proCurrency;
                                walletModel.proId = "";
                                walletModel.proName = product.proCurrency;
                                walletModel.timeStamp = System.currentTimeMillis();
                                walletModel.userID = SessionManager.getInstance().getUser(getContext()).userId;

                                FirebaseFirestore.getInstance().collection("Wallet")
                                        .add(walletModel);

                                checkSelectedAmountExistInSellerAndAddAmount();


                                return;


                            } else {

                                for (int i = 0; i < walletModelList.size(); i++) {

                                    if (walletModelList.get(i).proCurrency.equals(product.proCurrency)) {

                                        walletModel = walletModelList.get(i);

                                        Long pq = product.proAmount;
                                        Long pwa = walletModel.proAmount;
                                        Long fAm = pwa + pq;


                                        FirebaseFirestore.getInstance().collection("Wallet")
                                                .document(walletModel.walletId).update("proAmount", fAm);

                                        checkSelectedAmountExistInSellerAndAddAmount();

                                        return;

                                    }

                                }

                                WalletModel walletModel = new WalletModel();
                                walletModel.clientSecret = "";
                                walletModel.imageUrl = "";
                                walletModel.proAmount = product.proAmount;
                                walletModel.proCurrency = product.proCurrency;
                                walletModel.proId = "";
                                walletModel.proName = product.proCurrency;
                                walletModel.timeStamp = System.currentTimeMillis();
                                walletModel.userID = SessionManager.getInstance().getUser(getContext()).userId;

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

                                if (walletModel.userID.equals(product.senderID)) {
                                    walletModelListForSeller.add(walletModel);
                                }


                            }

                            if (walletModelListForSeller.size() <= 0) {

                                WalletModel walletModel = new WalletModel();
                                walletModel.clientSecret = "";
                                walletModel.imageUrl = "";
                                walletModel.proAmount = product.exchangeAmount;
                                walletModel.proCurrency = product.exchangeCurrency;
                                walletModel.proId = "";
                                walletModel.proName = product.exchangeCurrency;
                                walletModel.timeStamp = System.currentTimeMillis();
                                walletModel.userID = product.senderID;


                                checkSelectedAmountExistInSellerAndDeductAmount();
                                return;


                            } else {

                                for (int i = 0; i < walletModelListForSeller.size(); i++) {

                                    if (walletModelListForSeller.get(i).proCurrency.equals(product.exchangeCurrency)) {

                                        walletModelSeller = walletModelListForSeller.get(i);
                                        Long pq = walletModelSeller.proAmount;
                                        Long pwa = product.exchangeAmount;
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
                                walletModel.proAmount = product.exchangeAmount;
                                walletModel.proCurrency = product.exchangeCurrency;
                                walletModel.proId = "";
                                walletModel.proName = product.exchangeCurrency;
                                walletModel.timeStamp = System.currentTimeMillis();
                                walletModel.userID = product.senderID;


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

                                if (walletModel.userID.equals(product.senderID)) {
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
                                        Long pwa = product.proAmount;
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
        orderModel.proAmount = product.proAmount;
        orderModel.proCurrency = product.proCurrency;
        orderModel.proImage = "";
        orderModel.proName = product.proCurrency;
        orderModel.proStatus = "Purchased";
        orderModel.userID = SessionManager.getInstance().getUser(getContext()).userId;

        FirebaseFirestore.getInstance().collection("Order").add(orderModel);

        NotificationModel notificationModel = new NotificationModel();

        notificationModel.setConversationID("");
        notificationModel.setImage("");
        notificationModel.setIsSent("0");
        notificationModel.setMessage("New purchase of " + product.proCurrency + " has been made.");
        notificationModel.setSenderId(SessionManager.getInstance().getUser(getContext()).userId);
        notificationModel.setTime(System.currentTimeMillis() + "");
        notificationModel.setTitle(product.proCurrency + " Purchased");
        notificationModel.setUserID(product.senderID);

        FirebaseFirestore.getInstance().collection("Notifications").add(notificationModel);


        getUserDataNoti(notificationModel);


    }

    private void getUserDataNoti(NotificationModel notificationModel) {

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        DocumentReference reference = firebaseFirestore.collection("users").document(product.senderID);

        snapshotListener = reference.addSnapshotListener((value, error) -> {


            UserModel userModel = value.toObject(UserModel.class);
            sendNotificationNo(notificationModel, userModel.userToken);

            snapshotListener.remove();


        });

    }

    private void sendNotificationNo(NotificationModel notificationModel, String token) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", notificationModel.getTitle())
                .addFormDataPart("description", notificationModel.getMessage())
                .addFormDataPart("token_id", token)
                .addFormDataPart("user_id", product.senderID)
                .addFormDataPart("papi", product.senderID)
                .addFormDataPart("cnid", "")

                .build();
        NotificationController.getApi().setNotification(requestBody)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        getOffers();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        getOffers();
                    }
                });


    }


}