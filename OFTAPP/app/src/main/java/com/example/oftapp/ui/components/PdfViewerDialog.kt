package com.example.oftapp.ui.components

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.io.File
import java.io.FileOutputStream

@Composable
fun PdfViewerDialog(
    tituloDocumento: String,
    uriString: String? = null,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var currentPageIndex by remember { mutableIntStateOf(0) }
    var totalPages by remember { mutableIntStateOf(1) }
    var bitmapPage by remember { mutableStateOf<Bitmap?>(null) }
    var errorCarga by remember { mutableStateOf<String?>(null) }

    DisposableEffect(uriString) {
        var fileDescriptor: ParcelFileDescriptor? = null
        var renderer: PdfRenderer? = null
        try {
            if (!uriString.isNullOrBlank()) {
                val uri = Uri.parse(uriString)
                if (uri.scheme == "file") {
                    val file = File(uri.path ?: "")
                    fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                } else {
                    val downloadedFile = File(context.cacheDir, "remote_report.pdf")
                    if (uri.scheme == "http" || uri.scheme == "https") {
                        val connection = java.net.URL(uriString).openConnection() as java.net.HttpURLConnection
                        try {
                            connection.connectTimeout = 15000
                            connection.readTimeout = 15000
                            connection.instanceFollowRedirects = true
                            connection.inputStream.use { input ->
                                FileOutputStream(downloadedFile).use { output -> input.copyTo(output) }
                            }
                        } finally {
                            connection.disconnect()
                        }
                        fileDescriptor = ParcelFileDescriptor.open(downloadedFile, ParcelFileDescriptor.MODE_READ_ONLY)
                    } else {
                        fileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
                    }
                }
            } else {
                val pdfFile = File(context.cacheDir, "temp_report.pdf")
                if (!pdfFile.exists()) {
                    val document = PdfDocument()
                    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                    val page = document.startPage(pageInfo)
                    val canvas = page.canvas
                    val paint = Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 20f
                        isAntiAlias = true
                    }
                    canvas.drawText("OFTAPP - INFORME CLÍNICO OFTALMOLÓGICO", 40f, 60f, paint)
                    paint.textSize = 14f
                    canvas.drawText("Documento generado mediante PdfRenderer Nativo", 40f, 100f, paint)
                    canvas.drawText("Documento: $tituloDocumento", 40f, 130f, paint)
                    canvas.drawText("Estado: Validadas normas WCAG 2.2 AA", 40f, 160f, paint)
                    canvas.drawText("Agudeza Visual: OD 20/20 · OI 20/25", 40f, 200f, paint)
                    canvas.drawText("Presión Intraocular: OD 14 mmHg · OI 15 mmHg", 40f, 230f, paint)
                    document.finishPage(page)
                    FileOutputStream(pdfFile).use { out -> document.writeTo(out) }
                    document.close()
                }
                fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
            }

            if (fileDescriptor != null) {
                renderer = PdfRenderer(fileDescriptor)
                totalPages = renderer.pageCount
                if (totalPages > 0) {
                    val page = renderer.openPage(0)
                    val bitmap = Bitmap.createBitmap(page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    bitmapPage = bitmap
                    page.close()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            errorCarga = "No se pudo renderizar el PDF: ${e.localizedMessage}"
        }

        onDispose {
            try {
                renderer?.close()
                fileDescriptor?.close()
            } catch (_: Exception) {}
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(520.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Visor PDF (PdfRenderer Nativo)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = tituloDocumento,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (errorCarga != null) {
                        Text(
                            text = errorCarga ?: "",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else bitmapPage?.let { bmt ->
                        Image(
                            bitmap = bmt.asImageBitmap(),
                            contentDescription = "Página del PDF $tituloDocumento",
                            modifier = Modifier.fillMaxSize()
                        )
                    } ?: Text("Cargando vista previa de PDF...", color = MaterialTheme.colorScheme.onSurface)
                }

                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Página ${currentPageIndex + 1} de $totalPages",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedButton(onClick = onDismiss) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}
