package com.mobilelab.artyomska.bookdeposit.model;

import java.time.LocalDate;
import java.util.List;

public class Book
{
    private int ID;
    public String title;
    private String isbn;
    private int noPage;
    private String edition;
    private String bLanguage;
    private byte[] bookPic;
    private String publisherSite;
    private String bookFormat;
    private LocalDate releaseDate;
    private LocalDate initialReleaseDate;
    private String publisher;
    private String overview;
    private List<Author> author;
    private List<Genre> genre;
    private List<UserData> user;
    private double rating;
    private List<Review> review;

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    private String series;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getNoPage() {
        return noPage;
    }

    public void setNoPage(int noPage) {
        this.noPage = noPage;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getbLanguage() {
        return bLanguage;
    }

    public void setbLanguage(String bLanguage) {
        this.bLanguage = bLanguage;
    }

    public byte[] getBookPic() {
        return bookPic;
    }

    public void setBookPic(byte[] bookPic) {
        this.bookPic = bookPic;
    }

    public String getPublisherSite() {
        return publisherSite;
    }

    public void setPublisherSite(String publisherSite) {
        this.publisherSite = publisherSite;
    }

    public String getBookFormat() {
        return bookFormat;
    }

    public void setBookFormat(String bookFormat) {
        this.bookFormat = bookFormat;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public LocalDate getInitialReleaseDate() {
        return initialReleaseDate;
    }

    public void setInitialReleaseDate(LocalDate initialReleaseDate) {
        this.initialReleaseDate = initialReleaseDate;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public List<Author> getAuthor() {
        return author;
    }

    public void setAuthor(List<Author> author) {
        this.author = author;
    }

    public List<Genre> getGenre() {
        return genre;
    }

    public void setGenre(List<Genre> genre) {
        this.genre = genre;
    }

    public List<UserData> getUser() {
        return user;
    }

    public void setUser(List<UserData> user) {
        this.user = user;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<Review> getReview() {
        return review;
    }

    public void setReview(List<Review> review) {
        this.review = review;
    }

    public Book() {
    }

    public Book(int ID, String title, String isbn, int noPage, String edition, String bLanguage, String publisherSite, String bookFormat, LocalDate releaseDate, LocalDate initialReleaseDate, String publisher, String overview, List<Author> author, List<Genre> genre, double rating, List<Review> review, byte[] bookPic) {
        this.ID = ID;
        this.title = title;
        this.isbn = isbn;
        this.noPage = noPage;
        this.edition = edition;
        this.bLanguage = bLanguage;
        this.bookPic = bookPic;
        this.publisherSite = publisherSite;
        this.bookFormat = bookFormat;
        this.releaseDate = releaseDate;
        this.initialReleaseDate = initialReleaseDate;
        this.publisher = publisher;
        this.overview = overview;
        this.author = author;
        this.genre = genre;
        this.rating = rating;
        this.review = review;
    }
}
