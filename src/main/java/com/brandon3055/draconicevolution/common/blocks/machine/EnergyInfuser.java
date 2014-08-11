package com.brandon3055.draconicevolution.common.blocks.machine;

import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.interfaces.GuiHandler;
import com.brandon3055.draconicevolution.common.blocks.BlockContainerDE;
import com.brandon3055.draconicevolution.common.blocks.ModBlocks;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.tileentities.TileEnergyInfuser;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by Brandon on 27/06/2014.
 */
public class EnergyInfuser extends BlockContainerDE {
	public EnergyInfuser() {
		super(Material.iron);
		this.setBlockName(Strings.energyInfuserName);
		this.setCreativeTab(DraconicEvolution.tolkienTabBlocksItems);
		this.setStepSound(soundTypeStone);
		this.setHardness(1f);
		this.setResistance(200.0f);
		this.setBlockBounds(0f, 0f, 0f, 1f, 0.375f, 1f);
		ModBlocks.register(this);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon(References.RESOURCESPREFIX + "machine_side");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEnergyInfuser();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float prx, float pry, float prz) {
		if (!world.isRemote) {
			FMLNetworkHandler.openGui(player, DraconicEvolution.instance, GuiHandler.GUIID_ENERGY_INFUSER, world, x, y, z);
		}
		world.markBlockForUpdate(x, y, z);
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if (te != null && te instanceof IInventory) {
			IInventory inventory = (IInventory) te;

			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				ItemStack stack = inventory.getStackInSlot(i);

				if (stack != null) {
					float spawnX = x + world.rand.nextFloat();
					float spawnY = y + world.rand.nextFloat();
					float spawnZ = z + world.rand.nextFloat();

					EntityItem droppedItem = new EntityItem(world, spawnX, spawnY, spawnZ, stack);

					float multiplier = 0.05F;

					droppedItem.motionX = (-0.5F + world.rand.nextFloat()) * multiplier;
					droppedItem.motionY = (4 + world.rand.nextFloat()) * multiplier;
					droppedItem.motionZ = (-0.5F + world.rand.nextFloat()) * multiplier;

					world.spawnEntityInWorld(droppedItem);
				}
			}
		}

		super.breakBlock(world, x, y, z, block, meta);
	}
}
