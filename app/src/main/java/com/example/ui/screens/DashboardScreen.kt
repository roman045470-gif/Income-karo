package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.net.Uri
import com.example.R
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserEntity
import com.example.ui.components.BalanceCard
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
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    state: EarningUiState,
    onNavigateToSpin: () -> Unit,
    onNavigateToAds: () -> Unit,
    onNavigateToMining: () -> Unit,
    onNavigateToReferral: () -> Unit,
    onNavigateToVip: () -> Unit,
    onNavigateToWithdraw: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onDismissMessage: () -> Unit
) {
    val user = state.currentUser ?: return
    val context = LocalContext.current

    val onWhatsAppClick = {
        try {
            val url = "https://api.whatsapp.com/send?phone=8801921679459&text=" + Uri.encode("আসসালামু আলাইকুম, টাকা ইনকাম অ্যাপে আমার সহায়তা প্রয়োজন। ইউজার আইডি: ${user.userIdCode}")
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:01921679459"))
                context.startActivity(dialIntent)
            } catch (_: Exception) {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        TakaAppBar(
            title = "টাকা ইনকাম",
            currentUser = user,
            showBackButton = false,
            onProfileClick = onNavigateToProfile,
            onAdminClick = onNavigateToAdmin,
            onWhatsAppClick = onWhatsAppClick
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                MessageBanner(
                    message = state.errorMessage ?: state.successMessage,
                    isError = state.errorMessage != null,
                    onDismiss = onDismissMessage
                )
            }

            // User Welcome & VIP Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "হ্যালো, ${user.fullName} 👋",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Slate900
                        )
                        Text(
                            text = "আজকের ইনকাম শুরু করুন",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    Surface(
                        color = if (user.isVip) GoldAccent.copy(alpha = 0.2f) else Color(0xFFE2E8F0),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.clickable { if (!user.isVip) onNavigateToVip() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = if (user.isVip) Icons.Default.WorkspacePremium else Icons.Default.Stars,
                                contentDescription = null,
                                tint = if (user.isVip) GoldAccent else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (user.isVip) "ভিআইপি গোল্ড সদস্য" else "ফ্রি মেম্বার (আপগ্রেড)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (user.isVip) Color(0xFFB45309) else Color.DarkGray
                            )
                        }
                    }
                }
            }

            // Balance Card
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    BalanceCard(
                        user = user,
                        onWithdrawClick = onNavigateToWithdraw,
                        onVipClick = onNavigateToVip,
                        onHistoryClick = onNavigateToHistory
                    )
                }
            }

            // Promotional Hero Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { onNavigateToVip() },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Box(modifier = Modifier.height(130.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.banner_earning),
                            contentDescription = "Earning Banner",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.85f),
                                            Color.Black.copy(alpha = 0.4f),
                                            Color.Transparent
                                        )
                                    )
                                )
                                .padding(16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Column(modifier = Modifier.fillMaxWidth(0.7f)) {
                                Surface(
                                    color = BkashPink,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "স্পেশাল অফার",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "ভিআইপি মেম্বারশিপে প্রতি ঘন্টায় ২৫ কয়েন মাইনিং!",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "বিকাশ ও নগদে ৳৫৯৯ দিয়ে সক্রিয় করুন",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GoldBright
                                )
                            }
                        }
                    }
                }
            }

            // Earning Sections Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "টাকা আয়ের ৪টি মাধ্যম",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Slate900
                    )
                    Surface(
                        color = EmeraldLight.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "১০০% নিরাপদ ও ভেরিফাইড",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // 4 Distinct Unique Options in 2x2 Grid Layout
            // Row 1: Daily Lucky Spin & Watch Video Ads
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Option 1: Daily Spin
                    UniqueGridEarningCard(
                        modifier = Modifier.weight(1f),
                        title = "দৈনিক স্পিন",
                        subtitle = "ফ্রি স্পিন ঘুরান",
                        rewardText = "১০ - ৫০ Coins",
                        badgeText = "দৈনিক ১ বার",
                        badgeBg = Color(0xFFFEF3C7),
                        badgeColor = Color(0xFFB45309),
                        cardGradient = listOf(Color(0xFFFFFBEB), Color(0xFFFEF3C7)),
                        borderColor = Color(0xFFFDE68A),
                        icon = Icons.Default.TrackChanges,
                        iconTint = GoldAccent,
                        iconBg = Color(0xFFFEF08A),
                        buttonText = "স্পিন খেলুন",
                        testTag = "task_card_spin",
                        onClick = onNavigateToSpin
                    )

                    // Option 2: Watch Video Ads
                    UniqueGridEarningCard(
                        modifier = Modifier.weight(1f),
                        title = "বিজ্ঞাপন দেখুন",
                        subtitle = if (user.isVip) "VIP: আনলিমিটেড" else "ফ্রি: ৫০ টি/দিন",
                        rewardText = "+২ Coins / ভিডিও",
                        badgeText = if (user.isVip) "VIP আনলিমিটেড" else "৫০ টি / দিন",
                        badgeBg = Color(0xFFCCFBF1),
                        badgeColor = EmeraldPrimary,
                        cardGradient = listOf(Color(0xFFF0FDF4), Color(0xFFDCFCE7)),
                        borderColor = Color(0xFFBBF7D0),
                        icon = Icons.Default.OndemandVideo,
                        iconTint = EmeraldPrimary,
                        iconBg = Color(0xFFA7F3D0),
                        buttonText = "ভিডিও দেখুন",
                        testTag = "task_card_ads",
                        onClick = onNavigateToAds
                    )
                }
            }

            // Row 2: Hourly Mining & Refer & Earn
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Option 3: Hourly Mining
                    UniqueGridEarningCard(
                        modifier = Modifier.weight(1f),
                        title = "ভিআইপি মাইনিং",
                        subtitle = if (user.isVip) "১ ঘন্টা পর পর চালু" else "ভিআইপি সুবিধা",
                        rewardText = "২৫ Coins / ঘন্টা",
                        badgeText = if (user.isVip) "সক্রিয় মাইনিং" else "৳৫৯৯ VIP",
                        badgeBg = if (user.isVip) Color(0xFFE0E7FF) else Color(0xFFFEE2E2),
                        badgeColor = if (user.isVip) Color(0xFF4338CA) else Color(0xFFDC2626),
                        cardGradient = listOf(Color(0xFFEEF2FF), Color(0xFFE0E7FF)),
                        borderColor = Color(0xFFC7D2FE),
                        icon = Icons.Default.Speed,
                        iconTint = Color(0xFF4F46E5),
                        iconBg = Color(0xFFC7D2FE),
                        buttonText = if (user.isVip) "মাইনিং চালু" else "VIP নিন",
                        testTag = "task_card_mining",
                        onClick = onNavigateToMining
                    )

                    // Option 4: Refer & Earn
                    UniqueGridEarningCard(
                        modifier = Modifier.weight(1f),
                        title = "রেফার ও ইনভাইট",
                        subtitle = "বন্ধুর ইনকামের ১০%",
                        rewardText = "১০% লাইফটাইম",
                        badgeText = "১০% কমিশন",
                        badgeBg = Color(0xFFEDE9FE),
                        badgeColor = Color(0xFF7C3AED),
                        cardGradient = listOf(Color(0xFFFAF5FF), Color(0xFFF3E8FF)),
                        borderColor = Color(0xFFE9D5FF),
                        icon = Icons.Default.Share,
                        iconTint = Color(0xFF7C3AED),
                        iconBg = Color(0xFFDDD6FE),
                        buttonText = "রেফার করুন",
                        testTag = "task_card_referral",
                        onClick = onNavigateToReferral
                    )
                }
            }

            // WhatsApp Support Card (Helpline: 01921679459)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { onWhatsAppClick() }
                        .testTag("card_whatsapp_support"),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFF25D366).copy(alpha = 0.5f)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFDCFCE7),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_whatsapp),
                                    contentDescription = "WhatsApp",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "WhatsApp হেল্পলাইন ও সাপোর্ট",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Slate900
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = Color(0xFF25D366),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "২৪/৭ একটিভ",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "উইথড্র বা ভিআইপি সংক্রান্ত সমস্যায় সরাসরি মেসেজ করুন",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "WhatsApp: 01921679459",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF15803D)
                            )
                        }

                        Surface(
                            color = Color(0xFF25D366),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = "মেসেজ দিন",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }

            // Recent Transactions Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "সাম্প্রতিক লেনদেন",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Slate900
                    )
                    TextButton(onClick = onNavigateToHistory) {
                        Text(
                            text = "সবগুলো দেখুন",
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (state.userTransactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "এখনো কোনো ইনকাম লেনদেন সম্পন্ন হয়নি",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "ওপরের অপশনগুলো থেকে এখনই কয়েন আয় শুরু করুন!",
                                style = MaterialTheme.typography.bodySmall,
                                color = EmeraldPrimary
                            )
                        }
                    }
                }
            } else {
                val previewList = state.userTransactions.take(3)
                items(previewList.size) { index ->
                    val tx = previewList[index]
                    TransactionItemCard(tx = tx)
                }
            }
        }
    }
}

@Composable
fun UniqueGridEarningCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    rewardText: String,
    badgeText: String,
    badgeBg: Color,
    badgeColor: Color,
    cardGradient: List<Color>,
    borderColor: Color,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    buttonText: String,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(cardGradient))
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top row with Icon and Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = iconBg,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = iconTint,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Surface(
                        color = badgeBg,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                            color = badgeColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            maxLines = 1
                        )
                    }
                }

                // Middle: Title & Subtitle
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Slate900,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = Color.DarkGray,
                        maxLines = 1
                    )
                }

                // Bottom: Reward and Mini-CTA button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = rewardText,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = iconTint
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = iconTint
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = buttonText,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItemCard(tx: TransactionEntity) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val dateStr = dateFormat.format(Date(tx.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tx.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Slate900
                )
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                if (tx.status != "COMPLETED") {
                    Spacer(modifier = Modifier.height(4.dp))
                    StatusBadge(status = tx.status)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                val isPositive = tx.coins > 0
                Text(
                    text = if (isPositive) "+${tx.coins} Coins" else "${tx.coins} Coins",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isPositive) SuccessGreen else BkashPink
                )
                Text(
                    text = if (tx.bdtAmount > 0) "৳%.2f".format(tx.bdtAmount) else "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}
