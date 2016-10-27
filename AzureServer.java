package com.tianwen.eeducation.thirddata.factory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Semaphore;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;

import com.microsoft.windowsazure.Configuration;
import com.microsoft.windowsazure.core.utils.ConnectionStringSyntaxException;
import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.services.servicebus.ServiceBusConfiguration;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContract;
import com.microsoft.windowsazure.services.servicebus.ServiceBusService;
import com.microsoft.windowsazure.services.servicebus.models.BrokeredMessage;
import com.microsoft.windowsazure.services.servicebus.models.CreateSubscriptionResult;
import com.microsoft.windowsazure.services.servicebus.models.ListQueuesResult;
import com.microsoft.windowsazure.services.servicebus.models.ListSubscriptionsResult;
import com.microsoft.windowsazure.services.servicebus.models.ListTopicsResult;
import com.microsoft.windowsazure.services.servicebus.models.QueueInfo;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveMessageOptions;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveMode;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveQueueMessageResult;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveSubscriptionMessageResult;
import com.microsoft.windowsazure.services.servicebus.models.SubscriptionInfo;
import com.microsoft.windowsazure.services.servicebus.models.TopicInfo;
import com.tianwen.eeducation.thirddata.common.IParam;
import com.tianwen.eeducation.thirddata.utils.PropertiesUtils;

/**
 * <p>
 * Title:AzureServer
 * </p>
 * <p>
 * Description:Azure获取数据服务
 * </p>
 * <p>
 * Company:天闻数媒
 * </p>
 * 
 * @version $ [版本号, 2016年8月3日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 * @author lqd
 * @date 2016年9月22日 下午2:00:19
 * @doc
 */
public class AzureServer
{
	private static Logger						logger			= Logger.getLogger(AzureServer.class);

	private static ServiceBusContract			serviceBusContract;

	// 非集群或非分布式的情况下判断，若有集群或分布式则采用缓存或分布式锁处理
	private static CopyOnWriteArraySet<String>	copyOnWriteSet	= new CopyOnWriteArraySet<String>();

	final Semaphore								semaphore		= new Semaphore(1);

	private AzureServer()
	{
	}

	public static AzureServer getInstance()
	{
		return AzureServerInner.azureServer;
	}

	private static class AzureServerInner
	{
		private static AzureServer azureServer = null;

