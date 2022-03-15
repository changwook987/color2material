package io.github.changwook987.color2material.plugin

import io.github.monun.kommand.kommand
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.awt.Color
import java.io.File
import javax.imageio.ImageIO

class Color2materialPlugin : JavaPlugin() {
    val colorPalette = ArrayList<Pair<Color, Material>>()

    override fun onEnable() {
        val pictureFolder = File(dataFolder, "pictures")
        pictureFolder.mkdir()

        val pictures = pictureFolder.listFiles { it ->
            it.name.endsWith(".jpg") || it.name.endsWith(".png")
        }?.filterNotNull() ?: emptyList()

        paletteInit()
        Picture2Material.init(this)

        kommand {
            register("draw") {
                requires { isPlayer }

                val map = buildMap {
                    for (picture in pictures) {
                        set(picture.name ?: continue, picture)
                    }
                }

                val dynamicMap = dynamicByMap(map)

                then("dynamic" to dynamicMap) {
                    executes {
                        object : BukkitRunnable() {
                            override fun run() {
                                val pictureFile: File = it["dynamic"]
                                Picture2Material.draw(player.location, pictureFile)
                            }
                        }.runTask(this@Color2materialPlugin)
                    }
                }
            }
        }
    }

    private fun paletteInit() {
        val texturesFolder = File(dataFolder, "textures")
        texturesFolder.mkdir()

        val textures = texturesFolder.listFiles { it -> it.name.endsWith(".png") }?.filterNotNull() ?: emptyList()

        val blocks = Material.values().filter { it.isOccluding }

        for (block in blocks) {
            val images = textures.filter { it.name.startsWith(block.name.lowercase()) }

            for (image in images) {
                val color = try {
                    avgColor(image)
                } catch (e: Exception) {
                    continue
                }

                colorPalette += color to block
            }
        }
    }

    private fun avgColor(image: File): Color {
        val bufferedImage = ImageIO.read(image)

        var r = 0
        var g = 0
        var b = 0

        val h = bufferedImage.height
        val w = bufferedImage.width

        repeat(h) { y ->
            repeat(w) { x ->
                Color(bufferedImage.getRGB(x, y)).apply {
                    r += red
                    g += green
                    b += blue
                }
            }
        }

        r /= h * w
        g /= h * w
        b /= h * w

        return Color(r, g, b)
    }
}