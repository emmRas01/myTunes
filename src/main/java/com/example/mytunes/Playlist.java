package com.example.mytunes;

import java.io.Serializable;
import java.util.ArrayList;

// Denne klasse skal kunne gemmes på en fil -> derfor skal Serializable implementeres
public class Playlist implements Serializable
{
    private static final long serialVersionUID = 2L; // ID til serialisering skal være unikt for hver klasse

    // Definition af Attribut
    private String name;

    // Array-liste til at gemme en eller flere sang-objekter i en Playliste
    private ArrayList<Song> songs = new ArrayList<>();

    // Metode til at tilføje en sang til Array Listen
    public void tilføjSang(Song s)
    {
        songs.add(s);
    }

    // Konstruktør til Playlist
    public Playlist(String name)
    {
        this.name = name;
    }

    // Get-metode der returnere hele array-listen med sange der tilhører en Playliste
    public ArrayList<Song> getSongsList()
    {
        return songs;
    }

    // Set-metode der opdaterer hele Array-listen med en ny liste af sange der tilhøre en Playliste
    public void setSongsList(ArrayList<Song> songList)
    {
        songs = songList;
    }

    // Get- og set-metode til name
    public String getName()
    {
        return name;
    }
    public void setName(String n)
    {
        name = n;
    }
}
