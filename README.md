# ServiceBus
使用服务总线（servicebus）主题和订阅

# 1，java web中调用servicebus步骤：

可以通过wcf 和 sdk方式调用，所以下面的引用包就多了一些，包含了cxf调用和sdk的方式。

1-1，maven项目中Pom引用（服务部署WCF）:

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

1.2，代码调用参考上传的AzureServer和CXFWcfServer文件。请注意证书认证的问题。附带java的Keytool工具证书导入导出工具。
    
