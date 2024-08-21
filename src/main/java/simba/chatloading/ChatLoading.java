/* SPDX-License-Identifier: AGPL-3.0 WITH SimbaMC Proxy and SimbaMC Exceptions */
package simba.chatloading;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simba.chatloading.command.CommandEvent;
import simba.chatloading.command.TickTask;
import simba.chatloading.config.BindData;
import simba.chatloading.config.Config;

@Mod("chatloading")
public class ChatLoading {

	public static final String MODID = "CHAT_LOADING";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static Config config;

	public ChatLoading() {
		LifecycleEvent.SERVER_STARTING.register(this::onServerStarting);
		CommandRegistrationEvent.EVENT.register(this::onRegisterCommands);
		TickEvent.SERVER_POST.register(TickTask::onTick);
	}

	public void onServerStarting(MinecraftServer server) {
		// This method will be called when the server is starting
		config = Config.onStart();
		BindData.BindInstance = BindData.getServerState(server);
	}

	public void onRegisterCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registry, Commands.CommandSelection selection) {
		// This method will be called when commands are being registered
		CommandEvent.InitialCommand(dispatcher);
	}
}
