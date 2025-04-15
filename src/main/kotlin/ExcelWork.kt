import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFClientAnchor
import org.apache.poi.xssf.usermodel.XSSFDrawing
import java.io.FileInputStream
import java.io.FileOutputStream
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFPicture
import java.io.File
import java.io.IOException
class ExcelWork(private val filePath: String) {
    fun readXlsxRow(sheetIndex: Int = 0, rowIndex: Int) {
        try {
            FileInputStream(filePath).use { fis ->
                WorkbookFactory.create(fis).use { workbook ->
                    val sheet = workbook.getSheetAt(sheetIndex) // Get the sheet (e.g., first sheet)

                    val row = sheet.getRow(rowIndex) // Get the target row
                    if (row == null) {
                        println("Row $rowIndex does not exist.")
                        return
                    }

                    for (cell in row) { // Iterate over cells in the row
                        when (cell.cellType) {
                            CellType.STRING -> if (cell.stringCellValue.length<16) print(" | String: ${cell.stringCellValue} ")
                                                else print("| String: ${cell.stringCellValue.substring(0..15)}")
                            CellType.NUMERIC -> {
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    print(" | Date: ${cell.dateCellValue}")
                                } else {
                                    print(" | Number: ${cell.numericCellValue}")
                                }
                            }
                            CellType.BOOLEAN -> print(" | Boolean: ${cell.booleanCellValue}")
                            CellType.FORMULA -> handleFormulaCell(cell)
                            else -> {
                                print(" | Unsupported cell type")
//                                getImageFromCell(sheetIndex, rowIndex, cell.columnIndex)
                                extractImageFromCell(sheetIndex, rowIndex, cell.columnIndex)
                            }
                        }
                    }
                    println()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun handleFormulaCell(cell: Cell) {
        when (cell.cachedFormulaResultType) {
            CellType.STRING -> print(" | Formula (String): ${cell.stringCellValue}")
            CellType.NUMERIC -> print(" | Formula (Number): ${cell.numericCellValue}")
            CellType.BOOLEAN -> print(" | Formula (Boolean): ${cell.booleanCellValue}")
            else -> print(" | Unsupported formula result")
        }
    }


    fun readCellsFromExcel(sheetIndex: Int = 0, firstRow: Int = 2) {
        var rowsNum = 0
        FileInputStream(filePath).use { fis ->
            WorkbookFactory.create(fis).use { workbook ->
                val sheet = workbook.getSheetAt(sheetIndex) // Get the sheet (e.g., first sheet)
                rowsNum = sheet.lastRowNum
            }
        }
        println("rowsNum = $rowsNum")
        for (i in firstRow..rowsNum){
            readXlsxRow(sheetIndex, i)
        }
    }

    fun getDrawingsFromExcelSheet(sheetIndex: Int = 0): XSSFDrawing? {
        var drawings: XSSFDrawing?
        FileInputStream(filePath).use { inputStream ->
            XSSFWorkbook(inputStream).use { workbook ->
                // Iterate through all sheets in the workbook
                val sheet = workbook.getSheetAt(sheetIndex)
                drawings = sheet.drawingPatriarch
            }
        }
        return drawings
    }

    fun extractImageFromCell(
//        inputFilePath: String,
//        outputDir: String,
        sheetIndex: Int, // 0-based sheet index (e.g., 0 = first sheet)
        targetRow: Int, // 0-based row index
        targetCol: Int // 0-based column index
    ) {
        val workbook = WorkbookFactory.create(File(filePath)).use { workbook ->
            if (workbook !is XSSFWorkbook) {
                throw IllegalArgumentException("Only XLSX files are supported")
            }

            val sheet = workbook.getSheetAt(sheetIndex)
            val drawings = sheet.drawingPatriarch ?: return

            // Iterate through all images in the sheet
            drawings.forEachIndexed { index, drawing ->
                if (drawing is XSSFPicture) {
                    val anchor = drawing.clientAnchor as XSSFClientAnchor

                    // Check if image is anchored to the target cell (top-left corner)
                    if (anchor.row1 == targetRow && anchor.col1.toInt() == targetCol) {
                        val pictureData = drawing.pictureData
                        val imageBytes = pictureData.data
                        val extension = pictureData.suggestFileExtension()

                        // Save the image
                        val outputFile = File(
                          //  outputDir,
                            "image_${sheetIndex}_${targetRow}_${targetCol}_$index.$extension"
                        )
                        FileOutputStream(outputFile).use { fos ->
                            fos.write(imageBytes)
                        }
                        println("Saved image: ${outputFile.absolutePath}")
                    }
                }
            }
        }
    }

    fun getImageFromCell(sheetIndex: Int, // 0-based sheet index
                         targetRow: Int, // 0-based row index
                         targetCol: Int) {
        FileInputStream(filePath).use { inputStream ->
            XSSFWorkbook(inputStream).use { workbook ->
                // Iterate through all sheets in the workbook
//                val sheet = workbook.getSheetAt(sheetIndex)
                val drawings = getDrawingsFromExcelSheet(sheetIndex)

                // Iterate through all images in the sheet
//                for (drawing in drawings!!) {
                drawings?.forEach { shape ->
                    if (shape is XSSFPicture) {
                        val anchor = shape.clientAnchor as XSSFClientAnchor
                        // Check if image is anchored to the target cell's top-left corner
                        if (anchor.row1 == targetRow && anchor.col1.toInt() == targetCol) {
//                            println("cell[$targetRow,$targetCol] has image ")
                            val pictureData = shape.pictureData
                            val data = pictureData.data
                            // Generate unique file name
                            val format = pictureData.suggestFileExtension()
                            val outputFile = "image_${targetRow}_$targetCol.$format"
                            // Save the image
                            FileOutputStream(outputFile).use { fos ->
                                fos.write(data)
                            }
//                            Thread.sleep(1000)
                            println("found images: $outputFile")

                        }
                    }
                }
            }
        }
    }

    fun extractImagesFromExcel() {
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

//                             Save the image
                        FileOutputStream(outputFile).use { fos ->
                            fos.write(pictureData.data)
                        }
                            println("found images: $outputFile")
                        }
                    }
                }
            }
        }
    }
}