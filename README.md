# AIChatJava

#### 介绍
java对接国内Kimi大模型
* 在本项目中，我们使用Java的Spring Boot框架和Vue框架实现了一个简化版的AI主播功能。用户可以通过网页界面提出问题，系统将通过语音进行回复。该项目集成了语音合成功能，提供了一种交互式的用户体验。

#### 软件架构说明
* 整个系统主要分为前端和后端两部分：
* 前端：使用Vue框架实现，负责用户输入和展示语音回复。
* 后端：使用Spring Boot框架实现，负责处理用户请求，调用AI服务生成回答，并将回答转换为语音。

#### 主要技术和工具
* 前端：Vue.js, HTML, CSS, JavaScript
* 后端：Spring Boot, OkHttp, Gson, Kimi API
* 依赖管理：Maven

#### 项目目标
1. 实现一个基于Web的用户界面，用户可以在该界面上输入问题。
2. 将用户的问题发送到服务器端进行处理。
3. 服务器端调用AI服务生成回答。
4. 将AI回答通过语音合成技术转换为语音。
5. 在网页上播放生成的语音回答。

#### 安装教程

* 项目中使用的工具和框架安装请参考官方文献

#### 使用说明

1.  将项目fork到本地
2.  进入项目的target文件夹
3.  在终端运行`java -jar gettingstarted-0.0.1-SNAPSHOT.jar`

#### 实现过程
##### 1. 后端实现
* 用java编写调用Kimi API的客户端程序
* 编写控制器，接收来自端口/api/chat的请求，调用api并生成文本回复返回前端
* 编写 Spring Boot 应用程序入口点。使用 @SpringBootApplication 注解来启用自动配置和组件扫描，并通过 SpringApplication.run 方法启动 Spring 应用。
##### 2. 前端实现
* 创建Vue项目：

```
vue create ai-anchor-main
```
* 编写主页面，聊天框，用户输入框等，将用户请求发送到/api/chat端口，并接收来自控制器的回复
* 使用SpeechSynthesisUtterance类将接收的文本转换成语音
* 将编写好的vue项目打包成静态文件

```
npm run build //静态文件通常生成在./dist目录下
```
##### 3. 生成可执行文件
* 将打包后的vue静态文件（dist目录下的所有文件）复制到 src/main/resources/static 目录下
*进入项目根目录，运行
```
mvn clean package
java -jar ./target/gettingstarted-0.0.1-SNAPSHOT.jar
```
* 在浏览器打开http://localhost:8080/index.html即可看到前端页面


#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request
