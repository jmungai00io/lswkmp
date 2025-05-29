package com.lswmobile.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.lswmobile.app.ui.theme.BrandColors


/**
 * Standard text field for text input
 */
@Composable
fun LivestockTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    hint: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label?.let { { Text(it) } },
        placeholder = hint?.let { { Text(it) } },
        singleLine = true,
        enabled = enabled,
        isError = isError,
        supportingText = errorMessage?.let {
            { Text(it, color = MaterialTheme.colorScheme.error) }
        },
        colors = TextFieldDefaults.colors(
            /* containers */
            focusedContainerColor   = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor  = MaterialTheme.colorScheme.surface,
            errorContainerColor     = MaterialTheme.colorScheme.surface,

            /* outline / underline */
            focusedIndicatorColor   = BrandColors.PrimaryBrown,
            unfocusedIndicatorColor = BrandColors.SecondaryMediumGrey,
            disabledIndicatorColor  = BrandColors.SecondaryDarkerGrey,
            errorIndicatorColor     = MaterialTheme.colorScheme.error,

            /* text */
            focusedTextColor   = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            disabledTextColor  = BrandColors.SecondaryDarkerGrey,
            errorTextColor     = MaterialTheme.colorScheme.onSurface
        ),
        keyboardOptions  = keyboardOptions,
        keyboardActions  = keyboardActions,
        modifier = modifier.fillMaxWidth()
    )
}


/**
 * Password field with show/hide toggle
 */
@Composable
fun LivestockPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.Password,
        imeAction = ImeAction.Done
    ),
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    var passwordVisible by remember { mutableStateOf(false) }
    
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label?.let { { Text(text = it) } },
        singleLine = true,
        enabled = enabled,
        isError = isError,
        supportingText = errorMessage?.let { 
            { 
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                ) 
            } 
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//        trailingIcon = {
//            IconButton(onClick = { passwordVisible = !passwordVisible }) {
//                Icon(
//                    imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
//                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
//                )
//            }
//        },
        colors = TextFieldDefaults.colors(
            /* containers */
            focusedContainerColor   = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor  = MaterialTheme.colorScheme.surface,
            errorContainerColor     = MaterialTheme.colorScheme.surface,

            /* outline / underline */
            focusedIndicatorColor   = BrandColors.PrimaryBrown,
            unfocusedIndicatorColor = BrandColors.SecondaryMediumGrey,
            disabledIndicatorColor  = BrandColors.SecondaryDarkerGrey,
            errorIndicatorColor     = MaterialTheme.colorScheme.error,

            /* text */
            focusedTextColor   = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            disabledTextColor  = BrandColors.SecondaryDarkerGrey,
            errorTextColor     = MaterialTheme.colorScheme.onSurface
        ),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        modifier = modifier.fillMaxWidth()
    )
}

/**
 * Amount field with currency prefix
 */
@Composable
fun LivestockAmountField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Done
    ),
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = { 
            // Only allow digits and one decimal point
            if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                onValueChange(it)
            }
        },
        label = label?.let { { Text(text = it) } },
        singleLine = true,
        enabled = enabled,
        isError = isError,
        supportingText = errorMessage?.let { 
            { 
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                ) 
            } 
        },
        prefix = {
            Text(
                text = "R ",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        colors = TextFieldDefaults.colors(
            /* containers */
            focusedContainerColor   = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor  = MaterialTheme.colorScheme.surface,
            errorContainerColor     = MaterialTheme.colorScheme.surface,

            /* outline / underline */
            focusedIndicatorColor   = BrandColors.PrimaryBrown,
            unfocusedIndicatorColor = BrandColors.SecondaryMediumGrey,
            disabledIndicatorColor  = BrandColors.SecondaryDarkerGrey,
            errorIndicatorColor     = MaterialTheme.colorScheme.error,

            /* text */
            focusedTextColor   = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            disabledTextColor  = BrandColors.SecondaryDarkerGrey,
            errorTextColor     = MaterialTheme.colorScheme.onSurface
        ),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        modifier = modifier.fillMaxWidth()
    )
}
