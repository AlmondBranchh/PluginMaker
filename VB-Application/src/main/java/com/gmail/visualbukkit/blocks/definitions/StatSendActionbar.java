package com.gmail.visualbukkit.blocks.definitions;

import com.gmail.visualbukkit.blocks.ClassInfo;
import com.gmail.visualbukkit.blocks.Statement;
import com.gmail.visualbukkit.blocks.parameters.ExpressionParameter;

public class StatSendActionbar extends Statement {

    public StatSendActionbar() {
        super("stat-send-actionbar", "Send Actionbar", "Player", "Sends an actionbar message to a player");
    }

    @Override
    public Block createBlock() {
        return new Block(this, new ExpressionParameter("Player", ClassInfo.of("org.bukkit.entity.Player")), new ExpressionParameter("Message", ClassInfo.STRING)) {
            @Override
            public String toJava() {
                return arg(0) + ".spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, net.md_5.bungee.api.chat.TextComponent.fromLegacyText(" + arg(1) + "));";
            }
        };
    }
}
