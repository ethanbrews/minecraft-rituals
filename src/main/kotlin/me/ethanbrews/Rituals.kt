package me.ethanbrews

import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Rituals : ModInitializer {
	val logger: Logger = LoggerFactory.getLogger("rituals")
	private const val MOD_ID = "rituals"

	override fun onInitialize() {
		RitualsRegistry.registerBlock()
	}

	fun id(name: String): Identifier {
		return Identifier.of(MOD_ID, name)
	}
}