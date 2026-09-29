package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MessageBanner
import com.example.ui.components.TakaAppBar
import com.example.ui.model.EarningUiState
import com.example.ui.theme.BkashPink
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBright
import com.example.ui.theme.NagadOrange
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.SuccessGreen

data class SimulatedAd(
    val brand: String,
    val headline: String,
    val description: String,
    val bgColors: List<Color>,
    val badge: String
)

val adList = listOf(
    SimulatedAd(
        brand = "দারাজ বাংলাদেশ (Daraz)",
        headline = "১১.১১ মেগা শপিং ফেস্টিভ্যাল",
        description = "সেরা ব্র্যান্ডের গ্যাজেট ও পোশাকে ৭০% পর্যন্ত বিশাল মূল্যছাড় ও ফ্রি ডেলিভারি!",
        bgColors = listOf(Color(0xFFE11D48), Color(0xFF9F1239)),
        badge = "স্পনসর বিজ্ঞাপন"
    ),
    SimulatedAd(
        brand = "নগদ (Nagad)",
        headline = "ক্যাশ ইন করলেই ১০০% ক্যাশব্যাক!",
        description = "এখনই অ্যাপ ডাউনলোড করে প্রথম লেনদেনে উপভোগ করুন আকর্ষণীয় রিওয়ার্ড বোনাস।",
        bgColors = listOf(Color(0xFFEA580C), Color(0xFFC2410C)),
        badge = "স্পনসর বিজ্ঞাপন"
    ),
    SimulatedAd(
        brand = "ফুডপ্যান্ডা (Foodpanda)",
        headline = "পছন্দের খাবারে ৫০% ফ্ল্যাট ডিসকাউন্ট",
        description = "বিরিয়ানি, পিৎজা ও বার্গার অর্ডার করলেই দ্রুততম ডেলিভারি উপভোগ করুন।",
        bgColors = listOf(Color(0xFFDB2777), Color(0xFF9D174D)),
        badge = "স্পনসর বিজ্ঞাপন"
    ),
    SimulatedAd(
        brand = "বিকাশ (bKash)",
        headline = "সবার জন্য সহজ ও নিরাপদ লেনদেন",
        description = "মোবাইল রিচার্জ, সেন্ড মানি ও ইউটিলিটি বিল পেমেন্টে ক্যাশব্যাক অফার!",
        bgColors = listOf(Color(0xFFBE185D), Color(0xFF831843)),
        badge = "স্পনসর বিজ্ঞাপন"
    )
)

@Composable
fun WatchAdsScreen(
    state: EarningUiState,
    onBack: () -> Unit,
    onStartAd: () -> Unit,
    onClaimReward: () -> Unit,
    onCloseAd: () -> Unit,
    onDismissMessage: () -> Unit
) {
    BackHandler {
        if (state.isAdPlaying) {
            onCloseAd()
        } else {
            onBack()
        }
    }

    val user = state.currentUser ?: return
    val currentAd = adList[state.currentAdIndex % adList.size]

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        TakaAppBar(
            title = "ভিডিও দেখে আয়",
            currentUser = user,
            showBackButton = true,
            onBackClick = onBack
        )

        MessageBanner(
            message = state.errorMessage ?: state.successMessage,
            isError = state.errorMessage != null,
            onDismiss = onDismissMessage
        )

        if (state.isAdPlaying) {
            // FULLSCREEN VIDEO AD PLAYER VIEW
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top Bar inside Ad
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = GoldBright,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (state.canClaimAdReward) "সম্পূর্ণ হয়েছে!" else "${state.adSecondsRemaining} সেকেন্ড বাকি",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        Surface(
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = CircleShape
                        ) {
                            IconButton(
                                onClick = onCloseAd,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "বন্ধ করুন",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    // Simulated Video Canvas
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.verticalGradient(currentAd.bgColors))
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Surface(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = currentAd.badge,
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = currentAd.brand,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = currentAd.headline,
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = currentAd.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.85f),
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "ভিডিও স্পনসর প্লে হচ্ছে...",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }

                    // Bottom Controls & Claim Action
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val progress = (15 - state.adSecondsRemaining) / 15f
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = GoldAccent,
                            trackColor = Color.DarkGray
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (state.canClaimAdReward) {
                            Button(
                                onClick = onClaimReward,
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("btn_claim_ad_reward")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "🎉 ২ কয়েন যুক্ত হয়েছে! (পরবর্তী বিজ্ঞাপন)",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        } else {
                            Surface(
                                color = Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "পুরস্কার পেতে আরও ${state.adSecondsRemaining} সেকেন্ড অপেক্ষা করুন",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // NORMAL PREVIEW / TASK OVERVIEW
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFCCFBF1),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.OndemandVideo,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "বিজ্ঞাপন দেখে টাকা আয়",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Slate900
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (user.isVip)
                                "প্রতিটি ১৫ সেকেন্ডের বিজ্ঞাপন দেখলে পাবেন ২ কয়েন! ভিআইপি মেম্বার হিসেবে আপনি আনলিমিটেড বিজ্ঞাপন দেখতে পারবেন।"
                            else
                                "প্রতিটি ১৫ সেকেন্ডের বিজ্ঞাপন দেখলে পাবেন ২ কয়েন! ফ্রি মেম্বার হিসেবে দৈনিক সর্বোচ্চ ৫০ টি বিজ্ঞাপন দেখতে পারবেন।",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Surface(
                                color = if (!user.isVip && user.adsWatchedToday >= 50) Color(0xFFFEE2E2) else Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).padding(end = 6.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = if (user.isVip) "আজকের লিমিট (VIP)" else "আজকের লিমিট (ফ্রি)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = if (user.isVip) "${user.adsWatchedToday} টি (আনলিমিটেড)" else "${user.adsWatchedToday} / ৫০ টি",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (!user.isVip && user.adsWatchedToday >= 50) Color.Red else EmeraldPrimary
                                    )
                                }
                            }

                            Surface(
                                color = Color(0xFFFEF3C7),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).padding(start = 6.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "অ্যাড থেকে আয়",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFB45309)
                                    )
                                    Text(
                                        text = "${user.adsWatchedToday * 2} Coins",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF78350F)
                                    )
                                }
                            }
                        }
                    }
                }

                // Reward Showcase
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "রিওয়ার্ড রুলস",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Slate900
                            )
                            Text(
                                text = "১. পুরো ১৫ সেকেন্ড ভিডিও দেখতে হবে\n২. ভিডিও শেষ হলে 'কয়েন সংগ্রহ করুন' চাপুন\n৩. রেফারেল বোনাস ১০% প্রযোজ্য",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }

                val isLimitReached = !user.isVip && user.adsWatchedToday >= 50

                // Watch Button
                Button(
                    onClick = onStartAd,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("btn_watch_video_ad"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLimitReached) Color.Gray else EmeraldPrimary
                    ),
                    enabled = !isLimitReached
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isLimitReached)
                            "আজকের ৫০ টি বিজ্ঞাপন সম্পন্ন হয়েছে"
                        else
                            "ভিডিও অ্যাড দেখুন (+২ কয়েন)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
