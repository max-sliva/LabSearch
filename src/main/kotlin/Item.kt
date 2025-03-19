class Item( id: String = "", var name: String, var place: Place, info: String = ""): Thing(id ,info){
    override fun toString(): String {
        return "id = $id,  name = $name,  place = ${place.name},  info = $info"
    }

    fun getListOfValues(): List<String> {
       return listOf(id, name, place.name.toString(), info)
    }
}
