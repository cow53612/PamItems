package io.github.cow53612.cowmcmmo.event;

import dev.lone.itemsadder.api.CustomStack;
import io.github.cow53612.cowmcmmo.data.BooleanDataManager;
import io.github.cow53612.cowmcmmo.manager.CooldownManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

import java.util.Collection;
import java.util.Objects;

public class LightningAxeEventListener implements Activatable {

    @Override
    public void onRightClickWithCustomItem(CustomStack cs, PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack playerHoldItem;

        try {
            playerHoldItem = Objects.requireNonNull(player.getEquipment()).getItemInMainHand();

            if (CustomStack.byItemStack(playerHoldItem).getId().equals("lightning_axe") && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getHand() == EquipmentSlot.HAND) {
                //charge
                if (player.isSneaking()) {
                    if (CustomStack.byItemStack(player.getEquipment().getItemInOffHand()) == null) {
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.5f);
                        player.sendMessage("§cオフハンドにEnergy Orbがありません！");
                        return;
                    }

                    if (CustomStack.byItemStack(player.getEquipment().getItemInOffHand()).getId().equals("energy_orb")) {
                        if (CooldownManager.hasCooldown("lightning_axe_charge_pre_cooldown_" + player.getName())) {
                            if (CooldownManager.getCooldown("lightning_axe_charge_pre_cooldown_" + player.getName()) > System.currentTimeMillis()) {
                                return;
                            }
                        }

                        CooldownManager.addCooldown("lightning_axe_charge_pre_cooldown_" + player.getName(), System.currentTimeMillis() + 100);

                        if (CooldownManager.hasCooldown("lightning_axe_charge_cooldown_" + player.getName())) {
                            if (CooldownManager.getCooldown("lightning_axe_charge_cooldown_" + player.getName()) > System.currentTimeMillis()) {
                                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.5f);
                                player.sendMessage("§cこのアビリティはあと" + (((CooldownManager.getCooldown("lightning_axe_charge_cooldown_" + player.getName()) - System.currentTimeMillis()) / 1000) + 1) + "秒間クールダウン中です！");
                                return;
                            }
                        }

                        if (BooleanDataManager.hasData("lightning_axe_charge_" + player.getName())) {
                            if (BooleanDataManager.getData("lightning_axe_charge_" + player.getName())) {
                                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.5f);
                                player.sendMessage("§c既にオーバーチャージは使用しています！");
                                return;
                            }
                        }

                        ItemStack offHandItem = player.getEquipment().getItemInOffHand();
                        offHandItem.setAmount(offHandItem.getAmount() - 1);

                        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.0f, 0.25f);
                        player.setHealth(player.getHealth() * 0.25);
                        CooldownManager.addCooldown("lightning_axe_charge_weakness_" + player.getName(), System.currentTimeMillis() + 20000);
                        BooleanDataManager.addData("lightning_axe_charge_" + player.getName(), true);

                        CooldownManager.addCooldown("lightning_axe_charge_cooldown_" + player.getName(), System.currentTimeMillis() + 30000);
                    } else {
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.5f);
                        player.sendMessage("§cオフハンドにEnergy Orbがありません！");
                    }

                //damage
                } else {
                    if (CustomStack.byItemStack(player.getEquipment().getItemInOffHand()) == null) {
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.5f);
                        player.sendMessage("§cオフハンドにEnergy Orbがありません！");
                        return;
                    }

                    if (CustomStack.byItemStack(player.getEquipment().getItemInOffHand()).getId().equals("energy_orb")) {
                        if (CooldownManager.hasCooldown("lightning_axe_pre_cooldown_" + player.getName())) {
                            if (CooldownManager.getCooldown("lightning_axe_pre_cooldown_" + player.getName()) > System.currentTimeMillis()) {
                                return;
                            }
                        }

                        CooldownManager.addCooldown("lightning_axe_pre_cooldown_" + player.getName(), System.currentTimeMillis() + 100);

                        if (CooldownManager.hasCooldown("lightning_axe_cooldown_" + player.getName())) {
                            if (CooldownManager.getCooldown("lightning_axe_cooldown_" + player.getName()) > System.currentTimeMillis()) {
                                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.5f);
                                player.sendMessage("§cこのアビリティはあと" + (((CooldownManager.getCooldown("lightning_axe_cooldown_" + player.getName()) - System.currentTimeMillis()) / 1000) + 1) + "秒間クールダウン中です！");
                                return;
                            }
                        }

                        if (CooldownManager.hasCooldown("lightning_axe_charge_weakness_" + player.getName())) {
                            if (CooldownManager.getCooldown("lightning_axe_charge_weakness_" + player.getName()) > System.currentTimeMillis()) {
                                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.5f);
                                player.sendMessage("§cこのアビリティはあと" + (((CooldownManager.getCooldown("lightning_axe_charge_weakness_" + player.getName()) - System.currentTimeMillis()) / 1000) + 1) + "秒間使用できません！");
                                return;
                            }
                        }

                        ItemStack offHandItem = player.getEquipment().getItemInOffHand();
                        offHandItem.setAmount(offHandItem.getAmount() - 1);

                        Location targetGroundLocation;
                        targetGroundLocation = getGroundLocation(getLookingLocation(player));

                        player.getWorld().strikeLightning(targetGroundLocation);

                        Collection<Entity> targetEntities = player.getWorld().getNearbyEntities(targetGroundLocation, 3.0, 3.0, 3.0);

                        for (Entity targetEntity : targetEntities) {
                            if (targetEntity instanceof LivingEntity && !(targetEntity instanceof Player)) {
                                LivingEntity damagedEntity = (LivingEntity) targetEntity;

                                if (!(damagedEntity instanceof Villager)) {
                                    if (damagedEntity instanceof Tameable) {
                                        if (!((Tameable)damagedEntity).isTamed()) {
                                            if (BooleanDataManager.hasData("lightning_axe_charge_" + player.getName())) {
                                                if (BooleanDataManager.getData("lightning_axe_charge_" + player.getName())) {
                                                    damagedEntity.damage(20 + (damagedEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * 0.002), player);
                                                } else {
                                                    damagedEntity.damage(10 + (damagedEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * 0.001), player);
                                                }
                                            } else {
                                                damagedEntity.damage(10 + (damagedEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * 0.001), player);
                                            }
                                        }
                                    } else {
                                        if (BooleanDataManager.hasData("lightning_axe_charge_" + player.getName())) {
                                            if (BooleanDataManager.getData("lightning_axe_charge_" + player.getName())) {
                                                damagedEntity.damage(20 + (damagedEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * 0.002), player);
                                            } else {
                                                damagedEntity.damage(10 + (damagedEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * 0.001), player);
                                            }
                                        } else {
                                            damagedEntity.damage(10 + (damagedEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * 0.001), player);
                                        }
                                    }
                                }
                            }
                        }
                        BooleanDataManager.addData("lightning_axe_charge_" + player.getName(), false);

                        CooldownManager.addCooldown("lightning_axe_cooldown_" + player.getName(), System.currentTimeMillis() + 20000);
                    } else {
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.5f);
                        player.sendMessage("§cオフハンドにEnergy Orbがありません！");
                    }
                }

            }
        } catch (NullPointerException ignored) {}
    }

    @Override
    public void onDamagedByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (BooleanDataManager.hasData("lightning_axe_charge_" + player.getName())) {
                if (BooleanDataManager.getData("lightning_axe_charge_" + player.getName())) {
                    event.setDamage(event.getDamage() * 4);
                }
            }
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
