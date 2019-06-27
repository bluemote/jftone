# jftone
Jftone是一套集成，简单的后台框架，数据层支持SQL，同时也支持一定程度的O/R映射，并且支持服务组件的相互依赖注入，AOP拦截，多数据源，集群事务管理。
目前仅仅支持MySQL版本，其他数据库版本暂时没有扩展，没有特殊配置，基本都支持，比如Sqlite就可以直接使用

源码中test部分为使用的示例，仅供参考

1.1	配置文件
1.1.1	web.xml配置
listener标签：

<listener>
	<listener-class>org.jftone.listener.JFToneListener</listener-class>
</listener>


JFToneListener为应用启动时候加载JFTone框架类


context-param标签
param-name固定为：appConfig
param-value 请按照实际情况配置，默认为：jftone.properties，文件请配置在classes根目录下。

<context-param>
	<param-name>appConfig</param-name>
	<param-value>jftone.properties</param-value>
</context-param>

appConfig为应用启动时候JFToneListener加载配置文件，相关参数将在后面说明


ilter标签
filter-class为固定值：org.jftone.action.JFToneFilter
param-name为固定值：config
其他标签请根据实际情况配置

<filter>
    <filter-name>JFToneFilter</filter-name>
    <filter-class>org.jftone.action.JFToneFilter</filter-class>
    <init-param>
		<param-name>config</param-name>
		<param-value>com.xxxx.xxxx.config.BaseConfig</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>JFToneFilter</filter-name>
    <url-pattern>*</url-pattern>
    <dispatcher>FORWARD</dispatcher>
    <dispatcher>REQUEST</dispatcher>
</filter-mapping>


其中JFToneFilter为应用Action控制层相关框架初始及配置
BaseConfig类需要按照实际情况配置相关资源访问映射路径
url-pattern如果只处理特定格式请求，如： *.do   则appConfig 对应格式需要特殊制定

1.1.2	jftone.properties配置

#数据库连接配置文件，该文件直接放在classes根目录
datasourceConfig=jdbc.properties

#国际化文件路径，该文件请放在classes根目录下，如果有多个文件，各文件之间以英文逗号分隔,如中文国际化文件：i18n_zh_CN.properties
i18nResource=i18n.properties

#freemarker模板文件根目录路径，请确保在web站点无法之间访问路径下
templetRoot=WEB-INF/html

#应用是否以产品模式启动
productMode=false

#项目中涉及到的sql XML文件，classes根目录下，多个文件之间以英文逗号分隔
sqlConfig=sql-config.xml

#项目中Model实体javabean文件包路径，model需要按照实际情况映射数据库中表文件，并设置相关注解，可以设置子目录包，如果多个包下，以英文逗号分隔
modelPackage=com.xxxx.xxxx.model

#项目文件Service类包路径，，并设置相关注解，可以设置子目录包, 如果多个包下，以英文逗号分隔
componentPackage=com.lezu.xxxx.service

#设置启动时候需要随应用启动的业务初始化操作或相关加载数据
listenService=com.xxxx.xxxx.config.CommonLoad

#事务配置处理
transactional=true
transactionClass=com.xxx.xxx.service..*Service
transactionMethod=save*|del*|update*

#应用资源url模式，do表示默认以 *.do 模式的请求会被拦截并处理（包括伪静态url），如果删除此配置，则以默认伪静态url形式
urlPartern=do

#设置应用是开启缓存，并制定缓存类型，目前支持memcache和redis，对应配置文件在classes根目录下：memcache-config.xml redis-config.xml，如果不需要，则删除此配置。默认情况下，即删除时候，系统需要缓存数据，则直接保存在内存中，没有过期时间限制
memcached=memcache-config.xml
redis=redis-config.xml

#设置session共享方式，在多个二级域名之间需要共享会话情况下可以配置此项，目前支持memcache和redis,通过逗号分隔，可以同时支持两种方式
sessionSharing=memcache

#开启session情况下，session超时时间
sessionTimeout=30

#session共享的二级域名,如果是tomcat8以上，则二级域名不需要使用 . 开头
cookieDomain=xxxxx.com


1.1.3	jdbc.properties配置

#数据库类型
databaseType=mysql
#数据库驱动类
driverClassName=com.mysql.jdbc.Driver
#数据库连接串
url=jdbc:mysql://localhost:3306/cailutone?useUnicode=true&characterEncoding=utf-8&autoReconnect=true&failOverReadOnly=false&useOldAliasMetadataBehavior=true
#数据库账号
username=root
#数据库密码
password=root
#初始化连接
initialSize=10
#最大连接数量
maxActive=500
#最大空闲连接
maxIdle=50
#最小空闲连接
minIdle=10
#超时等待时间以毫秒为单位
maxWait=60000

