package com.example.phone_mobile.ui.screens.emergency

import android.widget.Toast
import com.example.phone_mobile.util.NotificationManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.phone_mobile.R
import com.example.phone_mobile.data.model.Contact
import com.example.phone_mobile.data.model.EmergencyContact
import com.example.phone_mobile.ui.theme.PrimaryBlue

enum class EmergencySubScreen {
    LIST,         // Màn hình 1: Danh sách liên hệ khẩn cấp
    ADD_METHOD,   // Màn hình 2: Thêm liên hệ khẩn cấp (Chọn từ danh bạ / Nhập thủ công)
    PICK_CONTACT, // Màn hình Chọn từ danh bạ TOÀN MÀN HÌNH (Full screen với thanh tìm kiếm)
    EDIT,         // Màn hình 3: Chỉnh sửa liên hệ khẩn cấp
    CALL_SOS      // Màn hình 4: Gọi khẩn cấp (Hiệu ứng sóng đỏ lan tỏa)
}

@Composable
fun EmergencyContactsScreen(
    emergencyContacts: List<EmergencyContact>,
    allContacts: List<Contact> = emptyList(),
    onUpdateEmergencyContacts: (List<EmergencyContact>) -> Unit,
    onMakeCall: (phoneNumber: String, contactName: String?) -> Unit,
    onBack: () -> Unit,
    bottomBar: @Composable () -> Unit
) {
    var currentSubScreen by remember { mutableStateOf(EmergencySubScreen.LIST) }
    var selectedContactForEdit by remember { mutableStateOf<EmergencyContact?>(null) }
    var selectedContactForSos by remember {
        mutableStateOf(emergencyContacts.find { it.isPriority } ?: emergencyContacts.firstOrNull())
    }

    when (currentSubScreen) {
        EmergencySubScreen.LIST -> {
            EmergencyListContent(
                contacts = emergencyContacts,
                onAddClick = { currentSubScreen = EmergencySubScreen.ADD_METHOD },
                onEditClick = { contact ->
                    selectedContactForEdit = contact
                    currentSubScreen = EmergencySubScreen.EDIT
                },
                onSosScreenClick = { contact ->
                    selectedContactForSos = contact
                    currentSubScreen = EmergencySubScreen.CALL_SOS
                },
                onMakeCall = onMakeCall,
                onBack = onBack,
                bottomBar = bottomBar
            )
        }

        EmergencySubScreen.ADD_METHOD -> {
            AddEmergencyContactMethodContent(
                onSelectFromContacts = {
                    currentSubScreen = EmergencySubScreen.PICK_CONTACT
                },
                onManualInput = {
                    selectedContactForEdit = null // tạo mới
                    currentSubScreen = EmergencySubScreen.EDIT
                },
                onBack = { currentSubScreen = EmergencySubScreen.LIST },
                bottomBar = bottomBar
            )
        }

        EmergencySubScreen.PICK_CONTACT -> {
            PickContactFullScreenContent(
                contacts = allContacts,
                onContactSelected = { picked ->
                    val nextKey = (1..9).firstOrNull { key -> emergencyContacts.none { it.speedDialKey == key } } ?: 1
                    selectedContactForEdit = EmergencyContact(
                        id = "em_${System.currentTimeMillis()}",
                        name = picked.name,
                        phoneNumber = picked.phoneNumber,
                        relationship = "Người thân",
                        speedDialKey = nextKey,
                        isPriority = emergencyContacts.none { it.isPriority }
                    )
                    currentSubScreen = EmergencySubScreen.EDIT
                },
                onQuickAdd = { picked ->
                    val nextKey = (1..9).firstOrNull { key -> emergencyContacts.none { it.speedDialKey == key } } ?: 1
                    val newEmergency = EmergencyContact(
                        id = "em_${System.currentTimeMillis()}",
                        name = picked.name,
                        phoneNumber = picked.phoneNumber,
                        relationship = "Người thân",
                        speedDialKey = nextKey,
                        isPriority = emergencyContacts.none { it.isPriority }
                    )
                    val newList = emergencyContacts + newEmergency
                    onUpdateEmergencyContacts(newList)
                    currentSubScreen = EmergencySubScreen.LIST
                },
                onBack = { currentSubScreen = EmergencySubScreen.ADD_METHOD },
                bottomBar = bottomBar
            )
        }

        EmergencySubScreen.EDIT -> {
            EditEmergencyContactContent(
                initialContact = selectedContactForEdit,
                existingContacts = emergencyContacts,
                onSave = { updatedContact ->
                    val isExisting = emergencyContacts.any { it.id == updatedContact.id }
                    val newList = if (isExisting) {
                        emergencyContacts.map { if (it.id == updatedContact.id) updatedContact else it }
                    } else {
                        emergencyContacts + updatedContact
                    }
                    // Nếu đặt làm ưu tiên, các số khác bỏ ưu tiên
                    val finalAssignedList = if (updatedContact.isPriority) {
                        newList.map { if (it.id == updatedContact.id) it else it.copy(isPriority = false) }
                    } else newList

                    onUpdateEmergencyContacts(finalAssignedList)
                    currentSubScreen = EmergencySubScreen.LIST
                },
                onDelete = { contactId ->
                    onUpdateEmergencyContacts(emergencyContacts.filter { it.id != contactId })
                    currentSubScreen = EmergencySubScreen.LIST
                },
                onBack = { currentSubScreen = EmergencySubScreen.LIST },
                bottomBar = bottomBar
            )
        }

        EmergencySubScreen.CALL_SOS -> {
            val contactToCall = selectedContactForSos ?: emergencyContacts.firstOrNull()
            EmergencyCallSosContent(
                contact = contactToCall,
                onCall = { num, name -> onMakeCall(num, name) },
                onBack = { currentSubScreen = EmergencySubScreen.LIST },
                bottomBar = bottomBar
            )
        }
    }
}

