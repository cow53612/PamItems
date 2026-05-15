package io.github.cow53612.pamItems.event;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public interface Activatable extends Listener {

    @EventHandler
    default void onActivate(PlayerInteractEvent event) {
        if ((event.getAction() == Action.RIGHT_CLICK_AIR
                || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                && event.getHand() == EquipmentSlot.HAND) {
            if (event.getItem() != null) {
                if (CustomStack.byItemStack(event.getItem()) != null) {
                    onRightClickWithCustomItem(CustomStack.byItemStack(event.getItem()), event);
                } else {
                    onRightClickWithItem(event.getItem(), event);
                }
            }
        }

        if ((event.getAction() == Action.LEFT_CLICK_AIR
                || event.getAction() == Action.LEFT_CLICK_BLOCK)
                && event.getHand() == EquipmentSlot.HAND) {
            if (event.getItem() != null) {
                if (CustomStack.byItemStack(event.getItem()) != null) {
                    onLeftClickWithCustomItem(CustomStack.byItemStack(event.getItem()), event);
                } else {
                    onLeftClickWithItem(event.getItem(), event);
                }
            }
        }
    }

    @EventHandler
    default void onActivate(PlayerToggleSneakEvent event) {
        if (event.isSneaking()) return;
        if (event.getPlayer().getEquipment() == null) return;

        EntityEquipment equipment = event.getPlayer().getEquipment();


    }

    @EventHandler
    default void onActivate(EntityDamageByEntityEvent event) {
        onDamagedByEntity(event);

        if (event.getDamager() instanceof LivingEntity) {
            onAttack(event);

            EntityEquipment equipment = ((LivingEntity) event.getDamager()).getEquipment();
            if (equipment != null) {
                if (CustomStack.byItemStack(equipment.getItemInMainHand()) != null) {
                    onAttackWithCustomItem(CustomStack.byItemStack(equipment.getItemInMainHand()), event);
                } else {
                    onAttackWithItem(equipment.getItemInMainHand(), event);
                }
            }
        }
    }

    @EventHandler
    default void onActivate(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;

        onKill(event);
        EntityEquipment equipment =  event.getEntity().getKiller().getEquipment();

        if (equipment != null) {
            if (CustomStack.byItemStack(equipment.getItemInMainHand()) != null) {
                onKillWithCustomItem(CustomStack.byItemStack(equipment.getItemInMainHand()), event);
            } else {
                onKillWithItem(equipment.getItemInMainHand(), event);
            }
        }
    }

    @EventHandler
    default void onActivate(BlockBreakEvent event) {
        Player player = event.getPlayer();
        EntityEquipment equipment = player.getEquipment();

        if (equipment == null) return;

        if (CustomStack.byItemStack(equipment.getItemInMainHand()) != null) {
            onBreakWithCustomItem(CustomStack.byItemStack(equipment.getItemInMainHand()), event);
        } else {
            onBreakWithItem(equipment.getItemInMainHand(), event);
        }
    }

    @EventHandler
    default void onActivate(EntityShootBowEvent event) {
        EntityEquipment equipment = event.getEntity().getEquipment();
        if (event.getEntity() instanceof Player) {
            if (equipment == null) return;

            if (CustomStack.byItemStack(equipment.getItemInMainHand()) != null) {
                onShootWithCustomBow(CustomStack.byItemStack(equipment.getItemInMainHand()), event);
            } else {
                onShootWithBow(equipment.getItemInMainHand(), event);
            }
        }
    }

    default void onRightClickWithItem(ItemStack is, PlayerInteractEvent event) {}
    default void onRightClickWithCustomItem(CustomStack cs, PlayerInteractEvent event) {}

    default void onLeftClickWithItem(ItemStack is, PlayerInteractEvent event) {}
    default void onLeftClickWithCustomItem(CustomStack cs, PlayerInteractEvent event) {}

    default void onDamagedByEntity(EntityDamageByEntityEvent event) {}

    default void onAttack(EntityDamageByEntityEvent event) {}
    default void onAttackWithItem(ItemStack is, EntityDamageByEntityEvent event) {}
    default void onAttackWithCustomItem(CustomStack cs, EntityDamageByEntityEvent event) {}

    default void onKill(EntityDeathEvent event) {}
    default void onKillWithItem(ItemStack is, EntityDeathEvent event) {}
    default void onKillWithCustomItem(CustomStack cs, EntityDeathEvent event) {}

    default void onBreakWithCustomItem(CustomStack cs, BlockBreakEvent event) {}
    default void onBreakWithItem(ItemStack is, BlockBreakEvent event) {}

    default void onShootWithCustomBow(CustomStack cs, EntityShootBowEvent event) {}
    default void onShootWithBow(ItemStack is, EntityShootBowEvent event) {}
}
