import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.skia.Image
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

fun searchItem( //возвращает нужную картинку для искомого предмета
    things: Array<Thing>?,
    searchValue: String,
    storageNameToPngHashMap: HashMap<StorageName, String>
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
fun App() {
    var text by remember { mutableStateOf("Найти") }
    var searchValue by remember { //объект для работы с текстом, для TextField
        mutableStateOf("") //его начальное значение
    }
    var storageNameToPngHashMap = HashMap<StorageName, String>()
    //BACK_SHELF, CENTER_TABLES, TABLE_AT_DOOR, CUSTOM_PLACE
    storageNameToPngHashMap[StorageName.BACK_SHELF] = "206_backShelf.png"
    storageNameToPngHashMap[StorageName.CENTER_TABLES] = "206_center.png"
    var imageSrc by remember { mutableStateOf("206.png") }
    val dataHolder = DataHolder()
    val things = dataHolder.getData()
    var arrayOfNames = dataHolder.getItemNames()
    var namesList = arrayOfNames.toMutableList()
    namesList.clear()
    val textStyle = TextStyle(fontSize = 20.sp)
    var isLazyRowVisible by remember { mutableStateOf(false) }
    var openDialog by remember { mutableStateOf(false)}
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
            modifier = Modifier.fillMaxSize(), //заполняем всё доступное пространство
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
                                    if (word.lowercase(Locale.getDefault()).startsWith(newText.lowercase())) accept =
                                        true
                                    //todo add check for several words
                                }
                                if (accept) namesList.add(it)
                            }
                        } else isLazyRowVisible = false
                        if (namesList.isNotEmpty()) isLazyRowVisible = true
                    },
                    textStyle = TextStyle( //объект для изменения стиля текста
                        fontSize = 14.sp //увеличиваем шрифт
                    ),
                    placeholder = { Text(text = "Введите текст для поиска") }
                )
                Button(onClick = {
                    imageSrc = searchItem(things, searchValue, storageNameToPngHashMap)
                    namesList.clear()
//                    println("item = $item")
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
                                imageSrc = searchItem(things, searchValue, storageNameToPngHashMap)
                                namesList.clear()
                            })
                            .border(BorderStroke(2.dp, Color.Gray))
                            .padding(4.dp)
                    )
                }
            }
            val file = loadImageFrom("src/main/resources/$imageSrc")
            Image(
                bitmap = file,
//                painter = painterResource(imageSrc),
//                painter = imageReso,
                contentDescription = "",
                contentScale = ContentScale.Fit
            )
                //todo сделать возможность кликом по месту на картинке посмотреть, какие там объекты
        }
    }
}

fun loadImageFrom(filePath: String): ImageBitmap { //ф-ия для получения изображения из файла
    val bytes = Files.readAllBytes(Path.of(filePath)) // path relative to project root
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
        App()
    }
}
