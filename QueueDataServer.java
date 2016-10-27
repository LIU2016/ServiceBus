package com.tianwen.eeducation.thirddata.task;

import javax.annotation.Resource;

import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tianwen.eeducation.thirddata.entity.condition.Condition;
import com.tianwen.eeducation.thirddata.service.IDealData;
import com.tianwen.eeducation.thirddata.service.IThirdDataService;

/**
 * <p>Title:QueueDataServer</p>
 * <p>Description:接收队列信息服务</p>
 * <p>Company:天闻数媒</p> 
 * @version $ [版本号, 2016年8月3日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 * @author lqd
 * @date 2016年9月23日 下午4:26:01
 * @doc
 */
@Component("queueDataServer")
@Lazy(false)
public class QueueDataServer
{
	@Resource(name="thirdDataService")
	private IThirdDataService ithirdDataService ;
	
	@Resource(name="baseDealData")
	private IDealData idealData ;
	
	@Scheduled(fixedRate=10000)
	public void scheduleQueueData()
	{
		Condition condition = new Condition();
		condition.setTopicKey("lgl.jiaowu.name");
		condition.setSubKey("lgl.jiaowu.name");
		ithirdDataService.queryThirdDataService(condition,idealData);
	}
}
