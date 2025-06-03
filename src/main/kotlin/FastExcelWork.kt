import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.delay
import org.dhatim.fastexcel.reader.ReadableWorkbook
import java.io.File
import java.io.FileInputStream

class FastExcelWork(private var filePath: String){
    fun setPath(path: String){
        filePath = path
    }

    fun readXlsxRow(sheetIndex: Int = 0, rowIndex: Int): Item? {
        var itemInRow:Item = Item("", "", Place())
        val file = File(filePath)
        val fis = FileInputStream(file)

        val wb = ReadableWorkbook(fis)
        val sheets = wb.sheets
        sheets.forEach {
            val index = it.index
//            println("sheet#$index rows = ${rows.count()}")
            if (index==sheetIndex){
                val rows = it.read()
                rows[rowIndex].forEach {cell ->
                    if (cell != null) {
                        val value = when (cell.rawValue) {
                            is String -> cell.rawValue as String
                            is Number -> (cell.rawValue as Number).toDouble()
                            is Boolean -> cell.rawValue as Boolean
                            else -> cell.asString() // Fallback for other types
                        }
                        println("  cell = $value")
                    }
                }
            }

        }
        return itemInRow
    }

//    suspend
    fun readCellsFromExcel(sheetIndex: Int = 0, firstRow: Int = 2, onAddItem: (Int) -> Unit): SnapshotStateList<Item?> {
        val itemsList = SnapshotStateList<Item?>()
        var rowsNum = 0
        rowsNum = getRowsCount(sheetIndex, firstRow)
        println("rowsNum = $rowsNum")
        for (i in firstRow..rowsNum+firstRow-1){
            itemsList.add(readXlsxRow(sheetIndex, i))
//            delay(1)
//            yield()
            onAddItem(i-1)
        }
        return itemsList
    }


    fun getRowsCount(sheetIndex: Int, fromRow: Int): Int {
        var rowsNum1 = fromRow
        val file = File(filePath)
        val fis = FileInputStream(file)

        val wb = ReadableWorkbook(fis)
        val sheets = wb.sheets
        sheets.forEach {
            val rows = it.read()
            val index = it.index
//            println("sheet#$index rows = ${rows.count()}")
            if (index==sheetIndex) rowsNum1 = rows.count() - rowsNum1
        }

        return rowsNum1
    }

    fun extractImageFromCell(
//        inputFilePath: String,
//        outputDir: String,
        sheetIndex: Int, // 0-based sheet index (e.g., 0 = first sheet)
        targetRow: Int, // 0-based row index
        targetCol: Int // 0-based column index
    ): String {
        var imgPath = ""

        return imgPath
    }


}