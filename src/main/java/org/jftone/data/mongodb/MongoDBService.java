package org.jftone.data.mongodb;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jftone.annotation.Component;
import org.jftone.config.PropertyConfigurer;
import org.jftone.util.FileUtil;
import org.jftone.util.IData;
import org.jftone.util.StringUtil;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

@Component(init="init", destroy="destroy")
public class MongoDBService {
	private static Log log = LogFactory.getLog(MongoDBService.class);

	private MongoClient mongoClient;
	private String databaseName;
	
	/**
	 * 初始化mongodb连接
	 * @throws IOException 
	 */
	public void init() throws IOException {
		String mongodbConf = PropertyConfigurer.get(PropertyConfigurer.MONGODB);
		try {
			if(StringUtil.isBlank(mongodbConf)) {
        		return;
        	}
			IData<String, Object> props = FileUtil.loadClasspathPropsData(mongodbConf);
			if(null == props || props.isEmpty()) {
				return;
			}
			databaseName = props.getString("database");
			
			String connectionsPerHost = props.getString("connectionsPerHost");
			String minConnectionsPerHost = props.getString("minConnectionsPerHost");
			String maxConnectionIdleTime = props.getString("maxConnectionIdleTime");
			String maxConnectionLifeTime = props.getString("maxConnectionLifeTime");
			String socketTimeout = props.getString("socketTimeout");
			String connectTimeout = props.getString("connectTimeout");
			String maxWaitTime = props.getString("maxWaitTime");
			
			Builder options = new MongoClientOptions.Builder();
			if(!StringUtil.isBlank(connectionsPerHost)) {
				options.connectionsPerHost(Integer.parseInt(connectionsPerHost));
			}
			if(!StringUtil.isBlank(minConnectionsPerHost)) {
				options.minConnectionsPerHost(Integer.parseInt(minConnectionsPerHost));
			}
			if(!StringUtil.isBlank(maxConnectionIdleTime)) {
				options.maxConnectionIdleTime(Integer.parseInt(maxConnectionIdleTime));
			}
			
			if(!StringUtil.isBlank(maxConnectionLifeTime)) {
				options.maxConnectionLifeTime(Integer.parseInt(maxConnectionLifeTime));
			}
			
			if(!StringUtil.isBlank(socketTimeout)) {
				options.socketTimeout(Integer.parseInt(socketTimeout));
			}
			
			if(!StringUtil.isBlank(connectTimeout)) {
				options.connectTimeout(Integer.parseInt(connectTimeout));
			}
			
			if(!StringUtil.isBlank(maxWaitTime)) {
				options.maxWaitTime(Integer.parseInt(maxWaitTime));
			}
			
			MongoClientURI mongoClientURI = new MongoClientURI(props.getString("url"), options);
			mongoClient = new MongoClient(mongoClientURI);
			
//			MongoCredential credential = MongoCredential.createCredential("user", "databaseName", "password".toCharArray());
//			mongoClient = new MongoClient(serverAddrs, credential, options.build());
			
			
		} catch (IOException e) {
			log.error("初始化MongoDB错误", e);
			throw e;
		}
	}

	public void destroy() {
		if (mongoClient != null) {
			mongoClient.close();
			mongoClient = null;
		}
	}
	
	MongoDatabase getMongoDatabase() {
		return mongoClient.getDatabase(databaseName);
	}

	MongoClient getMongoClient() {
		return mongoClient;
	}
}
