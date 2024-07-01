package me.ethanbrews

import me.ethanbrews.Rituals.id
import me.ethanbrews.block.OreMagnetBlock
import me.ethanbrews.block.OreMagnetEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry


object RitualsRegistry {
    private var ore_magnet_block = OreMagnetBlock()

    val ore_magnet_entity: BlockEntityType<OreMagnetEntity> =
        Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            id("ore_magnet_entity"),
            BlockEntityType.Builder.create({ pos, state -> OreMagnetEntity(pos, state) }, ore_magnet_block).build()
        )

    fun registerBlock() {
        Registry.register(Registries.BLOCK, id("ore_magnet"), ore_magnet_block);
        Registry.register(Registries.ITEM, id("ore_magnet"), BlockItem(ore_magnet_block, Item.Settings()))
    }
}