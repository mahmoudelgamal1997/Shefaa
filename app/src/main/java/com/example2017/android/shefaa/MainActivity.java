package com.example2017.android.shefaa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.util.SortedList;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Callback;

import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatabaseReference catorgy;
    DatabaseReference users;
    ListView listView;
    ImageView  ProfileImage;
    SharedPreferences sh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listview_catorgy);
        ProfileImage = (ImageView) findViewById(R.id.profile_image);
        catorgy = FirebaseDatabase.getInstance().getReference().child("Catorgy");
        users = FirebaseDatabase.getInstance().getReference();
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent =new Intent(MainActivity.this,ProfileDr.class);
                startActivity(intent);
            }
        });



        if (firebaseUser != null) {

            sh=getSharedPreferences("Type",MODE_PRIVATE);
            String type =sh.getString("UserType","");


            users.child(type).child(firebaseUser.getUid().toString()).child("ProfileImage").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    final String imageuri = dataSnapshot.getValue(String.class);

                    Picasso.with(getApplicationContext()).load(imageuri).placeholder(R.drawable.default_image).networkPolicy(NetworkPolicy.OFFLINE).into(ProfileImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                            Picasso.with(getApplicationContext()).load(imageuri).placeholder(R.drawable.default_image).into(ProfileImage);
                        }
                    });


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            FirebaseListAdapter<String> firebaseListAdapter = new FirebaseListAdapter<String>(
                    this,
                    String.class,
                    R.layout.listview_catorgy_item,
                    catorgy
            ) {
                @Override
                protected void populateView(View v, final String model, int position) {

                    TextView txt = (TextView) v.findViewById(R.id.txt_catorgy);
                    txt.setText(model);

                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MainActivity.this, Posts.class);
                            intent.putExtra("Catorgy", model);
                            startActivity(intent);

                        }
                    });
                }
            };


            listView.setAdapter(firebaseListAdapter);

        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_item, menu);


        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
        case R.id.id_logout:
           logout();
        //we call login to go login activity because we will still in main activity because onstart was called once
           login();
           break;

        case R.id.id_setting:

            break;


            }

            return super.onOptionsItemSelected(item);

    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser =FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser==null){

          login();

        }
    }

    private void login() {

            Intent intent=new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }

    private void logout() {

        FirebaseAuth.getInstance().signOut();

    }



    }

