package com.lswmobile.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lswmobile.app.ui.components.FarmProductCard
import com.lswmobile.app.ui.components.FarmersClubButton
import com.lswmobile.app.ui.components.LivestockAmountField
import com.lswmobile.app.ui.components.LivestockCard
import com.lswmobile.app.ui.components.LivestockFeatureCard
import com.lswmobile.app.ui.components.LivestockOutlinedCard
import com.lswmobile.app.ui.components.LivestockPasswordField
import com.lswmobile.app.ui.components.LivestockTextField
import com.lswmobile.app.ui.components.PrimaryButton
import com.lswmobile.app.ui.components.SecondaryButton
import com.lswmobile.app.ui.components.TextButton
import com.lswmobile.app.ui.theme.BrandColors
import com.lswmobile.app.ui.theme.LivestockWealthTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * A screen that showcases all UI components with the Livestock Wealth theme
 */
@Composable
fun ThemeShowcaseScreen() {
    var textFieldValue by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }
    var amountValue by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Livestock Wealth UI Theme",
            style = MaterialTheme.typography.headlineMedium,
            color = BrandColors.PrimaryBrown
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Typography showcase
        SectionTitle("Typography")
        Text("Display Large", style = MaterialTheme.typography.displayLarge)
        Text("Display Medium", style = MaterialTheme.typography.displayMedium)
        Text("Display Small", style = MaterialTheme.typography.displaySmall)
        Text("Headline Large", style = MaterialTheme.typography.headlineLarge)
        Text("Headline Medium", style = MaterialTheme.typography.headlineMedium)
        Text("Headline Small", style = MaterialTheme.typography.headlineSmall)
        Text("Title Large", style = MaterialTheme.typography.titleLarge)
        Text("Title Medium", style = MaterialTheme.typography.titleMedium)
        Text("Title Small", style = MaterialTheme.typography.titleSmall)
        Text("Body Large", style = MaterialTheme.typography.bodyLarge)
        Text("Body Medium", style = MaterialTheme.typography.bodyMedium)
        Text("Body Small", style = MaterialTheme.typography.bodySmall)
        Text("Label Large", style = MaterialTheme.typography.labelLarge)
        Text("Label Medium", style = MaterialTheme.typography.labelMedium)
        Text("Label Small", style = MaterialTheme.typography.labelSmall)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Buttons showcase
        SectionTitle("Buttons")
        PrimaryButton(
            text = "Primary Button",
            onClick = { /* do nothing */ },
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        SecondaryButton(
            text = "Secondary Button",
            onClick = { /* do nothing */ },
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        TextButton(
            text = "Text Button",
            onClick = { /* do nothing */ }
        )
        
        FarmersClubButton(
            text = "Farmers Club Button",
            onClick = { /* do nothing */ },
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Text fields showcase
        SectionTitle("Text Fields")
        LivestockTextField(
            value = textFieldValue,
            onValueChange = { textFieldValue = it },
            label = "Standard Text Field",
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        LivestockPasswordField(
            value = passwordValue,
            onValueChange = { passwordValue = it },
            label = "Password Field",
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        LivestockAmountField(
            value = amountValue,
            onValueChange = { amountValue = it },
            label = "Amount Field",
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Cards showcase
        SectionTitle("Cards")
        LivestockCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "Standard Card",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("This is a standard card component for content.")
        }
        
        LivestockOutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "Outlined Card",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("This is an outlined card with a border instead of elevation.")
        }
        
        LivestockFeatureCard(
            title = "Feature Card",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("This card has a colored header for feature highlights.")
        }
        
        FarmProductCard(
            title = "Premium Cow",
            price = "R 18,000",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("This card is specifically designed for farm products with pricing.")
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = BrandColors.PrimaryBrownDark,
        modifier = Modifier.padding(vertical = 8.dp)
    )
    Divider(color = BrandColors.SecondaryLightGrey)
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
@Preview
fun ThemeShowcaseScreenPreview() {
    LivestockWealthTheme {
        ThemeShowcaseScreen()
    }
}
