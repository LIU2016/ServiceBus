package com.tianwen.eeducation.thirddata.factory;

import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.log4j.Logger;

import com.tianwen.eeducation.thirddata.utils.PropertiesUtils;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:获取wcf数据连接服务
 * </p>
 * <p>
 * Company:天闻数媒
 * </p>
 * 
 * @version $ [版本号, 2016年8月3日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 * @author lqd
 * @date 2016年9月21日 上午11:06:19
 * @doc
 */
public final class CXFWcfServer
{
	/** 主题客户端 **/
	private static org.apache.cxf.endpoint.Client	topicClient			= null;

	/** 订阅客户端 **/
	private static org.apache.cxf.endpoint.Client	subScriptionClient	= null;

	private static Logger							logger				= Logger.getLogger(CXFWcfServer.class);

	static
	{
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		try
		{
			topicClient = dcf.createClient(PropertiesUtils.getInstance().getTopicNameUrl());
			subScriptionClient = dcf.createClient(PropertiesUtils.getInstance().getSubscriptionNameUrl());

		} catch (Exception e)
		{
			logger.error("远程连接wcf服务失败:" + e.getMessage());
		}
	}

	private CXFWcfServer()
	{
	}

	public static CXFWcfServer getInstance()
	{
		return CXFWcfUtilsInner.cXFWcfServer;
	}

	private static class CXFWcfUtilsInner
	{
		private static CXFWcfServer cXFWcfServer = null;

		static
		{
			cXFWcfServer = new CXFWcfServer();
		}
	}

	public org.apache.cxf.endpoint.Client getTopicClient()
	{
		return topicClient;
	}

	public void setTopicClient(org.apache.cxf.endpoint.Client topicClient)
	{
		CXFWcfServer.topicClient = topicClient;
	}

	public org.apache.cxf.endpoint.Client getSubScriptionClient()
	{
		return subScriptionClient;
	}

	public void setSubScriptionClient(org.apache.cxf.endpoint.Client subScriptionClient)
	{
		CXFWcfServer.subScriptionClient = subScriptionClient;
	}
}
