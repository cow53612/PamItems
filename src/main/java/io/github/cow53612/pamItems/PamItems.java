package io.github.cow53612.pamItems;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import io.github.cow53612.pamItems.event.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class PamItems extends JavaPlugin {

    public static AuraSkillsApi auraSkills;

    @Override
    public void onEnable() {
        auraSkills = AuraSkillsApi.get();
        Objects.requireNonNull(getCommand("pamitems")).setExecutor(new PamItemsCommandExecutor());

        registerEvents();

        getLogger().info("[PamItems] プラグインを有効化しました。");
    }

    @Override
    public void onDisable() {
        getLogger().info("[PamItems] プラグインを無効化しました。");
    }

    private void registerEvents() {
        PluginManager manager = getServer().getPluginManager();

        manager.registerEvents(new AspectOfTheEndEventListener(), this);
        manager.registerEvents(new RendBowEventListener(), this);
        manager.registerEvents(new CombatSpadeEventListener(), this);
        manager.registerEvents(new EnderBreakerEventListener(), this);
        manager.registerEvents(new FarmerBootsEventListener(), this);
        manager.registerEvents(new HyperionEventListener(), this);
        manager.registerEvents(new TerminatorEventListener(), this);
        manager.registerEvents(new LightningAxeEventListener(), this);
        manager.registerEvents(new GiantSwordEventListener(this), this);
    }

}
