package com.example;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

import com.example.Controllers.DataProcessor;
/**
 * <h3>JavaFX Application.</h3>
 * The main class of the application.
 * 
 * @author <a href="mailto:Gavrilin5423@gmail.com">Author</a>
 * @version 1.0
 * @since 1.0
 * @see <a href="https://github.com/Rabblerise">GitHub</a>
*/
public class App extends Application {

    private static Scene scene;
    private final HostServices hostServices = getHostServices();
    private static App instance;
    private static boolean initialized = false;
    private DataProcessor dataProcessor;
    private Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage; 

        scene = new Scene(loadFXML("primary"));
        stage.setScene(scene);
        stage.getIcons().add(new Image(App.getResources("Icons/icons-folder.png")));
        stage.setTitle("Explorer");
        stage.show();

        synchronized(hostServices){
            initialized = true;
            hostServices.notifyAll();
        }

        instance = this;
        dataProcessor = new DataProcessor();
        dataProcessor.overwrite();
    }

    public Stage getStage(){
        return stage;
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static App getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        launch();
    }

    public HostServices getLocalHostServices() {
        return hostServices;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static String getResources(String path){
        return App.class.getResource(path).toString();
    }

    public static void alert(Exception message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        
        alert.setGraphic(null);
        Stage stage = (Stage)alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(App.getResources("Icons/error.png")));
        alert.setTitle("Ошибка");
        alert.setHeaderText(message.getClass().toString());
        alert.setContentText(message.toString());

        alert.initModality(Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }

    public static void alert(String title, String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        
        alert.setGraphic(null);
        Stage stage = (Stage)alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(App.getResources("Icons/icons-folder.png")));
        alert.setTitle("Информация");
        alert.setHeaderText(title);
        alert.setContentText(message);

        alert.initModality(Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }
    public static boolean choice(String title, String message){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle(title);
        Stage stage = (Stage)alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(App.getResources("Icons/icons-folder.png")));
        alert.setHeaderText("Подтвердите действие");
        alert.setContentText(message);
        alert.showAndWait();
        
        return alert.resultProperty().getValue().getButtonData().isDefaultButton();
    }
}