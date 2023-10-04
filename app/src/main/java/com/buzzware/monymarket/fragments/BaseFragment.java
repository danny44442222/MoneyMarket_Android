package com.buzzware.monymarket.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.fragment.app.Fragment;


public class BaseFragment extends Fragment {


    public ProgressDialog progressDialog;
    
    public BaseFragment() {
        // Required empty public constructor
    }


    static final String TAG = "FireBase";
    public ProgressDialog mDialog;

//    FirebaseAuth mAuth;
//    DatabaseReference mDatabase;

    public AlertDialog errorDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);

//        mAuth = FirebaseAuth.getInstance();

//        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void hideKeyboard(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(!(networkInfo != null && networkInfo.isConnected())){
            mDialog.dismiss();
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void showErrorAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
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

    public void showLoader() {

        if (getActivity().isFinishing() || getActivity().isDestroyed()) {
            return;

        }
        if (progressDialog != null && progressDialog.isShowing())

            progressDialog.dismiss();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("");
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);

        progressDialog.show();
    }

    public void hideLoader() {

        if (getActivity().isDestroyed() || getActivity().isFinishing())

            return;

        if (progressDialog != null && progressDialog.isShowing())

            progressDialog.dismiss();
    }
}