package com.example.Controllers;

import com.example.Utils.FileInfo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;


public class PrimaryController {

    @FXML protected Pane mainPane;
    @FXML protected HBox mainContainer, navContainer;
    @FXML protected MenuBar menuBar;
    @FXML protected TabPane tabPanel;
    @FXML protected TreeView <String> fastAccess; 
    @FXML protected AnchorPane tablePanel;
    @FXML protected TableView<FileInfo> tableView;
    @FXML protected TextField directory, search;
    @FXML protected Button moveBack, moveForward, back;
    @FXML protected MenuItem menuFile, menuEdit, menuHelp;

    private static PrimaryController instance;

    public TableView<FileInfo> getTableView() {
        return tableView;
    }
    @FXML
    public void initialize(){

        navContainer.prefWidthProperty().bind(mainPane.widthProperty());
        mainContainer.prefWidthProperty().bind(mainPane.widthProperty());
        menuBar.prefWidthProperty().bind(mainPane.widthProperty());
        tabPanel.prefWidthProperty().bind(mainContainer.widthProperty());
        fastAccess.prefWidthProperty().bind(mainContainer.widthProperty());
        tablePanel.prefWidthProperty().bind(tabPanel.widthProperty());
        tableView.prefWidthProperty().bind(tabPanel.widthProperty());
        directory.prefWidthProperty().bind(navContainer.widthProperty());
        search.prefWidthProperty().bind(navContainer.widthProperty());

        navContainer.prefHeightProperty().bind(mainPane.heightProperty());
        mainContainer.prefHeightProperty().bind(mainPane.heightProperty());
        menuBar.prefHeightProperty().bind(mainPane.heightProperty());
        tabPanel.prefHeightProperty().bind(mainContainer.heightProperty());
        fastAccess.prefHeightProperty().bind(mainContainer.heightProperty());
        tablePanel.prefHeightProperty().bind(tabPanel.heightProperty());
        tableView.prefHeightProperty().bind(tabPanel.heightProperty());

        instance = this;

        tabPanel.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            DataProcessor.getProcessor().setTabIndex(tabPanel.getSelectionModel().getSelectedIndex());
        });
    }
    public static void setSizeProperty(TabPane tab, AnchorPane anchorPane, TableView<FileInfo> tableView) {
        anchorPane.prefWidthProperty().bind(tab.prefWidthProperty());
        tableView.prefWidthProperty().bind(anchorPane.widthProperty());
    }

    public static PrimaryController getController() {
        return instance;
    }
}
