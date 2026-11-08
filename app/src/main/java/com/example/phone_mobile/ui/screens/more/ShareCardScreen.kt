package com.example.phone_mobile.ui.screens.more

import android.widget.Toast
import com.example.phone_mobile.util.NotificationManager
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.window.Dialog
import com.example.phone_mobile.R
import com.example.phone_mobile.data.model.Contact
import com.example.phone_mobile.data.model.EmergencyContact
import com.example.phone_mobile.ui.screens.emergency.EmergencyContactsScreen
import com.example.phone_mobile.ui.theme.PrimaryBlue

@Composable
fun ShareCardScreen(
    emergencyContacts: List<EmergencyContact> = emptyList(),
    allContacts: List<Contact> = emptyList(),
    onUpdateEmergencyContacts: (List<EmergencyContact>) -> Unit = {},
    onMakeCall: (phoneNumber: String, contactName: String?) -> Unit = { _, _ -> },
    onImportScannedContact: (Contact) -> Unit = {},
    onViewContactDetail: (Contact) -> Unit = {},
    bottomBar: @Composable () -> Unit
) {
    val context = LocalContext.current

    // Thông tin danh thiếp cá nhân của người dùng (mặc định như trong ảnh)
    var myName by remember { mutableStateOf("Nguyễn Thị Hồng Ngọc") }
    var myTitle by remember { mutableStateOf("Sinh viên") }
    var myPhone by remember { mutableStateOf("0987 654 321") }
    var myEmail by remember { mutableStateOf("ngocnguyen@gmail.com") }

    var isViewingQrPage by remember { mutableStateOf(false) }
    var isScanningQr by remember { mutableStateOf(false) }
    var isViewingEmergencyScreen by remember { mutableStateOf(false) }
    var scannedSuccessContact by remember { mutableStateOf<Contact?>(null) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    // 0. Màn hình "Danh bạ khẩn cấp & Gọi nhanh" (Bộ 4 màn hình chuẩn như ảnh)
    if (isViewingEmergencyScreen) {
        EmergencyContactsScreen(
            emergencyContacts = emergencyContacts,
            allContacts = allContacts,
            onUpdateEmergencyContacts = onUpdateEmergencyContacts,
            onMakeCall = onMakeCall,
            onBack = { isViewingEmergencyScreen = false },
            bottomBar = bottomBar
        )
        return
    }

    // 1. Màn hình "Thêm liên hệ từ QR" (Khi quét thành công - giao diện chuẩn 100% như ảnh)
    if (scannedSuccessContact != null) {
        ScanSuccessScreen(
            contact = scannedSuccessContact!!,
            onViewDetail = { contact ->
                scannedSuccessContact = null
                onViewContactDetail(contact)
            },
            onBack = {
                scannedSuccessContact = null
            },
            bottomBar = bottomBar
        )
        return
    }

    // 2. Màn hình "Quét mã QR" (Camera thực tế, bật đèn Flash, chọn ảnh từ thư viện - chuẩn 100% như ảnh)
    if (isScanningQr) {
        QrScannerScreen(
            onContactScanned = { contact ->
                onImportScannedContact(contact)
                scannedSuccessContact = contact
                isScanningQr = false
            },
            onBackClick = { isScanningQr = false }
        )
        return
    }

    // 3. Chuyển sang trang "Mã QR danh thiếp" toàn màn hình chuẩn như hình (sinh vCard thực tế)
    if (isViewingQrPage) {
        CardQrScreen(
            contactName = myName,
            contactTitle = myTitle,
            phoneNumber = myPhone,
            email = myEmail,
            onBackClick = { isViewingQrPage = false },
            bottomBar = bottomBar
        )
        return
    }

    // Hộp thoại chỉnh sửa thông tin cá nhân
    if (showEditProfileDialog) {
        var tempName by remember { mutableStateOf(myName) }
        var tempTitle by remember { mutableStateOf(myTitle) }
        var tempPhone by remember { mutableStateOf(myPhone) }
        var tempEmail by remember { mutableStateOf(myEmail) }

        Dialog(onDismissRequest = { showEditProfileDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                modifier = Modifier.width(320.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Sửa thông tin danh thiếp",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("Họ và tên") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = tempTitle,
                        onValueChange = { tempTitle = it },
                        label = { Text("Chức danh / Nghề nghiệp") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = tempPhone,
                        onValueChange = { tempPhone = it },
                        label = { Text("Số điện thoại") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = tempEmail,
                        onValueChange = { tempEmail = it },
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF1F3F4))
                                .clickable { showEditProfileDialog = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Hủy", color = Color.Black)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimaryBlue)
                                .clickable {
                                    myName = tempName
                                    myTitle = tempTitle
                                    myPhone = tempPhone
                                    myEmail = tempEmail
                                    showEditProfileDialog = false
                                    NotificationManager.show(context, "Đã cập nhật danh thiếp!")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Lưu", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
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
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "Danh thiếp của tôi",
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
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. Ảnh đại diện Avatar lớn kèm icon Camera xanh tròn (chuẩn 100% như ảnh)
            Box(
                modifier = Modifier.size(130.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                // Vòng tròn Avatar
                Box(
                    modifier = Modifier
                        .size(124.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFCBD5E1))
                        .clickable { showEditProfileDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_user_avatar),
                        contentDescription = "Avatar",
                        tint = Color.White,
                        modifier = Modifier.size(90.dp)
                    )
                }

                // Huy hiệu Camera màu xanh tròn ở góc dưới bên phải
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue)
                        .border(3.dp, Color.White, CircleShape)
                        .clickable {
                            NotificationManager.show(context, "Chọn ảnh đại diện mới")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_camera),
                        contentDescription = "Đổi ảnh",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Họ tên
            Text(
                text = myName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 3. Chức danh / Nghề nghiệp
            Text(
                text = myTitle,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 4. Card SĐT (Bo góc, nền xanh nhạt, icon điện thoại xanh)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showEditProfileDialog = true },
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF0F5FF)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_call),
                        contentDescription = "Số điện thoại",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "SĐT",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = myPhone,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5. Card Email (Bo góc, nền xanh nhạt, icon thư xanh)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showEditProfileDialog = true },
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF0F5FF)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_mail),
                        contentDescription = "Email",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Email",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = myEmail,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 6. Nút Tạo mã QR (Mở trang Mã QR danh thiếp chuẩn 100% như hình)
            Button(
                onClick = { isViewingQrPage = true },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(26.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_share),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Tạo mã QR",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 7. Nút Quét mã QR (Mở camera quét mã QR thật & chọn từ thư viện)
            Button(
                onClick = { isScanningQr = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEBF3FE),
                    contentColor = PrimaryBlue
                ),
                shape = RoundedCornerShape(26.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_qr_scan),
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Quét mã QR",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 8. Chú thích phía dưới: "ⓘ Quét mã QR để lưu danh bạ của tôi"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_info),
                    contentDescription = null,
                    tint = Color(0xFF6B7280),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Quét mã QR để lưu danh bạ của tôi",
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            // 9. Thẻ Danh bạ khẩn cấp & Gọi nhanh (Nơi 1: Trong tab Thêm - Khuyên dùng)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isViewingEmergencyScreen = true },
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFFFF1F2),
                border = BorderStroke(1.dp, Color(0xFFFECDD3))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEE2E2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_warning_filled),
                            contentDescription = "Danh bạ khẩn cấp",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Danh bạ khẩn cấp",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF991B1B)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFEF4444)
                            ) {
                                Text(
                                    text = "SOS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Gọi nhanh Bố, Mẹ và người thân (Phím 1-9)",
                            fontSize = 12.sp,
                            color = Color(0xFFB91C1C)
                        )
                    }

                    Icon(
                        painter = painterResource(id = R.drawable.ic_call),
                        contentDescription = "Mở",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
