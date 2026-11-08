package com.example.phone_mobile.ui.screens.more

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.phone_mobile.R
import com.example.phone_mobile.data.model.Contact
import com.example.phone_mobile.ui.theme.PrimaryBlue
import com.example.phone_mobile.util.QrCodeUtils
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@Composable
fun QrScannerScreen(
    onContactScanned: (Contact) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    @Suppress("DEPRECATION")
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Cần cấp quyền Camera để quét mã QR", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    BackHandler {
        onBackClick()
    }

    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }
    var isTorchOn by remember { mutableStateOf(false) }
    var isScanned by remember { mutableStateOf(false) }

    // Xử lý chọn ảnh từ Thư viện / Gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri)) { decoder, _, _ ->
                        decoder.isMutableRequired = true
                    }
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }

                if (bitmap != null) {
                    val inputImage = InputImage.fromBitmap(bitmap, 0)
                    val scanner = BarcodeScanning.getClient(
                        BarcodeScannerOptions.Builder()
                            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                            .build()
                    )

                    scanner.process(inputImage)
                        .addOnSuccessListener { barcodes ->
                            val qr = barcodes.firstOrNull()?.rawValue
                            if (!qr.isNullOrBlank()) {
                                isScanned = true
                                val contact = QrCodeUtils.parseContactFromRawText(qr)
                                onContactScanned(contact)
                            } else {
                                // Thử fallback qua ZXing
                                val zxingResult = QrCodeUtils.decodeQrFromBitmap(bitmap)
                                if (!zxingResult.isNullOrBlank()) {
                                    isScanned = true
                                    val contact = QrCodeUtils.parseContactFromRawText(zxingResult)
                                    onContactScanned(contact)
                                } else {
                                    Toast.makeText(context, "Không tìm thấy mã QR trong ảnh này", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        .addOnFailureListener {
                            val zxingResult = QrCodeUtils.decodeQrFromBitmap(bitmap)
                            if (!zxingResult.isNullOrBlank()) {
                                isScanned = true
                                val contact = QrCodeUtils.parseContactFromRawText(zxingResult)
                                onContactScanned(contact)
                            } else {
                                Toast.makeText(context, "Không đọc được mã QR từ ảnh", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi đọc ảnh: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Black,
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
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "Quay lại",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Quét mã QR",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (hasCameraPermission) {
                // Live CameraX Preview
                val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
                DisposableEffect(Unit) {
                    onDispose {
                        cameraExecutor.shutdown()
                    }
                }

                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx).apply {
                            scaleType = PreviewView.ScaleType.FILL_CENTER
                        }

                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()

                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val scannerOptions = BarcodeScannerOptions.Builder()
                                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                                .build()
                            val barcodeScanner = BarcodeScanning.getClient(scannerOptions)

                            val imageAnalysis = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                                .also { analysis ->
                                    analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                                        @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
                                        val mediaImage = imageProxy.image
                                        if (mediaImage != null && !isScanned) {
                                            val image = InputImage.fromMediaImage(
                                                mediaImage,
                                                imageProxy.imageInfo.rotationDegrees
                                            )
                                            barcodeScanner.process(image)
                                                .addOnSuccessListener { barcodes ->
                                                    val barcode = barcodes.firstOrNull()
                                                    if (barcode != null && !isScanned) {
                                                        val rawText = barcode.rawValue
                                                        if (!rawText.isNullOrBlank()) {
                                                            isScanned = true
                                                            val contact = QrCodeUtils.parseContactFromRawText(rawText)
                                                            onContactScanned(contact)
                                                        }
                                                    }
                                                }
                                                .addOnCompleteListener {
                                                    imageProxy.close()
                                                }
                                        } else {
                                            imageProxy.close()
                                        }
                                    }
                                }

                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                            try {
                                cameraProvider.unbindAll()
                                val camera: Camera = cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview,
                                    imageAnalysis
                                )
                                cameraControl = camera.cameraControl
                            } catch (exc: Exception) {
                                exc.printStackTrace()
                            }
                        }, ContextCompat.getMainExecutor(ctx))

                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Hiển thị giao diện khi chưa cấp quyền Camera
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Ứng dụng cần quyền Camera để quét mã QR danh thiếp",
                        color = Color.White,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text("Cấp quyền Camera", color = Color.White)
                    }
                }
            }

            // Lớp phủ Scrim bán trong suốt và khung quét 4 góc bo xanh dương (chuẩn 100% như ảnh)
            ScannerOverlay(
                modifier = Modifier.fillMaxSize()
            )

            // Khối nội dung trên lớp phủ (Dòng chữ thông báo & 2 nút điều khiển dưới đáy)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                // Nút bật đèn Flash (Trái) & Nút chọn ảnh từ Thư viện (Phải)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Nút Bật/Tắt đèn Flash
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                if (isTorchOn) Color(0xFFFACC15) else Color(0x992B2D31)
                            )
                            .clickable {
                                isTorchOn = !isTorchOn
                                cameraControl?.enableTorch(isTorchOn)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_flashlight),
                            contentDescription = "Bật đèn flash",
                            tint = if (isTorchOn) Color.Black else Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // 2. Nút Chọn ảnh từ Thư viện (Gallery)
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0x992B2D31))
                            .clickable {
                                galleryLauncher.launch("image/*")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_gallery),
                            contentDescription = "Chọn ảnh từ thư viện",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Lớp phủ kính ngắm với 4 góc xanh dương nổi bật và viền tối bao quanh (y như hình thiết kế)
 */
