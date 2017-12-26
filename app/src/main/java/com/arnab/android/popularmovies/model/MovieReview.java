package com.arnab.android.popularmovies.model;

import java.io.Serializable;

/**
 * Created by arnab on 11/25/17.
 */

public class MovieReview implements Serializable {

    String author;
    String content;
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
