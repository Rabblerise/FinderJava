package com.example.Controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.Date;

import com.example.App;
import com.example.Utils.DataParser;
import com.example.Utils.FileInfo;
import com.example.Utils.FileInfoCell;
import com.example.Utils.IconHandler;
import com.example.Utils.PinnedLinksData;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;


public class DataProcessor {
    private List<DataParser> dParser = new ArrayList<>();
    private List<TableView<FileInfo>> tableViews = new ArrayList<>();
    private IconHandler iconHandler = new IconHandler();
    private PrimaryController pc;
    private ContextMenu contextMenuFile, contextMenuTab;
    private MenuItem fileMenuItemRename, openInNewTab, delete, addPinedItem, closeTab;

    private static DataProcessor instance;
    private PinnedLinksData pinnedLinksData = new PinnedLinksData();
    
    private Integer tabIndex = 0;

    public DataProcessor(){
        pc = PrimaryController.getController();

        contextMenuFile = new ContextMenu();
        fileMenuItemRename = new MenuItem("Переименовать");
        openInNewTab = new MenuItem("Открыть в новой вкладке");
        delete = new MenuItem("Удалить");
        addPinedItem = new MenuItem("Добавить в закладки");
        contextMenuFile.getItems().addAll(fileMenuItemRename, openInNewTab, delete, addPinedItem);

        contextMenuTab = new ContextMenu();
        closeTab  = new MenuItem("Закрыть вкладку");
        contextMenuTab.getItems().addAll(closeTab);

        dParser.add(new DataParser());
        tableViews.add(pc.tableView);

        TreeItem<String> rootItem = new TreeItem<String>("Быстрый доступ");
        pc.fastAccess.setRoot(rootItem);
        rootItem.setExpanded(true);

        try{
            pinnedLinksData.loadPinnedLinks();

            ObservableList<String> drives = pinnedLinksData.getDrives();
            TreeItem<String> tempTreeItem;
            for(String drive : drives) {
                tempTreeItem = new TreeItem<String>(drive.concat("\\"));
                tempTreeItem.setExpanded(true);
                rootItem.getChildren().add(tempTreeItem);
                for(String link : pinnedLinksData.getPinnedLinksByDrive(drive)) {
                    tempTreeItem.getChildren().add(new TreeItem<String>(link));
                }
            }
        }
        catch(IOException error) {
            App.alert(error);
        }

        pc.fastAccess.setOnMouseClicked(event -> {
            TreeItem<String> selectedItem = pc.fastAccess.getSelectionModel().getSelectedItem();
            if ((event.getClickCount() == 2 && (selectedItem != null && event.getButton() == MouseButton.PRIMARY)) && !selectedItem.getValue().equals(rootItem.getValue())) {
                try{
                    dParser.get(tabIndex).openPath(Paths.get(selectedItem.getValue()));
                    overwrite();
                }
                catch(IOException| NullPointerException error){
                    App.alert(error);
                }
            }
        });

        pc.directory.setOnAction(event ->{
            File file = new File(pc.directory.getText());
            try{
                if(file.isFile() || file.isDirectory()){
                    dParser.get(tabIndex).openPath(Paths.get(file.toString()));
                    overwrite();
                }
                else{
                    pc.directory.setText("");
                }
            }
            catch(IOException| NullPointerException error){
                App.alert(error);
            }
        });
        pc.search.setOnAction(event -> {
            String searchPattern = pc.search.getText();
            try {
                Files.walkFileTree(dParser.get(tabIndex).getPath(), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        String fileName = file.getFileName().toString();
                        if (fileName.matches(searchPattern)) {
                            dParser.get(tabIndex).openPath(file.toAbsolutePath());
                            overwrite();
                            return FileVisitResult.TERMINATE;
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException error) {
                App.alert(error);
            }
        });

        pc.moveBack.setOnAction(event ->{
            dParser.get(tabIndex).moveBackward();
            overwrite();
        });
        
        pc.moveForward.setOnAction(event -> {
            dParser.get(tabIndex).moveForward();
            overwrite();
        });

        pc.tabPanel.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                for (Tab tab : pc.tabPanel.getTabs()) {
                    closeTab.setOnAction(eventTab -> {
                        if(pc.tabPanel.getTabs().size() > 1) {
                            pc.tabPanel.getTabs().remove(tab);
                            int index = tableViews.indexOf(((AnchorPane)tab.getContent()).getChildren().get(0));
                            tableViews.remove(index);
                            dParser.remove(index);
                        }
                    });
                    tab.setContextMenu(contextMenuTab);
                }
            }
        });

