package com.example.phone_mobile.ui.screens.more

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phone_mobile.R
import com.example.phone_mobile.data.model.Contact
import com.example.phone_mobile.ui.theme.PrimaryBlue

@Composable
fun ScanSuccessScreen(
    contact: Contact,
    onViewDetail: (Contact) -> Unit,
    onBack: () -> Unit,
    bottomBar: @Composable () -> Unit
) {
    BackHandler {
        onBack()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            Surface(
                color = PrimaryBlue,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(64.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "Quay lại",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Thêm liên hệ từ QR",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        },
        bottomBar = bottomBar
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Vòng tròn checkmark xanh lá với các chấm confetti trang trí xung quanh chuẩn như ảnh
            Box(
                modifier = Modifier.size(190.dp),
                contentAlignment = Alignment.Center
            ) {
                // Các chấm pháo hoa / confetti trang trí xung quanh
                ConfettiDecoration(modifier = Modifier.fillMaxSize())

                // Vòng tròn xanh lá chính giữa
                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2ECC71)),
                    contentAlignment = Alignment.Center
                ) {
                    // Dấu tick trắng sắc nét
                    CheckmarkIcon(modifier = Modifier.size(54.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Tiêu đề: "Thêm liên hệ thành công!"
            Text(
                text = "Thêm liên hệ thành công!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Phụ đề: "Đã lưu liên hệ vào danh bạ của bạn"
            Text(
                text = "Đã lưu liên hệ vào danh bạ của bạn",
                fontSize = 15.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center
            )

            // Hiển thị tên và số điện thoại liên hệ vừa quét
            Spacer(modifier = Modifier.height(18.dp))
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF3F4F6),
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = contact.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = contact.phoneNumber,
                        fontSize = 14.sp,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(42.dp))

            // 1. Nút "Xem chi tiết" (PrimaryBlue)
            Button(
                onClick = { onViewDetail(contact) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(26.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Xem chi tiết",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Nút "Quay về" (Nút viền bo tròn)
            OutlinedButton(
                onClick = onBack,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = PrimaryBlue
                ),
                border = BorderStroke(1.5.dp, PrimaryBlue),
                shape = RoundedCornerShape(26.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Quay về",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBlue
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

/**
 * Vẽ dấu tick trắng bằng Canvas
 */
@Composable
private fun CheckmarkIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = 7.dp.toPx()
        val pathStart = Offset(size.width * 0.22f, size.height * 0.52f)
        val pathMid = Offset(size.width * 0.44f, size.height * 0.74f)
        val pathEnd = Offset(size.width * 0.80f, size.height * 0.32f)

        drawLine(
            color = Color.White,
            start = pathStart,
            end = pathMid,
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White,
            start = pathMid,
            end = pathEnd,
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

/**
 * Vẽ các tia và chấm confetti xung quanh biểu tượng thành công (chuẩn 100% như ảnh)
 */
@Composable
private fun ConfettiDecoration(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f

        // Tia ngắn bên trái trên
        drawLine(
            color = Color(0xFF60A5FA),
            start = Offset(cx - 72.dp.toPx(), cy - 40.dp.toPx()),
            end = Offset(cx - 86.dp.toPx(), cy - 48.dp.toPx()),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
        // Chấm vàng bên trái giữa
        drawCircle(
            color = Color(0xFFFBBF24),
            radius = 3.5.dp.toPx(),
            center = Offset(cx - 82.dp.toPx(), cy - 12.dp.toPx())
        )
        // Tia xanh dương bên trái dưới
        drawLine(
            color = Color(0xFF38BDF8),
            start = Offset(cx - 68.dp.toPx(), cy + 32.dp.toPx()),
            end = Offset(cx - 80.dp.toPx(), cy + 40.dp.toPx()),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Tia ngắn bên phải trên
        drawLine(
            color = Color(0xFF34D399),
            start = Offset(cx + 72.dp.toPx(), cy - 40.dp.toPx()),
            end = Offset(cx + 86.dp.toPx(), cy - 48.dp.toPx()),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
        // Chấm xanh lá / vàng bên phải giữa
        drawCircle(
            color = Color(0xFFA3E635),
            radius = 3.5.dp.toPx(),
            center = Offset(cx + 82.dp.toPx(), cy - 12.dp.toPx())
        )
        // Tia xanh cyan bên phải dưới
        drawLine(
            color = Color(0xFF38BDF8),
            start = Offset(cx + 68.dp.toPx(), cy + 32.dp.toPx()),
            end = Offset(cx + 80.dp.toPx(), cy + 40.dp.toPx()),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}
