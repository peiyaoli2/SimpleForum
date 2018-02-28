package com.example.amelia.uiucforum;

import java.util.List;

/**
 * Created by peiyaol2 on 4/18/2017.
 * A class for parsing Post objects
 */

public class Post {
    private String title;
    private PostFragment op;
    private List<PostFragment> replies;

    public Post() {

    }

    public Post(String title, PostFragment op) {
        this.title = title;
        this.op = op;
    }

    public Post(String title, PostFragment op, List<PostFragment> replies) {
        this.title = title;
        this.op = op;
        this.replies = replies;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PostFragment getOp() {
        return op;
    }

    public void setOp(PostFragment op) {
        this.op = op;
    }

    public List<PostFragment> getReplies() {
        return replies;
    }

    public void setReplies(List<PostFragment> replies) {
        this.replies = replies;
    }
}
