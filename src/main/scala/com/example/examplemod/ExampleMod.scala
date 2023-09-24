package com.example.examplemod

import net.minecraft.client.Minecraft
import net.minecraft.core.registries.Registries
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.{BlockItem, CreativeModeTab, CreativeModeTabs, Item}
import net.minecraft.world.level.block.{Block, Blocks}
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.MapColor
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent
import net.minecraftforge.event.server.ServerStartingEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.{FMLClientSetupEvent, FMLCommonSetupEvent}
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.registries.{DeferredRegister, ForgeRegistries, RegistryObject}
import org.apache.logging.log4j.LogManager

/**
 * Converted from forge MDK in https://github.com/MinecraftForge/MinecraftForge
 */
@Mod(ExampleMod.MOD_ID)
object ExampleMod {
  final val MOD_ID = "examplemod"
  // Directly reference a log4j logger.
  private val LOGGER = LogManager.getLogger

  val BLOCKS: DeferredRegister[Block] = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID)
  val ITEMS: DeferredRegister[Item] = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID)
  val CREATIVE_MODE_TABS: DeferredRegister[CreativeModeTab] = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID)

  val EXAMPLE_BLOCK: RegistryObject[Block] = BLOCKS.register("example_block", () => new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)))
  val EXAMPLE_BLOCK_ITEM: RegistryObject[Item] = ITEMS.register("example_block", () => new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()))

  val EXAMPLE_ITEM: RegistryObject[Item] = ITEMS.register("example_item", () => new Item(new Item.Properties().food(new FoodProperties.Builder()
    .alwaysEat().nutrition(1).saturationMod(2f).build())))

  val EXAMPLE_TAB: RegistryObject[CreativeModeTab] = CREATIVE_MODE_TABS.register("example_tab", () => CreativeModeTab.builder()
    .withTabsBefore(CreativeModeTabs.COMBAT)
    .icon(() => EXAMPLE_ITEM.get().getDefaultInstance)
    .displayItems((parameters, output) => {
      output.accept(EXAMPLE_ITEM.get())
    }).build()
  )

  {
    val modEventBus = FMLJavaModLoadingContext.get().getModEventBus

    // Register the commonSetup method for modloading
    modEventBus.addListener(this.commonSetup)

    // Register the Deferred Register to the mod event bus so blocks get registered
    BLOCKS.register(modEventBus)
    // Register the Deferred Register to the mod event bus so items get registered
    ITEMS.register(modEventBus)
    // Register the Deferred Register to the mod event bus so tabs get registered
    CREATIVE_MODE_TABS.register(modEventBus)

    // Register ourselves for server and other game events we are interested in
    MinecraftForge.EVENT_BUS.register(this)

    // Register the item to a creative tab// Register the item to a creative tab
    modEventBus.addListener(this.addCreative)

    // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us// Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
    ModLoadingContext.get.registerConfig(ModConfig.Type.COMMON, Config.SPEC)
  }

  private def commonSetup(event: FMLCommonSetupEvent): Unit = {
    // Some common setup code
    LOGGER.info("HELLO FROM COMMON SETUP")
    if (Config.logDirtBlock)
      LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT))
    LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber)
    Config.items.foreach(item => LOGGER.info("ITEM >> {}", item.toString))
  }

  // Add the example block item to the building blocks tab
  private def addCreative(event: BuildCreativeModeTabContentsEvent): Unit = {
    if (event.getTabKey == CreativeModeTabs.BUILDING_BLOCKS)
      event.accept(EXAMPLE_BLOCK_ITEM)
  }

  // You can use SubscribeEvent and let the Event Bus discover methods to call
  @SubscribeEvent
  def onServerStarting(event: ServerStartingEvent): Unit = {
    // Do something when the server starts
    LOGGER.info("HELLO from server starting")
  }

  // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
  @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Array(Dist.CLIENT))
  object ClientModEvents {
    @SubscribeEvent
    def onClientSetup(event: FMLClientSetupEvent): Unit = {
      // Some client setup code
      LOGGER.info("HELLO FROM CLIENT SETUP")
      LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance.getUser.getName)
    }
  }
}
