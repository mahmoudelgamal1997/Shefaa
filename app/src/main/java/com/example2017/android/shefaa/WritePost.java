package com.example2017.android.shefaa;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class WritePost extends AppCompatActivity {

    EditText edittext;
    private DatabaseReference posts,temp,users;
    private Button upload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);


        Intent intent=getIntent();
        final String Catorgy=intent.getStringExtra("Catorgy");

        edittext=(EditText)findViewById(R.id.editText_Post);
        upload=(Button)findViewById(R.id.upload);
        users=FirebaseDatabase.getInstance().getReference().child("Users");
        posts= FirebaseDatabase.getInstance().getReference().child("Posts").child(Catorgy);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)

            {
                if (checkInternet()) {

                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    String id = firebaseUser.getUid();
                    String text = edittext.getText().toString().trim();
                    temp = posts.push();
                    temp.child("PostText").setValue(text);
                    temp.child("PostTime").setValue(OrganizeTime());
                    temp.child("UserId").setValue(id);
                    users.child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            String image = dataSnapshot.child("ProfileImage").getValue(String.class);
                            String name  = dataSnapshot.child("name").getValue(String.class);
                            temp.child("UserImage").setValue(image);
                            temp.child("PostUserName").setValue(name);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    Toast.makeText(WritePost.this, "تم اضافه المنشور بنجاح", Toast.LENGTH_SHORT).show();
                    finish();

                }else {
                    Toast.makeText(WritePost.this, "لا يوجد اتصال بالانترنت", Toast.LENGTH_SHORT).show();
                }


            }
        });



    }

    String OrganizeTime(){
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day= calendar.get(Calendar.DAY_OF_MONTH);
        int hour =calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);

        String time=""+year+"-"+month+"-"+day+" "+hour+":"+minute;
    return time;
    }


    boolean checkInternet(){
        ConnectivityManager cm=(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo()!= null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected() ){
            return true;
        }else{return false;}

    }
}
