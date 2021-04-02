package org.jftone.data.mongodb;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.jftone.annotation.Component;
import org.jftone.annotation.Resource;
import org.jftone.util.DataMap;
import org.jftone.util.IData;
import org.jftone.util.Page;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

@Component
public class MongoTemplate {
	private MongoDatabase mongoDatabase;
	private MongoClient mongoClient;
	
	@Resource
	private MongoDBService mongoDBService;
	
	public void setMongoDBService(MongoDBService mongoDBService) {
		this.mongoDBService = mongoDBService;
		if(null != mongoDBService) {
			this.mongoClient = mongoDBService.getMongoClient();
			this.mongoDatabase = mongoDBService.getMongoDatabase();
		}
	}
	
	public MongoClient getMongoClient() {
		return this.mongoClient;
	}
	
	/**
	 * 转换成Document
	 * @param data
	 * @return
	 */
	private Document convertDocument(IData<String, Object> data){
		if(data.isEmpty()){
			return null;
		}
		Document doc = new Document(); 
		for(Map.Entry<String, Object> item : data.entrySet()){
			doc.put(item.getKey(), item.getValue());
		}
		return doc;
	}
	/**
	 * 转换成DataMap
	 * @param doc
	 * @return
	 */
	private IData<String, Object> convertData(Document doc){
		if(doc.isEmpty()){
			return null;
		}
		IData<String, Object> retData = new DataMap<String, Object>();
		for(Map.Entry<String, Object> item : doc.entrySet()){
			retData.put(item.getKey(), item.getValue());
		}
		return retData;
	}
	
	/**
	 * 获取指定Collection
	 * @param collectionName
	 * @return
	 */
	private MongoCollection<Document> getCollection(String collectionName) {
		return mongoDatabase.getCollection(collectionName);
	}
	
	/**
	 * 创建指定名称的Collection
	 * @param collectionName
	 */
	public void createCollection(String collectionName) {
		mongoDatabase.createCollection(collectionName);
	}
	
	/**
	 * 删除指定Collection
	 * @param collectionName
	 */
	public void dropCollection(String collectionName) {
		getCollection(collectionName).drop();
	}

	/**
	 * 更新
	 * @param collectionName
	 * @param data
	 */
	public void save(String collectionName, IData<String, Object> data) {
		Document doc = convertDocument(data);
		if(doc == null) {
			return;
		}
		getCollection(collectionName).insertOne(doc);
	}
	
	public void save(String collectionName, List<IData<String, Object>> dataList) {
		List<Document> docList = new ArrayList<Document>();
		if(dataList == null || dataList.isEmpty()) {
			return;
		}
		for(IData<String, Object> dataItem : dataList) {
			docList.add(convertDocument(dataItem));
		}
		getCollection(collectionName).insertMany(docList);
	}
	
	/**
	 * 通过ID获取对应行记录
	 * @param collectionName
	 * @param id
	 * @return
	 */
	public IData<String, Object> get(String collectionName, String id) {
		Bson filter = Filters.eq("_id", new ObjectId(id));
		return get(collectionName, filter);
	}
	/**
	 * 通过DataMap等于条件获取对应行记录
	 * @param collectionName
	 * @param inData
	 * @return
	 */
	public IData<String, Object> get(String collectionName, IData<String, Object> inData) {
		return get(collectionName, convertDocument(inData));
	}
	/**
	 * 通过过滤条件获取对应行记录
	 * @param collectionName
	 * @param filter
	 * @return
	 */
	public IData<String, Object> get(String collectionName, Bson filter) {
		FindIterable<Document> findIterable = null;
		if(null == filter) {
			findIterable = getCollection(collectionName).find();
		}else {
			findIterable = getCollection(collectionName).find(filter);
		}
		Document doc = findIterable.first();
		return convertData(doc);
	}
	
	/**
	 * 根据过滤条件查询指定collectionName的数据
	 * @param collectionName
	 * @param filter
	 * @return
	 */
	public List<IData<String, Object>> query(String collectionName, Bson filter) {
		List<IData<String, Object>> retList = new ArrayList<IData<String, Object>>();
		FindIterable<Document> findIterable = null;
		if(null == filter) {
			findIterable = getCollection(collectionName).find();
		}else {
			findIterable = getCollection(collectionName).find(filter);
		}
		MongoCursor<Document> cursor = findIterable.iterator();
		Document dc = null;
		while(cursor.hasNext()){
			dc = cursor.next();
			IData<String, Object> data = convertData(dc);
			if(!data.isEmpty()){
				retList.add(data);
			}
		}
		dc = null;
		return retList;
	}
	
	public List<IData<String, Object>> query(String collectionName, IData<String, Object> inData) {
		Bson filter = convertDocument(inData);
		return query(collectionName, filter);
	}
	
	/**
	 * 根据过滤条件分页查询指定collectionName的数据
	 * @param collectionName
	 * @param inData
	 * @return
	 */
	public List<IData<String, Object>> queryByPage(String collectionName, Bson filter, Page page) {
		List<IData<String, Object>> retList = new ArrayList<IData<String, Object>>();
		MongoCollection<Document> collection = getCollection(collectionName);
		FindIterable<Document> findIterable = null;
		long count = 0;
		if(null == filter) {
			count = collection.count();
			findIterable = collection.find();
		}else {
			count = collection.count(filter);
			findIterable = collection.find(filter);
		}
		page.setRecordCount(count);
		MongoCursor<Document> cursor = findIterable.skip(page.getCurrentPage()-1)
				.limit(page.getPageSize()).iterator();
		Document dc = null;
		while(cursor.hasNext()){
			dc = cursor.next();
			IData<String, Object> data = convertData(dc);
			if(!data.isEmpty()){
				retList.add(data);
			}
		}
		dc = null;
		return retList;
	}
	public List<IData<String, Object>> query(String collectionName, IData<String, Object> inData, Page page) {
		Bson filter = convertDocument(inData);
		return queryByPage(collectionName, filter, page);
	}
	
	public long delete(String collectionName, IData<String, Object> inData) {
		Bson filter = convertDocument(inData);
		return delete(collectionName, filter);
	}
	public long delete(String collectionName, String id) {
		Bson filter = Filters.eq("_id", new ObjectId(id));
		DeleteResult rs = getCollection(collectionName).deleteOne(filter);
		return rs.getDeletedCount();
	}
	
	public long delete(String collectionName, Bson filter) {
		DeleteResult rs = getCollection(collectionName).deleteMany(filter);
		return rs.getDeletedCount();
	}
	
	public void update(String collectionName, String id, IData<String, Object> setData) {
		Bson filter = Filters.eq("_id", new ObjectId(id));
		//coll.replaceOne(filter, newdoc);完全替代
		getCollection(collectionName).updateOne(filter, new Document("$set", convertDocument(setData)));
	}
	public long update(String collectionName, IData<String, Object> updateData, Bson filter) {
		UpdateResult rs = getCollection(collectionName).updateMany(filter, new Document("$set", convertDocument(updateData)));
		return rs.getModifiedCount();
	}
	public long update(String collectionName, IData<String, Object> updateData, IData<String, Object> whereData) {
		Bson filter = convertDocument(whereData);
		return update(collectionName, updateData, filter);
	}
	
}
