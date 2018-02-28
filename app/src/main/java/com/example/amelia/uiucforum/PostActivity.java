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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.CountDownLatch;

/**
 * Created by peiyaol2 on 4/25/2017.
 * Citation: https://www.youtube.com/watch?v=0QnvFYsKl4U&t=357s
 */

public class PostActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton addReplyBtn;
    private RecyclerView repliesRecyclerView;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private TextView titleTextView, authorTextView, bodyTextView;

    private String title;
    private String author;
    private String body;

    private String email;

    private int childCount = 0;      //For pushing replies to firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //Get extra messages from DashboardActivity
        Intent intent = getIntent();
        title = intent.getStringExtra("Title");
        author = intent.getStringExtra("Author");
        body = intent.getStringExtra("Body");
        String path = "post/" + title;

        addReplyBtn = (ImageButton) findViewById(R.id.addReplyBtn);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        authorTextView = (TextView) findViewById(R.id.authorTextView);
        bodyTextView = (TextView) findViewById(R.id.bodyTextView);

        addReplyBtn.setOnClickListener(this);

        //Get recyclerView
        repliesRecyclerView = (RecyclerView) findViewById(R.id.repliesRecyclerView);
        repliesRecyclerView.setHasFixedSize(true);
        repliesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Init firebase
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        email = auth.getCurrentUser().getEmail();

        titleTextView.setText(title);
        authorTextView.setText(author);
        bodyTextView.setText(body);

        reference = database.getReference(path + "/replies");

        //Count number of children
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                childCount++;
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                childCount--;
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.addReplyBtn) {
            //Create a dialog for reply submission
            final Dialog dialog = new Dialog(view.getContext());
            dialog.setContentView(R.layout.activity_add_reply);
            dialog.setTitle("Reply Submission");

            final EditText replyEditText = (EditText) dialog.findViewById(R.id.replyEditText);
            final Button submitReplyButton = (Button) dialog.findViewById(R.id.submitReplyButton);
            submitReplyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String replyBody = replyEditText.getText().toString();
                    int indexOfAt = email.indexOf('@');
                    final String replyAuthor = email.substring(0, indexOfAt);
                    PostFragment reply = new PostFragment(replyAuthor, replyBody);

                    //Use childCount as key for the next reply
                    reference.child(Integer.toString(childCount)).setValue(reply);
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    //Handle the recycler view using FirebaseRecyclerAdapter
    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<PostFragment, ReplyViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<PostFragment, ReplyViewHolder>(
                        PostFragment.class,
                        R.layout.reply_list_item,
                        ReplyViewHolder.class,
                        reference
                ) {
                    @Override
                    protected void populateViewHolder
                            (ReplyViewHolder viewHolder, PostFragment model, int position) {
                        viewHolder.setAuthor(model.getAuthor());
                        viewHolder.setBody(model.getBody());
                    }
                };
        repliesRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    //ViewHolder for replyRecyclerView
    public static class ReplyViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public ReplyViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setAuthor(String author) {
            TextView replyAuthorTextView = (TextView) mView.findViewById(R.id.replyAuthorTextView);
            replyAuthorTextView.setText(author);
        }

        public void setBody(String body) {
            TextView replyBodyTextView = (TextView) mView.findViewById(R.id.replyBodyTextView);
            replyBodyTextView.setText(body);
        }
    }
}