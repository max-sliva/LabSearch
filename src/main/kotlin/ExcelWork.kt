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
    private fun setItemField(cellIndexInRow: Int, itemInRow: Item, stringCellValue: String): Item {
        when (cellIndexInRow){ //1 - name, 2 - info, 3 - кол-во, 4 - img, 6 - place
            1 -> {itemInRow.name = stringCellValue}
            2 -> {itemInRow.info = stringCellValue}
            3 -> {itemInRow.info += ", кол-во: $stringCellValue"}
            4 -> {itemInRow.img = stringCellValue}
            6 -> {itemInRow.place = Place(name = StorageName.valueOf(stringCellValue))}
        }
        return itemInRow
    }

    fun readXlsxRow(sheetIndex: Int = 0, rowIndex: Int): Item? {
        var itemInRow:Item = Item("", "", Place())
        try {
            FileInputStream(filePath).use { fis ->
                WorkbookFactory.create(fis).use { workbook ->
                    val sheet = workbook.getSheetAt(sheetIndex) // Get the sheet (e.g., first sheet)

                    val row = sheet.getRow(rowIndex) // Get the target row
                    if (row == null) {
                        println("Row $rowIndex does not exist.")
                        return null
                    }
//                    var name = ""
//                    var place = ""
//                    var info = ""
//                    var img = ""
                    for (cell in row) { // Iterate over cells in the row
                        val cellIndexInRow = row.indexOf(cell)
//                        print(" || cell's number in row = $cellIndexInRow ") //1 - name, 2 - info, 3 - кол-во, 4 - img, 6 - place
                        when (cell.cellType) {
                            CellType.STRING -> {
//                                if (cell.stringCellValue.length<16) print(" | String: ${cell.stringCellValue} ")
//                                else print("| String: ${cell.stringCellValue.substring(0..15)}")
                                itemInRow = setItemField(cellIndexInRow, itemInRow, cell.stringCellValue)
                            }
                            CellType.NUMERIC -> {
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    print(" | Date: ${cell.dateCellValue}")
                                } else {
//                                    print(" | Number: ${cell.numericCellValue}")
                                    itemInRow = setItemField(cellIndexInRow, itemInRow, cell.numericCellValue.toInt().toString())
                                }
                            }
                            CellType.BOOLEAN -> print(" | Boolean: ${cell.booleanCellValue}")
                            CellType.FORMULA -> handleFormulaCell(cell)
                            else -> {
//                                print(" | Unsupported cell type")
//                                getImageFromCell(sheetIndex, rowIndex, cell.columnIndex)
                                itemInRow.img = extractImageFromCell(sheetIndex, rowIndex, cell.columnIndex)
                            }
                        }
                    }
                //    println()
//                    println("item: $itemInRow")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return itemInRow
    }

    private fun handleFormulaCell(cell: Cell) {
        when (cell.cachedFormulaResultType) {
            CellType.STRING -> print(" | Formula (String): ${cell.stringCellValue}")
            CellType.NUMERIC -> print(" | Formula (Number): ${cell.numericCellValue}")
            CellType.BOOLEAN -> print(" | Formula (Boolean): ${cell.booleanCellValue}")
            else -> print(" | Unsupported formula result")
        }
    }


    fun readCellsFromExcel(sheetIndex: Int = 0, firstRow: Int = 2): ArrayList<Item?> {
        val itemsList = ArrayList<Item?>()
        var rowsNum = 0
        FileInputStream(filePath).use { fis ->
            WorkbookFactory.create(fis).use { workbook ->
                val sheet = workbook.getSheetAt(sheetIndex) // Get the sheet (e.g., first sheet)
                rowsNum = sheet.lastRowNum
            }
        }
        println("rowsNum = $rowsNum")
        for (i in firstRow..rowsNum){
            itemsList.add(readXlsxRow(sheetIndex, i))
        }
        return itemsList
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
    ): String {
        var imgPath = ""
        val workbook = WorkbookFactory.create(File(filePath)).use { workbook ->
            if (workbook !is XSSFWorkbook) {
                throw IllegalArgumentException("Only XLSX files are supported")
            }

            val sheet = workbook.getSheetAt(sheetIndex)
            val drawings = sheet.drawingPatriarch ?: return ""

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
                            "Images/image_${sheetIndex}_${targetRow}_${targetCol}_$index.$extension"
                        )
                        FileOutputStream(outputFile).use { fos ->
                            fos.write(imageBytes)
                        }
//                        println(" Saved image: ${outputFile.absolutePath}")
                        imgPath = outputFile.absolutePath
                        imgPath = imgPath.substringAfter("LabSearch\\")
//                        println("imgPath after cut = $imgPath")
//                        imgPath = imgPath.substringAfter("\\\\")
                    }
                }
            }
        }
        return imgPath
    }

//    fun getImageFromCell(sheetIndex: Int, // 0-based sheet index
//                         targetRow: Int, // 0-based row index
//                         targetCol: Int) {
//        FileInputStream(filePath).use { inputStream ->
//            XSSFWorkbook(inputStream).use { workbook ->
//                // Iterate through all sheets in the workbook
////                val sheet = workbook.getSheetAt(sheetIndex)
//                val drawings = getDrawingsFromExcelSheet(sheetIndex)
//
//                // Iterate through all images in the sheet
////                for (drawing in drawings!!) {
//                drawings?.forEach { shape ->
//                    if (shape is XSSFPicture) {
//                        val anchor = shape.clientAnchor as XSSFClientAnchor
//                        // Check if image is anchored to the target cell's top-left corner
//                        if (anchor.row1 == targetRow && anchor.col1.toInt() == targetCol) {
////                            println("cell[$targetRow,$targetCol] has image ")
//                            val pictureData = shape.pictureData
//                            val data = pictureData.data
//                            // Generate unique file name
//                            val format = pictureData.suggestFileExtension()
//                            val outputFile = "image_${targetRow}_$targetCol.$format"
//                            // Save the image
//                            FileOutputStream(outputFile).use { fos ->
//                                fos.write(data)
//                            }
////                            Thread.sleep(1000)
//                            println("found images: $outputFile")
//
//                        }
//                    }
//                }
//            }
//        }
//    }

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