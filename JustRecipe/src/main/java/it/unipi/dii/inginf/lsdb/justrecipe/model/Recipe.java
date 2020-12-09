package it.unipi.dii.inginf.lsdb.justrecipe.model;

import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

public class Recipe {
    private String title;
    private String instructions;
    private List<String> ingredients;
    private List<String> categories;
    private int calories;
    private int fat;
    private int protein;
    private int carbs;
    private String creationTime;
    private String picture;
    private List<Comment> comments;

    //Blank constructor
    public Recipe(){}

    //Constructor
    public Recipe(String title, String picture, String instructions, List<String> ingredients, List<String> categories,
                  String creationTime, int calories, int fat, int protein, int carbs){
        this.title = title;
        this.picture = picture;
        this.instructions = instructions;
        this.ingredients = ingredients;
        this.categories = categories;
        this.creationTime = creationTime;
        this.calories = calories;
        this.fat = fat;
        this.protein = protein;
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

    public String getCreationTime() {
        return creationTime;
    }

    public int getCalories() {
        return calories;
    }

    public int getFat() {
        return fat;
    }

    public int getProtein() {
        return protein;
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

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }
    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setFat(int fat) {
        this.fat = fat;
    }

    public void setProtein(int protein) {
        this.protein = protein;
    }

    public void setCarbs(int carbs) {
        this.carbs = carbs;
    }
}