		static
		{
			azureServer = new AzureServer();
			try
			{
				azureServer.ServicebusConnector();
			} catch (IOException e)
			{
				e.printStackTrace();
			} catch (ConnectionStringSyntaxException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void ServicebusConnector() throws IOException, ConnectionStringSyntaxException
	{
		String accessKey = PropertiesUtils.getInstance().getAccesskey();
		String accessKeyName = PropertiesUtils.getInstance().getAccesskeyname();
		String namespace = PropertiesUtils.getInstance().getNamespace();
		String serviceBusRootUri = PropertiesUtils.getInstance().getServicebusrooturi();
		logger.info("accessKey:"+accessKey+",accessKeyName:"+accessKeyName+",namespace:"+namespace+",serviceBusRootUri:"+serviceBusRootUri);
		Configuration configuration = ServiceBusConfiguration.configureWithSASAuthentication(namespace, accessKeyName,
				accessKey, serviceBusRootUri);

		serviceBusContract = ServiceBusService.create(configuration);
	}

	/**
	 * @Description:获取队列信息[方法描述]
	 * @return
	 * @throws ServiceException
	 *             CopyOnWriteArrayList<String>
	 * @exception: [违例类型]
	 *                 [违例说明]
	 * @author: lqd
	 * @time:2016年9月23日 上午10:53:35
	 */
	public CopyOnWriteArrayList<String> displayQueueInfo() throws ServiceException
	{
		CopyOnWriteArrayList<String> copyOnWriteArrayList = new CopyOnWriteArrayList<String>();
		ListQueuesResult listResult = serviceBusContract.listQueues();
		java.util.List<QueueInfo> list = listResult.getItems();
		for (int i = 0; i < list.size(); i++)
		{
			QueueInfo qi = list.get(i);
			String path = qi.getPath();
			long count = qi.getMessageCount();
			logger.debug("Queue path:" + path + ",count:" + count);
			copyOnWriteArrayList.add(path);
		}
		return copyOnWriteArrayList;
	}

	/**
	 * @Description:获取主题信息[方法描述]
	 * @return
	 * @throws ServiceException
	 *             CopyOnWriteArrayList<String>
	 * @exception: [违例类型]
	 *                 [违例说明]
	 * @author: lqd
	 * @time:2016年9月23日 上午10:53:25
	 */
	public CopyOnWriteArrayList<String> displayTopicInfo() throws ServiceException
	{
		CopyOnWriteArrayList<String> copyOnWriteArrayList = new CopyOnWriteArrayList<String>();
		ListTopicsResult listResult = serviceBusContract.listTopics();
		java.util.List<TopicInfo> list = listResult.getItems();
		for (int i = 0; i < list.size(); i++)
		{
			TopicInfo qi = list.get(i);
			String path = qi.getPath();
			logger.debug("Topic path:" + path);
			copyOnWriteArrayList.add(path);
		}
		return copyOnWriteArrayList;
	}

	/**
	 * @Description:创建主题[方法描述]
	 * @param topicName
	 * @param topicByte
	 * @return
	 * @throws Exception
	 *             String
	 * @exception: [违例类型]
	 *                 [违例说明]
	 * @author: lqd
	 * @time:2016年9月22日 下午5:51:21
	 */
	public String createTopic(String topicName, long topicByte) throws Exception
	{
		TopicInfo topic = new TopicInfo();
		topic.setPath(topicName);
		topic.setSizeInBytes(topicByte);
		com.microsoft.windowsazure.services.servicebus.models.CreateTopicResult result = serviceBusContract
				.createTopic(topic);
		TopicInfo topicResult = result.getValue();
		return topicResult.getPath();
	}

	/**
	 * @Description:创建队列[方法描述]
	 * @param topicName
	 * @param topicByte
	 * @return
	 * @throws Exception
	 *             String
	 * @exception: [违例类型]
	 *                 [违例说明]
	 * @author: lqd
	 * @time:2016年9月22日 下午5:51:21
	 */
	public String createQueue(String queueName, long queueByte) throws Exception
	{
		QueueInfo queueInfo = new QueueInfo();
		queueInfo.setPath(queueName);
		queueInfo.setSizeInBytes(queueByte);
		com.microsoft.windowsazure.services.servicebus.models.CreateQueueResult result = serviceBusContract
				.createQueue(queueInfo);
		QueueInfo queueResult = result.getValue();
		return queueResult.getPath();
	}

	/**
	 * @Description:给主题发送消息[方法描述]
	 * @param topicName
	 * @param msg
	 * @param property
	 * @throws Exception
	 *             void
	 * @exception: [违例类型]
	 *                 [违例说明]
	 * @author: lqd
	 * @time:2016年9月23日 上午10:56:34
	 */
	public void sendMessageToTopic(String topicName, String msg, Map<String, String> property) throws Exception
	{
		BrokeredMessage message = new BrokeredMessage(msg);
		Iterator<Map.Entry<String, String>> it = property.entrySet().iterator();
		while (it.hasNext())
		{
			Entry<String, String> entry = it.next();
			message.setProperty(entry.getKey(), entry.getValue());
		}
		serviceBusContract.sendTopicMessage(topicName, message);
	}

	/**
	 * @Description:给队列发送消息[方法描述]
	 * @param topicName
	 * @param msg
	 * @param property
	 * @throws Exception
	 *             void
	 * @exception: [违例类型]
	 *                 [违例说明]
	 * @author: lqd
	 * @time:2016年9月23日 上午10:56:34
	 */
	public void sendMessageToQueue(String queueName, String msg, Map<String, String> property) throws Exception
	{
		BrokeredMessage message = new BrokeredMessage(msg);
		Iterator<Map.Entry<String, String>> it = property.entrySet().iterator();
		while (it.hasNext())
		{
			Entry<String, String> entry = it.next();
			message.setProperty(entry.getKey(), entry.getValue());
		}
		serviceBusContract.sendQueueMessage(queueName, message);
	}

	/**
	 * @Description:创建订阅[方法描述]
	 * @param topicName
	 * @param subscriptionName
	 * @return
	 * @throws Exception
	 *             String
	 * @exception: [违例类型]
	 *                 [违例说明]
	 * @author: lqd
	 * @time:2016年9月22日 下午5:54:26
	 */
	public String creatSubscription(String topicName, String subscriptionName) throws Exception
	{
		SubscriptionInfo subscription = new SubscriptionInfo();
		subscription.setName(subscriptionName);
		CreateSubscriptionResult subResult = serviceBusContract.createSubscription(topicName, subscription);
		SubscriptionInfo subscriptionRslt = subResult.getValue();
		if (null != subscriptionRslt)
		{
			if (logger.isDebugEnabled())
			{
				logger.debug("status:" + subscriptionRslt.getStatus() + ",name:" + subscriptionRslt.getName() + ",rule:"
						+ subscriptionRslt.getDefaultRuleDescription());
			}
			return subscriptionRslt.getName();
		}
		logger.debug("在主题" + topicName + "下,创建订阅" + subscriptionName + "失败！");
		return null;
	}

	/**
	 * @Description:接收消息 [方法描述]
	 * @param topicName
	 * @param subscriptionName
	 * @return
	 * @throws Exception
	 *             String
	 * @exception: [违例类型]
	 *                 [违例说明]
	 * @author: lqd
	 * @time:2016年9月23日 上午10:21:07
	 */
	public BrokeredMessage receiveSubscriptionMessage(String topicName, String subscriptionName) throws Exception
	{
		if (isExits(topicName, subscriptionName))
		{
			BrokeredMessage brokeredMessage;
			try
			{
				ReceiveMessageOptions opts = ReceiveMessageOptions.DEFAULT;
				opts.setReceiveMode(ReceiveMode.PEEK_LOCK);
				ReceiveSubscriptionMessageResult result = serviceBusContract.receiveSubscriptionMessage(topicName,
						subscriptionName, opts);
				brokeredMessage = result.getValue();
				return brokeredMessage;
			} catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
			
		}
		return null;
	}

	/**
	 * @Description:提交事务 [方法描述]
	 * @param brokeredMessage
	 * void  
	 * @exception:   [违例类型] [违例说明]
	 * @author: lqd
	 * @time:2016年10月12日 上午10:41:24
	 */
	public void submitMessage(BrokeredMessage brokeredMessage)
	{
		try
		{
			serviceBusContract.deleteMessage(brokeredMessage);
			
		} catch (ServiceException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @Description:失败后回滚事务[方法描述]
	 * @param brokeredMessage
	 *            void
	 * @exception: [违例类型]
	 *                 [违例说明]
	 * @author: lqd
	 * @time:2016年10月12日 上午10:38:12
	 */
	public void rollbackMessage(BrokeredMessage brokeredMessage)
	{
		try
		{
			serviceBusContract.unlockMessage(brokeredMessage);

		} catch (ServiceException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @Description:判断是否存在订阅，若不存在，再创建订阅[方法描述]
	 * @param topicName
	 * @param subscriptionName
	 * @return boolean
	 * @exception: [违例类型]
	 *                 [违例说明]
	 * @author: lqd
	 * @throws Exception
	 * @time:2016年10月10日 下午3:14:43
	 */
	public boolean isExits(String topicName, String subscriptionName) throws Exception
	{
		if (copyOnWriteSet.contains(subscriptionName))
		{
			return true;
		}
		ListSubscriptionsResult listSubResult = serviceBusContract.listSubscriptions(topicName);
		java.util.List<SubscriptionInfo> listR = listSubResult.getItems();
		for (int i = 0; i < listR.size(); i++)
		{
			SubscriptionInfo qi = listR.get(i);
			String path = qi.getName();
			if (subscriptionName.equals(path))
			{
				return true;
			}
		}
		semaphore.acquire();
		if (copyOnWriteSet.contains(subscriptionName))
		{
			return true;
		}
		String path = creatSubscription(topicName, subscriptionName);
		if (subscriptionName.equals(path))
		{
			copyOnWriteSet.add(subscriptionName);
			semaphore.release();
			return true;
		}
		semaphore.release();
		return false;
	}

	/**
	 * @Description:从数据流中获取msg[方法描述]
	 * @param bmsg
	 * @return
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 *             String
	 * @exception: [违例类型]
	 *                 [违例说明]
	 * @author: lqd
	 * @time:2016年9月23日 上午10:33:25
	 */
	public String getStringFromBodyMsg(InputStream bmsg) throws IOException, UnsupportedEncodingException
	{
		BufferedInputStream sbufmsg = new BufferedInputStream(bmsg, bmsg.available());
		String msg = null;
		if (sbufmsg != null)
		{
			int numRead = 0;
			StringBuilder sb = new StringBuilder();
			byte[] bytes = new byte[256];
			while ((numRead = sbufmsg.read(bytes)) != -1)
			{
				sb.append(new String(bytes, 0, numRead, IParam.UTF8));
			}
			msg = sb.toString();
		}
		return msg;
	}

	/**
	 * @Description:读取队列的信息[方法描述]
	 * @param qname
	 * @return
	 * @throws ServiceException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 *             String
	 * @exception: [违例类型]
	 *                 [违例说明]
	 * @author: lqd
	 * @time:2016年9月23日 上午10:59:12
	 */
	public String readMessageFromQueue(String qname) throws ServiceException, UnsupportedEncodingException, IOException
	{
		ReceiveMessageOptions opts = ReceiveMessageOptions.DEFAULT;
		opts.setPeekLock();
		ReceiveQueueMessageResult resultQM = serviceBusContract.receiveQueueMessage(qname, opts);
		BrokeredMessage brokeredMessage = resultQM.getValue();
		InputStream bmsg = brokeredMessage.getBody();
		String msg = getStringFromBodyMsg(bmsg);
		return msg;
	}

	static
	{
		try
		{
			disableSslVerification();

		} catch (KeyManagementException e)
		{
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
	}

	private static void disableSslVerification() throws NoSuchAlgorithmException, KeyManagementException
	{
		TrustManager[] trustAllCerts = new TrustManager[]
		{ new X509TrustManager()
		{
			public java.security.cert.X509Certificate[] getAcceptedIssuers()
			{
				return null;
			}

			public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException
			{

			}

			public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException
			{

			}
		} };

		// Install the all-trusting trust manager
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier()
		{
			public boolean verify(String hostname, SSLSession session)
			{
				return true;
			}
		};

		// Install the all-trusting host verifier
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	}
}
