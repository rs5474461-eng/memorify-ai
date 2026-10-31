package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Memorify Dark Studio Theme Colors
val BackgroundDark = Color(0xFF090514)
val SurfaceDark = Color(0xFF130924)
val SurfaceCard = Color(0xFF1C1033)
val SurfaceCardElevated = Color(0xFF271547)
val CardStroke = Color(0xFF381F66)
val CardStrokeGlow = Color(0xFF6B21A8)

val NeonViolet = Color(0xFFA855F7)
val NeonPurple = Color(0xFF9333EA)
val DeepIndigo = Color(0xFF4C1D95)
val ElectricCyan = Color(0xFF06B6D4)
val BrightCyan = Color(0xFF22D3EE)
val AmberGold = Color(0xFFF59E0B)
val EmeraldGreen = Color(0xFF10B981)
val CoralRed = Color(0xFFEF4444)

val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

// Gradients
val PurpleCyanGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFA855F7), Color(0xFF06B6D4))
)

val StudioCardGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF221142), Color(0xFF130924))
)

val GlowGradient = Brush.radialGradient(
    colors = listOf(Color(0x33A855F7), Color(0x00090514))
)
