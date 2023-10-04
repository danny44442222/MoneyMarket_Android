package com.buzzware.monymarket.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class BaseActivity extends AppCompatActivity {

    static final String TAG = "FireBase";

    ProgressDialog progressDialog;

    public AlertDialog errorDialog;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

    }

    public void showLoader() {

        if (BaseActivity.this.isFinishing() || BaseActivity.this.isDestroyed())

            return;

        if (progressDialog != null && progressDialog.isShowing())

            progressDialog.dismiss();

        progressDialog = new ProgressDialog(BaseActivity.this);
        progressDialog.setTitle("");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);

        progressDialog.show();
    }

    public void hideKeyboard(View view) {

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }


    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (!(networkInfo != null && networkInfo.isConnected())) {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void showErrorAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this)
                .setTitle("Alert")
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        try {
            errorDialog = builder.create();
            errorDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void hideLoader() {

        if (BaseActivity.this.isDestroyed() || BaseActivity.this.isFinishing())

            return;

        if (progressDialog != null && progressDialog.isShowing())

            progressDialog.dismiss();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }

    public static String getUserId() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            user.getUid();

            return user.getUid();

        }

        return "";
    }
}