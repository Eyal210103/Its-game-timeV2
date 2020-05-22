package com.example.whosin.ui.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.whosin.R;
import com.example.whosin.model.Objects.User;
import com.example.whosin.model.Singleton.CurrentUser;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends AppCompatActivity implements ImageUploadListener{

    Dialog signUpDialog, loginDialog;
    ProgressDialog progressDialog;


    private static final int PICK_IMAGE = 100;
    private static final int GOOGLE = 101;
    private static final String EMAIL = "email";

    private User thisUser;
    private Uri imageUri = Uri.parse("data:image/jpeg;base64,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");
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private StorageReference mStorageRef;
    private Intent mainActivity;
    CallbackManager callbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            setContentView(R.layout.no_internet_layout);
        } else {
            startUp();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            CircleImageView profile = (CircleImageView)signUpDialog.findViewById(R.id.civ_profile_sign_up);
            Glide.with(this).load(imageUri).into(profile);
        } else if (requestCode == GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Authentication failed, Try Again", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE);
    }

    private void firebaseAuthWithGoogle(@NotNull GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = mAuth.getCurrentUser();
                            thisUser.setImageUri(currentUser.getPhotoUrl().toString());
                            thisUser.setEmail(currentUser.getEmail());
                            thisUser.setFullName(currentUser.getDisplayName());
                            thisUser.setPhone(currentUser.getPhoneNumber());
                            thisUser.setId(currentUser.getUid());
                            myRef.child(currentUser.getUid()).child("details").setValue(thisUser);
                            Toast.makeText(getApplicationContext(), "Authentication succeed.", Toast.LENGTH_SHORT).show();
                            openAppMain();
                        } else {
                            Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }

    private void handleFacebookToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                try {
                    if (task.isSuccessful()) {
                        currentUser = mAuth.getCurrentUser();
                        thisUser.setImageUri(currentUser.getPhotoUrl().toString());
                        thisUser.setEmail(currentUser.getEmail());
                        thisUser.setFullName(currentUser.getDisplayName());
                        thisUser.setPhone(currentUser.getPhoneNumber());
                        thisUser.setId(currentUser.getUid());
                        myRef.child(currentUser.getUid()).child("details").setValue(thisUser);
                        Toast.makeText(getApplicationContext(), "Authentication succeed.", Toast.LENGTH_SHORT).show();
                        openAppMain();
                    }else {
                        Log.d("login", "onComplete: --------------------------------------------------------");
                        task.getException().printStackTrace();
                    }
                } catch (Exception e) {
                    Log.d("login","-------------------------------------------------------"+e.getMessage());
                }
            }
        });
    }

    //___________________________________________________________ Login & SignUp With Email

    private void openSignUpDialog(){
        signUpDialog = new Dialog(this);
        signUpDialog.setContentView(R.layout.dialog_sign_up);
        signUpDialog.setCancelable(false);
        signUpDialog.show();

        signUpDialog.findViewById(R.id.button_choose_image_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        signUpDialog.findViewById(R.id.button_done_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSignUp();
            }
        });
        signUpDialog.findViewById(R.id.button_cancel_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpDialog.dismiss();
            }
        });

    }
    public void onClickSignUp(){
        EditText firstNameET = signUpDialog.findViewById(R.id.editText_first_name_sign_up);
        EditText lastNameET = signUpDialog.findViewById(R.id.editText_last_name_sign_up);
        EditText emailET = signUpDialog.findViewById(R.id.editText_email_sign_up);
        EditText passwordET = signUpDialog.findViewById(R.id.editText_password_sign_up);
        EditText passwordConfirmET = signUpDialog.findViewById(R.id.editText_confirm_password_sign_up);

        if (!emailET.getText().toString().matches("") && !passwordET.getText().toString().matches("") && !firstNameET.getText().toString().matches("") &&!emailET.getText().toString().matches("") && !lastNameET.getText().toString().matches("") &&!emailET.getText().toString().matches("") && !passwordConfirmET.getText().toString().matches("")){
            if (passwordET.getText().toString().equals(passwordConfirmET.getText().toString())) {
                signUpWithEmail(firstNameET.getText().toString(),lastNameET.getText().toString(),emailET.getText().toString(), passwordET.getText().toString() );
            }else {
                Toast.makeText(LoginActivity.this, "Passwords Are Not Identical", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(LoginActivity.this, "Please Type", Toast.LENGTH_SHORT).show();
        }
    }

    private void signUpWithEmail(final String lFirstName, final String lLastName , String email , String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = mAuth.getCurrentUser();
                            firstName = lFirstName;
                            lastName = lLastName;
                            uploadProfilePhoto(imageUri , currentUser.getUid());
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }
    private void uploadProfilePhoto(Uri imageUri, final String userId){
        final ProgressDialog progressDialogUpload = new ProgressDialog(this, android.R.style.Theme_DeviceDefault_Dialog);
        progressDialogUpload.setTitle("Uploading Please Wait....");
        progressDialogUpload.show();
        mStorageRef.child(userId).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    mStorageRef.child(userId).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("details").child("image").setValue(task.getResult().toString());

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(Uri.parse(task.getResult().toString()))
                                        .build();

                                currentUser.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(LoginActivity.this,"Photo Upload Succeed" , Toast.LENGTH_SHORT).show();
                                                    progressDialogUpload.dismiss();
                                                    ImageUploadListener imageUploadListener = (ImageUploadListener)LoginActivity.this;
                                                    imageUploadListener.onImageUploaded();
                                                }
                                            }
                                        });
                            }else {
                                Toast.makeText(LoginActivity.this,"Photo Wont Upload" , Toast.LENGTH_SHORT).show();
                                progressDialogUpload.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }

    private void openLoginDialog(){
        loginDialog = new Dialog(this);
        loginDialog.setContentView(R.layout.dialog_login);
        loginDialog.setCancelable(false);
        loginDialog.show();
        loginDialog.findViewById(R.id.button_submit_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLogin();
            }
        });

        loginDialog.findViewById(R.id.button_cancel_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginDialog.dismiss();
            }
        });
    }

    public void onClickLogin(){
        EditText emailET = loginDialog.findViewById(R.id.editText_email_login);
        EditText passwordET = loginDialog.findViewById(R.id.editText_password_login);
        if (!emailET.getText().toString().matches("") && !passwordET.getText().toString().matches("")){
            loginWithEmail(emailET.getText().toString(),passwordET.getText().toString());
        }else {
            Toast.makeText(LoginActivity.this, "Please Type", Toast.LENGTH_SHORT).show();
        }
    }

    private void loginWithEmail(String email,String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Authentication succeed", Toast.LENGTH_SHORT).show();
                            openAppMain();
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

    //___________________________________________________________ Update Sh!t
    public void openAppMain() {
        this.mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(this.mainActivity);
        this.finish();
    }

    public void startUp() {
        setContentView(R.layout.activity_login);

        this.mAuth = FirebaseAuth.getInstance();
        this.currentUser = mAuth.getCurrentUser();
        this.mainActivity = new Intent(this, MainActivity.class).setClass(this, MainActivity.class);
        if (currentUser != null) {
            openAppMain();
        } else {
            this.thisUser = new User();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            this.myRef = database.getReference().child("Users");
            this.mStorageRef = FirebaseStorage.getInstance().getReference("Profile Images");

            //Google
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
            this.mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            //facebook
            FacebookSdk.sdkInitialize(getApplicationContext());
            AppEventsLogger.activateApp(getApplication());
            callbackManager = CallbackManager.Factory.create();
            LoginButton loginButton = (LoginButton) findViewById(R.id.loginButton);
            loginButton.setReadPermissions("email", "public_profile");
            loginButton.setReadPermissions(Arrays.asList(EMAIL));

            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    handleFacebookToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                }
            });

            this.progressDialog = new ProgressDialog(this, android.R.style.Theme_DeviceDefault_Dialog);
            this.progressDialog.setTitle("Please Wait....");
            findViewById(R.id.google).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signInWithGoogle();
                }
            });

            findViewById(R.id.button_login).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openLoginDialog();
                }
            });
            findViewById(R.id.textView_sign_up).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openSignUpDialog();
                }
            });
        }
    }

    @Override
    public void onImageUploaded() {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(firstName + " " + lastName)
                .build();

        currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication succeed", Toast.LENGTH_SHORT).show();
                            FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("details").setValue(CurrentUser.getInstance());
                            openAppMain();
                        }
                    }
                });
    }
}
 interface ImageUploadListener{
    void onImageUploaded();
}
