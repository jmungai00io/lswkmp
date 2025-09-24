package com.lswmobile.app.ui.screens.statement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.lswmobile.app.network.model.Statement
import com.lswmobile.app.ui.components.ErrorDisplay
import com.lswmobile.app.ui.theme.AppIcons
import com.lswmobile.app.util.NumberFormatUtils
import com.lswmobile.app.viewmodel.StatementViewModel
import com.lswmobile.app.viewmodel.UserViewModel
import org.koin.compose.koinInject
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyStatementScreen(
    onNavigateBack: () -> Unit
) {
    val statementViewModel = koinInject<StatementViewModel>()
    val userViewModel = koinInject<UserViewModel>()

    val isLoading by statementViewModel.isLoading.collectAsState()
    val hasError by statementViewModel.hasError.collectAsState()
    val statements by statementViewModel.statements.collectAsState()
    val isDownloading by statementViewModel.isDownloading.collectAsState()
    val downloadError by statementViewModel.downloadError.collectAsState()
    val user by userViewModel.user.collectAsState()

    var query by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        statementViewModel.loadStatements()
    }

    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val currentYear = now.year
    val lastYear = currentYear - 1

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Statement") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = AppIcons.Filled.Back, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Actions row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    enabled = !isDownloading && user != null,
                    onClick = { user?.let { statementViewModel.downloadStatement(it._id) } }
                ) {
                    if (isDownloading) {
                        CircularProgressIndicator(modifier = Modifier.height(18.dp))
                    }
                    Text(" Export/Print")
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    enabled = !isDownloading,
                    onClick = { statementViewModel.downloadTaxCertificate(currentYear) }
                ) {
                    Icon(imageVector = AppIcons.Filled.Receipt, contentDescription = null)
                    Text(" Tax Cert $currentYear")
                }
                Button(
                    enabled = !isDownloading,
                    onClick = { statementViewModel.downloadTaxCertificate(lastYear) }
                ) {
                    Icon(imageVector = AppIcons.Filled.Receipt, contentDescription = null)
                    Text(" Tax Cert $lastYear")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search") },
                leadingIcon = { Icon(imageVector = AppIcons.Filled.Search, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onSearch = { focusManager.clearFocus() }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (hasError != null) {
                ErrorDisplay(message = hasError!!, onDismiss = { })
            }
            if (downloadError != null) {
                ErrorDisplay(message = downloadError!!, onDismiss = { })
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Text(
                    text = "Showing ${statements.size} items",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(4.dp))
                StatementList(statements = statements, query = query)
            }
        }
    }
}

@Composable
private fun StatementList(statements: List<Statement>, query: String) {
    val filtered = remember(statements, query) {
        if (query.isBlank()) statements else statements.filter { s ->
            val q = query.trim().lowercase()
            fun String?.m() = this?.lowercase() ?: ""
            s.reference.m().contains(q) ||
            s.note.m().contains(q) ||
            s.transactionType.m().contains(q) ||
            s.paymentRef.m().contains(q) ||
            s.label.m().contains(q)
        }
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(filtered) { entry ->
            StatementRow(entry)
            Divider()
        }
    }
}


fun formatStatementDate(raw: String?): String {
    if (raw.isNullOrBlank()) return ""
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    return runCatching {
        val instant = Instant.parse(raw)
        val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val day = local.dayOfMonth.toString().padStart(2, '0')
        val month = months[local.monthNumber - 1]
        val year = local.year
        val hour = local.hour.toString().padStart(2, '0')
        val minute = local.minute.toString().padStart(2, '0')
        "$day $month $year • $hour:$minute"
    }.getOrElse { raw }
}

@Composable
private fun StatementRow(entry: Statement) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(formatStatementDate(entry.dateOfTransaction), style = MaterialTheme.typography.bodyMedium)
            Text(NumberFormatUtils.formatCurrencyR(entry.balance), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(entry.reference)
            Text(entry.transactionType)
        }
        entry.paymentRef?.takeIf { it.isNotBlank() }?.let { value ->
            Text(value, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        entry.note?.takeIf { it.isNotBlank() }?.let { value ->
            Text(value, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            val amountStr = NumberFormatUtils.formatCurrencyR(entry.amount)
            if (entry.amount < 0) {
                Text("Credit: ${amountStr}")
                Text("Debit: ")
            } else {
                Text("Credit: ")
                Text("Debit: ${amountStr}")
            }
        }
    }
}
