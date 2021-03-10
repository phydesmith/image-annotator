package io.javasmithy.controller;


import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private Stage stage;
    private List<Image> images;
    private Rectangle objectBoundary;

    @FXML
    private ListView<String> imageNamesListView;
    @FXML
    private ImageView imgView;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        initObjectBoundary();
    }
    private void initObjectBoundary(){
        objectBoundary = new Rectangle(0,0,100,100);
        objectBoundary.setStroke(Color.RED);
        objectBoundary.setVisible(false);
        addBoundaryDrawEventHandler(objectBoundary);
    }
    private void addBoundaryDrawEventHandler(Rectangle objectBoundary){
        this.imgView.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.isPrimaryButtonDown()) {
                    objectBoundary.setVisible(true);
                    objectBoundary.setX(mouseEvent.getX());
                    objectBoundary.setY(mouseEvent.getY());
                }
                if (mouseEvent.isDragDetect()) {
                    objectBoundary.setWidth(mouseEvent.getX() - objectBoundary.getTranslateX());
                    objectBoundary.setHeight(mouseEvent.getY() - objectBoundary.getTranslateY());
                }
                if (mouseEvent.getEventType()
                        == MouseEvent.MOUSE_RELEASED) {
                    //objectBoundary.setVisible(false);
                    System.out.println(objectBoundary.getX() + "," + objectBoundary.getY());
                }
            }
        });
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    @FXML
    private void importImageList(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png")
        );
        processImportedImages(fileChooser.showOpenMultipleDialog(this.stage));
    }
    private void processImportedImages(List<File> fileList){
        this.images = new ArrayList<Image>();
        List<String> names = new ArrayList<String>();
        for (File f : fileList) {
            images.add(new Image(f.toURI().toString()));
            names.add(f.getName());
        }
        this.imageNamesListView.setItems(FXCollections.observableList(names));
        addListViewSelectionListener();
    }
    private void addListViewSelectionListener() {
        this.imageNamesListView.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends String> observableValue, String oldValue, String newValue) -> {
                    handleListViewItemSelection();
                }
        );
    }
    @FXML
    public void handleListViewItemSelection(){
        this.imgView.setImage(this.images.get(this.imageNamesListView.getSelectionModel().getSelectedIndex()));
    }

    @FXML
    private void exit(){
        Platform.exit();
    }


}