目前暂时只支持mysql，可支持多个数据库连接池
多数据源场景下：
datasource=db240,db241,db242
#以db240为前缀的其他配置
db240.databaseType=mysql
。。。。。。。
#以db241为前缀的其他配置
db241.databaseType=mysql
。。。。。。。
#以db242为前缀的其他配置
db242.databaseType=mysql
。。。。。。。



如果数据库进行了读写分离，以上面多数据源为例来说明，db240为主库，其他为从库，则配置如下：
clusterEnabled=true

#如果有水平分库，变成多个域的情况，在这个项目通过逗号分隔，保持唯一即可
clusterDomain=cluster1
cluster1.master=db240
cluster1.slave=db241,db242

#下面此项可以不用配置，默认采用轮询模式
cluster1.slave.weight=4:3   

如果只有一个域的情况则可以简化为：
clusterEnabled=true
master=db240
slave=db241,db242

#下面此项可以不用配置，默认采用轮询模式，如果双主则为master.weight
slave.weight=4:3


1.1.4	缓存文件配置
Memcache配置文件（memcache-config.xml）

<?xml version="1.0" encoding="UTF-8"?>
<jftone>
    <poolSize>5</poolSize>
    <timeout>1</timeout>
    <hosts>	
	<host>
  		<ip>localhost</ip>
  		<port>11211</port>
  		<weight>1</weight>
  	</host>
   </hosts>	
</jftone>


Redis配置文件（redis-config.xml）

<?xml version="1.0" encoding="UTF-8"?>
<jftone>
<!-- 最大活动的对象个数 -->
    <maxTotal>500</maxTotal>
<!-- 对象最大空闲时间 -->
    <maxIdle>100</maxIdle>
<!-- 获取对象时最大等待时间 -->
    <minIdle>10</minIdle>	
<!-- 等待时间 -->
    <maxWait>3</maxWait>
    <hosts>
	<host>
  		<ip>localhost</ip>
  		<port>6379</port>
  		<auth>passwd</auth>
  		<weight>1</weight>
  	</host>
    </hosts>
</jftone>



如果有多个缓存，可以配置多个host，其中weight为各个缓存服务所占权重
1.1.5	SQL文件配置

