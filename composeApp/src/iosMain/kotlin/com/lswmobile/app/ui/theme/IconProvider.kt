package com.lswmobile.app.ui.theme

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import com.lswmobile.app.App
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
// Deprecated: platform IconProvider removed by common-only refactor.
internal object IconProviderRemoved {
    // Keep references so the compiler doesn't strip the helpers during incremental builds
    private val filledIconsRemoved = IOSFilledIcons
    private val outlinedIconsRemoved = IOSOutlinedIcons
    
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
                    val isOutlined = name.contains("Outlined")

                    // Outer circle
                    path(
                        fill = if (!isOutlined) SolidColor(AppColors.AppPrimary) else SolidColor(Color.Transparent),
                        stroke = if (isOutlined) iconFill else null,
                        strokeLineWidth = if (isOutlined) 1.5f else 0f,
                        strokeLineCap = StrokeCap.Round,
                        strokeLineJoin = StrokeJoin.Round
                    ) {
                        // Draw a full circle by chaining two arcs
                        moveTo(22f, 12f)
                        arcTo(
                            horizontalEllipseRadius = 10f,
                            verticalEllipseRadius = 10f,
                            theta = 0f,
                            isMoreThanHalf = true,
                            isPositiveArc = true,
                            x1 = 2f,
                            y1 = 12f
                        )
                        arcTo(
                            horizontalEllipseRadius = 10f,
                            verticalEllipseRadius = 10f,
                            theta = 0f,
                            isMoreThanHalf = true,
                            isPositiveArc = true,
                            x1 = 22f,
                            y1 = 12f
                        )
                        close() // Required to actually fill the shape!
                    }

                    // Head
                    path(
                        fill = if (!isOutlined) SolidColor(Color.White) else SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = if (!isOutlined) 2f else 1.5f,
                        strokeLineCap = StrokeCap.Round,
                        strokeLineJoin = StrokeJoin.Round
                    ) {
                        moveTo(15f, 9f)
                        arcTo(
                            horizontalEllipseRadius = 3f,
                            verticalEllipseRadius = 3f,
                            theta = 0f,
                            isMoreThanHalf = true,
                            isPositiveArc = true,
                            x1 = 9f,
                            y1 = 9f
                        )
                        arcTo(
                            horizontalEllipseRadius = 3f,
                            verticalEllipseRadius = 3f,
                            theta = 0f,
                            isMoreThanHalf = true,
                            isPositiveArc = true,
                            x1 = 15f,
                            y1 = 9f
                        )
                        close() // So white head fill works
                    }

