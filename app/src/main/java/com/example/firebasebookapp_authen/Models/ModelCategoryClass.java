package com.example.firebasebookapp_authen.Models;

public class ModelCategoryClass {  // this for the Categories and niot for books
  private   String category, id, timestamp, uId;

    public ModelCategoryClass() {
    }

    public ModelCategoryClass(String category, String id, String timestamp, String uId) {
        this.category = category;
        this.id = id;
        this.timestamp = timestamp;
        this.uId = uId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }
}
