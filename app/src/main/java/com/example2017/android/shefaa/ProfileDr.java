package com.example2017.android.shefaa;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class ProfileDr extends AppCompatActivity {

    TextView name,number,specialist;
    ImageView locationImage,profileImage;
    DatabaseReference doctors;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_dr);

        name=(TextView)findViewById(R.id.name_text);
        number=(TextView)findViewById(R.id.number_text);
        specialist=(TextView)findViewById(R.id.specialist_text);

        locationImage=(ImageView)findViewById(R.id.location);
        profileImage=(ImageView)findViewById(R.id.profile_image);

        doctors= FirebaseDatabase.getInstance().getReference().child("Doctors");
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        String id =currentUser.getUid();
        doctors.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            String n =dataSnapshot.child("name").getValue(String.class);
            final String img=dataSnapshot.child("ProfileImage").getValue(String.class);
            String specialisty =dataSnapshot.child("Specialist").getValue(String.class);

            name.setText(n);
            specialist.setText(specialisty);
                Picasso.with(getApplicationContext()).load(img).placeholder(R.drawable.default_image).networkPolicy(NetworkPolicy.OFFLINE).into(profileImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        Picasso.with(getApplicationContext()).load(img).placeholder(R.drawable.default_image).into(profileImage);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
