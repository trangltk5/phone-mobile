package com.example.phone_mobile.ui.screens.keypad

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phone_mobile.R
import com.example.phone_mobile.data.model.Contact
import com.example.phone_mobile.data.model.EmergencyContact
import com.example.phone_mobile.ui.theme.GreenCall
import com.example.phone_mobile.ui.theme.PrimaryBlue
import com.example.phone_mobile.ui.theme.SearchBarBg

data class MatchContactItem(
    val name: String,
    val phoneNumber: String
)

@Composable
fun KeypadScreen(
    initialPhoneNumber: String = "",
    contacts: List<Contact> = emptyList(),
    emergencyContacts: List<EmergencyContact> = emptyList(),
    onMakeCall: (phoneNumber: String, contactName: String?) -> Unit,
    bottomBar: @Composable () -> Unit
) {
    var phoneNumber by remember { mutableStateOf(initialPhoneNumber) }
    var showOtherMatchesDialog by remember { mutableStateOf(false) }

    LaunchedEffect(initialPhoneNumber) {
        if (initialPhoneNumber.isNotEmpty()) {
            phoneNumber = initialPhoneNumber
        }
    }

    val cleanInput = phoneNumber.replace(" ", "")

    // Tự động tìm kiếm & gợi ý danh bạ khớp số hoặc tên (chuẩn ảnh 2 & 3)
    val matchedContactsList = remember(phoneNumber, contacts, emergencyContacts) {
        if (cleanInput.isEmpty()) {
            emptyList()
        } else {
            val result = mutableListOf<MatchContactItem>()

            // 1. Nếu gõ 1 chữ số (vd: 1 -> Bố), ưu tiên đưa liên hệ Gọi nhanh lên đầu
            if (cleanInput.length == 1 && cleanInput[0].isDigit()) {
                val key = cleanInput[0].digitToInt()
                val speedDial = emergencyContacts.find { it.speedDialKey == key }
                if (speedDial != null) {
                    result.add(MatchContactItem(name = speedDial.name, phoneNumber = speedDial.phoneNumber))
                }
            }

            // 2. Tìm kiếm trong danh bạ khớp số điện thoại hoặc tên
            val fromContacts = contacts.filter { contact ->
                val cleanContactPhone = contact.phoneNumber.replace(" ", "")
                cleanContactPhone.contains(cleanInput) ||
                    contact.name.contains(cleanInput, ignoreCase = true)
            }.map { MatchContactItem(name = it.name, phoneNumber = it.phoneNumber) }

            for (item in fromContacts) {
                if (result.none { it.phoneNumber.replace(" ", "") == item.phoneNumber.replace(" ", "") }) {
                    result.add(item)
                }
            }
            result
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            Surface(
                color = PrimaryBlue,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(64.dp)
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "Bàn phím",
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Khu vực hiển thị số & gợi ý (đặt gọn gàng ở trên, sát thanh tiêu đề)
            Spacer(modifier = Modifier.height(16.dp))

            // Số điện thoại hiển thị to ở trên
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (phoneNumber.isEmpty()) " " else phoneNumber,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    letterSpacing = 1.sp,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Thẻ gợi ý tên kèm số ở dưới (như ảnh 2 & ảnh 3)
            if (matchedContactsList.isNotEmpty()) {
                val topMatch = matchedContactsList.first()
                val otherMatchesCount = matchedContactsList.size - 1

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFF1F3F4),
                    modifier = Modifier.fillMaxWidth(0.92f)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Dòng 1: Icon user + Tên + Số điện thoại (Ảnh 2)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onMakeCall(topMatch.phoneNumber, topMatch.name)
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_user_avatar),
                                contentDescription = null,
                                tint = Color(0xFF4B5563),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = topMatch.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = topMatch.phoneNumber,
                                fontSize = 15.sp,
                                color = Color(0xFF4B5563),
                                maxLines = 1
                            )
                        }

                        // Dòng 2: Icon kính lúp + "X kết quả khác" (Ảnh 3)
                        if (otherMatchesCount > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Color(0xFFE2E8F0))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showOtherMatchesDialog = true },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_search),
                                    contentDescription = null,
                                    tint = Color(0xFF6B7280),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$otherMatchesCount kết quả khác",
                                    fontSize = 13.sp,
                                    color = Color(0xFF6B7280),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Khoảng đệm co giãn linh hoạt ở giữa gợi ý và bàn phím:
            // Đảm bảo bàn phím luôn cố định ở đáy, không bao giờ bị xê dịch khi gõ hay khi có gợi ý xuất hiện
            Spacer(modifier = Modifier.weight(1f))

            // 2. Khu vực bàn phím số & nút gọi cố định ở đáy (Tuyệt đối không bị dịch chuyển)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Hàm tra cứu gọi nhanh cho phím số
                val getSpeedDial = { key: Int -> emergencyContacts.find { it.speedDialKey == key } }

                // Bàn phím số 3x4
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Hàng 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        val sd1 = getSpeedDial(1)
                        KeypadButton(
                            main = "1",
                            sub = "",
                            onLongClick = if (sd1 != null) { { onMakeCall(sd1.phoneNumber, sd1.name) } } else null
                        ) { phoneNumber += "1" }

                        val sd2 = getSpeedDial(2)
                        KeypadButton(
                            main = "2",
                            sub = "ABC",
                            onLongClick = if (sd2 != null) { { onMakeCall(sd2.phoneNumber, sd2.name) } } else null
                        ) { phoneNumber += "2" }

                        val sd3 = getSpeedDial(3)
                        KeypadButton(
                            main = "3",
                            sub = "DEF",
                            onLongClick = if (sd3 != null) { { onMakeCall(sd3.phoneNumber, sd3.name) } } else null
                        ) { phoneNumber += "3" }
                    }

                    // Hàng 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        val sd4 = getSpeedDial(4)
                        KeypadButton(
                            main = "4",
                            sub = "GHI",
                            onLongClick = if (sd4 != null) { { onMakeCall(sd4.phoneNumber, sd4.name) } } else null
                        ) { phoneNumber += "4" }

                        val sd5 = getSpeedDial(5)
                        KeypadButton(
                            main = "5",
                            sub = "JKL",
                            onLongClick = if (sd5 != null) { { onMakeCall(sd5.phoneNumber, sd5.name) } } else null
                        ) { phoneNumber += "5" }

                        val sd6 = getSpeedDial(6)
                        KeypadButton(
                            main = "6",
                            sub = "MNO",
                            onLongClick = if (sd6 != null) { { onMakeCall(sd6.phoneNumber, sd6.name) } } else null
                        ) { phoneNumber += "6" }
                    }

                    // Hàng 3
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        val sd7 = getSpeedDial(7)
                        KeypadButton(
                            main = "7",
                            sub = "PQRS",
                            onLongClick = if (sd7 != null) { { onMakeCall(sd7.phoneNumber, sd7.name) } } else null
                        ) { phoneNumber += "7" }

                        val sd8 = getSpeedDial(8)
                        KeypadButton(
                            main = "8",
                            sub = "TUV",
                            onLongClick = if (sd8 != null) { { onMakeCall(sd8.phoneNumber, sd8.name) } } else null
                        ) { phoneNumber += "8" }

                        val sd9 = getSpeedDial(9)
                        KeypadButton(
                            main = "9",
                            sub = "WXYZ",
                            onLongClick = if (sd9 != null) { { onMakeCall(sd9.phoneNumber, sd9.name) } } else null
                        ) { phoneNumber += "9" }
                    }

                    // Hàng 4
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        KeypadButton(main = "*", sub = "") { phoneNumber += "*" }
                        KeypadButton(main = "0", sub = "+") { phoneNumber += "0" }
                        KeypadButton(main = "#", sub = "") { phoneNumber += "#" }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Hàng phím Gọi điện & Xóa
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Spacer(modifier = Modifier.size(48.dp))

                    // Nút gọi điện màu xanh lá
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(GreenCall)
                            .clickable {
                                if (phoneNumber.isNotEmpty()) {
                                    if (matchedContactsList.isNotEmpty()) {
                                        val target = matchedContactsList.first()
                                        if (cleanInput.length <= 4) {
                                            onMakeCall(target.phoneNumber, target.name)
                                        } else {
                                            onMakeCall(phoneNumber, target.name)
                                        }
                                    } else {
                                        onMakeCall(phoneNumber, null)
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_call),
                            contentDescription = "Gọi",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Phím xóa lùi
                    Box(
                        modifier = Modifier.size(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (phoneNumber.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    if (phoneNumber.isNotEmpty()) {
                                        phoneNumber = phoneNumber.dropLast(1)
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_backspace),
                                    contentDescription = "Xóa ký tự",
                                    tint = Color.Black,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Hộp thoại hiển thị danh sách các kết quả gợi ý khác (chuẩn như ảnh 3)
    if (showOtherMatchesDialog) {
        Dialog(onDismissRequest = { showOtherMatchesDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Kết quả gợi ý (${matchedContactsList.size})",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        IconButton(onClick = { showOtherMatchesDialog = false }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_close),
                                contentDescription = "Đóng",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(matchedContactsList) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        showOtherMatchesDialog = false
                                        onMakeCall(item.phoneNumber, item.name)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFF1F3F4)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_user_avatar),
                                            contentDescription = null,
                                            tint = Color.Gray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = item.name,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.Black
                                        )
                                        Text(
                                            text = item.phoneNumber,
                                            fontSize = 13.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEBF3FE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_call),
                                        contentDescription = "Gọi",
                                        tint = PrimaryBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun KeypadButton(
    main: String,
    sub: String,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(74.dp)
            .clip(CircleShape)
            .background(Color(0xFFF1F3F4))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = main,
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                lineHeight = 30.sp
            )
            if (sub.isNotEmpty()) {
                Text(
                    text = sub,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    letterSpacing = 1.sp,
                    maxLines = 1
                )
            }
        }
    }
}