                    // Shoulders/body
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = if (!isOutlined) 2f else 1.5f,
                        strokeLineCap = StrokeCap.Round,
                        strokeLineJoin = StrokeJoin.Round
                    ) {
                        moveTo(6f, 18f)
                        quadTo(12f, 14f, 18f, 18f)
                    }
                }
                
                "Newspaper", "Newspaper.Outlined" -> {
                    val isFilled = !name.contains("Outlined")
                    
                    // Newspaper with proper folds and sections
                    path(
                        fill = if (isFilled) iconFill else SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f,
                        strokeLineCap = StrokeCap.Round,
                        strokeLineJoin = StrokeJoin.Round
                    ) {
                        // Main paper rectangle
                        moveTo(4f, 4f)
                        lineTo(20f, 4f)
                        lineTo(20f, 20f)
                        lineTo(4f, 20f)
                        close()
                        
                        // Fold lines
                        moveTo(4f, 8f)
                        lineTo(20f, 8f)
                        moveTo(4f, 12f)
                        lineTo(20f, 12f)
                        moveTo(4f, 16f)
                        lineTo(20f, 16f)
                        
                        // Vertical fold
                        moveTo(12f, 4f)
                        lineTo(12f, 20f)
                    }
                    
                    // Newspaper text lines
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1f,
                        strokeLineCap = StrokeCap.Round
                    ) {
                        // Header section
                        moveTo(6f, 6f)
                        lineTo(10f, 6f)
                        
                        // Article titles
                        moveTo(6f, 10f)
                        lineTo(14f, 10f)
                        moveTo(6f, 11.5f)
                        lineTo(12f, 11.5f)
                        
                        // Body text lines
                        moveTo(6f, 14f)
                        lineTo(18f, 14f)
                        moveTo(6f, 15.5f)
                        lineTo(16f, 15.5f)
                        moveTo(6f, 17f)
                        lineTo(15f, 17f)
                        moveTo(6f, 18.5f)
                        lineTo(13f, 18.5f)
                        
                        // Right column
                        moveTo(13f, 10f)
                        lineTo(18f, 10f)
                        moveTo(13f, 11.5f)
                        lineTo(18f, 11.5f)
                        moveTo(13f, 14f)
                        lineTo(18f, 14f)
                    }
                }
                
                "ShoppingCart", "ShoppingCart.Outlined" -> {
                    val isFilled = !name.contains("Outlined")
                    // Shopping cart with handle and wheels
                    path(
                        fill = if (isFilled) iconFill else SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f,
                        strokeLineCap = StrokeCap.Round
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
                        strokeLineCap = StrokeCap.Round,
                        strokeLineJoin = StrokeJoin.Round
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
                        strokeLineCap = StrokeCap.Round,
                        strokeLineJoin = StrokeJoin.Round
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
                    val isFilled = !name.contains("Outlined")
                    // Up arrow
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f,
                        strokeLineCap = StrokeCap.Round,
                        strokeLineJoin = StrokeJoin.Round
                    ) {
                        moveTo(12f, 20f)
                        lineTo(12f, 4f)
                        lineTo(6f, 10f)
                        moveTo(12f, 4f)
                        lineTo(18f, 10f)
                    }
                }
                
                "ArrowDownward" -> {
                    val isFilled = !name.contains("Outlined")
                    // Down arrow
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f,
                        strokeLineCap = StrokeCap.Round,
                        strokeLineJoin = StrokeJoin.Round
                    ) {
                        moveTo(12f, 4f)
                        lineTo(12f, 20f)
                        lineTo(6f, 14f)
                        moveTo(12f, 20f)
                        lineTo(18f, 14f)
                    }
                }
                
                "ArrowForward" -> {
                    val isFilled = !name.contains("Outlined")
                    // Right arrow
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f,
                        strokeLineCap = StrokeCap.Round,
                        strokeLineJoin = StrokeJoin.Round
                    ) {
                        moveTo(4f, 12f)
                        lineTo(20f, 12f)
                        lineTo(14f, 6f)
                        moveTo(20f, 12f)
                        lineTo(14f, 18f)
                    }
                }
                
                "Add" -> {
                    val isFilled = !name.contains("Outlined")
                    // Plus sign
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f,
                        strokeLineCap = StrokeCap.Round
                    ) {
                        moveTo(12f, 5f)
                        lineTo(12f, 19f)
                        moveTo(5f, 12f)
                        lineTo(19f, 12f)
                    }
                }
                
                "FilterList" -> {
                    val isFilled = !name.contains("Outlined")
                    // Filter icon (funnel shape)
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f
                    ) {
                        moveTo(4f, 6f)
                        lineTo(20f, 6f)
                        lineTo(16f, 12f)
                        lineTo(16f, 18f)
                        lineTo(8f, 18f)
                        lineTo(8f, 12f)
                        close()
                    }
                }
                
                "LocationOn" -> {
                    val isFilled = !name.contains("Outlined")
                    // Location pin
                    path(
                        fill = if (isFilled) iconFill else SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f
                    ) {
                        // Pin shape (teardrop)
                        moveTo(12f, 2f)
                        lineTo(8f, 8f)
                        lineTo(12f, 22f)
                        lineTo(16f, 8f)
                        close()
                        
                        // Inner circle
                        moveTo(12f, 8f)
                        arcTo(2f, 2f, 0f, false, true, 12f, 12f)
                    }
                }
                
                "Alarm" -> {
                    val isFilled = !name.contains("Outlined")
                    // Alarm clock
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f
                    ) {
                        // Clock circle
                        val cx = 12f
                        val cy = 12f
                        val radius = 8f
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
                        
                        // Hour hand
                        moveTo(12f, 12f)
                        lineTo(12f, 8f)
                        
                        // Minute hand
                        moveTo(12f, 12f)
                        lineTo(16f, 12f)
                        
                        // Top bell
                        moveTo(8f, 4f)
                        lineTo(16f, 4f)
                        
                        // Bottom bell
                        moveTo(8f, 20f)
                        lineTo(16f, 20f)
                    }
                }
                
                "CheckCircle" -> {
                    val isFilled = !name.contains("Outlined")
                    // Checkmark in circle
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f
                    ) {
                        // Circle
                        val cx = 12f
                        val cy = 12f
                        val radius = 8f
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
                    }
                    
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f,
                        strokeLineCap = StrokeCap.Round,
                        strokeLineJoin = StrokeJoin.Round
                    ) {
                        moveTo(9f, 12f)
                        lineTo(11f, 14f)
                        lineTo(15f, 10f)
                    }
                }
                
                "Refresh" -> {
                    val isFilled = !name.contains("Outlined")
                    // Refresh icon (circular arrow)
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f,
                        strokeLineCap = StrokeCap.Round,
                        strokeLineJoin = StrokeJoin.Round
                    ) {
                        // Circular arrow
                        val cx = 12f
                        val cy = 12f
                        val radius = 8f
                        val segments = 12
                        
                        val angleIncrement = 2.0 * PI / segments
                        moveTo(cx + radius, cy)
                        
                        for (i in 1..(segments * 3/4)) {
                            val angle = i * angleIncrement
                            val x = cx + radius * cos(angle).toFloat()
                            val y = cy + radius * sin(angle).toFloat()
                            lineTo(x, y)
                        }
                        
                        // Arrow head
                        moveTo(15f, 7f)
                        lineTo(17f, 5f)
                        lineTo(19f, 7f)
                    }
                }
                
                "Back" -> {
                    val isFilled = !name.contains("Outlined")
                    // Back arrow (left arrow)
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f,
                        strokeLineCap = StrokeCap.Round,
                        strokeLineJoin = StrokeJoin.Round
                    ) {
                        moveTo(20f, 12f)
                        lineTo(4f, 12f)
                        lineTo(10f, 6f)
                        moveTo(4f, 12f)
                        lineTo(10f, 18f)
                    }
                }
                
                "KeyboardArrowDown" -> {
                    val isFilled = !name.contains("Outlined")
                    // Down arrow
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f,
                        strokeLineCap = StrokeCap.Round,
                        strokeLineJoin = StrokeJoin.Round
                    ) {
                        moveTo(7f, 10f)
                        lineTo(12f, 15f)
                        lineTo(17f, 10f)
                    }
                }
                
                "KeyboardArrowUp" -> {
                    val isFilled = !name.contains("Outlined")
                    // Up arrow
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f,
                        strokeLineCap = StrokeCap.Round,
                        strokeLineJoin = StrokeJoin.Round
                    ) {
                        moveTo(7f, 14f)
                        lineTo(12f, 9f)
                        lineTo(17f, 14f)
                    }
                }

                "Favorite" -> {
                    val isOutlined = name.contains("Outlined")

                    path(
                        fill = if (!isOutlined) iconFill else SolidColor(Color.Transparent),
                        stroke = if (isOutlined) iconFill else null,
                        strokeLineWidth = if (isOutlined) 1.5f else 0f,
                        strokeLineCap = StrokeCap.Round,
                        strokeLineJoin = StrokeJoin.Round
                    ) {
                        // Start at the bottom tip of the heart
                        moveTo(12f, 21f)

                        // Left bottom curve up to the left lobe
                        quadTo(6f, 16f, 6f, 10f)

                        // Left arc (lobe)
                        arcTo(
                            horizontalEllipseRadius = 3f,
                            verticalEllipseRadius = 3f,
                            theta = 0f,
                            isMoreThanHalf = true,
                            isPositiveArc = false,
                            x1 = 12f,
                            y1 = 8f
                        )

                        // Right arc (lobe)
                        arcTo(
                            horizontalEllipseRadius = 3f,
                            verticalEllipseRadius = 3f,
                            theta = 0f,
                            isMoreThanHalf = true,
                            isPositiveArc = false,
                            x1 = 18f,
                            y1 = 10f
                        )

                        // Right bottom curve down to the bottom tip
                        quadTo(18f, 16f, 12f, 21f)

                        close()
                    }
                }
                
                "Close" -> {
                    val isFilled = !name.contains("Outlined")
                    // Close X
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f,
                        strokeLineCap = StrokeCap.Round,
                        strokeLineJoin = StrokeJoin.Round
                    ) {
                        moveTo(6f, 6f)
                        lineTo(18f, 18f)
                        moveTo(18f, 6f)
                        lineTo(6f, 18f)
                    }
                }
                
                "ChevronLeft" -> {
                    val isFilled = !name.contains("Outlined")
                    // Left chevron
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f,
                        strokeLineCap = StrokeCap.Round,
                        strokeLineJoin = StrokeJoin.Round
                    ) {
                        moveTo(15f, 6f)
                        lineTo(9f, 12f)
                        lineTo(15f, 18f)
                    }
                }
                
                "ChevronRight" -> {
                    val isFilled = !name.contains("Outlined")
                    // Right chevron
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f,
                        strokeLineCap = StrokeCap.Round,
                        strokeLineJoin = StrokeJoin.Round
                    ) {
                        moveTo(9f, 6f)
                        lineTo(15f, 12f)
                        lineTo(9f, 18f)
                    }
                }
                
                "FavoriteBorder" -> {
                    val isFilled = !name.contains("Outlined")
                    // Heart outline
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f,
                        strokeLineCap = StrokeCap.Round,
                        strokeLineJoin = StrokeJoin.Round
                    ) {
                        moveTo(12f, 21f)
                        lineTo(10.55f, 19.7f)
                        lineTo(5f, 14.51f)
                        lineTo(5f, 9f)
                        lineTo(7f, 7f)
                        lineTo(12f, 11.95f)
                        lineTo(17f, 7f)
                        lineTo(19f, 9f)
                        lineTo(19f, 14.51f)
                        lineTo(13.45f, 19.7f)
                        close()
                    }
                }
                
                else -> {
                    // Default placeholder - simple circle
                    path(
                        fill = SolidColor(Color.Transparent),
                        stroke = iconFill,
                        strokeLineWidth = 1.5f
                    ) {
                        val cx = 12f
                        val cy = 12f
                        val radius = 8f
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
                    }
                }
            }
        }.build()
    }
    
    /**
     * iOS implementation of Filled icons using placeholder vectors
     */
    private object IOSFilledIcons {
        val Home: ImageVector = createPlaceholderIcon("Home")
        val ShoppingCart: ImageVector = createPlaceholderIcon("ShoppingCart")
        val Wallet: ImageVector = createPlaceholderIcon("Wallet")
        val AccountCircle: ImageVector = createPlaceholderIcon("AccountCircle")
        val List: ImageVector = createPlaceholderIcon("List")
        val Newspaper: ImageVector = createPlaceholderIcon("Newspaper")
        
        val Add: ImageVector = createPlaceholderIcon("Add")
        val ArrowForward: ImageVector = createPlaceholderIcon("ArrowForward")
        val ArrowDownward: ImageVector = createPlaceholderIcon("ArrowDownward")
        val ArrowUpward: ImageVector = createPlaceholderIcon("ArrowUpward")
        
        val Description: ImageVector = createPlaceholderIcon("Description")
        val Folder: ImageVector = createPlaceholderIcon("Folder")
        val Receipt: ImageVector = createPlaceholderIcon("Receipt")
        
        val Check: ImageVector = createPlaceholderIcon("Check")
        val Clear: ImageVector = createPlaceholderIcon("Clear")
        val Error: ImageVector = createPlaceholderIcon("Error")
        val FilterList: ImageVector = createPlaceholderIcon("FilterList")
        val Search: ImageVector = createPlaceholderIcon("Search")
        val Schedule: ImageVector = createPlaceholderIcon("Schedule")
        val LocationOn: ImageVector = createPlaceholderIcon("LocationOn")
        val Alarm: ImageVector = createPlaceholderIcon("Alarm")
        val CheckCircle: ImageVector = createPlaceholderIcon("CheckCircle")
        val Refresh: ImageVector = createPlaceholderIcon("Refresh")
        val Back: ImageVector = createPlaceholderIcon("Back")
        val KeyboardArrowDown: ImageVector = createPlaceholderIcon("KeyboardArrowDown")
        val KeyboardArrowUp: ImageVector = createPlaceholderIcon("KeyboardArrowUp")
        val Favorite: ImageVector = createPlaceholderIcon("Favorite")
        val Close: ImageVector = createPlaceholderIcon("Close")
        val ChevronLeft: ImageVector = createPlaceholderIcon("ChevronLeft")
        val ChevronRight: ImageVector = createPlaceholderIcon("ChevronRight")
    }
    
    /**
     * iOS implementation of Outlined icons using placeholder vectors
     */
    private object IOSOutlinedIcons {
        val Portfolio: ImageVector = createPlaceholderIcon("Portfolio.Outlined")
        val Home: ImageVector = createPlaceholderIcon("Home.Outlined")
        val ShoppingCart: ImageVector = createPlaceholderIcon("ShoppingCart.Outlined")
        val Wallet: ImageVector = createPlaceholderIcon("Wallet.Outlined")
        val AccountCircle: ImageVector = createPlaceholderIcon("AccountCircle.Outlined")
        val List: ImageVector = createPlaceholderIcon("List.Outlined")
        val Newspaper: ImageVector = createPlaceholderIcon("Newspaper.Outlined")
        val FavoriteBorder: ImageVector = createPlaceholderIcon("FavoriteBorder")
    }
}
