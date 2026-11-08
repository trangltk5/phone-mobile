package com.example.phone_mobile.ui.screens.contact

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phone_mobile.R
import com.example.phone_mobile.data.model.Contact
import com.example.phone_mobile.data.model.sampleContacts
import com.example.phone_mobile.ui.theme.IconGray
import com.example.phone_mobile.ui.theme.PrimaryBlue
import com.example.phone_mobile.ui.theme.SearchBarBg
import com.example.phone_mobile.ui.theme.TextDark
import com.example.phone_mobile.util.removeVietnameseDiacritics

@Composable
fun ContactListScreen(
    contacts: List<Contact> = sampleContacts,
    onContactClick: (Contact) -> Unit = {},
    onCallClick: (Contact) -> Unit = {},
    onMessageClick: (Contact) -> Unit = {},
    onAddClick: () -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    val isSearching = searchQuery.isNotEmpty()

    // Bấm nút quay lại của Android khi đang tìm kiếm thì xóa tìm kiếm
    BackHandler(enabled = isSearching) {
        searchQuery = ""
    }

    // 1. Màn hình "Danh bạ trống" chuẩn 100% theo Ảnh 2 (Node 5:1791)
    if (contacts.isEmpty()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.White,
            topBar = {
                ContactTopBar(
                    isSearching = false,
                    onBackFromSearch = {},
                    onAddClick = onAddClick
                )
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
                // Minh họa thẻ danh bạ profile card màu xanh nhạt
                Icon(
                    painter = painterResource(id = R.drawable.ic_empty_contacts),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(width = 240.dp, height = 180.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Tiêu đề: "Chưa có liên hệ nào"
                Text(
                    text = "Chưa có liên hệ nào",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Hướng dẫn: "Hãy thêm liên hệ đầu tiên của bạn để bắt đầu quản lý danh bạ"
                Text(
                    text = "Hãy thêm liên hệ đầu tiên của bạn\nđể bắt đầu quản lý danh bạ",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Nút "Thêm liên hệ" màu xanh có icon +
                Button(
                    onClick = onAddClick,
                    modifier = Modifier
                        .fillMaxWidth(0.82f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Thêm liên hệ",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
        return
    }

    // Filter contacts based on search query (hỗ trợ tìm kiếm cả có dấu và không dấu tiếng Việt)
    val filteredContacts = remember(searchQuery, contacts) {
        if (searchQuery.isBlank()) {
            contacts
        } else {
            val normalizedQuery = searchQuery.trim().removeVietnameseDiacritics().lowercase()
            contacts.filter { contact ->
                val normalizedName = contact.name.removeVietnameseDiacritics().lowercase()
                val cleanPhone = contact.phoneNumber.replace(" ", "")
                normalizedName.contains(normalizedQuery) ||
                        cleanPhone.contains(normalizedQuery) ||
                        contact.name.contains(searchQuery, ignoreCase = true) ||
                        contact.phoneNumber.contains(searchQuery)
            }
        }
    }

    // Group contacts by the first letter of their display name
    val groupedContacts = remember(filteredContacts) {
        filteredContacts.groupBy { contact ->
            val firstChar = contact.name.trim().take(1).uppercase()
            if (firstChar.isNotEmpty() && firstChar[0].isLetter()) firstChar else "#"
        }.toSortedMap()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            ContactTopBar(
                isSearching = isSearching,
                onBackFromSearch = { searchQuery = "" },
                onAddClick = onAddClick
            )
        },
        bottomBar = bottomBar
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Thanh tìm kiếm (có nút xóa x chuẩn như Ảnh 1)
            ContactSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onClearQuery = { searchQuery = "" },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            )

            // 2. Màn hình "Không tìm thấy liên hệ phù hợp" chuẩn 100% theo Ảnh 1
            if (filteredContacts.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Hình minh họa khung vuông viền xanh dương và cuốn sổ danh bạ bên trong
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search_empty),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(190.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Tiêu đề đậm: "Không tìm thấy liên hệ phù hợp."
                    Text(
                        text = "Không tìm thấy liên hệ phù hợp.",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Phụ đề xám: "Hãy thử từ khóa khác."
                    Text(
                        text = "Hãy thử từ khóa khác.",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Danh sách liên hệ gom nhóm theo bảng chữ cái
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    groupedContacts.forEach { (initial, contactList) ->
                        item(key = "header_$initial") {
                            Text(
                                text = initial,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 8.dp)
                            )
                        }

                        items(contactList, key = { it.id }) { contact ->
                            ContactItemRow(
                                contact = contact,
                                onClick = { onContactClick(contact) },
                                onCallClick = { onCallClick(contact) }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

/**
 * TopBar: Thay đổi giữa "Danh bạ" (kèm nút +) và "Tìm kiếm" (kèm nút ←) chuẩn như ảnh
 */
@Composable
private fun ContactTopBar(
    isSearching: Boolean,
    onBackFromSearch: () -> Unit,
    onAddClick: () -> Unit
) {
    Surface(
        color = PrimaryBlue,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(64.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isSearching) {
                    IconButton(onClick = onBackFromSearch) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "Quay lại",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = if (isSearching) "Tìm kiếm" else "Danh bạ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            if (!isSearching) {
                IconButton(onClick = onAddClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = "Thêm liên hệ",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

/**
 * Thanh tìm kiếm có icon Search và nút Xóa X bên phải chuẩn như Ảnh 1
 */
@Composable
private fun ContactSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .background(SearchBarBg, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Tìm kiếm",
                tint = Color.Black,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 17.sp,
                    color = Color.Black
                ),
                cursorBrush = SolidColor(PrimaryBlue),
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text(
                            text = "Tìm kiếm liên hệ",
                            fontSize = 17.sp,
                            color = Color(0xFF555555)
                        )
                    }
                    innerTextField()
                }
            )

            // Nút xóa 'X' xuất hiện khi có nội dung tìm kiếm (chuẩn như Ảnh 1)
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = onClearQuery,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "Xóa từ khóa",
                        tint = Color(0xFF555555),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactItemRow(
    contact: Contact,
    onClick: () -> Unit,
    onCallClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Avatar tròn (cố định căn lề trái thẳng hàng)
            Icon(
                painter = painterResource(id = R.drawable.ic_user_avatar),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Tên và SĐT
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = contact.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    // Ngôi sao vàng yêu thích nằm ngay cạnh tên
                    if (contact.isFavorite) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_star_filled),
                            contentDescription = "Yêu thích",
                            tint = Color(0xFFFFB800),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = contact.phoneNumber,
                    fontSize = 14.sp,
                    color = Color(0xFF555555)
                )
            }
        }

        // Nút gọi điện thoại nhanh
        IconButton(
            onClick = onCallClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_call),
                contentDescription = "Gọi cho ${contact.name}",
                tint = IconGray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
