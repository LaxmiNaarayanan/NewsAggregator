package com.example.newsaggregator.model;

import android.content.res.Resources;

import com.example.newsaggregator.MainActivity;
import com.example.newsaggregator.R;

import java.util.HashMap;
import java.util.Map;

public class News {
    String id,category, title,author,desc,time,newsUrl,urlImage,name;
    private Map<String, Integer> colors;



    public News(){

    }


    public News(String title, String author, String desc, String time, String urlImage, String newsUrl) {
        this.title = title;
        this.author = author;
        this.desc = desc;
        this.time = time;
        this.urlImage = urlImage;
        this.newsUrl = newsUrl;

    }

// Name of the publisher , id then the category of the news we will get this from the above link

    public News(String id, String name, String category) {
        this.id = id;this.name = name; this.category = category;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDesc() {
        return desc;
    }

    public String getTime() {
        return time;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setColor(MainActivity mainActivity) {
        colors = new HashMap<>();
        Resources r = mainActivity.getResources();


        colors.put("business", r.getColor(R.color.colorAccent));
        colors.put("general", r.getColor(R.color.business));

        colors.put("technology", r.getColor(R.color.entertainment));
        colors.put("sports", r.getColor(R.color.purple_200));

        colors.put("entertainment", r.getColor(R.color.science));
        colors.put("health", r.getColor(R.color.general));

        colors.put("science", r.getColor(R.color.health));
    }

    public int getColor(String type) {
        return colors.get(type);
    }

    @Override
    public String toString() {
        return "News{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", desc='" + desc + '\'' +
                ", time='" + time + '\'' +
                ", urlImage='" + urlImage + '\'' +
                '}';
    }


}
