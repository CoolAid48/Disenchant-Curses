package coolaid.disenchantCurses.mixin;

import net.minecraft.core.Holder;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.world.inventory.GrindstoneMenu$4")
public abstract class GrindstoneResultSlotMixin {

    @Redirect(
            method = "getExperienceFromItem(Lnet/minecraft/world/item/ItemStack;)I",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Holder;is(Lnet/minecraft/tags/TagKey;)Z")
    )
    private boolean countCursesForExperience(Holder<Enchantment> holder, TagKey<Enchantment> tag) {
        return !EnchantmentTags.CURSE.equals(tag) && holder.is(tag);
    }
}
