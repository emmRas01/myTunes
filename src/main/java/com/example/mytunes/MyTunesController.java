package com.example.mytunes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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

    //variabel til at indholde hvilken sang der spilles lige nu
    private Song currentSong;

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
            System.out.println("Could not read the file: " + e.getMessage());
        }

        //i vores TableViews og ListView indsættes objekterne fra vores ObservableLister (playlister, sange og sangeIplayliste)
        tableViewPlaylists.setItems(playlister);
        tableViewSongs.setItems(sange);
        listViewSongsOnPlaylist.setItems(sangeIplayliste);

        //når programmet starter sættes volumenSlider til lydstyrke 50 ud af 100
        volumenSlider.setValue(50);

        //når brugeren rykker på slideren ændres volumen
        volumenSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            mediaPlayer.setVolume(newValue.doubleValue() / 100);
        });

        //sortering af playliste navne i alfabetisk rækkefølge
        kolonneName.setSortType(TableColumn.SortType.ASCENDING); //stigende rækkefølge fra A-Z
        tableViewPlaylists.getSortOrder().add(kolonneName); //tilføjer kolonnen, så den kan sorteres
        tableViewPlaylists.sort(); //udfører sorteringen i TableViewet

        //sortering af sang navne i alfabetisk rækkefølge
        kolonneTitle.setSortType(TableColumn.SortType.ASCENDING); //stigende rækkefølge fra A-Z
        tableViewSongs.getSortOrder().add(kolonneTitle); //tilføjer kolonnen, så den kan sorteres
        tableViewSongs.sort(); //udfører sorteringen i TableViewet
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
            tableViewPlaylists.sort();
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
            tableViewSongs.sort();
        }
    }

    //når brugeren klikker på knappen med en pilen tilføjes den markeret sang til den markerede playliste
    @FXML
    void handleAddSongToPlaylist(ActionEvent event)
    {
        Playlist valgtPlayliste = tableViewPlaylists.getSelectionModel().getSelectedItem(); //henter den playliste som brugeren har markeret
        Song valgtSang = tableViewSongs.getSelectionModel().getSelectedItem(); //henter den sang som brugeren har markeret

        if (valgtPlayliste != null && valgtSang != null) //hvis brugeren har valgt en Playliste og en Sang
        {
            //sangen tilføjes til playlisten (gemmes i vores ArrayList songs der findes under Playlist-klassen)
            valgtPlayliste.tilføjSang(valgtSang);

            //Opdaterer ObservableList sangeIplayliste
            sangeIplayliste.setAll(valgtPlayliste.getSongsList());

        } else { //Hvis brugeren ikke markere både en ordre og en vare, så meldes der en fejl
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a Song and a Playlist!");
            alert.show();
        }
    }

    //metode til hvad der sker hvis brugeren klikker 1 eller 2 gange på en playliste
    @FXML
    void museklikPlaylists(MouseEvent event)
    {
        if(event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 1) //hvis brugeren klikker 1 gang
        {
            Playlist p = tableViewPlaylists.getSelectionModel().getSelectedItem(); //variabel der gemmer den playliste der er markeret
            if (p != null) //tjek at der er en markeret playliste
            {
                sangeIplayliste.setAll(p.getSongsList()); //vises de sange der tilhørere playlisten
            }
        } else
        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) //hvis brugeren klikker 2 gange
        {
            Playlist p = tableViewPlaylists.getSelectionModel().getSelectedItem(); //variabel der gemmer den playliste der er markeret
            if (p != null) { //tjek at der er en markeret playliste
                redigerPlaylistLinje(p); //kalder redigerings vinduet
            }
        }
    }

    //metode til hvad der sker hvis brugeren bruger piletast op eller ned i playliste tableview
    @FXML
    void tastOpNedPlaylists(KeyEvent event)
    {
        switch (event.getCode()) //getCode henter hvilken tast brugeren har klikket på
        {
            case UP: //pil up tasten -> ingen break efter, da den skal udføre samme handling for begge taster
            case DOWN: //pil ned tasten
                Playlist p = tableViewPlaylists.getSelectionModel().getSelectedItem(); //henter den playliste som brugeren har markeret
                if (p != null)
                {
                    sangeIplayliste.setAll(p.getSongsList()); //så vises sange der tilhører playlisten
                }
                break;
        }
    }

    @FXML
    void handleBackToPreviousSong(ActionEvent event) {

        // Henter index på den sang der er valgt i TableView
        int index = tableViewSongs.getSelectionModel().getSelectedIndex();

        // Hvis vi ikke er på første sang - vælg forrige
        if (index > 0) {

            // Vælg sangen før den nuværende
            tableViewSongs.getSelectionModel().select(index - 1);

            // Scroll så den forrige sang bliver synlig i mp3 afspilleren
            tableViewSongs.scrollTo(index - 1);
        }

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
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please choose a song to delete!");
            alert.showAndWait();
        }
    }

    @FXML
    void handleDeleteSongFromPlaylist(ActionEvent event) {
        Playlist valgtPlayliste = tableViewPlaylists.getSelectionModel().getSelectedItem();
        Song valgtSang = listViewSongsOnPlaylist.getSelectionModel().getSelectedItem();

        if (valgtPlayliste != null && valgtSang != null) {
            valgtPlayliste.getSongsList().remove(valgtSang);

            sangeIplayliste.setAll(valgtPlayliste.getSongsList());

            //sætter markøren til den sidste sang i listViewet -> så brugeren kan slette hele listen hurtigt ved behov
            listViewSongsOnPlaylist.getSelectionModel().selectLast();
        }
        else //error hvis brugeren ikke har markeret både en playliste og en sang
        {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a Playlist and a song!");
            alert.showAndWait();
        }
    }

    @FXML
    void handleEditPlaylist(ActionEvent event)
    {
        Playlist p = tableViewPlaylists.getSelectionModel().getSelectedItem(); //variabel der gemmer den playliste der er markeret
        if (p != null) { //tjek at der er en markeret playliste
            redigerPlaylistLinje(p); //kalder redigerings vinduet
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please choose a Playlist to edit!");
            alert.showAndWait();
        }
    }

    @FXML
    void handleEditSong(ActionEvent event)
    {
        Song s = tableViewSongs.getSelectionModel().getSelectedItem(); //variabel der gemmer den sang der er markeret
        if (s != null) { //tjek at der er en markeret sang
            redigerSangLinje(s); //kalder redigerings vinduet
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please choose a Song to edit!");
            alert.showAndWait();
        }
    }

    @FXML
    void handleMoveSongDown(ActionEvent event) {
        try {
            Song valgtSang = listViewSongsOnPlaylist.getSelectionModel().getSelectedItem(); //henter den sang som brugeren har markeret

            if (valgtSang != null) //hvis brugeren har markeret en sang
            {
                int i = sangeIplayliste.indexOf(valgtSang); //henter den plads/index som sangen står på
                Song næsteSang = sangeIplayliste.get(i + 1);

                sangeIplayliste.set(i, næsteSang); //den næste sang flyttes en plads op i listview
                sangeIplayliste.set(i + 1, valgtSang); //den valgte sang flyttes en plads ned i listView

                listViewSongsOnPlaylist.getSelectionModel().select(valgtSang); //sætter markeringen efter flyt
            }
        } catch (Exception e) {
            System.out.println("The song is already at the bottom: " + e.getMessage());
        }
    }

    @FXML
    void handleMoveSongUp(ActionEvent event) {
        try {
            Song valgtSang = listViewSongsOnPlaylist.getSelectionModel().getSelectedItem(); //henter den sang som brugeren har markeret

            if (valgtSang != null) //hvis brugeren har markeret en sang
            {
                int i = sangeIplayliste.indexOf(valgtSang); //henter den plads/index som sangen står på
                Song næsteSang = sangeIplayliste.get(i - 1);

                sangeIplayliste.set(i, næsteSang); //den næste sang flyttes en plads ned i listview
                sangeIplayliste.set(i - 1, valgtSang); //den valgte sang flyttes en plads op i listView

                listViewSongsOnPlaylist.getSelectionModel().select(valgtSang); //sætter markeringen efter flyt
            }
        } catch (Exception e) {
            System.out.println("The song is already at the top: " + e.getMessage());
        }
    }

    @FXML
    void handlePlaySong(MouseEvent event)
    {
        try
        {
            Song valgtSang = tableViewSongs.getSelectionModel().getSelectedItem(); //henter den sang som brugeren har markeret

            String filSti = new File(valgtSang.getMusicFile()).toURI().toString(); //henter fil-stien til sangen via getMusicFile()

            if (mediaPlayer == null || !valgtSang.equals(currentSong)) //hvis der ikke er oprettet en mediaPlayer eller hvis brugeren har valgt en ny sang
            {
                if (mediaPlayer != null) //hvis der i forvejen afspilles musik, stoppes den inden den nye valgte sang afspilles
                {
                    mediaPlayer.stop();
                }

                //opretter et Medie/musikfilen, opretter Medieplayer med mediet indeni
                Media media = new Media(filSti);
                mediaPlayer = new MediaPlayer(media);

                currentSong = valgtSang; //gemmer den valgte sang som currentSong

                mediaPlayer.play(); //afspiller sangen

                //udskriver hvilken sang der afspilles, så brugeren kan se det
                currentlyPlayingSong.setText(valgtSang.getTitle());

            }
            else //hvis brugeren ikke har valgt en ny sang -> genoptages sangafspilningen bare efter den har været på pause
            {
                mediaPlayer.play();
            }

        } catch (Exception e) { //hvis der sker en fejl i afspilningen, så får brugeren besked
            currentlyPlayingSong.setText("Please select a song to play!");
        }
    }

    @FXML
    void handlePauseSong(MouseEvent event)
    {
        if (mediaPlayer != null)
        {
            mediaPlayer.pause();
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

        // Henter index på den sang der aktuelt er valgt i TableView
        int index = tableViewSongs.getSelectionModel().getSelectedIndex();

        // Hvis vi IKKE er på den sidste sang i listen → vælg næste sang
        if (index < sange.size() - 1) {

            // Vælg den næste sang i rækken
            tableViewSongs.getSelectionModel().select(index + 1);

            // Scroll så den nye valgte sang bliver synlig i mp3 afspilleren
            tableViewSongs.scrollTo(index + 1);
        }
    }

    //Metode til at redigere en Playliste ved at åbne modalt dialogvindue med data i
    private void redigerPlaylistLinje(Playlist p) {
        // Lav vinduet som en dialog med 3 tekstfelter med data
        Dialog<ButtonType> dialogvindue = new Dialog();
        dialogvindue.setTitle("Edit Playlist");
        dialogvindue.setHeaderText("Edit Playlist");
        dialogvindue.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField name = new TextField();
        name.setPromptText("Enter name");
        TextField songs = new TextField();
        songs.setPromptText("Enter number of songs");
        TextField time = new TextField();
        time.setPromptText("Enter time");

        VBox box = new VBox(10, name, songs, time); //10 pixels mellemrum mellem hver tekst felt
        dialogvindue.getDialogPane().setContent(box);

        //Sæt data i felterne fra playliste-objektet
        name.setText(p.getName());
        songs.setText(p.getSongs());
        time.setText(p.getTime());

        //Her afsluttes dialogen med at man kan trykke på OK
        Optional<ButtonType> knap = dialogvindue.showAndWait();

        // Hvis man trykker OK gemmes data fra felterne og tabellen opdateres
        if (knap.isPresent() && knap.get() == ButtonType.OK) {
            p.setName(name.getText());
            p.setSongs(songs.getText());
            p.setTime(time.getText());
            tableViewPlaylists.refresh();
            tableViewPlaylists.sort();
        }
    }

    //Metode til at redigere en sang ved at åbne modalt dialogvindue med data i
    private void redigerSangLinje(Song s) {
        // Lav vinduet som en dialog med to tekstfelter med data
        Dialog<ButtonType> dialogvindue = new Dialog();
        dialogvindue.setTitle("Edit Song");
        dialogvindue.setHeaderText("Please enter information about the song");
        dialogvindue.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField title = new TextField();
        title.setPromptText("Enter title");
        TextField artist = new TextField();
        artist.setPromptText("Enter artist");
        TextField category = new TextField();
        category.setPromptText("Enter category");
        TextField time = new TextField();
        time.setPromptText("Enter time");

        VBox box = new VBox(10, title, artist, category, time); //10 pixels mellemrum mellem hver tekst felt
        dialogvindue.getDialogPane().setContent(box);

        //Sæt data i felterne fra sang-objektet
        title.setText(s.getTitle());
        artist.setText(s.getArtist());
        category.setText(s.getCategory());
        time.setText(s.getTime());

        //Her afsluttes dialogen med at man kan trykke på OK
        Optional<ButtonType> knap = dialogvindue.showAndWait();

        //Hvis man trykker OK gemmes data fra felterne og tabellen opdateres
        if (knap.isPresent() && knap.get() == ButtonType.OK)
        {
            s.setTitle(title.getText());
            s.setArtist(artist.getText());
            s.setCategory(category.getText());
            s.setTime(time.getText());
            tableViewSongs.refresh();
            tableViewSongs.sort();
            listViewSongsOnPlaylist.refresh();
        }
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