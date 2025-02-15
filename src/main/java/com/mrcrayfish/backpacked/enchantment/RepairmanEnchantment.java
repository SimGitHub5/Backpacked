package com.mrcrayfish.backpacked.enchantment;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.core.ModEnchantments;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.stream.IntStream;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class RepairmanEnchantment extends Enchantment
{
    public RepairmanEnchantment()
    {
        super(Rarity.UNCOMMON, Backpacked.ENCHANTMENT_TYPE, new EquipmentSlotType[]{});
    }

    @Override
    public boolean isTreasureOnly()
    {
        return true;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPickupExperience(PlayerXpEvent.PickupXp event)
    {
        PlayerEntity player = event.getPlayer();
        ItemStack backpack = Backpacked.getBackpackStack(player);
        if(backpack.isEmpty())
            return;

        if(EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.REPAIRMAN.get(), backpack) <= 0)
            return;

        ExperienceOrbEntity orb = event.getOrb();
        player.takeXpDelay = 2;
        player.take(orb, 1);

        BackpackInventory inventory = ((BackpackedInventoryAccess) player).getBackpackedInventory();
        IntStream.range(0, inventory.getContainerSize()).mapToObj(inventory::getItem).filter(stack -> {
            return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MENDING, stack) > 0;
        }).mapToInt(stack -> {
            if(!stack.isDamaged()) return 0;
            int repaired = Math.min((int) (orb.value * stack.getXpRepairRatio()), stack.getDamageValue());
            stack.setDamageValue(stack.getDamageValue() - repaired);
            return repaired;
        }).max().ifPresent(value -> {
            orb.value -= value / 2;
        });

        if(orb.value > 0)
        {
            player.giveExperiencePoints(orb.value);
        }
    }
}
