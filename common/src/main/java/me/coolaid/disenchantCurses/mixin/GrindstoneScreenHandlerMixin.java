package me.coolaid.disenchantCurses.mixin;

import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GrindstoneMenu.class)
public abstract class GrindstoneScreenHandlerMixin extends AbstractContainerMenu {

    protected GrindstoneScreenHandlerMixin(MenuType<?> type, int syncId) {
        super(type, syncId);
    }

    @Inject(method = "createResult", at = @At("TAIL"))
    private void removeCursesAndGrantXp(CallbackInfo ci) {
        Slot outputSlot = this.slots.get(2);
        ItemStack output = outputSlot.getItem().copy();
        if (output.isEmpty()) return;

        if (EnchantmentHelper.hasAnyEnchantments(output)) {
            EnchantmentHelper.updateEnchantments(output, enchantments -> enchantments.removeIf(holder -> holder.is(EnchantmentTags.CURSE)));

            // Strip enchants -> unenchanted book
            if (output.is(Items.ENCHANTED_BOOK) && !EnchantmentHelper.hasAnyEnchantments(output)) {
                ItemStack plainBook = new ItemStack(Items.BOOK, output.getCount());
                outputSlot.set(plainBook);
            } else {
                outputSlot.set(output);
            }
        }
    }
}
