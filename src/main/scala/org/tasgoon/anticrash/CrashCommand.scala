package org.tasgoon.anticrash

import java.util

import net.minecraft.command.{ICommand, ICommandSender}
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent

import scala.collection.JavaConversions.seqAsJavaList

/**
  * Test crash command
  */
class CrashCommand extends ICommand {
  class CrashTick {
    @SubscribeEvent
    def crashTicker(event: ServerTickEvent): Unit = {
      throw new Exception("AntiCrash test")
    }
  }

  override def getCommandName: String = "crash"

  override def getCommandAliases: util.List[String] = seqAsJavaList(Seq("crash"))

  override def isUsernameIndex(args: Array[String], index: Int): Boolean = false

  override def getCommandUsage(sender: ICommandSender): String = "crash"

  override def execute(server: MinecraftServer, sender: ICommandSender, args: Array[String]): Unit = {
    MinecraftForge.EVENT_BUS.register(new CrashTick())
  }

  override def getTabCompletionOptions(server: MinecraftServer, sender: ICommandSender, args: Array[String], pos: BlockPos): util.List[String] = null

  override def checkPermission(server: MinecraftServer, sender: ICommandSender): Boolean = true

  override def compareTo(o: ICommand): Int = ???
}
