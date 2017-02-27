package org.tasgoon.anticrash.gui

import java.io.IOException
import javax.annotation.Nullable

import com.google.common.base.Splitter
import com.google.common.collect.Lists
import net.minecraft.client.gui.{GuiButton, GuiCreateWorld, GuiScreen, GuiYesNoCallback}
import net.minecraft.client.resources.I18n
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

@SideOnly(Side.CLIENT) class GuiWorldSelect(/** The screen to return to when this closes (always Main Menu). */
                                            var prevScreen: GuiScreen) extends GuiScreen with GuiYesNoCallback {
  protected var title: String = "Select world"
  /** Tooltip displayed a world whose version is different from this client's */
  private var worldVersTooltip: String = null
  private var deleteButton: GuiButton = null
  private var selectButton: GuiButton = null
  private var renameButton: GuiButton = null
  private var copyButton: GuiButton = null
  private var selectionList: GuiListWorldSelect = null

  /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
  override def initGui() {
    //this.title = I18n.format("selectWorld.title", new Array[AnyRef](0))
    this.title = "wooloo"
    this.selectionList = new GuiListWorldSelect(this, this.mc, this.width, this.height, 32, this.height - 64, 36)
    this.postInit()
  }

  /**
    * Handles mouse input.
    */
  @throws[IOException]
  override def handleMouseInput() {
    super.handleMouseInput()
    this.selectionList.handleMouseInput()
  }

  def postInit() {
    this.selectButton = this.addButton(new GuiButton(1, this.width / 2 - 154, this.height - 52, 150, 20, I18n.format("selectWorld.select", new Array[AnyRef](0))))
    this.addButton(new GuiButton(3, this.width / 2 + 4, this.height - 52, 150, 20, I18n.format("selectWorld.create", new Array[AnyRef](0))))
    this.renameButton = this.addButton(new GuiButton(4, this.width / 2 - 154, this.height - 28, 72, 20, I18n.format("selectWorld.edit", new Array[AnyRef](0))))
    this.deleteButton = this.addButton(new GuiButton(2, this.width / 2 - 76, this.height - 28, 72, 20, I18n.format("selectWorld.delete", new Array[AnyRef](0))))
    this.copyButton = this.addButton(new GuiButton(5, this.width / 2 + 4, this.height - 28, 72, 20, I18n.format("selectWorld.recreate", new Array[AnyRef](0))))
    this.addButton(new GuiButton(0, this.width / 2 + 82, this.height - 28, 72, 20, I18n.format("gui.cancel", new Array[AnyRef](0))))
    this.selectButton.enabled = false
    this.deleteButton.enabled = false
    this.renameButton.enabled = false
    this.copyButton.enabled = false
  }

  /**
    * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
    */
  @throws[IOException]
  override protected def actionPerformed(button: GuiButton) {
    if (button.enabled) {
      val guilistworldselectionentry: GuiListWorldSelectEntry = this.selectionList.getSelectedWorld
      if (button.id == 2) if (guilistworldselectionentry != null) guilistworldselectionentry.deleteWorld()
      else if (button.id == 1) if (guilistworldselectionentry != null) guilistworldselectionentry.joinWorld()
      else if (button.id == 3) this.mc.displayGuiScreen(new GuiCreateWorld(this))
      else if (button.id == 4) if (guilistworldselectionentry != null) guilistworldselectionentry.editWorld()
      else if (button.id == 0) this.mc.displayGuiScreen(this.prevScreen)
      else if (button.id == 5 && guilistworldselectionentry != null) guilistworldselectionentry.recreateWorld()
    }
  }

  /**
    * Draws the screen and all the components in it.
    */
  override def drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
    this.worldVersTooltip = null
    this.selectionList.drawScreen(mouseX, mouseY, partialTicks)
    this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 20, 16777215)
    super.drawScreen(mouseX, mouseY, partialTicks)
    if (this.worldVersTooltip != null) this.drawHoveringText(Lists.newArrayList(Splitter.on("\n").split(this.worldVersTooltip)), mouseX, mouseY)
  }

  /**
    * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
    */
  @throws[IOException]
  override protected def mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
    super.mouseClicked(mouseX, mouseY, mouseButton)
    this.selectionList.mouseClicked(mouseX, mouseY, mouseButton)
  }

  /**
    * Called when a mouse button is released.
    */
  override protected def mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
    super.mouseReleased(mouseX, mouseY, state)
    this.selectionList.mouseReleased(mouseX, mouseY, state)
  }

  /**
    * Called back by selectionList when we call its drawScreen method, from ours.
    */
  def setVersionTooltip(tooltip: String)
  {
    this.worldVersTooltip = tooltip
  }

  def selectWorld(@Nullable entry: GuiListWorldSelectEntry) {
    val flag: Boolean = entry != null
    this.selectButton.enabled = flag
    this.deleteButton.enabled = flag
    this.renameButton.enabled = flag
    this.copyButton.enabled = flag
  }
}