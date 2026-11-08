package com.example.phone_mobile.ui.screens.keypad

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import com.example.phone_mobile.ui.theme.GreenCall
import com.example.phone_mobile.ui.theme.PrimaryBlue
import com.example.phone_mobile.ui.theme.SearchBarBg

@Composable
fun KeypadScreen(
    initialPhoneNumber: String = "",
    contacts: List<Contact> = emptyList(),
    onMakeCall: (phoneNumber: String, contactName: String?) -> Unit,
    bottomBar: @Composable () -> Unit
) {
    var phoneNumber by remember { mutableStateOf(initialPhoneNumber) }

    LaunchedEffect(initialPhoneNumber) {
        if (initialPhoneNumber.isNotEmpty()) {
            phoneNumber = initialPhoneNumber
        }
    }

    // Tự động tìm kiếm liên hệ khớp với số điện thoại đang nhập
    val matchedContact = remember(phoneNumber, contacts) {
        val cleanNumber = phoneNumber.replace(" ", "")
        if (cleanNumber.isNotEmpty()) {
            contacts.find { it.phoneNumber.replace(" ", "") == cleanNumber }
        } else {
            null
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
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Màn hình hiển thị tên & số điện thoại người được gọi
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SearchBarBg, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Nếu khớp liên hệ, hiển thị tên liên hệ ở trên
                    if (matchedContact != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Text(
                                text = matchedContact.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                            if (matchedContact.isFavorite) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_star_filled),
                                    contentDescription = "Yêu thích",
                                    tint = Color(0xFFFFB800),
                                    modifier = Modifier
                                        .padding(start = 6.dp)
                                        .size(16.dp)
                                )
                            }
                        }
                    }

                    // Số điện thoại hiển thị to
                    Text(
                        text = if (phoneNumber.isEmpty()) " " else phoneNumber,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        letterSpacing = 1.sp,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                    KeypadButton(main = "1", sub = "") { phoneNumber += "1" }
                    KeypadButton(main = "2", sub = "ABC") { phoneNumber += "2" }
                    KeypadButton(main = "3", sub = "DEF") { phoneNumber += "3" }
                }

                // Hàng 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    KeypadButton(main = "4", sub = "GHI") { phoneNumber += "4" }
                    KeypadButton(main = "5", sub = "JKL") { phoneNumber += "5" }
                    KeypadButton(main = "6", sub = "MNO") { phoneNumber += "6" }
                }

                // Hàng 3
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    KeypadButton(main = "7", sub = "PQRS") { phoneNumber += "7" }
                    KeypadButton(main = "8", sub = "TUV") { phoneNumber += "8" }
                    KeypadButton(main = "9", sub = "WXYZ") { phoneNumber += "9" }
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

            Spacer(modifier = Modifier.height(8.dp))

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
                                onMakeCall(phoneNumber, matchedContact?.name)
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

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun KeypadButton(
    main: String,
    sub: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(74.dp)
            .clip(CircleShape)
            .background(Color(0xFFF1F3F4))
            .clickable(onClick = onClick),
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
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
