import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.awt.FileDialog
import java.io.File
import java.nio.file.Paths
import kotlin.enums.EnumEntries

@Composable
fun ItemsGUI(
    objList: SnapshotStateList<Item>,
    dbWork: DBwork,
    curPlace: MutableState<StorageName>,
    onPlaceSelect: (place: StorageName) -> Unit
) {
//    MaterialTheme {
    println("ItemsGUI")
    var updateDb = remember { mutableStateOf(true) }
//    var objList = dbWork.getAllObjectsForCollection("Items") as List<Item>
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
                    val currentDir = File(System.getProperty("user.dir"))
                    val currentDir2 = Paths.get("").toAbsolutePath().toString()
                    val fileDialog = FileDialog(null as ComposeWindow?, "Select File", FileDialog.LOAD)
                    println("curDir = ${currentDir.absolutePath} \ncurDir2 = $currentDir2")
//            fileDialog.directory = currentDir.absolutePath
                    fileDialog.directory = currentDir2
                    fileDialog.isVisible = true
                    println("fileDialog.directory = ${fileDialog.directory}")
                    if (fileDialog.file != null) {
                        val file = File(fileDialog.directory, fileDialog.file)
//                val bytes = file.readBytes()
//                onFileSelected(bytes, file.name)
                        println("file = ${file.name}")
                        val excelWork = ExcelWork(file.path)
                        excelWork.readCellsFromExcel(1, 2)
//                        excelWork.readXlsxRow(1, 2)

//                        excelWork.extractImagesFromExcel()
                    }
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
        TableForItems(objList, updateDb, dbWork, isChecked, id, itemForEdit, curPlace) {
            curPlace.value = it
            onPlaceSelect(it)
        }
    }
//    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaceSelectComboBox(
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
            else PlaceSelectComboBox(StorageName.entries, mapForItemFields["place"]!! /*, btnActive1*/) {
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
        ) {
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TableForItems(
//                  objList: MutableList<Item>,
    objList: SnapshotStateList<Item>,
    updateDb: MutableState<Boolean>,
    dbWork: DBwork,
    isChecked: MutableState<Boolean>,
    id: MutableState<String>,
    itemForEdit: MutableState<Item>?,
    curPlace: MutableState<StorageName>, onPlaceSelect: (place: StorageName) -> Unit
) {
    val itemColumns = 4
//    var objList = remember { mutableStateListOf<Item>() }
//    dbWork.getAllObjectsForCollection("Items").also { objList = it as SnapshotStateList<Item> }
    if (updateDb.value) {
        objList.clear()
//        val tempList = dbWork.getAllObjectsForCollection("Items") as SnapshotStateList<Item>
//        val tempList
        val tempList = if (curPlace.value != StorageName.CUSTOM_PLACE) dbWork.getAllObjectsForPlace(
            "Items",
            curPlace.value
        ) else dbWork.getAllObjectsForCollection("Items")
        objList.addAll(tempList as Collection<Item>)
        updateDb.value = false
        println("db updated")
    }
    val showDialog = remember { mutableStateOf(false) }
    val delOk = remember { mutableStateOf(false) }
    var delId = remember { mutableStateOf("") }
    val mapForFieldNames = remember { mutableStateMapOf<String, Boolean>() }

    if (showDialog.value)
        AlertDialog(
            onDismissRequest = {
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
                        dbWork.deleteObjectFromCollectiobById(delId.value, "Items")
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
    val fieldNames = objList[0].getListOfFieldNames()
    fieldNames.forEach {
        if (!mapForFieldNames.keys.contains(it)) mapForFieldNames[it] = true
    }
    var mDisplayMenu = remember { mutableStateOf(false) }
    MakeTableCaption(mDisplayMenu, mapForFieldNames, fieldNames)

    var clickedItemId = remember { mutableStateOf("") }
    val mapColnamesToNumber = mapOf(0 to "id", 1 to "name", 2 to "place", 3 to "info")
    //todo разобраться с фильтром колонок по полям объекта или переделать как с заголовками

    MakeTableContent(
        mapForFieldNames,
        objList,
        mapColnamesToNumber,
        isChecked,
        id,
        clickedItemId,
        itemForEdit,
        delId,
        showDialog,
        curPlace,
        onPlaceSelect
    )
}

@Composable
private fun MakeTableCaption(
    mDisplayMenu: MutableState<Boolean>,
    mapForFieldNames: SnapshotStateMap<String, Boolean>,
    fieldNames: List<String>
) {
//    var mDisplayMenu1 = mDisplayMenu
    Row( //строка с названиями полей
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .border(2.dp, Color.Black)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val button = event.buttons
                        if (button.isSecondaryPressed || button.isPrimaryPressed) {
                            println("context menu for captions is called")
                            mDisplayMenu.value = true
                            println("mapForFieldNames = $mapForFieldNames")
                        }
                    }
                }
            },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        fieldNames.forEach {
            if (mapForFieldNames[it]!!)
                Text(
                    text = it,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .border(2.dp, Color.Black)
                        .wrapContentWidth()
                        .weight(5f)
                )
            DropdownMenu(
                expanded = mDisplayMenu.value,
                onDismissRequest = { mDisplayMenu.value = false }
            ) {
                mapForFieldNames.forEach { (name, checkValue) ->
                    DropdownMenuItem(
                        content = {
                            Checkbox(
                                checked = checkValue,
                                onCheckedChange = { checked ->
                                    mapForFieldNames[name] = checked
                                }
                            )
                            Text(
                                text = name, fontSize = 20.sp,
                                modifier = Modifier
                                    .clickable {
                                    }
                            )
                        },
                        onClick = {
                            mDisplayMenu.value = false
                        }
                    )
                }

            }
        }
    }
}

