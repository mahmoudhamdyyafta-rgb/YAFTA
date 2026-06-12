package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DesignLayer(
    val id: String,
    val name: String,
    val type: String, // "BACKGROUND", "TEXT", "SHAPE", "STICKER", "IMAGE"
    var x: Float = 100f,
    var y: Float = 100f,
    var width: Float = 400f,
    var height: Float = 150f,
    var rotation: Float = 0f,
    var opacity: Float = 1f,
    var colorHex: String = "#FFFFFF",
    var secondaryColorHex: String? = null,
    var text: String = "",
    var fontSize: Float = 24f,
    var fontFamily: String = "SANS_SERIF", // "SANS_SERIF", "SERIF", "MONOSPACE", "ARABIC_DISPLAY", "ARABIC_GEOMETRIC"
    var shapeType: String = "RECTANGLE", // "RECTANGLE", "ROUNDED_RECTANGLE", "CIRCLE", "TRIANGLE", "STAR_BADGE", "BANNER_RIBBON"
    var stickerId: String = "ic_sparkle", // Special identifiers
    var imageUrl: String? = null,
    var strokeWidth: Float = 0f,
    var isLocked: Boolean = false,
    var zIndex: Int = 0,
    var isVisible: Boolean = true,
    var letterSpacing: Float = 0f,
    var isCurved: Boolean = false,
    var hasShadow: Boolean = false,
    var textStrokeWidth: Float = 0f,
    var textStrokeColorHex: String = "#000000",
    var textShadowColorHex: String = "#000000",
    var blendMode: String = "SRC_OVER", // "SRC_OVER", "MULTIPLY", "SCREEN", "OVERLAY"
    var groupId: String? = null
)
