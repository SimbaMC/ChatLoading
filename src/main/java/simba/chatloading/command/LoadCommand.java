/* SPDX-License-Identifier: AGPL-3.0 WITH SimbaMC Proxy and SimbaMC Exceptions */

package simba.chatloading.command;

import com.mojang.brigadier.context.CommandContext;
import dev.ftb.mods.ftbchunks.api.ClaimedChunk;
import dev.ftb.mods.ftbchunks.api.FTBChunksAPI;
import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import simba.chatloading.config.BindData;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static simba.chatloading.command.CommandEvent.BIND_KEY;
import static simba.chatloading.command.CommandEvent.LOAD_LEN;

public class LoadCommand {
    static final TicketType<ChunkPos> STATIC_LOAD = TicketType.create("chatae2:context", Comparator.comparingLong(ChunkPos::toLong), 100000);
    public static int LoadExecute(CommandContext<CommandSourceStack> context) {
        String bindKey = context.getArgument(BIND_KEY, String.class);
        int loadLength = context.getArgument(LOAD_LEN, Integer.class);
        BindData.Tuple3<Tag, UUID, String> bindData = BindData.BindInstance.Bind_data.get(bindKey);
        if (bindData == null) {
            context.getSource().sendSuccess(() -> Component.literal("Bind data not found"), false);
            return 0;
        }
        UUID playerUUID = bindData.FUUID;
        Optional<Team> teamOptional = FTBTeamsAPI.api().getManager().getTeamForPlayerID(playerUUID);
        if (teamOptional.isEmpty()) {
            context.getSource().sendSuccess(() -> Component.literal("Team not found"), false);
            return 0;
        }
        UUID teamUUID = teamOptional.get().getTeamId();
        TicketType<ChunkPos> Load = TicketType.create("chatae2:context", Comparator.comparingLong(ChunkPos::toLong), loadLength);
        AtomicInteger loaded = new AtomicInteger(0);
        FTBChunksAPI.api().getManager().getAllClaimedChunks().stream()
                .filter(cc -> cc.getTeamData().getTeam().getTeamId().equals(teamUUID))
                .filter(ClaimedChunk::isForceLoaded)
                .forEach(cc -> {
                    ServerLevel level = context.getSource().getServer().getLevel(cc.getPos().dimension());
                    if (level != null) {
                        ChunkPos chunkPos = cc.getPos().getChunkPos();
                        level.getChunkSource().addRegionTicket(STATIC_LOAD, chunkPos, 1, chunkPos, true);
                        loaded.incrementAndGet();
                    }
                });
        context.getSource().sendSuccess(() -> Component.translatable("chat.chatloading.load.success", loaded.get()), false);
        return loaded.get();
    }

}
