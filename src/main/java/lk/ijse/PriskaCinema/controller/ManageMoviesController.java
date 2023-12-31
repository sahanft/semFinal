package lk.ijse.PriskaCinema.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import lk.ijse.PriskaCinema.db.DbConnection;
import lk.ijse.PriskaCinema.dto.ManageMoviesDto;
import lk.ijse.PriskaCinema.dto.ManageProducerDto;
import lk.ijse.PriskaCinema.dto.ManageTicketDto;
import lk.ijse.PriskaCinema.dto.ProducerDetailsDto;
import lk.ijse.PriskaCinema.model.ManageMoviesModel;
import lk.ijse.PriskaCinema.model.ManageProducerModel;
import lk.ijse.PriskaCinema.model.ManageTicketModel;
import lk.ijse.PriskaCinema.tm.MovieTm;
import lk.ijse.PriskaCinema.tm.ProducerTm;
import lk.ijse.PriskaCinema.tm.TicketTm;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ManageMoviesController {
    public AnchorPane movieroot;
    public TableView movie_tm;
    public TableColumn id_tm;
    public TableColumn name_tm;
    public TableColumn genre_tm;
    public TableColumn duration_tm;
    public TableColumn time_tm;
    public TextField id_txt;
    public TextField name_txt;
    public TextField genre_txt;
    public TextField duration_txt;
    public TextField time_txt;
    public TableColumn proid_tm;
    public TextField proid_txt;
    public ComboBox cmb_producer;

    private ManageMoviesModel manageMoviesModel = new ManageMoviesModel();

    public void initialize() throws IOException, SQLException {
        setCellValueFactory();
        clearField();
        loadAllMovie();
        loadslider();
        loadCmb();
        tableListener();

    }

    private void loadAllMovie() {

        ObservableList<MovieTm> obList = FXCollections.observableArrayList();

        try {
            ArrayList<ManageMoviesDto> dtoList = (ArrayList<ManageMoviesDto>) manageMoviesModel.loadAllmovie();

            for (ManageMoviesDto dto : dtoList) {
                obList.add(
                        new MovieTm(
                                dto.getId_txt(),
                                dto.getName_txt(),
                                dto.getGenre_txt(),
                                dto.getDuration_txt(),
                                dto.getTime_txt()


                        ));
            }

            movie_tm.setItems(obList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    private void loadCmb() throws SQLException {
        ObservableList<Object> objects = FXCollections.observableArrayList();
        ManageProducerModel.loadAllproducer().forEach(element -> objects.add(element.getProducerid_txt()));
        cmb_producer.setItems(objects);

    }

    private void clearField() {
        id_txt.setText("");
        name_txt.setText("");
        genre_txt.setText("");
        duration_txt.setText("");
    }

    public void next_onaction(ActionEvent actionEvent) throws IOException {

        movieroot.getChildren().clear();
        movieroot.getChildren().add(FXMLLoader.load(getClass().getResource("/view/" + "managehall.fxml")));

    }

    public void back_onaction(ActionEvent actionEvent) throws IOException {

        movieroot.getChildren().clear();
        movieroot.getChildren().add(FXMLLoader.load(getClass().getResource("/view/" + "manageproducer.fxml")));


    }

    public void add_onaction(ActionEvent actionEvent) throws SQLException {

        String selected_producerId = cmb_producer.getSelectionModel().getSelectedItem().toString();

        String movie_id = id_txt.getText();
        String name = name_txt.getText();
        String genre = genre_txt.getText();
        String duration = duration_txt.getText();
        String time = time_txt.getText();


        var dto = new ManageMoviesDto(movie_id, name, genre, duration, time);
        var dto1 = new ProducerDetailsDto(selected_producerId, movie_id);
        Connection connection = DbConnection.getInstance().getConnection();
        connection.setAutoCommit(false);
        try {
            boolean isSaved1 = ManageMoviesModel.saveMovie(dto);
            if (isSaved1) {
                boolean isSaved2 = ManageMoviesModel.saveProducerMovieDetails(dto1);
                if (isSaved2) {
                    connection.commit();
                    new Alert(Alert.AlertType.CONFIRMATION, "movie added!").show();
                } else {
                    connection.rollback();
                    new Alert(Alert.AlertType.CONFIRMATION, "movie details not added!").show();
                }
            } else {
                connection.rollback();
                new Alert(Alert.AlertType.CONFIRMATION, "movie added!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }

        loadAllmovie();
        clearField();
    }


    private void loadAllmovie() {

        ObservableList<MovieTm> obList = FXCollections.observableArrayList();

        try {
            ArrayList<ManageMoviesDto> dtoList = (ArrayList<ManageMoviesDto>) manageMoviesModel.loadAllmovie();

            for (ManageMoviesDto dto : dtoList) {
                obList.add(
                        new MovieTm(
                                dto.getId_txt(),
                                dto.getName_txt(),
                                dto.getGenre_txt(),
                                dto.getDuration_txt(),
                                dto.getTime_txt()


                        ));
            }

            movie_tm.setItems(obList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setCellValueFactory() {
        id_tm.setCellValueFactory(new PropertyValueFactory<>("id"));
        name_tm.setCellValueFactory(new PropertyValueFactory<>("name"));
        genre_tm.setCellValueFactory(new PropertyValueFactory<>("genre"));
        duration_tm.setCellValueFactory(new PropertyValueFactory<>("duration"));
        time_tm.setCellValueFactory(new PropertyValueFactory<>("time"));


    }

    private void setData(MovieTm row) {
        id_txt.setText(row.getId());
        name_txt.setText(row.getName());
        genre_txt.setText(row.getGenre());
        duration_txt.setText(row.getDuration());
        time_txt.setText(row.getTime());


    }

    public void tableListener() {
        movie_tm.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            setData((MovieTm) newValue);
        });
    }


    public void update_onaction(ActionEvent actionEvent) {


        String movie_id = id_txt.getText();
        String name = name_txt.getText();
        String genre = genre_txt.getText();
        String duration = duration_txt.getText();
        String time = time_txt.getText();




        try {

            var dto = new ManageMoviesDto(movie_id,name,genre,duration,time);
            boolean isUpdated = ManageMoviesModel.updateMovie (dto);
            if(isUpdated) {
                new Alert(Alert.AlertType.CONFIRMATION, "movie details updated").show();
                clearField();
                loadAllMovie();
            } else {
                new Alert(Alert.AlertType.ERROR, "ticket details not updated").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            clearField();
        }



    }

    public void delete_onaction(ActionEvent actionEvent) {

        String id = id_txt.getText();

//        var model = new CustomerModel();
        try {
            boolean isDeleted = ManageMoviesModel.deleteMovie(id);
            if (isDeleted) {
                new Alert(Alert.AlertType.CONFIRMATION, "movie deleted!").show();
                loadAllmovie();
                clearField();

            } else {
                new Alert(Alert.AlertType.CONFIRMATION, "movie not deleted!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }

    }

    public AnchorPane testingAnhcor8;

  /*  public void initialize() throws IOException {
        loadslider();
    }*/

    private void loadslider() throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/autoimageslider.fxml"));
        this.testingAnhcor8.getChildren();
        this.testingAnhcor8.getChildren().add(root);
    }


}
