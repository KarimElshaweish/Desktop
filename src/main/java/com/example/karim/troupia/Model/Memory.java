package com.example.karim.troupia.Model;

/**
 * Created by Karim on 2/13/2018.
 */

public class Memory {
    String Name;
    String Text;
    String Date;
    String ImageUrl;
    String Longtuide;
    String Lontuide;

    public String GetLongtuide() {
        return this.Longtuide;
    }

    public void SetLongtuide(String longtuide) {
        this.Longtuide = longtuide;
    }

    public String GetLontuide() {
        return Lontuide;
    }

    public void SetLontuide(String lontuide) {
        Lontuide = lontuide;
    }


    public String GetImageUrl() {
        return ImageUrl;
    }

    public void SetImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }


    public String GetName() {
        return this.Name;
    }

    public void SetName(String name) {
        this.Name = name;
    }

    public String GetText() {
        return Text;
    }

    public void SetText(String text) {
        Text = text;
    }

    public String GetDate() {
        return Date;
    }

    public void SetDate(String date) {
        Date = date;
    }

}
