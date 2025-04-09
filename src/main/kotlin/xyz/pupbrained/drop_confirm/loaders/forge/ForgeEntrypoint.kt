//? if forge {
package xyz.pupbrained.drop_confirm.loaders.forge

import net.minecraft.client.Minecraft
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory
import net.minecraftforge.client.event.InputEvent
import net.minecraftforge.client.event.RegisterKeyMappingsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.loading.FMLEnvironment
import org.spongepowered.asm.launch.MixinBootstrap
import org.spongepowered.asm.mixin.MixinEnvironment
import org.spongepowered.asm.mixin.Mixins
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.LOADING_CONTEXT
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import xyz.pupbrained.drop_confirm.DropConfirm
import xyz.pupbrained.drop_confirm.DropConfirm.TOGGLE_KEY
import xyz.pupbrained.drop_confirm.DropConfirm.handleKeyPresses
import xyz.pupbrained.drop_confirm.config.DropConfirmConfig
import xyz.pupbrained.drop_confirm.config.DropConfirmConfigScreen

@Mod("drop_confirm")
class ForgeEntrypoint {
  init {
    MixinBootstrap.init()
    Mixins.addConfiguration("drop_confirm.mixins.json")
    MixinEnvironment.getCurrentEnvironment().obfuscationContext = "searge"

    MOD_BUS.addListener { event: RegisterKeyMappingsEvent -> event.register(TOGGLE_KEY) }

    LOADING_CONTEXT.registerExtensionPoint(ConfigScreenFactory::class.java) {
      ConfigScreenFactory { _, screen ->
        try {
          DropConfirmConfigScreen(screen)
        } catch (e: Throwable) {
          DropConfirm.LOGGER.error("Failed to load config screen", e)
          screen
        }
      }
    }

    DropConfirmConfig.load()

    if (FMLEnvironment.dist == Dist.CLIENT)
      FORGE_BUS.register(object {
        @SubscribeEvent
        fun onKeyInput(event: InputEvent.Key) = handleKeyPresses(Minecraft.getInstance())
      })
  }
}
//?}