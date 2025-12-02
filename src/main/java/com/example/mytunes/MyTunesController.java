package com.example.mytunes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;

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

    //media objekt sættes op her, så den ikke bliver fjernet af garbage collectoren
    private static MediaPlayer mediaPlayer;

    public void initialize() //køres når programmet starter
    {
        //Kolonnerne sættes op med forbindelse til klassen Playlist med hver sit felt
        kolonneName.setCellValueFactory(new PropertyValueFactory<>("name"));
        kolonneSongs.setCellValueFactory(new PropertyValueFactory<>("songs"));
        kolonnePlaylistTime.setCellValueFactory(new PropertyValueFactory<>("time"));

        //Kolonnerne sættes op med forbindelse til klassen Song med hver sit felt
        kolonneTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        kolonneArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        kolonneCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        kolonneSongTime.setCellValueFactory(new PropertyValueFactory<>("time"));

        //når programmet starter læses data ind fra filen playlists.txt
        try {
            playlister = læsPlaylistObjekter();
            sange = læsSangObjekter();
        }  catch (Exception e) {
            System.out.println("Kunne ikke indlæse filen: " + e.getMessage());
        }

        //i vores TableViews og ListView indsættes objekterne fra vores ObservableLister (playlister, sange og sangeIplayliste)
        tableViewPlaylists.setItems(playlister);
        tableViewSongs.setItems(sange);
        listViewSongsOnPlaylist.setItems(sangeIplayliste);
    }

    @FXML
    void handleAddNewPlaylist(ActionEvent event)
    {
        Dialog<ButtonType> dialog = new Dialog<>(); //Opretter en ny dialogboks, hvor knapperne (OK/Cancel) er typen ButtonType
        dialog.setTitle("Add new playlist"); //titlen i vinduet
        dialog.setHeaderText("Enter information about the new playlist"); //overskrift
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        //Opretter tekstfelter til title, song, time
        TextField titleFelt = new TextField();
        titleFelt.setPromptText("Name");
        TextField songFelt = new TextField();
        songFelt.setPromptText("Song");
        TextField timeFelt = new TextField();
        timeFelt.setPromptText("Time");


        //opretter en VBox med 3 tekstfelter og med 10 pixels mellemrum
        VBox box = new VBox(10, titleFelt, songFelt, timeFelt);
        dialog.getDialogPane().setContent(box); //VBoxen sættes ind som indhold i dialogboksen

        Optional<ButtonType> resultat = dialog.showAndWait(); //viser dialogen og stopper og venter på at brugeren klikker OK eller Cancel

        if (resultat.isPresent() && resultat.get() == ButtonType.OK) //Tjekker om brugeren har valgt en knap og om det er OK-knappen
        {
            String name = titleFelt.getText(); //henter den tekst brugeren har skrevet i felterne
            String song = songFelt.getText();
            String time = timeFelt.getText();

            Playlist nyPlaylist = new Playlist(name, song, time); //opretter det nye sang objekt
            playlister.add(nyPlaylist); //den nye sang tilføjes til vores ObservableList sange
            tableViewPlaylists.refresh(); //tableView opdateres
            //tableViewSongs.sort();
        }
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
        TextField fileChooserFelt = new TextField();
        fileChooserFelt.setPromptText("Select a mp3 File");
        fileChooserFelt.setEditable(false); //gør at brugeren ikke kan taste i feltet

        //Opretter en file chooser knap
        Button selectFileButton = new Button("Select File");

        //sætter action på knappen til file chooser
        selectFileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select a File");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Music File", "*.mp3", "*.wav"));
        File valgtFil = fileChooser.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if (valgtFil != null) //hvis der er valgt en fil, så sættes tekstfeltet til den valgte fil
            {
                fileChooserFelt.setText(valgtFil.getAbsolutePath());
            }
        });

        //Opretter en HBox til file chooser feltet og knappen
        HBox fileChoserBox = new HBox(10, fileChooserFelt, selectFileButton);

        //opretter en VBox med 5 tekstfelter og med 10 pixels mellemrum
        VBox box = new VBox(10, titleFelt, artistFelt, categoryFelt, timeFelt, fileChooserFelt, fileChoserBox);
        dialog.getDialogPane().setContent(box); //VBoxen sættes ind som indhold i dialogboksen

        Optional<ButtonType> resultat = dialog.showAndWait(); //viser dialogen og stopper og venter på at brugeren klikker OK eller Cancel

        if (resultat.isPresent() && resultat.get() == ButtonType.OK) //Tjekker om brugeren har valgt en knap og om det er OK-knappen
        {
            String title = titleFelt.getText(); //henter den tekst brugeren har skrevet i felterne
            String artist = artistFelt.getText();
            String category = categoryFelt.getText();
            String time = timeFelt.getText();
            String musicFile = fileChooserFelt.getText();

            Song nySang = new Song(title, artist, category, time, musicFile); //opretter det nye sang objekt
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
    void handleDeletePlaylist(ActionEvent event)
    {
        Playlist valgtPlayliste = tableViewPlaylists.getSelectionModel().getSelectedItem(); //henter den playliste som brugeren har markeret

        if (valgtPlayliste != null) //hvis brugeren har markeret en playliste
        {
            playlister.remove(valgtPlayliste); //fjernes den fra playlist listen (ObservableList Playlister)
            sangeIplayliste.clear(); //clear listView'et
        }
        else //hvis brugeren ikke har markeret en ordre, så meldes der fejl
        {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please choose a Playlist to Delete!");
            alert.showAndWait();
        }

    }

    @FXML
    void handleDeleteSong(ActionEvent event)
    {
        Song valgtSang = tableViewSongs.getSelectionModel().getSelectedItem(); //henter den sang som brugeren har markeret og gemmer i variablen valgtSang

        if (valgtSang != null) //hvis brugeren har markeret en sang
        {
            sange.remove(valgtSang); //fjernes denne sang fra sang listen (ObservableList sange)

        } else { //hvis brugeren ikke har markeret en sang, så meldes der fejl
            Alert alert = new Alert(Alert.AlertType.WARNING, "Vælg en vare, der skal slettes!");
            alert.showAndWait();
        }
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
    protected void handlePlaySong(ActionEvent event)
    {
        Song valgtSang = tableViewSongs.getSelectionModel().getSelectedItem(); //henter den sang som brugeren har markeret

        if (valgtSang == null) //hvis brugeren ikke har valgt en sang kommer der en advarsel op
        {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please choose a Song to Play!");
            alert.showAndWait();
        }

        try
        {
            String filSti = new File(valgtSang.getMusicFile()).toURI().toString(); //henter fil-stien til sangen via getMusicFile()

            if (mediaPlayer != null) //hvis der i forvejen afspilles musik, stoppes den inden den nye valgte sang afspilles
            {
                mediaPlayer.stop();
            }

            //opretter Medie med den markerede sang og Medieplayer med mediet
            Media media = new Media(filSti);
            mediaPlayer = new MediaPlayer(media);

            //udskriver hvilken sang der afspilles, så brugeren kan se det
            currentlyPlayingSong.setText(valgtSang.getTitle());

            mediaPlayer.play(); //afspiller sangen

        } catch (Exception e) { //hvis der sker fejl får brugeren besked
            currentlyPlayingSong.setText("Error playing song");
        }
    }

    @FXML
    void handleSearch(ActionEvent event) {
        String searchTerm = searchField.getText().toLowerCase();
        ObservableList<Song> filteredSongs = sange.filtered(song ->
                song.getTitle().toLowerCase().contains(searchTerm)
        );
        tableViewSongs.setItems(filteredSongs);
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
    private ObservableList<Song> læsSangObjekter() throws IOException, ClassNotFoundException {
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