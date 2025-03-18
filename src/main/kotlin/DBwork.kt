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

//    fun getLastObjectFromCollection(database: Database, collectionName: String): Result? {
//        // Create a query to select all documents from the specified collection
//        val query = QueryBuilder
//            .select(SelectResult.all())
//            .from(DataSource.collection(collectionName))
//            .orderBy(Ordering.property("timestamp").descending()) // Sort by timestamp in descending order
//            .limit(Expression.intValue(1)) // Limit to 1 result
//
//        // Execute the query
//        val resultSet = query.execute()
//
//        // Get the first (and only) result
//        return resultSet.allResults().firstOrNull()
//    }

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

    fun deleteObjectFromCollectiobById(id: String, collection: String) {
        val collection = db.getCollection(collection)
        val sourceDoc = collection!!.getDocument(id)
        collection.delete(sourceDoc!!)
        getAllCollectionsFromDB()

    }
}