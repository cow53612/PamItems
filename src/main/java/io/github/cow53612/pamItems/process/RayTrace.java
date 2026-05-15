package io.github.cow53612.pamItems.process;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class RayTrace {

    public static void traceParticleToEntity(World world, LivingEntity startEntity, LivingEntity targetEntity, Particle particle) {
        Location startLoc = startEntity.getEyeLocation();
        Location endLoc = targetEntity.getEyeLocation();
        int count;

        Vector traceVector = new Vector(
                endLoc.getX() - startLoc.getX(),
                endLoc.getY() - startLoc.getY(),
                endLoc.getZ() - startLoc.getZ());
        count = (int) (traceVector.length() / 0.1) + 1;
        traceVector = traceVector.multiply((1.0 / count));

        for (int i = 1; i <= count; i++) {
            world.spawnParticle(particle, startLoc, 1);
            startLoc = startLoc.add(traceVector);
        }
    }

    public static void traceParticle(World world, LivingEntity livingEntity, double length, Particle particle) {
        Location startLoc = livingEntity.getEyeLocation();
        int count;

        Vector traceVector = startLoc.getDirection();

        count = (int) (traceVector.length() / 0.2) + 1;
        traceVector = traceVector.multiply((1.0 / count));

        for (int i = 1; i <= (double) count * length; i++) {
            world.spawnParticle(particle, startLoc, 1, 0, 0, 0, 0, null, false);
            startLoc = startLoc.add(traceVector);
        }
    }

    public static List<LivingEntity> traceEntity(World world, LivingEntity livingEntity, double length) {
        Location startLoc = livingEntity.getEyeLocation();
        int count;
        List<LivingEntity> entities = new ArrayList<>();

        Vector traceVector = startLoc.getDirection();

        count = (int) (traceVector.length() / 0.1) + 1;
        traceVector = traceVector.multiply((1.0 / count));

        for (int i = 1; i <= (double) count * length; i++) {
            for (Entity entity : world.getNearbyEntities(startLoc, 0.5, 0.5, 0.5)) {
                if (entity instanceof LivingEntity) {
                    entities.add((LivingEntity) entity);
                }
            }

            startLoc = startLoc.add(traceVector);
        }

        return entities;
    }

}