<?xml version="1.0" encoding="UTF-8"?>
<!-- 
dataType可选类型:string,short,int,long,float,double,date,time,datetime,boolean,decimal
-->
<sqlMap>
  	<!-- table user start -->	
  	<statement id="queryUser" desc="查询会员用户信息" parse="true">
  		<![CDATA[ select id, user_no, user_name from user where remove_tag = 0 <!- and user_no = #USER_NO#-> <!- and user_name like #USER_NAME#->]]>
  	</statement>	
  	<statement id="delUser"  desc="删除会员信息">
  		<![CDATA[ update user set remove_tag = 1 where id=#USER_ID:int#]]>
  	</statement>	
  	<!-- table user end -->	
  		
  	<!-- table merchant start -->	
  	<statement id="queryMerchant" desc="查询商家信息">
  		<![CDATA[ select id, mct_no, user_id from merchant where mct_no=#MCT_NO# and mct_name like #MCT_NAME#]]>
  	</statement>
  	<!-- table merchant end -->
</sqlMap>

配置文件以sqlMap为根节点，sql语句为根节点下statement，一个statement表示一个可执行的sql查询，更新，删除，或execute语句

注意事项：
1)Statement id属性为sql语句唯一标识，请确保唯一，desc属性为sql描述，非必填属性，建议配置，说明sql用途
2)针对同一个表操作的所有sql语句建议放在<!-- table user start -->和<!-- table user end -->注释范围内，这样方便查找和重复利用
3)查询条件参数以#包围，变量名参数建议使用大写字符
如：#USER_NO:string# 标识接口传送进来一个名为USER _NO的变量，冒号后面string 表示该变量类型，默认情况下可以不用配置，直接使用#USER_NO#即可，如果为时间类型，建议按照【date,time,datetime】进行设置，如果不配置，则默认为datetime，其他类型还包括：
[string,short,int,long,float,double,date,time,datetime,boolean,decimal]
其他后续根据要求扩展
4)	如果某个参数为非必填参数，会根据业务场景需要，有可能设置，有可能不设置，则该变量对应的sql片段之间用  <!-   ->  包围。检测sql配置是否正确原则是，去掉<!-  ->之间sql片段，该sql语句仍然为一条可执行sql语句
5)	带有<!-  ->标记符的sql语句，需要增加属性parse=”true”，不然解析会出问题

1.1.6	国际化文件配置
国际化以web.properties文件中指定文件加语言和国家简写拼接的名称命名文件：
 
例如：
web.properties中配置：i18nResource=i18n.properties
那么在中文环境设置的服务器上，框架应用默认查找 i18n_zh_CN.properties
如果找不到则查找i18n.properties
1.2	功能说明
1.2.1	JFToneFilter配置

<filter>
    <filter-name>JFToneFilter</filter-name>
    <filter-class>org.jftone.action.JFToneFilter</filter-class>
    <init-param>
		<param-name>config</param-name>
		<param-value>com.xxx.sample.config.BaseConfig</param-value>
    </init-param>
</filter>

BaseConfig类必须继承框架抽象类：
com.lezu.jftone.config.AppConfig

并实现其中两个方法：loadRoute ，loadInterceptor
返回值	方法
void	loadRoute(Route route)
加载action路由配置数据，例如：
route.add("/loginAction", LoginAction.class)  #也可以直接在action层的类上面增加@Controller 注解，制定路由映射

如果路由配置比较多，创建多个
继承框架com.lezu.jftone.config.Route
的类，并覆盖其中config方法
通过route.add加入进来。

系统路由映射比较多的情况下，可以根据功能模块划分，创建多个实现Route的类，以避免loadRoute方法过于庞大
List<ActionInterceptor>	loadInterceptor()
Action拦截器，可以配置多个，也可以不设置，每个ActionInterceptor必须实现三个方法：
before：action方法执行前调用
after：action方法执行后调用
throwable：action方法抛错调用


1.2.2	应用启动侦听
web.properties配置中listenService配置类，必须AppListener接口
该实现类，主要完成随应用启动过程需要加载的初始化装载，相关数据缓存，服务启动，应用环境自检等。
返回值	方法
void	load()
应用启动加载时候调用，相关操作可以在放在这个方法内
void	destroy()
应用注销情况下调用，

此侦听为可选配置项，如果应用不需要有任何加载或注销的业务层面要求，可以不配置

1.2.3	Action层
Action层相关类的映射在JFToneFilter的config类中配置，框架会根据请求，找到匹配的配置，并进行相关action类实例的初始化，根据配置url模式，找到对应的方法并调用。

Action映射规则如下：
如com.xxx.jftone.config.AppConfig继承类中有一条路由配置：
route.add("/login", LoginAction.class)
LoginAction中存在访问名为login
则访问uri： /login/login
此外action还有一个默认方法execute，对应访问uri：/login

第二种情况如下：
jftone.properties配置中如urlPartern=do
则还支持uri:  /login.do?method=login
如urlPartern=action
则uri：/login.action?method=login
此时，两种访问路径都支持，如果没有配置项urlPartern，则仅支持第一种伪静态访问路径

Action开发规范：
必须继承org.jftone.action.ActionSupport
所有对外开放方法必须设置为public，且为无参非静态方法，原则上统一抛出异常ActionException
getRequest()  获取 HttpServletRequest对象
getResponse()  获取 HttpServletResponse对象
getData()   返回IData接口，内部包含了通过request.getParamter获取的所有请求参数，实际开发，请直接使用该方法获取参数，不需要从getRequest().getParamter(“参数名”)拿参数值

对于简单的几个参数，action支持setter，getter模式获取，如action中有配置setter，则框架会判断当前是否存在当前参数名的setter，并自动注入参数值。考虑到从web页面提交过来的数据校验，建议少使用或不使用这种方式

涉及简单数据库查询或更新（一般只有一次数据库读写），可以通过
BaseService service = BeanContext.getBean(BaseService.class)

或者Action增加注解
@Controller(mapping = "/passport")
那么设计到的依赖服务或其他资源可以通过

@Autowired
private PassportService service;

直接引入 PassportService， 而不需要PassportService service = BeanContext.getBean(PassportService.class)

直接进行操作，如果业务逻辑比较复杂，或涉及多次数据库表操作，则必须实现对应业务模块的定制Service，完成相关业务层面的处理

send(String str,  String contentType) 
制定向页面响应字符串，可以是text，json，html等
render(String pageFile) 显示Freemarker模板，模板相关参数设值，请通过：
putRenderInfo(String key, Object value)   设置单个freemarker模板变量
setRenderData(IData data)  设置一个IData类型的模板变量
forward(String actionUrl) 页面转发
redirect(String actionUrl)  url重定向


1.2.4	Service层
 Service为业务逻辑处理模块，框架默认已经实现一个Service类[BaseService]，封装了底层Dao访问相关操作，提供给Action层调用（简单逻辑处理）：
BaseService service = BeanContext.getBean(BaseService.class)

通过BeanContext拿到Service实例。
Service只是提供一些简单的访问操作，如果需要实现更加复杂的业务逻辑处理，开发人员需要创建自己的Service，步骤如下：
1、	设置Service注解

@Service
public class AdminService

2、设置Service的Dao对象
1)	在只有一个数据源的情况下：

	@DataSource
	private Dao dao;
	public void setDao(Dao dao) {
		this.dao = dao;
	}
