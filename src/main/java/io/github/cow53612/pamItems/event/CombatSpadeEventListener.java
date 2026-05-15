package io.github.cow53612.pamItems.event;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import static io.github.cow53612.pamItems.constant.Performance.COMBAT_SPADE_ADDTIONAL_DAMAGE;

public class CombatSpadeEventListener implements Activatable{

    @Override
    public void onDamagedByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player attackingPlayer &&
                event.getEntity() instanceof LivingEntity damagedEntity) {

            if (attackingPlayer.getEquipment() == null) {
                return;
            }
            ItemStack holdingItem = attackingPlayer.getEquipment().getItemInMainHand();

            if (CustomStack.byItemStack(holdingItem) == null) {
                return;
            }

            if (CustomStack.byItemStack(holdingItem).getId().equals("combat_spade")) {
                int additionalDamage = 0;

                for (ItemStack armorPiece : damagedEntity.getEquipment().getArmorContents()) {
                    if (!armorPiece.getType().isAir()) {
                        additionalDamage += COMBAT_SPADE_ADDTIONAL_DAMAGE;
                    }
                }

                if (additionalDamage != 0) {
                    attackingPlayer.playSound(attackingPlayer.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1, 1);
                    attackingPlayer.sendMessage("コンバットスペードは追加で" + additionalDamage + "ダメージを与えました！");
                }
                event.setDamage(event.getDamage() + additionalDamage);
            }
        }
    }

}
