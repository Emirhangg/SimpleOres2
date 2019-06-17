package mod.alexndr.simpleores;

import com.google.common.base.Preconditions;
import mod.alexndr.simpleores.config.ConfigHelper;
import mod.alexndr.simpleores.config.ConfigHolder;
import mod.alexndr.simpleores.config.SimpleOresConfig;
import mod.alexndr.simpleores.init.ModTabGroups;
import net.minecraft.block.Block;
import net.minecraft.block.OreBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;

import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@EventBusSubscriber(modid = SimpleOres.MODID, bus = MOD)
public final class ModEventSubscriber 
{
	private static final Logger LOGGER = LogManager.getLogger(SimpleOres.MODID + " Mod Event Subscriber");

	/**
	 * This method will be called by Forge when it is time for the mod to register its Blocks.
	 * This method will always be called before the Item registry method.
	 */
	@SubscribeEvent
	public static void onRegisterBlocks(final RegistryEvent.Register<Block> event)
	{
		// Register all your blocks inside this registerAll call
        event.getRegistry().registerAll(
                setup(new OreBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance( 1.7F, 3.0F)), "copper_ore"),
				setup(new OreBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance( 3.0F, 3.0F)), "tin_ore"),
				setup(new OreBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance( 4.0F, 3.0F)), "adamantium_ore"),
				setup(new OreBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance( 5.0F, 3.0F)), "mythril_ore"),
				setup(new OreBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance( 7.0F, 3.0F)), "onyx_ore"),
				setup(new Block(Block.Properties.create(Material.IRON, MaterialColor.ORANGE_TERRACOTTA)
									.hardnessAndResistance(3.0F, 6.0F).sound( SoundType.METAL)), "copper_block"),
				setup(new Block(Block.Properties.create(Material.IRON)
									.hardnessAndResistance(4.0F, 6.0F).sound( SoundType.METAL)), "tin_block"),
				setup(new Block(Block.Properties.create(Material.IRON, MaterialColor.BLUE)
												.hardnessAndResistance(7.0F, 6.0F).sound( SoundType.METAL)), "mythril_block"),
				setup(new Block(Block.Properties.create(Material.IRON, MaterialColor.GREEN)
												.hardnessAndResistance(7.0F, 12.0F).sound( SoundType.METAL)), "adamantium_block"),
				setup(new Block(Block.Properties.create(Material.ROCK, MaterialColor.OBSIDIAN)
												.hardnessAndResistance(20.0F, 100.0F)), "onyx_block")
        );
		LOGGER.debug("Registered Blocks");
	}

	@SubscribeEvent
	public static void onModConfigEvent(final ModConfig.ModConfigEvent event)
	{
		final ModConfig config = event.getConfig();

		// Rebake the configs when they change
		if (config.getSpec() == ConfigHolder.CLIENT_SPEC) {
			ConfigHelper.bakeClient(config);
		}
		else if (config.getSpec() == ConfigHolder.SERVER_SPEC) {
			ConfigHelper.bakeServer(config);
		}
	} // onModConfigEvent

	/**
	 * This method will be called by Forge when it is time for the mod to register its Items.
	 * This method will always be called after the Block registry method.
	 */
	@SubscribeEvent
	public static void onRegisterItems(final RegistryEvent.Register<Item> event)
	{
		final IForgeRegistry<Item> registry = event.getRegistry();

		// ingots, nuggets, gems, parts
		registry.registerAll(
				setup(new Item(new Item.Properties().group(ModTabGroups.MOD_ITEM_GROUP)), "copper_ingot"),
				setup(new Item(new Item.Properties().group(ModTabGroups.MOD_ITEM_GROUP)), "tin_ingot"),
				setup(new Item(new Item.Properties().group(ModTabGroups.MOD_ITEM_GROUP)), "mythril_ingot"),
				setup(new Item(new Item.Properties().group(ModTabGroups.MOD_ITEM_GROUP)), "adamantium_ingot"),
				setup(new Item(new Item.Properties().group(ModTabGroups.MOD_ITEM_GROUP)), "onyx_gem"),
				setup(new Item(new Item.Properties().group(ModTabGroups.MOD_ITEM_GROUP)), "copper_nugget"),
				setup(new Item(new Item.Properties().group(ModTabGroups.MOD_ITEM_GROUP)), "tin_nugget"),
				setup(new Item(new Item.Properties().group(ModTabGroups.MOD_ITEM_GROUP)), "mythril_nugget"),
				setup(new Item(new Item.Properties().group(ModTabGroups.MOD_ITEM_GROUP)), "adamantium_nugget"),
				setup(new Item(new Item.Properties().group(ModTabGroups.MOD_ITEM_GROUP)), "mythril_rod"),
				setup(new Item(new Item.Properties().group(ModTabGroups.MOD_ITEM_GROUP)), "onyx_rod")
		);

		// We need to go over the entire registry so that we include any potential Registry Overrides
		for (final Block block : ForgeRegistries.BLOCKS.getValues()) {

			final ResourceLocation blockRegistryName = block.getRegistryName();
			Preconditions.checkNotNull(blockRegistryName, "Registry Name of Block \"" + block + "\" is null! This is not allowed!");

			// Check that the blocks is from our mod, if not, continue to the next block
			if (!blockRegistryName.getNamespace().equals(SimpleOres.MODID)) {
				continue;
			}
			// Make the properties, and make it so that the item will be on our ItemGroup (CreativeTab)
			final Item.Properties properties = new Item.Properties().group(ModTabGroups.MOD_ITEM_GROUP);

			// Create the new BlockItem with the block and it's properties
			final BlockItem blockItem = new BlockItem(block, properties);
			// Setup the new BlockItem with the block's registry name and register it
			registry.register(setup(blockItem, blockRegistryName));
		}
		LOGGER.debug("Registered Items");
	}

	public static <T extends IForgeRegistryEntry<T>> T setup(final T entry, final String name)
	{
		return setup(entry, new ResourceLocation(SimpleOres.MODID, name));
	}

	public static <T extends IForgeRegistryEntry<T>> T setup(final T entry, final ResourceLocation registryName) 
	{
		entry.setRegistryName(registryName);
		return entry;
	}

} // end class ModEventSubscriber
