package coolaid.disenchantCurses.mixin;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GrindstoneScreenHandler.class)
public abstract class GrindstoneScreenHandlerMixin extends ScreenHandler {

    protected GrindstoneScreenHandlerMixin(ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Inject(method = "updateResult", at = @At("TAIL"))
    private void removeCursesAndGrantXp(CallbackInfo ci) {
        Slot outputSlot = this.slots.get(2);
        ItemStack output = outputSlot.getStack().copy();
        if (output.isEmpty()) return;

        ItemEnchantmentsComponent component = EnchantmentHelper.getEnchantments(output);
        boolean changed = false;

        // Checks if enchant is a curse
        for (var entry : component.getEnchantmentEntries()) {
            if (entry.getKey().isIn(EnchantmentTags.CURSE)) {
                changed = true;
            }
        }

        if (changed) {
            ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(component);
            builder.remove(e -> e.isIn(EnchantmentTags.CURSE));
            ItemEnchantmentsComponent newComponent = builder.build();
            EnchantmentHelper.set(output, newComponent);

        // Strip enchants -> unenchanted book
            ItemEnchantmentsComponent finalComponent = EnchantmentHelper.getEnchantments(output);
            if (output.isOf(Items.ENCHANTED_BOOK) && finalComponent.getEnchantmentEntries().isEmpty()) {
                ItemStack plainBook = new ItemStack(Items.BOOK, output.getCount());
                outputSlot.setStack(plainBook);
            } else {
                outputSlot.setStack(output);
            }
        }
    }
}