或者

	@Autowired
	private Dao dao;

2)	在有多个数据源情况下

	@DataSource("db1")
	private Dao baseDao;
	
	@DataSource("db2")
	private Dao userDao;
	
	public void setBaseDao(Dao baseDao) {
		this.baseDao = baseDao;
	}
	
	public void setUserDao(Dao userDao) {
		this.userDao = userDao;
	}

其中db1，db2为前面jdbc.properties文件中配置中datasource指定的名称
这样，在操作不同Dao情况下，就会针对不同数据库进行查询或更新操作

3)	在有数据读写分离情况下
形式如同上面第二点，只是将DataSource注解的值设定为jdbc.properties中的clusterDomain值


在有其他Service需要引入（依赖注入）情况下

	@Resource
	private BrandService brandService;
	public void setBrandService(BrandService brandService) {
		this.brandService = brandService;
	}
或者：

	@Autowired
	private BrandService brandService;
	public void setBrandService(BrandService brandService) {
		this.brandService = brandService;
	}



如创建了一个AdminService
则通过：
AdminService service = ServiceContext.getService(AdminService.class)
拿到实例。

开发规范：
原则上Service命名以Service结尾
Service内部处理，统一以ServiceException对外抛出异常
Service 如果继承BaseService，内部不需要创建 setDao 之类setter，框架会自动注入Dao，其他情况，需要指定好Dao，框架才会他初始化其他Dao，并获取实例接
Service内部涉及超过多张数据库表更新，或读写分离数据库，需要走写库时候，需要启用事务。
	
直接在方法上面加入注解：
@Transactional
public int save(SysAdmin admin) throws ServiceException
   如果两者同时存在情况下，以注解为准
 
如果方法内部涉及到多个数据库的更新事务，则需要在注解Transactional 指定是启用哪个数据源注解，可以支持多数据源事务
在读写分离情况下，尤其要注意写库操作一定要记得启用事务，否则无法路由到主库更新数据

1.2.5	工具类
框架jar包：org.jftone.util 路径下有常用工具类：
工具类	说明
ClassUtil	根据指定包从文件或jar包中遍历查询所有class类
DateUtil	时间获取或格式化等，应用中建议获取时间，建议全部从这个工具类取，后期会进行封装，保证分布式时间一致问题
EncryptUtil	Md5，sha1  base64等加密工具
FileUtil	文件读写
OKHttpUtil	http连接工具类，包括同步，非阻塞，HTTPS等
ImageUtil	图片文件裁剪，缩放工具类
Ipv4Util	获取ip地址，同时支持ip地址字符到长整形相互转换
JsonUtil	对象转json工具类
MathUtil	数字格式化，转换，仍在扩展补充
ObjectUtil	对象复制，反射设置，取值等
Page	分页对象
StringUtil	字符串工具处理
VerifyCodeUtil	验证码工具类
CompressUtil	字节流压缩及解压
SerializeUtil	Java序列化及反序列化
。。。。。	。。。。。。。。。。。

1.3	Model配置及生成
框架所有实体类跟数据库表是一一对应，表与表之间的关联，在实体类之间不建立关系，所有逻辑及关联处理，由程序层面来控制。
一个实体仅仅只映射数据表字段。
实体类必须继承Model
实体类命名规则：去除表名下横杠以大写开头驼峰连接组成实体类名，例如：表prod_detail，对应实体类名为：ProdDetail；字段命名按照同样规则，并以小写开头，如prod_detail表字段 prod_name则命名为：prodName。

表名和字段名需要设置相关注解。具体如下：


@Entity
@Table(name="sys_admin")
public class SysAdmin extends Model {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Integer id;

	@Column(name="login_time", columnDefinition="datetime" )
	private Date loginTime;

	@Column(name="login_ip")
	private String loginIp;

	@Column(name="user_name")
	private String userName;

	@Column(name="locked")
	private Short locked;

	@Column(name="password")
	private String password;

	@Column(name="mobile")
	private String mobile;

	public Integer getter()
	public void setter(Integer id) 
    。。。。。。。。。。。。
}

    
主键字段需要设置@Id，@GeneratedValue并制定主键类型。
其他字段只需要设置@Column 并指定表字段名字。
对于时间类型字段，需要设置columnDefinition指定是datetime，date，还是time

以上就是这个项目的使用方法简介
