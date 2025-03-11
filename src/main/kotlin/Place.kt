

enum class StorageName {
    BACK_SHELF, CENTER_TABLES, TABLE_AT_DOOR, CUSTOM_PLACE, ROBOT_STAND, CABLE_STAND
}

class Place(info: String = "", var name: StorageName = StorageName.CUSTOM_PLACE, var parent: StorageName? = null/*, var items: Array<Thing>? = null*/): Thing(info)
