# WebSocket中间服务使用方法

## 增订修改说明

- 2018-5-15 修改说明
  1. 增加`点对点`、`组内广播`、`全局广播`三种广播方式
  2. 修改消息发送API格式，需要增加`clientId`与`groupId`确定消息扩散范围
  3. 客户端自动启动时，可以自行加入clientId与groupId属性用于注册
  4. web客户端在连接成功后需要传递格式化的消息信息以进行注册
  5. 模块功能正式改名为`neptune-websocket-starter`，增加了自动配置，无需再手动扫描
  6. 大幅修订`使用说明.md`文档
  7. 增加客户端与服务端使用的实例项目



## 引入：

将项目引入到pom.xml中

```xml
<dependency>
    <groupId>neptune</groupId>
    <artifactId>neptune-websocket-starter</artifactId>
    <version>版本号</version>
</dependency>
```

## 配置文件：

在`application.yml`中配置以下属性：

```yaml
websocket:
  #配置Websocket服务器属性
  server:
  	#WebSocket服务器IP地址
    ip: 10.1.40.155
    #WebSocket服务器端口号，默认值为8990
    port: 8990
    #WebSocket请求连接前缀，默认值为/ws
    urlPrefix: /ws
    #WebSocket服务器启动时，是否自带启动一个客户端连接，默认值为false
    runWithClient: false
  client:
  	#WebSocket客户端是否随系统启动时自动连接服务器，默认值为false
    autoStart: false
    #当autoStart值为true时启用，可以根据此名称在server端进行注册，默认值为空
    clientId: #####
    #当autoStart值为true时启用，可以根据此名称在server端进行注册，默认值为空
    groupId: ######
```

## 系统启动

在使用Websocket请求的服务中增加以下注解：

```java
@SpringBootApplication
//用于作为服务端时使用，不是服务端时无需添加此注解
@EnableWebSocketServer
//用于作为客户端时使用，不是客户端时无需添加此注解
@EnableWebSocketClient
public class XXXXXXApplication {
    public static void main( String[] args ) {
        SpringApplication.run(XXXXXXApplication.class,args);
    }
}
```

> 其中`@EnableWebSocketServer`注解将会把本服务作为Websocket服务器模式启动。
>
> `@EnableWebSocketClient`注解将会把本服务作为WebSocket客户端使用
>
> 以上两个注解需要与`application.yml`中的相关配置配合服用
>

## 作为服务端使用

### 启动方式：

```java
@SpringBootApplication
@EnableWebSocketServer
public class WebSocketServerApplication {
    public static void main( String[] args ) {
        SpringApplication.run(XXXXXXApplication.class,args);
    }
}
```

### 说明

1. 需要在`application.yml`中设定`websocket.server`中的`ip`，`port`，`urlPrefix`等字段属性，系统将根据以上属性对外公布Server服务器地址。
2. `websocket.server.runWithClient`属性决定是否在服务器启动时，自动启动一个客户端连接服务器。若此值为true，则会使用`websocket.client`中的属性，具体功能见`客户端使用部分`
3. 添加`@EnableWebSocketServer`注解后，服务启动后将自动变为WebSocket服务器。

## 作为客户端使用

### 启动方式

```java
@SpringBootApplication
@EnableWebSocketClient
public class WebSocketClientApplication {
    public static void main( String[] args ) {
        SpringApplication.run(XXXXXXApplication.class,args);
   }
}
```

### 说明

1. `websocket.client.autoStart`设置为true时，WebSocketClientApplication启动后将会自动产生一个长连接与server服务器建立联系。
2. `websocket.client`中的`clientId`与`groupId`值在autoStarter为true时，用于在server端进行注册。
3. 当`websocket.client.autoStart`为true时，可以通过直接

### 基本使用

> 客户端的使用方式均为调用`NettyWebSocketClient`实例的方法。

#### 基础

