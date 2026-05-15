package io.github.cow53612.pamItems.event;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

import static io.github.cow53612.pamItems.constant.Performance.*;

public class RendBowEventListener implements Activatable{

    @Override
    public void onLeftClickWithCustomItem(CustomStack playerHoldItem, PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (playerHoldItem.getId().equals("rend_bow")) {
            Collection<Entity> targetEntities = player.getWorld().getNearbyEntities(player.getLocation(), REND_BOW_RANGE, REND_BOW_RANGE, REND_BOW_RANGE);

            for (Entity targetEntity : targetEntities) {
                if (targetEntity instanceof LivingEntity damagedEntity && !(targetEntity instanceof Player)) {
                    int arrows = damagedEntity.getArrowsInBody();

                    if (arrows != 0) {
                        AttributeInstance healthAttribute = damagedEntity.getAttribute(Attribute.MAX_HEALTH);
                        if (healthAttribute == null) {
                            continue;
                        }
                        damagedEntity.setNoDamageTicks(0);
                        damagedEntity.damage((healthAttribute.getValue() * REND_BOW_HEALTH_DAMAGE_PERCENTAGE * arrows) + (arrows * REND_BOW_DAMAGE), player);
                        player.getWorld().playSound(damagedEntity.getLocation(), Sound.ENTITY_WITHER_BREAK_BLOCK, 1, 1);
                        damagedEntity.setArrowsInBody(0);
                        player.getInventory().addItem(new ItemStack(Material.ARROW, arrows));
                    }

                }
            }
        }
    }

}
