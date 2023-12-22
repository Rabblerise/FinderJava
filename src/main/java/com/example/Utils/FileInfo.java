package com.example.Utils;

import java.util.Date;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class FileInfo {
    private HBox fileName;
    private Date lastModified;
    private String fileType;
    private String fileSize;
    private final ObjectProperty<HBox> hboxProperty = new SimpleObjectProperty<>();

    public FileInfo(HBox fileName, Date lastModified, String fileType, String fileSize) {
        setHboxProperty(new HBox(fileName));
        this.fileName = fileName;
        this.lastModified = lastModified;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }

    public String getFileName() {
        Object fObject = fileName.getChildren().get(1);
        if (fObject instanceof TextField){
            return ((TextField)fObject).getText();
        }
        else{
            return ((Label)fObject).getText();
        }
    }

    public TextField getTextField(){
        return (TextField)fileName.getChildren().get(1);
    }

    public ImageView getImage() {
        return ((ImageView)fileName.getChildren().get(0));
    }

    public HBox getHbox() {
        return fileName;
    }
    public void setHbox(HBox fileName) {
        this.fileName = fileName;
    }

    public ObservableValue<HBox> HBoxProperty(){
        return hboxProperty;
    }

    public String getFileSize() {
        return fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public Date getLastModified() {
        return lastModified;
    }

    private final void setHboxProperty(HBox hbox) {
        hboxProperty.set(hbox);
    }
}