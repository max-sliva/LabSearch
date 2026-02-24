import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.awt.FileDialog
import java.io.File
import java.nio.file.Paths
import kotlin.enums.EnumEntries
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.delay
import kotlin.system.measureTimeMillis

@Composable
fun ProgressAlertDialogExample(showDialog: MutableState<Boolean>, progress: Float, itemsTotal: Int, addedItemsCount: Int) {
    //var progress by remember { mutableStateOf(0f) }
//    val scope = rememberCoroutineScope()
  //  var showDialog = remember { mutableStateOf(false) }
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { /* Prevent dismiss or handle as needed */ },
            title = { Text("Loading...") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Progress: ${(progress * 100).toInt()}%")
                    Text(text = "Добавлено $addedItemsCount из ${itemsTotal}")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showDialog.value = false },
              //      enabled = progress >= 1f
                ) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
fun ItemsGUI(
    objList: SnapshotStateList<Item>,
    dbWork: DBwork,
    curPlace: MutableState<StorageName>,
    mode: String = "server",
    onPlaceSelect: (place: StorageName, imgPath: String) -> Unit
) {
//    MaterialTheme {
    println("ItemsGUI")
    var updateDb = remember { mutableStateOf(true) }
    var addedItemsCount by remember { mutableStateOf(0) }
    var itemsTotal by remember { mutableStateOf(0) }
    var showDialog = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var progress by remember { mutableStateOf(0f) }
    var excelWork by remember { mutableStateOf(ExcelWork(""))}
//    var itemsList: ArrayList<Item?> = ArrayList<Item?>()
//    var objList = dbWork.getAllObjectsForCollection("Items") as List<Item>
    val currentDir = File(System.getProperty("user.dir"))
    val currentDir2 = Paths.get("").toAbsolutePath().toString()
    val fileDialog = FileDialog(null as ComposeWindow?, "Select File", FileDialog.LOAD)
    println("curDir = ${currentDir.absolutePath} \ncurDir2 = $currentDir2")
//            fileDialog.directory = currentDir.absolutePath
    fileDialog.directory = currentDir2

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .border(2.dp, Color.Blue)
        ) {
            var itemsList = remember { mutableStateListOf<Item?>() }
//            var itemsList2 = remember { mutableStateListOf<Int>() }
            if (mode == "server") {
                Button(
                    onClick = {
                        addedItemsCount = 0
                        println("load")
//                    val currentDir = File(System.getProperty("user.dir"))
//                    val currentDir2 = Paths.get("").toAbsolutePath().toString()
//                    val fileDialog = FileDialog(null as ComposeWindow?, "Select File", FileDialog.LOAD)
//                    println("curDir = ${currentDir.absolutePath} \ncurDir2 = $currentDir2")
////            fileDialog.directory = currentDir.absolutePath
//                    fileDialog.directory = currentDir2
                        fileDialog.isVisible = true
                        println("fileDialog.directory = ${fileDialog.directory}")
                        if (fileDialog.file != null) {
                            val file = File(fileDialog.directory, fileDialog.file)
                            println("file = ${file.name}")
                            excelWork.setPath(file.path)
                            itemsTotal = excelWork.getRowsCount(1, 2) - 1
                            println("itemsTotal.value in button click = ${itemsTotal}")
                            showDialog.value = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xff1e63b2), // Custom background color
                        contentColor = Color.White    // Custom text color
                    ),
                ) {
                    Text(text = "Загрузить БД (Apache POI)")
                }
            }
//            Button(
//                onClick = {
//                    //попробовать fastexcel-reader https://github.com/dhatim/fastexcel
//                    println("FastExcel work")
//                    fileDialog.isVisible = true
//                    println("fileDialog.directory = ${fileDialog.directory}")
//                    if (fileDialog.file != null) {
//                        val file = File(fileDialog.directory, fileDialog.file)
//                        println("file = ${file.name}")
//                        val fastExcelWork = FastExcelWork(file.path)
//                        val rows = fastExcelWork.getRowsCount(1, 2)
//                        println("rows number with FastExcel = $rows")
//                        fastExcelWork.readCellsFromExcel(1,2){
//
//                        }
//                    }
//                },
//                modifier = Modifier
//
//            ){
//                Text(text = "FastExcel")
//            }
            if (showDialog.value) {
                LaunchedEffect(Unit) {
                    println("---!!!  LaunchedEffect1 started !!!!----")
                    val duration = measureTimeMillis {
                        itemsList.addAll(excelWork.readCellsFromExcel(1, 2) {
                                                        addedItemsCount = it
                                                        println("addedItemsCount = $addedItemsCount")
                                                    }
                        )
                    }
                    println("--!! excelWork time = $duration ms !!--")
                    dbWork.addAllItemsFromListToCollection(itemsList, "Items")
                    println("LaunchedEffect1 passed")
                    updateDb.value = true
                }
                LaunchedEffect(Unit) {
                    println("---!!!  LaunchedEffect2 started !!!!----")
                    println("itemsTotal.value in LaunchedEffect2 = ${itemsTotal}")
                    while (addedItemsCount < itemsTotal) {
//                                while (showDialog.value) {
                        delay(1)
                        println("itemsTotal = ${itemsTotal}, progress = ${progress} addedItemsCount in progress = ${addedItemsCount}")
                        progress = addedItemsCount.toFloat() / (itemsTotal)
                    }
                }
            }
            ProgressAlertDialogExample(showDialog, progress, itemsTotal, addedItemsCount)
        }
        var isChecked = remember { mutableStateOf(false) }
        var btnActive = remember { mutableStateOf(false) }
        if (mode == "server") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "New item")
                Checkbox(
                    checked = isChecked.value,
                    onCheckedChange = { isChecked.value = it }
                )
            }
        }
        var itemForEdit = remember { mutableStateOf(Item(name = "", place = Place(name = StorageName.CUSTOM_PLACE))) }
        var id = remember { mutableStateOf("") }
        if (isChecked.value) {
            var fieldNames = if (objList.isNotEmpty()) objList[0].getListOfFieldNames() else Item("", "", Place()).getListOfFieldNames()
            MakeInputRow(id = id, itemForEdit, fieldNames, btnActive, dbWork, updateDb) { item ->
                dbWork.addObjectToCollection(item.id, item, "Items")
                isChecked.value = false
            }
        } else {
            btnActive.value = false
            id.value = ""
        }
        TableForItems(objList, updateDb, dbWork, isChecked, id, itemForEdit, curPlace, mode) { place, imgPath ->
            curPlace.value = place
            onPlaceSelect(place, imgPath)
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
    fieldNames_: List<String>?,
    btnActive: MutableState<Boolean>,
    dbWork: DBwork,
    updateDb: MutableState<Boolean>,
    onUpdate: (Item) -> Unit
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
        var fieldNames = fieldNames_?.filter { it != "id" }
        var mapForItemFields =
            if (id.value == "") remember { mutableStateMapOf("name" to "", "place" to "", "info" to "") }
            else remember {
                mutableStateMapOf(
                    "name" to item!!.value.name,
                    "place" to item.value.place.name.toString(),
                    "info" to item.value.info
                )
            }
        fieldNames?.forEach { field ->
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
                    if (lastId == "") lastId = dbWork.getLastIdPlusOne("Items")!!
                    else {
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
    curPlace: MutableState<StorageName>,
    mode: String,
    onPlaceSelect: (place: StorageName, imgPath: String) -> Unit
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
    val fieldNames = if (objList.isNotEmpty()) objList[0].getListOfFieldNames() else null
    println("fieldNames = $fieldNames")
    fieldNames?.forEach {
        if (!mapForFieldNames.keys.contains(it)) mapForFieldNames[it] = true
    }
    var mDisplayMenu = remember { mutableStateOf(false) }
    var sortColumn = remember{mutableStateOf("id")}
    val itemsAll by mutableStateOf(objList.size)
    val listState: LazyGridState = rememberLazyGridState(0,0)
//    val visibleItemsCount = listState.layoutInfo.visibleItemsInfo.size
    var visItems = remember { mutableStateOf(listState.layoutInfo.visibleItemsInfo.size) }
//    var visItems by mutableStateOf(listState.layoutInfo.visibleItemsInfo.size)
    val fields = fieldNames?.count {mapForFieldNames[it]!!}?:1
    val tempCount = listState.layoutInfo.visibleItemsInfo.size / fields
    println("---!! table row visible = ${tempCount} !!---")
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                val cols = fieldNames?.count {(mapForFieldNames[it]!!) }
                val visibleItemsCount = listState.layoutInfo.visibleItemsInfo.size
                visItems.value = index / cols!! + visibleItemsCount / cols!!
//                    println("Scrolled to item index: ${index / cols!! + visibleItemsCount / cols!!}")
                println("Scrolled to item index: $index")
                println("visItems: ${visItems.value} cols = $cols")
                println("visibleItemsCount: $visibleItemsCount")
            }
    }

    Row( modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)
        .border(2.dp, Color.Black)
    ){
        val rows = if (visItems.value==0) tempCount else itemsAll
        val rowsAtTable = if (visItems.value<itemsAll) visItems.value else rows
             Text(text = "Просмотрено: $rowsAtTable ||  Всего: $itemsAll")
    }
    MakeTableCaption(mDisplayMenu, mapForFieldNames, fieldNames, sortColumn)

    var clickedItemId = remember { mutableStateOf("") }
    val mapColnamesToNumber = mapOf(0 to "id", 1 to "name", 2 to "place", 3 to "info")
