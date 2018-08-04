package com.mobilelab.artyomska.bookdeposit.model;

public class Rating {

    private int ID;
    private double ratingScore;
    private int userID;
    private int bookID;
    private UserData user;
    private Book book;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public double getRatingScore() {
        return ratingScore;
    }

    public void setRatingScore(double ratingScore) {
        this.ratingScore = ratingScore;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }


    public Rating(int ID, int ratingScore, int userID, int bookID) {
        this.ID = ID;
        this.ratingScore = ratingScore;
        this.userID = userID;
        this.bookID = bookID;
    }

    public Rating() {
    }
}
