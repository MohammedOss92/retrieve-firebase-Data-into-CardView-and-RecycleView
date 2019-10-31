package com.f.s;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    LinearLayoutManager mLayoutManager;
    SharedPreferences mSharedPreferences;
FirebaseDatabase ds;
RecyclerView mBlogList;
DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("global");
        mDatabase.keepSynced(true);

        mSharedPreferences=getSharedPreferences("SortSettings",MODE_PRIVATE);
        String mSorting = mSharedPreferences.getString("Sort","newest");
        if(mSorting.equals("newest")){
            mLayoutManager = new LinearLayoutManager(this);
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);
        }

        else if(mSorting.equals("oldest")){
            mLayoutManager = new LinearLayoutManager(this);
            mLayoutManager.setReverseLayout(false);
            mLayoutManager.setStackFromEnd(false);
        }


        mBlogList=(RecyclerView)findViewById(R.id.myrecyclerview);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(mLayoutManager);



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

            @Override
            public BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                BlogViewHolder blogViewHolder=super.onCreateViewHolder(parent,viewType);
                blogViewHolder.SetOnClickListener(new BlogViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        TextView mTitleTv=view.findViewById(R.id.post_title);
                        TextView mDescTv=view.findViewById(R.id.post_desc);
                        ImageView mImageView=view.findViewById(R.id.post_image);
                        String mTitle=mTitleTv.getText().toString();
                        String mDesc = mDescTv.getText().toString();
                        Drawable mDrawable = mImageView.getDrawable();
                        Bitmap mBitmap =((BitmapDrawable)mDrawable).getBitmap();

                        Intent intent = new Intent(view.getContext(),PostDetailActivity.class);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        mBitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
                        byte[] bytes =stream.toByteArray();
                        intent.putExtra("image",bytes);
                        intent.putExtra("title",mTitle);
                        intent.putExtra("desc",mDesc);
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                });
                return blogViewHolder;
            }
        };



        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }

    private void firebaseSearch(String searchText){
        String aa=searchText.toLowerCase();
        Query firebaseSearchQuery = mDatabase.orderByChild("title").startAt(aa).endAt(aa + "\uf0ff");
        FirebaseRecyclerAdapter<Blog,BlogViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                        Blog.class,
                        R.layout.blog_row,
                        BlogViewHolder.class,
                        firebaseSearchQuery
                ) {
                    @Override
                    protected void populateViewHolder(BlogViewHolder blogViewHolder, Blog blog, int i) {
                        blogViewHolder.setTitle(blog.getTitle());
                        blogViewHolder.setDesc(blog.getDesc());
                        blogViewHolder.setImage(getApplicationContext(),blog.getImage());
                    }


                    @Override
                    public BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        BlogViewHolder blogViewHolder=super.onCreateViewHolder(parent,viewType);
                        blogViewHolder.SetOnClickListener(new BlogViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                TextView mTitleTv=view.findViewById(R.id.post_title);
                                TextView mDescTv=view.findViewById(R.id.post_desc);
                                ImageView mImageView=view.findViewById(R.id.post_image);
                                String mTitle=mTitleTv.getText().toString();
                                String mDesc = mDescTv.getText().toString();
                                Drawable mDrawable = mImageView.getDrawable();
                                Bitmap mBitmap =((BitmapDrawable)mDrawable).getBitmap();

                                Intent intent = new Intent(view.getContext(),PostDetailActivity.class);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                mBitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
                                byte[] bytes =stream.toByteArray();
                                intent.putExtra("image",bytes);
                                intent.putExtra("title",mTitle);
                                intent.putExtra("desc",mDesc);
                                startActivity(intent);
                            }

                            @Override
                            public void onItemLongClick(View view, int position) {

                            }
                        });
                        return blogViewHolder;
                    }

                };
        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onItemClick(v,getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mClickListener.onItemLongClick(v,getAdapterPosition());
                    return true;
                }
            });
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

        private BlogViewHolder.ClickListener mClickListener;

        public interface ClickListener{
            void onItemClick(View view,int position);
            void onItemLongClick(View view,int position);
        }

        public void SetOnClickListener(BlogViewHolder.ClickListener clickListener){
            mClickListener = clickListener;
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView=(SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                firebaseSearch(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_search){
            //TODO
            return true;
        }

        else if(id==R.id.action_sort){
            ShowSortDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void ShowSortDialog() {
        String [] SortOptions = {"Newest","Oldest"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sort by")
                .setIcon(R.drawable.ic_action_sort)
                .setItems(SortOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putString("Sort","newest");
                            editor.apply();
                            recreate();
                        }

                        else if(which==1){{
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putString("Sort","oldest");
                            editor.apply();
                            recreate();
                        }

                        }
                    }
                });
        builder.show();
    }
}
