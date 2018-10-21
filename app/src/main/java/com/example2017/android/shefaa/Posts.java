package com.example2017.android.shefaa;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

public class Posts extends AppCompatActivity {


    private DatabaseReference posts;
    private RecyclerView mre;
    private FloatingActionButton fbtn;
    String id;
    boolean isLike;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        Intent intent=getIntent();
        final String Catorgy=intent.getStringExtra("Catorgy");

        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
         id=firebaseUser.getUid();

        posts= FirebaseDatabase.getInstance().getReference().child("Posts").child(Catorgy);
        posts.keepSynced(true);

        fbtn=(FloatingActionButton)findViewById(R.id.fab);

        mre = (RecyclerView) findViewById(R.id.view);
        mre.setHasFixedSize(true);
        mre.setLayoutManager(new LinearLayoutManager(this));



        fbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(Posts.this,WritePost.class);
                intent1.putExtra("Catorgy",Catorgy);
                startActivity(intent1);
            }
        });
            Retrive();

    }

    public void Retrive(){


        FirebaseRecyclerAdapter<PostItem,Post_viewholder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<PostItem, Post_viewholder>(
                PostItem.class,
                R.layout.post_shape,
                Post_viewholder.class,
                posts
        )

        {
            @Override
            protected void populateViewHolder(Post_viewholder viewHolder, final PostItem model, final int position)
            {

                viewHolder.SetTitle(model.getPostText(),model.PostTime,model.getPostUserName());
                viewHolder.SetImage(getApplicationContext(),model.getUserImage());
                final ImageButton like = (ImageButton) viewHolder.view.findViewById(R.id.like);

/*
                posts.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child("like").hasChild(id)){
                            like.setImageResource(R.drawable.likeafter);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

*/
               like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String postId = String.valueOf(getRef(position).getKey());



                        posts.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.child("like").hasChild(id)) {
                                    like.setImageResource(R.drawable.likeafter);
                                    posts.child(postId).child("like").setValue(true);

                                } else {
                                    posts.child(postId).child("like").setValue(false);

                                }

                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });






                    }
                });
            }
        };

        mre.setAdapter(firebaseRecyclerAdapter);

    }


    public  static  class Post_viewholder extends RecyclerView.ViewHolder {

        View view;

        public Post_viewholder(View itemView) {
            super(itemView);

            view = itemView;


        }

        public void SetTitle(String Text,String Time,String UserName) {

            TextView describtion = (TextView) view.findViewById(R.id.textView_desc);
            TextView PostTime = (TextView) view.findViewById(R.id.TextTime);
            TextView Name = (TextView)view.findViewById(R.id.Text_username);

            Name.setText(UserName);
            describtion.setText(Text);
            PostTime.setText(Time);

        }


        public void SetImage(final Context cnt, final String img) {

            final ImageView imgview = (ImageView) view.findViewById(R.id.profile_image);

            // .networkPolicy(NetworkPolicy.OFFLINE)
            //to cash data
            Picasso.with(cnt).load(img).networkPolicy(NetworkPolicy.OFFLINE).into(imgview, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                    Picasso.with(cnt).load(img).placeholder(R.drawable.default_image).into(imgview);
                }
            });
        }

    }




}
