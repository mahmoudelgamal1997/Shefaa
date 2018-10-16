package com.example2017.android.shefaa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatabaseReference catorgy;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView=(ListView)findViewById(R.id.listview_catorgy);
        catorgy= FirebaseDatabase.getInstance().getReference().child("Catorgy");

        FirebaseListAdapter<String> firebaseListAdapter=new FirebaseListAdapter<String>(
                this,
                String.class,
                R.layout.listview_catorgy_item,
                catorgy
        ) {
            @Override
            protected void populateView(View v, String model, int position) {

                TextView txt=(TextView)v.findViewById(R.id.txt_catorgy);
                txt.setText(model);
            }
        };


        listView.setAdapter(firebaseListAdapter);

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

        case R.id.search:

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

