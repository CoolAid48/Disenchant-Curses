package coolaid.disenchantCurses.mixin;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
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
        int extraXp = 0;
        boolean changed = false;

        for (var entry : component.getEnchantmentEntries()) {
            if (entry.getKey().isIn(EnchantmentTags.CURSE)) {
                extraXp += 10 * entry.getIntValue();
                changed = true;
            }
        }

        if (changed) {
            ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(component);
            builder.remove(e -> e.isIn(EnchantmentTags.CURSE));
            ItemEnchantmentsComponent newComponent = builder.build();
            EnchantmentHelper.set(output, newComponent);
            outputSlot.setStack(output);
        }

        if (extraXp > 0) {
            Slot hotbarSlot = this.slots.get(30);
            PlayerInventory pinv = (PlayerInventory) hotbarSlot.inventory;
            PlayerEntity player = pinv.player;
            player.addExperience(extraXp);
        }
    }
}