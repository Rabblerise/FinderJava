package com.example.Utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import com.example.App;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.scene.image.Image;

public class IconHandler{
    private Map<String, String> iconMappings = new HashMap<String, String>();

    public IconHandler(){
        initializeIconMappings();
    }

    public Image getFileIcon(File file) {
        String extension = FilenameUtils.getExtension(file.getName());
        String iconPath = iconMappings.getOrDefault(extension, "icons-file.png");
        return new Image(App.getResources("Icons/" + iconPath));
    }

    public Image getDirectoryIcon(File directory) {
        return new Image(App.getResources("Icons/icons-folder.png"));
    }

    private void initializeIconMappings() {
        ObjectMapper mapper = new ObjectMapper();  
        File fileObj = new File("src\\main\\java\\com\\example\\Data\\RegIcons.json");
        try{
            iconMappings = mapper.readValue(
                fileObj, new TypeReference<Map<String, String>>(){  
            });
        }catch(IOException error) {
            App.alert(error);
        }
    }
}
