package me.ethanbrews.block

import me.ethanbrews.Rituals.logger
import me.ethanbrews.RitualsRegistry
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.data.server.advancement.AdvancementTabGenerator.reference
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World


class OreMagnetEntity(pos: BlockPos, state: BlockState) : BlockEntity(RitualsRegistry.ore_magnet_entity, pos, state) {
    private val nbtKeyScanPos = "scanPos"
    private val nbtKeyScanRadius = "scanRad"
    private val nbtKeyIsEnabled = "scanEnabled"
    private var scanPos: BlockPos = pos
    private var scanRadius: Int = 5
    private var didAffectWorld = true
    private var isEnabled = true
    private var tickCount = 0
    private val tickInterval = 20  // Once per second

    override fun writeNbt(nbt: NbtCompound, registryLookup: RegistryWrapper.WrapperLookup) {
        nbt.putBoolean(nbtKeyIsEnabled, isEnabled)
        nbt.putIntArray(nbtKeyScanPos, intArrayOf(pos.x, pos.y, pos.z))
        nbt.putInt(nbtKeyScanRadius, scanRadius)
        super.writeNbt(nbt, registryLookup)
    }

    override fun readNbt(nbt: NbtCompound, registryLookup: RegistryWrapper.WrapperLookup) {
        super.readNbt(nbt, registryLookup)

        if (nbt.contains(nbtKeyScanPos)) nbt.getIntArray(nbtKeyScanPos).let {
            scanPos = BlockPos(it[0], it[1], it[2])
        }
        if (nbt.contains(nbtKeyScanRadius)) scanRadius = nbt.getInt(nbtKeyScanRadius)
        if (nbt.contains(nbtKeyIsEnabled)) isEnabled = nbt.getBoolean(nbtKeyIsEnabled)
    }

    fun tickServer(world: World, pos: BlockPos, state: BlockState) {
        if (!isEnabled) return
        if (tickCount++ < tickInterval) return
        tickCount = 0
        moveToNextScanPosition()
        val blockHere = world.getBlockState(scanPos)
        if (isMagnetised(blockHere)) {
            val posAbove = scanPos.add(0, 1, 0)
            val blockAbove = world.getBlockState(posAbove)
            if (!isMagnetised(blockAbove)) {
                world.setBlockState(scanPos, blockAbove)
                world.setBlockState(posAbove, blockHere)
                didAffectWorld = true
                // After moving a block, move up a layer + next pos
                scanPos = scanPos.add(0, 1, 0)
                moveToNextScanPosition()
                // TODO: Send a particle packet to the observing clients
            }
        }
    }

    fun tickClient(world: World, pos: BlockPos, state: BlockState) {

    }

    private fun moveToNextScanPosition() {
        var x: Int = scanPos.x
        var y: Int = scanPos.y
        var z: Int = scanPos.z

        // Move in z direction

        // Move in z direction
        z++
        if (z > pos.z + 2) {
            z = pos.z - 2
            x++
            if (x > pos.x + 2) {
                x = pos.x - 2
                y++
                if (y >= pos.y-2) { // Stop two blocks below the magnet (to avoid swapping with it)
                    // Reset the scan
                    isEnabled = didAffectWorld
                    didAffectWorld = false
                    y = -64
                }
            }
        }

        scanPos = BlockPos(x, y, z)
    }

    companion object {
        fun tick(world: World, pos: BlockPos, state: BlockState, be: OreMagnetEntity) {
            if (world.isClient)
                be.tickClient(world, pos, state)
            else
                be.tickServer(world, pos, state)
        }

        private fun isMagnetised(state: BlockState): Boolean {
            return state.streamTags().anyMatch { tag -> tag.id.namespace == "c" && tag.id.path == "ores" }
        }
    }
}