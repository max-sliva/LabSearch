/**
 * Class for returning data either from local or network storage
 */
class DataHolder {
    var things: Array<Thing>? = arrayOf(
        Place(StorageName.BACK_SHELF), Place(StorageName.CENTER_TABLES), Item("Arduino uno", Place(StorageName.CENTER_TABLES)),
        Item("Arduino Yun", Place(StorageName.BACK_SHELF))
    )

    fun getData(): Array<Thing>? {

        return things
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