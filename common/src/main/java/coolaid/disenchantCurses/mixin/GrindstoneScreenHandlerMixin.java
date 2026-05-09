package coolaid.disenchantCurses.mixin;

import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
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

        ItemEnchantments component = EnchantmentHelper.getEnchantmentsForCrafting(output);
        boolean changed = false;

        // Checks if enchant is a curse
        for (var entry : component.entrySet()) {
            if (entry.getKey().is(EnchantmentTags.CURSE)) {
                changed = true;
            }
        }

        if (changed) {
            ItemEnchantments.Mutable builder = new ItemEnchantments.Mutable(component);
            builder.removeIf(holder -> holder.is(EnchantmentTags.CURSE));

            ItemEnchantments newComponent = builder.toImmutable();
            EnchantmentHelper.setEnchantments(output, newComponent);

            // Strip enchants -> unenchanted book
            ItemEnchantments finalComponent = EnchantmentHelper.getEnchantmentsForCrafting(output);
            if (output.is(Items.ENCHANTED_BOOK) && finalComponent.isEmpty()) {
                ItemStack plainBook = new ItemStack(Items.BOOK, output.getCount());
                outputSlot.set(plainBook);
            } else {
                outputSlot.set(output);
            }
        }
    }
}