@Composable
private fun ScannerOverlay(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val boxSize = 250.dp.toPx()
            val left = (size.width - boxSize) / 2f
            val top = (size.height - boxSize) / 2f - 40.dp.toPx()
            val right = left + boxSize
            val bottom = top + boxSize
            val cornerRadius = 18.dp.toPx()

            val rectPath = Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = Rect(left, top, right, bottom),
                        cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                    )
                )
            }

            // Làm mờ vùng ngoài ô quét
            clipPath(rectPath, clipOp = ClipOp.Difference) {
                drawRect(color = Color(0x55000000))
            }

            // Vẽ 4 góc bo màu xanh dương đậm nét chuẩn như ảnh
            val strokeWidth = 4.5.dp.toPx()
            val cornerLength = 32.dp.toPx()
            val cornerBlue = Color(0xFF2F80ED)

            // Góc trên-trái
            drawLine(
                color = cornerBlue,
                start = Offset(left, top + cornerRadius),
                end = Offset(left, top + cornerLength),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawArc(
                color = cornerBlue,
                startAngle = 180f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(left, top),
                size = Size(cornerRadius * 2, cornerRadius * 2),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            drawLine(
                color = cornerBlue,
                start = Offset(left + cornerRadius, top),
                end = Offset(left + cornerLength, top),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            // Góc trên-phải
            drawLine(
                color = cornerBlue,
                start = Offset(right - cornerLength, top),
                end = Offset(right - cornerRadius, top),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawArc(
                color = cornerBlue,
                startAngle = 270f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(right - cornerRadius * 2, top),
                size = Size(cornerRadius * 2, cornerRadius * 2),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            drawLine(
                color = cornerBlue,
                start = Offset(right, top + cornerRadius),
                end = Offset(right, top + cornerLength),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            // Góc dưới-trái
            drawLine(
                color = cornerBlue,
                start = Offset(left, bottom - cornerLength),
                end = Offset(left, bottom - cornerRadius),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawArc(
                color = cornerBlue,
                startAngle = 90f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(left, bottom - cornerRadius * 2),
                size = Size(cornerRadius * 2, cornerRadius * 2),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            drawLine(
                color = cornerBlue,
                start = Offset(left + cornerRadius, bottom),
                end = Offset(left + cornerLength, bottom),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )

            // Góc dưới-phải
            drawLine(
                color = cornerBlue,
                start = Offset(right - cornerLength, bottom),
                end = Offset(right - cornerRadius, bottom),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            drawArc(
                color = cornerBlue,
                startAngle = 0f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(right - cornerRadius * 2, bottom - cornerRadius * 2),
                size = Size(cornerRadius * 2, cornerRadius * 2),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            drawLine(
                color = cornerBlue,
                start = Offset(right, bottom - cornerRadius),
                end = Offset(right, bottom - cornerLength),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }

        // Dòng chữ hướng dẫn: "Hướng camera vào mã QR để quét" nằm ngay dưới ô quét
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 220.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xCC1A1C1E))
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Hướng camera vào mã QR\nđể quét",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
