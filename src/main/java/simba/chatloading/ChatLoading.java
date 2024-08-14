/* SPDX-License-Identifier: AGPL-3.0 WITH SimbaMC Proxy and SimbaMC Exceptions */
package simba.chatloading;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simba.chatloading.command.CommandEvent;
import simba.chatloading.config.BindData;
import simba.chatloading.config.Config;

@Mod("chatloading")
public class ChatLoading {

	public static final String MODID = "CHAT_LOADING";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static Config config;

	public ChatLoading() {
		MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);
		MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
	}

	public void onServerStarting(ServerStartedEvent event) {
		// This method will be called when the server is starting
		config = Config.onStart();
		BindData.BindInstance = BindData.getServerState(event.getServer());
	}

	public void onRegisterCommands(RegisterCommandsEvent event) {
		// This method will be called when commands are being registered
		CommandEvent.InitialCommand(event.getDispatcher());
	}
}
