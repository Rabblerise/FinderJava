package com.example.Utils;

import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

public class FileInfoCell extends TableCell<FileInfo, HBox> {

    @Override
    protected void updateItem(HBox item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
            setText(null);
            return;
        }
        if (item instanceof HBox) {
            setGraphic(item);
            setText(null);
        } else {
            setText(item.toString());
            setGraphic(null);
        }
    }
}