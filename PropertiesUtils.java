package com.tianwen.eeducation.thirddata.utils;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:配置文件读取
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
public class PropertiesUtils
{
	private static Properties	properties				= new Properties();

	private static Logger		logger					= Logger.getLogger(PropertiesUtils.class);

	/** 属性文件名 */
	public static final String	PROPERTIES_FILE_PATH	= "/wcf.properties";

	/** 主题 */
	private static final String	TOPIC_NAME_URL			= "wcf.topicManage";

	/** 订阅 */
	private static final String	SubScription_NAME_URL	= "wcf.subScriptionManage";
	
	private static final String accessKey = "azure.accessKey" ;
	
	private static final String accessKeyName = "azure.accessKeyName" ;
	
	private static final String namespace = "azure.namespace" ;
	
	private static final String serviceBusRootUri = "azure.serviceBusRootUri" ;
	
	static
	{
		PropertiesUtilsInner.propertiesUtils.initProperties(PROPERTIES_FILE_PATH);
	}

	private PropertiesUtils()
	{
	}

	public static PropertiesUtils getInstance()
	{
		return PropertiesUtilsInner.propertiesUtils;
	}

	private static class PropertiesUtilsInner
	{
		private static PropertiesUtils propertiesUtils = null;

		static
		{
			propertiesUtils = new PropertiesUtils();
		}
	}

	private void initProperties(String path)
	{
		if (StringUtils.isEmpty(path))
		{
			path = PROPERTIES_FILE_PATH ;
		}
		Resource resource = new ClassPathResource(path);
		try
		{
			properties = PropertiesLoaderUtils.loadProperties(resource);

		} catch (IOException e)
		{
			logger.error("加载配置文件wcf.properties失败原因:" + e.getMessage());
		}
	}
	
	public String getValueByKey(String key)
	{
		return getValueByKey(PROPERTIES_FILE_PATH, key) ;
	}

	public String getValueByKey(String path, String key)
	{
		// 如果properties为空，则初始化
		if (properties.isEmpty())
			initProperties(path);
		return properties.getProperty(key);
	}

	/**
	 * @Description:得到主题请求地址[方法描述]
	 * @return String
	 * @exception: [违例类型]
	 *                 [违例说明]
	 * @author: lqd
	 * @time:2016年9月21日 上午11:12:21
	 */
	public String getTopicNameUrl()
	{
		return PropertiesUtilsInner.propertiesUtils.getValueByKey(PROPERTIES_FILE_PATH, TOPIC_NAME_URL);
	}

	/**
	 * @Description:得到订阅请求地址[方法描述]
	 * @return String
	 * @exception: [违例类型]
	 *                 [违例说明]
	 * @author: lqd
	 * @time:2016年9月21日 上午11:12:26
	 */
	public String getSubscriptionNameUrl()
	{
		return PropertiesUtilsInner.propertiesUtils.getValueByKey(PROPERTIES_FILE_PATH, SubScription_NAME_URL);
	}

	public String getAccesskey()
	{
		return PropertiesUtilsInner.propertiesUtils.getValueByKey(PROPERTIES_FILE_PATH, accessKey);
	}

	public String getAccesskeyname()
	{
		return PropertiesUtilsInner.propertiesUtils.getValueByKey(PROPERTIES_FILE_PATH, accessKeyName);
	}

	public String getNamespace()
	{
		return PropertiesUtilsInner.propertiesUtils.getValueByKey(PROPERTIES_FILE_PATH, namespace);
	}

	public String getServicebusrooturi()
	{
		return PropertiesUtilsInner.propertiesUtils.getValueByKey(PROPERTIES_FILE_PATH, serviceBusRootUri);
	}
}
