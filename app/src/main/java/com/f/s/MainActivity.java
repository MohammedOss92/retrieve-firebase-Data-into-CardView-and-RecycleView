package com.f.s;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
FirebaseDatabase ds;
RecyclerView mBlogList;
DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("global");
        mDatabase.keepSynced(true);

        mBlogList=(RecyclerView)findViewById(R.id.myrecyclerview);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Blog,BlogViewHolder>firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(Blog.class,R.layout.blog_row,BlogViewHolder.class,mDatabase) {
            @Override
            protected void populateViewHolder(BlogViewHolder ViewHolder, Blog blog, int i) {
                ViewHolder.setTitle(blog.getTitle());
                ViewHolder.setDesc(blog.getDesc());
                ViewHolder.setImage(getApplicationContext(),blog.getImage());
            }
        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setTitle(String title){
            TextView post_title=(TextView)mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }

        public void setDesc(String desc){
            TextView post_desc=(TextView)mView.findViewById(R.id.post_desc);
            post_desc.setText(desc);
        }

        public void setImage(Context ctx,String image){
            ImageView post_image=(ImageView)mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(post_image);
        }


    }
}
