package com.example.phone_mobile.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.phone_mobile.R
import com.example.phone_mobile.data.model.CallLog
import com.example.phone_mobile.data.model.CallType
import com.example.phone_mobile.data.model.Contact
import com.example.phone_mobile.data.model.EmergencyContact
import com.example.phone_mobile.data.model.sampleCallLogs
import com.example.phone_mobile.data.model.sampleContacts
import com.example.phone_mobile.data.model.sampleEmergencyContacts
import com.example.phone_mobile.ui.components.PhoneBottomBar
import com.example.phone_mobile.ui.screens.call.CallHistoryScreen
import com.example.phone_mobile.ui.screens.call.InCallScreen
import com.example.phone_mobile.ui.screens.contact.AddEditContactScreen
import com.example.phone_mobile.ui.screens.contact.ContactDetailScreen
import com.example.phone_mobile.ui.screens.contact.ContactListScreen
import com.example.phone_mobile.ui.screens.favorite.FavoritesScreen
import com.example.phone_mobile.ui.screens.keypad.KeypadScreen
import com.example.phone_mobile.ui.screens.more.ShareCardScreen
import com.example.phone_mobile.util.NotificationManager
import kotlinx.coroutines.delay

sealed interface Screen {
    data object MainTabs : Screen
    data class Detail(val contactId: String) : Screen
    data object Add : Screen
    data class Edit(val contactId: String) : Screen
    data class InCall(
        val phoneNumber: String,
        val contactName: String?,
        val previousScreen: Screen
    ) : Screen
}

