package com.example.mytunes;

import java.io.Serializable;

// Denne klasse skal kunne gemmes på en fil -> derfor skal Serializable implementeres
public class Song implements Serializable
{
    private static final long serialVersionUID = 1L; // ID til serialisering skal være unikt for hver klasse

    // Definition af attributterne
    private String title;
    private String artist;
    private String category;
    private String time;
    private String musicFile;

    // Konstruktør til Song
    public Song(String title, String artist, String category, String time, String musicFile)
    {
        this.title = title;
        this.artist = artist;
        this.category = category;
        this.time = time;
        this.musicFile = musicFile;
    }

    // Get-metoder og set-metoder
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
    } // Bruges bl.a. til at hente musik filen når den skal afspilles

    public void setMusicFile(String musicFile)
    {
        this.musicFile = musicFile;
    }

    // Udseende på udskrift i vores ListView i midten (sange der tilhører en playliste)
    @Override
    public String toString()
    {
        return title + "   " + artist + "   " + category + "   " + time;
    }
}
