package com.example.ubc_bookstore;

public class ReviewCardData {
    private String reviewtxt;
    private String userinfo;
    private float userrating;
    public ReviewCardData(String userinfo, String reviewtxt, float userrating) {
        this.reviewtxt = reviewtxt;
        this.userinfo = userinfo;
        this.userrating = userrating;
    }
    public String getReviewtxt() {
        return reviewtxt;
    }
    public void setReviewtxt(String description) {
        this.reviewtxt = description;
    }
    public String getUserinfo() {
        return userinfo;
    }
    public void setUserinfo(String userinfo) {
        this.userinfo = userinfo;
    }
    public float getUserrating() { return userrating;};
    public void setUserrating(float userrating) {this.userrating = userrating;};
}
