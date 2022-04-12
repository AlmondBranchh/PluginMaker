package com.gmail.visualbukkit;

import com.google.common.base.Throwables;
import javafx.application.Application;

import javax.swing.*;

import static javafx.application.Application.*;

public class VisualBukkitLauncher {

    public static void main(String[] args) {
        try {
            System.setProperty("javafx.preloader", "com.gmail.visualbukkit.VisualBukkitPreloader");
            launch(VisualBukkitApp.class);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, Throwables.getStackTraceAsString(e), "Failed to launch PluginMaker", JOptionPane.ERROR_MESSAGE);
        }
    }
}
