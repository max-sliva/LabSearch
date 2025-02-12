enum class StorageName {
    BACK_SHELF, CENTER_TABLES, TABLE_AT_DOOR, CUSTOM_PLACE
}
data class Place(var name: StorageName = StorageName.CUSTOM_PLACE, override var info:String = "", var parent: StorageName? = null, var items: Array<Thing>? = null): Thing(info)
