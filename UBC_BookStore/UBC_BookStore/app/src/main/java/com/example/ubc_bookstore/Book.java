package com.example.ubc_bookstore;

public class Book {


    private String ISBN;
    private String title;
    private String author;
    private String genre;
    private int qtyNew;
    private int qtyUsed;
    private String lastUpdate;
    private double priceNew;
    private double priceUsed;
    private String imageURL;
    private String searchString;

    public Book() {
    }

    public Book(String ISBN, String title, String author, String genre, int qtyNew, int qtyUsed, String lastUpdate, double priceNew, double priceUsed, String imageURL, String searchString) {
        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.qtyNew = qtyNew;
        this.qtyUsed = qtyUsed;
        this.lastUpdate = lastUpdate;
        this.priceNew = priceNew;
        this.priceUsed = priceUsed;
        this.imageURL = imageURL;
        this.searchString = searchString;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getQtyNew() {
        return qtyNew;
    }

    public void setQtyNew(int qtyNew) {
        this.qtyNew = qtyNew;
    }

    public int getQtyUsed() {
        return qtyUsed;
    }

    public void setQtyUsed(int qtyUsed) {
        this.qtyUsed = qtyUsed;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public double getPriceNew() {
        return priceNew;
    }

    public void setPriceNew(double priceNew) {
        this.priceNew = priceNew;
    }

    public double getPriceUsed() {
        return priceUsed;
    }

    public void setPriceUsed(double priceUsed) {
        this.priceUsed = priceUsed;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }
}

