/* SPDX-License-Identifier: AGPL-3.0 WITH SimbaMC Proxy and SimbaMC Exceptions */
package simba.chatloading.command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import static simba.chatloading.command.CommandEvent.BIND_KEY;
import static simba.chatloading.config.BindData.BindInstance;


public class UnbindCommand {
    public static int UnbindExecute(CommandContext<CommandSourceStack> context) {
        if (BindInstance.Unbind(context.getArgument(BIND_KEY, String.class))) {
            context.getSource().sendSuccess(() -> Component.translatable("chat.chatloading.unbind.success"), false);
            return 1;
        } else {
            context.getSource().sendSuccess(() -> Component.translatable( "chat.chatloading.unbind.failed"), false);
            return 0;
        }
    }
}
