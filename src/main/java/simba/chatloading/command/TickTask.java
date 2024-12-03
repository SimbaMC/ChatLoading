package simba.chatloading.command;

import dev.ftb.mods.ftbchunks.api.ClaimedChunk;
import dev.ftb.mods.ftbchunks.api.FTBChunksAPI;
import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import simba.chatloading.ChatLoading;
import simba.chatloading.config.BindData;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static simba.chatloading.config.BindData.BindInstance;

public class TickTask {
    private static final int LOAD_INTERVAL = 0x3FF;
    static final TicketType<ChunkPos> STATIC_LOAD = TicketType.create("chatloadings:context", Comparator.comparingLong(ChunkPos::toLong), LOAD_INTERVAL + 10);
    public static void onTick(MinecraftServer minecraftServer) {
        if ((minecraftServer.getTickCount() & LOAD_INTERVAL) == 0) { // Run loading every 1024 Ticks
            for (Map.Entry<String, BindData.Tuple3<Integer, UUID, String>> entry : BindInstance.Bind_data.entrySet()) {
                if (entry.getValue().FInt <= 0) break;
                doForceLoad(minecraftServer, entry.getKey(), entry.getValue());
            }
        }
    }

    public static int doForceLoad(MinecraftServer minecraftServer, String bindKey, BindData.Tuple3<Integer, UUID, String> bindValue) {
        UUID playerUUID = bindValue.FUUID;
        Optional<Team> teamOptional = FTBTeamsAPI.api().getManager().getTeamForPlayerID(playerUUID);
        if (teamOptional.isEmpty()) {
            ChatLoading.LOGGER.warn("Team not found" + playerUUID);
            return 0;
        }
        UUID teamUUID = teamOptional.get().getTeamId();
        AtomicInteger loaded = new AtomicInteger(0);
        FTBChunksAPI.api().getManager().getAllClaimedChunks().stream()
                .filter(cc -> cc.getTeamData().getTeam().getTeamId().equals(teamUUID))
                .filter(ClaimedChunk::isForceLoaded)
                .forEach(cc -> {
                    ServerLevel level = minecraftServer.getLevel(cc.getPos().dimension());
                    if (level != null) {
                        ChunkPos chunkPos = cc.getPos().getChunkPos();
                        level.getChunkSource().addRegionTicket(STATIC_LOAD, chunkPos, 2, chunkPos, true);
                        loaded.incrementAndGet();
                    }
                });
        BindInstance.setLength(bindKey, Math.max(0, bindValue.FInt - LOAD_INTERVAL));
        return loaded.get();
    }
}
