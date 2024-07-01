package me.ethanbrews.block

import com.mojang.serialization.MapCodec
import me.ethanbrews.RitualsRegistry
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class OreMagnetBlock : BlockWithEntity(Settings.copy(Blocks.IRON_BLOCK)) {

    private val _codec: MapCodec<OreMagnetBlock> = createCodec { _ -> OreMagnetBlock() }
    override fun getCodec(): MapCodec<out BlockWithEntity> = _codec

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return OreMagnetEntity(pos, state)
    }

    override fun <T : BlockEntity> getTicker(world: World, state: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T>? =
        if (!world.isClient) validateTicker(type, RitualsRegistry.ore_magnet_entity, OreMagnetEntity::tick) else null
}