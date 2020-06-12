package io.github.lhernandez1848.plantpedia;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int GOOGLE_REQUEST_CODE = 9001;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 201;

    private GoogleSignInClient googleSignInClient;

    private FirebaseAuth firebaseAuth;

    private GlobalMethods globalMethods;

    Handler handler;
    Runnable runnable;

    // declare widgets
    LinearLayout splash_first_layout, splash_second_layout, email_text_fields, sign_up_text_fields;
    TextView start_welcome_message, start_app_name, tvError, tvVerifyEmail, tvSignUp, tvNewEmailError;
    EditText fieldEmail, fieldPassword, etNewEmail, etNewPassword, etNewConfirmPassword;
    ImageView logo_image_view;
    Button emailSignIn, emailSignInVerify, btnEmailSignUp;
    SignInButton googleSignIn;

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

        // initialize variables
        typedEmail = "";
        typedPassword = "";

        // set listeners
        emailSignIn.setOnClickListener(this);
        emailSignInVerify.setOnClickListener(this);
        googleSignIn.setOnClickListener(this);
        tvVerifyEmail.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);
        btnEmailSignUp.setOnClickListener(this);

        // initialize classes
        globalMethods = new GlobalMethods(this);
        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        handler = new Handler();
        runnable = () -> {
            splash_first_layout.setVisibility(View.VISIBLE);
            splash_second_layout.setVisibility(View.VISIBLE);

            start_welcome_message.setVisibility(View.GONE);
            start_app_name.setVisibility(View.GONE);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    logo_image_view.getWidth() - 150,
                    logo_image_view.getHeight() - 150);
            layoutParams.setMargins(0, 20, 0, 0);

            logo_image_view.setLayoutParams(layoutParams);
        };

        try {
            Intent intent = getIntent();
            String action = intent.getStringExtra("action");

            if (action.equals("SIGN_OUT")){
                signOut();
            }
        } catch (Exception e){

        }

        if (!globalMethods.checkStoragePermission()){
            globalMethods.requestStoragePermission();
        } else if(!globalMethods.checkCameraPermission()){
            globalMethods.requestCameraPermission();
        }

        if(globalMethods.checkCameraPermission() && globalMethods.checkStoragePermission()) {
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
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (!globalMethods.checkCameraPermission()){
                globalMethods.requestCameraPermission();
            }
        }
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE){
            transitionToNext();
        }
    }

    //
    public void transitionToNext(){
        handler.postDelayed(runnable, 2000);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        loginCheck(user);
    }

    // check if user has previously signed in
    private void loginCheck(FirebaseUser user) {

        if (user != null) {
            // check if registered and verified
            if (user.isEmailVerified()){
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            } else {
                tvVerifyEmail.setVisibility(View.VISIBLE);
                tvSignUp.setVisibility(View.GONE);
            }
        }
    }

    // verify that email and password fields are not empty
    public void verifyEmailFields(){
        typedEmail = fieldEmail.getText().toString();
        typedPassword = fieldPassword.getText().toString();

        if(typedEmail.equals("") || typedPassword.equals("")){
            tvError.setText("Email and Password are required");
        } else {
            tvError.setText("");

            // sign in with Email and Password
            firebaseAuth.signInWithEmailAndPassword(typedEmail, typedPassword)
                    .addOnCompleteListener(this, task -> {
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

                    });
        }
    }

    // verify email if not verified yet
    private void verifyEmail() {
        // Disable button
        // mBinding.verifyEmailButton.setEnabled(false);
        tvVerifyEmail.setVisibility(View.GONE);

        final FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Snackbar.make(findViewById(R.id.start_main_layout),
                                    "Verification email sent to " + user.getEmail(), Snackbar.LENGTH_SHORT).show();
                        } else {

                            Snackbar.make(findViewById(R.id.start_main_layout),
                                    "Failed to send verification email", Snackbar.LENGTH_SHORT).show();
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
                    .addOnCompleteListener(this, task -> {
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
                    });
        }
    }

    // Google sign in option was clicked
    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_REQUEST_CODE);
    }

    // on result of Google Sign in
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

    // sign in with Google account
    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, task -> {
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
            });
    }

    private void signOut() {
        // Firebase sign out
        firebaseAuth.signOut();

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this,
                task -> loginCheck(null));
    }

}
