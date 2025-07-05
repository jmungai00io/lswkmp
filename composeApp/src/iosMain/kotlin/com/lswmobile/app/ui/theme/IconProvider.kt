package com.lswmobile.app.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.lswmobile.app.ui.theme.AppIcons.Filled
import com.lswmobile.app.ui.theme.AppIcons.Outlined
import kotlin.math.cos
import kotlin.math.PI
import kotlin.math.sin

/**
 * iOS implementation of IconProvider using vector paths to simulate Material icons
 * 
 * Since Material icons are not available on iOS, we create simple placeholder icons
 * that visually represent the same concepts
 */
internal actual object IconProvider {
    actual val filledIcons: AppIcons.FilledIcons = IOSFilledIcons
    actual val outlinedIcons: AppIcons.OutlinedIcons = IOSOutlinedIcons
    
    /**
     * Create a simple placeholder icon for iOS
     */
    private fun createPlaceholderIcon(name: String): ImageVector {
        // Default icon color - using a dark color that will be visible on light backgrounds
        val iconColor = Color(0xFF333333)
        val iconFill = SolidColor(iconColor)
        
        return ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            // Create distinct shapes for each icon type
            when (name) {
                // Bottom navigation icons
                "Home", "Home.Outlined" -> {
                    // House shape with outline/filled variants
                    val isFilled = !name.contains("Outlined")
                    path(
                        fill = if (isFilled) iconFill else SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f
                    ) {
                        // Roof
                        moveTo(12f, 3f)  // Top of roof
                        lineTo(4f, 9f)   // Left edge of roof
                        lineTo(20f, 9f)  // Right edge of roof
                        close()
                        
                        // House body
                        moveTo(4f, 9f)
                        lineTo(4f, 21f)  // Bottom left corner
                        lineTo(20f, 21f) // Bottom right corner
                        lineTo(20f, 9f)  // Top right corner
                        
                        // Door
                        moveTo(10f, 21f)
                        lineTo(10f, 14f)
                        lineTo(14f, 14f)
                        lineTo(14f, 21f)
                    }
                }
                
                "Wallet", "Wallet.Outlined" -> {
                    val isFilled = !name.contains("Outlined")
                    path(
                        fill = if (isFilled) iconFill else SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f
                    ) {
                        // Wallet body
                        moveTo(4f, 6f)
                        lineTo(20f, 6f)
                        lineTo(20f, 18f)
                        lineTo(4f, 18f)
                        close()
                        
                        // Card slot
                        moveTo(16f, 12f)
                        arcTo(
                            1f, 1f, 0f, false, true,
                            16f, 14f
                        )
                    }
                }
                
                "List", "List.Outlined" -> {
                    val isFilled = !name.contains("Outlined")
                    // List items
                    
                    // First list item
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f
                    ) {
                        moveTo(6f, 8f)
                        lineTo(18f, 8f)
                    }
                    
                    // Second list item
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f
                    ) {
                        moveTo(6f, 12f)
                        lineTo(18f, 12f)
                    }
                    
                    // Third list item
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f
                    ) {
                        moveTo(6f, 16f)
                        lineTo(18f, 16f)
                    }
                }
                
                "AccountCircle", "AccountCircle.Outlined" -> {
                    val isFilled = !name.contains("Outlined")
                    
                    // Circle
                    path(
                        fill = if (isFilled) iconFill else SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f
                    ) {
                        // Simple circle approximation with lines
                        val cx = 12f
                        val cy = 12f
                        val radius = 9f
                        val segments = 20  // Number of segments to approximate the circle
                        
                        // Draw circle using lines
                        val angleIncrement = 2.0 * PI / segments
                        moveTo(cx + radius, cy)  // Start at right side of circle
                        
                        for (i in 1..segments) {
                            val angle = i * angleIncrement
                            val x = cx + radius * cos(angle).toFloat()
                            val y = cy + radius * sin(angle).toFloat()
                            lineTo(x, y)
                        }
                        close()
                    }
                    
                    // Head
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f
                    ) {
                        val headRadius = 3f
                        val cx = 12f
                        val cy = 10f
                        
                        // Simple circle for head
                        val segments = 12
                        val angleIncrement = 2.0 * PI / segments
                        moveTo(cx + headRadius, cy)
                        
                        for (i in 1..segments) {
                            val angle = i * angleIncrement
                            val x = cx + headRadius * cos(angle).toFloat()
                            val y = cy + headRadius * sin(angle).toFloat()
                            lineTo(x, y)
                        }
                        close()
                    }
                    
                    // Body
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f
                    ) {
                        moveTo(12f, 13f) // Top of body
                        lineTo(12f, 17f) // Bottom of body
                        moveTo(9f, 15f)  // Left arm
                        lineTo(15f, 15f) // Right arm
                    }
                }
                
                "Newspaper", "Newspaper.Outlined" -> {
                    val isFilled = !name.contains("Outlined")
                    
                    // Main paper rectangle
                    path(
                        fill = if (isFilled) iconFill else SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f
                    ) {
                        moveTo(4f, 4f)
                        lineTo(20f, 4f)
                        lineTo(20f, 20f)
                        lineTo(4f, 20f)
                        close()
                    }
                    
                    // Header line
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f
                    ) {
                        moveTo(6f, 8f)
                        lineTo(18f, 8f)
                    }
                    
                    // Content lines
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1f
                    ) {
                        moveTo(6f, 11f)
                        lineTo(18f, 11f)
                        moveTo(6f, 14f)
                        lineTo(18f, 14f)
                        moveTo(6f, 17f)
                        lineTo(14f, 17f)
                    }
                }
                
                "ShoppingCart", "ShoppingCart.Outlined" -> {
                    val isFilled = !name.contains("Outlined")
                    // Shopping cart with handle and wheels
                    path(
                        fill = if (isFilled) iconFill else SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f,
                        strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round
                    ) {
                        // Cart body
                        moveTo(7f, 8f)      // Top-left of cart
                        lineTo(5f, 5f)      // Handle left point
                        lineTo(3f, 5f)      // Handle far left
                        
                        moveTo(7f, 8f)
                        lineTo(19f, 8f)     // Top-right of cart
                        lineTo(17f, 16f)    // Bottom-right of cart
                        lineTo(9f, 16f)     // Bottom-left of cart
                        close()             // Back to top-left
                    }
                    
                    // Wheels (separate paths to avoid connection lines)
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f
                    ) {
                        // Left wheel
                        val cx1 = 10f
                        val cy1 = 19f
                        val radius1 = 1.5f
                        val segments = 8
                        
                        val angleIncrement = 2.0 * PI / segments
                        moveTo(cx1 + radius1, cy1)
                        
                        for (i in 1..segments) {
                            val angle = i * angleIncrement
                            val x = cx1 + radius1 * cos(angle).toFloat()
                            val y = cy1 + radius1 * sin(angle).toFloat()
                            lineTo(x, y)
                        }
                        close()
                    }
                    
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f
                    ) {
                        // Right wheel
                        val cx2 = 16f
                        val cy2 = 19f
                        val radius2 = 1.5f
                        val segments = 8
                        
                        val angleIncrement = 2.0 * PI / segments
                        moveTo(cx2 + radius2, cy2)
                        
                        for (i in 1..segments) {
                            val angle = i * angleIncrement
                            val x = cx2 + radius2 * cos(angle).toFloat()
                            val y = cy2 + radius2 * sin(angle).toFloat()
                            lineTo(x, y)
                        }
                        close()
                    }
                }
                
                "Check" -> {
                    // Checkmark
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 2f,
                        strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
                        strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
                    ) {
                        moveTo(6f, 12f)
                        lineTo(10f, 16f)
                        lineTo(18f, 8f)
                    }
                }
                
                "Clear", "Error" -> {
                    // X mark
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 2f,
                        strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
                        strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
                    ) {
                        moveTo(6f, 6f)
                        lineTo(18f, 18f)
                        moveTo(18f, 6f)
                        lineTo(6f, 18f)
                    }
                }
                
                "Schedule" -> {
                    // Clock shape
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f
                    ) {
                        // Simple circle approximation with lines (no oval/arc needed)
                        val cx = 12f
                        val cy = 12f
                        val radius = 8f
                        val segments = 20  // Number of segments to approximate the circle
                        
                        // Draw circle using lines
                        val angleIncrement = 2.0 * PI / segments
                        moveTo(cx + radius, cy)  // Start at right side of circle
                        
                        for (i in 1..segments) {
                            val angle = i * angleIncrement
                            val x = cx + radius * cos(angle).toFloat()
                            val y = cy + radius * sin(angle).toFloat()
                            lineTo(x, y)
                        }
                        close()
                        
                        // Hour hand
                        moveTo(12f, 12f)
                        lineTo(12f, 8f)
                        
                        // Minute hand
                        moveTo(12f, 12f)
                        lineTo(16f, 12f)
                    }
                }
                
                "Search" -> {
                    // Search magnifying glass
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f
                    ) {
                        // Search glass circle
                        val cx = 11f
                        val cy = 11f
                        val radius = 6f
                        val segments = 16
                        
                        val angleIncrement = 2.0 * PI / segments
                        moveTo(cx + radius, cy)
                        
                        for (i in 1..segments) {
                            val angle = i * angleIncrement
                            val x = cx + radius * cos(angle).toFloat()
                            val y = cy + radius * sin(angle).toFloat()
                            lineTo(x, y)
                        }
                        close()
                        
                        // Handle
                        moveTo(15.5f, 15.5f)
                        lineTo(19f, 19f)
                    }
                }
                
                "Description" -> {
                    // Document icon
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f
                    ) {
                        // Document outline
                        moveTo(6f, 4f)
                        lineTo(18f, 4f)
                        lineTo(18f, 20f)
                        lineTo(6f, 20f)
                        close()
                        
                        // Document lines
                        moveTo(9f, 9f)
                        lineTo(15f, 9f)
                        
                        moveTo(9f, 12f)
                        lineTo(15f, 12f)
                        
                        moveTo(9f, 15f)
                        lineTo(15f, 15f)
                    }
                }
                
                "Folder" -> {
                    // Folder icon
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f
                    ) {
                        // Folder shape
                        moveTo(4f, 8f)      // Start at top-left folder tab
                        lineTo(9f, 8f)      // Across to tab
                        lineTo(10f, 6f)     // Up to tab peak
                        lineTo(20f, 6f)     // Across top of folder
                        lineTo(20f, 18f)    // Down right side
                        lineTo(4f, 18f)     // Across bottom
                        close()             // Back to start
                    }
                }
                
                "Receipt" -> {
                    // Receipt icon
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f
                    ) {
                        // Receipt shape with zigzag bottom
                        moveTo(6f, 4f)
                        lineTo(18f, 4f)
                        lineTo(18f, 18f)
                        lineTo(16f, 19f)
                        lineTo(14f, 18f)
                        lineTo(12f, 19f)
                        lineTo(10f, 18f)
                        lineTo(8f, 19f)
                        lineTo(6f, 18f)
                        close()
                        
                        // Receipt lines
                        moveTo(9f, 8f)
                        lineTo(15f, 8f)
                        
                        moveTo(9f, 11f)
                        lineTo(15f, 11f)
                        
                        moveTo(9f, 14f)
                        lineTo(13f, 14f)
                    }
                }
                
                "ArrowUpward" -> {
                    // Up arrow
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 2f,
                        strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
                        strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
                    ) {
                        // Arrow shaft
                        moveTo(12f, 20f)
                        lineTo(12f, 8f)
                        
                        // Arrow head
                        moveTo(7f, 12f)
                        lineTo(12f, 7f)
                        lineTo(17f, 12f)
                    }
                }
                
                "ArrowDownward" -> {
                    // Down arrow
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 2f,
                        strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
                        strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
                    ) {
                        // Arrow shaft
                        moveTo(12f, 4f)
                        lineTo(12f, 16f)
                        
                        // Arrow head
                        moveTo(7f, 12f)
                        lineTo(12f, 17f)
                        lineTo(17f, 12f)
                    }
                }
                
                "ArrowForward" -> {
                    // Forward arrow (right)
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 2f,
                        strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
                        strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
                    ) {
                        // Arrow shaft
                        moveTo(4f, 12f)
                        lineTo(16f, 12f)
                        
                        // Arrow head
                        moveTo(12f, 7f)
                        lineTo(17f, 12f)
                        lineTo(12f, 17f)
                    }
                }
                
                "Add" -> {
                    // Plus sign icon
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 2f,
                        strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round
                    ) {
                        // Horizontal line of plus sign
                        moveTo(6f, 12f)
                        lineTo(18f, 12f)
                        
                        // Vertical line of plus sign
                        moveTo(12f, 6f)
                        lineTo(12f, 18f)
                    }
                }
                
                "FilterList" -> {
                    // Filter icon
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f,
                        strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round
                    ) {
                        // First (longest) line
                        moveTo(4f, 6f)
                        lineTo(20f, 6f)
                        
                        // Second (medium) line
                        moveTo(6f, 12f)
                        lineTo(18f, 12f)
                        
                        // Third (shortest) line
                        moveTo(8f, 18f)
                        lineTo(16f, 18f)
                    }
                }
                
                "Close" -> {
                    // X/close symbol
                    path(
                        fill = SolidColor(Color.Black),
                        fillAlpha = 1f,
                        stroke = null,
                        strokeAlpha = 1f,
                        strokeLineWidth = 1.0f,
                        strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Butt,
                        strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Miter,
                        strokeLineMiter = 1f,
                        pathFillType = androidx.compose.ui.graphics.PathFillType.NonZero
                    ) {
                        moveTo(19f, 6.41f)
                        lineTo(17.59f, 5f)
                        lineTo(12f, 10.59f)
                        lineTo(6.41f, 5f)
                        lineTo(5f, 6.41f)
                        lineTo(10.59f, 12f)
                        lineTo(5f, 17.59f)
                        lineTo(6.41f, 19f)
                        lineTo(12f, 13.41f)
                        lineTo(17.59f, 19f)
                        lineTo(19f, 17.59f)
                        lineTo(13.41f, 12f)
                        close()
                    }
                }
                
                "Refresh" -> {
                    // Refresh/reload circular arrow
                    path(
                        fill = SolidColor(Color.Black),
                        fillAlpha = 1f,
                        stroke = null,
                        strokeAlpha = 1f,
                        strokeLineWidth = 1.0f,
                        strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Butt,
                        strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Miter,
                        strokeLineMiter = 1f,
                        pathFillType = androidx.compose.ui.graphics.PathFillType.NonZero
                    ) {
                        moveTo(17.65f, 6.35f)
                        curveTo(16.2f, 4.9f, 14.21f, 4f, 12f, 4f)
                        curveTo(7.58f, 4f, 4f, 7.58f, 4f, 12f)
                        curveTo(4f, 16.42f, 7.58f, 20f, 12f, 20f)
                        curveTo(15.73f, 20f, 18.84f, 17.45f, 19.73f, 14f)
                        horizontalLineTo(17.65f)
                        curveTo(16.83f, 16.33f, 14.61f, 18f, 12f, 18f)
                        curveTo(8.69f, 18f, 6f, 15.31f, 6f, 12f)
                        curveTo(6f, 8.69f, 8.69f, 6f, 12f, 6f)
                        curveTo(13.66f, 6f, 15.14f, 6.69f, 16.22f, 7.78f)
                        lineTo(13f, 11f)
                        horizontalLineTo(20f)
                        verticalLineTo(4f)
                        lineTo(17.65f, 6.35f)
                        close()
                    }
                }
                
                "Back" -> {
                    // Back arrow (iOS style)
                    path(
                        fill = SolidColor(Color.Black),
                        fillAlpha = 1f,
                        stroke = null,
                        strokeAlpha = 1f,
                        strokeLineWidth = 1.0f,
                        strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Butt,
                        strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Miter,
                        strokeLineMiter = 1f,
                        pathFillType = androidx.compose.ui.graphics.PathFillType.NonZero
                    ) {
                        moveTo(17.77f, 3.77f)
                        lineTo(16f, 2f)
                        lineTo(6f, 12f)
                        lineTo(16f, 22f)
                        lineTo(17.77f, 20.23f)
                        lineTo(9.54f, 12f)
                        close()
                    }
                }
                
                else -> {
                    // Default square shape for other icons like Add, FilterList, etc.
                    path(
                        fill = SolidColor(Color.Black),
                        fillAlpha = 1f,
                        stroke = null,
                        strokeAlpha = 1f,
                        strokeLineWidth = 1.0f,
                        strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Butt,
                        strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Miter,
                        strokeLineMiter = 1f,
                        pathFillType = androidx.compose.ui.graphics.PathFillType.NonZero
                    ) {
                        moveTo(3f, 3f)
                        horizontalLineToRelative(18f)
                        verticalLineToRelative(18f)
                        horizontalLineToRelative(-18f)
                        close()
                    }
                }
            }
        }.build()
    }
    
    /**
     * iOS implementation of Filled icons using placeholder vectors
     */
    private object IOSFilledIcons : AppIcons.FilledIcons {
        override val Home: ImageVector = createPlaceholderIcon("Home")
        override val ShoppingCart: ImageVector = createPlaceholderIcon("ShoppingCart")
        override val Wallet: ImageVector = createPlaceholderIcon("Wallet")
        override val AccountCircle: ImageVector = createPlaceholderIcon("AccountCircle")
        override val List: ImageVector = createPlaceholderIcon("List")
        override val Newspaper: ImageVector = createPlaceholderIcon("Newspaper")
        
        override val Add: ImageVector = createPlaceholderIcon("Add")
        override val ArrowForward: ImageVector = createPlaceholderIcon("ArrowForward")
        override val ArrowDownward: ImageVector = createPlaceholderIcon("ArrowDownward")
        override val ArrowUpward: ImageVector = createPlaceholderIcon("ArrowUpward")
        
        override val Description: ImageVector = createPlaceholderIcon("Description")
        override val Folder: ImageVector = createPlaceholderIcon("Folder")
        override val Receipt: ImageVector = createPlaceholderIcon("Receipt")
        
        override val Check: ImageVector = createPlaceholderIcon("Check")
        override val Clear: ImageVector = createPlaceholderIcon("Clear")
        override val Error: ImageVector = createPlaceholderIcon("Error")
        override val FilterList: ImageVector = createPlaceholderIcon("FilterList")
        override val Search: ImageVector = createPlaceholderIcon("Search")
        override val Schedule: ImageVector = createPlaceholderIcon("Schedule")
        override val LocationOn: ImageVector = createPlaceholderIcon("LocationOn")
        override val Alarm: ImageVector = createPlaceholderIcon("Alarm")
        override val CheckCircle: ImageVector = createPlaceholderIcon("CheckCircle")
        override val Refresh: ImageVector = createPlaceholderIcon("Refresh")
        override val Back: ImageVector = createPlaceholderIcon("Back")
    }
    
    /**
     * iOS implementation of Outlined icons using placeholder vectors
     */
    private object IOSOutlinedIcons : AppIcons.OutlinedIcons {
        override val Home: ImageVector = createPlaceholderIcon("Home.Outlined")
        override val ShoppingCart: ImageVector = createPlaceholderIcon("ShoppingCart.Outlined")
        override val Wallet: ImageVector = createPlaceholderIcon("Wallet.Outlined")
        override val AccountCircle: ImageVector = createPlaceholderIcon("AccountCircle.Outlined")
        override val List: ImageVector = createPlaceholderIcon("List.Outlined")
        override val Newspaper: ImageVector = createPlaceholderIcon("Newspaper.Outlined")
    }
}