/**
 * Màn hình 1: Danh bạ khẩn cấp (Danh sách liên hệ + Banner cảnh báo)
 */
@Composable
private fun EmergencyListContent(
    contacts: List<EmergencyContact>,
    onAddClick: () -> Unit,
    onEditClick: (EmergencyContact) -> Unit,
    onSosScreenClick: (EmergencyContact) -> Unit,
    onMakeCall: (phoneNumber: String, contactName: String?) -> Unit,
    onBack: () -> Unit,
    bottomBar: @Composable () -> Unit
) {
    BackHandler { onBack() }

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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                            text = "Danh bạ khẩn cấp",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Nút Thêm liên hệ khẩn cấp "+" góc phải TopBar
                    IconButton(onClick = onAddClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add),
                            contentDescription = "Thêm",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        },
        bottomBar = bottomBar
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Banner cảnh báo màu hồng nhạt viền mềm chuẩn như ảnh
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFEE2E2),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // Nhấn vào banner mở màn hình Gọi SOS khẩn cấp
                        val priority = contacts.find { it.isPriority } ?: contacts.firstOrNull()
                        if (priority != null) onSosScreenClick(priority)
                    }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_warning_filled),
                        contentDescription = "Cảnh báo",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Trong trường hợp khẩn cấp,\nbạn có thể gọi nhanh các liên hệ này",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFB91C1C),
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tiêu đề: "Danh sách liên hệ"
            Text(
                text = "Danh sách liên hệ",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (contacts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Chưa có liên hệ khẩn cấp nào.\nBấm dấu \"+\" ở góc trên để thêm.",
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(contacts, key = { it.id }) { contact ->
                        EmergencyContactRowItem(
                            contact = contact,
                            onItemClick = { onEditClick(contact) },
                            onCallClick = { onMakeCall(contact.phoneNumber, contact.name) }
                        )
                    }
                }
            }

            // Nút mở Màn hình Gọi khẩn cấp (SOS) tiện ích
            Button(
                onClick = {
                    val priority = contacts.find { it.isPriority } ?: contacts.firstOrNull()
                    if (priority != null) onSosScreenClick(priority)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_call),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Chế độ Gọi khẩn cấp (SOS)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Hàng hiển thị 1 liên hệ khẩn cấp
 */
@Composable
private fun EmergencyContactRowItem(
    contact: EmergencyContact,
    onItemClick: () -> Unit,
    onCallClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar tròn xám
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFFCBD5E1)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_user_avatar),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Tên & SĐT & Huy hiệu phím tắt (VD: #1 Bố)
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = contact.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                if (contact.speedDialKey in 1..9) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFEBF3FE)
                    ) {
                        Text(
                            text = "Phím #${contact.speedDialKey}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryBlue,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                if (contact.isPriority) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFEE2E2)
                    ) {
                        Text(
                            text = "Ưu tiên",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = contact.phoneNumber,
                fontSize = 14.sp,
                color = Color(0xFF4B5563)
            )
        }

        // Nút gọi điện tròn màu xanh dương bên phải
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(PrimaryBlue)
                .clickable(onClick = onCallClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_call),
                contentDescription = "Gọi cho ${contact.name}",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Màn hình 2: Thêm liên hệ khẩn cấp (Chọn từ danh bạ / Nhập thủ công)
 */
@Composable
private fun AddEmergencyContactMethodContent(
    onSelectFromContacts: () -> Unit,
    onManualInput: () -> Unit,
    onBack: () -> Unit,
    bottomBar: @Composable () -> Unit
) {
    BackHandler { onBack() }

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
                        text = "Thêm liên hệ khẩn cấp",
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
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon khiên xanh lớn kèm ống nghe và tia sáng lấp lánh (chuẩn 100% như ảnh)
            Icon(
                painter = painterResource(id = R.drawable.ic_shield_phone),
                contentDescription = "Khiên bảo vệ khẩn cấp",
                tint = Color.Unspecified,
                modifier = Modifier.size(110.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Dòng chữ hướng dẫn: "Chọn người liên hệ bạn muốn đánh dấu là khẩn cấp"
            Text(
                text = "Chọn người liên hệ bạn muốn\nđánh dấu là khẩn cấp",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1F2937),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            // 1. Nút "Chọn từ danh bạ" (Nút xanh đậm)
            Button(
                onClick = onSelectFromContacts,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(26.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Chọn từ danh bạ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Nút "Nhập thủ công" (Nút viền bo)
            OutlinedButton(
                onClick = onManualInput,
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
                    text = "Nhập thủ công",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBlue
                )
            }
        }
    }
}

/**
 * Màn hình 3: Chỉnh sửa liên hệ khẩn cấp (Họ tên, SĐT, Quan hệ, Ưu tiên, Phím số 1-9)
 */
@Composable
private fun EditEmergencyContactContent(
    initialContact: EmergencyContact?,
    existingContacts: List<EmergencyContact>,
    onSave: (EmergencyContact) -> Unit,
    onDelete: (String) -> Unit,
    onBack: () -> Unit,
    bottomBar: @Composable () -> Unit
) {
    val context = LocalContext.current
    BackHandler { onBack() }

    var name by remember(initialContact) { mutableStateOf(initialContact?.name ?: "") }
    var phoneNumber by remember(initialContact) { mutableStateOf(initialContact?.phoneNumber ?: "") }
    var relationship by remember(initialContact) { mutableStateOf(initialContact?.relationship ?: "Bố") }
    var isPriority by remember(initialContact) { mutableStateOf(initialContact?.isPriority ?: (existingContacts.none { it.isPriority })) }
    var speedDialKey by remember(initialContact) {
        val nextKey = (1..9).firstOrNull { key -> existingContacts.none { it.speedDialKey == key } } ?: 1
        mutableIntStateOf(initialContact?.speedDialKey ?: nextKey)
    }

    var isDropdownExpanded by remember { mutableStateOf(false) }
    val relationshipOptions = listOf("Bố", "Mẹ", "Vợ / Chồng", "Con", "Người thân", "Bác sĩ", "Bạn bè", "Khác")

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
                        text = if (initialContact != null) "Chỉnh sửa liên hệ khẩn cấp" else "Thêm liên hệ khẩn cấp",
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
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Khu vực Avatar + Nút Thay ảnh (chuẩn 100% như ảnh)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.size(68.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFCBD5E1)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_user_avatar),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    // Huy hiệu camera xanh tròn
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue)
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_camera),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(18.dp))

                // Nút "Thay ảnh" hình viên thuốc xanh nhạt
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFFE8F1FD),
                    modifier = Modifier.clickable {
                        NotificationManager.show(context, "Chọn ảnh đại diện cho $name")
                    }
                ) {
                    Text(
                        text = "Thay ảnh",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Trường: "Tên liên hệ"
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Tên liên hệ",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF374151)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    placeholder = { Text("Nhập tên liên hệ (VD: Bố)") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color(0xFFE5E7EB)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Trường: "Số điện thoại"
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Số điện thoại",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF374151)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    singleLine = true,
                    placeholder = { Text("Nhập số điện thoại") },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_call),
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color(0xFFE5E7EB)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Trường: "Quan hệ" (Dropdown selector)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Quan hệ",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF374151)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = relationship,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_more),
                                contentDescription = "Chọn quan hệ",
                                tint = Color.Gray,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { isDropdownExpanded = true }
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color(0xFFE5E7EB)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isDropdownExpanded = true }
                    )

                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        relationshipOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    relationship = option
                                    if (name.isBlank() || relationshipOptions.contains(name)) {
                                        name = option
                                    }
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Gán phím gọi nhanh trên Bàn phím số (1-9)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Gán phím gọi nhanh (Bàn phím số)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF374151)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    (1..5).forEach { key ->
                        val isSelected = speedDialKey == key
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PrimaryBlue else Color(0xFFF3F4F6))
                                .clickable { speedDialKey = key },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "#$key",
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF4B5563)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Hàng: "Ưu tiên liên hệ" kèm Switch bật/tắt (chuẩn 100% như ảnh)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Ưu tiên liên hệ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Text(
                        text = "Liên hệ đầu tiên được gọi khi kích hoạt SOS",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Switch(
                    checked = isPriority,
                    onCheckedChange = { isPriority = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PrimaryBlue,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFD1D5DB)
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Nút "Lưu thay đổi" (Nút xanh filled)
            Button(
                onClick = {
                    if (name.isBlank() || phoneNumber.isBlank()) {
                        NotificationManager.show(context, "Vui lòng nhập đầy đủ Tên và Số điện thoại")
                        return@Button
                    }
                    val item = EmergencyContact(
                        id = initialContact?.id ?: "em_${System.currentTimeMillis()}",
                        name = name.trim(),
                        phoneNumber = phoneNumber.trim(),
                        relationship = relationship,
                        isPriority = isPriority,
                        speedDialKey = speedDialKey
                    )
                    onSave(item)
                    NotificationManager.show(context, "Đã lưu liên hệ khẩn cấp \"$name\"")
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(26.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Lưu thay đổi",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Nút "Hủy" (Nút viền)
            OutlinedButton(
                onClick = onBack,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF4B5563)
                ),
                border = BorderStroke(1.2.dp, Color(0xFFD1D5DB)),
                shape = RoundedCornerShape(26.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Hủy",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4B5563)
                )
            }

            // Nếu đang sửa liên hệ, cho phép Xóa
            if (initialContact != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Xóa khỏi danh bạ khẩn cấp",
                    color = Color(0xFFEF4444),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable {
                        onDelete(initialContact.id)
                        NotificationManager.show(context, "Đã xóa khỏi danh bạ khẩn cấp")
                    }
                )
            }
        }
    }
}

