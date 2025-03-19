import com.couchbase.lite.*
import com.google.gson.Gson
import com.couchbase.lite.Collection
class DBwork {
    var db: Database

    init {
        CouchbaseLite.init()
        println("Starting DB")
        val cfg = DatabaseConfiguration()
        db = Database("mydb", cfg)
//        val collectionPlaces = database.createCollection("Places")
//        val collectionItems = database.createCollection("Items")
    }

    fun getDB() = db

    fun getAllObjectsForCollection(collectionName: String): MutableList<Thing> {
        val collection = db.getCollection(collectionName)
        var objList = mutableListOf<Thing>()
        val query: Query = QueryBuilder.select(SelectResult.all()).from(DataSource.collection(collection!!))

        query.execute().use { result ->
            for (row in result) {
                val dict: Dictionary? = row.getDictionary(0)
//                    val documentId = row.getString("id")
//                    println("Document ID: $documentId")
                // Process each document here
                when (collectionName) {
                    "Items" -> {
                        val idValue = dict!!.getString("id")
                        val nameValue = dict!!.getString("name")
                        val placeValue = dict.getDictionary("place")!!.getString("name")
                        val infoValue = dict!!.getString("info")
//                        println("dict = $dict")
//                        print("item id = $idValue,")
//                        print("item name = $nameValue,")
//                        println(" place name = $placeValue")
//                        print("item info = $infoValue,")
                        objList.add(Item(idValue!!, nameValue!!, Place(name=StorageName.valueOf(placeValue!!)), infoValue!!))
                    }
                    "Places" ->{

                    }
                }
            }
        }
        println("in collection $collectionName: ")
        objList.forEach {
            println("obj: $it")
        }
        return objList
    }

    fun getAllCollectionsFromDB() {
        db.scopes.forEach { scope ->
            println("Scope :: ${scope.name}")

            // Get all collections for the current scope
            scope.collections.forEach { collection ->
                println("Collection :: ${collection.name}")
//            println("in db ${ db.count } items")
                // Create a SQL++ query to get all documents from the collection
                val query: Query = QueryBuilder.select(SelectResult.all()).from(DataSource.collection(collection))

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

    fun addObjectToCollection(id: String, obj: Thing, collection: String) {
//        var mutableDoc = MutableDocument()
        val collectionInDB = db.getCollection(collection)
        val gson = Gson()
        val json = gson.toJson(obj)
        println("json = $json")
        val mutableDoc = MutableDocument(obj.id).setJSON(json)
        println("mutableDoc = $mutableDoc")
        collectionInDB?.save(mutableDoc)
        getAllCollectionsFromDB()
    }

    fun getLastDocument(collectionName: String): Result? {
            val collection = db.getCollection(collectionName)
            // Create a query to fetch documents sorted by a specific field in descending order
            val query = QueryBuilder.select(SelectResult.all())
                .from(DataSource.collection(collection!!))
                .orderBy(Ordering.property("id").descending()) // Replace "timestamp" with your sort field
                .limit(Expression.intValue(1)) // Limit to one document

            val resultSet = query.execute()

            // Get the first (and only) result
            return resultSet.allResults().firstOrNull()
    }

    fun getLastId(collectionName: String): String? {
        val res = getLastDocument(collectionName = "Items")
//        println("res = ${res!!.toMap()}")
        val items = res?.getDictionary("Items")
//        println("items = $items")
        val id = items!!.getString("id")
//    val id = res.getString("id")
//        println("last id = $id")
        return id
    }

    fun deleteObjectFromCollectiobById(id: String, collection: String) {
        val collection = db.getCollection(collection)
        val sourceDoc = collection!!.getDocument(id)
        collection.delete(sourceDoc!!)
        getAllCollectionsFromDB()

    }
}