@Composable
private fun MakeTableContent2( //содержимое таблицы на основе обычного Row
    mapForFieldNames: SnapshotStateMap<String, Boolean>,
    objList: SnapshotStateList<Item>,
    mapColnamesToNumber: Map<Int, String>,
    isChecked: MutableState<Boolean>,
    id: MutableState<String>,
    clickedItemId: MutableState<String>,
    itemForEdit: MutableState<Item>?,
    delId: MutableState<String>,
    showDialog: MutableState<Boolean>,
    curPlace: MutableState<StorageName>,
    onPlaceSelect: (StorageName) -> Unit
) {
    var clickedItemId1 = clickedItemId
    if (objList.size > 0) {
        objList.forEach { obj ->
            val row = obj.getListOfValues().filterIndexed { index, s ->
                val colName = mapColnamesToNumber[index]
                mapForFieldNames[colName]!!
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .border(2.dp, Color.Black)
            ) {

            }
        }
    }

}

@Composable
private fun MakeTableContent( //содержимое таблицы на основе LazyVerticalGrid
    mapForFieldNames: SnapshotStateMap<String, Boolean>,
    objList: SnapshotStateList<Item>,
    mapColnamesToNumber: Map<Int, String>,
    isChecked: MutableState<Boolean>,
    id: MutableState<String>,
    clickedItemId: MutableState<String>,
    itemForEdit: MutableState<Item>?,
    delId: MutableState<String>,
    showDialog: MutableState<Boolean>,
    curPlace: MutableState<StorageName>,
    onPlaceSelect: (StorageName) -> Unit
) {
    var clickedItemId1 = clickedItemId
    LazyVerticalGrid(
//        columns = GridCells.Fixed(itemColumns),
        columns = GridCells.Fixed(mapForFieldNames.filterValues { it }.size), //кол-во колонок в зависимости от выбранных в контекстном меню
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .border(2.dp, Color.Black)
            .padding(4.dp)
    ) {
        if (objList.size > 0) {
            println("TableForItems updated")
            objList.forEach { obj ->
                val row = obj.getListOfValues().filterIndexed { index, s ->
                    val colName = mapColnamesToNumber[index]
                    mapForFieldNames[colName]!!
                }
                items(row.size) { index ->
//                items(mapForFieldNames.filterValues{ it }.size) { index ->
                    val colName = mapColnamesToNumber[index]
                    if (mapForFieldNames[colName]!!) {
                        ContextMenuArea(
                            items = {
                                listOf(
                                    ContextMenuItem("Edit") {
                                        println("trying to edit item with id = ${row[0]} ")
                                        isChecked.value = true
                                        id.value = row[0]
                                        clickedItemId1.value = row[0]
                                        itemForEdit!!.value = obj
                                        //                                borderColor = Color.Green
                                    },
                                    ContextMenuItem("Delete") {
                                        println("trying to delete item with id = ${row[0]} ")
                                        clickedItemId1.value = row[0]
                                        delId.value = row[0]
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
                            var borderColor = colorTransition.animateColor(
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
                                    .border(
                                        if (clickedItemId1.value == row[0]) 4.dp else 2.dp,
                                        borderColor.value
                                    )
                                    //                                .onClick {
                                    //                                }
                                    .pointerInput(Unit) {
                                        awaitPointerEventScope {
                                            while (true) {
                                                val event = awaitPointerEvent()
                                                val button = event.buttons
                                                if (button.isSecondaryPressed || button.isPrimaryPressed) {
                                                    println("item clicked with id = ${row[0]} place = ${row[2]}")
                                                    clickedItemId1.value = row[0]
                                                    curPlace.value = StorageName.valueOf(row[2])
                                                    onPlaceSelect(curPlace.value)
                                                    clickedItemId1.value = row[0]
                                                }
                                            }
                                        }
                                    }
                                //                            .border(2.dp, color = animatedColor,)
                            )
                        }
                    }
                }
            }
        }
    }
}