/**
 * Màn hình 4: Gọi khẩn cấp (SOS Screen với hiệu ứng sóng âm đỏ lan tỏa chuẩn 100% như ảnh)
 */
@Composable
private fun EmergencyCallSosContent(
    contact: EmergencyContact?,
    onCall: (phoneNumber: String, contactName: String?) -> Unit,
    onBack: () -> Unit,
    bottomBar: @Composable () -> Unit
) {
    BackHandler { onBack() }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_ripple")
    val pulseProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )

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
                        text = "Danh bạ khẩn cấp",
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Đồ họa trung tâm: Nút đỏ lớn với các vòng sóng âm / rung lan tỏa màu hồng nhạt xung quanh
            Box(
                modifier = Modifier.size(230.dp),
                contentAlignment = Alignment.Center
            ) {
                // Vòng sóng lan tỏa chuyển động động (Canvas Ripple)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val maxRadius = size.width / 2f

                    // Vòng ngoài 1
                    val r1 = 55.dp.toPx() + (maxRadius - 55.dp.toPx()) * pulseProgress
                    val alpha1 = (1f - pulseProgress) * 0.45f
                    drawCircle(
                        color = Color(0xFFFCA5A5).copy(alpha = alpha1),
                        radius = r1,
                        center = androidx.compose.ui.geometry.Offset(cx, cy)
                    )

                    // Vòng ngoài 2
                    val r2 = 55.dp.toPx() + (maxRadius - 55.dp.toPx()) * ((pulseProgress + 0.5f) % 1f)
                    val alpha2 = (1f - ((pulseProgress + 0.5f) % 1f)) * 0.45f
                    drawCircle(
                        color = Color(0xFFFECDD3).copy(alpha = alpha2),
                        radius = r2,
                        center = androidx.compose.ui.geometry.Offset(cx, cy)
                    )
                }

                // Vòng tròn đỏ chính giữa chứa icon điện thoại trắng nghiêng (chuẩn như ảnh)
                Box(
                    modifier = Modifier
                        .size(108.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE02424))
                        .clickable {
                            if (contact != null) {
                                onCall(contact.phoneNumber, contact.name)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_call),
                        contentDescription = "Gọi khẩn cấp",
                        tint = Color.White,
                        modifier = Modifier.size(46.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Tiêu đề: "Gọi khẩn cấp"
            Text(
                text = "Gọi khẩn cấp",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Phụ đề: "Liên hệ ngay với người thân trong trường hợp khẩn cấp"
            Text(
                text = "Liên hệ ngay với người thân\ntrong trường hợp khẩn cấp",
                fontSize = 15.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            if (contact != null) {
                Spacer(modifier = Modifier.height(18.dp))
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFEE2E2),
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${contact.name} (${contact.relationship})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF991B1B)
                        )
                        Text(
                            text = contact.phoneNumber,
                            fontSize = 14.sp,
                            color = Color(0xFFB91C1C)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Nút đỏ to bản "Gọi ngay" (chuẩn 100% như ảnh)
            Button(
                onClick = {
                    if (contact != null) {
                        onCall(contact.phoneNumber, contact.name)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
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
                        painter = painterResource(id = R.drawable.ic_call),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Gọi ngay",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Màn hình Chọn từ danh bạ TOÀN MÀN HÌNH (Full screen với thanh tìm kiếm và nút Chọn/Thêm ngay)
 */
@Composable
private fun PickContactFullScreenContent(
    contacts: List<Contact>,
    onContactSelected: (Contact) -> Unit,
    onQuickAdd: (Contact) -> Unit,
    onBack: () -> Unit,
    bottomBar: @Composable () -> Unit
) {
    val context = LocalContext.current
    BackHandler { onBack() }
    var searchQuery by remember { mutableStateOf("") }

    val filteredContacts = remember(contacts, searchQuery) {
        if (searchQuery.isBlank()) {
            contacts
        } else {
            val q = searchQuery.trim().lowercase()
            contacts.filter {
                it.name.lowercase().contains(q) || it.phoneNumber.contains(q)
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
                        text = "Chọn từ danh bạ",
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
        ) {
            // Thanh tìm kiếm danh bạ
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Tìm theo tên hoặc số điện thoại...", color = Color.Gray, fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = "Tìm kiếm",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_close),
                                    contentDescription = "Xóa",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Danh sách liên hệ cuộn toàn màn hình
            if (filteredContacts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isBlank()) "Không có liên hệ nào trong danh bạ" else "Không tìm thấy kết quả phù hợp",
                        color = Color.Gray,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredContacts, key = { it.id }) { c ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onContactSelected(c) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Avatar tròn
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE2E8F0)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_user_avatar),
                                        contentDescription = null,
                                        tint = Color(0xFF94A3B8),
                                        modifier = Modifier.size(26.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                // Tên & SĐT
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = c.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = c.phoneNumber,
                                        fontSize = 14.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }

                                // Nút "Chọn" bên phải (Chuyển sang màn chỉnh sửa thông tin)
                                Button(
                                    onClick = { onContactSelected(c) },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                    shape = RoundedCornerShape(18.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text(text = "Chọn", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
