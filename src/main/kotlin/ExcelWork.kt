import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.FileInputStream
import java.io.FileOutputStream
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFPicture
import java.io.IOException

fun readXlsxRow(filePath: String, sheetIndex: Int = 0, rowIndex: Int) {
    try {
        FileInputStream(filePath).use { fis ->
            WorkbookFactory.create(fis).use { workbook ->
                // Get the sheet (e.g., first sheet)
                val sheet = workbook.getSheetAt(sheetIndex)

                // Get the target row
                val row = sheet.getRow(rowIndex)
                if (row == null) {
                    println("Row $rowIndex does not exist.")
                    return
                }

                // Iterate over cells in the row
                for (cell in row) {
                    when (cell.cellType) {
                        CellType.STRING -> println("String: ${cell.stringCellValue}")
                        CellType.NUMERIC -> {
                            if (DateUtil.isCellDateFormatted(cell)) {
                                println("Date: ${cell.dateCellValue}")
                            } else {
                                println("Number: ${cell.numericCellValue}")
                            }
                        }
                        CellType.BOOLEAN -> println("Boolean: ${cell.booleanCellValue}")
                        CellType.FORMULA -> handleFormulaCell(cell)
                        else -> println("Unsupported cell type")
                    }
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

private fun handleFormulaCell(cell: Cell) {
    when (cell.cachedFormulaResultType) {
        CellType.STRING -> println("Formula (String): ${cell.stringCellValue}")
        CellType.NUMERIC -> println("Formula (Number): ${cell.numericCellValue}")
        CellType.BOOLEAN -> println("Formula (Boolean): ${cell.booleanCellValue}")
        else -> println("Unsupported formula result")
    }
}


fun  readCellsFromExcel(){

}

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