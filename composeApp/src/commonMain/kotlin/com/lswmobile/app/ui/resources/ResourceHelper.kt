package com.lswmobile.app.ui.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import livestockwealth.composeapp.generated.resources.Res
import livestockwealth.composeapp.generated.resources.livestock_wealth_logo
import livestockwealth.composeapp.generated.resources.short_logo
import livestockwealth.composeapp.generated.resources.connectedGarden
import livestockwealth.composeapp.generated.resources.farmLand
import livestockwealth.composeapp.generated.resources.freeRangeCow
import livestockwealth.composeapp.generated.resources.macadamia
import livestockwealth.composeapp.generated.resources.pregnantCow
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

/**
 * Helper class for handling resources in Compose Multiplatform
 */
@OptIn(ExperimentalResourceApi::class)
object ResourceHelper {
    /**
     * Load the Livestock Wealth logo
     */
    @Composable
    fun loadLivestockWealthLogo(): Painter {
        return painterResource(Res.drawable.livestock_wealth_logo)
    }
    
    /**
     * Load the short logo
     */
    @Composable
    fun loadShortLogo(): Painter {
        return painterResource(Res.drawable.short_logo)
    }
    
    /**
     * Load product image based on productType
     * Maps productType strings to corresponding PNG drawable resources
     */
    @Composable
    fun loadProductImage(productType: String?): Painter {
        return when (productType?.lowercase()) {
            "connectedgarden" -> painterResource(Res.drawable.connectedGarden)
            "farmland" -> painterResource(Res.drawable.farmLand)
            "freerangecow" -> painterResource(Res.drawable.freeRangeCow)
            "macadamia" -> painterResource(Res.drawable.macadamia)
            "pregnantcow" -> painterResource(Res.drawable.pregnantCow)
            else -> painterResource(Res.drawable.pregnantCow)
        }
    }
    
    /**
     * Get a fallback image for unknown product types
     */
    @Composable
    fun loadFallbackProductImage(): Painter {
        return painterResource(Res.drawable.livestock_wealth_logo)
    }
}
