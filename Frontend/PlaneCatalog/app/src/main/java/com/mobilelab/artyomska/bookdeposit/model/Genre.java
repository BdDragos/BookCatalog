package com.mobilelab.artyomska.bookdeposit.model;

import java.util.List;

public class Genre {

    private int ID;

    private String genreName;

    private List<Book> book;

    public Genre(int ID, String genreName) {
        this.ID = ID;
        this.genreName = genreName;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public List<Book> getBook() {
        return book;
    }

    public void setBook(List<Book> book) {
        this.book = book;
    }

    public Genre() {
    }
}
