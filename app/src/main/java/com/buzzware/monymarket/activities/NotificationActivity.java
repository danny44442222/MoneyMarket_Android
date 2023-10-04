package com.buzzware.monymarket.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.buzzware.monymarket.Models.NotificationModel;
import com.buzzware.monymarket.R;
import com.buzzware.monymarket.adapters.NotificationAdapter;
import com.buzzware.monymarket.adapters.OrderAdapter;
import com.buzzware.monymarket.databinding.ActivityMyOrderBinding;
import com.buzzware.monymarket.databinding.ActivityNotificationBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationActivity extends BaseActivity {

    ActivityNotificationBinding binding;
    List<NotificationModel> notificationsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListener();
        getNotificationsList();

    }


    private void setListener() {

        binding.backIV.setOnClickListener(v -> {
            finish();
        });

    }


    private void getNotificationsList() {
        if (isOnline()) {
            showLoader();
            notificationsList = new ArrayList<NotificationModel>();

            db.collection("Notifications")
                    .whereEqualTo("userID", getUserId())
//                    .orderBy("time", Query.Direction.ASCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w("TAG", "Listen failed.", e);
                                hideLoader();
                                return;
                            }
                            notificationsList.clear();
                            for (QueryDocumentSnapshot doc : value) {
                                if (doc != null) {
                                    NotificationModel notificationModel = doc.toObject(NotificationModel.class);
//                                    categoriesModel.setId(doc.getId());
                                    notificationsList.add(notificationModel);
                                }
                            }
                            initRecyclerViewNotifications();
                        }
                    });
        }
    }

    private void initRecyclerViewNotifications() {
        LinearLayoutManager layoutManager5 = new LinearLayoutManager(NotificationActivity.this, LinearLayoutManager.VERTICAL, false);
        binding.notificationRV.setLayoutManager(layoutManager5);

        if (notificationsList.size() == 0) {
            Snackbar.make(binding.getRoot(), "You have no Notifications yet!!", Snackbar.LENGTH_LONG)
                    .setAction("CLOSE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    })
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                    .show();
        }
        Collections.reverse(notificationsList);
        NotificationAdapter adapter = new NotificationAdapter(NotificationActivity.this, notificationsList);
        binding.notificationRV.setItemAnimator(new DefaultItemAnimator());
        binding.notificationRV.setAdapter(adapter);
        hideLoader();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}