@Composable
fun MainApp() {
    val context = LocalContext.current

    var contacts by remember { mutableStateOf(sampleContacts) }
    var emergencyContacts by remember { mutableStateOf(sampleEmergencyContacts) }
    var callLogs by remember { mutableStateOf(sampleCallLogs) }
    var selectedTab by remember { mutableIntStateOf(2) } // Mặc định là tab Danh bạ
    var currentScreen by remember { mutableStateOf<Screen>(Screen.MainTabs) }

    // Số điện thoại ban đầu trên bàn phím (nếu có)
    var keypadPhoneNumber by remember { mutableStateOf("") }

    // Quản lý thông báo nổi ở Top Center
    var topNotificationMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        NotificationManager.messages.collect { msg ->
            topNotificationMessage = msg
            delay(2500)
            if (topNotificationMessage == msg) {
                topNotificationMessage = null
            }
        }
    }

    // Bắt đầu cuộc gọi: Mở trực tiếp màn hình In-Call chuẩn phong cách iOS
    fun initiateCall(phoneNumber: String, name: String? = null) {
        val contactName = name ?: contacts.find { 
            it.phoneNumber.replace(" ", "") == phoneNumber.replace(" ", "") 
        }?.name ?: phoneNumber

        keypadPhoneNumber = phoneNumber
        currentScreen = Screen.InCall(
            phoneNumber = phoneNumber,
            contactName = contactName,
            previousScreen = currentScreen
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val screen = currentScreen) {
        is Screen.InCall -> {
            InCallScreen(
                phoneNumber = screen.phoneNumber,
                contactName = screen.contactName,
                onEndCall = { duration ->
                    // Thêm vào nhật ký cuộc gọi
                    val newCallLog = CallLog(
                        id = System.currentTimeMillis().toString(),
                        contactName = screen.contactName ?: screen.phoneNumber,
                        phoneNumber = screen.phoneNumber,
                        type = CallType.OUTGOING,
                        time = "Vừa xong",
                        duration = duration
                    )
                    callLogs = listOf(newCallLog) + callLogs
                    NotificationManager.show(context, "Cuộc gọi đã kết thúc ($duration)")
                    currentScreen = screen.previousScreen
                }
            )
        }

        is Screen.MainTabs -> {
            when (selectedTab) {
                0 -> {
                    CallHistoryScreen(
                        callLogs = callLogs,
                        onCallClick = { log ->
                            initiateCall(log.phoneNumber, log.contactName)
                        },
                        bottomBar = {
                            PhoneBottomBar(
                                selectedTab = selectedTab,
                                onTabSelected = { selectedTab = it }
                            )
                        }
                    )
                }

                1 -> {
                    KeypadScreen(
                        initialPhoneNumber = keypadPhoneNumber,
                        contacts = contacts,
                        emergencyContacts = emergencyContacts,
                        onMakeCall = { number, contactName ->
                            initiateCall(number, contactName)
                        },
                        bottomBar = {
                            PhoneBottomBar(
                                selectedTab = selectedTab,
                                onTabSelected = { selectedTab = it }
                            )
                        }
                    )
                }

                2 -> {
                    ContactListScreen(
                        contacts = contacts,
                        onContactClick = { contact ->
                            currentScreen = Screen.Detail(contact.id)
                        },
                        onCallClick = { contact ->
                            initiateCall(contact.phoneNumber, contact.name)
                        },
                        onMessageClick = { contact ->
                            NotificationManager.show(context, "Mở nhắn tin tới: ${contact.phoneNumber}")
                        },
                        onAddClick = {
                            currentScreen = Screen.Add
                        },
                        bottomBar = {
                            PhoneBottomBar(
                                selectedTab = selectedTab,
                                onTabSelected = { selectedTab = it }
                            )
                        }
                    )
                }

                3 -> {
                    FavoritesScreen(
                        favoriteContacts = contacts.filter { it.isFavorite },
                        onContactClick = { contact ->
                            currentScreen = Screen.Detail(contact.id)
                        },
                        onCallClick = { contact ->
                            initiateCall(contact.phoneNumber, contact.name)
                        },
                        onMessageClick = { contact ->
                            NotificationManager.show(context, "Mở nhắn tin tới: ${contact.phoneNumber}")
                        },
                        bottomBar = {
                            PhoneBottomBar(
                                selectedTab = selectedTab,
                                onTabSelected = { selectedTab = it }
                            )
                        }
                    )
                }

                4 -> {
                    ShareCardScreen(
                        emergencyContacts = emergencyContacts,
                        allContacts = contacts,
                        onUpdateEmergencyContacts = { emergencyContacts = it },
                        onMakeCall = { number, contactName ->
                            initiateCall(number, contactName)
                        },
                        onImportScannedContact = { scannedContact ->
                            contacts = listOf(scannedContact) + contacts
                        },
                        onViewContactDetail = { contact ->
                            currentScreen = Screen.Detail(contact.id)
                        },
                        bottomBar = {
                            PhoneBottomBar(
                                selectedTab = selectedTab,
                                onTabSelected = { selectedTab = it }
                            )
                        }
                    )
                }
            }
        }

        is Screen.Detail -> {
            val contact = contacts.find { it.id == screen.contactId }
            if (contact != null) {
                BackHandler {
                    currentScreen = Screen.MainTabs
                }

                ContactDetailScreen(
                    contact = contact,
                    onBackClick = { currentScreen = Screen.MainTabs },
                    onEditClick = { currentScreen = Screen.Edit(contact.id) },
                    onCallClick = { initiateCall(contact.phoneNumber, contact.name) },
                    onVideoCallClick = {
                        NotificationManager.show(context, "Bắt đầu cuộc gọi video tới: ${contact.name}...")
                    },
                    onMessageClick = {
                        NotificationManager.show(context, "Mở nhắn tin tới: ${contact.phoneNumber}")
                    },
                    onToggleFavorite = {
                        contacts = contacts.map {
                            if (it.id == contact.id) it.copy(isFavorite = !it.isFavorite) else it
                        }
                    },
                    onDeleteClick = {
                        contacts = contacts.filterNot { it.id == contact.id }
                        NotificationManager.show(context, "Đã xóa liên hệ \"${contact.name}\"")
                        currentScreen = Screen.MainTabs
                    }
                )
            } else {
                currentScreen = Screen.MainTabs
            }
        }

        is Screen.Add -> {
            BackHandler {
                currentScreen = Screen.MainTabs
            }

            AddEditContactScreen(
                contactToEdit = null,
                existingContacts = contacts,
                onBackClick = { currentScreen = Screen.MainTabs },
                onSaveContact = { newContact ->
                    contacts = listOf(newContact) + contacts
                    NotificationManager.show(context, "Đã thêm liên hệ mới thành công!")
                    currentScreen = Screen.Detail(newContact.id)
                }
            )
        }

        is Screen.Edit -> {
            val contact = contacts.find { it.id == screen.contactId }
            if (contact != null) {
                BackHandler {
                    currentScreen = Screen.Detail(contact.id)
                }

                AddEditContactScreen(
                    contactToEdit = contact,
                    existingContacts = contacts,
                    onBackClick = { currentScreen = Screen.Detail(contact.id) },
                    onSaveContact = { updatedContact ->
                        contacts = contacts.map {
                            if (it.id == updatedContact.id) updatedContact else it
                        }
                        NotificationManager.show(context, "Đã cập nhật liên hệ thành công!")
                        currentScreen = Screen.Detail(updatedContact.id)
                    }
                )
            } else {
                currentScreen = Screen.MainTabs
            }
        }
    }

    // THÔNG BÁO NỔI TOÀN APP Ở TOP CENTER (Đỉnh giữa màn hình)
    AnimatedVisibility(
        visible = topNotificationMessage != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = Modifier
            .align(Alignment.TopCenter)
            .statusBarsPadding()
            .padding(top = 16.dp, start = 24.dp, end = 24.dp)
            .zIndex(9999f)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xF21E293B), // Slate đậm hiện đại
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_info),
                    contentDescription = null,
                    tint = Color(0xFF60A5FA),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = topNotificationMessage ?: "",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}
}
