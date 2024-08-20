/* SPDX-License-Identifier: AGPL-3.0 WITH SimbaMC Proxy and SimbaMC Exceptions */
package simba.chatloading.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class CommandEvent {

    public static final String BIND_KEY = "bindKey";
    public static final String LOAD_LEN = "loadLen";

    public static void InitialCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                literal("chatloading")
                        .then(argument( BIND_KEY,
                                StringArgumentType.string())
                                .then(literal("bind")
                                        .executes(BindCommand::BindExecute)
                                )
                                .then(literal("unbind").requires(source -> source.hasPermission(2))
                                        .executes(UnbindCommand::UnbindExecute)
                                )
                                .then(literal("load").requires(source -> source.hasPermission(2))
                                        .then(argument( LOAD_LEN, IntegerArgumentType.integer(0, 1000000))
                                            .executes(LoadCommand::LoadExecute)
                                        )
                                )
                        )
                        .then(literal("list").requires(source -> source.hasPermission(2))
                                .executes(ListCommand::ListExecute)
                        )
        );
    }
}

