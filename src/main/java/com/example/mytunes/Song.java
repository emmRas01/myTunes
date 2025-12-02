package com.example.mytunes;

import java.io.Serializable;

public class Song implements Serializable //denne klasse skal kunne gemmes på en fil -> derfor skal man implementere Serializable
{
    private static final long serialVersionUID = 1L; //id til serialisering skal være unikt for hver klasse

    //Definition af title, artist, category, time
    private String title;
    private String artist;
    private String category;
    private String time;
    private String musicFile;

    //konstruktør
    public Song(String title, String artist, String category, String time, String musicFile)
    {
        this.title = title;
        this.artist = artist;
        this.category = category;
        this.time = time;
        this.musicFile = musicFile;
    }

    //Der skal være get-metoder og set-metoder for at tableview Songs både kan hente data og indsætte ny data
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

    public String getMusicFile()
    {
        return musicFile;
    }


    //Udseende på udskrift i vores listView i midten
    @Override
    public String toString()
    {
        return title + "   " + artist + "   " + category + "   " + time;
    }

}
