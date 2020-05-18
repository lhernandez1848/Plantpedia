package io.github.lhernandez1848.plantpedia;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 201;
    private static final int GOOGLE_REQUEST_CODE = 9001;

    private GoogleSignInClient googleSignInClient;

   // private CallbackManager mCallbackManager;

    private FirebaseAuth firebaseAuth;

    Handler handler;
    Runnable runnable;

    // declare widgets
    LinearLayout splash_first_layout, splash_second_layout, email_text_fields, sign_up_text_fields;
    TextView start_welcome_message, start_app_name, tvError, tvVerifyEmail, tvSignUp, tvNewEmailError;
    EditText fieldEmail, fieldPassword, etNewEmail, etNewPassword, etNewConfirmPassword;
    ImageView logo_image_view;
    Button emailSignIn, emailSignInVerify, btnEmailSignUp;
    SignInButton googleSignIn;
    // LoginButton facebookSignIn;

    // declare variables
    String typedEmail, typedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // initialize widgets
        // Linear Layouts
        splash_first_layout = findViewById(R.id.splash_first_layout);
        splash_second_layout = findViewById(R.id.splash_second_layout);
        email_text_fields = findViewById(R.id.email_text_fields);
        sign_up_text_fields = findViewById(R.id.sign_up_text_fields);

        // Text Views
        tvError = findViewById(R.id.tvError);
        start_welcome_message = findViewById(R.id.start_welcome_message);
        start_app_name = findViewById(R.id.start_app_name);
        tvVerifyEmail = findViewById(R.id.tvVerifyEmail);
        tvSignUp = findViewById(R.id.tvSignUp);
        tvNewEmailError = findViewById(R.id.tvNewEmailError);

        // Edit Texts
        fieldEmail = findViewById(R.id.fieldEmail);
        fieldPassword = findViewById(R.id.fieldPassword);
        etNewEmail = findViewById(R.id.etNewEmail);
        etNewPassword = findViewById(R.id.etNewPassword);
        etNewConfirmPassword = findViewById(R.id.etNewConfirmPassword);

        // Image Views
        logo_image_view = findViewById(R.id.logo_image_view);

        // Buttons
        emailSignIn = findViewById(R.id.btnEmailSignIn);
        emailSignInVerify = findViewById(R.id.btnVerifyEmailSignIn);
        googleSignIn = findViewById(R.id.btnGoogleSignIn);
        btnEmailSignUp = findViewById(R.id.btnEmailSignUp);
        // facebookSignIn = findViewById(R.id.btnFacebookSignIn);

        // initialize variables
        typedEmail = "";
        typedPassword = "";

        // set listeners
        emailSignIn.setOnClickListener(this);
        emailSignInVerify.setOnClickListener(this);
        googleSignIn.setOnClickListener(this);
        // facebookSignIn.setOnClickListener(this);
        tvVerifyEmail.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);
        btnEmailSignUp.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

       // mCallbackManager = CallbackManager.Factory.create();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                splash_first_layout.setVisibility(View.VISIBLE);
                splash_second_layout.setVisibility(View.VISIBLE);

                start_welcome_message.setVisibility(View.GONE);
                start_app_name.setVisibility(View.GONE);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        logo_image_view.getWidth() - 150,
                        logo_image_view.getHeight() - 150);
                layoutParams.setMargins(0, 20, 0, 0);

                logo_image_view.setLayoutParams(layoutParams);
            }
        };

        try {
            Intent intent = getIntent();
            String action = intent.getStringExtra("action");

            if (action.equals("SIGN_OUT")){
                signOut();
            }
        } catch (Exception e){

        }

        if (!checkStoragePermission()){
            requestStoragePermission();
        }else{
            if(!checkCameraPermission()){
                requestCameraPermission();
            }
        }

        if(checkCameraPermission() && checkStoragePermission()) {
            transitionToNext();
        }
    }

    @Override
    public void onClick(View view) {

            switch (view.getId()){
                case R.id.btnEmailSignIn:
                    email_text_fields.setVisibility(View.VISIBLE);
                    emailSignIn.setVisibility(View.GONE);
                    googleSignIn.setVisibility(View.GONE);

                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    loginCheck(user);
                    break;
                case R.id.btnVerifyEmailSignIn:
                    verifyEmailFields();
                    break;
                case R.id.tvVerifyEmail:
                    verifyEmail();
                    break;
                case R.id.tvSignUp:
                    email_text_fields.setVisibility(View.GONE);
                    sign_up_text_fields.setVisibility(View.VISIBLE);
                    break;
                case R.id.btnEmailSignUp:
                    emailSignUp();
                    break;
                case R.id.btnGoogleSignIn:
                    email_text_fields.setVisibility(View.GONE);
                    signInWithGoogle();
                    break;
                /*case R.id.btnFacebookSignIn:
                    facebookSignIn.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            System.out.println("facebook:onSuccess:" + loginResult);
                            handleFacebookAccessToken(loginResult.getAccessToken());
                        }

                        @Override
                        public void onCancel() {}

                        @Override
                        public void onError(FacebookException error) {}
                    });

                    break;*/
            }
    }


    public boolean checkCameraPermission() {
        // Permission is not granted
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkStoragePermission() {
        // Permission is not granted
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!checkCameraPermission()){
                    requestCameraPermission();
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        showMessage();
                    }
                }
            }
        }
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                transitionToNext();

            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        showMessage();
                    }
                }
            }
        }

    }

    private void showMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Permission Denied");
        builder.setMessage("This app requires access to your device storage and camera. " +
                "Allow access when prompted or change the permissions in app settings");
        builder.setIcon(R.drawable.plant);
        builder.setPositiveButton("OK",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    System.exit(0);
                }
            });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void transitionToNext(){
        handler.postDelayed(runnable, 2000);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        loginCheck(user);
    }

    private void loginCheck(FirebaseUser user) {

        if (user != null) {
            if (user.isEmailVerified()){
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            } else {
                tvVerifyEmail.setVisibility(View.VISIBLE);
                tvSignUp.setVisibility(View.GONE);
            }
        }
    }

    public void verifyEmailFields(){
        typedEmail = fieldEmail.getText().toString();
        typedPassword = fieldPassword.getText().toString();

        if(typedEmail.equals("") || typedPassword.equals("")){
            tvError.setText("Email and Password are required");
        } else {
            tvError.setText("");

            firebaseAuth.signInWithEmailAndPassword(typedEmail, typedPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                loginCheck(user);
                            } else {
                                // Sign in failed
                                Snackbar.make(findViewById(R.id.start_main_layout),
                                        "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                                loginCheck(null);
                            }

                        }
                    });
        }
    }

    // verify email
    private void verifyEmail() {
        // Disable button
        // mBinding.verifyEmailButton.setEnabled(false);
        tvVerifyEmail.setVisibility(View.GONE);

        final FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Snackbar.make(findViewById(R.id.start_main_layout),
                                        "Verification email sent to " + user.getEmail(), Snackbar.LENGTH_SHORT).show();
                            } else {

                                Snackbar.make(findViewById(R.id.start_main_layout),
                                        "Failed to send verification email", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    // Email account creation
    private void emailSignUp() {
        if (etNewEmail.length() < 6){
            tvNewEmailError.setText("Invalid email entered");
        } else if (etNewPassword.length() < 6){
            tvNewEmailError.setText("Password must be at least six(6) characters long");
        } else if (!etNewPassword.equals(etNewConfirmPassword)){
            tvNewEmailError.setText("Passwords do not match");
        } else {
            String email = etNewEmail.getText().toString();
            String password = etNewPassword.getText().toString();

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // sign in success
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                loginCheck(user);
                            } else {
                                // sign in fails
                                Snackbar.make(findViewById(R.id.start_main_layout),
                                        "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                                loginCheck(null);
                            }

                        }
                    });
        }
    }

    // Google sign in
    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_REQUEST_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed
                loginCheck(null);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // sign in success
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            loginCheck(user);
                        } else {
                            // sign in fails
                            Snackbar.make(findViewById(R.id.start_main_layout),
                                    "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            loginCheck(null);
                        }
                    }
                });
    }

    private void signOut() {
        // Firebase sign out
        firebaseAuth.signOut();

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loginCheck(null);
                    }
                });
    }

    /*private void reload() {
        firebaseAuth.getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    loginCheck();
                    Toast.makeText(getApplicationContext(), "Reload successful!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),"Failed to reload user.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }*/

    /*private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            user = firebaseAuth.getCurrentUser();
                            loginCheck();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }*/

}
