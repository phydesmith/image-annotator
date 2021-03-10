package io.javasmithy.controller;


import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private Stage stage;
    private List<Image> images;
    private GraphicsContext gc;
    private double orgX, orgY;
    private double[] coords;


    @FXML
    private ListView<String> imageNamesListView;
    @FXML
    private Canvas canvas;
    @FXML
    private TextField imgClassField;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.gc = this.canvas.getGraphicsContext2D();
        this.orgX = 0;
        this.orgY = 0;
        this.coords = new double[4];
        initObjectBoundary();
    }
    private void initObjectBoundary(){
        addBoundaryDrawEventHandler();
    }
    private void addBoundaryDrawEventHandler(){
        this.canvas.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                gc.setStroke(Color.RED);
                if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    gc.strokeRect(mouseEvent.getX(), mouseEvent.getY(), 0,0);
                    orgX= mouseEvent.getX();
                    orgY= mouseEvent.getY();
                }
                if (mouseEvent.isPrimaryButtonDown()) {
                    gc.clearRect(0,0,1600, 800);
                    drawSelectedImage();
                    gc.strokeRect(orgX, orgY, (mouseEvent.getX()-orgX), (mouseEvent.getY()-orgY));
                }
                if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED){
                    coords[0] = orgX;
                    coords[1] = orgY;
                    coords[2] = mouseEvent.getX();
                    coords[3] = mouseEvent.getY();
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
                    drawSelectedImage();
                }
        );
    }
    @FXML
    private void drawSelectedImage(){
        if (this.images == null) return;
        gc.drawImage(this.images.get(this.imageNamesListView.getSelectionModel().getSelectedIndex()), 0.0, 0.0);
    }

    @FXML
    private void exit(){
        Platform.exit();
    }

    @FXML
    private void exportAnnotation(){
        StringBuilder sb = new StringBuilder();
        String name = this.imageNamesListView.getSelectionModel().getSelectedItem();
        name = name.substring(0, name.length()-4);

        File file = new File("annotations/"+name+"annotations.xml");
        sb.append("<annotation>");
        sb.append("\n\t<filename>"+name+"</filename>");
        sb.append("\n\t<object>");
        sb.append("\n\t\t<name>"+this.imgClassField.getText()+"</name>");
        sb.append("\n\t\t<bndbox>");
        sb.append("\n\t\t\t<xmin>"+this.coords[0]+"</xmin>");
        sb.append("\n\t\t\t<ymin>"+this.coords[1]+"</ymin>");
        sb.append("\n\t\t\t<xmax>"+this.coords[2]+"</xmax>");
        sb.append("\n\t\t\t<ymax>"+this.coords[3]+"</ymax>");
        sb.append("\n\t\t</bndbox>");
        sb.append("\n\t</object>");
        sb.append("\n</annotation>");
        if (file != null){
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                bufferedWriter.write(sb.toString());
                System.out.println("file saved");
                bufferedWriter.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }


}