import java.io.FileInputStream
import java.io.FileOutputStream
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFPicture

fun extractImagesFromExcel(filePath: String) {
    var imageCounter = 0

    FileInputStream(filePath).use { inputStream ->
        XSSFWorkbook(inputStream).use { workbook ->
            // Iterate through all sheets in the workbook
            for (sheet in workbook) {
                // Get the drawing patriarch (container for shapes)
                val drawing = sheet.drawingPatriarch
                drawing?.forEach { shape ->
                    // Check if the shape is an image
                    if (shape is XSSFPicture) {
                        val pictureData = shape.pictureData
                        // Generate unique file name
                        val format = pictureData.suggestFileExtension()
                        val outputFile = "image_${++imageCounter}.$format"

                        // Save the image
//                        FileOutputStream(outputFile).use { fos ->
//                            fos.write(pictureData.data)
//                        }
                        println("found images: $outputFile")
                    }
                }
            }
        }
    }
}