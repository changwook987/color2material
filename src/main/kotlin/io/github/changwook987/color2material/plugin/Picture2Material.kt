package io.github.changwook987.color2material.plugin

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.scheduler.BukkitRunnable
import java.awt.Color
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.absoluteValue

object Picture2Material {
    private lateinit var plugin_: Color2materialPlugin
    private val plugin: Color2materialPlugin
        get() = plugin_

    fun init(plugin: Color2materialPlugin) {
        plugin_ = plugin
    }

    private fun getNearbyColorMaterial(color: Color): Material {
        return plugin.colorPalette.minByOrNull {
            it.first.run {
                (color.red - red).absoluteValue + (color.green - green).absoluteValue + (color.blue - blue).absoluteValue
            }
        }!!.second
    }

    fun draw(location: Location, pictureFile: File) {
        val bufferedImage = try {
            ImageIO.read(pictureFile)
        } catch (e: Exception) {
            return
        }

        val w = bufferedImage.width
        val h = bufferedImage.height

        class DrawRow(val z: Int) : BukkitRunnable() {
            override fun run() {
                repeat(w) { x ->
                    Location(location.world, location.x + x, location.y, location.z + z).block.setType(
                        getNearbyColorMaterial(Color(bufferedImage.getRGB(x, z))),
                        false)
                }
            }
        }

        repeat(h) { z ->
            DrawRow(z).runTask(plugin)
        }
    }
}