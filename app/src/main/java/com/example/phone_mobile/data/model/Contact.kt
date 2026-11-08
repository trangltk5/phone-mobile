package com.example.phone_mobile.data.model

data class Contact(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val email: String = "",
    val address: String = "",
    val birthday: String = "",
    val note: String = "",
    val isFavorite: Boolean = false
)

enum class CallType {
    INCOMING,
    OUTGOING,
    MISSED
}

data class CallLog(
    val id: String,
    val contactName: String,
    val phoneNumber: String,
    val type: CallType,
    val time: String,
    val duration: String = ""
)

val sampleContacts = listOf(
    Contact(
        id = "1",
        name = "Nguyễn Văn A",
        phoneNumber = "0123456789",
        email = "nguyenvana@gmail.com",
        address = "Hà Nội",
        birthday = "15/08/1998",
        note = "Bạn đại học",
        isFavorite = true
    ),
    Contact(
        id = "2",
        name = "Lê Thị B",
        phoneNumber = "0111222333",
        email = "lethib@gmail.com",
        address = "Đà Nẵng",
        birthday = "20/11/1999",
        note = "Đồng nghiệp công ty",
        isFavorite = true
    ),
    Contact(
        id = "3",
        name = "Nguyễn Ngọc B",
        phoneNumber = "0444555666",
        email = "nguyenngocb@gmail.com",
        address = "TP. Hồ Chí Minh",
        birthday = "05/03/1997",
        note = "Khách hàng thân thiết",
        isFavorite = false
    ),
    Contact(
        id = "4",
        name = "Trần Minh C",
        phoneNumber = "0777888999",
        email = "tranminhc@gmail.com",
        address = "Hải Phòng",
        birthday = "12/06/1995",
        note = "Trưởng phòng kinh doanh",
        isFavorite = true
    ),
    Contact(
        id = "5",
        name = "Đặng Văn C",
        phoneNumber = "0987654321",
        email = "dangvanc@gmail.com",
        address = "Cần Thơ",
        birthday = "25/09/2000",
        note = "Đối tác phần mềm",
        isFavorite = false
    ),
    Contact(
        id = "6",
        name = "Hoàng Tuấn Anh",
        phoneNumber = "0905123456",
        email = "tuananh.hoang@gmail.com",
        address = "Quận 1, TP. Hồ Chí Minh",
        birthday = "10/01/1996",
        note = "Bạn cấp 3",
        isFavorite = true
    ),
    Contact(
        id = "7",
        name = "Vũ Đức Bình",
        phoneNumber = "0912345678",
        email = "binh.vuduc@outlook.com",
        address = "Cầu Giấy, Hà Nội",
        birthday = "02/04/1997",
        note = "Kỹ sư Android",
        isFavorite = false
    ),
    Contact(
        id = "8",
        name = "Lê Thùy D",
        phoneNumber = "0654123987",
        email = "thuyd.le@gmail.com",
        address = "Sơn Trà, Đà Nẵng",
        birthday = "18/07/2001",
        note = "Thiết kế UI/UX",
        isFavorite = true
    ),
    Contact(
        id = "9",
        name = "Bùi Quốc Dũng",
        phoneNumber = "0938765432",
        email = "dungbq@gmail.com",
        address = "Bình Thạnh, TP. Hồ Chí Minh",
        birthday = "22/12/1994",
        note = "Quản lý dự án",
        isFavorite = false
    ),
    Contact(
        id = "10",
        name = "Cao Văn E",
        phoneNumber = "0888555222",
        email = "caovane@gmail.com",
        address = "Nha Trang, Khánh Hòa",
        birthday = "30/08/1999",
        note = "Chuyên viên phân tích",
        isFavorite = false
    ),
    Contact(
        id = "11",
        name = "Đỗ Minh Hải",
        phoneNumber = "0977112233",
        email = "haidm@gmail.com",
        address = "Thanh Xuân, Hà Nội",
        birthday = "14/02/1998",
        note = "Thành viên nhóm",
        isFavorite = false
    ),
    Contact(
        id = "12",
        name = "Phạm Thu Hương",
        phoneNumber = "0966445566",
        email = "huongpham@gmail.com",
        address = "Hải Châu, Đà Nẵng",
        birthday = "08/10/2000",
        note = "Nhân viên nhân sự",
        isFavorite = true
    ),
    Contact(
        id = "13",
        name = "Trịnh Tuấn Kiệt",
        phoneNumber = "0944778899",
        email = "kiettuan@gmail.com",
        address = "Biên Hòa, Đồng Nai",
        birthday = "19/05/1997",
        note = "Bạn thể thao",
        isFavorite = false
    ),
    Contact(
        id = "14",
        name = "Mai Thị Lan",
        phoneNumber = "0933221100",
        email = "lanmai@gmail.com",
        address = "Vinh, Nghệ An",
        birthday = "03/11/1999",
        note = "Kế toán viên",
        isFavorite = false
    ),
    Contact(
        id = "15",
        name = "Võ Hoàng Long",
        phoneNumber = "0922883344",
        email = "longvo@gmail.com",
        address = "Huế",
        birthday = "11/09/1996",
        note = "Backend Developer",
        isFavorite = false
    ),
    Contact(
        id = "16",
        name = "Nguyễn Quỳnh Nga",
        phoneNumber = "0911556677",
        email = "quynhnga@gmail.com",
        address = "Nam Từ Liêm, Hà Nội",
        birthday = "27/06/2002",
        note = "Thực tập sinh",
        isFavorite = true
    ),
    Contact(
        id = "17",
        name = "Hoàng Gia Phúc",
        phoneNumber = "0909112233",
        email = "phuchg@gmail.com",
        address = "Vũng Tàu",
        birthday = "16/04/1995",
        note = "Giám đốc kỹ thuật",
        isFavorite = false
    ),
    Contact(
        id = "18",
        name = "Lương Trọng Quân",
        phoneNumber = "0988665544",
        email = "quanlt@gmail.com",
        address = "Long Xuyên, An Giang",
        birthday = "29/01/1998",
        note = "Chuyên viên bảo mật",
        isFavorite = false
    ),
    Contact(
        id = "19",
        name = "Phạm Minh Tuấn",
        phoneNumber = "0977334455",
        email = "tuanpm@gmail.com",
        address = "Đống Đa, Hà Nội",
        birthday = "07/07/1994",
        note = "Bạn thân",
        isFavorite = true
    ),
    Contact(
        id = "20",
        name = "Đinh Thanh Thảo",
        phoneNumber = "0966223344",
        email = "thaodt@gmail.com",
        address = "Quận 7, TP. Hồ Chí Minh",
        birthday = "24/10/2000",
        note = "Marketing Lead",
        isFavorite = false
    )
)

