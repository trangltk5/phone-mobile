package com.example.phone_mobile.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.phone_mobile.data.model.CallLog
import com.example.phone_mobile.data.model.CallType
import com.example.phone_mobile.data.model.Contact
import com.example.phone_mobile.data.model.sampleCallLogs
import com.example.phone_mobile.data.model.sampleContacts
import com.example.phone_mobile.ui.components.PhoneBottomBar
import com.example.phone_mobile.ui.screens.call.CallHistoryScreen
import com.example.phone_mobile.ui.screens.call.InCallScreen
import com.example.phone_mobile.ui.screens.contact.AddEditContactScreen
import com.example.phone_mobile.ui.screens.contact.ContactDetailScreen
import com.example.phone_mobile.ui.screens.contact.ContactListScreen
import com.example.phone_mobile.ui.screens.favorite.FavoritesScreen
import com.example.phone_mobile.ui.screens.keypad.KeypadScreen

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
    var callLogs by remember { mutableStateOf(sampleCallLogs) }
    var selectedTab by remember { mutableIntStateOf(2) } // Mặc định là tab Danh bạ
    var currentScreen by remember { mutableStateOf<Screen>(Screen.MainTabs) }

    // Số điện thoại ban đầu trên bàn phím (nếu có)
    var keypadPhoneNumber by remember { mutableStateOf("") }

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
                    Toast.makeText(context, "Cuộc gọi đã kết thúc ($duration)", Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(context, "Mở nhắn tin tới: ${contact.phoneNumber}", Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(context, "Mở nhắn tin tới: ${contact.phoneNumber}", Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(context, "Bắt đầu cuộc gọi video tới: ${contact.name}...", Toast.LENGTH_SHORT).show()
                    },
                    onMessageClick = {
                        Toast.makeText(context, "Mở nhắn tin tới: ${contact.phoneNumber}", Toast.LENGTH_SHORT).show()
                    },
                    onToggleFavorite = {
                        contacts = contacts.map {
                            if (it.id == contact.id) it.copy(isFavorite = !it.isFavorite) else it
                        }
                    },
                    onDeleteClick = {
                        contacts = contacts.filterNot { it.id == contact.id }
                        Toast.makeText(context, "Đã xóa liên hệ \"${contact.name}\"", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(context, "Đã thêm liên hệ mới thành công!", Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(context, "Đã cập nhật liên hệ thành công!", Toast.LENGTH_SHORT).show()
                        currentScreen = Screen.Detail(updatedContact.id)
                    }
                )
            } else {
                currentScreen = Screen.MainTabs
            }
        }
    }
}
