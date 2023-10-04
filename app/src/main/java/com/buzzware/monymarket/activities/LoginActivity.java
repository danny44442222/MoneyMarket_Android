package com.buzzware.monymarket.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.buzzware.monymarket.Models.UserModel;
import com.buzzware.monymarket.R;
import com.buzzware.monymarket.classes.SessionManager;
import com.buzzware.monymarket.databinding.ActivityLoginBinding;
import com.buzzware.monymarket.facebook.FacebookHelper;
import com.buzzware.monymarket.facebook.FacebookResponse;
import com.buzzware.monymarket.facebook.FacebookUser;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.williammora.snackbar.Snackbar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends BaseActivity {

    ActivityLoginBinding binding;

    FacebookHelper facebookHelper;

    CallbackManager callbackManager;

    CallbackManager mCallbackManager;

    GoogleSignInClient mGoogleSignInClient;

    private static final int RC_SIGN_IN = 100;

    boolean fromStartup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fromStartup = true;

        setView();

        setListener();

        setUpSocial();

    }

    private void setUpSocial() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        setupFacebook();

    }

    private void setView() {

        binding.includeBar.titleTV.setText("Login to your Account");

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        }

    }

    private void setListener() {

        binding.includeBar.backIV.setOnClickListener(v -> {

            finish();

        });

        binding.loginBtn.setOnClickListener(v -> {

            if (isValid())
                loginNow();

        });

        binding.signUpTV.setOnClickListener(v -> {

            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));

        });

        binding.facebookBtn.setOnClickListener(v -> {
            facebookHelper.performSignIn(LoginActivity.this);
        });

        binding.googleBtn.setOnClickListener(v -> {
            signInWithGoogle();
        });

    }

    void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private boolean isValid() {

        boolean check = false;

        if (binding.emailET.getText().toString().trim().isEmpty()) {

            showMessage("Email Required");

            return check;

        }

        if (binding.passwordET.getText().toString().trim().isEmpty()) {

            showMessage("Password Required!");

            return check;

        }

        check = true;

        return check;

    }

    private void loginNow() {

        showLoader();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(binding.emailET.getText().toString().trim(), binding.passwordET.getText().toString())
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            getUser(mAuth.getCurrentUser().getUid());

                        } else {

                            hideLoader();

                            showMessage("error" + task.getException().getLocalizedMessage());


                        }
                    }
                });


    }
    ListenerRegistration snapshotListener=null;

    private void getUser(String uid) {



        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        DocumentReference reference = firebaseFirestore.collection("users").document(uid);

        snapshotListener = reference.addSnapshotListener((value, error) -> {



            hideLoader();

            UserModel userModel = value.toObject(UserModel.class);

            userModel.userId = uid;

            SessionManager.getInstance().setUser(LoginActivity.this, userModel);

            showMessage("Successfully Login!");
            snapshotListener.remove();
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            finish();



        });


    }

    private void showMessage(String message) {

        Snackbar.with(LoginActivity.this)
                .text(message)
                .show(LoginActivity.this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    private void setUser(FirebaseUser user1) {
        final DocumentReference docRef = db.collection("users").document(user1.getUid());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {

                hideLoader();
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    mAuth.signOut();
                    return;
                }

                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";

                if (snapshot != null && snapshot.exists()) {

                    Log.d(TAG, source + " data: " + snapshot.getData());

                    UserModel user = snapshot.toObject(UserModel.class);
                    user.userId = snapshot.getId();
                    SessionManager.getInstance().setUser(LoginActivity.this, user);

                    if (fromStartup) {
                        fromStartup = false;
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }

                } else {
                    hideLoader();
                    mAuth.signOut();
                    Log.d(TAG, source + " data: null");
                }
            }
        });
    }

    private void setupFacebook() {

        FacebookSdk.setApplicationId(getResources().getString(R.string.facebook_app_id));

        callbackManager = CallbackManager.Factory.create();
        facebookHelper = new FacebookHelper(response, "id,name", LoginActivity.this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
//                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                //showErrorAlert(""+e.getLocalizedMessage()+""+task.getException().getLocalizedMessage());
                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed", e);
            }
        } else
            // Pass the activity result back to the Facebook SDK
