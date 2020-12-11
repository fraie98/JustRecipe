package it.unipi.dii.inginf.lsdb.justrecipe.model;

import com.google.gson.annotations.SerializedName;
import org.bson.types.ObjectId;

import java.beans.Transient;
import java.util.ArrayList;
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
    private Date creationTime;
    private String picture;
    private String authorUsername;
    private List<Comment> comments;

    //Blank constructor
    public Recipe(){}

    //Constructor
    public Recipe(String title, String instructions, List<String> ingredients, List<String> categories,
                  int calories, int fat, int protein, int carbs, Date creationTime, String picture, String authorUsername,
                  List<Comment> comments)
    {
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
        this.authorUsername = authorUsername;
        this.comments = comments;
    }

    public Recipe(String title, String instructions, List<String> ingredients, List<String> categories,
                  int calories, int fat, int protein, int carbs, Date creationTime, String picture,
                  String authorUsername)
    {
        this( title, instructions, ingredients, categories, calories, fat, protein, carbs,
                creationTime, picture, authorUsername, new ArrayList<Comment>());
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

    public Date getCreationTime() {
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

    public String getAuthorUsername() {
        return authorUsername;
    }

    public List<Comment> getComments() {
        return comments;
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

    public void setCreationTime(Date creationTime) {
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

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "title='" + title + '\'' +
                ", instructions='" + instructions + '\'' +
                ", ingredients=" + ingredients +
                ", categories=" + categories +
                ", calories=" + calories +
                ", fat=" + fat +
                ", protein=" + protein +
                ", carbs=" + carbs +
                ", creationTime=" + creationTime +
                ", picture='" + picture + '\'' +
                ", authorUsername='" + authorUsername + '\'' +
                ", comments=" + comments +
                '}';
    }
}
