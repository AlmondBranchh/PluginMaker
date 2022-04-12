package com.gmail.visualbukkit.hotkey;

import com.gmail.visualbukkit.VisualBukkitApp;
import com.gmail.visualbukkit.blocks.*;
import com.gmail.visualbukkit.ui.IconButton;
import com.gmail.visualbukkit.ui.LanguageManager;
import com.gmail.visualbukkit.ui.StyleableHBox;
import com.gmail.visualbukkit.ui.StyleableVBox;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.textfield.CustomTextField;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class Hotkey extends StyleableVBox {


    private CustomTextField searchField = new CustomTextField();

    public ObservableList<String> blocks = FXCollections.observableArrayList();
    public FilteredList<String> filteredBlocks = new FilteredList<>(blocks);

    private CustomTextField hotkeyField = new CustomTextField();
    private CheckBox ctrlBox = new CheckBox("ctrl");
    private CheckBox shiftBox = new CheckBox("shift");
    private Text fullSelected = new Text();
    private Label status = new Label("Status:");
    private Label selected = new Label("Selected:");
    private Label text = new Label();
    private Button submit = new Button("Submit");
    private Button remove = new Button("Remove");


    public Hotkey() {

        ListView<String> listView = new ListView<>(filteredBlocks);
        listView.prefHeightProperty().bind(heightProperty());

        if (!VisualBukkitApp.getData().has("HotKey")) {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            jsonObject.put("c+true+true", "[Bukkit] Command");
            jsonArray.put(jsonObject);
            VisualBukkitApp.getData().put("HotKey", jsonArray);
        }
        JSONArray array = VisualBukkitApp.getData().getJSONArray("HotKey");
        for (int n = 0; n < array.length(); n++) {
            JSONObject jsonObject = array.getJSONObject(n);

            submit.setOnAction(e -> {
                        if (hotkeyField.getText().length() > 1) {
                            status.setText("Error: Only One Character");
                        }
                        else if (text.getText().contains("<")){status.setText("Error: Remove Hotkey First");}

                            else {
                            jsonObject.put(hotkeyField.getText(0, 1) + "+" + ctrlBox.isSelected() + "+" + shiftBox.isSelected(), text.getText());
                            updateHotkey(blocks.indexOf(fullSelected.getText()), hotkeyField.getText(0, 1) + "+" + ctrlBox.isSelected() + "+" + shiftBox.isSelected());
                            hotkeyField.clear();
                            ctrlBox.setSelected(false);
                            shiftBox.setSelected(false);
                            text.setText("");
                            status.setText("Status: Success");
                            listView.getSelectionModel().select(listView.getSelectionModel().getSelectedIndex()+1);
                            listView.getSelectionModel().select(listView.getSelectionModel().getSelectedIndex()-1);

                        }

                    });

            remove.setOnAction(e -> {
                String string = text.getText();
                String [] separatedBlock = string.split("<");
                text.setText(separatedBlock[0]);
                updateHotkey(blocks.indexOf(fullSelected.getText()), null);
                String [] separatedHotkey = string.split(">");
                List<String> separatedList = new ArrayList<String>(Arrays.asList(separatedHotkey[1].split("\\+")));
                String jsonString = separatedList.get(0);
                if (separatedList.contains("ctrl")){jsonString = jsonString+"+true";} else {jsonString = jsonString + "+false";}
                if (separatedList.contains("shift")){jsonString = jsonString+"+true";} else {jsonString = jsonString + "+false";}
                jsonObject.remove(jsonString);


            });


        }
        VisualBukkitApp.getData().put("HotKey", array);


        Label title = new Label("HotKey Selection");
        title.setUnderline(true);


        listView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            String selectedItem = listView.getSelectionModel().getSelectedItem();
            fullSelected.setText(selectedItem);
            text.setText(selectedItem);
            status.setText("Status:");

        });

        searchField.setRight(new IconButton("x", null, e -> searchField.clear()));
        searchField.textProperty().addListener((o, oldValue, newValue) -> updateFiltered());
        hotkeyField.setPrefWidth(25);


        getStyleClass().add("hotkey");
        getChildren().addAll(new StyleableVBox(title, new StyleableHBox(new Label(LanguageManager.get("label.search")), searchField)), new Separator(), new StyleableHBox(selected, text), new StyleableHBox(hotkeyField, ctrlBox, shiftBox, submit, remove), status , new Separator(), listView);
    }

    public void setBlocks(Set<BlockDefinition> definitions) {
        blocks.clear();

        JSONArray array = VisualBukkitApp.getData().getJSONArray("HotKey");
        List<String> arrayList = new ArrayList<>();
        for (int n = 0; n < array.length(); n++) {
            arrayList.add(String.valueOf(array.get(n)));
        }

        String togetherList = (StringUtils.join(arrayList, "\""));

        List<String> separatedList = new ArrayList<String>(Arrays.asList(togetherList.split("\"")));

        for (BlockDefinition def : definitions) {
            if(separatedList.contains(def.getFullTitle())){
                String [] hotkeySeparated = separatedList.get(separatedList.indexOf(def.getFullTitle())-2).split("\\+");
                String hotkeyDisplayed = hotkeySeparated[0];
                if(Objects.equals(hotkeySeparated[1], "true")) hotkeyDisplayed += "+ctrl";
                if(Objects.equals(hotkeySeparated[2], "true")) hotkeyDisplayed += "+shift";

                String block = def.getFullTitle()+"<-------->"+hotkeyDisplayed;
                blocks.add(block);
            }else {
                String block = def.getFullTitle();
                blocks.add(block);
            }
        }
        }

        private void updateFiltered () {
            filteredBlocks.setPredicate(block ->
                    StringUtils.containsIgnoreCase(block, searchField.getText()));
        }

        public void updateHotkey(int blockIndex, String hotkey) {
            if (hotkey == null) {
                blocks.set(blockIndex, text.getText());
            } else {
                String[] hotkeySeparated = hotkey.split("\\+");
                String hotkeyDisplayed = hotkeySeparated[0];
                if (Objects.equals(hotkeySeparated[1], "true")) hotkeyDisplayed += "+ctrl";
                if (Objects.equals(hotkeySeparated[2], "true")) hotkeyDisplayed += "+shift";
                blocks.set(blockIndex, blocks.get(blockIndex) + "<-------->" + hotkeyDisplayed);

            }
        }

    }