1. 当`websocket.client.autoStart`为true时，可以通过直接注入的方式使用已经建立好的客户端连接推送消息。

   ```java
   //注入连接
   @Autowired
   private NettyWebSocketClient nettyWebSocketClient;

   //在方法中：
   public void sendMessage(){
       nettyWebSocketClient.sendMessage("你要发送的消息");
   }
   ```



2. 当`websocket.client.autoStart`为false时，需要先建立连接再执行发送。一样可以通过注入方式调用

在springboot的容器类中通过添加以下代码进行使用：

```java
//注入连接
@Autowired
private NettyWebSocketClient nettyWebSocketClient;

//在方法中：
public void sendMessage(){
    //先创建一个客户端
	nettyWebSocketClient.createClient();
    //然后再发送消息
    nettyWebSocketClient.sendMessage("你要发送的消息");
    //注意使用完毕后要将该链接关闭
    nettyWebSocketClient.shutdown
}
```

#### 消息格式

1. 前端JS消息格式如下：

   ```json
   {
       //消息内容随意
   	"content": "创建连接",
       //产生消息时间，long类型
   	"sendTime": 1557906140981,
       //声明此消息为注册信息
   	"spreadType": "INITIAL",
       //组名
   	"groupId": "group0003",
       //客户名
   	"clientId": "client0001",
       //发送消息客户名
   	"sendClientId": "client0001"
   }
   ```



2. 所有发送消息将按照一下消息格式进行发送

   ```java
   public class MessageModel {
       //消息正文
       private String content;
       //消息发送时间
       private long sendTime;
       //消息广播方式
       private SpreadType spreadType;

       //组别
       private String groupId;
       //客户端识别ID
       private String clientId;

       //发送端ID
       private String sendClientId;
   }
   ```

#### 客户端注册

1. 无注册

   > 1. 无注册的连接将只能接受到广播消息
   >
   > 2. 无注册连接可通过以下方式实现：
   >
   >    1. 设置`websocket.client.autoStart`为true，且groupId与clientId`均`设置为空或不设置值
   >
   >    2. 创建连接时使用空参方法创建：
   >
   >       ```java
   >       nettyWebSocketClient.createClient()
   >       ```

2. 有注册

   > 1. 注册分为`组注册`与`客户注册`。分别对应属性`groupId`与`clientId`
   >
   > 2. 创建连接时groupId与clientId任一不为空即可在Server端进行注册。
   >
   >    ```java
   >    //两个参数均可为空。如果都为空时，相当于无注册连接
   >    createWebSocketClient(String groupId,String clientId)
   >    ```

#### 发送消息

1. 注册消息

   > 用于创建连接后（包括无注册连接与有注册连接），重新在服务端进行注册。
   >
   > 需要注意的是，新的注册信息将覆盖旧的注册信息（如果是同一连接的话）。
   >
   > ```java
   > public void sendRegister(String groupId,String clientId)
   > ```

2. 发送广播信息

   > 当不携带任何gourpId与clientId时，将发送广播消息，所有与server建立连接的客户均会收到此消息
   >
   > ```java
   > public void sendMessage(String message)
   > ```

3. 发送组内广播信息

   > 当仅携带groupId时，将发送组内广播消息。所有使用groupId注册的连接均会受到此消息
   >
   > ```java
   > public void sendMessage(String message,String groupId)
   > ```

4. 发送点对点信息

   > 当groupId与clientId全部携带时，将会发送点对点消息。组名与客户名全部符合时才会受到此消息
   >
   > ```java
   > public void sendMessage(String message,String groupId,String clientId)
   > ```

5. 自定义消息格式

   > 客户单可自行按照MessageModel消息格式进行消息发送。详细方法见内部API
   >
   > ```java
   > public void sendMessage(MessageModel messageModel)
   > ```

### 客户端关闭

当不再使用推送服务或很长一段时间间隔内不需要再启动推送服务时，可使用如下方法终止客户端连接

```java
nettyWebSocketClient.shutdown()；
```

