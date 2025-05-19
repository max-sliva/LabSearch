import androidx.compose.runtime.Immutable
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.couchbase.lite.*
import com.google.gson.Gson
//import com.couchbase.client.kotlin.query.dsl.Select.select

@Immutable
class DBwork {
    var db: Database

    init {
        CouchbaseLite.init()
        println("Starting DB in DBwork")
        val cfg = DatabaseConfiguration()
        db = Database("mydb", cfg)
//        val collectionPlaces = database.createCollection("Places")
//        val collectionItems = database.createCollection("Items")
    }

    fun getDB() = db

    fun getAllObjectsForCollection(collectionName: String): SnapshotStateList<Thing> {
        val collection = db.getCollection(collectionName)
        var objList = SnapshotStateList<Thing>()
        val query: Query = QueryBuilder.select(SelectResult.all()).from(DataSource.collection(collection!!))
        //    .orderBy(Ordering.property("id").ascending())

        query.execute().use { result ->
            for (row in result) {
                val dict: Dictionary? = row.getDictionary(0)
//                    val documentId = row.getString("id")
//                    println("Document ID: $documentId")
                // Process each document here
                when (collectionName) {
                    "Items" -> {
                        val idValue = dict!!.getString("id")
                        val nameValue = dict.getString("name")
                        val placeValue = dict.getDictionary("place")!!.getString("name")
                        val infoValue = dict.getString("info")
                        val imgValue = dict.getString("img")?:"default"
//                        println("dict = $dict")
//                        print("item id = $idValue,")
//                        print("item name = $nameValue,")
//                        println(" place name = $placeValue")
//                        print("item info = $infoValue,")
                        objList.add(Item(idValue!!, nameValue!!, Place(name=StorageName.valueOf(placeValue!!)), infoValue!!, imgValue!!))
                    }
                    "Places" ->{

                    }
                }
            }
        }
//        println("in collection $collectionName: ")
//        objList.forEach {
//            println("obj: $it")
//        }
        return objList /*.sortBy {
            it.id
        }*/
    }

    fun getAllObjectsForPlace(collectionName: String, place: StorageName): List<Thing> {
//        var objList = SnapshotStateList<Thing>()
        val listForCollection = getAllObjectsForCollection(collectionName)
        val objList = listForCollection.filter {(it as Item).place.name == place}

        return objList
    }

    fun clearCollection(collectionName: String){
        val items = getAllObjectsForCollection(collectionName)
        items.forEach {
            deleteObjectFromCollectiobById(it.id, collectionName)
        }
        val collection = db.getCollection(collectionName)
        println("after clear in $collectionName there are ${collection!!.count} items")
//        val query = "SELECT META().id FROM `bucket`.`scope`.`collection`"
//        val result = collection.query(query)
//        for (row in result.rowsAsObject()) {
//            val id = row.getString("id")
//            collection.remove(id)
//        }
    }

    fun getAllCollectionsFromDB() {
        db.scopes.forEach { scope ->
            println("Scope :: ${scope.name}")

            // Get all collections for the current scope
            scope.collections.forEach { collection ->
//                println("Collection :: ${collection.name}")
//            println("in db ${ db.count } items")
                // Create a SQL++ query to get all documents from the collection
                val query: Query = QueryBuilder.select(SelectResult.all()).from(DataSource.collection(collection))
//                val query: Query = QueryBuilder.select(SelectResult.all()).from(DataSource.collection(collection)).orderBy(Ordering.property("id").ascending())

                query.execute().use { result ->
                    for (row in result) {
                        val dict: Dictionary? = row.getDictionary(0)
//                    val documentId = row.getString("id")
//                    println("Document ID: $documentId")
                        // Process each document here
                        println("dict = $dict")
                        val nameValue = dict!!.getString("name")
                        print("item name = $nameValue,")
                        val placeValue = dict.getDictionary("place")!!.getString("name")
                        println(" place name = $placeValue")
                    }
                }
                val collectionItems = db.getCollection("Items")
                println("in collection 'Items' ${collectionItems!!.count} objects")
            }
        }
    }

