import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.delay

class FastExcelWork(private var filePath: String){
    fun setPath(path: String){
        filePath = path
    }

    fun readXlsxRow(sheetIndex: Int = 0, rowIndex: Int): Item? {
        var itemInRow:Item = Item("", "", Place())

        return itemInRow
    }

    suspend fun readCellsFromExcel(sheetIndex: Int = 0, firstRow: Int = 2, onAddItem: (Int) -> Unit): SnapshotStateList<Item?> {
        val itemsList = SnapshotStateList<Item?>()
        var rowsNum = 0
        rowsNum = getRowsCount(sheetIndex, rowsNum)
        println("rowsNum = $rowsNum")
        for (i in firstRow..rowsNum){
            itemsList.add(readXlsxRow(sheetIndex, i))
            delay(1)
//            yield()
            onAddItem(i-1)
        }
        return itemsList
    }


    fun getRowsCount(sheetIndex: Int, rowsNum: Int): Int {
        var rowsNum1 = rowsNum

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