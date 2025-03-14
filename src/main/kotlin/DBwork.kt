import com.couchbase.lite.*

class DBwork {
    lateinit var db: Database

    init {
        CouchbaseLite.init()
        println("Starting DB")
        val cfg = DatabaseConfiguration()
        var db = Database("mydb", cfg)
//        val collectionPlaces = database.createCollection("Places")
//        val collectionItems = database.createCollection("Items")
    }

    fun getDB() = db

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
                println("in collection 'items' ${collectionItems!!.count} objects")
            }
        }
    }


    fun deleteObjectFromCollectiobById(db: Database, id: String, collection: String) {
        val collection = db.getCollection(collection)
        val sourceDoc: Document? = collection!!.getDocument(id)
        collection.delete(sourceDoc!!)
        getAllCollectionsFromDB(db)

    }
}