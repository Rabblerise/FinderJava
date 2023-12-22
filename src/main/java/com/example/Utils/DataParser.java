package com.example.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.App;

import javafx.stage.FileChooser;

public class DataParser {
    private Path path = Paths.get(System.getProperty("user.home"));
    private FileChooser fileChooser = new FileChooser();
    private List<Path> history = new ArrayList<Path>(Arrays.asList(path));
    private int historyIndex = 0;

    public DataParser(Path path){
        this.path = path;
    }

    public DataParser(){}

    public void setPath(Path path) {
        this.path = path;
    }

    public void setPath(String path) {
        this.path = Paths.get(path);
    }

    public Path getPath() {
        return path;
    }
    
    public String getLink(){
        return path.toString();
    }
    public String getName(){
        Path path = this.path.getFileName();
        if(path != null){
            return path.toString();
        }
        else{
            return "root";
        }
        
    }
    
    public List<File> getDirs(){
        File fObj = path.toFile();
        return Arrays.asList(fObj.listFiles());
    }

    public File getFile(){
        return path.toFile();
    }

    public void openPath(Path path) throws IOException{
        if(path.toFile().isFile()){
            fileChooser.setTitle("Open Resource File");
            App.getInstance().getLocalHostServices().showDocument(path.toUri().toString());
        }
        else{
            this.path = path;
            if((historyIndex != history.size() - 1)){
                for (int i = historyIndex; i < history.size() ; i++) {
                    history.remove(i);
                }
            }
            else if (!history.contains(path)){
                history.add(path);
                historyIndex++;
            }
        }
    }
    
    public Path getChangePath(String obj){
        return Paths.get(getLink().concat("\\").concat(obj));
    }

    public void moveForward(){
        if(!history.isEmpty() && historyIndex != history.size() - 1){
            this.path = history.get(historyIndex + 1);
            try{
                openPath(history.get(historyIndex + 1));
            }
            catch(IOException error) {
                App.alert(error);
            }
        }
    }

    public void moveBackward(){
        if(!history.isEmpty() && historyIndex > 0){
            this.path = history.get(historyIndex -1);
            try{
                openPath(history.get(historyIndex - 1));
            }
            catch(IOException error){
                App.alert(error);
            }
            historyIndex--;
        }
    }
}
