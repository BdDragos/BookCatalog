package com.mobilelab.artyomska.bookdeposit.model;

import java.util.List;

public class Author
{
    private int ID;

    private String authorName;

    private List<Book> book;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public List<Book> getBook() {
        return book;
    }

    public void setBook(List<Book> book) {
        this.book = book;
    }

    public Author() {
    }

    public Author(int ID, String authorName) {
        this.ID = ID;
        this.authorName = authorName;
    }
}
