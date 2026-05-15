package io.github.cow53612.pamItems.event;

import dev.aurelium.auraskills.api.user.SkillsUser;
import dev.lone.itemsadder.api.CustomStack;
import io.github.cow53612.pamItems.PamItems;
import io.github.cow53612.pamItems.manager.CooldownManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;

import static io.github.cow53612.pamItems.constant.CoolDown.CLICK_COOLDOWN;
import static io.github.cow53612.pamItems.constant.CosumeMana.AOTE_MANA;
import static io.github.cow53612.pamItems.constant.Performance.AOTE_SPEED_AMPLIFIER;
import static io.github.cow53612.pamItems.constant.Performance.AOTE_SPEED_DURATION;
import static io.github.cow53612.pamItems.constant.Performance.AOTE_TELEPORT_DISTANCE;

public class AspectOfTheEndEventListener implements Activatable {

    @Override
    public void onRightClickWithCustomItem(CustomStack playerHoldItem, PlayerInteractEvent event) {
        Player player = event.getPlayer();
        SkillsUser skillsUser = PamItems.auraSkills.getUser(player.getUniqueId());

        if (playerHoldItem.getId().equals("aote")) {
            if (CooldownManager.hasCooldown("aote_cooldown_" + player.getName())) {
                if (CooldownManager.getCooldown("aote_cooldown_" + player.getName()) > System.currentTimeMillis()) {
                    return;
                }
            }
            CooldownManager.addCooldown("aote_cooldown_" + player.getName(), System.currentTimeMillis() + CLICK_COOLDOWN);

            if (skillsUser.getMana() < AOTE_MANA) {
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.5f);
                player.sendMessage("§cマナが足りません！");
                return;
            }

            skillsUser.setMana(skillsUser.getMana() - AOTE_MANA);

            Location teleportLocation = getTargetLocation(player);

            teleportLocation.setPitch(player.getLocation().getPitch());
            teleportLocation.setYaw(player.getLocation().getYaw());
            teleportLocation.setX(teleportLocation.getX() + 0.5);
            teleportLocation.setZ(teleportLocation.getZ() + 0.5);

            player.teleportAsync(teleportLocation);

            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, AOTE_SPEED_DURATION, AOTE_SPEED_AMPLIFIER, true));
            player.getWorld().playSound(teleportLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        }
    }

    private Location getTargetLocation(Player player) {
        BlockIterator it = new BlockIterator(player.getEyeLocation(), 0, AOTE_TELEPORT_DISTANCE);

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
