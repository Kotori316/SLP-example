package com.example.examplemod

import net.minecraft.world.level.block.{Block, Blocks}
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.event.server.ServerStartingEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.InterModComms
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.{FMLClientSetupEvent, FMLCommonSetupEvent, InterModEnqueueEvent, InterModProcessEvent}
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import org.apache.logging.log4j.LogManager

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExampleMod.MOD_ID)
object ExampleMod {
  final val MOD_ID = "examplemod"
  // Directly reference a log4j logger.
  private val LOGGER = LogManager.getLogger

  // Register the setup method for modloading
  FMLJavaModLoadingContext.get.getModEventBus.addListener(this.setup)
  // Register the enqueueIMC method for modloading
  FMLJavaModLoadingContext.get.getModEventBus.addListener(this.enqueueIMC)
  // Register the processIMC method for modloading
  FMLJavaModLoadingContext.get.getModEventBus.addListener(this.processIMC)
  // Register the doClientStuff method for modloading
  FMLJavaModLoadingContext.get.getModEventBus.addListener(this.doClientStuff)

  // Register ourselves for server and other game events we are interested in
  MinecraftForge.EVENT_BUS.register(this)

  private def setup(event: FMLCommonSetupEvent): Unit = {
    // some preinit code
    LOGGER.info("HELLO FROM PRE-INIT")
    LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName)
  }

  private def doClientStuff(event: FMLClientSetupEvent): Unit = {
    // do something that can only be done on the client
    LOGGER.info("Hello Client!")
  }

  private def enqueueIMC(event: InterModEnqueueEvent): Unit = {
    // some example code to dispatch IMC to another mod
    InterModComms.sendTo(MOD_ID, "helloworld", () => {
      LOGGER.info("Hello world from the MDK")
      "Hello world"
    })
  }

  private def processIMC(event: InterModProcessEvent): Unit = {
    // some example code to receive and process InterModComms from other mods
    import scala.jdk.StreamConverters._
    val messages = event.getIMCStream.toScala(LazyList).map(_.messageSupplier().get())
    LOGGER.info("Got IMC {}", messages.mkString(", "))
  }

  // You can use SubscribeEvent and let the Event Bus discover methods to call
  @SubscribeEvent
  def onServerStarting(event: ServerStartingEvent): Unit = {
    // do something when the server starts
    LOGGER.info("HELLO from server starting")
  }

}

// You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
// Event bus for receiving Registry Events)
// The object must be at the top-level. Don't forget to fill modid.
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = ExampleMod.MOD_ID)
object RegistryEvents {
  private val LOGGER = LogManager.getLogger

  @SubscribeEvent
  def onBlocksRegistry(blockRegistryEvent: RegistryEvent.Register[Block]): Unit = {
    // register a new block here
    LOGGER.info("HELLO from Register Block")
  }
}
