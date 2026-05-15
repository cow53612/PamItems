package io.github.cow53612.pamItems.event;

import dev.aurelium.auraskills.api.user.SkillsUser;
import dev.lone.itemsadder.api.CustomStack;
import io.github.cow53612.pamItems.PamItems;
import io.github.cow53612.pamItems.manager.CooldownManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

import java.util.Collection;

import static io.github.cow53612.pamItems.constant.CoolDown.GIANT_SWORD_COOLDOWN;
import static io.github.cow53612.pamItems.constant.CosumeMana.GIANT_SWORD_MANA;
import static io.github.cow53612.pamItems.constant.Performance.GIANT_SWORD_ATTACK_RANGE;
import static io.github.cow53612.pamItems.constant.Performance.GIANT_SWORD_DAMAGE;

public class GiantSwordEventListener implements Activatable {

    private final PamItems pamItems;

    public GiantSwordEventListener(PamItems pamItems) {
        this.pamItems = pamItems;
    }

    @Override
    public void onRightClickWithCustomItem(CustomStack playerHoldItem, PlayerInteractEvent event) {
        Player player = event.getPlayer();
        SkillsUser skillsUser = PamItems.auraSkills.getUser(player.getUniqueId());

        if (playerHoldItem.getId().equals("giant_sword")) {
            if (skillsUser.getMana() < GIANT_SWORD_MANA) {
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.5f);
                player.sendMessage("§cマナが足りません！");
                return;
            }

            if (CooldownManager.hasCooldown("giant_sword_pre_cooldown_" + player.getName())) {
                if (CooldownManager.getCooldown("giant_sword_pre_cooldown_" + player.getName()) > System.currentTimeMillis()) {
                    return;
                }
            }

            CooldownManager.addCooldown("giant_sword_pre_cooldown_" + player.getName(), System.currentTimeMillis() + 100);

            if (CooldownManager.hasCooldown("giant_sword_cooldown_" + player.getName())) {
                if (CooldownManager.getCooldown("giant_sword_cooldown_" + player.getName()) > System.currentTimeMillis()) {
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.5f);
                    player.sendMessage("§cこのアビリティはあと" + (((CooldownManager.getCooldown("giant_sword_cooldown_" + player.getName()) - System.currentTimeMillis()) / 1000) + 1) + "秒間クールダウン中です！");
                    return;
                }
            }

            skillsUser.setMana(skillsUser.getMana() - GIANT_SWORD_MANA);
            
            player.setCollidable(false);

            Location targetGroundLocation, summoningLocation, preSummoningLocation;
            targetGroundLocation = getGroundLocation(getLookingLocation(player));
            summoningLocation = getGroundLocation(getLookingLocation(player));
            preSummoningLocation = getGroundLocation(getLookingLocation(player));

            summoningLocation.setX(targetGroundLocation.getX() - 1);
            summoningLocation.setZ(targetGroundLocation.getZ() - 4);

            preSummoningLocation.setY(targetGroundLocation.getY() + 10);

            Giant giant = (Giant) player.getWorld().spawnEntity(summoningLocation, EntityType.GIANT);

            giant.setInvulnerable(true);
            giant.setCollidable(false);
            giant.setGravity(false);
            giant.customName(Component.text("Grumm"));
            giant.setCustomNameVisible(false);

            EntityEquipment equipment = giant.getEquipment();
            if (equipment == null) return;
            equipment.setItem(EquipmentSlot.HAND, ItemStack.of(Material.IRON_SWORD));

            new BukkitRunnable() {
                @Override
                public void run() {
                    giant.remove();
                    player.setCollidable(true);
                }
            }.runTaskLater(pamItems, 200);

            Collection<Entity> targetEntities = player.getWorld().getNearbyEntities(targetGroundLocation, GIANT_SWORD_ATTACK_RANGE, GIANT_SWORD_ATTACK_RANGE, GIANT_SWORD_ATTACK_RANGE);

            for (Entity targetEntity : targetEntities) {
                if (targetEntity instanceof LivingEntity damagedEntity && !(targetEntity instanceof Player)) {

                    if (damagedEntity instanceof Villager || damagedEntity instanceof Giant) continue;
                    if (damagedEntity instanceof Tameable) {
                        if (((Tameable)damagedEntity).isTamed()) continue;
                    }
                    damagedEntity.damage(GIANT_SWORD_DAMAGE, player);
                }
            }
            player.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, targetGroundLocation, 5, 0, 0, 0);
            player.getWorld().playSound(targetGroundLocation, Sound.BLOCK_ANVIL_PLACE, 1f, 0.5f);

            CooldownManager.addCooldown("giant_sword_cooldown_" + player.getName(), System.currentTimeMillis() + GIANT_SWORD_COOLDOWN);
        }
    }

    @EventHandler
    public void onGiantSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Giant) {
            event.getEntity().setInvisible(true);
        }
    }

    private Location getLookingLocation(Player player) {
        BlockIterator it = new BlockIterator(player.getEyeLocation(), 0, 5);

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

    private Location getGroundLocation(Location location) {
        location.setPitch(90);
        BlockIterator it = new BlockIterator(location, 0, 100);

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
