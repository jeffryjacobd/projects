package com.suntecgroup.xelerate.cs002manageinvoicebs.function.api;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import com.suntecgroup.xelerate.sunrise.logging.XLogger;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

/**
 * @author Sruthi Subash P K
 *
 * Util class for Mongo DB related operations 
 *
 */
public class MongoDatabaseUtil{
	protected static XLogger xLogger = XLogger.getXLogger(MongoDatabaseUtil.class);
	private static String mongoDBName;
	private static String mongoDBServerURL;
	private static int mongoDBPort;
	private static int maxRetry;

	private static MongoDatabaseUtil mongoDatabaseUtil = null;
	public static MongoClient mongoDBClient = null;

	/**
	 * Get the only instance
	 * @return
	 */
	public static MongoDatabaseUtil getInstance(){
		if(null == mongoDatabaseUtil){ 
			mongoDatabaseUtil = new MongoDatabaseUtil();
			loadDataBaseProperties();
			getMongoDBClient();
		}
		return mongoDatabaseUtil;
	}

	private static void loadDataBaseProperties() {
		Properties properties = new Properties();
		try {
			properties.load(new FileReader("src/test/resources/testng.properties"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		for (Map.Entry<Object, Object> each : properties.entrySet()) {
			String key = each.getKey().toString();
			switch(key){
			case "mongoDBName" :
				mongoDBName = each.getValue().toString();
				break;
			case "mongoDBServerURL" :
				mongoDBServerURL = each.getValue().toString();
				break;
			case "mongoDBPort" :
				mongoDBPort =  Integer.parseInt(each.getValue().toString());
				break;
			case "maxRetry" :
				maxRetry =  Integer.parseInt(each.getValue().toString());
				break;
			}
		}
		xLogger.error("Inside MongoDataBaseUtil.loadDataBaseProperties():: mongoDBName :"+mongoDBName+", mongoDBServerURL : "+mongoDBServerURL+", mongoDBPort : "+mongoDBPort+", maxRetry :"+maxRetry);

	}

	/**
	 * Get the mongoDB client
	 * @return
	 * @throws Exception 
	 */
	public static void getMongoDBClient(){
		int counter = 0;
		while(null == MongoDatabaseUtil.mongoDBClient){
			xLogger.debug("mongoDBServerURL-"+mongoDBServerURL+",mongoDBPort - "+mongoDBPort);
			try {
				mongoDBClient = new MongoClient( mongoDBServerURL , mongoDBPort );
			} catch (Exception e) {
				if (++counter > maxRetry) {
					break;
				} 
			}finally {
			/*	if (null != mongoDBClient) {
					try {
						MongoDataBaseUtil.mongoDBClient.close();
					} catch (Exception e) {
						System.err.println("Error while closing MongoDB Connection"+ e.getMessage());
					}
				}*/
			}
		}			
	}

	/**
	 * Get the mongo data base connection
	 * @return
	 * @throws Exception 
	 */
	public MongoDatabase getMongoDBDatabase() throws Exception {
		MongoDatabase mongoDBDatabase = null;
		try {
			if(null == MongoDatabaseUtil.mongoDBClient){
				getMongoDBClient();
			}	
			if(null != MongoDatabaseUtil.mongoDBClient){
				mongoDBDatabase = mongoDBClient.getDatabase(mongoDBName);
			}
		} catch (Exception e) {
			xLogger.error("Error while getting MongoDB Connection"+ e.getMessage());


			throw e;
		}
		return mongoDBDatabase;
	}

	public void closeMongoClient(MongoDatabase mongoDbConnection, MongoCollection<Document> mongoDBcollection) {
		try {
			if(null != mongoDBClient){
				MongoDatabaseUtil.mongoDBClient.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the Mongo DB Collection
	 * @return 
	 * @return
	 * @throws Exception 
	 */
	public MongoCollection<Document> getMongoDBCollection(MongoDatabase dbCon,String collectionName) throws Exception {
		MongoCollection<Document> mongoDBcollection = null;
		try {
			if(null != dbCon && null!=collectionName){
				mongoDBcollection = dbCon.getCollection(collectionName);
			}
		} catch (Exception e) {
			xLogger.error("Error while taking getMongoDBCollection"+ e.getMessage());
			throw e;
		}
		return mongoDBcollection;
	}


	public Document findOneMongoDocument(MongoCollection<Document> mongoDBcollection, String[] includeFields ,Document filterQuery) {

		Bson projectionFields = Projections.fields(
				Projections.include(includeFields),
				Projections.excludeId());


		Document doc = mongoDBcollection.find(filterQuery)
				.projection(projectionFields)
				.first(); //first for findOne
		return doc;
	}

	/**
	 * Update a record in the database. Any field/value pairs in the specified
	 * values HashMap will be written into the record with the specified record
	 * key, overwriting any existing values with the same field name.
	 * 
	 * @param table
	 *          The name of the table
	 * @param key
	 *          The record key of the record to write.
	 * @param values
	 *          A HashMap of field/value pairs to update in the record
	 * @return Zero on success, a non-zero error code on error. See this class's
	 *         description for a discussion of error codes.
	 */
	public void updateMongoDocument(MongoCollection<Document> collection,String key ,Bson updateFields) {
		try {
			// criteria to update
			Document filterQuery = new Document("testCaseId", key);

			// direct update from document instead of collection filter query

			// execute update
			UpdateResult result = update(collection, filterQuery, updateFields ,false,false); 

			if (result.wasAcknowledged() && result.getMatchedCount() == 0 && result.getModifiedCount() == 0) {
				xLogger.error("Nothing updated for TestCaseId:" + key);
			}
			xLogger.debug("Updated document with TestCaseId : " + key);

		} catch (Exception e) {
			xLogger.error("Unable to update due to an error: " + e);
		}
	}

	/***
	 * 
	 * @param collectionName
	 * @param filterQuery
	 * @return
	 */
	public long getDocumentCount(String collectionName,Document filterQuery) {
		Integer documentCount = 0;
		try {
			MongoDatabase mongoDbConnection = getMongoDBDatabase();
			return getMongoDBCollection(mongoDbConnection,collectionName).countDocuments(filterQuery);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return documentCount;
	}

	/**
	 * Update a single or all documents in the collection according to the specified arguments.
	 * When upsert set to true, the new document will be inserted if there are no matches to the query filter.
	 * 
	 * @param filter Bson filter
	 * @param document Bson document
	 * @param upsert a new document should be inserted if there are no matches to the query filter
	 * @param many whether find all documents according to the query filter
	 * @return 
	 */
	public UpdateResult update(MongoCollection<Document> collection,Document filterQuery, Bson updateFields, boolean upsert, boolean many) {
		UpdateOptions options = new UpdateOptions();
		if (upsert) {
			options.upsert(true);
		}
		if (many) {
			return collection.updateMany(filterQuery, updateFields, options);
		} else {
			return collection.updateOne(filterQuery, updateFields, options);
		}
	}

	/**
	 * Get the Documents
	 * @param mongoDBcollection
	 * @return
	 * @throws Exception 
	 */
	public FindIterable<Document> getMongoDocumentsFind(MongoDatabase dbCon,MongoCollection<Document> mongoDBcollection, String collectionName) throws Exception {

		FindIterable<Document> iterDoc = null;
		try {
			if(null != mongoDBcollection){
				iterDoc = mongoDBcollection.find(); 
			}
		} catch (Exception e) {
			System.err.println("Error while taking getMongoDocuments"+ e.getMessage());
			throw e;
		}
		return iterDoc;
	}

	public void updateMongoDB(MongoCollection<Document> mongoDBcollection, Bson updates, String testCaseId){

		Document query = new Document().append("testCaseId",  testCaseId);
		try {
			UpdateResult result = mongoDBcollection.updateOne(query, updates);
			if(result.getModifiedCount()>0)
				xLogger.debug("Modified Count : "+result.getModifiedCount()+ ", Updated document with TestCaseId : " + testCaseId);
		} catch (MongoException me) {
			xLogger.error("Unable to update due to an error: " + me);
		}
	}

	/***
	 * 
	 * @param actualResult
	 * @param status
	 * @return
	 */
	public Bson buildUpdateDocument(String actualResult, String status) {
		Bson updateFields = Updates.combine(
				Updates.set("executedYN", "Y"),
				Updates.set("actualResult", actualResult),
				Updates.set("status", status),
				Updates.currentTimestamp("lastUpdated"));	
		return updateFields;
	}

	public static String getMongoDBName() {
		return mongoDBName;
	}

	public static String getMongoDBServerURL() {
		return mongoDBServerURL;
	}

	public static int getMongoDBPort() {
		return mongoDBPort;
	}
}
