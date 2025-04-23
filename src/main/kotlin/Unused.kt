//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.wrapContentHeight
//import androidx.compose.foundation.layout.wrapContentWidth
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.MutableState
//import androidx.compose.runtime.snapshots.SnapshotStateList
//import androidx.compose.runtime.snapshots.SnapshotStateMap
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//
//@Composable
//private fun MakeTableContent2( //содержимое таблицы на основе обычного Row
//    mapForFieldNames: SnapshotStateMap<String, Boolean>,
//    objList: SnapshotStateList<Item>,
//    mapColnamesToNumber: Map<Int, String>,
//    isChecked: MutableState<Boolean>,
//    id: MutableState<String>,
//    clickedItemId: MutableState<String>,
//    itemForEdit: MutableState<Item>?,
//    delId: MutableState<String>,
//    showDialog: MutableState<Boolean>,
//    curPlace: MutableState<StorageName>,
//    onPlaceSelect: (StorageName) -> Unit
//) {
//    var clickedItemId1 = clickedItemId
//    if (objList.isNotEmpty()) {
//        LazyColumn( //объект для представления списка
////добавляем отступы между эл-ми списка
//            verticalArrangement = Arrangement.spacedBy(12.dp),
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color.White)
//        ) {
//            items(
//                items = objList,
////                key =
//                itemContent = { item -> //содержимое эл-та списка
//                    TableRow(item, mapColnamesToNumber, mapForFieldNames)//вызываем метод для формирования каждого эл-та списка
//                }
//            )
//        }
//    }
//}
//
//@Composable
//fun TableRow(item: Item, mapColnamesToNumber: Map<Int, String>, mapForFieldNames: SnapshotStateMap<String, Boolean>){ //ф-ия для создания ряда с данными для LazyColumn
//    Row( //создаем ряд с данными
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = Modifier
//            .wrapContentHeight()
//            .fillMaxWidth()
//            .border(BorderStroke(2.dp, Color.Blue)) //синяя граница для каждого эл-та списка
//    ) {
////        val row = item.getListOfValues()
//        val row = item.getListOfValues().filterIndexed { index, s ->
////            val colName = mapColnamesToNumber[index]
////            mapForFieldNames[colName]!!
//            mapForFieldNames[mapColnamesToNumber[index]]==true
//        }
//        println("row = $row")
//        row.forEachIndexed {index, cell ->
////        for (cell in row) {
//            val colName = mapColnamesToNumber[index]
////            if (mapForFieldNames[colName]!!) {
//            Text(
//                text = cell,
//                textAlign = TextAlign.Center,
//                modifier = Modifier
//                    .border(2.dp, Color(0xff1e63b2))
//                    .wrapContentWidth()
//                    .weight(5f)
//            )
////            }
//        }
//    }
//}