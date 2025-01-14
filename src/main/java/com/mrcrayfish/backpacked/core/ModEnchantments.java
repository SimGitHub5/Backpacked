package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.enchantment.FunnellingEnchantment;
import com.mrcrayfish.backpacked.enchantment.RepairmanEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Author: MrCrayfish
 */
public class ModEnchantments
{
    public static final DeferredRegister<Enchantment> REGISTER = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Reference.MOD_ID);

    public static final RegistryObject<FunnellingEnchantment> FUNNELLING = REGISTER.register("funnelling", FunnellingEnchantment::new);
    public static final RegistryObject<RepairmanEnchantment> REPAIRMAN = REGISTER.register("repairman", RepairmanEnchantment::new);
}
