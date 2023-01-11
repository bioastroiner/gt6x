package org.altadoon.gt6x.common;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.data.*;
import gregapi.item.IItemGT;
import gregapi.oredict.OreDictItemData;
import gregapi.oredict.OreDictMaterial;
import gregapi.oredict.OreDictMaterialStack;
import gregapi.util.OM;
import gregapi.util.ST;
import gregapi.util.UT;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import java.util.List;

import static gregapi.data.CS.*;

public class ItemMaterialDisplay extends Item implements IItemGT {
    public static final ItemMaterialDisplay INSTANCE = new ItemMaterialDisplay();
    public static final ItemStack STACK = ST.amount(1, ST.make(INSTANCE, 1, 0));
    //TODO fix
    private final IIcon icon = OP.scrapGt.mat(MT.Fe, 1).getItem().getIconFromDamage(0);

    private final String name = "gt6x.display.oredictmaterialstack";

    public static ItemStack display(OreDictMaterial mat) {
        return display(mat, 1);
    }

    public static ItemStack display(OreDictMaterial mat, long amount) {
        return display(new OreDictMaterialStack(mat, amount));
    }

    public static ItemStack display(OreDictMaterialStack OMStack) {
        ItemStack rStack = ST.copyAmountAndMeta(OMStack.mAmount, OMStack.mMaterial.mID, OM.get_(STACK));
        if (rStack == null) return null;
        NBTTagCompound tNBT = UT.NBT.makeShort("m", OMStack.mMaterial.mID);
        if (OMStack.mAmount != 0) UT.NBT.setNumber(tNBT, "a", OMStack.mAmount);
        return UT.NBT.set(rStack, tNBT);
    }

    public static void touch() {}

    protected ItemMaterialDisplay() {
        super();
        LH.add(name + ".name", "OreDictMaterialStack Display");
        GameRegistry.registerItem(this, name, MD.GAPI.mID);
        ST.hide(this);
        CS.ItemsGT.DEBUG_ITEMS.add(this);
        CS.ItemsGT.ILLEGAL_DROPS.add(this);
        CS.GarbageGT.BLACKLIST.add(this);
    }

    public OreDictMaterialStack UnDisplay(ItemStack stack) {
        NBTTagCompound NBT = stack.getTagCompound();
        long amount = 0;
        OreDictMaterial mat = MT.NULL;
        if (NBT != null) {
            amount = NBT.getLong("a");
            short matId = NBT.getShort("m");
            mat = OreDictMaterial.get(matId);
        }
        return new OreDictMaterialStack(mat, amount);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean F3_H) {
        OreDictMaterialStack mat = UnDisplay(stack);
        if (mat.mMaterial == MT.NULL) {
            list.add(LH.Chat.BLINKING_RED + "CLIENTSIDE MATERIAL IS NULL!!!");
        }

        if (mat.mAmount > 0) {
            list.add(LH.Chat.BLUE + String.format("Content: %.3f Units of %s", (double)mat.mAmount / U, mat.mMaterial.getLocal()));
        }
        list.add(LH.Chat.GREEN + "" + String.format("Density: %.3f g/cm\u2083", mat.mMaterial.mGramPerCubicCentimeter));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int aMeta) {
        return icon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        OreDictMaterialStack mat = UnDisplay(stack);
        if (renderPass == 0)
            return UT.Code.getRGBInt(mat.mMaterial.mRGBaSolid);
        else
            return 16777215;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if (stack == null) return "";
        OreDictMaterialStack mat = UnDisplay(stack);
        return mat.mMaterial.mNameInternal;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if (stack == null) return "";
        OreDictMaterialStack mat = UnDisplay(stack);
        return mat.mMaterial.getLocal();
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void getSubItems(Item aItem, CreativeTabs aTab, List list) {
        for (int i = 0; i < OreDictMaterial.MATERIAL_ARRAY.length; i++) {
            OreDictMaterial mat = OreDictMaterial.MATERIAL_ARRAY[i];
            if (mat != null) {
                ItemStack tStack = display(mat);
                if (tStack != null) list.add(tStack);
            }
        }
    }

    @Override public final Item setUnlocalizedName(String aName) {return this;}
    @Override public final String getUnlocalizedName() {return name;}
    @Override public ItemStack getContainerItem(ItemStack aStack) {
        return null;
    }
    @Override public final boolean hasContainerItem(ItemStack aStack) {
        return false;
    }
}
