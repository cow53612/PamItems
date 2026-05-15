package io.github.cow53612.pamItems.event;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class FarmerBootsEventListener implements Listener {

    @EventHandler
    public void onStepFarmlands(PlayerInteractEvent event) {
        if (event.getPlayer().getEquipment() == null) return;
        if (event.getPlayer().getEquipment().getBoots() == null) return;
        if (CustomStack.byItemStack(event.getPlayer().getEquipment().getBoots()) == null) return;
        if (event.getClickedBlock() == null) return;

        if ((CustomStack.byItemStack(event.getPlayer().getEquipment().getBoots()).getId().equals("farmer_boots") ||
                CustomStack.byItemStack(event.getPlayer().getEquipment().getBoots()).getId().equals("rancher_boots"))
                && event.getClickedBlock().getType() == Material.FARMLAND
                && event.getAction() == Action.PHYSICAL) {
            event.setCancelled(true);
        }
    }

}
