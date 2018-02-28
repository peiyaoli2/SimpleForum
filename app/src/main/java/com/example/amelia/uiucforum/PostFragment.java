package com.example.amelia.uiucforum;

/**
 * Created by peiyaol2 on 4/18/2017.
 * A class for parsing PostFragments objects
 */

public class PostFragment {
    private String author;
    private String body;

    public PostFragment() {

    }

    public PostFragment(String author, String body) {
        this.author = author;
        this.body = body;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
