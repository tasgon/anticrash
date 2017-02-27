package org.tasgoon.anticrash

import java.io.{File, FileInputStream}

import net.minecraft.client.Minecraft
import net.minecraft.nbt.{CompressedStreamTools, NBTTagCompound}
import net.minecraft.world.storage.WorldSummary
import net.minecraftforge.fml.client.{FMLClientHandler, GuiOldSaveLoadConfirm}
import net.minecraftforge.fml.common.{FMLLog, StartupQuery}
import org.tasgoon.anticrash.gui.GuiWorldSelect

/**
  * World loader with anti-crash.
  */
object WorldLoader {
  def tryLoadExistingWorld(selectWorldGUI: GuiWorldSelect, comparator: WorldSummary): Unit = {
    val dir: File = new File(FMLClientHandler.instance().getSavesDir, comparator.getFileName)
    var leveldat: NBTTagCompound = null
    try
      leveldat = CompressedStreamTools.readCompressed(new FileInputStream(new File(dir, "level.dat")))

    catch {
      case e: Exception => {
        try
          leveldat = CompressedStreamTools.readCompressed(new FileInputStream(new File(dir, "level.dat_old")))

        catch {
          case e1: Exception => {
            FMLLog.warning("There appears to be a problem loading the save %s, both level files are unreadable.", comparator.getFileName)
            return
          }
        }
      }
    }
    val fmlData: NBTTagCompound = leveldat.getCompoundTag("FML")
    if (fmlData.hasKey("ModItemData")) FMLClientHandler.instance.showGuiScreen(new GuiOldSaveLoadConfirm(comparator.getFileName, comparator.getDisplayName, selectWorldGUI))
    else try
      Minecraft.getMinecraft.launchIntegratedServer(comparator.getFileName, comparator.getDisplayName, null)

    catch {
      case e: StartupQuery.AbortedException =>
      case e: Exception => {
        e.printStackTrace
      }
    }
  }
}
