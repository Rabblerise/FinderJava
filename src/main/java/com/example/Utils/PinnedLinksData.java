package com.example.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.example.App;
import com.example.Beans.ObservableListDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PinnedLinksData {
    private ObservableList<String> drives;
    private Map<String, ObservableList<String>> linksByDrive;
    private Map<String, ObservableList<String>> pinnedLinksByDrive;
    private ObjectMapper objectMapper = new ObjectMapper();
    PinnedLinksData pinnedLinksData;

    File file = new File("src\\main\\java\\com\\example\\Data\\PinnedLinks.json");

    public PinnedLinksData(){
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ObservableList.class, new ObservableListDeserializer());
        objectMapper.registerModule(module);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void loadPinnedLinks() throws FileNotFoundException, IOException {
        pinnedLinksByDrive = new HashMap<>();

        try {
            pinnedLinksData = objectMapper.readValue(file, PinnedLinksData.class);

            for (String drive : pinnedLinksData.getLocalDrives()) {
                List<String> links = pinnedLinksData.getLinksByDrive().get(drive);
                ObservableList<String> linksForDrive = FXCollections.observableArrayList(links);
                pinnedLinksByDrive.put(drive, linksForDrive);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public ObservableList<String> getPinnedLinksByDrive(String drive) {
        return pinnedLinksByDrive.getOrDefault(drive, FXCollections.emptyObservableList());
    }

    public void addPinnedLink(String link) {
        String drive = getDriveFromLink(link);
        ObservableList<String> linksForDrive = pinnedLinksByDrive.get(drive);
        if (linksForDrive == null) {
            linksForDrive = FXCollections.observableArrayList();
            pinnedLinksByDrive.put(drive, linksForDrive);
        }
        linksForDrive.add(link);

        try{
            savePinnedLinks();
        }
        catch(IOException error) {
            App.alert(error);
        }
    }

    private String getDriveFromLink(String link) {
        // Пример реализации: извлекаем диск из ссылки
        return link.substring(0, 2);
    }

    private void savePinnedLinks() throws FileNotFoundException, IOException {
        try {
            setDrives(FXCollections.observableArrayList(new LinkedList<>(pinnedLinksByDrive.keySet())));
            setLinksByDrive(pinnedLinksByDrive);

            objectMapper.writeValue(file, new HashMap<String, Object>(){{
                put("drives", drives);
                put("linksByDrive", linksByDrive);
            }});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ObservableList<String> getLocalDrives() {
        return drives;
    }

    public ObservableList<String> getDrives() {
        return FXCollections.observableArrayList(new LinkedList<>(pinnedLinksByDrive.keySet()));
    }

    public void setDrives(ObservableList<String> drives) {
        this.drives = drives;
    }

    public Map<String, ObservableList<String>> getLinksByDrive() {
        return linksByDrive;
    }

    public void setLinksByDrive(Map<String, ObservableList<String>> linksByDrive) {
        this.linksByDrive = linksByDrive;
    }
}
