package com.mobilelab.artyomska.bookdeposit.model;

import java.time.LocalDate;

public class Review
{
    private int ID;
    private String reviewText;
    private int bookID;
    private int userID;
    private UserData user;
    private Rating rating;
    private double ratingScore;
    private LocalDate addedTime;
    private Book book;

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public LocalDate getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(LocalDate addedTime) {
        this.addedTime = addedTime;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public double getRatingScore() {
        return ratingScore;
    }

    public void setRatingScore(double ratingScore) {
        this.ratingScore = ratingScore;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }

    public Review(int ID, String reviewText, int bookID, int userID) {
        this.ID = ID;
        this.reviewText = reviewText;
        this.bookID = bookID;
        this.userID = userID;
    }

    public Review() {

    }
}
