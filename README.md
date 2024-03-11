# 伙伴匹配系统
伙伴匹配系统后端系统，帮助大家找到志同道合的小伙伴，一起组队干大事！！！
## 需求分析
1.用户可以自己添加标签(兴趣方向：java/c++)，工作/学习<br/>
2.主动搜索：用户可以根据标签搜索需要的伙伴(redis提高效率)<br/>
3.组队功能：<br/>
    &nbsp; &nbsp;a.创建队伍<br/>
&nbsp; &nbsp;b.加入队伍<br/>
&nbsp; &nbsp;c.寻找队伍<br/>
&nbsp; &nbsp;d.邀请别人加入队伍<br/>
4.自动推荐功能，（匹配算法）<br/>
5.完成基本的增删改查功能<br/>
6.usercenter:使用已经创建好的用户中心，其中需要用户角色等相关属性<br/>

## 后端技术栈
SpringBoot+MybatisPlus+Mysql+Redis+Swagger+Knife4j（接口文档）<br/>

## 启动流程
&nbsp; &nbsp;1.修改mysql和redis配置
```yaml
spring:
  application:
    name: user-center
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:13306/universe
    username: ###
    password: ######
  #设置session过期时间
  session:
    timeout: 86400
    store-type: redis
  redis:
    database: 0
    host: localhost
    port: 6379
```
&nbsp; &nbsp;2.执行sql文件夹中文件建表以及插入模拟数据<br>
&nbsp; &nbsp;3.在本地8080端口启动，访问http://localhost:8080/api/doc.html#/home
打开接口测试文档<br>
<img src=".\images\接口文档.jpg">
## 数据库表重要设计
关于用户和标签的数据库表设计两种方案选型：<br/>
方案一：<br>
&nbsp;&nbsp;直接在用户属性上添加tags标签属性，需要保存为json格式（例如['java','男']），根据
标签查询用户时，可以在数据库上进行模糊查询，或者将数据库信息读取到内存中
查询。<br>
&nbsp;&nbsp;优点<br>
&nbsp;&nbsp;&nbsp;&nbsp;实现简单,将标签看作是用户的固有属性，不用进行关联查询<br>
&nbsp;&nbsp;缺点<br>
&nbsp;&nbsp;&nbsp;&nbsp;进行模糊查询可能效率不高，将数据读取到内存中，可能因数数据量过大导致内存溢出
，实际情况根据实测为准<br>
方案二：<br>
&nbsp;&nbsp;建立用户和标签的关系表<br>
&nbsp;&nbsp;优点<br>
&nbsp;&nbsp;&nbsp;&nbsp;查询灵活，可以正查，反查<br>
&nbsp;&nbsp;缺点<br>
&nbsp;&nbsp;&nbsp;&nbsp;需要进行关联查询，可能影响查询效率<br>

这里我选择第一种方式，并考虑使用redis作为缓存提高查询效率

    