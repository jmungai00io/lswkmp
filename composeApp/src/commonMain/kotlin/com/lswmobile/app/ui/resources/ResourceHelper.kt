package com.lswmobile.app.ui.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import livestockwealth.composeapp.generated.resources.Res
import livestockwealth.composeapp.generated.resources.livestock_wealth_logo
import livestockwealth.composeapp.generated.resources.short_logo
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
}
