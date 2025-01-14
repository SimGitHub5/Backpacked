package com.mrcrayfish.backpacked.network.message;

import com.mrcrayfish.backpacked.network.play.ClientPlayHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class MessageUpdateBackpack implements IMessage<MessageUpdateBackpack>
{
    private int entityId;
    private ItemStack backpack;

    public MessageUpdateBackpack() {}

    public MessageUpdateBackpack(int entityId, ItemStack backpack)
    {
        this.entityId = entityId;
        this.backpack = backpack;
    }

    @Override
    public void encode(MessageUpdateBackpack message, PacketBuffer buffer)
    {
        buffer.writeInt(message.entityId);
        buffer.writeItemStack(message.backpack, true);
    }

    @Override
    public MessageUpdateBackpack decode(PacketBuffer buffer)
    {
        return new MessageUpdateBackpack(buffer.readInt(), buffer.readItem());
    }

    @Override
    public void handle(MessageUpdateBackpack message, Supplier<NetworkEvent.Context> supplier)
    {
        supplier.get().enqueueWork(() -> ClientPlayHandler.handleUpdateBackpack(message));
        supplier.get().setPacketHandled(true);
    }

    public int getEntityId()
    {
        return this.entityId;
    }

    public ItemStack getBackpack()
    {
        return this.backpack;
    }
}
