//import androidx.compose.material3.*
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
//import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.layout.RowScopeInstance.weight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.TooltipBox
//import androidx.compose.material3.TooltipDefaults
//import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
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
fun ComboBoxExample(
    entries: EnumEntries<StorageName>,
    currentPlace: String/*, btnActive: Boolean*/,
    onUpdate: (x: String) -> Unit
) {
    println("currentPlace = $currentPlace")
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(if (currentPlace == "") "Select an option" else currentPlace) }
    println("selectedOption = $selectedOption")
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
fun ItemsGUI(/*objList: MutableList<Item>,*/ dbWork: DBwork) {
//    MaterialTheme {
    println("ItemsGUI")
    var updateDb = remember { mutableStateOf(true) }
    var objList = dbWork.getAllObjectsForCollection("Items") as List<Item>

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
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xff1e63b2), // Custom background color
                    contentColor = Color.White    // Custom text color
                ),
            ) {
                Text(text = "Загрузить БД")
            }
        }
        var isChecked = remember { mutableStateOf(false) }
        var btnActive = remember { mutableStateOf(false) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "New item")
            Checkbox(
                checked = isChecked.value,
                onCheckedChange = { isChecked.value = it }
            )
        }
        var itemForEdit = remember { mutableStateOf(Item(name = "", place = Place(name = StorageName.CUSTOM_PLACE))) }
        var id = remember { mutableStateOf("") }
        if (isChecked.value) {
            var fieldNames = objList[0].getListOfFieldNames()
            MakeInputRow(id = id, itemForEdit, fieldNames, btnActive, dbWork, updateDb) { item ->
                dbWork.addObjectToCollection(item.id, item, "Items")
                isChecked.value = false
            }
        } else {
            btnActive.value = false
            id.value = ""
        }
        TableForItems(/*objList,*/ updateDb, dbWork, isChecked, id, itemForEdit)
    }
//    }
}

//@OptIn(ExperimentalMaterial3Api::class)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MakeInputRow( //создаем ряд с полями для ввода данных
    id: MutableState<String>,
    item: MutableState<Item>?,
    fieldNames_: List<String>,
    btnActive: MutableState<Boolean>,
    dbWork: DBwork,
    updateDb: MutableState<Boolean>,
    onUpdate: (x: Item) -> Unit
) {
//    var btnActive1 = btnActive
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .border(2.dp, Color.Gray)
    ) {
//        var fieldNames = fieldNames_
        var fieldNames = fieldNames_.filter { it != "id" }
        var mapForItemFields =
            if (id.value == "") remember { mutableStateMapOf("name" to "", "place" to "", "info" to "") }
            else remember {
                mutableStateMapOf(
                    "name" to item!!.value.name,
                    "place" to item.value.place.name.toString(),
                    "info" to item.value.info
                )
            }
        fieldNames.forEach { field ->
            if (field != "place")
                TextField(
                    value = mapForItemFields[field]!!,
                    onValueChange = { newText -> //обработчик ввода значений в поле
                        mapForItemFields[field] = newText //все изменения сохраняем в наш объект
                        println("place = ${mapForItemFields["place"]} name = place = ${mapForItemFields["name"]}")
                        if (mapForItemFields["place"] != "" && mapForItemFields["name"] != "") {
                            println("activate btn")
                            btnActive.value = true
                        } else {
                            println("disactivate btn")
                            btnActive.value = false
                        }
                    },
                    label = { Text(field) },
                    //                        textAlign = TextAlign.Center,
                    //                        fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .border(2.dp, Color.Black)
                        .weight(2f)
                )
            else ComboBoxExample(StorageName.entries, mapForItemFields["place"]!! /*, btnActive1*/) {
                println("----!! ComboBox place input = $it !!!!---------")
                mapForItemFields["place"] = it
                if (mapForItemFields["name"] != "") btnActive.value = true
            }
        }
//        TooltipBox(
//            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
//            tooltip = { Text("Input name and place!") },
//            state = rememberTooltipState()
//        ) {
        var showTooltip by remember { mutableStateOf(false) }
        TooltipArea(
            tooltip = {
                // Composable tooltip content:
                Surface(
                    modifier = Modifier.shadow(4.dp),
                    color = Color(255, 255, 210),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "Input name and place!",
                        modifier = Modifier.padding(10.dp)
                    )
                }
            },
            modifier = Modifier.padding(start = 40.dp),
            delayMillis = 600, // In milliseconds
            tooltipPlacement = TooltipPlacement.CursorPoint(
                alignment = Alignment.BottomEnd,
                offset = DpOffset.Zero // Tooltip offset
            )
        ){
            Button(
                enabled = btnActive.value,
                onClick = {
                    println("mapForItemFields = $mapForItemFields")
                    var lastId = id.value
                    if (lastId == "") lastId = dbWork.getLastIdPlusOne("Items")!! else {
                        id.value = ""
                        dbWork.deleteObjectFromCollectiobById(lastId, "Items")
                    }
                    try {
                        val place = Place(name = StorageName.valueOf(mapForItemFields["place"]!!))
                        val newItem = Item(
                            id = lastId,
                            name = mapForItemFields["name"]!!,
                            place = place,
                            info = mapForItemFields["info"]!!
                        )
                        println("new item = $newItem")
                        onUpdate(newItem)
                        updateDb.value = true
                    } catch (e: IllegalArgumentException) {
                        println("error in new item")
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                if (id.value == "") Text(text = "Add") else Text("Replace")
            }
        }

    }
}

