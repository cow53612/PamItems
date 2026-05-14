package io.github.cow53612.cowmcmmo.event;

import dev.lone.itemsadder.api.CustomStack;
import io.github.cow53612.cowmcmmo.manager.CooldownManager;
import io.github.cow53612.cowmcmmo.CowMcMMO;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

import java.util.Collection;
import java.util.Objects;

public class GiantSwordEventListener implements Listener {

    private final CowMcMMO cmm;

    public GiantSwordEventListener(CowMcMMO cmm) {
        this.cmm = cmm;
    }

    @EventHandler
    public void onRightClickWithGiantSword(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack playerHoldItem;

        try {
            playerHoldItem= Objects.requireNonNull(player.getEquipment()).getItemInMainHand();

            if (CustomStack.byItemStack(playerHoldItem).getId().equals("giants_sword") && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.HAND) {
                if (CustomStack.byItemStack(player.getEquipment().getItemInOffHand()) == null) {
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.5f);
                    player.sendMessage("§cオフハンドにEnergy Orbがありません！");
                    return;
                }

                if (CustomStack.byItemStack(player.getEquipment().getItemInOffHand()).getId().equals("energy_orb")) {
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

                    ItemStack offHandItem = player.getEquipment().getItemInOffHand();
                    offHandItem.setAmount(offHandItem.getAmount() - 1);

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
                    giant.setCustomName("Grumm");
                    giant.setCustomNameVisible(false);
                    Objects.requireNonNull(giant.getEquipment()).setItemInMainHand(CustomStack.getInstance("giants_sword").getItemStack());

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            giant.remove();
                            player.setCollidable(true);
                        }
                    }.runTaskLater(cmm, 200);

                    Collection<Entity> targetEntities = player.getWorld().getNearbyEntities(targetGroundLocation, 4.0, 4.0, 4.0);

                    for (Entity targetEntity : targetEntities) {
                        if (targetEntity instanceof LivingEntity && !(targetEntity instanceof Player)) {
                            LivingEntity damagedEntity = (LivingEntity) targetEntity;

                            if (!(damagedEntity instanceof Villager || damagedEntity instanceof Giant)) {
                                if (damagedEntity instanceof Tameable) {
                                    if (!((Tameable)damagedEntity).isTamed()) {
                                        damagedEntity.damage(50, player);
                                    }
                                }  else {
                                    damagedEntity.damage(50, player);
                                }
                            }
                        }
                    }
                    player.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, targetGroundLocation, 5, 0, 0, 0);
                    player.getWorld().playSound(targetGroundLocation, Sound.BLOCK_ANVIL_PLACE, 1f, 0.5f);

                    CooldownManager.addCooldown("giant_sword_cooldown_" + player.getName(), System.currentTimeMillis() + 10000);
                } else {
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.5f);
                    player.sendMessage("§cオフハンドにEnergy Orbがありません！");
                }
            }
        } catch (NullPointerException ignored) {}
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
