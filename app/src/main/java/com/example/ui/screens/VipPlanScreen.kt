package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MessageBanner
import com.example.ui.components.StatusBadge
import com.example.ui.components.TakaAppBar
import com.example.ui.model.EarningUiState
import com.example.ui.theme.BkashPink
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldDark
import com.example.ui.theme.NagadOrange
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VipPlanScreen(
    state: EarningUiState,
    onBack: () -> Unit,
    onSubmitVipRequest: (method: String, senderNumber: String, trxId: String, note: String) -> Unit,
    onInstantTestApprove: (userId: Long) -> Unit,
    onDismissMessage: () -> Unit
) {
    BackHandler { onBack() }

    val user = state.currentUser ?: return
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var selectedMethod by remember { mutableStateOf("bKash") } // bKash or Nagad
    var senderNumber by remember { mutableStateOf("") }
    var trxId by remember { mutableStateOf("") }
    var screenshotNote by remember { mutableStateOf("") }

    val bkashNumber = "01921679459"
    val nagadNumber = "01921679459"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .imePadding()
    ) {
        TakaAppBar(
            title = "ভিআইপি মেম্বারশিপ প্ল্যান",
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
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Price & Status Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Slate900, Slate800, Color(0xFF78350F))
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
                                Surface(
                                    color = GoldAccent,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "প্রিমিয়াম সুবিধা",
                                        color = Color.Black,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "ভিআইপি গোল্ড মেম্বার",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "মূল্য",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.LightGray
                                )
                                Text(
                                    text = "৳৫৯৯",
                                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                                    color = GoldBright
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (user.isVip) {
                            Surface(
                                color = SuccessGreen.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DoneAll,
                                        contentDescription = null,
                                        tint = SuccessGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "আপনার একাউন্টে ভিআইপি প্ল্যান সক্রিয় আছে!",
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "• আনলিমিটেড ভিডিও বিজ্ঞাপন দেখার সুযোগ (ফ্রি মেম্বার দৈনিক ৫০ টি)\n• প্রতি ১ ঘন্টায় ২৫ কয়েন অটো মাইনিং\n• দ্রুততম প্রায়োরিটি উইথড্র সুবিধা\n• রেফারেল ও স্পিন বোনাস দেড়গুণ (১.৫x)",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f),
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val pendingVipReq = state.userVipRequests.firstOrNull { it.status == "PENDING" }

            if (user.isVip) {
                // Already VIP status banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFEF3C7),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = GoldDark, modifier = Modifier.size(32.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "আপনার ভিআইপি মেম্বারশিপ সক্রিয় রয়েছে!",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Slate900
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "আপনি আনলিমিটেড ভিডিও বিজ্ঞাপন ও প্রতি ঘন্টায় ২৫ কয়েন মাইনিং সুবিধা উপভোগ করছেন।",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else if (pendingVipReq != null) {
                // Pending Lock Card: Cannot submit again while pending!
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("card_vip_pending_status"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = Color(0xFFFEF3C7),
                                    shape = CircleShape,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = Color(0xFFB45309),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "ভিআইপি রিকোয়েস্ট পেন্ডিং আছে",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Slate900
                                    )
                                    Text(
                                        text = "এডমিন কর্তৃক যাচাই চলছে",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                            StatusBadge(status = "PENDING")
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "আপনার প্রেরিত রিকোয়েস্টের তথ্য:",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Slate900
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "• পেমেন্ট মাধ্যম: ${pendingVipReq.method}", style = MaterialTheme.typography.bodySmall)
                                Text(text = "• প্রেরক নাম্বার: ${pendingVipReq.senderNumber}", style = MaterialTheme.typography.bodySmall)
                                Text(text = "• ট্রানজেকশন আইডি: ${pendingVipReq.trxId}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                                Text(text = "• পরিমাণ: ৳৫৯৯.০০", style = MaterialTheme.typography.bodySmall)
                                if (pendingVipReq.screenshotNote.isNotBlank()) {
                                    Text(text = "• নোট/রেফারেন্স: ${pendingVipReq.screenshotNote}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            color = Color(0xFFFEF2F2),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "⚠️ আপনার রিকোয়েস্টটি পেন্ডিং অবস্থায় আছে। এডমিন যাচাই করে অনুমোদন দেওয়া পর্যন্ত নতুন কোনো রিকোয়েস্ট পাঠানো যাবে না। অনুগ্রহ করে অপেক্ষা করুন।",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = Color(0xFF991B1B)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                try {
                                    val url = "https://api.whatsapp.com/send?phone=8801921679459&text=" + Uri.encode("আসসালামু আলাইকুম অ্যাডমিন, আমার ভিআইপি রিকোয়েস্ট পেন্ডিং আছে। TrxID: ${pendingVipReq.trxId}, প্রেরক: ${pendingVipReq.senderNumber}, ইউজার আইডি: ${user.userIdCode}")
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(46.dp).testTag("btn_pending_vip_whatsapp")
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_whatsapp),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("দ্রুত ভেরিফিকেশনের জন্য WhatsApp করুন", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Step 1: Payment Instructions Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = EmeraldPrimary,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("১", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "৫৯৯ টাকা Send Money করুন",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Slate900
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // bKash Number Box
                        PaymentNumberCard(
                            title = "বিকাশ পার্সোনাল (Send Money)",
                            number = bkashNumber,
                            color = BkashPink,
                            onCopy = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("bKash Number", bkashNumber))
                                Toast.makeText(context, "বিকাশ নাম্বার কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Nagad Number Box
                        PaymentNumberCard(
                            title = "নগদ পার্সোনাল (Send Money)",
                            number = nagadNumber,
                            color = NagadOrange,
                            onCopy = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Nagad Number", nagadNumber))
                                Toast.makeText(context, "নগদ নাম্বার কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Step 2: Verification Submission Form
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = EmeraldPrimary,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("২", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "পেমেন্টের তথ্য জমা দিন",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Slate900
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Autofilled User info preview
                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("আপনার আইডি:", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                    Text(user.userIdCode, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Slate900)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("ইউজার নেম:", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                    Text(user.fullName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Slate900)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Method selector
                        Text("কোন মাধ্যমে টাকা পাঠিয়েছেন?", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MethodChip(
                                label = "বিকাশ (bKash)",
                                isSelected = selectedMethod == "bKash",
                                color = BkashPink,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedMethod = "bKash" }
                            )
                            MethodChip(
                                label = "নগদ (Nagad)",
                                isSelected = selectedMethod == "Nagad",
                                color = NagadOrange,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedMethod = "Nagad" }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = senderNumber,
                            onValueChange = { senderNumber = it },
                            label = { Text("যে নাম্বার থেকে টাকা পাঠিয়েছেন") },
                            placeholder = { Text("01XXXXXXXXX") },
                            leadingIcon = {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = EmeraldPrimary)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth().testTag("vip_sender_phone_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = trxId,
                            onValueChange = { trxId = it },
                            label = { Text("ট্রানজেকশন আইডি (TrxID)") },
                            placeholder = { Text("যেমন: 9J37D8QW") },
                            leadingIcon = {
                                Icon(Icons.Default.Receipt, contentDescription = null, tint = GoldAccent)
                            },
                            modifier = Modifier.fillMaxWidth().testTag("vip_trx_id_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = screenshotNote,
                            onValueChange = { screenshotNote = it },
                            label = { Text("স্ক্রিনশট / নোট / রেফারেন্স") },
                            placeholder = { Text("যেমন: ট্রানজেকশনের লাস্ট ৩ ডিজিট বা স্ক্রিনশট রেফারেন্স") },
                            modifier = Modifier.fillMaxWidth().testTag("vip_note_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                onSubmitVipRequest(selectedMethod, senderNumber, trxId, screenshotNote)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_submit_vip_request"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            enabled = !state.isLoading && senderNumber.isNotBlank() && trxId.isNotBlank()
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                            } else {
                                Text(
                                    text = "রিকোয়েস্ট সাবমিট করুন",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User's Submitted VIP Requests List
            if (state.userVipRequests.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "আপনার ভিআইপি রিকোয়েস্ট সমূহ",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Slate900
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        state.userVipRequests.forEach { req ->
                            VipRequestCard(req = req)
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentNumberCard(
    title: String,
    number: String,
    color: Color,
    onCopy: () -> Unit
) {
    Surface(
        color = color.copy(alpha = 0.08f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, style = MaterialTheme.typography.labelSmall, color = color)
                Text(text = number, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Slate900)
            }
            IconButton(
                onClick = onCopy,
                modifier = Modifier.testTag("copy_button_$number")
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "কপি করুন",
                    tint = color
                )
            }
        }
    }
}

@Composable
fun MethodChip(
    label: String,
    isSelected: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) color else Color(0xFFF1F5F9),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (isSelected) Color.White else Color.DarkGray,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun VipRequestCard(req: com.example.data.local.entity.VipRequestEntity) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val dateStr = dateFormat.format(Date(req.timestamp))

    Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${req.method} • Trx: ${req.trxId}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Slate900
                )
                Text(
                    text = "প্রেরক: ${req.senderNumber} • $dateStr",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            StatusBadge(status = req.status)
        }
    }
}
