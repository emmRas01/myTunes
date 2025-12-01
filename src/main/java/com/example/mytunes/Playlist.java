package com.example.mytunes;

import java.io.Serializable;

public class Playlist implements Serializable //denne klasse skal kunne gemmes på en fil -> derfor skal man implementere Serializable
{
    private static final long serialVersionUID = 2L; //id til serialisering skal være unikt for hver klasse

    //Definition af Attributterne name, songs og time
    private String name;
    private String songs;
    private String time;


}
