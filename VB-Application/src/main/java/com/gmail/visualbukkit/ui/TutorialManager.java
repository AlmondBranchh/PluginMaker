package com.gmail.visualbukkit.ui;

import com.gmail.visualbukkit.tutorial.Examples;
import javafx.scene.control.Menu;

public class TutorialManager {

    public Menu CreateTutorialMenu(){
        Menu examplesMenu = new Menu("Examples");
        Menu examplesCommand = new Menu("Command");
        Menu examplesPlayerInteract = new Menu("Player Interact");


        examplesCommand.getItems().setAll(
                new ActionMenuItem("Start", e -> Examples.launch("command", "start.gif"))
        );


        examplesPlayerInteract.getItems().setAll(
                new ActionMenuItem("Start", e -> Examples.launch("playerinteract", "simple.jpg"))
        );


        examplesMenu.getItems().setAll(
                examplesCommand, examplesPlayerInteract
        );

        return new Menu("Help", null, examplesMenu);

    }
}
