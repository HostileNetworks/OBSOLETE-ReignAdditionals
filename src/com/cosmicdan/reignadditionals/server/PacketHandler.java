package com.cosmicdan.reignadditionals.server;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {
    public static final SimpleNetworkWrapper packetReq = NetworkRegistry.INSTANCE.newSimpleChannel("reignadditionals");

    public static void init() {
        // Register EntityPlayerME for updates TO clients (e.g. info for GUI overlay)
        packetReq.registerMessage(GuiInfoPacket.class, GuiInfoPacket.class, 0, Side.CLIENT);
    }
}
