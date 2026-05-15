package io.github.cow53612.pamItems;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class PamItemsCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (command.getName().equalsIgnoreCase("pamitems")) {
            if (args.length < 1) {
                commandSender.sendMessage("[PI] 引数が足りません。");
                return false;
            }

            if (args[0].equalsIgnoreCase("info")) {
                commandSender.sendMessage("[PI] ぱむ鯖のアイテムの機能を実装するプラグインです、バグを多く含みます");
                return true;
            }
        }
        return false;
    }
}
