package org.mesdag.particlestorm.api;

import org.jetbrains.annotations.ApiStatus;
import org.redlance.dima_dencep.mods.particletsunami.ParticleTsunamiMod;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class IntAllocator {
    private final PriorityQueue<Integer> availableIds;
    private final Set<Integer> usedIds;
    private int nextId;

    public IntAllocator() {
        this.availableIds = new PriorityQueue<>();
        this.usedIds = new HashSet<>();
        this.nextId = 0;
    }

    public int allocate() {
        int id;
        if (availableIds.isEmpty()) {
            id = nextId++;
        } else {
            id = availableIds.poll();
        }
        usedIds.add(id);
        return id;
    }

    public void release(int id) {
        release(id, true);
    }

    @ApiStatus.Internal
    public void release(int id, boolean warn) {
        if (usedIds.contains(id)) {
            usedIds.remove(id);
            availableIds.offer(id);
        } else if (warn) {
            ParticleTsunamiMod.LOGGER.warn("ID {} is not currently allocated.", id);
        }
    }

    public boolean isAllocated(int id) {
        return usedIds.contains(id);
    }

    public boolean forceAllocate(int id) {
        return usedIds.add(id);
    }

    public void clear() {
        availableIds.clear();
        usedIds.clear();
        this.nextId = 0;
    }
}
