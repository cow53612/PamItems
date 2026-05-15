package io.github.cow53612.pamItems.event;

import dev.aurelium.auraskills.api.user.SkillsUser;
import dev.lone.itemsadder.api.CustomStack;
import io.github.cow53612.pamItems.PamItems;
import io.github.cow53612.pamItems.manager.BooleanDataManager;
import io.github.cow53612.pamItems.manager.CooldownManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.BlockIterator;

import java.util.Collection;

import static io.github.cow53612.pamItems.constant.CoolDown.*;
import static io.github.cow53612.pamItems.constant.CosumeMana.LIGHTNING_AXE_ATTACK_MANA;
import static io.github.cow53612.pamItems.constant.CosumeMana.LIGHTNING_AXE_CHARGE_MANA;
import static io.github.cow53612.pamItems.constant.Performance.*;

public class LightningAxeEventListener implements Activatable {

    @Override
    public void onRightClickWithCustomItem(CustomStack playerHoldItem, PlayerInteractEvent event) {
        Player player = event.getPlayer();
        SkillsUser skillsUser = PamItems.auraSkills.getUser(player.getUniqueId());

        if (playerHoldItem.getId().equals("lightning_axe")) {
            //charge
            if (player.isSneaking()) {
                if (skillsUser.getMana() < LIGHTNING_AXE_CHARGE_MANA) {
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.5f);
                    player.sendMessage("§cマナが足りません！");
                    return;
                }

                if (CooldownManager.hasCooldown("lightning_axe_charge_pre_cooldown_" + player.getName())) {
                    if (CooldownManager.getCooldown("lightning_axe_charge_pre_cooldown_" + player.getName()) > System.currentTimeMillis()) {
                        return;
                    }
                }
                CooldownManager.addCooldown("lightning_axe_charge_pre_cooldown_" + player.getName(), System.currentTimeMillis() + CLICK_COOLDOWN);

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

                skillsUser.setMana(skillsUser.getMana() - LIGHTNING_AXE_CHARGE_MANA);

                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.0f, 0.25f);
                player.setHealth(player.getHealth() * LIGHTNING_AXE_CHARGE_CONSUME_HEALTH_PERCENTAGE);
                CooldownManager.addCooldown("lightning_axe_charge_weakness_" + player.getName(), System.currentTimeMillis() + LIGHTNING_AXE_CHARGE_WEAKNESS_DURATION);
                BooleanDataManager.addData("lightning_axe_charge_" + player.getName(), true);

                CooldownManager.addCooldown("lightning_axe_charge_cooldown_" + player.getName(), System.currentTimeMillis() + LIGHTNING_AXE_CHARGE_COOLDOWN);

            //damage
            } else {
                if (skillsUser.getMana() < 5) {
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.5f);
                    player.sendMessage("§cマナが足りません！");
                    return;
                }

                if (CooldownManager.hasCooldown("lightning_axe_pre_cooldown_" + player.getName())) {
                    if (CooldownManager.getCooldown("lightning_axe_pre_cooldown_" + player.getName()) > System.currentTimeMillis()) {
                        return;
                    }
                }

                CooldownManager.addCooldown("lightning_axe_pre_cooldown_" + player.getName(), System.currentTimeMillis() + CLICK_COOLDOWN);

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

                skillsUser.setMana(skillsUser.getMana() - LIGHTNING_AXE_ATTACK_MANA);

                Location targetGroundLocation;
                targetGroundLocation = getGroundLocation(getLookingLocation(player));

                player.getWorld().strikeLightning(targetGroundLocation);

                Collection<Entity> targetEntities = player.getWorld().getNearbyEntities(targetGroundLocation, LIGHTNING_AXE_ATTACK_RANGE, LIGHTNING_AXE_ATTACK_RANGE, LIGHTNING_AXE_ATTACK_RANGE);

                for (Entity targetEntity : targetEntities) {
                    if (targetEntity instanceof LivingEntity damagedEntity && !(targetEntity instanceof Player)) {
                        AttributeInstance healthAttribute = damagedEntity.getAttribute(Attribute.MAX_HEALTH);
                        if (healthAttribute == null) continue;

                        if (damagedEntity instanceof Villager) continue;
                        if (damagedEntity instanceof Tameable tameableEntity) {
                            if (tameableEntity.isTamed()) continue;
                        }

                        if (BooleanDataManager.getData("lightning_axe_charge_" + player.getName())) {
                            damagedEntity.damage(LIGHTNING_AXE_ATTACK_DAMAGE * LIGHTNING_AXE_CHARGE_MULTIPLIER, player);
                        } else {
                            damagedEntity.damage(LIGHTNING_AXE_ATTACK_DAMAGE, player);
                        }
                    }
                }
                BooleanDataManager.addData("lightning_axe_charge_" + player.getName(), false);

                CooldownManager.addCooldown("lightning_axe_cooldown_" + player.getName(), System.currentTimeMillis() + LIGHTNING_AXE_ATTACK_COOLDOWN);
            }
        }
    }

    @Override
    public void onDamagedByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (BooleanDataManager.hasData("lightning_axe_charge_" + player.getName())) {
                if (BooleanDataManager.getData("lightning_axe_charge_" + player.getName())) {
                    event.setDamage(event.getDamage() * LIGHTNING_AXE_CHARGE_DAMAGED_MULTIPLIER);
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
