import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.skia.Image
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.collections.ArrayList

fun searchItem( //возвращает нужную картинку для искомого предмета
    things: ArrayList<Item>,
    searchValue: String,
    storageNameToPngHashMap: Map<StorageName, String>
): String {
    var imageSrc = "206.png" //дефолтная картинка
    things!!.forEach {
        if (it is Item && it.name.lowercase(Locale.getDefault()) == searchValue.lowercase(Locale.getDefault())) {
            imageSrc = storageNameToPngHashMap[it.place.name].toString()
            //                            println("${storageNameToPngHashMap[it.place.name]}")
        }
    }
    return imageSrc
}

@Composable
@Preview
fun LabRenderView(curPlace: MutableState<StorageName>?, itemImage: MutableState<String>?, onPlaceOrItemSelect: (StorageName, String) -> Unit) {
    var text by remember { mutableStateOf("Найти") }
    var searchValue by remember { //объект для работы с текстом, для TextField
        mutableStateOf("") //его начальное значение
    }
    val dataHolder = DataHolder()
    var storageNameToPngMap = dataHolder.getStorageNameToPngMap()
//    var imageSrc by remember { mutableStateOf(storageNameToPngMap[StorageName.CUSTOM_PLACE]) }
//    var imageSrc by remember { mutableStateOf(storageNameToPngMap[curPlace?.value]) }
    var imageSrc = storageNameToPngMap[curPlace?.value]
    val things = dataHolder.getData()
    println("all things: ")
    things?.forEach { println(" $it") }
    val arrayOfNames = dataHolder.getItemNamesFromDB() //получаем из БД
    val itemsFromDB = dataHolder.itemsFromDB
    var namesList = arrayOfNames.toMutableList()
    namesList.clear()
    val textStyle = TextStyle(fontSize = 20.sp)
    var isLazyRowVisible by remember { mutableStateOf(false) }
    var openDialog by remember { mutableStateOf(false)}
    var imageRatio by remember { mutableStateOf(1.0) }
    var imageRotate = remember {mutableStateOf(0f)}
    var curPlaceItems = remember { mutableListOf(Thing()) }
    var place = StorageName.CUSTOM_PLACE

    if (openDialog)
        AlertDialog( // для показа сообщения о ненайденном объекте
            onDismissRequest = { openDialog=false },//действия при закрытии окна
            text = { Text(text = "Нет такого", fontSize = 20.sp) },//содержимое окна
            confirmButton = { //кнопка Ok, которая будет закрывать окно
                Button(onClick = { openDialog=false })
                { Text(text = "OK") }
            }
        )
    MaterialTheme {
        Column(
            modifier = Modifier
//                .fillMaxSize() //заполняем всё доступное пространство
                .wrapContentWidth()
//                .weight(2f)
            ,
            horizontalAlignment = Alignment.CenterHorizontally, //по центру горизонтально
//            verticalArrangement = Arrangement.Center //и вертикально
        ) { // вертикальная колонка для размещения объектов
            Row() {
                TextField(
                    value = searchValue, //связываем текст из поля с созданным ранее объектом
                    onValueChange = { newText -> //обработчик ввода значений в поле
                        searchValue = newText //все изменения сохраняем в наш объект
//                        namesList.forEach { print("$it ") }
                        if (newText.length >= 3) { //если в поиске >3 букв
                            namesList.clear()
                            arrayOfNames.forEach {
                                var accept = false
                                it.split(" ").forEach { word ->
                                    if (word.lowercase(Locale.getDefault()).startsWith(newText.lowercase())) accept = true
                                    //todo add check for several words
                                }
                                if (accept) namesList.add(it) //todo сделать фильтрацию объектов в таблице согласно списку подходящих
                            }
                        } else isLazyRowVisible = false
                        if (namesList.isNotEmpty()) isLazyRowVisible = true
                    },
                    textStyle = TextStyle( //объект для изменения стиля текста
                        fontSize = 14.sp //увеличиваем шрифт
                    ),
                    placeholder = { Text(text = "Введите текст для поиска") }
                )
                Button(onClick = { //todo выяснить, почему вылетает с ошибкой Index 0 out of bounds при втором поиске (иногда)
                    imageSrc = searchItem(itemsFromDB, searchValue, storageNameToPngMap)
                    namesList.clear()
                    storageNameToPngMap.forEach { k, v ->
                        if(v==imageSrc) place = k
                    }
                    curPlaceItems = things?.filter {
                        it is Item && it.place.name.toString() == place.toString()
                    } as MutableList<Thing>
                    println("in place: $curPlaceItems")
                    onPlaceOrItemSelect(curPlace!!.value, searchValue)
                    if (imageSrc == "206.png") openDialog = true
                }) {
                    Text(text)
                }
            }
            if (isLazyRowVisible) LazyRow() {
                items(namesList) { name ->
                    Text(
                        text = name,
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable(onClick = {
                                searchValue = name
                                imageSrc = searchItem(itemsFromDB, searchValue, storageNameToPngMap)
                                namesList.clear()
                                storageNameToPngMap.forEach { k, v ->
                                    if(v==imageSrc) place = k
                                }
                                onPlaceOrItemSelect(curPlace!!.value, searchValue)
                                curPlaceItems = things?.filter {
                                    it is Item && it.place.name.toString() == place.toString()
                                } as MutableList<Thing>
                            })
                            .border(BorderStroke(2.dp, Color.Gray))
                            .padding(4.dp)
                    )
                }
            }
            val labImageFile = loadImageFrom("src/main/resources/$imageSrc")
//            Row(){
                Image(
                    bitmap = labImageFile,
//                painter = painterResource(imageSrc),
//                painter = imageReso,
                    contentDescription = "",
//                    contentScale = ContentScale.Fit,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
//                        .wrapContentWidth()
                        .wrapContentSize()
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val coords = offset // backShelf: upper left x=355.0  y=33.0,   lowe right x=408.0  y=121.0
                                // при размерах картинки 784 x 441
                                println("image clicked on x=${coords.x}  y=${coords.y}")
                                val x = coords.x * imageRatio //скалируем координаты относительно коэф-та изменения картинки
                                val y = coords.y * imageRatio
                                println("after scaling x=${x}  y=${y}")
//                                var place: StorageName
//                                BACK_SHELF, CENTER_TABLES, TABLE_AT_DOOR, CUSTOM_PLACE, ROBOT_STAND, CABLE_STAND
                                place = getStorageName(x, y, imageRotate)
                                onPlaceOrItemSelect(place, "")
                                imageSrc = storageNameToPngMap[place]
                                curPlaceItems = things?.filter {
                                    it is Item && it.place.name.toString() == place.toString()
                                } as MutableList<Thing>
                                println("in place: $curPlaceItems")
                            }

                        }
                        .onGloballyPositioned { layoutCoordinates ->
                            // Get the size of the image in pixels
                            val size = layoutCoordinates.size // IntSize (width, height)
                            println("image size = ${size.width} x ${size.height}")
                            imageRatio = 784.0 / size.width //получаем коэффициент изменения размера картинки
                        }
                        .rotate(imageRotate.value)
                )
            Button(  //todo сделать возможность поворота изображения лаборатории в зависимости от положения устройства с программой
                onClick = {
                    imageRotate.value+=90
                    if (imageRotate.value==360f) imageRotate.value = 0f
                    println("lab image rotate = $imageRotate.value")
                },
                enabled = false

            ){
                Text(text = "rotate")
            }
//            var  itemImageFile = loadImageFrom("Images/${itemImage?.value}.png")
            var  itemImageFile = loadImageFrom(itemImage!!.value)
            if (itemImage==null || itemImage.value == "")
             itemImageFile = loadImageFrom("Images/default.png")

            Image(
                bitmap = itemImageFile,
                contentDescription = "",
//                    contentScale = ContentScale.Fit,
                contentScale = ContentScale.FillHeight,

            )
//                LazyColumn (modifier = Modifier
//                    .wrapContentWidth()
//                ) {
//                    items(curPlaceItems){thing->
//                        println("item in LazyColumn: ${thing}")
//                        Text(
//                            text = if (thing is Item) thing.name else ""
//                        )
//                    }
//                }
//            }
        }
    }
}

