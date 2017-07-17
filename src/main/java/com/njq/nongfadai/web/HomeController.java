package com.njq.nongfadai.web;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.njq.nongfadai.util.ZkCache;
import com.njq.nongfadai.util.ZkCfgFactory;
import com.njq.nongfadai.util.ZkManagerImpl;

@Controller
public class HomeController {

	private static final Logger log = LoggerFactory
			.getLogger(HomeController.class);
	
	@PostConstruct
	public void beforeInit(){
		//ZkCache.init(ZkCfgFactory.getZkCfgManager());
		//log.info("init {} zk instance" , ZkCache.size());
	}
	
	@RequestMapping(value = "/")
	public String init() {
		log.info("ZKCacheServlet begin{}...");
		for(Map<String , Object> m : ZkCfgFactory.getZkCfgManager().query()){
			ZkCache.put(m.get("ID").toString(), ZkManagerImpl.createZk().connect(m.get("CONNECTSTR").toString(), Integer.parseInt(m.get("SESSIONTIMEOUT").toString())));
		}
		return "home";
	}
}
