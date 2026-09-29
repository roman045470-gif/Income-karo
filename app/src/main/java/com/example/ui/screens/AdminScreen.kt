package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.VipRequestEntity
import com.example.ui.components.MessageBanner
import com.example.ui.components.StatusBadge
import com.example.ui.components.TakaAppBar
import com.example.ui.model.EarningUiState
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldDark
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminScreen(
    state: EarningUiState,
    onBack: () -> Unit,
    onApproveVip: (requestId: Long) -> Unit,
    onRejectVip: (requestId: Long) -> Unit,
    onSetUserVip: (userId: Long, isVip: Boolean) -> Unit,
    onToggleBanUser: (userId: Long, isBanned: Boolean) -> Unit,
    onAdjustCoins: (userId: Long, newCoins: Int) -> Unit,
    onDeleteUser: (userId: Long) -> Unit,
    onUpdateWithdrawalStatus: (txId: Long, status: String) -> Unit,
    onDismissMessage: () -> Unit
) {
    BackHandler { onBack() }

    val user = state.currentUser ?: return
    val context = LocalContext.current

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Users, 1 = VIP Requests, 2 = Withdrawals, 3 = Ad Settings

    // User search & filter state
    var searchQuery by remember { mutableStateOf("") }
    var userFilterType by remember { mutableStateOf("ALL") } // ALL, VIP, FREE, BANNED

    // Dialog States
    var userToBan by remember { mutableStateOf<UserEntity?>(null) }
    var userToDelete by remember { mutableStateOf<UserEntity?>(null) }
    var userToAdjustCoins by remember { mutableStateOf<UserEntity?>(null) }
    var customCoinsInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        TakaAppBar(
            title = "এডমিন কন্ট্রোল প্যানেল",
            currentUser = user,
            showBackButton = true,
            onBackClick = onBack
        )

        MessageBanner(
            message = state.errorMessage ?: state.successMessage,
            isError = state.errorMessage != null,
            onDismiss = onDismissMessage
        )

        // Secret Admin Header Banner
        Surface(
            color = Slate900,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "গোপন এডমিন মোড সক্রিয় (কোড: 08520852)",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Surface(
                    color = EmeraldPrimary,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "মাস্টার এডমিন",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            // Metrics Summary Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminMetricCard(
                    title = "মোট ইউজার",
                    value = "${state.allUsers.size} জন",
                    color = Slate800,
                    modifier = Modifier.weight(1f)
                )
                AdminMetricCard(
                    title = "ভিআইপি",
                    value = "${state.allUsers.count { it.isVip }} জন",
                    color = GoldDark,
                    modifier = Modifier.weight(1f)
                )
                AdminMetricCard(
                    title = "পেন্ডিং VIP",
                    value = "${state.allVipRequests.count { it.status == "PENDING" }} টি",
                    color = if (state.allVipRequests.any { it.status == "PENDING" }) ErrorRed else EmeraldPrimary,
                    modifier = Modifier.weight(1f)
                )
                AdminMetricCard(
                    title = "পেন্ডিং ক্যাশ",
                    value = "${state.allWithdrawals.count { it.status == "PENDING" }} টি",
                    color = if (state.allWithdrawals.any { it.status == "PENDING" }) ErrorRed else EmeraldPrimary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Navigation Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = EmeraldPrimary
                    )
                },
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("ইউজার (${state.allUsers.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_admin_users")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("ভিআইপি (${state.allVipRequests.count { it.status == "PENDING" }})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_admin_vip")
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("উইথড্র (${state.allWithdrawals.count { it.status == "PENDING" }})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_admin_withdraw")
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("বিজ্ঞাপন", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_admin_ads")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (selectedTab) {
                0 -> {
                    // TAB 0: USERS MANAGEMENT
                    val filteredUsers = state.allUsers.filter { u ->
                        val matchesSearch = searchQuery.isBlank() ||
                            u.fullName.contains(searchQuery, ignoreCase = true) ||
                            u.phone.contains(searchQuery) ||
                            u.userIdCode.contains(searchQuery, ignoreCase = true) ||
                            u.referralCode.contains(searchQuery, ignoreCase = true)

                        val matchesFilter = when (userFilterType) {
                            "VIP" -> u.isVip
                            "FREE" -> !u.isVip
                            "BANNED" -> u.isBanned
                            else -> true
                        }
                        matchesSearch && matchesFilter
                    }

                    Column {
                        // Search bar
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("নাম, ফোন বা আইডি দিয়ে সার্চ...") },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null, tint = EmeraldPrimary)
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear")
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_user_search_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Filter chips row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("ALL" to "সব (${state.allUsers.size})", "VIP" to "ভিআইপি (${state.allUsers.count { it.isVip }})", "FREE" to "ফ্রি (${state.allUsers.count { !it.isVip }})", "BANNED" to "ব্যান্ড (${state.allUsers.count { it.isBanned }})").forEach { (type, label) ->
                                FilterChip(
                                    selected = userFilterType == type,
                                    onClick = { userFilterType = type },
                                    label = { Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (filteredUsers.isEmpty()) {
                            AdminEmptyCard("কোনো ইউজার খুঁজে পাওয়া যায়নি")
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(filteredUsers.size) { index ->
                                    val u = filteredUsers[index]
                                    val refCount = state.allReferrals.count { it.referrerCode == u.referralCode }
                                    AdminUserCard(
                                        user = u,
                                        referralCount = refCount,
                                        onToggleVip = { onSetUserVip(u.id, !u.isVip) },
                                        onBanClick = { userToBan = u },
                                        onAdjustCoinsClick = {
                                            userToAdjustCoins = u
                                            customCoinsInput = u.coins.toString()
                                        },
                                        onDeleteClick = { userToDelete = u }
                                    )
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // TAB 1: VIP REQUESTS
                    if (state.allVipRequests.isEmpty()) {
                        AdminEmptyCard("কোনো ভিআইপি রিকোয়েস্ট জমা হয়নি")
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(state.allVipRequests.size) { index ->
                                val req = state.allVipRequests[index]
                                AdminVipRequestCard(
                                    req = req,
                                    onApprove = { onApproveVip(req.id) },
                                    onReject = { onRejectVip(req.id) },
                                    onCopy = { text, label ->
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
                                        Toast.makeText(context, "$label কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 2: WITHDRAWALS
                    if (state.allWithdrawals.isEmpty()) {
                        AdminEmptyCard("কোনো উইথড্র রিকোয়েস্ট নেই")
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(state.allWithdrawals.size) { index ->
                                val wth = state.allWithdrawals[index]
                                AdminWithdrawalCard(
                                    tx = wth,
                                    onApprove = { onUpdateWithdrawalStatus(wth.id, "COMPLETED") },
                                    onReject = { onUpdateWithdrawalStatus(wth.id, "REJECTED") },
                                    onCopy = { text, label ->
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
                                        Toast.makeText(context, "$label কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }

                3 -> {
                    // TAB 3: AD SETTINGS
                    AdminAdSettingsCard()
                }
            }
        }
    }

    // Dialog 1: Ban / Unban User Confirmation Dialog
    if (userToBan != null) {
        val targetUser = userToBan!!
        val isBanning = !targetUser.isBanned
        AlertDialog(
            onDismissRequest = { userToBan = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isBanning) Icons.Default.Block else Icons.Default.Check,
                        contentDescription = null,
                        tint = if (isBanning) ErrorRed else SuccessGreen
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBanning) "ইউজার ব্যান্ড করবেন?" else "ইউজার আনব্যান্ড করবেন?",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Text(
                    text = if (isBanning)
                        "আপনি কি নিশ্চিত যে '${targetUser.fullName}' (${targetUser.phone}) এর একাউন্টটি ব্যান্ড করতে চান? ব্যান্ড করলে ইউজার আর লগইন বা কোনো টাস্ক সম্পন্ন করতে পারবে না।"
                    else
                        "আপনি কি '${targetUser.fullName}' এর একাউন্টটি পুনরায় সক্রিয় (আনব্যান্ড) করতে চান?",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onToggleBanUser(targetUser.id, isBanning)
                        userToBan = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isBanning) ErrorRed else SuccessGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (isBanning) "হ্যাঁ, ব্যান্ড করুন" else "আনব্যান্ড করুন")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { userToBan = null },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("বাতিল")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Dialog 2: Delete User Confirmation Dialog
    if (userToDelete != null) {
        val targetUser = userToDelete!!
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = ErrorRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "অ্যাকাউন্ট ডিলিট নিশ্চিত করুন", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(
                    text = "সতর্কতা: '${targetUser.fullName}' (${targetUser.phone}) এর অ্যাকাউন্টটি স্থায়ীভাবে মুছে যাবে। তার কয়েন, ট্রানজেকশন ও রেফারেল হিস্ট্রি চিরতরে মুছে ফেলা হবে। আপনি কি নিশ্চিত?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate700
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteUser(targetUser.id)
                        userToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("স্থায়ীভাবে ডিলিট করুন")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { userToDelete = null },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("বাতিল")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Dialog 3: Adjust User Coins
    if (userToAdjustCoins != null) {
        val targetUser = userToAdjustCoins!!
        AlertDialog(
            onDismissRequest = { userToAdjustCoins = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = GoldAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "কয়েন ব্যালেন্স সমন্বয়", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        text = "ইউজার: ${targetUser.fullName} (${targetUser.phone})",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "বর্তমান ব্যালেন্স: ${targetUser.coins} Coins (৳${targetUser.coins / 10.0} টাকা)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = customCoinsInput,
                        onValueChange = { customCoinsInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("নতুন মোট কয়েন সংখ্যা") },
                        placeholder = { Text("যেমন: 500") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("input_adjust_coins"),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newCoins = customCoinsInput.toIntOrNull() ?: targetUser.coins
                        onAdjustCoins(targetUser.id, newCoins)
                        userToAdjustCoins = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("সেভ করুন")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { userToAdjustCoins = null },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("বাতিল")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun AdminMetricCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = Color.Gray, maxLines = 1)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = color, maxLines = 1)
        }
    }
}

@Composable
fun AdminUserCard(
    user: UserEntity,
    referralCount: Int,
    onToggleVip: () -> Unit,
    onBanClick: () -> Unit,
    onAdjustCoinsClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val joinedStr = dateFormat.format(Date(user.createdAt))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_user_card_${user.id}"),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, if (user.isBanned) ErrorRed.copy(alpha = 0.5f) else Color(0xFFE2E8F0)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header with User Info & Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = if (user.isBanned) Color(0xFFFEE2E2) else if (user.isVip) Color(0xFFFEF3C7) else Color(0xFFF1F5F9),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = user.fullName.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = if (user.isBanned) ErrorRed else if (user.isVip) GoldDark else Slate800
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = user.fullName,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Slate900
                            )
                            if (user.isVip) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Surface(
                                    color = Color(0xFFFEF3C7),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "VIP",
                                        color = Color(0xFFB45309),
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "ফোন: ${user.phone}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldPrimary
                        )
                    }
                }

                // Account Status Badge
                Surface(
                    color = if (user.isBanned) Color(0xFFFEE2E2) else Color(0xFFDCFCE7),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (user.isBanned) "🚫 ব্যান্ড" else "✓ সক্রিয়",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (user.isBanned) ErrorRed else Color(0xFF15803D),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Details Box
            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "ব্যালেন্স: ${user.coins} Coins (৳${user.coins / 10.0})",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = Slate900
                        )
                        Text(
                            text = "রেফারেল: $referralCount জন",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF7C3AED)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "আইডি: ${user.userIdCode} • রেফার কোড: ${user.referralCode}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    if (user.address.isNotBlank() || user.email.isNotBlank()) {
                        Text(
                            text = "ঠিকানা: ${user.address.ifBlank { "N/A" }} • ${user.email.ifBlank { "" }}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                    Text(
                        text = "যোগদান: $joinedStr",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.LightGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Admin Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Adjust Coins
                OutlinedButton(
                    onClick = onAdjustCoinsClick,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("কয়েন", fontSize = 11.sp)
                }

                // Toggle VIP
                Button(
                    onClick = onToggleVip,
                    colors = ButtonDefaults.buttonColors(containerColor = if (user.isVip) Color(0xFFFEF3C7) else GoldDark),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (user.isVip) "VIP সরাও" else "VIP করো",
                        color = if (user.isVip) Color(0xFF78350F) else Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Ban / Unban
                Button(
                    onClick = onBanClick,
                    colors = ButtonDefaults.buttonColors(containerColor = if (user.isBanned) SuccessGreen else ErrorRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (user.isBanned) "আনব্যান্ড" else "ব্যান্ড",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Delete User
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete User",
                        tint = ErrorRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AdminVipRequestCard(
    req: VipRequestEntity,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onCopy: (text: String, label: String) -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val dateStr = dateFormat.format(Date(req.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_vip_card_${req.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${req.userName} (${req.userPhone})",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Slate900
                    )
                    Text(text = "মাধ্যম: ${req.method} • পরিমাণ: ৳৫৯৯", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                StatusBadge(status = req.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = Color(0xFFF1F5F9),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "প্রেরক নাম্বার: ${req.senderNumber}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { onCopy(req.senderNumber, "প্রেরক নাম্বার") }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Slate700, modifier = Modifier.size(14.dp))
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "TrxID: ${req.trxId}", style = MaterialTheme.typography.bodySmall, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { onCopy(req.trxId, "TrxID") }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                        }
                    }

                    if (req.screenshotNote.isNotBlank()) {
                        Text(text = "নোট: ${req.screenshotNote}", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                    }
                    Text(text = "সময়: $dateStr", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }

            if (req.status == "PENDING") {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1.5f).testTag("admin_approve_vip_${req.id}")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("অনুমোদন করুন (Approve)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onReject,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).testTag("admin_reject_vip_${req.id}")
                    ) {
                        Text("বাতিল (Reject)", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminWithdrawalCard(
    tx: TransactionEntity,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onCopy: (text: String, label: String) -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val dateStr = dateFormat.format(Date(tx.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_withdrawal_card_${tx.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${tx.paymentMethod} • ${tx.accountNumber}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Slate900
                        )
                        IconButton(onClick = { onCopy(tx.accountNumber ?: "", "অ্যাকাউন্ট নাম্বার") }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Slate700, modifier = Modifier.size(14.dp))
                        }
                    }
                    Text(
                        text = "পরিমাণ: ৳%.2f (${kotlin.math.abs(tx.coins)} Coins)".format(tx.bdtAmount),
                        style = MaterialTheme.typography.bodySmall,
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "সময়: $dateStr • TrxID: ${tx.trxId}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
                StatusBadge(status = tx.status)
            }

            if (tx.status == "PENDING") {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).testTag("admin_approve_withdrawal_${tx.id}")
                    ) {
                        Text("টাকা পাঠানো হয়েছে", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onReject,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).testTag("admin_reject_withdrawal_${tx.id}")
                    ) {
                        Text("বাতিল ও রিফান্ড", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAdSettingsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Tv, contentDescription = null, tint = EmeraldPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "বিজ্ঞাপন নেটওয়ার্ক সেটিংস",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Slate900
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = Color(0xFFFEF3C7),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "💡 বিজ্ঞাপন বসানোর প্রস্তুতি সম্পন্ন",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFFB45309)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "আপনার AdMob বা অন্যান্য বিজ্ঞাপন নেটওয়ার্কের App ID ও Rewarded Ad Unit ID প্রদান করলেই সরাসরি লাইভ ভিডিও বিজ্ঞাপন চালু হবে।",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = "ca-app-pub-3940256099942544~3347511713 (Test App ID)",
                onValueChange = {},
                readOnly = true,
                label = { Text("Google AdMob App ID") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = "ca-app-pub-3940256099942544/5224354917 (Rewarded Video Unit ID)",
                onValueChange = {},
                readOnly = true,
                label = { Text("Rewarded Video Unit ID") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                color = Color(0xFFF1F5F9),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "• প্রতি ভিডিও বিজ্ঞাপনে পুরস্কার: ২ কয়েন", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    Text(text = "• ফ্রি মেম্বার দৈনিক লিমিট: ৫০ টি বিজ্ঞাপন", style = MaterialTheme.typography.bodySmall)
                    Text(text = "• ভিআইপি মেম্বার লিমিট: আনলিমিটেড (অসীম)", style = MaterialTheme.typography.bodySmall, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AdminEmptyCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = text, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
    }
}
