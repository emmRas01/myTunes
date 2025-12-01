package com.example.mytunes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

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
    void handleAddNewSong(ActionEvent event) {

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

    }

    @FXML
    void handleDeleteSongFromPlaylist(ActionEvent event) {

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

}