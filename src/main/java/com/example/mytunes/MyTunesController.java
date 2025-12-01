package com.example.mytunes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.*;
import java.util.Optional;

public class MyTunesController {

    @FXML
    private Label currentlyPlayingSong;

    @FXML
    private TableColumn<Song, String> kolonneArtist;

    @FXML
    private TableColumn<Song, String> kolonneCategory;

    @FXML
    private TableColumn<Playlist, String> kolonneName;

    @FXML
    private TableColumn<Playlist, String> kolonnePlaylistTime;

    @FXML
    private TableColumn<Song, String> kolonneSongTime;

    @FXML
    private TableColumn<Playlist, String> kolonneSongs;

    @FXML
    private TableColumn<Song, String> kolonneTitle;

    @FXML
    private ListView<Song> listViewSongsOnPlaylist;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Playlist> tableViewPlaylists;

    @FXML
    private TableView<Song> tableViewSongs;

    @FXML
    private Slider volumenSlider;

    //Definition af listerne der holder dataene
    private ObservableList<Playlist> playlister = FXCollections.observableArrayList();
    private ObservableList<Song> sange = FXCollections.observableArrayList();
    private final ObservableList<Song> sangeIplayliste = FXCollections.observableArrayList();

    @FXML
    void handleAddNewPlaylist(ActionEvent event) {

    }

    @FXML
    void handleAddNewSong(ActionEvent event)
    {
        Dialog<ButtonType> dialog = new Dialog<>(); //Opretter en ny dialogboks, hvor knapperne (OK/Cancel) er typen ButtonType
        dialog.setTitle("Add new song"); //titlen i vinduet
        dialog.setHeaderText("Enter information about the new song"); //overskrift
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        //Opretter tekstfelter til title, artist, category, time
        TextField titleFelt = new TextField();
        titleFelt.setPromptText("Title");
        TextField artistFelt = new TextField();
        artistFelt.setPromptText("Artist");
        TextField categoryFelt = new TextField();
        categoryFelt.setPromptText("Category");
        TextField timeFelt = new TextField();
        timeFelt.setPromptText("Time");

        //opretter en VBox med 4 tekstfelter og med 10 pixels mellemrum
        VBox box = new VBox(10, titleFelt, artistFelt, categoryFelt, timeFelt);
        dialog.getDialogPane().setContent(box); //VBoxen sættes ind som indhold i dialogboksen

        Optional<ButtonType> resultat = dialog.showAndWait(); //viser dialogen og stopper og venter på at brugeren klikker OK eller Cancel

        if (resultat.isPresent() && resultat.get() == ButtonType.OK) //Tjekker om brugeren har valgt en knap og om det er OK-knappen
        {
            String title = titleFelt.getText(); //henter den tekst brugeren har skrevet i felterne
            String artist = artistFelt.getText();
            String category = categoryFelt.getText();
            String time = timeFelt.getText();

            Song nySang = new Song(title, artist, category, time); //opretter det nye sang objekt
            sange.add(nySang); //den nye sang tilføjes til vores ObservableList sange
            tableViewSongs.refresh(); //tableView opdateres
            //tableViewSongs.sort();
        }
    }

    @FXML
    void handleAddSongToPlaylist(ActionEvent event) {

    }

    @FXML
    void handleBackToPreviousSong(ActionEvent event) {

    }


    @FXML
    void handleDeletePlaylist(ActionEvent event) {

    }

    @FXML
    void handleDeleteSong(ActionEvent event) {
        Song s = tableViewSongs.getSelectionModel().getSelectedItem();
        playlister.remove(s);

    }

    @FXML
    void handleDeleteSongFromPlaylist(ActionEvent event) {
        Playlist valgtPlayliste = tableViewPlaylists.getSelectionModel().getSelectedItem();
        Song valgtSang = listViewSongsOnPlaylist.getSelectionModel().getSelectedItem();

        valgtPlayliste.getSongsList().remove(valgtSang);

        sangeIplayliste.setAll(valgtPlayliste.getSongsList());


    }

    @FXML
    void handleEditPlaylist(ActionEvent event) {

    }

    @FXML
    void handleEditSong(ActionEvent event) {

    }

    @FXML
    void handleMoveSongDown(ActionEvent event) {

    }

    @FXML
    void handleMoveSongUp(ActionEvent event) {

    }

    @FXML
    void handlePlaySong(ActionEvent event) {

    }

    @FXML
    void handleSearch(ActionEvent event) {

    }

    @FXML
    void handleSkipSong(ActionEvent event) {

    }

    //metode der gemmer en liste af Playlist-objekter i filen playlists.txt
    private void skrivPlaylisteObjekter(ObservableList<Playlist> playlists) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream("playlists.txt");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

        objectOutputStream.writeInt(playlists.size());
        for (Playlist p : playlists)
            objectOutputStream.writeObject(p);

        objectOutputStream.flush();
        objectOutputStream.close();
    }

    //metode der gemmer en liste af Song-objekter i filen songs.txt
    private void skrivSongsObjekter(ObservableList<Song> songs) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream("songs.txt");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

        objectOutputStream.writeInt(songs.size());
        for (Song s : songs)
            objectOutputStream.writeObject(s);

        objectOutputStream.flush();
        objectOutputStream.close();
    }

    //metode der bruges til at gemme data når brugeren lukker vinduet
    public void gemData() throws IOException
    {
        skrivPlaylisteObjekter(playlister);//gemmer Playlist objekter i filen ordrer.txt
        skrivSongsObjekter(sange); //gemmer Song objekter i filen varelager.txt
    }

    //metode der indlæser gemte Playlister fra filen playlists.txt
    private ObservableList<Playlist> læsPlaylistObjekter() throws IOException, ClassNotFoundException {
        ObservableList<Playlist> liste = FXCollections.observableArrayList();
        FileInputStream fileInputStream = new FileInputStream("playlists.txt");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

        int antal = objectInputStream.readInt();
        for (int i = 0; i < antal; ++i) {
            Playlist p = (Playlist) objectInputStream.readObject();
            liste.add(p);
        }

        objectInputStream.close();
        return (ObservableList<Playlist>) liste;
    }

    //metode der indlæser gemte varer fra filen songs.txt
    private ObservableList<Song> læsVareObjekter() throws IOException, ClassNotFoundException {
        ObservableList<Song> liste = FXCollections.observableArrayList();
        FileInputStream fileInputStream = new FileInputStream("songs.txt");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

        int antal = objectInputStream.readInt();
        for (int i = 0; i < antal; ++i) {
            Song s = (Song)objectInputStream.readObject();
            liste.add(s);
        }

        objectInputStream.close();
        return (ObservableList<Song>) liste;
    }
}