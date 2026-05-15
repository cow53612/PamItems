package io.github.cow53612.pamItems.event;

import dev.lone.itemsadder.api.CustomStack;
import io.github.cow53612.pamItems.manager.CooldownManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import java.util.Collection;

import static io.github.cow53612.pamItems.constant.CoolDown.CLICK_COOLDOWN;
import static io.github.cow53612.pamItems.constant.Performance.*;

public class HyperionEventListener implements Activatable {

    @Override
    public void onRightClickWithCustomItem(CustomStack playerHoldItem, PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (playerHoldItem.getId().equals("hyperion") && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.HAND) {
            //マナが0かの確認処理未実装
            if (CooldownManager.hasCooldown("hyperion_cooldown_" + player.getName())) {
                if (CooldownManager.getCooldown("hyperion_cooldown_" + player.getName()) > System.currentTimeMillis()) {
                    return;
                }
            }
            CooldownManager.addCooldown("hyperion_cooldown_" + player.getName(), System.currentTimeMillis() + CLICK_COOLDOWN);

            //マナ減少処理未実装

            Location teleportLocation = getTargetLocation(player);

            teleportLocation.setPitch(player.getLocation().getPitch());
            teleportLocation.setYaw(player.getLocation().getYaw());
            teleportLocation.setX(teleportLocation.getX() + 0.5);
            teleportLocation.setZ(teleportLocation.getZ() + 0.5);

            player.teleport(teleportLocation);

            Collection<Entity> targetEntities = player.getWorld().getNearbyEntities(player.getLocation(), HYPERION_EXPLOSION_RANGE, HYPERION_EXPLOSION_RANGE, HYPERION_EXPLOSION_RANGE);
            player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation(), 10, 0.0, 0.0, 0.0, 6);
            for (Entity targetEntity : targetEntities) {
                if (targetEntity instanceof LivingEntity damagedEntity && !(targetEntity instanceof Player)) {

                    if (damagedEntity instanceof Villager) continue;
                    if (damagedEntity instanceof Tameable) {
                        if (((Tameable)damagedEntity).isTamed()) continue;
                    }

                    if (damagedEntity instanceof Boss || damagedEntity.getScoreboardTags().contains("boss")) {
                        damagedEntity.damage(HYPERION_BOSS_DAMAGE, player);
                    } else {
                        damagedEntity.damage(HYPERION_DAMAGE, player);
                    }
                }
            }

            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, HYPERION_ABSORPTION_DURATION, HYPERION_ABSORPTION_AMPLIFIER, true));
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, HYPERION_RESISTANCE_DURATION, HYPERION_RESISTANCE_AMPLIFIER, true));
            player.getWorld().playSound(teleportLocation, Sound.ENTITY_GENERIC_EXPLODE, (float) 0.7, 1);
        }
    }

    private Location getTargetLocation(Player player) {
        BlockIterator it = new BlockIterator(player.getEyeLocation(), 0, HYPERION_TELEPORT_DISTANCE);

        Block preBlock = it.next();
        Block block;

        while (it.hasNext()) {
            block = it.next();

            if (block.getType() != Material.AIR
                    && block.getType() != Material.VOID_AIR
                    && block.getType() != Material.CAVE_AIR
                    && block.getType() != Material.WATER
                    && block.getType() != Material.LAVA) {
                return preBlock.getLocation();
            } else if (!it.hasNext()) {
                return block.getLocation();
            }

            preBlock = block;
        }

        block = preBlock;

        return block.getLocation();
    }

}
