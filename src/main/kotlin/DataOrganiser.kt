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

//fun getAllCollectionsFromDB(db: Database) {
////    val db = Database("mydatabase")
//
//// Get all scopes
//    db.scopes.forEach { scope ->
//        println("Scope :: ${scope.name}")
//
//        // Get all collections for the current scope
//        scope.collections.forEach { collection ->
//            println("Collection :: ${collection.name}")
////            println("in db ${ db.count } items")
//            // Create a SQL++ query to get all documents from the collection
//            val query: Query = QueryBuilder.select(SelectResult.all()).from(DataSource.collection(collection))
//
//            query.execute().use { result ->
//                for (row in result) {
//                    val dict: Dictionary? = row.getDictionary(0)
////                    val documentId = row.getString("id")
////                    println("Document ID: $documentId")
//                    // Process each document here
//                    println("dict = $dict")
//                    val nameValue = dict!!.getString("name")
//                    print("item name = $nameValue,")
//                    val placeValue = dict.getDictionary("place")!!.getString("name")
//                    println(" place name = $placeValue")
//                }
//            }
////            val documentId = "6d53eeda-b16e-494a-a7e1-f21449ab8acf"
////            val sourceDoc: Document? = db.getDocument(documentId)
////            db.delete(sourceDoc!!)
//            //для получения id объектов
////            val query2: Query = QueryBuilder.select(SelectResult.expression(Meta.id))
////                .from(database(db))
////
////            val resultSet = query2.execute()
////            for (result in resultSet) {
////                val documentId = result.getString("id")
////                println("Document ID: $documentId")
////            }
////            println("in db ${ db.count } items")
//            val collectionItems = db.getCollection("Items")
//            println("in collection 'items' ${collectionItems!!.count} objects")
//        }
//    }
//}


fun main() = application {
    //todo сделать отдельный класс для работы с БД
//    CouchbaseLite.init()
//    val cfg = DatabaseConfiguration()
//    var database = Database("mydb", cfg)
////    val collection = database.collection("myCollection")
//    val collectionPlaces = database.createCollection("Places")
//    val collectionItems = database.createCollection("Items")
    val dbWork = DBwork()

    val data = DataHolder()
    var mutableDoc = MutableDocument()
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

//fun deleteObjectFromCollectiobById(db: Database, id: String, collection: String) {
//    val collection = db.getCollection(collection)
//    val sourceDoc: Document? = collection!!.getDocument(id)
//    collection.delete(sourceDoc!!)
//    getAllCollectionsFromDB(db)
//
//}
