package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

@JsonClass(generateAdapter = true)
data class InlineData(
    val mimeType: String,
    val data: String
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val responseMimeType: String? = null,
    val temperature: Float? = null,
    val topP: Float? = null,
    val maxOutputTokens: Int? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<Candidate>?
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content?
)

// Standard Response classes for our generative designer output:
@JsonClass(generateAdapter = true)
data class AiLayoutResponse(
    val title: String,
    val backgroundColorHex: String,
    val isGradient: Boolean = false,
    val gradientStartColorHex: String? = null,
    val gradientEndColorHex: String? = null,
    val layers: List<AiLayerResponse>
)

@JsonClass(generateAdapter = true)
data class AiLayerResponse(
    val name: String,
    val type: String, // "TEXT", "SHAPE", "STICKER"
    val x: Float, // 0 to 1000 coordinate scale
    val y: Float, // 0 to 1000 coordinate scale
    val width: Float,
    val height: Float,
    val rotation: Float = 0f,
    var opacity: Float = 1f,
    val colorHex: String,
    val text: String = "",
    val fontSize: Float = 24f,
    val shapeType: String = "RECTANGLE", // "RECTANGLE", "ROUNDED_RECTANGLE", "CIRCLE", "TRIANGLE", "STAR_BADGE", "BANNER_RIBBON"
    val stickerId: String = "ic_sparkle",
    val zIndex: Int = 0
)