//    println("visibleItemsCount = $visibleItemsCount")
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
        sortColumn,
        listState,
        mode,
        onPlaceSelect
    )

}

@Composable
private fun MakeTableCaption(
    mDisplayMenu: MutableState<Boolean>,
    mapForFieldNames: SnapshotStateMap<String, Boolean>,
    fieldNames: List<String>?,
    sortColumn: MutableState<String>
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
                        if (button.isSecondaryPressed) {
                            println("context menu for captions is called")
                            mDisplayMenu.value = true
                            println("mapForFieldNames = $mapForFieldNames")
                        }
                    }
                }
            },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        fieldNames?.forEach {
            if (mapForFieldNames[it]!!)
                Text(
                    text = it,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .border(2.dp, Color.Black)
                        .wrapContentWidth()
                        .weight(5f)
//                        .clickable{
//                            println("Column in clickable = $it")
//                        }
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    val button = event.buttons
                                    if (button.isPrimaryPressed) {
                                        sortColumn.value = it
                                        println("Column = ${sortColumn.value}")
                                    }
                                }
                            }
                        }
                )
            DropdownMenu(
                expanded = mDisplayMenu.value,
                onDismissRequest = { mDisplayMenu.value = false }
            ) {
    //                mapForFieldNames.forEach { (name, checkValue) ->
                fieldNames.forEach { name ->
                    DropdownMenuItem(
                        content = {
                            Checkbox(
                                //                                checked = checkValue,
                                checked = mapForFieldNames[name]==true,
                                onCheckedChange = { checked ->
                                    mapForFieldNames[name] = checked
                                }
                            )
                            Text(
                                text = name, fontSize = 20.sp,
                                modifier = Modifier
                                    .clickable {
                                        mapForFieldNames[name] = !mapForFieldNames[name]!!
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
    sortColumn: MutableState<String>,
    listState: LazyGridState,
    mode: String,
    onPlaceSelect: (StorageName, String) -> Unit
) {
    when (sortColumn.value){
        "id"->{
            println("sort by id")
            objList.sortBy { it.id.toInt() }
        }
        "name"->{
            println("sort by name")
            objList.sortBy { it.name }
        }
        "place"->{
            println("sort by place")
            objList.sortBy { it.place.name }
        }
        "info"->{
            println("sort by info")
            objList.sortBy { it.info }
        }
    }
    var clickedItemId1 = clickedItemId
//    val listState: LazyGridState = rememberLazyGridState()
////    var itemIsVisible_3 = listState.layoutInfo.visibleItemsInfo.any { it.index == 3 }
//    var visibleItemsCount = listState.layoutInfo.visibleItemsInfo.size
//    println("visibleItemsCount = $visibleItemsCount")
//    if (itemIsVisible_3) {
//        println("---!!! item 3 is visible !!!---")
//    } else {
//        println("---!!! item 3 is not visible !!!---")
//    }
//    LaunchedEffect(listState) {
//        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
//            .collect { (index, offset) ->
//                val cols = fieldNames?.count {(mapForFieldNames[it]!!) }
//                println("Scrolled to item index: $index with offset: $offset")
//                // You can trigger other actions here on scroll
//            }
//    }
    if (mapForFieldNames.isNotEmpty()) {
        LazyVerticalGrid(
//        columns = GridCells.Fixed(itemColumns),
            columns = GridCells.Fixed(mapForFieldNames.filterValues { it }.size), //кол-во колонок в зависимости от выбранных в контекстном меню
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            state = listState,
            modifier = Modifier
                .border(2.dp, Color.Black)
                .padding(4.dp)
//                .wrapContentHeight()
        ) {
            if (objList.isNotEmpty()) {
                println("TableForItems updated")
//                var itemIsVisible_3 = listState.layoutInfo.visibleItemsInfo.any { it.index == 3 }
                objList.forEach { obj ->
//                    val rowWithID = obj.getListOfValuesWithoutImg() //все поля
                    val rowWithID = obj.getListOfValues() //все поля
                    val row =
                        obj.getListOfValuesWithoutImg().filterIndexed { index, s -> //только нужные согласно выбору чекбоксов
                            val colName = mapColnamesToNumber[index]
                            mapForFieldNames[colName]!!
                        }
                    items(row.size) { index ->
//                items(mapForFieldNames.filterValues{ it }.size) { index ->
//                    val colName = mapColnamesToNumber[index]
//                    if (mapForFieldNames[colName]!!) {
                        TableRowItem(
                            rowWithID,
                            isChecked,
                            id,
                            clickedItemId1,
                            itemForEdit,
                            obj,
                            delId,
                            showDialog,
                            row,
                            index,
                            curPlace,
                            mode,
                            onPlaceSelect
                        )
//                    }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
private fun TableRowItem(
    rowWithID: List<String>,
    isChecked: MutableState<Boolean>,
    id: MutableState<String>,
    clickedItemId1: MutableState<String>,
    itemForEdit: MutableState<Item>?,
    obj: Item,
    delId: MutableState<String>,
    showDialog: MutableState<Boolean>,
    row: List<String>,
    index: Int,
    curPlace: MutableState<StorageName>,
    mode: String,
    onPlaceSelect: (StorageName, String) -> Unit
) {
    ContextMenuArea(
        items = {
            if (mode=="server") listOf(
                ContextMenuItem("Edit") {
                    println("trying to edit item with id = ${rowWithID[0]} ")
                    isChecked.value = true
                    id.value = rowWithID[0]
                    clickedItemId1.value = rowWithID[0]
                    itemForEdit!!.value = obj
                    //                                borderColor = Color.Green
                },
                ContextMenuItem("Delete") {
                    println("trying to delete item with id = ${rowWithID[0]} ")
                    clickedItemId1.value = rowWithID[0]
                    delId.value = rowWithID[0]
                    showDialog.value = true
                }
            )
            else listOf()
        }
    ) {
        //                    val borderColor = if (row[0]==id.value) Color.Green  else Color(0xff1e63b2)
        val animatedColor by animateColorAsState(
            targetValue = if (rowWithID[0] == id.value) Color.Green else Color(0xff1e63b2),
            animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
            //                        animationSpec = finiteRepeatable(durationMillis = 3000, easing = LinearEasing)
        )
        val colorTransition = updateTransition(rowWithID[0] == id.value, label = "pulseTransition")
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
        TooltipArea(
            tooltip = {
                        Surface(
                            modifier = Modifier
                                .width(400.dp) // Set explicit width
                                .padding(8.dp), // Internal padding
                            shape = RoundedCornerShape(8.dp)
                        ) {Text(row[index]) }
            },
            delayMillis = 500, // in milliseconds
            tooltipPlacement = TooltipPlacement.CursorPoint(
                offset = DpOffset(0.dp, 16.dp)
            ),
            modifier = Modifier
//                .border(2.dp, Color.Red),
//                .padding(start = 5.dp)
                //.clickable()
//                .pointerInput(Unit) {  //с этим методом глючил при выбранном объекте и переключении на другое место на картинке
//                    awaitPointerEventScope {
//                        while (true) {
//                            val event = awaitPointerEvent()
//                            val button = event.buttons
//                            if (button.isSecondaryPressed || button.isPrimaryPressed) {
//                                println("item clicked with id = ${rowWithID[0]} image = ${if (rowWithID.size>4) rowWithID[4] else "no img"}")
////                                clickedItemId1.value = rowWithID[0]
//                                curPlace.value = StorageName.valueOf(rowWithID[2])
//                                onPlaceSelect(curPlace.value, rowWithID[4])
//                                clickedItemId1.value = rowWithID[0]
//                            }
//                        }
//                    }
//                }
                .combinedClickable(  // detects multiple click types
                    onClick = {
//                                clickCount++
//                                lastClickInfo = "Primary click"
                        println("Primary click")
                        println("item clicked with id = ${rowWithID[0]} image = ${if (rowWithID.size>4) rowWithID[4] else "no img"}")
//                                clickedItemId1.value = rowWithID[0]
                        curPlace.value = StorageName.valueOf(rowWithID[2])
                        onPlaceSelect(curPlace.value, rowWithID[4])
                        clickedItemId1.value = rowWithID[0]

                    },
//                            onSecondaryClick = {
//                                println("Primary click")
//                            },
//                            onLongClick = {
//                                lastClickInfo = "Long press"
//                            }
                )
                .border(
                    if (clickedItemId1.value == rowWithID[0]) 4.dp else 2.dp,
                    borderColor.value
                ),
            content = {
                Text(
                    text = row[index],
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(start = 5.dp)
//                        .combinedClickable(  // detects multiple click types
//                            onClick = {
////                                clickCount++
////                                lastClickInfo = "Primary click"
//                                println("Primary click")
//                                println("item clicked with id = ${rowWithID[0]} image = ${if (rowWithID.size>4) rowWithID[4] else "no img"}")
////                                clickedItemId1.value = rowWithID[0]
//                                curPlace.value = StorageName.valueOf(rowWithID[2])
//                                onPlaceSelect(curPlace.value, rowWithID[4])
//                                clickedItemId1.value = rowWithID[0]
//
//                            },
////                            onSecondaryClick = {
////                                println("Primary click")
////                            },
////                            onLongClick = {
////                                lastClickInfo = "Long press"
////                            }
//                        ),
                )
            })
    }
}
