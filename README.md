# ServiceBus
使用服务总线（servicebus）主题和订阅

# 1，java web中调用servicebus步骤：

可以通过wcf 和 sdk方式调用，所以下面的引用包就多了一些，包含了cxf调用和sdk的方式。

1-1，maven项目中Pom引用（当然spring的相关包就不列出来了）:

		<dependency>
			<groupId>org.apache.cxf</groupId>
			<artifactId>cxf-rt-frontend-jaxws</artifactId>
			<version>2.5.2</version>
		</dependency>
		<dependency>
			<groupId>org.apache.cxf</groupId>
			<artifactId>cxf-rt-databinding-aegis</artifactId>
			<version>2.5.2</version>
		</dependency>
		<dependency>
			<groupId>org.apache.cxf</groupId>
			<artifactId>cxf-rt-transports-http</artifactId>
			<version>2.5.2</version>
		</dependency>
		<dependency>
		    <groupId>com.microsoft.azure</groupId>
		    <artifactId>azure</artifactId>
		    <version>1.0.0-beta2</version>
		</dependency>
		<dependency>
		    <groupId>com.microsoft.azure</groupId>
		    <artifactId>azure-servicebus</artifactId>
		    <version>0.9.4</version>
		</dependency>

1.2，代码调用参考上传的AzureServer和CXFWcfServer文件。消息接收方采用定时器的方式获取数据。

请注意证书认证的问题。附带java的Keytool工具证书导入导出工具。

# 2，参考地址

sdk方式：https://www.azure.cn/documentation/articles/service-bus-java-how-to-use-topics-subscriptions/

WCF方式：https://msdn.microsoft.com/zh-cn/library/dn574799.aspx
