/* SPDX-License-Identifier: AGPL-3.0 WITH SimbaMC Proxy and SimbaMC Exceptions */

package simba.chatloading.command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import simba.chatloading.config.BindData;

import java.util.UUID;

import static simba.chatloading.command.CommandEvent.BIND_KEY;
import static simba.chatloading.command.CommandEvent.LOAD_LEN;
import static simba.chatloading.command.TickTask.doForceLoad;

public class LoadCommand {

    public static int LoadExecute(CommandContext<CommandSourceStack> context) {
        String bindKey = context.getArgument(BIND_KEY, String.class);
        int loadLength = context.getArgument(LOAD_LEN, Integer.class);
        BindData.Tuple3<Integer, UUID, String> bindData = BindData.BindInstance.Bind_data.get(bindKey);
        if (bindData == null) {
            context.getSource().sendSuccess(() -> Component.literal("Bind data not found"), false);
            return 0;
        }
        boolean loadResult = BindData.BindInstance.setLength(bindKey, loadLength);
        int loadChunks = doForceLoad(context.getSource().getServer(), bindKey, bindData);
        if (loadResult) {
            context.getSource().sendSuccess(() -> Component.translatable("chat.chatloading.load.success", loadLength), false);
            return loadChunks;
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Load Internal Error"), false);
            return 0;
        }

    }

}
