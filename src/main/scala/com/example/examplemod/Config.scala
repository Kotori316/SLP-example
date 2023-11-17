package com.example.examplemod

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.config.ModConfigEvent
import net.neoforged.neoforge.common.ModConfigSpec
import net.neoforged.neoforge.registries.ForgeRegistries

import scala.annotation.static
import scala.jdk.CollectionConverters.*

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
object Config {
  private val BUILDER: ModConfigSpec.Builder = new ModConfigSpec.Builder()

  private val LOG_DIRT_BLOCK: ModConfigSpec.BooleanValue = BUILDER
    .comment("Whether to log the dirt block on common setup")
    .define("logDirtBlock", true)

  private val MAGIC_NUMBER: ModConfigSpec.IntValue = BUILDER
    .comment("A magic number")
    .defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE)

  private val MAGIC_NUMBER_INTRODUCTION: ModConfigSpec.ConfigValue[String] = BUILDER
    .comment("What you want the introduction message to be for the magic number")
    .define("magicNumberIntroduction", "The magic number is... ")

  // a list of strings that are treated as resource locations for items
  private val ITEM_STRINGS: ModConfigSpec.ConfigValue[java.util.List[_ <: String]] = BUILDER
    .comment("A list of items to log on common setup.")
    .defineListAllowEmpty("items", java.util.List.of("minecraft:iron_ingot"), this.validateItemName)

  val SPEC: ModConfigSpec = BUILDER.build

  var logDirtBlock: Boolean = false
  var magicNumber: Int = 0
  var magicNumberIntroduction: String = "null"
  var items: Set[Item] = Set.empty

  private def validateItemName(obj: AnyRef) = {
    obj match {
      case itemName: String => ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName))
      case _ => false
    }
  }

  @static
  @SubscribeEvent
  def onLoad(event: ModConfigEvent): Unit = {
    logDirtBlock = LOG_DIRT_BLOCK.get()
    magicNumber = MAGIC_NUMBER.get()
    magicNumberIntroduction = MAGIC_NUMBER_INTRODUCTION.get()

    // convert the java list of strings into a scala set of items
    items = ITEM_STRINGS.get().asScala
      .map(itemName => ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName)))
      .toSet
  }
}

@Mod.EventBusSubscriber(modid = ExampleMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
class Config
