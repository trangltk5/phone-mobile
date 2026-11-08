package com.example.phone_mobile.data.model

data class EmergencyContact(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val relationship: String = "Người thân",
    val isPriority: Boolean = false,
    val speedDialKey: Int = 1,
    val avatarUrl: String = ""
)

val sampleEmergencyContacts = listOf(
    EmergencyContact(
        id = "em_1",
        name = "Bố",
        phoneNumber = "0987 111 222",
        relationship = "Bố",
        isPriority = true,
        speedDialKey = 1
    ),
    EmergencyContact(
        id = "em_2",
        name = "Mẹ",
        phoneNumber = "0987 333 444",
        relationship = "Mẹ",
        isPriority = false,
        speedDialKey = 2
    ),
    EmergencyContact(
        id = "em_3",
        name = "Người thân",
        phoneNumber = "0912 345 678",
        relationship = "Người thân",
        isPriority = false,
        speedDialKey = 3
    )
)
