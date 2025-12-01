package com.example.mytunes;

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
    private TableColumn<?, ?> kolonneArtist;

    @FXML
    private TableColumn<?, ?> kolonneCategory;

    @FXML
    private TableColumn<?, ?> kolonneName;

    @FXML
    private TableColumn<?, ?> kolonnePlaylistTime;

    @FXML
    private TableColumn<?, ?> kolonneSongTime;

    @FXML
    private TableColumn<?, ?> kolonneSongs;

    @FXML
    private TableColumn<?, ?> kolonneTitle;

    @FXML
    private ListView<?> listViewSongsOnPlaylist;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<?> tableViewPlaylists;

    @FXML
    private TableView<?> tableViewSongs;

    @FXML
    private Slider volumenSlider;

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
    void handleClose(ActionEvent event) {

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