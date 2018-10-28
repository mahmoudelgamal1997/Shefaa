package com.example2017.android.shefaa;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
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

import java.util.Calendar;

public class Comments extends AppCompatActivity {

    private String postId;
    private DatabaseReference comments,temp,users,doctors;
    private ListView listView;
    private EditText editText_comment;
    private Button button;
    private SharedPreferences sh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        Intent i=getIntent();
        postId = i.getStringExtra("PostId");
        comments = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        final FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();

        temp=comments.push();


        sh=getSharedPreferences("Type",MODE_PRIVATE);
        String type=sh.getString("UserType","Users");

        users=FirebaseDatabase.getInstance().getReference().child(type);
        doctors=FirebaseDatabase.getInstance().getReference().child("Doctors");
        listView=(ListView)findViewById(R.id.listView_comment);
        editText_comment=(EditText)findViewById(R.id.writeComment);
        button=(Button)findViewById(R.id.sendComment);





        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                users.child(currentUser.getUid().toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        String image = dataSnapshot.child("ProfileImage").getValue(String.class);
                        String name = dataSnapshot.child("name").getValue(String.class);

                        Toast.makeText(Comments.this, name, Toast.LENGTH_SHORT).show();
                        temp.child("CommentProfileImage").setValue(image);
                        temp.child("UserName").setValue(name);
                        temp.child("UserId").setValue(currentUser.getUid().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

              String text=editText_comment.getText().toString();
              temp.child("CommentText").setValue(text);
              temp.child("Time").setValue(OrganizeTime());

            }
        });




        FirebaseListAdapter<CommentItem> firebaseListAdapter= new FirebaseListAdapter<CommentItem>(
                this,
                CommentItem.class,
                R.layout.comment_shape,
                comments
        ) {
            @Override
            protected void populateView(View v, final CommentItem model, final int position) {

                TextView name=(TextView)v.findViewById(R.id.comment_userName);
                TextView text=(TextView)v.findViewById(R.id.commentText);
                TextView time=(TextView)v.findViewById(R.id.comment_Time);
                final ImageView img=(ImageView)v.findViewById(R.id.commentProfileImage);

                name.setText(model.getUserName());
                text.setText(model.getCommentText());
                time.setText(model.getTime());
                Picasso.with(getApplicationContext()).load(model.getCommentProfileImage()).placeholder(R.drawable.default_image).networkPolicy(NetworkPolicy.OFFLINE).into(img, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        Picasso.with(getApplicationContext()).load(model.getCommentProfileImage()).placeholder(R.drawable.default_image).into(img);
                    }
                });



                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        comments.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                          String id= String.valueOf(dataSnapshot.child(getRef(position).getKey()).child("UserId").getValue(String.class));


                                Toast.makeText(Comments.this, id, Toast.LENGTH_SHORT).show();
                                /*
                                doctors.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });





                                */




















                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });

            }





        };


        listView.setAdapter(firebaseListAdapter);
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



}
