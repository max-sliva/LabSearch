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
import java.util.Locale

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
            var itemImage = remember { mutableStateOf("Images/default.png") }
            Column(
                modifier = Modifier
                    .weight(2f)
            ) {
                LabRenderView(curPlace, itemImage) { place, selItem ->
                    curPlace.value = place
                    selectedItem.value = selItem
                    var itemsInPlace = if (place != StorageName.CUSTOM_PLACE /*&& place != StorageName.IN_BACK_SHELF*/) dbWork.getAllObjectsForPlace(
                        "Items",
                        if (place==StorageName.IN_BACK_SHELF) StorageName.BACK_SHELF else place
                    ) else dbWork.getAllObjectsForCollection("Items")
                    objList.clear()
                    if (selItem.isNotEmpty()) {
//                        println("---!!! item is found selItem = $selItem !!----")
//                        itemsInPlace = itemsInPlace.filter { (it as Item).name.contains(selItem) }
                        itemsInPlace = itemsInPlace.filter { (it as Item).name.lowercase(Locale.getDefault()).contains(selItem.lowercase()) }
                    } else {
                        println("---!!! place is found !!----")
                    }
                    objList.addAll(itemsInPlace)
//                    objList.clear()
//                    println("itemsInPlace = $objList")
//                    println("selectedItem = ${selectedItem.value}")
                }
            }
            Column(
                modifier = Modifier
                    .weight(3f)
            ) {
                TabPane(objList, dbWork, curPlace){ place, imgPath ->
                    curPlace.value = place
                    itemImage.value = imgPath
                    println("curPlace = ${curPlace.value}, imgPath = $imgPath")
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
fun TabPane(objList: SnapshotStateList<Thing>, dbWork: DBwork, curPlace: MutableState<StorageName>, onPlaceSelect: (place: StorageName, imgPath: String) -> Unit) {
    //todo сделать нормальное оформление табов
    MaterialTheme {
        var tabIndex by remember { mutableStateOf(0) }
        val tabs = listOf("Items",
        //    "Places" ////раскомментировать для серверной части
        )

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
                0 -> ItemsGUI(objList as SnapshotStateList<Item>, dbWork, curPlace,"client"){ place, imgPath ->
                    curPlace.value = place
                    onPlaceSelect(place, imgPath)
                }
              //  1 -> PlacesGUI() //раскомментировать для серверной части
//            2 -> SettingsScreen()
            }
        }
    }
}


fun main() = application {
    val dbWork = DBwork()
    val maxId =  dbWork.findMaxNumericId("Items")
    println("maxId = $maxId")
//    dbWork.clearCollection("Items")
//    dbWork.getAllCollectionsFromDB()
    println("-----------------------------------------------------------")
//    dbWork.deleteObjectFromCollectiobById("1", "Items")
//    dbWork.deleteObjectFromCollectiobById("2", "Items")
//    var objList: MutableList<Item>
//    var objList = remember { mutableStateListOf<Item>()}
//    dbWork.getAllObjectsForCollection("Items").also { objList = it as SnapshotStateList<Item> }
//    objList = dbWork.getAllObjectsForCollection("Items") as SnapshotStateList<Item>
//    val id = dbWork.getLastId(collectionName = "Items")
//    println("last id = $id")

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
