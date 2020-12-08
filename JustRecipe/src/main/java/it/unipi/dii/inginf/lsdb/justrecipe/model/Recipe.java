package it.unipi.dii.inginf.lsdb.justrecipe.model;

import java.sql.Timestamp;
import java.util.List;

public class Recipe {
    private String title;
    private String picture;
    private String instructions;
    private List<String> ingredients;
    private List<String> categories;
    private Timestamp creationTime;
    private int calories;
    private int fat;
    private int protain;
    private int carbs;
    private List<Comment> comments;

    //Blank constructor
    public Recipe(){}

    //Constructor
    public Recipe(String title, String picture, String instructions, List<String> ingredients, List<String> categories,
                  Timestamp creationTime, int calories, int fat, int protain, int carbs){
        this.title = title;
        this.picture = picture;
        this.instructions = instructions;
        this.ingredients = ingredients;
        this.categories = categories;
        this.creationTime = creationTime;
        this.calories = calories;
        this.fat = fat;
        this.protain = protain;
        this.carbs = carbs;
    }


    //Getters
    public String getTitle() {
        return title;
    }

    public String getPicture() {
        return picture;
    }

    public String getInstructions() {
        return instructions;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public List<String> getCategories() {
        return categories;
    }

    public Timestamp getCreationTime() {
        return creationTime;
    }

    public int getCalories() {
        return calories;
    }

    public int getFat() {
        return fat;
    }

    public int getProtain() {
        return protain;
    }

    public int getCarbs() {
        return carbs;
    }


    //Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setFat(int fat) {
        this.fat = fat;
    }

    public void setProtain(int protain) {
        this.protain = protain;
    }

    public void setCarbs(int carbs) {
        this.carbs = carbs;
    }
}
