package com.brandon3055.draconicevolution.entity;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.MinecraftForge;

import java.util.*;

/**
 * Created by brandon3055 on 21/10/2015.
 */
public class DragonChunkLoader implements LoadingCallback {
    public static DragonChunkLoader instance;
    public static Map<EntityChaosGuardian, Ticket> ticketList = new HashMap<>();
    public static Map<EntityChaosGuardian, ArrayList<ChunkPos>> chunkList = new HashMap<>();
    public static boolean hasReportedIssue = false;

    public static void init() {
        instance = new DragonChunkLoader();
        MinecraftForge.EVENT_BUS.register(instance);
        ForgeChunkManager.setForcedChunkLoadingCallback(DraconicEvolution.instance, instance);
    }

    public static void updateLoaded(EntityChaosGuardian guardian) {
        Ticket ticket;

        //Calculate the chunks to be loaded
        ArrayList<ChunkPos> dragonChunks = new ArrayList<>();
        for (int xx = guardian.chunkCoordX - 2; xx <= guardian.chunkCoordX + 2; xx++) {
            for (int zz = guardian.chunkCoordZ - 2; zz <= guardian.chunkCoordZ + 2; zz++) {
                dragonChunks.add(new ChunkPos(xx, zz));
            }
        }

        //Check if the chunks are already loadsed
        if (chunkList.containsKey(guardian) && dragonChunks.hashCode() == chunkList.get(guardian).hashCode()) {
            return;
        }

        //Release the current ticket and get a new one
        if (ticketList.containsKey(guardian)) {
            ticket = ticketList.get(guardian);
            ForgeChunkManager.releaseTicket(ticket);
        }
        ticket = ForgeChunkManager.requestTicket(DraconicEvolution.instance, guardian.worldObj, ForgeChunkManager.Type.ENTITY);

        if (ticket != null) {
            ticket.bindEntity(guardian);
            ticket.setChunkListDepth(25);
            ticketList.put(guardian, ticket);

        }
        else {
            if (!hasReportedIssue) {
                LogHelper.error("##########################################################################################");
                LogHelper.error("Could not get ticket for dragon");
                LogHelper.error("Fore some reason forge has denied DE's request for a loader ticket for the chaos guardian");
                LogHelper.error("This means the chaos guardian may not behave as indented");
                LogHelper.error("This error will not show again.");
                LogHelper.error("##########################################################################################");
                hasReportedIssue = true;
            }
            return;
        }

        //Force load the chunks
        for (ChunkPos pos : dragonChunks) {
            ForgeChunkManager.forceChunk(ticket, pos);
        }

        chunkList.put(guardian, dragonChunks);
    }

    public static void stopLoading(EntityChaosGuardian guardian) {
        if (!ticketList.containsKey(guardian)) return;
        ForgeChunkManager.releaseTicket(ticketList.get(guardian));
        ticketList.remove(guardian);
    }

    @Override
    public void ticketsLoaded(List<Ticket> tickets, World world) {
        if (!tickets.isEmpty()) {
            for (Ticket ticket : tickets) {
                ForgeChunkManager.releaseTicket(ticket);
            }
        }
    }
}
