import androidx.compose.runtime.Immutable

@Immutable
class Item( id: String = "", var name: String, var place: Place, info: String = "", var img: String="default"): Thing(id, info){
    override fun toString(): String {
        return "id = $id,  name = $name,  place = ${place.name},  info = $info , img = $img"
    }

    fun getListOfValuesWithoutImg(): List<String> {
       return listOf(id, name, place.name.toString(), info)
    }

    fun getListOfValues(): List<String> {
       return listOf(id, name, place.name.toString(), info, img)
    }

    fun getListOfFieldNames(): List<String> {
        return listOf("id", "name", "place", "info")
    }
}
