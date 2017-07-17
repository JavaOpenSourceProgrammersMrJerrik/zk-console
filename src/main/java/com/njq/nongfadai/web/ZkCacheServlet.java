package com.njq.nongfadai.web;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.njq.nongfadai.util.ZkCache;
import com.njq.nongfadai.util.ZkCfgFactory;
import com.njq.nongfadai.util.ZkManagerImpl;

public class ZkCacheServlet extends HttpServlet {
	
	private static final Logger log = LoggerFactory
			.getLogger(ZkCacheServlet.class);
	
	private static final long serialVersionUID = 1L;

    public ZkCacheServlet() {
    	System.out.println("正在启动服务()...");
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.info("ZKCacheServlet begin{}...");
		for(Map<String , Object> m : ZkCfgFactory.getZkCfgManager().query()){
			log.info("ID : {},CONNECTSTR : {},SESSIONTIMEOUT : {}",new Object[]{m.get("ID"),m.get("CONNECTSTR"),m.get("SESSIONTIMEOUT")});
			ZkCache.put(m.get("ID").toString(), ZkManagerImpl.createZk().connect(m.get("CONNECTSTR").toString(), Integer.parseInt(m.get("SESSIONTIMEOUT").toString())));
		}
		for(String key : ZkCache.get_cache().keySet()){
			log.info("key : {} , zk : {}",key,ZkCache.get(key));
		}
	}
	
	@Override
	public void init() throws ServletException {
		ZkCache.init(ZkCfgFactory.getZkCfgManager());
		log.info("init {} zk instance" , ZkCache.size());
		super.init();
	}

}