private fun getStorageName(x: Double, y: Double, imageRotate: MutableState<Float>): StorageName =
    if (imageRotate.value==0f) {
        println("---!! 0 degrees !!----")
        when {
            x in 355.0..408.0 && y in 33.0..121.0 -> {
                println("backShelf")
                StorageName.BACK_SHELF
            }

            x in 349.0..394.0 && y in 208.0..337.0 -> {
                println("center")
                StorageName.CENTER_TABLES
            }

            x in 178.0..252.0 && y in 273.0..302.0 -> {
                println("cableStand")
                StorageName.CABLE_STAND
            }

            x in 474.0..564.0 && y in 269.0..303.0 -> {
                println("door")
                StorageName.TABLE_AT_DOOR
            }

            x in 150.0..235.0 && y in 357.0..397.0 -> {
                println("robotStand")
                StorageName.ROBOT_STAND
            }

            x in 440.0..469.0 && y in 136.0..180.0 -> {
                println("UNDER_3D_PRINTER")
                StorageName.UNDER_3D_PRINTER
            }
            //x=406.0  y=127.0   x=432.0  y=166.0
            else -> {
                StorageName.CUSTOM_PLACE
            }
        }
    } else {
        println("---!! not 0 degrees !!----")
        StorageName.CUSTOM_PLACE
    }

fun loadImageFrom(filePath: String): ImageBitmap { //ф-ия для получения изображения из файла
    println("filePath = $filePath")
    var bytes: ByteArray
    if (!filePath.isEmpty()) {
        bytes = Files.readAllBytes(Path.of(filePath)) // path relative to project root
    } else bytes = Files.readAllBytes(Path.of("Images/default.png"))
    return Image.makeFromEncoded(bytes).toComposeImageBitmap()
}

fun main() = application {
    val windowState = rememberWindowState(
        position = WindowPosition(Alignment.Center)
    )
    Window(
        state = windowState,
        onCloseRequest = ::exitApplication
    ) {
        LabRenderView(null, null){ storageName, item ->

        }
    }
}
