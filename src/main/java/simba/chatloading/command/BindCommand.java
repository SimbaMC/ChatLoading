/* SPDX-License-Identifier: AGPL-3.0 WITH SimbaMC Proxy and SimbaMC Exceptions */
package simba.chatloading.command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.IntTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import static simba.chatloading.command.CommandEvent.BIND_KEY;
import static simba.chatloading.config.BindData.BindInstance;

public class BindCommand {

    public static int BindExecute(CommandContext<CommandSourceStack> context) {
        if (context.getSource().isPlayer()) {
            ServerPlayer player = context.getSource().getPlayer();
            String bindKey = context.getArgument(BIND_KEY, String.class);
            BindInstance.Bind(bindKey, IntTag.valueOf(0), context.getSource().getPlayer().getUUID());
            return 1;
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Cannot Execute from console"), false);
        }
        return 0;
    }

}
