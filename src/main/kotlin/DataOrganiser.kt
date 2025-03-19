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
import com.couchbase.lite.*


@Composable
fun OrganiserGUI() {
    MaterialTheme {
        Column (
            modifier = Modifier.fillMaxSize(), //заполняем всё доступное пространство
            horizontalAlignment = Alignment.CenterHorizontally, //по центру горизонтально
        ){
            //todo сделать возможность кликом по месту на картинке посмотреть, какие там объекты, и добавить, или импортировать из файла
        }
    }
}

@Composable
fun DbGUI(){
    MaterialTheme{
    //todo сделать интерфейс для работы с БД (просмотр всех записей, изменение, добавление, удаление)
        Column (
            modifier = Modifier.fillMaxSize(),
        ){
            Row(modifier = Modifier
                .wrapContentHeight()
                .border(2.dp, Color.Blue)
            ){
                Button(
                    onClick = {
                        println("load")
                    }

                ){
                    Text(text = "Загрузить БД")
                }
            }
//            TableWork()
            TableExample()
            //CustomTable()
        }
    }
}

@Composable
fun TableExample() {
    val columns = 3
    val data = listOf(
        listOf("Cell A1", "Cell B1", "Cell C1"),
        listOf("Cell A2", "Cell B2", "Cell C2"),
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        data.forEach { row ->
            items(row.size) { index ->
                Text(
                    text = row[index],
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
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
        listOf(
            listOf("Cell A1", "Cell B1", "Cell C1"),
            listOf("Cell A2", "Cell B2", "Cell C2"),
        ).forEach { row ->
            Row {
                row.forEach { cell ->
                    Text(cell)
                }
            }
        }
    }
}

@Composable
fun TableWork(){

}

fun main() = application {
    val dbWork = DBwork()
//    dbWork.getAllCollectionsFromDB()
    println("-----------------------------------------------------------")
//    dbWork.deleteObjectFromCollectiobById("1", "Items")
//    dbWork.deleteObjectFromCollectiobById("2", "Items")
//    var item = Item(id = "1","Arduino", Place(name=StorageName.CENTER_TABLES))
//    dbWork.addObjectToCollection(id="1", obj=item, collection = "Items")
//    item = Item(id = "2","Raspberry", Place(name=StorageName.BACK_SHELF))
//    dbWork.addObjectToCollection(id="2", obj=item, collection = "Items")
//    val res = dbWork.getLastDocument(collectionName = "Items")
//    println("res = ${res!!.toMap()}")
//    val items = res.getDictionary("Items")
//    println("items = $items")
    dbWork.getAllObjectsForCollection("Items")
    val id = dbWork.getLastId(collectionName = "Items")
//    val id = res.getString("id")
    println("last id = $id")
//    val data = DataHolder()
//    var mutableDoc = MutableDocument()
//    val collectionItems = database.getCollection("Items")
//    val
//    val item = data.things?.get(0)
//    val item = Item(id = "1","Arduino", Place(name=StorageName.CUSTOM_PLACE))
//    val gson = Gson()
//    val json = gson.toJson(item)
////    println("json item = $json")
//    mutableDoc = MutableDocument(item.id).setJSON(json)
////    database.save(mutableDoc)
//    collectionItems.save(mutableDoc)
//    val json = Json.encodeToJsonElement(item)
//    println("in db ${ database.count } items")
//    database.
//    getAllCollectionsFromDB(database)
//    deleteObjectFromCollectiobById(database, id = "1", collection = "Items")
    val windowState = rememberWindowState(
        position = WindowPosition(Alignment.Center)
    )
    Window(
        state = windowState,
        onCloseRequest = ::exitApplication
    ) {
        DbGUI()
    }
}
