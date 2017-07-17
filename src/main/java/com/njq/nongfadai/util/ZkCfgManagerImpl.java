package com.njq.nongfadai.util;

import static com.njq.nongfadai.util.PropertiesReaderUtils.getValue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class ZkCfgManagerImpl implements ZkCfgManager {

	private static Logger log = LoggerFactory.getLogger(ZkCfgManagerImpl.class);

	private static final String MYSQL_DRIVER = getValue("jdbc.driverClassName");
	private static final String MYSQL_DB_URL = getValue("jdbc.url");
	private static final String MYSQL_USER_NAME = getValue("jdbc.username");
	private static final String MYSQL_PASSWORD = getValue("jdbc.password");

	private static ComboPooledDataSource dataSourcePool;

	// 静态初始化块进行初始化
	static {
		try {
			dataSourcePool = new ComboPooledDataSource();// 创建连接池实例
			dataSourcePool.setDriverClass(MYSQL_DRIVER);// 设置连接池连接数据库所需的驱动
			dataSourcePool.setJdbcUrl(MYSQL_DB_URL);// 设置连接数据库的URL
			dataSourcePool.setUser(MYSQL_USER_NAME);// 设置连接数据库的用户名
			dataSourcePool.setPassword(MYSQL_PASSWORD);// 设置连接数据库的密码
			dataSourcePool.setMaxPoolSize(40);// 设置连接池的最大连接数
			dataSourcePool.setMinPoolSize(2);// 设置连接池的最小连接数
			dataSourcePool.setInitialPoolSize(10);// 设置连接池的初始连接数
			dataSourcePool.setMaxStatements(100);// 设置连接池的缓存Statement的最大数
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static QueryRunner run = new QueryRunner(dataSourcePool);

	// 获取与指定数据库的连接
	public static ComboPooledDataSource getInstance() {
		return dataSourcePool;
	}

	// 从连接池返回一个连接
	public static Connection getConnection() {
		Connection conn = null;
		try {
			conn = dataSourcePool.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	// 释放资源
	public static void realeaseResource(ResultSet rs, PreparedStatement ps,
			Connection conn) {
		if (null != rs) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (null != ps) {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ZkCfgManagerImpl() {
	}

	public boolean init() {
		log.info("ZkCfgManagerImpl init{}...create table ZK");
		PreparedStatement ps = null;
		Connection connection = null;
		try {
			connection = getConnection();
			ps = connection.prepareStatement(ZkCfgManager.initSql);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("init zkCfg error : {}", e.getMessage());
		} finally {
			if (null != ps) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(null != connection){
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public boolean add(String des, String connectStr, String sessionTimeOut) {
		PreparedStatement ps = null;
		Connection connection = null;
		try {
			connection = getConnection();
			ps = connection.prepareStatement(
					"INSERT INTO ZK VALUES(?,?,?,?)");
			ps.setString(1, UUID.randomUUID().toString().replaceAll("-", ""));
			ps.setString(2, des);
			ps.setString(3, connectStr);
			ps.setString(4, sessionTimeOut);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("add zkCfg error : {}", e.getMessage());
		} finally {
			if (null != ps) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(null != connection){
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public List<Map<String, Object>> query() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection connection = null;
		try {
			connection = getConnection();
			ps = connection.prepareStatement("SELECT * FROM ZK");
			rs = ps.executeQuery();
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			ResultSetMetaData meta = rs.getMetaData();
			Map<String, Object> map = null;
			int cols = meta.getColumnCount();
			while (rs.next()) {
				map = new HashMap<String, Object>();
				for (int i = 0; i < cols; i++) {
					map.put(meta.getColumnName(i + 1), rs.getObject(i + 1));
				}
				list.add(map);
			}

			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (null != rs) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (null != ps) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(null != connection){
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}
		return new ArrayList<Map<String, Object>>();
	}

	public boolean update(String id, String des, String connectStr,
			String sessionTimeOut) {
		PreparedStatement ps = null;
		Connection connection = null;
		try {
			connection = getConnection();
			ps = connection
					.prepareStatement(
							"UPDATE ZK SET DES=?,CONNECTSTR=?,SESSIONTIMEOUT=? WHERE ID=?");
			ps.setString(1, des);
			ps.setString(2, connectStr);
			ps.setString(3, sessionTimeOut);
			ps.setString(4, id);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("update id={} zkCfg error : {}",
					new Object[] { id, e.getMessage() });
		} finally {
			if (null != ps) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(null != connection){
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return false;
	}

	public boolean delete(String id) {

		PreparedStatement ps = null;
		Connection connection = null;
		try {
			connection = getConnection();
			ps = connection.prepareStatement("DELETE from ZK WHERE ID = ?");
			ps.setString(1, id);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("delete id={} zkCfg error : {}",
					new Object[] { id, e.getMessage() });
		} finally {
			if (null != ps) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(null != connection){
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		}
		return false;
	}

	public Map<String, Object> findById(String id) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection connection = null;
		try {
			connection = getConnection();
			ps = connection.prepareStatement(
					"SELECT * FROM ZK WHERE ID = ?");
			ps.setString(1, id);
			rs = ps.executeQuery();
			Map<String, Object> map = new HashMap<String, Object>();
			ResultSetMetaData meta = rs.getMetaData();
			int cols = meta.getColumnCount();
			if (rs.next()) {
				for (int i = 0; i < cols; i++) {
					map.put(meta.getColumnName(i + 1).toLowerCase(),
							rs.getObject(i + 1));
				}
			}
			return map;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (null != rs) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (null != ps) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(null != connection){
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public List<Map<String, Object>> query(Integer page, Integer rows) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection connection = null;
		try {
			connection = getConnection();
			ps = connection.prepareStatement("SELECT * FROM ZK limit ?,?");
			ps.setInt(1, (page - 1) * rows);
			ps.setInt(2, rows);
			rs = ps.executeQuery();

			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			Map<String, Object> map = null;
			while (rs.next()) {
				map = new HashMap<String, Object>();
				for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
					map.put(rs.getMetaData().getColumnName(i + 1),
							rs.getObject(i + 1));
				}
				list.add(map);
			}

			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (null != rs) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (null != ps) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(null != connection){
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return new ArrayList<Map<String, Object>>();
	}

	public boolean add(String id, String des, String connectStr,
			String sessionTimeOut) {
		PreparedStatement ps = null;
		Connection connection = null;
		try {
			connection = getConnection();
			ps = connection.prepareStatement(
					"INSERT INTO ZK VALUES(?,?,?,?);");
			ps.setString(1, id);
			ps.setString(2, des);
			ps.setString(3, connectStr);
			ps.setString(4, sessionTimeOut);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("add zkCfg error : {}", e.getMessage());
		} finally {
			if (null != ps) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(null != connection){
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public int count() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection connection = null;
		try {
			connection = getConnection();
			ps = connection.prepareStatement("SELECT count(id) FROM ZK");
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("count zkCfg error : {}", e.getMessage());
		} finally {
			if (null != rs) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (null != ps) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(null != connection){
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return 0;
	}

}
