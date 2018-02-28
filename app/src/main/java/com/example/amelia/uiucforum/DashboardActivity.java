package com.example.amelia.uiucforum;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by peiyaol2 on 4/25/2017.
 * Citation: https://www.youtube.com/watch?v=0QnvFYsKl4U&t=357s
 */

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton settingBtn;
    private ImageButton addBtn;
    private RecyclerView postRecyclerView;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Get buttons
        settingBtn = (ImageButton) findViewById(R.id.settingBtn);
        addBtn = (ImageButton) findViewById(R.id.addBtn);

        settingBtn.setOnClickListener(this);
        addBtn.setOnClickListener(this);

        //Get recyclerView
        postRecyclerView = (RecyclerView) findViewById(R.id.postRecyclerView);
        postRecyclerView.setHasFixedSize(true);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Init firebase
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("post");

        //Get user email
        auth = FirebaseAuth.getInstance();
        email = auth.getCurrentUser().getEmail();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settingBtn: {
                startActivity(new Intent(this,SettingActivity.class));
                finish();
                break;
            }
            case R.id.addBtn: {
                //Create a dialog for post submission
                final Dialog dialog = new Dialog(view.getContext());
                dialog.setContentView(R.layout.activity_add_post);
                dialog.setTitle("Post submission");

                final EditText titleEditText = (EditText) dialog.findViewById(R.id.titleEditText);
                final EditText bodyEditText = (EditText) dialog.findViewById(R.id.bodyEditText);
                final Button submitButton = (Button) dialog.findViewById(R.id.submitButton);

                submitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Get title, body and author
                        final String title = titleEditText.getText().toString();
                        final String body = bodyEditText.getText().toString();
                        int indexOfAt = email.indexOf('@');
                        final String author = email.substring(0,indexOfAt);

                        //Add to firebase
                        PostFragment op = new PostFragment(author, body);
                        Post post = new Post(title, op);
                        reference.child(title).setValue(post);
                        dialog.dismiss();
                    }
                });

                dialog.show();
                break;
            }
        }
    }

    //Handle the recycler view using FirebaseRecyclerAdapter
    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Post, PostViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Post, PostViewHolder>(
                        Post.class,
                        R.layout.post_list_item,
                        PostViewHolder.class,
                        reference
                ) {
                    @Override
                    protected void populateViewHolder
                            (PostViewHolder viewHolder, Post post, int position) {
                        viewHolder.setTitle(post.getTitle());
                        viewHolder.setAuthor(post.getOp().getAuthor());
                        viewHolder.setBody(post.getOp().getBody());
                    }
                };
        postRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    //ViewHolder for postRecyclerView
    public static class PostViewHolder extends RecyclerView.ViewHolder {
        View mView;
        String title;
        String author;
        String body;
        public PostViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), PostActivity.class);
                    intent.putExtra("Title", title);        //Pass in extra messages
                    intent.putExtra("Author", author);
                    intent.putExtra("Body", body);
                    v.getContext().startActivity(intent);
                }
            });
        }

        public void setTitle(String title) {
            TextView mainTitleTextView = (TextView) mView.findViewById(R.id.mainTitleTextView);
            mainTitleTextView.setText(title);
            this.title = title;
        }

        public void setAuthor(String author) {
            TextView mainAuthorTextView = (TextView) mView.findViewById(R.id.mainAuthorTextView);
            mainAuthorTextView.setText(author);
            this.author = author;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }
}