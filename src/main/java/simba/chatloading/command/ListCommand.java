/* SPDX-License-Identifier: AGPL-3.0 WITH SimbaMC Proxy and SimbaMC Exceptions */
package simba.chatloading.command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import simba.chatloading.config.BindData;

import java.util.Map;
import java.util.UUID;

import static simba.chatloading.config.BindData.BindInstance;

public class ListCommand {
    public static int ListExecute(CommandContext<CommandSourceStack> context) {
        for(Map.Entry<String, BindData.Tuple3<Integer, UUID, String>> entry : BindInstance.Bind_data.entrySet()) {
            context.getSource().sendSuccess(() -> Component.literal(
                    entry.getKey() + " " +
                            entry.getValue().FInt + " " +
                            entry.getValue().FUUID + " " +
                            entry.getValue().FLang
            ), false);
        }
        return 0;
    }

}
