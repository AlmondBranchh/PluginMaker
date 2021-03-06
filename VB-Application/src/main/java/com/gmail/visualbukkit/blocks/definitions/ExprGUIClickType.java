package com.gmail.visualbukkit.blocks.definitions;

import com.gmail.visualbukkit.blocks.ClassInfo;
import com.gmail.visualbukkit.blocks.Expression;

public class ExprGUIClickType extends Expression {

    public ExprGUIClickType() {
        super("expr-gui-click-type", "Click Type", "GUI", "The click type");
    }

    @Override
    public ClassInfo getReturnType() {
        return ClassInfo.of("org.bukkit.event.inventory.ClickType");
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
                return "event.getClick()";
            }
        };
    }
}
