package io.github.cow53612.pamItems.event;

import dev.lone.itemsadder.api.CustomStack;
import io.github.cow53612.pamItems.manager.CooldownManager;
import io.github.cow53612.pamItems.manager.DoubleDataManager;
import io.github.cow53612.pamItems.process.RayTrace;
import net.kyori.adventure.text.Component;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Collection;

import static io.github.cow53612.pamItems.constant.Performance.TERMINATOR_ENERGY_ARROW_RANGE;
import static io.github.cow53612.pamItems.constant.Performance.TERMINATOR_SULVATION_RANGE;

public class TerminatorEventListener implements Activatable {

    @Override
    public void onShootWithCustomBow(CustomStack playerHoldItem, EntityShootBowEvent event) {
        if (event.getProjectile() instanceof Arrow arrow) {
            if (playerHoldItem.getId().equals("terminator")) {
                arrow.customName(Component.text("terminate_arrow"));
            }
        }
    }

    @EventHandler
    public void onHitWithTerminator(EntityDamageByEntityEvent event) {
        Player shooter;
        LivingEntity damagedEntity;
        Arrow arrow;

        if (event.getDamager() instanceof Arrow && event.getEntity() instanceof LivingEntity) {
            arrow = (Arrow) event.getDamager();
            damagedEntity = (LivingEntity) event.getEntity();

            if (arrow.getShooter() instanceof Player) {
                shooter = (Player) arrow.getShooter();

                if (arrow.getName().equals("terminate_arrow")) {
                    Collection<Entity> targetEntities = damagedEntity.getWorld().getNearbyEntities(arrow.getLocation(), TERMINATOR_ENERGY_ARROW_RANGE, TERMINATOR_ENERGY_ARROW_RANGE, TERMINATOR_ENERGY_ARROW_RANGE);
                    double totalDamage = 0;

                    for (Entity targetEntity : targetEntities) {
                        if (targetEntity instanceof LivingEntity targetLivingEntity && !(targetEntity instanceof Player)) {

                            if (targetLivingEntity instanceof Villager) continue;
                            if (targetLivingEntity instanceof Tameable) {
                                if (!((Tameable)targetLivingEntity).isTamed()) continue;
                            }

                            targetLivingEntity.damage(event.getDamage(), shooter);
                            totalDamage += event.getDamage();

                            shooter.getWorld().playSound(targetLivingEntity.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 2f);
                            rayTraceParticle(damagedEntity, targetLivingEntity);
                        }
                    }

                    if (DoubleDataManager.hasData("salvation_damage_" + shooter.getName())) {
                        if (DoubleDataManager.getData("salvation_damage_" + shooter.getName()) < totalDamage) {
                            DoubleDataManager.addData("salvation_damage_" + shooter.getName(), totalDamage);

                            shooter.sendMessage("サルベーションのダメージを記録: " + (int) totalDamage);
                        }
                    } else {
                        DoubleDataManager.addData("salvation_damage_" + shooter.getName(), totalDamage);

                        shooter.sendMessage("サルベーションのダメージを記録: " + (int) totalDamage);
                    }
                }
            }

        }
    }

    @Override
    public void onLeftClickWithCustomItem(CustomStack playerHoldItem, PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (playerHoldItem.getId().equals("terminator")) {
            if (CooldownManager.hasCooldown("salvation_cooldown_" + player.getName())) {
                if (CooldownManager.getCooldown("salvation_cooldown_" + player.getName()) > System.currentTimeMillis()) {
                    return;
                }
            }
            CooldownManager.addCooldown("salvation_cooldown_" + player.getName(), System.currentTimeMillis() + 100);

            //マナ確認処理未実装

            if (!DoubleDataManager.hasData("salvation_damage_" + player.getName())) {
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.5f);
                player.sendMessage("§cサルベーションのダメージは0です！");
                return;
            }

            //マナ減少処理未実装

            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1f, 0.5f);
            RayTrace.traceParticle(player.getWorld(), player, TERMINATOR_ENERGY_ARROW_RANGE, Particle.ELECTRIC_SPARK);

            for (LivingEntity livingEntity : RayTrace.traceEntity(player.getWorld(), player, TERMINATOR_SULVATION_RANGE)) {
                if (livingEntity instanceof Villager) continue;
                if (livingEntity instanceof Tameable) {
                    if (((Tameable) livingEntity).isTamed()) continue;
                }

                livingEntity.damage(DoubleDataManager.getData("salvation_damage_" + player.getName()), player);
            }

            player.sendMessage("現在のサルベーションのダメージ： " + ((int) DoubleDataManager.getData("salvation_damage_" + player.getName())) + " → " + (int) (DoubleDataManager.getData("salvation_damage_" + player.getName()) * 0.5));
            DoubleDataManager.addData("salvation_damage_" + player.getName(), DoubleDataManager.getData("salvation_damage_" + player.getName()) * 0.5);

            if ((int) DoubleDataManager.getData("salvation_damage_" + player.getName()) == 0) {
                DoubleDataManager.removeData("salvation_damage_" + player.getName());
            }
        }
    }

    private void rayTraceParticle(LivingEntity origin, LivingEntity direction) {
        RayTrace.traceParticleToEntity(origin.getWorld(), origin, direction, Particle.HAPPY_VILLAGER);
    }

}