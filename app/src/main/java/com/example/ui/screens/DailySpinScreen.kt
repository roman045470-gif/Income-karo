package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MessageBanner
import com.example.ui.components.TakaAppBar
import com.example.ui.model.EarningUiState
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldDark
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun DailySpinScreen(
    state: EarningUiState,
    onBack: () -> Unit,
    onSpinClaim: (coins: Int, onResult: (Int) -> Unit) -> Unit,
    onDismissMessage: () -> Unit
) {
    BackHandler { onBack() }

    val user = state.currentUser ?: return
    val scope = rememberCoroutineScope()

    val segments = listOf(10, 25, 15, 50, 20, 30)
    val segmentColors = listOf(
        Color(0xFF0F766E),
        Color(0xFFF59E0B),
        Color(0xFF0284C7),
        Color(0xFFE11D48),
        Color(0xFF10B981),
        Color(0xFF7C3AED)
    )

    val rotationAnim = remember { Animatable(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    var showRewardDialog by remember { mutableStateOf(false) }
    var wonCoinsAmount by remember { mutableIntStateOf(0) }

    // Check 24 hour cooldown
    val now = remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            now.longValue = System.currentTimeMillis()
            delay(1000L)
        }
    }

    val timeSinceLast = now.longValue - user.lastDailySpin
    val cooldownMs = 24 * 60 * 60 * 1000L // 24 hours
    val isCooldown = user.lastDailySpin > 0 && timeSinceLast < cooldownMs
    val remainingMs = if (isCooldown) cooldownMs - timeSinceLast else 0L

    val remainingHours = remainingMs / (1000 * 60 * 60)
    val remainingMinutes = (remainingMs % (1000 * 60 * 60)) / (1000 * 60)
    val remainingSeconds = (remainingMs % (1000 * 60)) / 1000

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        TakaAppBar(
            title = "দৈনিক লাকি স্পিন",
            currentUser = user,
            showBackButton = true,
            onBackClick = onBack
        )

        MessageBanner(
            message = state.errorMessage ?: state.successMessage,
            isError = state.errorMessage != null,
            onDismiss = onDismissMessage
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Info
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎯 প্রতিদিন ১ বার ফ্রি স্পিন",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Slate900
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (user.isVip) "ভিআইপি সদস্য হিসেবে স্পিনে দেড়গুণ (১.৫x) কয়েন পাবেন!"
                        else "প্রতিদিন স্পিন করে সর্বোচ্চ ৫০ কয়েন জিতে নিন!",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (user.isVip) GoldDark else Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Spinner Wheel Visual Area
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Spinning Wheel Canvas
                Canvas(
                    modifier = Modifier
                        .size(260.dp)
                        .rotate(rotationAnim.value)
                ) {
                    val radius = size.minDimension / 2f
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val sweepAngle = 360f / segments.size

                    // Draw Slices
                    for (i in segments.indices) {
                        val startAngle = i * sweepAngle
                        drawArc(
                            color = segmentColors[i % segmentColors.size],
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = true,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = Size(radius * 2, radius * 2)
                        )
                    }

                    // Outer border
                    drawCircle(
                        color = Color(0xFF0F172A),
                        radius = radius,
                        style = Stroke(width = 8f)
                    )

                    // Slice texts / labels
                    drawIntoCanvas { canvas ->
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = 36f
                            typeface = android.graphics.Typeface.DEFAULT_BOLD
                            textAlign = android.graphics.Paint.Align.CENTER
                        }

                        for (i in segments.indices) {
                            val midAngle = (i * sweepAngle + sweepAngle / 2f) * (PI / 180f)
                            val textDist = radius * 0.65f
                            val x = (center.x + textDist * cos(midAngle)).toFloat()
                            val y = (center.y + textDist * sin(midAngle)).toFloat() + 12f
                            canvas.nativeCanvas.drawText("${segments[i]}", x, y, paint)
                        }
                    }

                    // Center Hub circle
                    drawCircle(
                        color = Color.White,
                        radius = 28f
                    )
                    drawCircle(
                        color = Color(0xFF0F766E),
                        radius = 20f
                    )
                }

                // Top Pointer Pin
                Canvas(
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.TopCenter)
                ) {
                    val path = Path().apply {
                        moveTo(size.width / 2f, size.height)
                        lineTo(0f, 0f)
                        lineTo(size.width, 0f)
                        close()
                    }
                    drawPath(path, color = Color(0xFFE11D48))
                }
            }

            // Action / Cooldown Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isCooldown) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.HourglassTop,
                                contentDescription = null,
                                tint = GoldDark,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "আজকের স্পিন সম্পন্ন হয়েছে",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Slate900
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "পরবর্তী স্পিন: %02d:%02d:%02d পর".format(
                                    remainingHours,
                                    remainingMinutes,
                                    remainingSeconds
                                ),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFB45309),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "প্রতি ২৪ ঘন্টায় একবার বিনামূল্যে কয়েন জেতার সুযোগ থাকে। আগামীকাল আবার চেষ্টা করুন!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            text = "আপনার স্পিন তৈরি আছে!",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldPrimary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (!isSpinning) {
                                    isSpinning = true
                                    val winningIndex = Random.nextInt(segments.size)
                                    val winningCoins = segments[winningIndex]

                                    val segmentAngle = 360f / segments.size
                                    // Pointer is at Top (270 degrees).
                                    // Calculate target rotation
                                    val targetSegmentAngle = 270f - (winningIndex * segmentAngle + segmentAngle / 2f)
                                    val fullRotations = (5 + Random.nextInt(3)) * 360f
                                    val finalRotation = fullRotations + targetSegmentAngle

                                    scope.launch {
                                        rotationAnim.animateTo(
                                            targetValue = finalRotation,
                                            animationSpec = tween(
                                                durationMillis = 3500,
                                                easing = EaseOutCubic
                                            )
                                        )

                                        onSpinClaim(winningCoins) { awarded ->
                                            wonCoinsAmount = awarded
                                            showRewardDialog = true
                                            isSpinning = false
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("btn_spin_wheel"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            enabled = !isSpinning
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isSpinning) "স্পিন ঘুরছে..." else "স্পিন করুন (Spin Now)",
                                color = Color.Black,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    // Celebration Reward Dialog
    if (showRewardDialog) {
        AlertDialog(
            onDismissRequest = { showRewardDialog = false },
            confirmButton = {
                Button(
                    onClick = { showRewardDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("btn_collect_spin_reward")
                ) {
                    Text("কয়েন সংগ্রহ করুন", fontWeight = FontWeight.Bold)
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Celebration,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "অভিনন্দন!",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "আপনি দৈনিক স্পিন থেকে জিতেছেন",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = GoldDark,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "+$wonCoinsAmount Coins",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF78350F)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "কয়েন আপনার মূল ব্যালেন্সে যোগ করা হয়েছে!",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
}
