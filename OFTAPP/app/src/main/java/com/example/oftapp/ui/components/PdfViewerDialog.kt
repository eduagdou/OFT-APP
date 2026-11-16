package com.example.oftapp.ui.components

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
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
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var currentPageIndex by remember { mutableIntStateOf(0) }
    var totalPages by remember { mutableIntStateOf(1) }
    var bitmapPage by remember { mutableStateOf<Bitmap?>(null) }

    // Generación simulada de un PDF temporal mediante PdfRenderer nativo
    DisposableEffect(Unit) {
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

        var fileDescriptor: ParcelFileDescriptor? = null
        var renderer: PdfRenderer? = null
        try {
            fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
            renderer = PdfRenderer(fileDescriptor)
            totalPages = renderer.pageCount
            val page = renderer.openPage(0)
            val bitmap = Bitmap.createBitmap(page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            bitmapPage = bitmap
            page.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        onDispose {
            renderer?.close()
            fileDescriptor?.close()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(520.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
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
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = tituloDocumento,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color(0xFFF0F2F5), shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    bitmapPage?.let { bmt ->
                        Image(
                            bitmap = bmt.asImageBitmap(),
                            contentDescription = "Página del PDF $tituloDocumento",
                            modifier = Modifier.fillMaxSize()
                        )
                    } ?: Text("Cargando vista previa de PDF...")
                }

                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Página ${currentPageIndex + 1} de $totalPages",
                        style = MaterialTheme.typography.labelMedium
                    )

                    OutlinedButton(onClick = onDismiss) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}
