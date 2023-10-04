package com.buzzware.monymarket.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.buzzware.monymarket.activities.messages.chatList.ChatFragment;
import com.buzzware.monymarket.classes.AddPlaceEvent;
import com.buzzware.monymarket.classes.Constants;
import com.buzzware.monymarket.classes.OfferMessageEvent;
import com.buzzware.monymarket.databinding.ActivityHomeBinding;
import com.buzzware.monymarket.fragments.CartFragment;
import com.buzzware.monymarket.fragments.HomeFragment;
import com.buzzware.monymarket.fragments.OfferFragment;
import com.buzzware.monymarket.fragments.PlaceAddFragment;
import com.buzzware.monymarket.fragments.ProfileFragment;
import com.buzzware.monymarket.retrofit.Controller;
import com.buzzware.monymarket.retrofit.currencyRes.CurrencyResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity {

    ActivityHomeBinding binding;

    public static boolean isOffer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setFireBaseToken();
        setListener();
        loadFragment(new HomeFragment());

        getCurrencyRate();

        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .update("onlineStatus", true);

    }

    private void setFireBaseToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        return;

                    }

                    String token = task.getResult();

                    if (FirebaseAuth.getInstance().getCurrentUser() != null)
                        FirebaseFirestore.getInstance().collection("users")
                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("userToken", task.getResult() + "");


                });
    }


    private void loadFragment(Fragment fragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(binding.container.getId(), fragment);

        transaction.addToBackStack(null);

        transaction.commit();

    }


    private void setListener() {


        binding.homeIV.setOnClickListener(v -> {
            loadFragment(new HomeFragment());
        });

        binding.offerIV.setOnClickListener(v -> {
            loadFragment(new OfferFragment());
        });

        binding.placeIV.setOnClickListener(v -> {
            loadFragment(new PlaceAddFragment());
        });

        binding.chatIV.setOnClickListener(v -> {
            loadFragment(new ChatFragment());
        });


        binding.profileIV.setOnClickListener(v -> {
            loadFragment(new ProfileFragment());
        });


    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            finish();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    public void getCurrencyRate() {

        Controller.getApi().getCurrencyRate()
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        if (response.body() != null) {

                            parseXml(response.body());

                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });

    }

    public void parseXml(String res) {

//        XmlToJson xmlToJson = new XmlToJson.Builder(res)
//                .build();
//
//        String a = xmlToJson + "";
//
//        //below 6 line : just for formatting string
//        a = a.replace("gesmes:Envelope", "gesmesEnvelope");
//        a = a.replace("xmlns:gesmes", "xmlnsgesmes");
//        a = a.replace("\\n\\t", "");
//        a = a.replace("\\t", "");
//        a = a.replaceFirst("Cube", "MainCu");
//        a = a.replaceFirst("Cube", "SecCube");

        Constants.currencyApiResponseData = res;

        //  Toast.makeText(HomeActivity.this, date+ "", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isOffer) {
            isOffer = false;
            loadFragment(new OfferFragment());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {

            if (FirebaseAuth.getInstance().getCurrentUser().getUid() != null)
                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .update("onlineStatus", false);
        }catch (Exception e){

        }
    }
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AddPlaceEvent event) {
        // Do something

        loadFragment(new HomeFragment());
    }
}