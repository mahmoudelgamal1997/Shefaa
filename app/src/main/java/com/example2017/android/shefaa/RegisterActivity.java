package com.example2017.android.shefaa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    private  EditText regEmailText,regPassText,regConfirmPassText,Username;
    private Button register_Button,login_reg;
    private ImageView imageView;
    private DatabaseReference users;
    private final static int gallery_intent=0;
    private Uri uri;
    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        users= FirebaseDatabase.getInstance().getReference().child("Users");
        storageReference= FirebaseStorage.getInstance().getReference();

        imageView=(ImageView)findViewById(R.id.register_profile_image);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                Intent intent =new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,gallery_intent);
            }
        });


        regEmailText=(EditText)findViewById(R.id.editText_reg_email);
        regPassText=(EditText)findViewById(R.id.editText_reg_pass);
        regConfirmPassText=(EditText)findViewById(R.id.editText_reg_pass_confirm);
        Username=(EditText)findViewById(R.id.editText_username);
        register_Button=(Button)findViewById(R.id.but_register);
        login_reg=(Button)findViewById(R.id.but_login_reg);

        register_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name=Username.getText().toString().trim();
                String RegisterEmail=regEmailText.getText().toString().trim();
                String RegisterPass=regPassText.getText().toString().trim();
                String RegisterConfirmPass=regConfirmPassText.getText().toString().trim();

                if ( ! TextUtils.isEmpty(RegisterEmail) && ! TextUtils.isEmpty(RegisterPass) && ! TextUtils.isEmpty(RegisterConfirmPass))
                {
                    if (RegisterPass.equals(RegisterConfirmPass))
                    {

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(RegisterEmail,RegisterPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                                users.child(firebaseUser.getUid().toString()).child("name").setValue(name);
                                if (uri != null)
                                {
                                    uploadProfileImage(storageReference, uri, users);
                                    Toast.makeText(getApplicationContext(),"Image upload",Toast.LENGTH_LONG).show();

                                }
                                Toast.makeText(getApplicationContext(),"register sucsess",Toast.LENGTH_LONG).show();
                                login();

                            }else {
                                Toast.makeText(getApplicationContext(),task.getException().getMessage().toString(),Toast.LENGTH_LONG).show();

                            }
                        }
                    });

                }else {
                        Toast.makeText(getApplicationContext(), "confirm password and password doesn't match ", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


        login_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



    }
    void login(){
        Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap resized=null;
    if (requestCode==gallery_intent  &&  resultCode==RESULT_OK){
       uri=data.getData();
    }

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            resized = Bitmap.createScaledBitmap(bitmap, 100, 120, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(resized);


    }
    void uploadProfileImage(StorageReference s, Uri imageUri, final DatabaseReference databaseReference){
        StorageReference filepath=s.child("ProfileImage").child(imageUri.getLastPathSegment());
        filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Uri down=taskSnapshot.getDownloadUrl();
                FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                databaseReference.child(firebaseUser.getUid().toString()).child("ProfileImage").setValue(down.toString());

            }
        });
    }
}
