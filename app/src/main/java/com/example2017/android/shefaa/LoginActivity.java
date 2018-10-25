package com.example2017.android.shefaa;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private   EditText email,pass;
    private   Button login,register;
    private SignInButton GoogleBtn;
    public GoogleApiClient mGoogleApiClient;
    public final static int RC_SIGN_IN = 1;
    private FirebaseAuth mAuth;
    private SharedPreferences sh;
    PopupWindow mpopup;
    String type="Users";
    DatabaseReference userlogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //to avoid keyboard from automatic appear

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getSupportActionBar().hide();
        //to avoid keyboard from automatic appear
        setContentView(R.layout.activity_login);


        email=(EditText)findViewById(R.id.editText_email);
        pass=(EditText)findViewById(R.id.editText_pass);
        login=(Button)findViewById(R.id.login_but);
        register=(Button)findViewById(R.id.registerPage_but);
        GoogleBtn=(SignInButton) findViewById(R.id.GoogleBtn);

        userlogin= FirebaseDatabase.getInstance().getReference();
         // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this, "you have an error", Toast.LENGTH_SHORT).show();
                    }
                }/* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();




        GoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });



        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String loginEmail=email.getText().toString();
                String loginPass=pass.getText().toString();

                if(!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPass))
                {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(loginEmail,loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Login Succsesfull",Toast.LENGTH_LONG).show();
                                SentToMain();
                            }else {
                                Toast.makeText(getApplicationContext(),task.getException().getMessage().toString(),Toast.LENGTH_LONG).show();

                            }}});
                }else{


                }


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser !=null ){
            SentToMain();

        }


    }

    private void SentToMain() {
        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()){
                    GoogleSignInAccount account =result.getSignInAccount();
                    firebaseAuthWithGoogle(account);
                }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null)
                            {




                                GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                                if (acct != null) {

                                    sh=getSharedPreferences("Type",MODE_PRIVATE);
                                    SharedPreferences.Editor editor=sh.edit();
                                    editor.putString("UserType","Users");
                                    editor.commit();
                                    FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                                     String id=firebaseUser.getUid();
                                    String personName = acct.getDisplayName();
                                    String personGivenName = acct.getGivenName();
                                    String personFamilyName = acct.getFamilyName();
                                    String personEmail = acct.getEmail();
                                    String personId = acct.getId();
                                    Uri personPhoto = acct.getPhotoUrl();
                                    if (personPhoto ==null){

                                         Uri uri= Uri.parse("https://firebasestorage.googleapis.com/v0/b/shefaa-a4632.appspot.com/o/ProfileImage%2Fdefault_image.png?alt=media&token=f533822e-58d7-4c5b-bf4d-500b0b7bd0f6");
                                        userlogin.child(type).child(id).child("name").setValue(personName);
                                        userlogin.child(type).child(id).child("ProfileImage").setValue(uri);
                                    }else {

                                        userlogin.child(type).child(id).child("name").setValue(personName);
                                        userlogin.child(type).child(id).child("ProfileImage").setValue(personPhoto.toString());
                                    }

                                }
                                SentToMain();

                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "failed", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

    }}
