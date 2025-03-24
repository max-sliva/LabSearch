import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.onClick
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlin.enums.EnumEntries

//import kotlin.reflect.full.memberProperties
//import kotlin.reflect.jvm.isAccessible

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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ComboBoxExample(entries: EnumEntries<StorageName>, onUpdate: (x: String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Select an option") }
    val options = entries.toList()
 //todo поправить комбобокс
    Box(modifier = Modifier.wrapContentWidth()) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
//            modifier = Modifier
//                .clickable(onClick ={
//                    expanded = !expanded
//                },
            readOnly = true,
            modifier = Modifier.wrapContentWidth()
                .clickable {
                    println("combo clicked")
                    expanded = !expanded
                }
                .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    println("combo isFocused")
                    expanded = !expanded
                }
            },
            label = { Text("place") },
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.wrapContentWidth()
        ) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    selectedOption = option.toString()
                    expanded = false
                    onUpdate(selectedOption)
                }) {
                    Text(text = option.toString())
                }
            }
        }
    }
}

@Composable
fun ItemsGUI(objList: MutableList<Item>, dbWork: DBwork) {
//    MaterialTheme {
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
        var isChecked by remember { mutableStateOf(false) }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "New item")
            Checkbox(
                checked = isChecked,
                onCheckedChange = { isChecked = it }
            )
        }
        if (isChecked)
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .border(2.dp, Color.Gray)
            ) {
                var fieldNames = objList[0].getListOfFieldNames()
                fieldNames = fieldNames.filter { it!="id" }
                val mapForItemFields = remember { mutableStateMapOf("name" to "", "place" to "", "info" to "" )}
//                val mapForItemFields = remember { mutableStateMapOf<String, String>()}
//                for (field in fieldNames){
//                    mapForItemFields[field] = ""
//                }
                fieldNames.forEach { field ->
                    if (field!="place")
                        TextField(
                            value = mapForItemFields[field]!!,
                            onValueChange = { newText -> //обработчик ввода значений в поле
                                mapForItemFields[field] = newText //все изменения сохраняем в наш объект
                                //todo сделать активацию кнопки, если заполнены поля name и place03
                            },
                            label = { Text(field) },
    //                        textAlign = TextAlign.Center,
    //                        fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .border(2.dp, Color.Black)
                                .weight(2f)
                        )
                    else ComboBoxExample(StorageName.entries) {it->
                        mapForItemFields["place"] = it
                    }
                }
                Button(
                    onClick = {
                        println("mapForItemFields = $mapForItemFields")
                        val lastId = dbWork.getLastId("Items")
                        var place : Place = Place()
                        try {
                            place = Place(name = StorageName.valueOf(mapForItemFields["place"]!!))
                            val newItem = Item(id = (lastId!!.toInt()+1).toString(), name = mapForItemFields["name"]!!, place = place, info = mapForItemFields["info"]!!)
                            println("new item = $newItem")
                            dbWork.addObjectToCollection(newItem.id, newItem, "Items")
                            //todo выяснить, почему сразу не обновляется таблица с объектами
                        } catch (e: IllegalArgumentException){
                           println("error in new item")
                            //todo сделать всплывающее сообщение с просьбой выбрать место объекта
                        }
//                        finally {
//                            val newItem = Item(id = lastId!!+1, name = mapForItemFields["name"]!!, place = place, info = mapForItemFields["info"]!!)
//                            println("new item = $newItem")
//                        }
                      //  if (newItem.name!="" && place!=)
//                        dbWork.addObjectToCollection()
//                        mapForItemFields.forEach { (k, v) ->
//                            print()
//                        }
                    },
                    modifier = Modifier.weight(1f),
                ){
                    Text(text="Add")
                }
            }
//            TableWork()
        TableForItems(objList)
        //CustomTable()
    }
//    }
}

@Composable
fun TableForItems(objList: MutableList<Item>) {
    val itemColumns = 4

    LazyVerticalGrid(
        columns = GridCells.Fixed(itemColumns),
//        columns = GridCells.Adaptive(20.dp),
//        columns = GridCells.FixedSize(20.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .border(2.dp, Color.Black)
    ) {
        val fieldNames = objList[0].getListOfFieldNames()
        items(fieldNames.size) { index ->
            Text(
                text = fieldNames[index],
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .border(2.dp, Color.Black)
            )
        }
        objList.forEach { obj ->
            println("obj = ${obj.getListOfValues()}")
            val row = obj.getListOfValues()
            items(row.size) { index ->
                Text(
                    text = row[index],
                    textAlign = TextAlign.Center,
                    modifier = Modifier
//                        .padding(start=20.dp)
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
fun TabPane(objList: MutableList<Item>, dbWork: DBwork) {
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
                0 -> ItemsGUI(objList, dbWork)
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
//        ItemsGUI(objList)
        TabPane(objList, dbWork)
    }
}
