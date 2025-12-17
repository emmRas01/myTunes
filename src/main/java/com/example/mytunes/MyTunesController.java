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
import java.util.ArrayList;
import java.util.Optional;

public class MyTunesController
{
    @FXML
    private Label currentlyPlayingSong;

    @FXML
    private TableColumn<Song, String> kolonneArtist;

    @FXML
    private TableColumn<Song, String> kolonneCategory;

    @FXML
    private TableColumn<Playlist, String> kolonneName;

    @FXML
    private TableColumn<Song, String> kolonneSongTime;

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

    // Definition af listerne der holder dataene
    private ObservableList<Playlist> playlister = FXCollections.observableArrayList();
    private ObservableList<Song> sange = FXCollections.observableArrayList();
    private final ObservableList<Song> sangeIplayliste = FXCollections.observableArrayList();

    // Media objekt sættes op her, så den ikke bliver fjernet af garbage collector
    private static MediaPlayer mediaPlayer;

    // Variabel til at indeholde den sang der spilles lige nu
    private Song currentSong;

    // Den aktuelle sangliste, som der afspilles fra lige nu
    private ObservableList<Song> currentSongList;

    public void initialize()
    {
        // Name kolonnen sættes op med forbindelse til klassen Playlist
        kolonneName.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Kolonnerne sættes op med forbindelse til klassen Song
        kolonneTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        kolonneArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        kolonneCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        kolonneSongTime.setCellValueFactory(new PropertyValueFactory<>("time"));

        // Når programmet starter læses gemt data ind fra filerne playlists.txt og songs.txt
        try
        {
            playlister = læsPlaylistObjekter();
            sange = læsSangObjekter();
        }
        catch (Exception e)
        {
            System.out.println("Could not read the file: " + e.getMessage());
        }

        // Når programmet starter sættes Song og Playlist objekter fra vores ObservableLists ind i de 2 TableViews og ListViewet
        tableViewPlaylists.setItems(playlister);
        tableViewSongs.setItems(sange);
        listViewSongsOnPlaylist.setItems(sangeIplayliste);

        // Når programmet starter sættes volumenSlider til lydstyrke 50 ud af 100
        volumenSlider.setValue(50);

        // Når brugeren rykker på slideren ændres volumen
        volumenSlider.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            if (mediaPlayer != null)
            {
                mediaPlayer.setVolume(newValue.doubleValue() / 100); // Konverterer fra procent (0 - 100) til brøk (0.0 - 1.0)
            }
        });

        // Sortering af navnene på playlisterne i alfabetisk rækkefølge
        kolonneName.setSortType(TableColumn.SortType.ASCENDING); // Stigende fra A-Z
        tableViewPlaylists.getSortOrder().add(kolonneName); // Tilføjer kolonnen, så den kan sorteres
        tableViewPlaylists.sort(); // Udfører sorteringen i TableViewet

        // Sortering af navnene på sangene i alfabetisk rækkefølge
        kolonneTitle.setSortType(TableColumn.SortType.ASCENDING); // Stigende fra A-Z
        tableViewSongs.getSortOrder().add(kolonneTitle); // Tilføjer kolonnen, så den kan sorteres
        tableViewSongs.sort(); // Udfører sorteringen i TableViewet
    }

    @FXML // Metode til at brugeren kan oprette en ny playliste (knap: Add new playlist)
    void handleAddNewPlaylist(ActionEvent event)
    {
        Dialog<ButtonType> dialog = new Dialog<>(); // Opretter en ny dialogboks med info-tekst og knapper (OK/Cancel)
        dialog.setTitle("Add new playlist");
        dialog.setHeaderText("Enter information about the new playlist");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField titleFelt = new TextField(); // Opretter tekstfelt til name
        titleFelt.setPromptText("Name");

        VBox box = new VBox(titleFelt); // Opretter en VBox med tekstfeltet
        dialog.getDialogPane().setContent(box); // VBoxen sættes ind som indhold i dialogboksen

        // Viser dialogen, og stopper og venter på at brugeren klikker OK eller Cancel
        Optional<ButtonType> resultat = dialog.showAndWait();

        // Tjekker om brugeren har valgt en knap og om det er OK-knappen
        if (resultat.isPresent() && resultat.get() == ButtonType.OK)
        {
            String name = titleFelt.getText(); // Henter den tekst brugeren har skrevet i feltet

            if (!name.isEmpty()) // Tjekker at brugeren har skrevet noget i feltet
            {
                Playlist nyPlaylist = new Playlist(name); // Opretter det nye Playlist objekt
                playlister.add(nyPlaylist); // Den nye playliste tilføjes til vores ObservableList playlister
                tableViewPlaylists.refresh();
                tableViewPlaylists.sort();
            }
            else // Hvis brugeren ikke har udfyldt feltet, får man besked -> Please fill out all fields
            {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill out all fields.");
                alert.show();
            }
        }
    }

    @FXML // Metode til at brugeren kan oprette en ny sang (knap: Add new song)
    void handleAddNewSong(ActionEvent event)
    {
        Dialog<ButtonType> dialog = new Dialog<>(); // Opretter en ny dialogboks med info-tekst og knapper (OK/Cancel)
        dialog.setTitle("Add new song");
        dialog.setHeaderText("Enter information about the new song");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Opretter tekstfelter til title, artist, time, samt categoryBox til category og felt til valg af musikfil
        TextField titleFelt = new TextField();
        titleFelt.setPromptText("Title");
        TextField artistFelt = new TextField();
        artistFelt.setPromptText("Artist");
        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("Rock", "Pop", "Julemusik", "Elektronisk", "Hip-Hop", "Klassisk");
        categoryBox.setPromptText("Choose category");
        TextField timeFelt = new TextField();
        timeFelt.setPromptText("Time");
        TextField fileChooserFelt = new TextField();
        fileChooserFelt.setPromptText("Select a mp3 File");
        fileChooserFelt.setEditable(false); // Gør at brugeren ikke kan skrive i feltet

        // Opretter en file chooser knap
        Button selectFileButton = new Button("Select File");
        selectFileButton.setOnAction(e -> // Sætter action på knappen til file chooser
        {
            FileChooser fileChooser = new FileChooser(); // Opretter fileChooser med info tekst og krav til fil-type
            fileChooser.setTitle("Select a File");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Music File", "*.mp3", "*.wav"));

            // Åbner filechooser hvori brugeren kan vælge en musikfil
            File valgtFil = fileChooser.showOpenDialog(dialog.getDialogPane().getScene().getWindow());

            if (valgtFil != null) // Hvis der er valgt en fil, så sættes tekstfeltet til den valgte fil
            {
                fileChooserFelt.setText(valgtFil.getAbsolutePath()); // Så brugeren kan se det
            }
        });

        // Opretter en HBox til file chooser feltet og knappen, så layout bliver: [fileChooserFelt] [Select File]
        HBox fileChoserBox = new HBox(10, fileChooserFelt, selectFileButton);

        // Opretter en VBox med de 5 tekstfelter med 10 pixels mellemrum
        VBox box = new VBox(10, titleFelt, artistFelt, categoryBox, timeFelt, fileChooserFelt, fileChoserBox);
        dialog.getDialogPane().setContent(box); // VBoxen sættes ind som indhold i dialogboksen

        // Viser dialogen, og stopper og venter på at brugeren klikker OK eller Cancel
        Optional<ButtonType> resultat = dialog.showAndWait();

        // Tjekker om brugeren har valgt en knap og om det er OK-knappen
        if (resultat.isPresent() && resultat.get() == ButtonType.OK)
        {
            String title = titleFelt.getText(); // Henter den tekst brugeren har skrevet i felterne
            String artist = artistFelt.getText();
            String category = categoryBox.getValue();
            String time = timeFelt.getText();
            String musicFile = fileChooserFelt.getText();

            // Tjekker at brugeren har skrevet noget i felterne
            if (!title.isEmpty() && !artist.isEmpty() && !category.isEmpty() && !time.isEmpty() && !musicFile.isEmpty())
            {
                Song nySang = new Song(title, artist, category, time, musicFile); // Opretter det nye Song objekt
                sange.add(nySang); // Den nye sang tilføjes til vores ObservableList sange
                tableViewSongs.refresh();
                tableViewSongs.sort();
            }
            else // Hvis brugeren ikke har udfyldt felterne, får man besked -> Please fill out all fields
            {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please fill out all fields.");
                alert.show();
            }
        }
    }

    @FXML // Metode til at tilføje en markeret sang til en markeret playliste (knap: pil til venstre)
    void handleAddSongToPlaylist(ActionEvent event)
    {
        // Henter den playliste og den sang som brugeren har markeret
        Playlist valgtPlayliste = tableViewPlaylists.getSelectionModel().getSelectedItem();
        Song valgtSang = tableViewSongs.getSelectionModel().getSelectedItem();

        // Tjekker at brugeren både har valgt en Playliste og en Sang
        if (valgtPlayliste != null && valgtSang != null)
        {
            // Sangen tilføjes til playlisten (gemmes i vores ArrayList songs der findes under Playlist-klassen)
            valgtPlayliste.tilføjSang(valgtSang);

            // Opdaterer ObservableList sangeIplayliste
            sangeIplayliste.setAll(valgtPlayliste.getSongsList());

        }
        else // Hvis brugeren ikke markeret både en playliste og en sang, så får man besked
        {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a Song and a Playlist!");
            alert.show();
        }
    }

    @FXML // Metode til hvad der sker hvis brugeren klikker 1 eller 2 gange på en playliste
    void museklikPlaylists(MouseEvent event)
    {
        // Hvis brugeren klikker 1 gang vises de sange der tilhører playlisten
        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 1)
        {
            // Henter og gemmer den playliste der er markeret
            Playlist p = tableViewPlaylists.getSelectionModel().getSelectedItem();

            if (p != null) // Tjekker at der er en markeret playliste
            {
                sangeIplayliste.setAll(p.getSongsList()); // Sangene i playlisten vises i vores ListView
            }
        }
        // Hvis brugeren klikker 2 gange popper redigeringsvinduet op
        else if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2)
        {
            // Henter og gemmer den playliste der er markeret
            Playlist p = tableViewPlaylists.getSelectionModel().getSelectedItem();

            if (p != null) //Tjekker at der er en markeret playliste
            {
                redigerPlaylistLinje(p); // Kalder redigerings vinduet
            }
        }
    }

    @FXML // Metode til at brugeren kan bruge piletasterne op/ned i tableView for hurtigt at se sangene i playlisterne
    void tastOpNedPlaylists(KeyEvent event)
    {
        switch (event.getCode()) // GetCode henter hvilken tast brugeren har klikket på
        {
            case UP: // Pil op tasten
                // Ingen break her, da den skal hoppe videre udføre samme handling for begge taster
            case DOWN: // Pil ned tasten

                // Henter den playliste som brugeren har markeret
                Playlist p = tableViewPlaylists.getSelectionModel().getSelectedItem();

                // Tjekker at brugeren har markeret en playliste
                if (p != null)
                {
                    sangeIplayliste.setAll(p.getSongsList()); // Viser de sange der tilhører playlisten
                }
                break;
        }
    }

    @FXML // Metode til at slette en playliste (knap: Delete playlist)
    void handleDeletePlaylist(ActionEvent event)
    {
        // Henter den playliste som brugeren har markeret
        Playlist valgtPlayliste = tableViewPlaylists.getSelectionModel().getSelectedItem();

        // Tjekker at brugeren har markeret en playliste
        if (valgtPlayliste != null)
        {
            // Opretter et vindue hvor brugeren kan bekræfte at den skal slettes
            Alert erDuSikkerAlarm = new Alert(Alert.AlertType.CONFIRMATION);
            erDuSikkerAlarm.setTitle("Delete Playlist");
            erDuSikkerAlarm.setHeaderText("Are you sure you want to delete this playlist?");

            Optional<ButtonType> beslutning = erDuSikkerAlarm.showAndWait(); // Venter på at brugeren klikker ok

            // Hvis brugeren klikker ok
            if (beslutning.isPresent() && beslutning.get() == ButtonType.OK)
            {
                // Stop afspilningen hvis der afspilles en sang fra den playliste der slettes
                if (currentSong != null && valgtPlayliste.getSongsList().contains(currentSong))
                {
                    if (mediaPlayer != null)
                    {
                        mediaPlayer.stop();
                        mediaPlayer = null;
                    }

                    currentSong = null;
                    currentSongList = null;
                    currentlyPlayingSong.setText("");

                    tableViewSongs.getSelectionModel().clearSelection();
                    listViewSongsOnPlaylist.getSelectionModel().clearSelection();
                }

                // Sletter playlisten fra listen med playlister
                playlister.remove(valgtPlayliste);
                sangeIplayliste.clear(); // Clear ListView

                // Sætter markøren til den sidste playliste i tableViewet -> så brugeren kan slette hele listen hurtigt ved behov
                tableViewPlaylists.getSelectionModel().selectLast();
            }

            // Hvis brugeren klikker cancel
            if (beslutning.isPresent() && beslutning.get() == ButtonType.CANCEL)
            {
                System.out.println("Nothing got deleted");
            }
        }
        else // Hvis brugeren ikke har markeret en playliste, så får man besked
        {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please choose a Playlist to Delete!");
            alert.showAndWait();
        }
    }

    @FXML // Metode til at slette en sang (knap: Delete song)
    void handleDeleteSong(ActionEvent event)
    {
        // Henter den sang som brugeren har markeret og gemmer i variablen valgtSang
        Song valgtSang = tableViewSongs.getSelectionModel().getSelectedItem();

        // Tjekker at brugeren har valgt en sang
        if (valgtSang != null)
        {
            // Opretter et vindue hvor brugeren kan bekræfte at den skal slettes
            Alert erDuSikkerAlarm = new Alert(Alert.AlertType.CONFIRMATION);
            erDuSikkerAlarm.setTitle("Delete Song");
            erDuSikkerAlarm.setHeaderText("Are you sure you want to delete this song?");

            Optional<ButtonType> beslutning = erDuSikkerAlarm.showAndWait(); // Venter på at brugeren klikker ok

            // Hvis brugeren klikker ok
            if (beslutning.isPresent() && beslutning.get() == ButtonType.OK)
            {
                // Stop afspilningen hvis den slettede sang spiller
                if (currentSong != null && currentSong.equals(valgtSang))
                {
                    if (mediaPlayer != null)
                    {
                        mediaPlayer.stop();
                        mediaPlayer = null;
                    }

                    currentSong = null;
                    currentSongList = null;
                    currentlyPlayingSong.setText("");

                    tableViewSongs.getSelectionModel().clearSelection();
                    listViewSongsOnPlaylist.getSelectionModel().clearSelection();
                }

                // Sangen slettes fra listen med sange (ObservableList sange)
                sange.remove(valgtSang);

                // Sætter markøren til den sidste sang i tableViewet -> så brugeren kan slette hele listen hurtigt ved behov
                tableViewSongs.getSelectionModel().selectLast();
            }

            // Hvis brugeren klikker cancel
            if (beslutning.isPresent() && beslutning.get() == ButtonType.CANCEL)
            {
                System.out.println("Nothing got deleted");
            }

        }
        else // Hvis brugeren ikke har markeret en sang, så får man besked
        {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please choose a song to delete!");
            alert.showAndWait();
        }
    }

    @FXML // Metode til at slette en sang fra en playliste (knap: Delete song from playlist)
    void handleDeleteSongFromPlaylist(ActionEvent event)
    {
        // Henter den playliste og den sang som brugeren har markeret
        Playlist valgtPlayliste = tableViewPlaylists.getSelectionModel().getSelectedItem();
        Song valgtSang = listViewSongsOnPlaylist.getSelectionModel().getSelectedItem();

        // Tjekker at brugeren både har valgt en playliste og en sang
        if (valgtPlayliste != null && valgtSang != null)
        {
            // Opretter et vindue hvor brugeren kan bekræfte at den skal slettes
            Alert erDuSikkerAlarm = new Alert(Alert.AlertType.CONFIRMATION);
            erDuSikkerAlarm.setTitle("Delete Song from Playlist");
            erDuSikkerAlarm.setHeaderText("Are you sure you want to delete this song from the playlist?");

            Optional<ButtonType> beslutning = erDuSikkerAlarm.showAndWait(); // Venter på at brugeren klikker ok

            // Hvis brugeren klikker ok
            if (beslutning.isPresent() && beslutning.get() == ButtonType.OK)
            {
                // Afspilningen stoppes hvis den slettede sang spiller
                if (currentSong != null && currentSong.equals(valgtSang))
                {
                    if  (mediaPlayer != null)
                    {
                        mediaPlayer.stop();
                        mediaPlayer = null;
                    }

                    currentSong = null;
                    currentSongList = null;
                    currentlyPlayingSong.setText("");

                    tableViewSongs.getSelectionModel().clearSelection();
                    listViewSongsOnPlaylist.getSelectionModel().clearSelection();
                }

                // Sletter sangen i playlisten
                valgtPlayliste.getSongsList().remove(valgtSang);
                sangeIplayliste.setAll(valgtPlayliste.getSongsList()); // ListViewet opdateres

                // Sætter markøren til den sidste sang i listViewet -> så brugeren kan slette hele listen hurtigt ved behov
                listViewSongsOnPlaylist.getSelectionModel().selectLast();
            }

            // Hvis brugeren klikker cancel
            if (beslutning.isPresent() && beslutning.get() == ButtonType.CANCEL)
            {
                System.out.println("Nothing got deleted");
            }
        }
        else // Hvis brugeren ikke har markeret både en playliste og en sang, får man besked
        {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a Playlist and a song!");
            alert.showAndWait();
        }
    }

    @FXML // Metode der kalder redigerings metoden når brugeren klikker på knap: Edit playlist
    void handleEditPlaylist(ActionEvent event)
    {
        // Henter den playliste der er markeret
        Playlist p = tableViewPlaylists.getSelectionModel().getSelectedItem();

        // Tjekker at der er markeret en playliste
        if (p != null)
        {
            redigerPlaylistLinje(p); // Kalder redigerings vinduet
        }
        else // Hvis brugeren ikke har valgt en playliste, får man besked
        {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please choose a Playlist to edit!");
            alert.showAndWait();
        }
    }

    @FXML // Metode der kalder redigerings metoden når brugeren klikker på knap: Edit song
    void handleEditSong(ActionEvent event)
    {
        // Henter den sang der er markeret
        Song s = tableViewSongs.getSelectionModel().getSelectedItem();

        // Tjekker at der er en markeret en sang
        if (s != null)
        {
            redigerSangLinje(s); // Kalder redigerings vinduet
        }
        else // Hvis brugeren ikke har valgt en sang, får man besked
        {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please choose a Song to edit!");
            alert.showAndWait();
        }
    }

    @FXML // Metode til at brugeren kan flytte en sang ned i ListViewet (sange i en playliste) (Knap: pil ned)
    void handleMoveSongDown(ActionEvent event)
    {
        try
        {
            // Henter den sang og den playliste som brugeren har markeret
            Song valgtSang = listViewSongsOnPlaylist.getSelectionModel().getSelectedItem();
            Playlist valgtPlayliste = tableViewPlaylists.getSelectionModel().getSelectedItem();

            // Tjekker at brugeren har markeret en sang
            if (valgtSang != null)
            {
                // Henter den plads/index som sangen står på
                int i = sangeIplayliste.indexOf(valgtSang);
                // Henter sangen under den valgte sang
                Song næsteSang = sangeIplayliste.get(i + 1);

                // De 2 sange bytter plads
                sangeIplayliste.set(i, næsteSang); // Sangen under flyttes en plads op i ListView
                sangeIplayliste.set(i + 1, valgtSang); // Den valgte sang flyttes en plads ned i ListView

                // Sætter markeringen på den valgte sang efter den er blevet flyttet
                listViewSongsOnPlaylist.getSelectionModel().select(valgtSang);

                // Den nye rækkefølge af sange gemmes
                valgtPlayliste.setSongsList(new ArrayList<>(sangeIplayliste));
            }
        }
        catch (Exception e) // Fanger fejlen hvis brugeren prøver at flytte den nederste sang ned
        {
            System.out.println("The song is already at the bottom: " + e.getMessage());
        }
    }

    @FXML // Metode til at brugeren kan flytte en sang op i ListViewet (sange i en playliste) (Knap: pil op)
    void handleMoveSongUp(ActionEvent event)
    {
        try
        {
            // Henter den sang og den playliste som brugeren har markeret
            Song valgtSang = listViewSongsOnPlaylist.getSelectionModel().getSelectedItem();
            Playlist valgtPlayliste = tableViewPlaylists.getSelectionModel().getSelectedItem();

            // Tjekker at brugeren har markeret en sang
            if (valgtSang != null)
            {
                // Henter den plads/index som sangen står på
                int i = sangeIplayliste.indexOf(valgtSang);
                // Henter sangen over den valgte sang
                Song næsteSang = sangeIplayliste.get(i - 1);

                // De 2 sange bytter plads
                sangeIplayliste.set(i, næsteSang); // Sangen under flyttes en plads ned i ListView
                sangeIplayliste.set(i - 1, valgtSang); // Den valgte sang flyttes en plads op i ListView

                // Sætter markeringen på den valgte sang efter den er blevet flyttet
                listViewSongsOnPlaylist.getSelectionModel().select(valgtSang);

                // Den nye rækkefølge af sange gemmes
                valgtPlayliste.setSongsList(new ArrayList<>(sangeIplayliste));
            }
        }
        catch (Exception e) // Fanger fejlen hvis brugeren prøver at flytte den øverste sang op
        {
            System.out.println("The song is already at the top: " + e.getMessage());
        }
    }

    // Metode til at afspille en sang
    public void playSong(Song valgtSang)
    {
        try
        {
            // Hvis der ikke er valgt en sang sker der ingen ting -> metoden stoppes ved return
            if (valgtSang == null)
            {
                return;
            }

            // Hvis det er samme sang og den er pauset, så play/afspil
            if (mediaPlayer != null && valgtSang.equals(currentSong) && mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED)
            {
                mediaPlayer.play();
                return;
            }

            // Hvis der i forvejen afspilles musik, stoppes den inden den nye valgte sang afspilles
            if (mediaPlayer != null)
            {
                mediaPlayer.stop();
            }

            // Henter fil-stien til sangen via getMusicFile()
            String filSti = new File(valgtSang.getMusicFile()).toURI().toString();

            // Opretter et Medie der indeholder musikfilen og opret Medieplayer med mediet indeni
            Media media = new Media(filSti);
            mediaPlayer = new MediaPlayer(media);

            // Hver gang der bliver oprettet en ny mediaPlayer, så mister man de tidligere volumen indstillinger
            // Volumen slider sættes derfor til det nye mediaPlayer objekt, hver gang man spiller en ny sang
            mediaPlayer.setVolume(volumenSlider.getValue() / 100); // Konverterer fra procent (0 - 100) til brøk (0.0 - 1.0)

            currentSong = valgtSang; // Gemmer den valgte sang som currentSong

            // Udskriver hvilken sang der afspilles, så brugeren kan se det
            currentlyPlayingSong.setText(valgtSang.getTitle());

            mediaPlayer.play(); // Afspiller sangen

            // Næste sang afspilles automatisk når en sang er færdig
            mediaPlayer.setOnEndOfMedia(() ->
            {
                // Henter index/placering af den sang der afspilles lige nu
                int index = currentSongList.indexOf(currentSong);

                // Tjekker at der findes en næste sang
                if (index < currentSongList.size() - 1)
                {
                    // Henter index/placering af den næste sang
                    Song næsteSang = currentSongList.get(index + 1);
                    // Afspiller næste sang
                    playSong(næsteSang);

                    // Tjekker om der afspilles fra TableView (alle sange)
                    if (currentSongList == tableViewSongs.getItems())
                    {
                        // Flytter markøren, så brugeren kan se hvilken sang der nu afspilles
                        tableViewSongs.getSelectionModel().select(næsteSang);
                        tableViewSongs.scrollTo(næsteSang);
                    }
                    else // Hvis der afspilles fra ListView (sange i playliste)
                    {
                        // Flytter markøren, så brugeren kan se hvilken sang der nu afspilles
                        listViewSongsOnPlaylist.getSelectionModel().select(næsteSang);
                        listViewSongsOnPlaylist.scrollTo(næsteSang);
                    }
                }
            });

        }
        catch (Exception e) // Hvis der sker en fejl i afspilningen, så får brugeren besked
        {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Error. The file could not be played!");
            alert.show();
        }
    }

    @FXML // Metode der holder styr på brugerens sangvalg i ListView -> nødvendig fordi TableView overruler ListView
    void handleSongSelectedFromListView(MouseEvent event)
    {
        // Henter den markerede sang i ListView
        Song valgtSang = listViewSongsOnPlaylist.getSelectionModel().getSelectedItem();

        //Tjekker at brugeren har valgt en sang
        if (valgtSang != null)
        {
            // Den valgte sang gemmes i currentSong
            currentSong = valgtSang;
            // Den liste som den valgte sang befinder sig i gemmes i currentSongList
            currentSongList = listViewSongsOnPlaylist.getItems();
            // Sørger for at markøren fjernes fra TableView, så der sikres at vi befinder os i ListView og at der ikke sker fejl
            tableViewSongs.getSelectionModel().clearSelection();
        }
    }

    @FXML // Metode der holder styr på brugerens sangvalg i tableView -> nødvendig fordi TableView overruler ListView
    void handleSongSelectedFromTableView(MouseEvent event)
    {
        // Henter den markerede sang i TableView
        Song valgtSang = tableViewSongs.getSelectionModel().getSelectedItem();

        //Tjekker at brugeren har valgt en sang
        if (valgtSang != null)
        {
            // Den valgte sang gemmes i currentSong
            currentSong = valgtSang;
            // Den liste som den valgte sang befinder sig i gemmes i currentSongList
            currentSongList = tableViewSongs.getItems();
            // Sørger for at markøren fjernes fra ListView, så der sikres at vi befinder os i ListView og at der ikke sker fejl
            listViewSongsOnPlaylist.getSelectionModel().clearSelection();
        }
    }

    @FXML // Metode der håndterer play knappen -> Afspiller den valgte sang eller genoptager, hvis den er på pause
    void handlePlaySong(MouseEvent event)
    {
        // Tjekker at der findes en mediaPlayer og at den er sat på pause
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED)
        {
            mediaPlayer.play(); // Genoptager afspilningen fra der hvor den nåede til
            return; // Metoden afbrydes her
        }

        // Tjekker at brugeren har valgt en sang
        if (currentSong != null)
        {
            playSong(currentSong); // Afspiller sangen
        }
        else // Hvis ikke brugeren har valgt en sang, får man besked
        {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a song to play!");
            alert.show();
        }
    }

    @FXML // Metode der håndtere pause knappen -> hvis man klikker på den pauses sangen
    void handlePauseSong(MouseEvent event)
    {
        // Tjekker at der findes en mediaPlayer
        if (mediaPlayer != null)
        {
            mediaPlayer.pause(); // Sætter sangen på pause
        }
        else // Hvis ikke der findes en mediaPlayer, så får brugeren besked
        {
            System.out.println("Please select a song to play or pause!");
        }
    }

    @FXML // Metode der håndtere søge knappen -> hvis man klikker på search vises en filtreret liste
    void handleSearch(ActionEvent event)
    {
        // Henter søge teksten og konvertere den til små bogstaver
        String searchTerm = searchField.getText().toLowerCase();

        // filtered() returnere en ny liste med de sange der matcher søge teksten
        ObservableList<Song> filteredSongs = sange.filtered(song ->
                // Tager hver sangs titel (alle sange) og konvertere dem til små bogstaver, hvorefter der tjekkes om søge teksten matcher sangen
                song.getTitle().toLowerCase().contains(searchTerm)
        );
        // Den nye liste med sang-matches vises i TableView (alle sange)
        tableViewSongs.setItems(filteredSongs);
    }

    @FXML // Metode til at håndtere søgefeltet -> når brugeren taster søge ord ind søges der på sange imens man taster
    void handleSearchTextField(KeyEvent event)
    {
        // Henter søge teksten
        String søgeOrd = searchField.getText();

        // Opretter en ny tom observable list til de filtrerede sange
        ObservableList<Song> filteredSongs = FXCollections.observableArrayList();

        // for-løkke der gennemgår alle sangene
        for (Song song : sange)
        {
            // Hvis sangens navn indeholder søgeteksten -> tilføjes sangen til den filtrerede liste
            if (song.getTitle().toLowerCase().contains(søgeOrd))
            {
                filteredSongs.add(song);
            }
        }

        // Den nye liste med sang-matches vises i TableView (alle sange)
        tableViewSongs.setItems(filteredSongs);
    }

    @FXML // Metode der håndtere skip knappen -> Hvis man klikker på skip afspilles næste sang
    void handleSkipSong(ActionEvent event)
    {
        try
        {
            // Henter index/placering af sangen der afspilles lige nu
            int index = currentSongList.indexOf(currentSong);

            // Tjekker at der findes en næste sang
            if (index >= 0 && index < currentSongList.size() - 1)
            {
                // Henter index/placering af den næste sang
                Song næsteSang = currentSongList.get(index + 1);
                // Afspiller næste sang
                playSong(næsteSang);

                // Tjekker om der afspilles fra TableView (alle sange)
                if (currentSongList == tableViewSongs.getItems())
                {
                    // Flytter markøren, så brugeren kan se hvilken sang der nu afspilles
                    tableViewSongs.getSelectionModel().select(næsteSang);
                    tableViewSongs.scrollTo(næsteSang);
                }
                else // Hvis der afspilles fra ListView (sange i playliste)
                {
                    // Flytter markøren, så brugeren kan se hvilken sang der nu afspilles
                    listViewSongsOnPlaylist.getSelectionModel().select(næsteSang);
                    listViewSongsOnPlaylist.scrollTo(næsteSang);
                }
            }
        }
        catch (Exception e) // Hvis der sker en fejl, så får man besked
        {
            System.out.println("Could not skip the song: " + e.getMessage());
        }
    }

    @FXML // Metode der håndtere back knappen -> Hvis man klikker på back afspilles forrige sang
    void handleBackToPreviousSong(ActionEvent event)
    {
        try
        {
            // Henter index/placering af sangen der afspilles lige nu
            int index = currentSongList.indexOf(currentSong);

            // Tjekker at der findes en forrig sang
            if (index > 0)
            {
                // Henter index/placering af den forrige sang
                Song forrigeSang = currentSongList.get(index - 1);
                // Afspiller den forrige sang
                playSong(forrigeSang);

                // Tjekker om der afspilles fra TableView (alle sange)
                if (currentSongList == tableViewSongs.getItems())
                {
                    // Flytter markøren, så brugeren kan se hvilken sang der nu afspilles
                    tableViewSongs.getSelectionModel().select(forrigeSang);
                    tableViewSongs.scrollTo(forrigeSang);
                }
                else // Hvis der afspilles fra ListView (sange i playliste)
                {
                    // Flytter markøren, så brugeren kan se hvilken sang der nu afspilles
                    listViewSongsOnPlaylist.getSelectionModel().select(forrigeSang);
                    listViewSongsOnPlaylist.scrollTo(forrigeSang);
                }
            }
        }
        catch (Exception e) // Hvis der sker en fejl, så får man besked
        {
            System.out.println("Could not skip back to the previous song: " + e.getMessage());
        }
    }

    // Metode til at redigere en Playliste ved at åbne modalt dialogvindue med data i
    private void redigerPlaylistLinje(Playlist p)
    {
        // Opretter vinduet som en dialog med et tekstfelt med data
        Dialog<ButtonType> dialogvindue = new Dialog();
        dialogvindue.setTitle("Edit Playlist");
        dialogvindue.setHeaderText("Edit Playlist");
        dialogvindue.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField name = new TextField();
        name.setPromptText("Enter name");

        // Opretter en VBox med tekst feltet
        VBox box = new VBox(name);
        // Indsætter VBox i dialogen
        dialogvindue.getDialogPane().setContent(box);

        // Sætter data fra playliste-objektet ind i name-feltet
        name.setText(p.getName());

        // Her afsluttes dialogen med at man kan trykke på OK
        Optional<ButtonType> knap = dialogvindue.showAndWait();

        // Hvis man trykker OK gemmes data fra feltet og tabellen opdateres
        if (knap.isPresent() && knap.get() == ButtonType.OK)
        {
            p.setName(name.getText());
            tableViewPlaylists.refresh();
            tableViewPlaylists.sort();
        }
    }

    // Metode til at redigere en sang ved at åbne modalt dialogvindue med data i
    private void redigerSangLinje(Song s)
    {
        // Opretter vinduet som en dialog med tekstfelter med data
        Dialog<ButtonType> dialogvindue = new Dialog();
        dialogvindue.setTitle("Edit Song");
        dialogvindue.setHeaderText("Please enter information about the song");
        dialogvindue.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField title = new TextField();
        title.setPromptText("Enter title");
        TextField artist = new TextField();
        artist.setPromptText("Enter artist");
        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("Rock", "Pop", "Julemusik", "Elektronisk", "Hip-Hop", "Klassisk");
        TextField time = new TextField();
        time.setPromptText("Enter time");
        TextField fileField = new TextField();
        fileField.setPromptText("Enter filename");
        fileField.setEditable(false); // Gør at brugeren ikke kan taste i feltet

        // Opretter en file chooser knap
        Button selectFileButton = new Button("Select File");

        // Sætter action på knappen til file chooser
        selectFileButton.setOnAction(e ->
        {
            FileChooser fileChooser = new FileChooser(); // Opretter en FileChooser med info tekst og krav til filtyper
            fileChooser.setTitle("Select a File");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Music File", "*.mp3", "*.wav"));

            // Åbner FileChooser hvori brugeren kan vælge en musikfil
            File valgtFil = fileChooser.showOpenDialog(dialogvindue.getDialogPane().getScene().getWindow());

            // Hvis der er valgt en fil, så udfyldes tekstfeltet med filstien, så brugeren kan se at man har valgt en fil
            if (valgtFil != null)
            {
                fileField.setText(valgtFil.getAbsolutePath());
            }
        });

        // Opretter en VBox med alle elemeterne og 10 pixels mellemrum
        VBox box = new VBox(10, title, artist, categoryBox, time, fileField, selectFileButton); //10 pixels mellemrum mellem hver tekst felt
        dialogvindue.getDialogPane().setContent(box);

        // Sæt data i felterne fra sang-objektet
        title.setText(s.getTitle());
        artist.setText(s.getArtist());
        categoryBox.setValue(s.getCategory());
        time.setText(s.getTime());
        fileField.setText(s.getMusicFile());

        // Her afsluttes dialogen med at man kan trykke på OK
        Optional<ButtonType> knap = dialogvindue.showAndWait();

        // Hvis man trykker OK gemmes data fra felterne og tabellen opdateres
        if (knap.isPresent() && knap.get() == ButtonType.OK)
        {
            s.setTitle(title.getText());
            s.setArtist(artist.getText());
            s.setCategory(categoryBox.getValue());
            s.setTime(time.getText());
            s.setMusicFile(s.getMusicFile());
            tableViewSongs.refresh();
            tableViewSongs.sort();
            listViewSongsOnPlaylist.refresh();
        }
    }

    // Metode der gemmer en liste af Playlist-objekter i filen playlists.txt
    private void skrivPlaylisteObjekter(ObservableList<Playlist> playlists) throws IOException
    {
        // Opretter en binær fil kaldet playlists.txt
        FileOutputStream fileOutputStream = new FileOutputStream("playlists.txt");
        // Opretter en ObjectOutputStream, der kan gemme objekter ned på en fil
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

        // I filen starter objectOutputStream.writeInt med at skrive hvor mange playlister der findes i heltal (gør det lettere at læse filen)
        objectOutputStream.writeInt(playlists.size());

        // for-løkke der kører alle playlister igennem
        for (Playlist p : playlists)
            objectOutputStream.writeObject(p); // Skriver playlisten i filen

        objectOutputStream.flush();
        objectOutputStream.close(); // Sikre at alle data er gemt ved at lukke strømmen
    }

    // Metode der gemmer en liste af Song-objekter i filen songs.txt
    private void skrivSongsObjekter(ObservableList<Song> songs) throws IOException
    {
        // Opretter en binær fil kaldet songs.txt
        FileOutputStream fileOutputStream = new FileOutputStream("songs.txt");
        // Opretter en ObjectOutputStream, der kan gemme objekter ned på en fil
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

        // I filen starter objectOutputStream.writeInt med at skrive hvor mange sange der findes i heltal (gør det lettere at læse filen)
        objectOutputStream.writeInt(songs.size());

        // for-løkke der kører alle sange igennem
        for (Song s : songs)
            objectOutputStream.writeObject(s);

        objectOutputStream.flush();
        objectOutputStream.close(); // Sikre at alle data er gemt ved at lukke strømmen
    }

    // Metode der bruges til at gemme data når brugeren lukker programmet
    public void gemData() throws IOException
    {
        skrivPlaylisteObjekter(playlister); // Gemmer Playlist-objekter i filen playlists.txt
        skrivSongsObjekter(sange); // Gemmer Song-objekter i filen songs.txt
    }

    // Metode der indlæser gemte Playlister fra filen playlists.txt
    private ObservableList<Playlist> læsPlaylistObjekter() throws IOException, ClassNotFoundException
    {
        // Opretter en tom ObservableList, der senere skal indeholde alle de gemte Playliste objekter fra filen
        ObservableList<Playlist> liste = FXCollections.observableArrayList();
        // Åbner filen playlists.txt så den kan læses
        FileInputStream fileInputStream = new FileInputStream("playlists.txt");
        // Opretter ObjectInputStream der kan læse objekter i en fil
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

        // Først læses heltallet der angiver hvor mange Playlist objekter filen indeholder
        int antal = objectInputStream.readInt();
        // for-løkken kører alle Playlist objekterne igennem
        for (int i = 0; i < antal; ++i)
        {
            Playlist p = (Playlist) objectInputStream.readObject(); // Læser objektet
            liste.add(p); // Tilføjer objektet til ObservableListen
        }

        objectInputStream.close(); // lukker strømmen for at ungå resource leaks
        return (ObservableList<Playlist>) liste; // returnere listen, så den kan bruges i TableView (Playlists)
    }

    // Metode der indlæser gemte sange fra filen songs.txt
    private ObservableList<Song> læsSangObjekter() throws IOException, ClassNotFoundException
    {
        // Opretter en tom ObservableList, der senere skal indeholde alle de gemte Song objekter fra filen
        ObservableList<Song> liste = FXCollections.observableArrayList();
        // Åbner filen songs.txt så den kan læses
        FileInputStream fileInputStream = new FileInputStream("songs.txt");
        // Opretter ObjectInputStream der kan læse objekter i en fil
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

        // Først læses heltallet der angiver hvor mange Song objekter filen indeholder
        int antal = objectInputStream.readInt();
        // for-løkken kører alle Song objekterne igennem
        for (int i = 0; i < antal; ++i)
        {
            Song s = (Song)objectInputStream.readObject(); // Læser objektet
            liste.add(s); // Tilføjer objektet til ObservableListen
        }

        objectInputStream.close(); // lukker strømmen for at ungå resource leaks
        return (ObservableList<Song>) liste; // returnere listen, så den kan bruges i TableView (Songs)
    }
}