    fun isItemInCollection(name: String?, collection: String): Boolean {
        val itemsInCollection = getAllObjectsForCollection(collection)
        itemsInCollection.forEach {
            if ((it as Item).name==name) return true
        }
        return false
    }

    fun addAllItemsFromListToCollection(itemsList: ArrayList<Item?>, collection: String, onAddItem: (i: Int) -> Unit) {
        var i = 0
        itemsList.forEach {
            if (!isItemInCollection(it?.name, collection)){
//                println("${it?.name} is adding to db")
                val id = getLastIdPlusOne(collection)
                it?.id = id!!
                addObjectToCollection(it?.id!!, it, collection)
                i++
                onAddItem(i)
            } else  println("${it?.name} is already in db")
        }
    }

    fun addObjectToCollection(id: String, obj: Thing, collection: String) {
//        var mutableDoc = MutableDocument()
        val collectionInDB = db.getCollection(collection)
        val gson = Gson()
        val json = gson.toJson(obj)
//        println("json = $json")
        val mutableDoc = MutableDocument(obj.id).setJSON(json)
//        println("mutableDoc = $mutableDoc")
        collectionInDB?.save(mutableDoc)
        println("added obj with id = ${obj.id}")
//        getAllCollectionsFromDB()
    }

    fun findMaxNumericId(collectionName: String): Long? {
        val collection = db.getCollection(collectionName)
        val query = QueryBuilder
            .select(SelectResult.expression(Expression.property("id")))
            .from(DataSource.collection(collection!!))
            .where(Expression.property("id").isValued())

        var maxId: Long? = null
        try {
            query.execute().use { resultSet ->
                for (result in resultSet) {
                    val idString = result.getString("id") ?: continue

                    // Check if string is numeric without regex
                    if (idString.isEmpty() || !idString.all { it.isDigit() }) {
                        continue // Skip non-numeric values
                    }

                    idString.toLongOrNull()?.let { numericId ->
                        if (maxId == null || numericId > maxId!!) {
                            maxId = numericId
                        }
                    }
                }
            }
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
        return maxId
    }

    fun getLastDocument(collectionName: String): Result? {
            val collection = db.getCollection(collectionName)
            // Create a query to fetch documents sorted by a specific field in descending order
            val query = QueryBuilder.select(SelectResult.all())
                .from(DataSource.collection(collection!!))
                .orderBy(Ordering.property("id").descending()) // Replace "timestamp" with your sort field
//                .orderBy(Ordering.property(Meta.id).descending()) // Replace "timestamp" with your sort field
                .limit(Expression.intValue(1)) // Limit to one document

            val resultSet = query.execute()

            // Get the first (and only) result
            return resultSet.allResults().firstOrNull()
    }

    fun getLastId(collectionName: String): String? {
        val res = getLastDocument(collectionName = collectionName)
//        println("res = ${res!!.toMap()}")
        val items = res?.getDictionary("Items")
//        println("items = $items")
        val id = items?.getString("id")
//    val id = res.getString("id")
//        println("last id = $id")
        return id
    }

//    fun getLastIdPlusOne(collectionName: String): String? {
//        val res = getLastDocument(collectionName = collectionName)
////        println("res = ${res!!.toMap()}")
//        val items = res?.getDictionary("Items")
////        println("items = $items")
//        val id = if (items!=null) items?.getString("id")!!.toInt()+1 else 1
////    val id = res.getString("id")
//        println("last id = $id")
//        return id.toString()
//    }

    fun getLastIdPlusOne(collectionName: String): String? {
        val maxIdPlusOne = (findMaxNumericId("Items")?:0)+1
        return maxIdPlusOne.toString()

    }

    fun deleteObjectFromCollectiobById(id: String, collection: String) {
        val collection = db.getCollection(collection)
        val sourceDoc = collection!!.getDocument(id)
        collection.delete(sourceDoc!!)
        getAllCollectionsFromDB()

    }
}
