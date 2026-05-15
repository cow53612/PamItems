package io.github.cow53612.pamItems.event;

import dev.aurelium.auraskills.api.user.SkillsUser;
import dev.lone.itemsadder.api.CustomStack;
import io.github.cow53612.pamItems.PamItems;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

import static io.github.cow53612.pamItems.constant.CosumeMana.ENDER_BREAKER_CONSUME_MANA_CHANCE;
import static io.github.cow53612.pamItems.constant.CosumeMana.ENDER_BREAKER_MANA;

public class EnderBreakerEventListener implements Activatable {

    Random ran = new Random(System.currentTimeMillis());

    @Override
    public void onBreakWithCustomItem(CustomStack playerHoldItem, BlockBreakEvent event) {
        Player breaker = event.getPlayer();
        SkillsUser skillsUser = PamItems.auraSkills.getUser(breaker.getUniqueId());
        Block brokenBlock = event.getBlock();

        if (playerHoldItem.getId().equals("ender_breaker")) {
            if (brokenBlock.getType() != Material.END_STONE) {
                event.setCancelled(true);
                breaker.sendMessage("§cエンダーブレイカーはエンドストーン以外のブロックを掘れません！");
                return;
            }
            if (skillsUser.getMana() < ENDER_BREAKER_MANA) {
                breaker.sendMessage("§cマナが足りません！");
                return;
            }

            int roll = ran.nextInt(100);
            if (roll < ENDER_BREAKER_CONSUME_MANA_CHANCE) {
                skillsUser.setMana(skillsUser.getMana() - ENDER_BREAKER_MANA);
            }

            breaker.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 20, 49, true));
        }
    }
}
