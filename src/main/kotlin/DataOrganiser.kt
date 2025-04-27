import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*

@Composable
fun OrganiserGUI(dbWork: DBwork) {
    var objList = remember { mutableStateListOf<Thing>() }
    val curPlace = remember { mutableStateOf(StorageName.CUSTOM_PLACE) }
    val selectedItem = remember { mutableStateOf("") }
//    val curPlace = remember { mutableStateOf<StorageName> }
    val tempObjList = dbWork.getAllObjectsForCollection("Items")
    objList.addAll(tempObjList)
    MaterialTheme {
        Row(
            modifier = Modifier.fillMaxSize(), //заполняем всё доступное пространство
//            horizontalAlignment = Alignment.CenterHorizontally, //по центру горизонтально
        ) {
            Column(
                modifier = Modifier
                    .weight(2f)
            ) {
                //todo сделать возможность поворота изображения лаборатории в зависимости от положения устройства с программой
                LabRenderView(curPlace) { place, selItem -> //todo добавить selItem в параметры здесь и в TabPane и далее по списку, чтобы выделять в таблице при поиске по вводу слева
                    curPlace.value = place
                    selectedItem.value = selItem
                    val itemsInPlace = if (place != StorageName.CUSTOM_PLACE) dbWork.getAllObjectsForPlace(
                        "Items",
                        place
                    ) else dbWork.getAllObjectsForCollection("Items")
                    objList.clear()
                    objList.addAll(itemsInPlace)
                    println("itemsInPlace = $objList")
                    println("selectedItem = $selectedItem")
                }
            }
            Column(
                modifier = Modifier
                    .weight(3f)
            ) {
                TabPane(objList, dbWork, curPlace){//todo возвращать не StorageName, а Item, а у него брать StorageName
                    curPlace.value = it
                    println("curPlace = ${curPlace.value}")
                }
            }
        }
    }
}

@Composable
fun PlacesGUI() {
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
fun TabPane(objList: SnapshotStateList<Thing>, dbWork: DBwork, curPlace: MutableState<StorageName>, onPlaceSelect: (place: StorageName) -> Unit) {
    //todo сделать нормальное оформление табов
    MaterialTheme {
        var tabIndex by remember { mutableStateOf(0) }
        val tabs = listOf("Items", "Places")

        Column(modifier = Modifier.fillMaxWidth()) {
            TabRow(
                selectedTabIndex = tabIndex,
                contentColor = Color.Blue,
                backgroundColor = Color.White
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = tabIndex == index,
                        onClick = { tabIndex = index }
                    )
                }
            }

            when (tabIndex) {
                0 -> ItemsGUI(objList as SnapshotStateList<Item>, dbWork, curPlace){
                    curPlace.value = it
                    onPlaceSelect(it)
                }
                1 -> PlacesGUI()
//            2 -> SettingsScreen()
            }
        }
    }
}


fun main() = application {
    val dbWork = DBwork()
//    dbWork.getAllCollectionsFromDB()
    println("-----------------------------------------------------------")
//    dbWork.deleteObjectFromCollectiobById("1", "Items")
//    dbWork.deleteObjectFromCollectiobById("2", "Items")
//    var objList: MutableList<Item>
//    var objList = remember { mutableStateListOf<Item>()}
//    dbWork.getAllObjectsForCollection("Items").also { objList = it as SnapshotStateList<Item> }
//    objList = dbWork.getAllObjectsForCollection("Items") as SnapshotStateList<Item>
    val id = dbWork.getLastId(collectionName = "Items")
    println("last id = $id")

    val windowState = rememberWindowState(
        position = WindowPosition(Alignment.Center),
        size = DpSize(1400.dp, 600.dp)

    )
    Window(
        state = windowState,
        onCloseRequest = ::exitApplication,

        ) {
//        ItemsGUI(objList)
        OrganiserGUI(dbWork)
//        TabPane(/*objList,*/ dbWork)
    }
}