        pc.back.setOnAction(event ->{
            String link = dParser.get(tabIndex).getLink().replace("\\.", "");
            try{
                dParser.get(tabIndex).openPath(Paths.get(link.replaceAll("^(.*\\\\)[^\\\\]+$", "$1").concat("\\")));
                overwrite();
            }
            catch(IOException error){
                error.printStackTrace();
            }
        });

        pc.menuFile.setOnAction(event ->{
            App.getInstance().getStage().close();
        });

        pc.menuHelp.setOnAction(event -> {
            App.alert("Об авторе", "Автор: Гаврилин И.В.\n");
        });
        

        setActions(tableViews.get(tabIndex));
        instance = this;
    }

    public void setActions(TableView<FileInfo> tableView){

        tableView.setRowFactory( tv -> {
            TableRow<FileInfo> row = new TableRow<>();
            row.setFocusTraversable(true);

            row.setOnMouseClicked(event -> {
                FileInfo rowData = row.getItem();
                if ((event.getClickCount() == 2 && (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY))) {
                    try{
                        dParser.get(tabIndex).openPath(dParser.get(tabIndex).getChangePath(rowData.getFileName()));
                        overwriteAll();
                    }
                    catch(IOException error){
                        App.alert(error);
                    }
                }
                else if(!row.isEmpty() && event.getButton() == MouseButton.SECONDARY){
                    contextMenuFile.show(row, event.getScreenX(), event.getScreenY());
                    fileMenuItemRename.setOnAction(fileMenuEvent -> {
                        HBox tempBox = rowData.getHbox();
                        TextField tempField;
                        String fileName = rowData.getFileName();
                        tempBox.getChildren().remove(1);
                        tempBox.getChildren().add(1, tempField = new TextField(fileName));
                        tempField.requestFocus();
                        tempField.setStyle("-fx-border: none; -fx-background-color: transparent;");
                        tempField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                            if(!newValue){
                                tempBox.getChildren().remove(1);
                                if(tempField.getText() != fileName){
                                    tempBox.getChildren().add(1, new Label(tempField.getText()));
                                    File tempFile = dParser.get(tabIndex).getChangePath(fileName).toFile();
                                    tempFile.renameTo(new File(dParser.get(tabIndex).getChangePath(fileName).getParent().toFile(), tempField.getText()));
                                }
                                else{
                                    tempBox.getChildren().add(1, new Label(fileName));
                                }
                                overwriteAll();
                            }
                        });
                    });

                    openInNewTab.setOnAction(fileMenuEvent -> {
                        addNewTab(dParser.get(tabIndex).getChangePath(rowData.getFileName()));
                    });
                    delete.setOnAction(fileMenuEvent -> {
                        if(App.choice("Удаление", String.format("Вы точно хотите удалить файл %s?", rowData.getFileName().toString()))){
                            dParser.get(tabIndex).getChangePath(rowData.getFileName()).toFile().delete();
                            overwriteAll();
                        }
                    });
                    addPinedItem.setOnAction(fileMenuEvent ->{
                        String link = dParser.get(tabIndex).getChangePath(rowData.getFileName()).toString();
                        pinnedLinksData.addPinnedLink(link);
                        for(TreeItem<String> name : pc.fastAccess.getRoot().getChildren()){
                            if(link.substring(0, 3).equals(name.getValue())){
                                name.getChildren().add(new TreeItem<String>(link));
                            }
                        }
                    });
                }
                else if ((event.getClickCount() == 1 && (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY))){

                }
            });

            return row ;
        });

        tableView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                FileInfo rowData = tableViews.get(tabIndex).getSelectionModel().getSelectedItem();
                if (rowData != null) {
                    pc.mainContainer.requestFocus();
                }
            }
        });

        tableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.FORWARD){
                dParser.get(tabIndex).moveForward();
                overwrite();
            }
            else if (event.getButton() == MouseButton.BACK){
                dParser.get(tabIndex).moveBackward();
                overwrite();
            }
        });
    }

    public void setTabIndex(Integer tabIndex) {
        this.tabIndex = tabIndex;
    }

    public void addNewTab(Path path){
        AnchorPane originalContent = (AnchorPane)pc.tabPanel.getTabs().get(tabIndex).getContent();
        AnchorPane copiedContent  =  new AnchorPane();
        TableView<FileInfo> originalTableView = null;
        for(Node node : originalContent.getChildren()){
            if(node instanceof TableView){
                originalTableView = (TableView<FileInfo>)node;
            }
        }
        TableView<FileInfo> copiedTableView = new TableView<>(originalTableView.getItems());
        tableViews.add(copiedTableView);
        for(TableColumn<FileInfo, ?> column : originalTableView.getColumns()){
            TableColumn<FileInfo, ?> copiedColumn = new TableColumn<>(column.getText());
            copiedTableView.getColumns().add(copiedColumn);
        }

        copiedContent.getChildren().add(copiedTableView);

        Integer tempTabIndex = tabIndex;
        tabIndex = dParser.size();
        dParser.add(new DataParser(path));
        Tab newTab = new Tab(dParser.get(tabIndex).getName());
        newTab.setContent(copiedContent);
        pc.tabPanel.getTabs().add(newTab);
        PrimaryController.setSizeProperty(pc.tabPanel, copiedContent, copiedTableView);

        setActions(copiedTableView);
        overwrite();
        tabIndex = tempTabIndex;
    }

    public void overwrite(){
        String dir = dParser.get(tabIndex).getLink();
        
        pc.directory.setText(dir);

        pc.tabPanel.getTabs().get(tabIndex).setText(dParser.get(tabIndex).getName());

        List<File> files = dParser.get(tabIndex).getDirs();
        ObservableList<FileInfo> data = FXCollections.observableArrayList();
        ImageView icon;
        Label nameBox;
        HBox hBox;

        for (File file : files) {
            boolean isDirectory = file.isDirectory();
            icon = new ImageView(isDirectory ? iconHandler.getDirectoryIcon(file) : iconHandler.getFileIcon(file));
            icon.setFitWidth(16);
            icon.setFitHeight(16);
            nameBox = new Label(file.getName()); 
            hBox = new HBox(icon, nameBox);
            data.add(new FileInfo(
                hBox,
                new Date(file.lastModified()),
                isDirectory ? "Папка" : String.format("Файл %s", FilenameUtils.getExtension(file.toString())) ,
                isDirectory ? "" : String.valueOf(file.length())
            ));
        }

        tableViews.get(tabIndex).setItems(data);

        TableColumn<FileInfo, HBox> nameColumn =  (TableColumn<FileInfo, HBox>)tableViews.get(tabIndex).getColumns().get(0);
        nameColumn.setCellFactory(column -> new FileInfoCell());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().HBoxProperty());
        tableViews.get(tabIndex).getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("lastModified"));
        tableViews.get(tabIndex).getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("fileType"));
        tableViews.get(tabIndex).getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("fileSize"));
    }
    public void overwriteAll(){
        Integer tempTabIndex = tabIndex;
        for(int i = 0; i < tableViews.size(); i++){
            tabIndex = i;
            overwrite();
        }
        tabIndex = tempTabIndex;

    }

    public static DataProcessor getProcessor() {
        return instance;
    }
}