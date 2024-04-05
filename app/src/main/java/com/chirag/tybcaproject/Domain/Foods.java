package com.chirag.tybcaproject.Domain;

import java.io.Serializable;

public class Foods implements Serializable {

    private String key;
    private int CategoryId;
    private String Description;
    private  boolean BestFood =true;
    private int Id;
    private int LocationId;
    private double Price;
    private String ImagePath;
    private int PriceId;
    private double Star;
    private String Title;
    private int numberInCart;

    @Override
    public String toString() {
        return Title;
    }

    public Foods() {
    }
    public Foods(String key, int categoryId, String description, boolean bestFood, int id, int locationId, double price, String imagePath, int priceId, double star, String title, int numberInCart) {
        this.key = key;
        CategoryId = categoryId;
        Description = description;
        BestFood = bestFood;
        Id = id;
        LocationId = locationId;
        Price = price;
        ImagePath = imagePath;
        PriceId = priceId;
        Star = star;
        Title = title;
        this.numberInCart = numberInCart;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    public int getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(int categoryId) {
        CategoryId = categoryId;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public boolean isBestFood() {
        return BestFood;
    }

    public void setBestFood(boolean bestFood) {
        BestFood = bestFood;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getLocationId (){return LocationId;
    }

    public void setLocationId(int locationId) {
        LocationId = locationId;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        this.Price = price;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

    public int getPriceId() {
        return PriceId;
    }

    public void setPriceId(int priceId) {
        PriceId = priceId;
    }

    public double getStar() {
        return Star;
    }

    public void setStar(double star) {
        this.Star = star;
    }


    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    public int getNumberInCart() {
        return numberInCart;
    }

    public void setNumberInCart(int numberInCart) {
        this.numberInCart = numberInCart;
    }
}
