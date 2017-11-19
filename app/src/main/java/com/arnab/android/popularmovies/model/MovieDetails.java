package com.arnab.android.popularmovies.model;

import java.io.Serializable;

/**
 * Created by arnab on 11/19/17.
 */

public class MovieDetails implements Serializable {
    private int vote_count;
    private int id;
    private double vote_average;
    private String title;
    private String poster_path;
    private int runtime;
    private String tagline;
    private String [] genre;
    private String overview;
    private String release_date;
    private String backdrop_path;


    public int getVote_count() {
        return vote_count;
    }

    public void setVote_count(int vote_count) {
        this.vote_count = vote_count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
        if(this.tagline.equals("")){
            this.tagline = "Exciting movie";
        }
    }

    public String[] getGenre() {
        return genre;
    }

    public void setGenre(String[] genre) {
        this.genre = genre;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

}
