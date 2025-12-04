package com.example.mytunes;

import java.io.Serializable;
import java.util.ArrayList;

public class Playlist implements Serializable //denne klasse skal kunne gemmes på en fil -> derfor skal man implementere Serializable
{
    private static final long serialVersionUID = 2L; //id til serialisering skal være unikt for hver klasse

    //Definition af Attributterne name, songs og time
    private String name;
    private String song;
    private String time;

    //Array-liste til at gemme en eller flere sang-objekter i en playliste
    private ArrayList<Song> songs = new ArrayList<>();

    //konstruktør
    public Playlist(String name, String song, String time)
    {
        this.name = name;
        this.song = song;
        this.time = time;
    }

    //Metode til at tilføje en sang til arrayListen der indeholder de sange der tilhører en playliste
    public void tilføjSang(Song s)
    {
        songs.add(s);
    }

    //Metode der returnere hele listen med sange der tilhøre en playliste
    public ArrayList<Song> getSongsList()
    {
        return songs;
    }

    //Setter-metode der opdatere hele Arraylisten med en ny liste af sange der tilhøre en playliste
    public void setSongsList(ArrayList<Song> songList)
    {
        songs = songList;
    }

    //Der skal være get-metoder og set-metoder for at tableview Playlist både kan hente data og indsætte ny data
    public String getName()
    {
        return name;
    }

    public String getSongs()
    {
        return song;
    }

    public String getTime()
    {
        return time;
    }

    public void setName(String n)
    {
        name = n;
    }

    public void setSongs(String s)
    {
        song = s;
    }

    public void setTime(String t)
    {
        time = t;
    }
}
