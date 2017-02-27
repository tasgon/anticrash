package org.tasgoon.anticrash.gui

import java.awt.image.BufferedImage
import java.io.File
import java.text.{DateFormat, SimpleDateFormat}
import java.util.Date
import javax.imageio.ImageIO

import net.minecraft.client.Minecraft
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.client.gui._
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.resources.I18n
import net.minecraft.init.SoundEvents
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.storage.{ISaveFormat, ISaveHandler, WorldInfo, WorldSummary}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import org.apache.commons.lang3.{StringUtils, Validate}
import org.apache.logging.log4j.{LogManager, Logger}
import org.tasgoon.anticrash.WorldLoader

@SideOnly(Side.CLIENT) class GuiListWorldSelectEntry(val containingListSel: GuiListWorldSelect, val worldSummary: WorldSummary, val sf: ISaveFormat)
  extends GuiListExtended.IGuiListEntry {
  this.worldSelScreen = containingListSel.getGuiWorldSelect
  this.client = Minecraft.getMinecraft
  this.iconLocation = new ResourceLocation("worlds/" + worldSummary.getFileName + "/icon")
  this.iconFile = sf.getFile(worldSummary.getFileName, "icon.png")
  if (!this.iconFile.isFile) {
    this.iconFile = null
  }
  this.loadServerIcon()
  final private var client: Minecraft = null
  final private var worldSelScreen: GuiWorldSelect = null
  final private var iconLocation: ResourceLocation = null
  private var iconFile: File = null
  private var icon: DynamicTexture = null
  private var lastClickTime: Long = 0L

  private val LOGGER: Logger = LogManager.getLogger
  private val DATE_FORMAT: DateFormat = new SimpleDateFormat
  private val ICON_MISSING: ResourceLocation = new ResourceLocation("textures/misc/unknown_server.png")
  private val ICON_OVERLAY_LOCATION: ResourceLocation = new ResourceLocation("textures/gui/world_selection.png")

  def drawEntry(slotIndex: Int, x: Int, y: Int, listWidth: Int, slotHeight: Int, mouseX: Int, mouseY: Int, isSelected: Boolean) {
    var s: String = this.worldSummary.getDisplayName
    val s1: String = this.worldSummary.getFileName + " (" + DATE_FORMAT.format(new Date(this.worldSummary.getLastTimePlayed)) + ")"
    var s2: String = ""
    if (StringUtils.isEmpty(s)) {
      s = I18n.format("selectWorld.world", new Array[AnyRef](0)) + " " + (slotIndex + 1)
    }
    if (this.worldSummary.requiresConversion) {
      s2 = I18n.format("selectWorld.conversion", new Array[AnyRef](0)) + " " + s2
    }
    else {
      s2 = I18n.format("gameMode." + this.worldSummary.getEnumGameType.getName, new Array[AnyRef](0))
      if (this.worldSummary.isHardcoreModeEnabled) {
        s2 = TextFormatting.DARK_RED + I18n.format("gameMode.hardcore", new Array[AnyRef](0)) + TextFormatting.RESET
      }
      if (this.worldSummary.getCheatsEnabled) {
        s2 = s2 + ", " + I18n.format("selectWorld.cheats", new Array[AnyRef](0))
      }
      val s3: String = this.worldSummary.getVersionName
      if (this.worldSummary.markVersionInList) {
        if (this.worldSummary.askToOpenWorld) {
          s2 = s2 + ", " + I18n.format("selectWorld.version", new Array[AnyRef](0)) + " " + TextFormatting.RED + s3 + TextFormatting.RESET
        }
        else {
          s2 = s2 + ", " + I18n.format("selectWorld.version", new Array[AnyRef](0)) + " " + TextFormatting.ITALIC + s3 + TextFormatting.RESET
        }
      }
      else {
        s2 = s2 + ", " + I18n.format("selectWorld.version", new Array[AnyRef](0)) + " " + s3
      }
    }
    this.client.fontRendererObj.drawString(s, x + 32 + 3, y + 1, 16777215)
    this.client.fontRendererObj.drawString(s1, x + 32 + 3, y + this.client.fontRendererObj.FONT_HEIGHT + 3, 8421504)
    this.client.fontRendererObj.drawString(s2, x + 32 + 3, y + this.client.fontRendererObj.FONT_HEIGHT + this.client.fontRendererObj.FONT_HEIGHT + 3, 8421504)
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F)
    this.client.getTextureManager.bindTexture(if (this.icon != null) this.iconLocation
    else ICON_MISSING)
    GlStateManager.enableBlend()
    Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F)
    GlStateManager.disableBlend()
    if (this.client.gameSettings.touchscreen || isSelected) {
      this.client.getTextureManager.bindTexture(ICON_OVERLAY_LOCATION)
      Gui.drawRect(x, y, x + 32, y + 32, -1601138544)
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F)
      val j: Int = mouseX - x
      val i: Int = if (j < 32) 32
      else 0
      if (this.worldSummary.markVersionInList) {
        Gui.drawModalRectWithCustomSizedTexture(x, y, 32.0F, i.toFloat, 32, 32, 256.0F, 256.0F)
        if (this.worldSummary.askToOpenWorld) {
          Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, i.toFloat, 32, 32, 256.0F, 256.0F)
          if (j < 32) {
            this.worldSelScreen.setVersionTooltip(TextFormatting.RED + I18n.format("selectWorld.tooltip.fromNewerVersion1", new Array[AnyRef](0)) + "\n" + TextFormatting.RED + I18n.format("selectWorld.tooltip.fromNewerVersion2", new Array[AnyRef](0)))
          }
        }
        else {
          Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, i.toFloat, 32, 32, 256.0F, 256.0F)
          if (j < 32) {
            this.worldSelScreen.setVersionTooltip(TextFormatting.GOLD + I18n.format("selectWorld.tooltip.snapshot1", new Array[AnyRef](0)) + "\n" + TextFormatting.GOLD + I18n.format("selectWorld.tooltip.snapshot2", new Array[AnyRef](0)))
          }
        }
      }
      else {
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, i.toFloat, 32, 32, 256.0F, 256.0F)
      }
    }
  }

  /**
    * Called when the mouse is clicked within this entry. Returning true means that something within this entry was
    * clicked and the list should not be dragged.
    */
  def mousePressed(slotIndex: Int, mouseX: Int, mouseY: Int, mouseEvent: Int, relativeX: Int, relativeY: Int): Boolean = {
    this.containingListSel.selectWorld(slotIndex)
    if (relativeX <= 32 && relativeX < 32) {
      this.joinWorld()
      return true
    }
    else if (Minecraft.getSystemTime - this.lastClickTime < 250L) {
      this.joinWorld()
      return true
    }
    else {
      this.lastClickTime = Minecraft.getSystemTime
      return false
    }
  }

  def joinWorld() {
    if (this.worldSummary.askToOpenWorld) {
      this.client.displayGuiScreen(new GuiYesNo(new GuiYesNoCallback() {
        def confirmClicked(result: Boolean, id: Int) {
          if (result) {
            GuiListWorldSelectEntry.this.loadWorld()
          }
          else {
            GuiListWorldSelectEntry.this.client.displayGuiScreen(GuiListWorldSelectEntry.this.worldSelScreen)
          }
        }
      }, I18n.format("selectWorld.versionQuestion", new Array[AnyRef](0)), I18n.format("selectWorld.versionWarning", Array[AnyRef](this.worldSummary.getVersionName)), I18n.format("selectWorld.versionJoinButton", new Array[AnyRef](0)), I18n.format("gui.cancel", new Array[AnyRef](0)), 0))
    }
    else {
      this.loadWorld()
    }
  }

  def deleteWorld() {
    this.client.displayGuiScreen(new GuiYesNo(new GuiYesNoCallback() {
      def confirmClicked(result: Boolean, id: Int) {
        if (result) {
          GuiListWorldSelectEntry.this.client.displayGuiScreen(new GuiScreenWorking)
          val isaveformat: ISaveFormat = GuiListWorldSelectEntry.this.client.getSaveLoader
          isaveformat.flushCache()
          isaveformat.deleteWorldDirectory(GuiListWorldSelectEntry.this.worldSummary.getFileName)
          GuiListWorldSelectEntry.this.containingListSel.refreshList()
        }
        GuiListWorldSelectEntry.this.client.displayGuiScreen(GuiListWorldSelectEntry.this.worldSelScreen)
      }
    }, I18n.format("selectWorld.deleteQuestion", new Array[AnyRef](0)), "\'" + this.worldSummary.getDisplayName + "\' " + I18n.format("selectWorld.deleteWarning", new Array[AnyRef](0)), I18n.format("selectWorld.deleteButton", new Array[AnyRef](0)), I18n.format("gui.cancel", new Array[AnyRef](0)), 0))
  }

  def editWorld() {
    this.client.displayGuiScreen(new GuiWorldEdit(this.worldSelScreen, this.worldSummary.getFileName))
  }

  def recreateWorld() {
    this.client.displayGuiScreen(new GuiScreenWorking)
    val guicreateworld: GuiCreateWorld = new GuiCreateWorld(this.worldSelScreen)
    val isavehandler: ISaveHandler = this.client.getSaveLoader.getSaveLoader(this.worldSummary.getFileName, false)
    val worldinfo: WorldInfo = isavehandler.loadWorldInfo
    isavehandler.flush()
    guicreateworld.recreateFromExistingWorld(worldinfo)
    this.client.displayGuiScreen(guicreateworld)
  }

  private def loadWorld() {
    this.client.getSoundHandler.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F))
    if (this.client.getSaveLoader.canLoadWorld(this.worldSummary.getFileName)) {
      WorldLoader.tryLoadExistingWorld(worldSelScreen, this.worldSummary)
    }
  }

  private def loadServerIcon() {
    val flag: Boolean = this.iconFile != null && this.iconFile.isFile
    if (flag) {
      var bufferedimage: BufferedImage = null
      try {
        bufferedimage = ImageIO.read(this.iconFile)
        Validate.validState(bufferedimage.getWidth == 64, "Must be 64 pixels wide", new Array[AnyRef](0))
        Validate.validState(bufferedimage.getHeight == 64, "Must be 64 pixels high", new Array[AnyRef](0))
      }
      catch {
        case throwable: Throwable => {
          LOGGER.error("Invalid icon for world {}", Array[AnyRef](this.worldSummary.getFileName, throwable))
          this.iconFile = null
          return
        }
      }
      if (this.icon == null) {
        this.icon = new DynamicTexture(bufferedimage.getWidth, bufferedimage.getHeight)
        this.client.getTextureManager.loadTexture(this.iconLocation, this.icon)
      }
      bufferedimage.getRGB(0, 0, bufferedimage.getWidth, bufferedimage.getHeight, this.icon.getTextureData, 0, bufferedimage.getWidth)
      this.icon.updateDynamicTexture()
    }
    else if (!flag) {
      this.client.getTextureManager.deleteTexture(this.iconLocation)
      this.icon = null
    }
  }

  /**
    * Fired when the mouse button is released. Arguments: index, x, y, mouseEvent, relativeX, relativeY
    */
  def mouseReleased(slotIndex: Int, x: Int, y: Int, mouseEvent: Int, relativeX: Int, relativeY: Int) {
  }

  def setSelected(a: Int, b: Int, c: Int)
  {
  }
}