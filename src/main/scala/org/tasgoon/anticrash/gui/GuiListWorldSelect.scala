package org.tasgoon.anticrash.gui

import java.util
import javax.annotation.Nullable

import com.google.common.collect.Lists
import net.minecraft.client.{AnvilConverterException, Minecraft}
import net.minecraft.client.gui.{GuiErrorScreen, GuiListExtended}
import net.minecraft.world.storage.ISaveFormat
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import org.apache.logging.log4j.{LogManager, Logger}

import scala.collection.JavaConversions._

@SideOnly(Side.CLIENT) class GuiListWorldSelect(val worldSelectionObj: GuiWorldSelect, val clientIn: Minecraft, val a: Int, val b: Int, val c: Int, val d: Int, val e: Int)
  extends GuiListExtended(clientIn, a, b, c, d, e) {

  this.refreshList()
  final private val entries: util.List[GuiListWorldSelectEntry] = Lists.newArrayList[GuiListWorldSelectEntry]
  val LOGGER: Logger = LogManager.getLogger

  /** Index to the currently selected world */
  private var selectedIdx: Int = -1

  def refreshList() {
    val sf: ISaveFormat = this.mc.getSaveLoader
    try {
      val list = sf.getSaveList

      for (ws <- list) {
        this.entries.add(new GuiListWorldSelectEntry(this, ws, this.mc.getSaveLoader))
      }
    }
    catch {
      case ace: AnvilConverterException => {
        LOGGER.error("Couldn\'t load level list", ace.asInstanceOf[Throwable])
        this.mc.displayGuiScreen(new GuiErrorScreen("Unable to load worlds", ace.getMessage))
      }
    }
  }

  /**
    * Gets the IGuiListEntry object for the given index
    */
  def getListEntry(index: Int): GuiListWorldSelectEntry = {
    return this.entries.get(index)
  }

  protected def getSize: Int = {
    return this.entries.size
  }

  override protected def getScrollBarX: Int = {
    return super.getScrollBarX + 20
  }

  /**
    * Gets the width of the list
    */
  override def getListWidth: Int = {
    return super.getListWidth + 50
  }

  def selectWorld(idx: Int) {
    this.selectedIdx = idx
    this.worldSelectionObj.selectWorld(this.getSelectedWorld)
  }

  /**
    * Returns true if the element passed in is currently selected
    */
  override protected def isSelected(slotIndex: Int): Boolean = {
    return slotIndex == this.selectedIdx
  }

  @Nullable def getSelectedWorld: GuiListWorldSelectEntry = {
    return if (this.selectedIdx >= 0 && this.selectedIdx < this.getSize) this.getListEntry(this.selectedIdx)
    else null
  }

  def getGuiWorldSelect: GuiWorldSelect = {
    return this.worldSelectionObj
  }
}