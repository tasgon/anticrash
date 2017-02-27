package org.tasgoon.anticrash

import net.minecraft.client.gui.{GuiMainMenu, GuiWorldSelection}
import net.minecraft.launchwrapper.Launch
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLServerStartingEvent}
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import org.apache.logging.log4j.LogManager
import org.tasgoon.anticrash.gui.GuiWorldSelect

@Mod(modid = "anticrash", version = "0.0.1") class AntiCrash {
  class AntiCrashUIHandler {
    @SubscribeEvent @SideOnly(Side.CLIENT) def handleWorldUi(event: GuiOpenEvent): Unit = {
      event.getGui match {
        case gui: GuiWorldSelection => event.setGui(new GuiWorldSelect(new GuiMainMenu()))
        case gui => println("Gui: " + gui.getClass.getName)
      }
    }
  }

  val logger = LogManager.getLogger("AntiCrash")
  val dev: Boolean = Launch.blackboard.get("fml.deobfuscatedEnvironment").asInstanceOf[Boolean]

  @EventHandler def init(event: FMLInitializationEvent) {
    // some example code
    logger.info("Initiating AntiCrash")
    MinecraftForge.EVENT_BUS.register(new AntiCrashUIHandler())
  }

  @EventHandler def serverLoad(event: FMLServerStartingEvent): Unit = {
    logger.info("Registering crash command")
    if (dev) event.registerServerCommand(new CrashCommand())
  }
}