//            mCallbackManager.onActivityResult(requestCode, resultCode, data);
            facebookHelper.onActivityResult(requestCode, resultCode, data);
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            getMyProfile(user);

                        } else {

                            if (task.getException() != null)
                                showErrorAlert(task.getException().getLocalizedMessage());

                            else
                                showErrorAlert("Google Auth Failed");
                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            updateUI(null);
                        }
                    }
                });
    }

    void getMyProfile(FirebaseUser user) {


        FirebaseFirestore.getInstance().collection("users")
                .document(user.getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (value != null && value.exists()) {
                            setUser(mAuth.getCurrentUser());
                        } else {
                            addUserdetails(user);
                        }
                    }
                });
    }

    FacebookResponse response = new FacebookResponse() {

        @Override
        public void onFbSignTnFail() {

            Toast.makeText(LoginActivity.this, "Try Again With Facebook", Toast.LENGTH_LONG).show();

        }

        @Override
        public void onFbSignInSuccess() {
            Toast.makeText(LoginActivity.this, "Sign in Successful", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFbSignOut() {

            Toast.makeText(LoginActivity.this, "Sign out Successful", Toast.LENGTH_LONG).show();

        }


        @Override
        public void onFbProfileRecieved(FacebookUser facebookUser) {

            showLoader();

            AccessToken accessToken = AccessToken.getCurrentAccessToken();

            facebookUser.token = accessToken;

            accessToken.getDeclinedPermissions();

            handleFacebookAccessToken(facebookUser);

        }

    };


    private void handleFacebookAccessToken(final FacebookUser user) {

        AuthCredential credential = FacebookAuthProvider.getCredential(user.token.getToken());

        FirebaseAuth.getInstance()
                .signInWithCredential(credential)
                .addOnCompleteListener(LoginActivity.this, task -> {

                    if (task.isSuccessful()) {

                        checkFacebookUserExist(user);


                    } else {

                        Toast.makeText(LoginActivity.this, "Try Again With Facebook", Toast.LENGTH_LONG).show();

                    }

                });

    }

    private void checkFacebookUserExist(FacebookUser facebookUser) {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        FirebaseFirestore.getInstance().collection("users")
                .document(currentUser.getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (value != null && value.exists()) {
                            setUser(mAuth.getCurrentUser());
                        } else {
                            addUserdetails(facebookUser);
                        }
                    }
                });


    }

    private void addUserdetails(final FacebookUser facebookUser) {

        Map<String, String> userData = new HashMap<>();
        userData.put("fullname", facebookUser.name);
        userData.put("email", mAuth.getCurrentUser().getEmail());
        userData.put("userPhone", "");
        userData.put("password", "");
        userData.put("imageUrl", facebookUser.profilePic);
        userData.put("userToken", "");
        userData.put("userBio", "");
        userData.put("userCurrency", "\uD83C\uDDFA\uD83C\uDDF8 USD");
        userData.put("userId", mAuth.getCurrentUser().getUid());
        userData.put("dateOfBirth", "");

        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        setUser(user);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        hideLoader();
                        mAuth.signOut();
                    }
                });
    }

    private void addUserdetails(final FirebaseUser googleUser) {

        Map<String, String> userData = new HashMap<>();
        userData.put("fullname", googleUser.getDisplayName());
        userData.put("email", mAuth.getCurrentUser().getEmail());
        userData.put("userPhone", "");
        userData.put("password", "");
        userData.put("imageUrl", "");
        userData.put("userToken", "");
        userData.put("userBio", "");
        userData.put("userCurrency", "\uD83C\uDDFA\uD83C\uDDF8 USD");
        userData.put("userId", mAuth.getCurrentUser().getUid());
        userData.put("dateOfBirth", "");

        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        setUser(user);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        hideLoader();
                        mAuth.signOut();
                    }
                });
    }
}