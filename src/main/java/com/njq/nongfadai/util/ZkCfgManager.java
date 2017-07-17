package com.njq.nongfadai.util;

import java.util.List;
import java.util.Map;

public interface ZkCfgManager {

	public boolean init();

	public boolean add(String des, String connectStr, String sessionTimeOut);

	public boolean add(String id, String des, String connectStr,
			String sessionTimeOut);

	public List<Map<String, Object>> query();

	public List<Map<String, Object>> query(Integer page, Integer rows);

	public boolean update(String id, String des, String connectStr,
			String sessionTimeOut);

	public boolean delete(String id);

	public Map<String, Object> findById(String id);

	public int count();

	static String initSql = "CREATE TABLE IF NOT EXISTS ZK ( ID VARCHAR (200),DES VARCHAR (200),CONNECTSTR VARCHAR (200),SESSIONTIMEOUT VARCHAR (200),PRIMARY KEY (`id`))";

}
