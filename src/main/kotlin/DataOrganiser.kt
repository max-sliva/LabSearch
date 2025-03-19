import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

//var objList = remember { mutableListOf(Thing()) }

@Composable
fun OrganiserGUI() {
    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize(), //заполняем всё доступное пространство
            horizontalAlignment = Alignment.CenterHorizontally, //по центру горизонтально
        ) {
            //todo сделать возможность кликом по месту на картинке посмотреть, какие там объекты, и добавить, или импортировать из файла
        }
    }
}

@Composable
fun DbGUI(objList: MutableList<Item>) {
    MaterialTheme {
        //todo сделать интерфейс для работы с БД (просмотр всех записей, изменение, добавление, удаление)
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .border(2.dp, Color.Blue)
            ) {
                Button(
                    onClick = {
                        println("load")
                    }

                ) {
                    Text(text = "Загрузить БД")
                }
            }
//            TableWork()
            TableExample(objList)
            //CustomTable()
        }
    }
}

@Composable
fun TableExample(objList: MutableList<Item>) {
    val columns = 4
//    val data = listOf(
//        listOf("Cell A1", "Cell B1", "Cell C1"),
//        listOf("Cell A2", "Cell B2", "Cell C2"),
//    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
//        data.forEach { row ->
//            items(row.size) { index ->
//                Text(
//                    text = row[index],
//                    modifier = Modifier.padding(8.dp)
//                )
//            }
//        }
        objList.forEach { obj ->
            println("obj = ${ obj.getListOfValues() }")
            val row = obj.getListOfValues()
            items(row.size) { index ->
                Text(
                    text = row[index],
                    modifier = Modifier.padding(8.dp)
                        .border(2.dp, Color.Blue)
                )
            }
        }
//                for (property in (obj as Item).javaClass.declaredFields) {
//            for (property in Item::class.memberProperties) {
//                property.isAccessible = true // Required for private properties
////                    val value = property.get(obj)
//                println("${property.name} = ${property.get(obj)}")
            }
        }


    @Composable
    fun CustomTable() {
        Column {
            // Header Row
            Row {
                Text("Header A")
                Text("Header B")
                Text("Header C")
            }

            // Data Rows
//        listOf(
//            listOf("Cell A1", "Cell B1", "Cell C1"),
//            listOf("Cell A2", "Cell B2", "Cell C2"),
//        )
//            objList
//            .forEach { obj ->
//                for (property in (obj as Item).javaClass.declaredFields) {
//                    property.isAccessible = true
//                    println("${property.name} = ${property.get(obj)}")
//                }
//            Row {
////                obj.forEach { cell ->
////                    Text(cell)
////                }
//
//            }
        }
    }
//}

@Composable
fun TableWork() {

}

fun main() = application {
    val dbWork = DBwork()
//    dbWork.getAllCollectionsFromDB()
    println("-----------------------------------------------------------")
//    dbWork.deleteObjectFromCollectiobById("1", "Items")
//    dbWork.deleteObjectFromCollectiobById("2", "Items")
    var objList: MutableList<Item>
    dbWork.getAllObjectsForCollection("Items").also { objList = it as MutableList<Item> }
    val id = dbWork.getLastId(collectionName = "Items")
    println("last id = $id")

    val windowState = rememberWindowState(
        position = WindowPosition(Alignment.Center)
    )
    Window(
        state = windowState,
        onCloseRequest = ::exitApplication
    ) {
        DbGUI(objList)
    }
}
