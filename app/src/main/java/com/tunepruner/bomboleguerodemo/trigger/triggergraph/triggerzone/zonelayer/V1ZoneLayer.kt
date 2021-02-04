package com.tunepruner.bomboleguerodemo.trigger.triggergraph.triggerzone.zonelayer

import android.graphics.Point
import android.graphics.PointF
import com.tunepruner.bomboleguerodemo.instrument.ScreenDimensions

class V1ZoneLayer(
    val zoneCount: Int,
    private val zoneIteration: Int,
    private val layerIteration: Int,
    val layerCountOfZone: Int,
    val screenDimensions: ScreenDimensions
) : ZoneLayer {
    var leftLimit: Int = 0
    var rightLimit: Int = 0
    var topLimit: Int = 0
    var bottomLimit: Int = 0

    init {
        calculateLimits(zoneCount, zoneIteration, layerIteration, layerCountOfZone)
    }

    override fun isMatch(pointF: PointF): Boolean {
        return pointF.x.toInt() in (leftLimit + 1)..rightLimit &&
                pointF.y.toInt() in (topLimit + 1)..bottomLimit
    }

    override fun getZoneIteration(): Int {
        return zoneIteration
    }

    override fun getLayerIteration(): Int {
        return layerIteration
    }

    private fun calculateLimits(
        zoneCount: Int,
        zoneIteration: Int,
        layerIteration: Int,
        layerCountOfZone: Int
    ){
        /* Deriving top limit of this TriggerZone from (height of a zone) * (number of preceding ones) */
        val thisZoneHeight = screenDimensions.screenHeight / zoneCount
        val zoneTopLimit = thisZoneHeight * (zoneIteration - 1)

        /* Deriving top limit of this ZoneLayer from (height of a layer) * (number of preceding ones) */
        val thisLayerHeight = thisZoneHeight / layerCountOfZone
        topLimit = zoneTopLimit + thisLayerHeight * (layerIteration - 1)

        bottomLimit = topLimit + thisLayerHeight

        leftLimit = 0
        rightLimit = screenDimensions.screenWidth
    }


}