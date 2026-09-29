package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TransactionEntity
import com.example.ui.components.MessageBanner
import com.example.ui.components.StatusBadge
import com.example.ui.components.TakaAppBar
import com.example.ui.model.EarningUiState
import com.example.ui.theme.BkashPink
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBright
import com.example.ui.theme.NagadOrange
import com.example.ui.theme.RocketPurple
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WithdrawScreen(
    state: EarningUiState,
    onBack: () -> Unit,
    onSubmitWithdrawal: (method: String, accountNumber: String, coins: Int) -> Unit,
    onDismissMessage: () -> Unit
) {
    BackHandler { onBack() }

    val user = state.currentUser ?: return

    var selectedMethod by remember { mutableStateOf("bKash") }
    var accountNumber by remember { mutableStateOf(user.phone) }
    var coinAmountText by remember { mutableStateOf("1000") }

    val parsedCoins = coinAmountText.toIntOrNull() ?: 0
    val bdtEquivalent = parsedCoins / 10.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .imePadding()
    ) {
        TakaAppBar(
            title = "টাকা উত্তোলন (Withdraw)",
            currentUser = user,
            showBackButton = true,
            onBackClick = onBack
        )

        MessageBanner(
            message = state.errorMessage ?: state.successMessage,
            isError = state.errorMessage != null,
            onDismiss = onDismissMessage
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Balance Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.linearGradient(
                                    listOf(EmeraldDark, EmeraldPrimary)
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "বর্তমান ব্যালেন্স",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.MonetizationOn,
                                            contentDescription = null,
                                            tint = GoldBright,
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "%,d Coins".format(user.coins),
                                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                                            color = Color.White
                                        )
                                    }
                                }

                                Surface(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.End,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text("টাকা সমমূল্য", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f))
                                        Text("৳%.2f".format(user.coins / 10.0), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = GoldBright)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "💡 ১,০০০ কয়েন = ১০০ টাকা (সর্বনিম্ন উইথড্র ১০০০ কয়েন)",
                                style = MaterialTheme.typography.bodySmall,
                                color = GoldBright
                            )
                        }
                    }
                }
            }

            // Withdrawal Form Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "উইথড্র মাধ্যম নির্বাচন করুন",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Slate900
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Method selection buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MethodSelectionChip(
                                label = "বিকাশ",
                                isSelected = selectedMethod == "bKash",
                                color = BkashPink,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedMethod = "bKash" }
                            )
                            MethodSelectionChip(
                                label = "নগদ",
                                isSelected = selectedMethod == "Nagad",
                                color = NagadOrange,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedMethod = "Nagad" }
                            )
                            MethodSelectionChip(
                                label = "রকেট",
                                isSelected = selectedMethod == "Rocket",
                                color = RocketPurple,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedMethod = "Rocket" }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = accountNumber,
                            onValueChange = { accountNumber = it },
                            label = { Text("$selectedMethod নাম্বার") },
                            placeholder = { Text("01XXXXXXXXX") },
                            leadingIcon = {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = EmeraldPrimary)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth().testTag("withdraw_account_number_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = coinAmountText,
                            onValueChange = { coinAmountText = it },
                            label = { Text("কয়েন পরিমাণ (কমপক্ষে ১০০০)") },
                            placeholder = { Text("1000") },
                            leadingIcon = {
                                Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = GoldAccent)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("withdraw_coins_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick chips for 1000, 2000, 5000, Max
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            QuickCoinChip("1000 Coins", modifier = Modifier.weight(1f)) { coinAmountText = "1000" }
                            QuickCoinChip("2000 Coins", modifier = Modifier.weight(1f)) { coinAmountText = "2000" }
                            QuickCoinChip("সব কয়েন", modifier = Modifier.weight(1f)) { coinAmountText = user.coins.toString() }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Conversion Result Banner
                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "আপনি পাবেন:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.DarkGray
                                )
                                Text(
                                    text = "৳%.2f টাকা".format(bdtEquivalent),
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = EmeraldPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        val isValid = parsedCoins >= 1000 && parsedCoins <= user.coins && accountNumber.length >= 11

                        Button(
                            onClick = {
                                onSubmitWithdrawal(selectedMethod, accountNumber, parsedCoins)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("btn_submit_withdrawal"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            enabled = !state.isLoading && isValid
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                            } else {
                                Text(
                                    text = "উইথড্র রিকোয়েস্ট পাঠান",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (parsedCoins < 1000) {
                            Text(
                                text = "* সর্বনিম্ন ১০০০ কয়েন উইথড্র করা যাবে (১০০ টাকা)",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        } else if (parsedCoins > user.coins) {
                            Text(
                                text = "* পর্যাপ্ত ব্যালেন্স নেই!",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Red,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }
                }
            }

            // User's Withdrawals List
            item {
                Text(
                    text = "উইথড্র হিস্ট্রি",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Slate900,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            if (state.userWithdrawals.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Text(
                            text = "এখনো কোনো উইথড্র রিকোয়েস্ট করা হয়নি",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }
            } else {
                items(state.userWithdrawals.size) { index ->
                    val wth = state.userWithdrawals[index]
                    WithdrawalHistoryCard(tx = wth)
                }
            }
        }
    }
}

@Composable
fun MethodSelectionChip(
    label: String,
    isSelected: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) color else Color(0xFFF1F5F9),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (isSelected) Color.White else Color.DarkGray,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun QuickCoinChip(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = Color(0xFFF1F5F9),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun WithdrawalHistoryCard(tx: TransactionEntity) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val dateStr = dateFormat.format(Date(tx.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${tx.paymentMethod} • ${tx.accountNumber}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Slate900
                )
                Text(
                    text = "$dateStr • ID: ${tx.trxId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                StatusBadge(status = tx.status)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "৳%.2f".format(tx.bdtAmount),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = EmeraldPrimary
                )
                Text(
                    text = "${tx.coins} Coins",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}
