package com.mrcrayfish.backpacked.network.play;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.BackpackModelProperty;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import com.mrcrayfish.backpacked.inventory.ExtendedPlayerInventory;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainer;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageCustomiseBackpack;
import com.mrcrayfish.backpacked.network.message.MessageOpenBackpack;
import com.mrcrayfish.backpacked.network.message.MessagePlayerBackpack;
import com.mrcrayfish.backpacked.network.message.MessageUpdateBackpack;
import com.mrcrayfish.backpacked.util.PickpocketUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * Author: MrCrayfish
 */
public class ServerPlayHandler
{
    public static void handleCustomiseBackpack(MessageCustomiseBackpack message, ServerPlayerEntity player)
    {
        ItemStack stack = Backpacked.getBackpackStack(player);
        if(!stack.isEmpty())
        {
            CompoundNBT tag = stack.getOrCreateTag();
            tag.putString("BackpackModel", message.getModel());
            tag.putBoolean(BackpackModelProperty.SHOW_WITH_ELYTRA.getTagName(), message.isShowWithElytra());
            tag.putBoolean(BackpackModelProperty.SHOW_EFFECTS.getTagName(), message.isShowEffects());

            if(Backpacked.isCuriosLoaded())
                return;

            if(player.inventory instanceof ExtendedPlayerInventory)
            {
                ItemStack backpack = ((ExtendedPlayerInventory) player.inventory).getBackpackItems().get(0);
                if(!backpack.isEmpty() && backpack.getItem() instanceof BackpackItem)
                {
                    Network.getPlayChannel().send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new MessageUpdateBackpack(player.getId(), backpack));
                }
            }
        }
    }

    public static void handleOpenBackpack(MessageOpenBackpack message, ServerPlayerEntity player)
    {
        ItemStack backpack = Backpacked.getBackpackStack(player);
        if(backpack.getItem() instanceof BackpackItem)
        {
            ((BackpackItem) backpack.getItem()).showInventory(player);
        }
    }

    public static void handlePlayerBackpack(MessagePlayerBackpack message, ServerPlayerEntity player)
    {
        if(!Config.SERVER.pickpocketBackpacks.get())
            return;

        Entity entity = player.level.getEntity(message.getEntityId());
        if(!(entity instanceof PlayerEntity))
            return;

        PlayerEntity otherPlayer = (PlayerEntity) entity;
        if(!PickpocketUtil.canSeeBackpack(otherPlayer, player))
            return;

        ItemStack backpack = Backpacked.getBackpackStack(otherPlayer);
        if(!backpack.isEmpty())
        {
            BackpackInventory backpackInventory = ((BackpackedInventoryAccess) otherPlayer).getBackpackedInventory();
            if(backpackInventory == null)
                return;
            ITextComponent title = backpack.hasCustomHoverName() ? backpack.getHoverName() : BackpackItem.BACKPACK_TRANSLATION;
            int rows = ((BackpackItem) backpack.getItem()).getRowCount();
            NetworkHooks.openGui(player, new SimpleNamedContainerProvider((id, playerInventory, entity1) -> {
                return new BackpackContainer(id, player.inventory, backpackInventory, rows);
            }, title), buffer -> buffer.writeVarInt(rows));
            otherPlayer.displayClientMessage(new TranslationTextComponent("message.backpacked.player_opened"), true);
            player.level.playSound(player, otherPlayer.getX(), otherPlayer.getY() + 1.0, otherPlayer.getZ(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 0.75F, 1.0F);
        }
    }
}