package com.gmail.visualbukkit.blocks;

import com.gmail.visualbukkit.VisualBukkitApp;
import com.gmail.visualbukkit.ui.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.textfield.CustomTextField;
import org.json.JSONArray;

import java.util.*;

public class BlockSelector extends StyleableVBox {

    public ObservableList<BlockSource<?>> blocks = FXCollections.observableArrayList();
    public FilteredList<BlockSource<?>> filteredBlocks = new FilteredList<>(blocks);
    private Set<String> favPluginComponents = new HashSet<>();
    private Set<String> favStatements = new HashSet<>();
    private Set<String> favExpressions = new HashSet<>();

    public static CustomTextField searchField = new CustomTextField();
    private CheckBox pluginComponentCheckBox = new CheckBox(LanguageManager.get("check_box.plugin_components"));
    private CheckBox statementCheckBox = new CheckBox(LanguageManager.get("check_box.statements"));
    private CheckBox expressionCheckBox = new CheckBox(LanguageManager.get("check_box.expressions"));
    private CheckBox pinnedCheckBox = new CheckBox(LanguageManager.get("check_box.favorite"));


    @SuppressWarnings("unchecked")
    public BlockSelector() {

        VisualBukkitApp visualBukkitApp = new VisualBukkitApp();

        Label title = new Label(LanguageManager.get("label.block_selector"));
        title.setUnderline(true);

        ListView<BlockSource<?>> listView = new ListView<>(filteredBlocks);
        listView.prefHeightProperty().bind(heightProperty());

        pluginComponentCheckBox.setSelected(true);
        statementCheckBox.setSelected(true);
        expressionCheckBox.setSelected(true);

        searchField.setRight(new IconButton("x", null, e -> searchField.clear()));
        searchField.textProperty().addListener((o, oldValue, newValue) -> updateFiltered());
        pluginComponentCheckBox.setOnAction(e -> updateFiltered());
        statementCheckBox.setOnAction(e -> updateFiltered());
        expressionCheckBox.setOnAction(e -> updateFiltered());
        pinnedCheckBox.setOnAction(e -> updateFiltered());

        for (Pair<String, Set<String>> pair : new Pair[]{new Pair<>("favorite-plugin-components", favPluginComponents), new Pair<>("favorite-statements", favStatements), new Pair<>("favorite-expressions", favExpressions)}) {
            JSONArray json = VisualBukkitApp.getData().optJSONArray(pair.getKey());
            if (json != null) {
                for (Object obj : json) {
                    if (obj instanceof String s) {
                        pair.getValue().add(s);
                    }
                }
            }
        }

        getStyleClass().add("block-selector");
        getChildren().addAll(new StyleableVBox(title, new StyleableHBox(new Label(LanguageManager.get("label.search")), searchField), pluginComponentCheckBox, statementCheckBox, expressionCheckBox, pinnedCheckBox), new Separator(), listView);
    }

    public void setBlocks(Set<BlockDefinition> definitions) {
        blocks.clear();

        for (BlockDefinition def : definitions) {
            BlockSource<?> block = def.createSource();
            blocks.add(block);
            MenuItem favoriteItem = new MenuItem(LanguageManager.get("context_menu.favorite"));
            MenuItem unfavoriteItem = new MenuItem(LanguageManager.get("context_menu.unfavorite"));
            //MenuItem hotkeyItem = new MenuItem(LanguageManager.get("context_menu.hotkey"));
            favoriteItem.setDisable(getFavorites(block).contains(block.getBlockDefinition().getID()));
            favoriteItem.setOnAction(e -> {
                getFavorites(block).add(block.getBlockDefinition().getID());
                updateFavorite();
                updateFiltered();
                favoriteItem.setDisable(true);
            });
            unfavoriteItem.setOnAction(e -> {
                getFavorites(block).remove(block.getBlockDefinition().getID());
                updateFavorite();
                updateFiltered();
                favoriteItem.setDisable(false);
            });

            unfavoriteItem.disableProperty().bind(favoriteItem.disableProperty().not());
            block.setContextMenu(new ContextMenu(favoriteItem, unfavoriteItem));
        }
    }

    private void updateFiltered() {
        filteredBlocks.setPredicate(block ->
                (pluginComponentCheckBox.isSelected() || !(block instanceof PluginComponentSource)) &&
                (statementCheckBox.isSelected() || !(block instanceof StatementSource)) &&
                (expressionCheckBox.isSelected() || !(block instanceof ExpressionSource)) &&
                (!pinnedCheckBox.isSelected() || getFavorites(block).contains(block.getBlockDefinition().getID())) &&
                StringUtils.containsIgnoreCase(block.getBlockDefinition().getFullTitle(), searchField.getText()));
    }

    @SuppressWarnings("unchecked")
    private void updateFavorite() {
        for (Pair<String, Set<String>> pair : new Pair[]{new Pair<>("favorite-plugin-components", favPluginComponents), new Pair<>("favorite-statements", favStatements), new Pair<>("favorite-expressions", favExpressions)}) {
            VisualBukkitApp.getData().remove(pair.getKey());
            for (String block : pair.getValue()) {
                VisualBukkitApp.getData().append(pair.getKey(), block);
            }
        }
    }

    private Set<String> getFavorites(BlockSource<?> block) {
        return block instanceof PluginComponentSource ? favPluginComponents : block instanceof StatementSource ? favStatements : favExpressions;
    }

    public static void updateSearchField(String hotkeyFill){
        searchField.setText(hotkeyFill);
    }
    }

