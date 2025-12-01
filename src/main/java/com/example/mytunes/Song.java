package com.example.mytunes;

import java.io.Serializable;

public class Song implements Serializable //denne klasse skal kunne gemmes på en fil -> derfor skal man implementere Serializable
{
    private static final long serialVersionUID = 1L; //id til serialisering skal være unikt for hver klasse

    private String title;
    private String artist;
    private String category;
    private String time;


    public Song(String title, String artist, String category, String time)
    {
        this.title = title;
        this.artist = artist;
        this.category = category;
        this.time = time;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String t)
    {
        title = t;
    }

    public String getArtist()
    {
        return artist;
    }

    public void setArtist(String a)
    {
        artist = a;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String c)
    {
        category = c;
    }

    public String getTime()
    {
        return time;
    }
    public void setTime(String tm)
    {
        time = tm;
    }

    @Override
    public String toString()
    {
        return title + "   " + artist + "   " + category + "   " + time;
    }

}
