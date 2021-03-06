package com.gmail.visualbukkit.blocks.definitions;

import com.gmail.visualbukkit.blocks.ClassInfo;
import com.gmail.visualbukkit.blocks.Expression;

public class ExprGUIClickedInventoryView extends Expression {

    public ExprGUIClickedInventoryView() {
        super("expr-gui-clicked-inventory-view", "Clicked Inventory View", "GUI", "The clicked inventory view");
    }

    @Override
    public ClassInfo getReturnType() {
        return ClassInfo.of("org.bukkit.inventory.InventoryView");
    }

    @Override
    public Block createBlock() {
        return new Block(this) {
            @Override
            public void update() {
                super.update();
                checkForPluginComponent(CompGUIClickHandler.class);
            }

            @Override
            public String toJava() {
                return "event.getView()";
            }
        };
    }
}
