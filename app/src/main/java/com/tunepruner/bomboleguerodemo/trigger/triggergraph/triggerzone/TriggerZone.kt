package com.tunepruner.bomboleguerodemo.trigger.triggergraph.triggerzone

import android.graphics.Point
import com.tunepruner.bomboleguerodemo.trigger.triggergraph.triggerzone.zonelayer.ZoneLayer

interface TriggerZone {
    fun isMatch(point: Point): Boolean
    fun invokeLayer(point: Point): ZoneLayer?
    fun addLayer(triggerLayer: ZoneLayer)
    fun getLayer(zoneLayer: Int): ZoneLayer
}