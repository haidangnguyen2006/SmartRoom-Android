package com.seiuh.smartroomapp.ui.theme

import androidx.compose.ui.graphics.Color

// --- PALETTE FROM IMAGE ---
val PrimaryDark = Color(0xFF31374A)    // Dark Navy (Headings, Active Icons)
val PrimaryPurple = Color(0xFF985EE1)  // Purple Accent (Active toggles, Location Card)
val AccentPink = Color(0xFFF25656)     // Pink Accent (Gradient end)
val SoftPeach = Color(0xFFFFD0D0)      // Light Pink/Peach

// --- BACKGROUNDS ---
val AppBackground = Color(0xFFF5F5F9)  // Light Grayish Blue (Main BG)
val SurfaceWhite = Color(0xFFFFFFFF)   // Pure White (Cards)
val SurfaceLight = Color(0xFFDADFE7)   // Light Grey (Inactive Toggles/Tags)

// --- TEXT ---
val TextPrimary = Color(0xFF3C3C43)    // Dark Grey (Primary Text)
val TextSecondary = Color(0xFF3C3C43).copy(alpha = 0.6f) // Lighter Grey

// --- GRADIENTS ---
val PurplePinkGradient = listOf(PrimaryPurple, AccentPink)