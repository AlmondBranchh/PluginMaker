package com.gmail.visualbukkit.blocks.definitions;

import com.gmail.visualbukkit.blocks.ClassInfo;
import com.gmail.visualbukkit.blocks.Statement;
import com.gmail.visualbukkit.blocks.parameters.ExpressionParameter;

public class StatRemovePersistentData extends Statement {

    public StatRemovePersistentData() {
        super("stat-remove-persistent-data", "Remove Persistent Data", "Bukkit", "Removes persistent data");
    }

    @Override
    public Block createBlock() {
        return new Block(this, new ExpressionParameter("Target", ClassInfo.of("org.bukkit.persistence.PersistentDataHolder")), new ExpressionParameter("Key", ClassInfo.STRING)) {
            @Override
            public String toJava() {
                return arg(0) + ".getPersistentDataContainer().remove(new NamespacedKey(PluginMain.getInstance()," + arg(1) + "));";
            }
        };
    }
}
