import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.couchbase.lite.CouchbaseLite
import com.couchbase.lite.Database
import com.couchbase.lite.DatabaseConfiguration

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

        }
    }
}


fun main() = application {
    CouchbaseLite.init()
    val cfg = DatabaseConfiguration()
    var database = Database("mydb", cfg)
    val windowState = rememberWindowState(
        position = WindowPosition(Alignment.Center)
    )
    Window(
        state = windowState,
        onCloseRequest = ::exitApplication
    ) {
        OrganiserGUI()
    }
}