@Composable
fun TableForItems(/*objList: List<Item>,*/ updateDb: MutableState<Boolean>,
                  dbWork: DBwork,
                  isChecked: MutableState<Boolean>,
                  id: MutableState<String>,
                  itemForEdit: MutableState<Item>?
) {
    val itemColumns = 4
    var objList = remember { mutableStateListOf<Item>() }
    dbWork.getAllObjectsForCollection("Items").also { objList = it as SnapshotStateList<Item> }
    if (updateDb.value) {
        objList = dbWork.getAllObjectsForCollection("Items") as SnapshotStateList<Item>
        updateDb.value = false
        println("db updated")
    }
    val showDialog = remember { mutableStateOf(false) }
    val delOk = remember { mutableStateOf(false) }
    var delId by remember { mutableStateOf("") }
    if (showDialog.value)
        AlertDialog(
            onDismissRequest = {
                // Dismiss when user clicks outside or presses back
                showDialog.value = false
            },
            title = { Text("Confirmation") },
            text = { Text("Are you sure you want to delete?") },
            confirmButton = {
                Button(
                    onClick = {
                        // Handle OK action
                        delOk.value = true
                        showDialog.value = false
                        dbWork.deleteObjectFromCollectiobById(delId, "Items")
                        updateDb.value = true
                        println("deleted item with id = $delId")
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        // Handle Cancel action
                        showDialog.value = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
//    var borderColor by remember{ mutableStateOf(Color(0xff1e63b2)) }
    LazyVerticalGrid(
        columns = GridCells.Fixed(itemColumns),
//        columns = GridCells.Adaptive(20.dp),
//        columns = GridCells.FixedSize(20.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .border(2.dp, Color.Black)
    ) {
        var fieldNames = objList[0].getListOfFieldNames()
//        fieldNames = fieldNames.plus("")
//        val fieldNames = objList2[0].getListOfFieldNames()
        items(fieldNames.size) { index ->
            Text(
                text = fieldNames[index],
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .border(2.dp, Color.Black)
//                    .weight((index+1).toFloat())
//                    .weight(5f)
            )
        }
        println("TableForItems updated")
        objList.forEach { obj ->
//        objList2.forEach { obj ->
//            println("in table obj = ${obj.getListOfValues()}")
            val row = obj.getListOfValues()
            items(row.size) { index ->
                ContextMenuArea(
                    items = {
                        listOf(
                            ContextMenuItem("Edit") {
                                println("trying to edit item with id = ${row[0]} ")
                                isChecked.value = true
                                id.value = row[0]
                                itemForEdit!!.value = obj
//                                borderColor = Color.Green
                            },
                            ContextMenuItem("Delete") {
                                println("trying to delete item with id = ${row[0]} ")
                                delId = row[0]
                                showDialog.value = true
                            }
                        )
                    }
                ) {
//                    val borderColor = if (row[0]==id.value) Color.Green  else Color(0xff1e63b2)
                    val animatedColor by animateColorAsState(
                        targetValue = if (row[0] == id.value) Color.Green else Color(0xff1e63b2),
                        animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
//                        animationSpec = finiteRepeatable(durationMillis = 3000, easing = LinearEasing)
                    )
                    val colorTransition = updateTransition(row[0] == id.value, label = "pulseTransition")

                    val borderColor by colorTransition.animateColor(
                        transitionSpec = {
                            repeatable(
                                iterations = 4,  // 3 full cycles
                                animation = tween(1000),
                                repeatMode = RepeatMode.Reverse
                            )
                        },
                        label = "colorAnimation"
                    ) { pulsing ->
                        if (pulsing) Color.Green else Color(0xff1e63b2)
                    }

                    Text(
                        text = row[index],
                        textAlign = TextAlign.Center,
                        modifier = Modifier
//                        .padding(start=20.dp)
                            .border(2.dp, borderColor)
//                            .border(2.dp, color = animatedColor,)
                    )
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
fun TabPane(/*objList: MutableList<Item>,*/ dbWork: DBwork) {
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
                0 -> ItemsGUI(/*objList,*/ dbWork)
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
        position = WindowPosition(Alignment.Center)
    )
    Window(
        state = windowState,
        onCloseRequest = ::exitApplication
    ) {
//        ItemsGUI(objList)
        TabPane(/*objList,*/ dbWork)
    }
}