val sampleCallLogs = listOf(
    CallLog(
        id = "c1",
        contactName = "Lê Thị B",
        phoneNumber = "0111222333",
        type = CallType.INCOMING,
        time = "10:30",
        duration = "03:45"
    ),
    CallLog(
        id = "c2",
        contactName = "Lê Thùy D",
        phoneNumber = "0654123987",
        type = CallType.OUTGOING,
        time = "Hôm qua, 16:45",
        duration = "01:20"
    ),
    CallLog(
        id = "c3",
        contactName = "Cao Văn E",
        phoneNumber = "0888555222",
        type = CallType.MISSED,
        time = "Hôm qua, 14:10"
    ),
    CallLog(
        id = "c4",
        contactName = "Nguyễn Văn A",
        phoneNumber = "0123456789",
        type = CallType.OUTGOING,
        time = "03/11",
        duration = "05:12"
    ),
    CallLog(
        id = "c5",
        contactName = "Trần Minh C",
        phoneNumber = "0777888999",
        type = CallType.INCOMING,
        time = "02/11",
        duration = "00:55"
    ),
    CallLog(
        id = "c6",
        contactName = "Phạm Minh Tuấn",
        phoneNumber = "0977334455",
        type = CallType.MISSED,
        time = "01/11"
    )
)
