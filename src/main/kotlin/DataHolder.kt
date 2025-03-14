/**
 * Class for returning data either from local or network storage
 */
class DataHolder {
    var things: Array<Thing>? = arrayOf(
        Place(name=StorageName.BACK_SHELF), Place(name=StorageName.CENTER_TABLES), Item(name="Arduino uno", place=Place(name=StorageName.CENTER_TABLES)),
        Item(name="Arduino Yun", place=Place(name=StorageName.BACK_SHELF)), Item(name="HexaPod", place=Place(name=StorageName.ROBOT_STAND)),
        Item(name="QuadroPod", place=Place(name=StorageName.ROBOT_STAND))
    )

    private var storageNameToPngMap = mapOf(Pair(StorageName.BACK_SHELF, "206_backShelf.png"),
                                        Pair(StorageName.BACK_SHELF, "206_backShelf.png"),
                                        Pair(StorageName.CENTER_TABLES, "206_center.png"),
                                        Pair(StorageName.TABLE_AT_DOOR, "206_door.png"),
                                        Pair(StorageName.ROBOT_STAND, "206_robotStand.png"),
                                        Pair(StorageName.CABLE_STAND, "206_cableStand.png"),
                                        Pair(StorageName.CUSTOM_PLACE, "206.png")
    )
    fun getData(): Array<Thing>? {
        return things
    }

    fun getStorageNameToPngMap(): Map<StorageName, String> {
        return storageNameToPngMap
    }

    fun getItemNames(): Array<String> {
        var itemNames = arrayOf<String>()
        for (item in things!!){
            if (item is Item) {
                itemNames = itemNames.plus(item.name)
            }
        }
        return itemNames
    }
}