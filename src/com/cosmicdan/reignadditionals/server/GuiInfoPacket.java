package com.cosmicdan.reignadditionals.server;

import com.cosmicdan.reignadditionals.client.gui.GuiGameOverlay;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class GuiInfoPacket implements IMessage, IMessageHandler<GuiInfoPacket, IMessage> {
    private int fluxStore;
    private boolean isNearlyFull;

    public GuiInfoPacket() {}
    
    public GuiInfoPacket(int fluxStore, boolean isNearlyFull) {
        this.fluxStore = fluxStore;
        this.isNearlyFull = isNearlyFull;
    }

    // deserialization (load packet into client instance)
    @Override
    public void fromBytes(ByteBuf buf) {
        NBTTagCompound packet = ByteBufUtils.readTag(buf);
        fluxStore = packet.getInteger("fluxStore");
        isNearlyFull = packet.getBoolean("isNearlyFull");
    }

    // serialization (save server data to a client-outbound packet)
    @Override
    public void toBytes(ByteBuf buf) {
        NBTTagCompound packet = new NBTTagCompound();
        packet.setInteger("fluxStore", fluxStore);
        packet.setBoolean("isNearlyFull", isNearlyFull);
        ByteBufUtils.writeTag(buf, packet);
    }

    @Override
    public IMessage onMessage(GuiInfoPacket message, MessageContext ctx) {
        if (ctx.side == Side.CLIENT) {
            // assign message.fluxStore somewhere here
            GuiGameOverlay.cachedFluxStore = message.fluxStore;
            GuiGameOverlay.isNearlyFull = message.isNearlyFull;
        }
        return null;
    }
}
