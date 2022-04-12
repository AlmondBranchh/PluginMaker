package com.gmail.visualbukkit.tutorial;

import com.gmail.visualbukkit.VisualBukkitApp;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.InputStream;

public class Examples {

    public static void launch(String exampleFolder, String exampleName){
        Stage stage = new Stage();
        InputStream input = VisualBukkitApp.class.getClassLoader().getResourceAsStream("examples/"+exampleFolder+"/"+exampleName);
        Image image = new Image(input);

        ImageView imageView = new ImageView(image);


        HBox hBox = new HBox(imageView);
        Scene scene = new Scene(hBox, imageView.getFitWidth(), imageView.getFitHeight());
        stage.setScene(scene);
        stage.show();
    }
}
