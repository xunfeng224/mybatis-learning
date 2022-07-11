# MyBatis学习笔记

## 一、简介

### 1.什么是 MyBatis？

1. MyBatis 是一款优秀的持久层框架，它支持自定义 SQL、存储过程以及高级映射。
2. MyBatis 免除了几乎所有的 JDBC 代码以及设置参数和获取结果集的工作。
3. MyBatis 可以通过简单的 XML 或注解来配置和映射原始类型、接口和 Java POJO（Plain Old Java Objects，普通老式 Java 对象）为数据库中的记录。

**MyBatis中文网**：[MyBatis中文网](https://mybatis.net.cn/index.html)

**Maven Repository**:[Maven Repository: Search/Browse/Explore (mvnrepository.com)](https://mvnrepository.com/)

### 2.MyBatis特点

1. 简单易学：本身就很小且简单。没有任何第三方依赖，最简单安装只要两个jar文件+配置几个sql映射文件。易于学习，易于使用。通过文档和源代码，可以比较完全的掌握它的设计思路和实现。
2. 灵活：mybatis不会对应用程序或者数据库的现有设计强加任何影响。 sql写在xml里，便于统一管理和优化。通过sql语句可以满足操作数据库的所有需求。
3. 解除sql与程序代码的耦合：通过提供DAO层，将业务逻辑和数据访问逻辑分离，使系统的设计更清晰，更易维护，更易单元测试。sql和代码的分离，提高了可维护性。
4. 提供映射标签，支持对象与数据库的orm字段关系映射。
5. 提供对象关系映射标签，支持对象关系组建维护。
6. 提供xml标签，支持编写动态sql。 

## 二、Quick Start

### 1.项目整体结构

![image-20220711095107244](https://s2.loli.net/2022/07/11/eiYxMgN6w7skDuc.png)

### 2.mysql数据库

新建数据库mybatis 新建表user

user表sql脚本如下

```sql
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int NOT NULL,
  `name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pwd` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '张三', '123456');
INSERT INTO `user` VALUES (2, '张三2', '123456');
INSERT INTO `user` VALUES (3, '张三3', '123456');
INSERT INTO `user` VALUES (4, '张三4', '123456');
INSERT INTO `user` VALUES (5, '张三5', '123456');

SET FOREIGN_KEY_CHECKS = 1;
```



### 3.安装MyBatis

> 要使用 MyBatis， 只需将 [mybatis-x.x.x.jar](https://github.com/mybatis/mybatis-3/releases) 文件置于类路径（classpath）中即可。
>
> 如果使用 Maven 来构建项目，则需将下面的依赖代码置于 pom.xml 文件中：
>
> ```
> <dependency>
>   <groupId>org.mybatis</groupId>
>   <artifactId>mybatis</artifactId>
>   <version>x.x.x</version>
> </dependency>
> ```

新建maven项目study-mybatis并配置pom.xml导入相关依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.xfeng</groupId>
    <artifactId>study-mybatis</artifactId>
    <packaging>pom</packaging>
    <version>1.0-SNAPSHOT</version>
    <modules>
        <module>mybatis-01</module>
        <module>mybatis-02</module>
        <module>mybatis-03</module>
    </modules>
    <dependencies>
        <!--        MyBatis jar包导入-->
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.5.10</version>
        </dependency>
        <!--     mysql连接器 实现了JDBC，为使用java开发的程序提供连接，方便java程序操作数据库 -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.29</version>
        </dependency>
        <!--        单元测试-->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
        </dependency>
    </dependencies>

    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
    </properties>

<!--maven资源过滤-->
    <build>
        <resources>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.properties</include>
                    <include>**/*.xml</include>
                </includes>
                <filtering>false</filtering>
            </resource>
            <resource>
                <directory>src/main/resources</directory>
                <includes>
                    <include>**/*.properties</include>
                    <include>**/*.xml</include>
                </includes>
                <filtering>false</filtering>
            </resource>
        </resources>
    </build>
</project>
```

### 4.从 XML 中构建 SqlSessionFactory

1. 在study-mybatis下新建子maven项目mybatis-01,并在resources目录下 新建mybatis-config.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<!--核心配置文件-->
<configuration>
    <environments default="development">
        <environment id="development">
            <!--            默认使用JDBC事务管理-->
            <!--            在 MyBatis 中有两种类型的事务管理器（也就是 type="[JDBC|MANAGED]"）：
            如果你正在使用 Spring + MyBatis，则没有必要配置事务管理器，因为 Spring 模块会使用自带的管理器来覆盖前面的配置。-->
            <transactionManager type="JDBC"/>
            <!--            数据库连接-->
            <!--            有三种内建的数据源类型（也就是 type="[UNPOOLED|POOLED|JNDI]"）-->
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
                <property name="url"
                          value="jdbc:mysql://localhost:3306/mybatis?useSSL=true&amp;useUnicode=true&amp;characterEncoding=UTF-8&amp;serverTimezone=UTC"/>
                <property name="username" value="root"/>
                <property name="password" value="root"/>
            </dataSource>
        </environment>
    </environments>
    <!--    每一个Mapper.xml都需要在Mybatis核心配置文件中注册！-->
    <mappers>
        <mapper resource="com/xfeng/dao/UserMapper.xml"/>
    </mappers>
</configuration>
```



官方中文文档：

> 每个基于 MyBatis 的应用都是以一个 SqlSessionFactory 的实例为核心的。SqlSessionFactory 的实例可以通过 SqlSessionFactoryBuilder 获得。而 SqlSessionFactoryBuilder 则可以从 XML 配置文件或一个预先配置的 Configuration 实例来构建出 SqlSessionFactory 实例。
>
> 从 XML 文件中构建 SqlSessionFactory 的实例非常简单，建议使用类路径下的资源文件进行配置。 但也可以使用任意的输入流（InputStream）实例，比如用文件路径字符串或 file:// URL 构造的输入流。MyBatis 包含一个名叫 Resources 的工具类，它包含一些实用方法，使得从类路径或其它位置加载资源文件更加容易。
>
> ```
> String resource = "org/mybatis/example/mybatis-config.xml";
> InputStream inputStream = Resources.getResourceAsStream(resource);
> SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
> ```



在项目路径下新建utils包,新建MyBatisUtils工具类,使用工具类获取sqlSessionFactory

```java
package com.xfeng.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import java.io.IOException;
import java.io.InputStream;

/**
 * @program: study-mybatis
 * @description:
 * @author: xiongfeng
 * @create: 2022-07-08 21:51
 **/
public class MyBatisUtils {
    private static SqlSessionFactory sqlSessionFactory;

    static {
        String resource = "mybatis-config.xml";
        try {
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static SqlSession getSqlSeesion() {
        return sqlSessionFactory.openSession();
    }

}
```



### 5.快速启动

1. 新建dao包和pojo包

   ![image-20220711095828812](https://s2.loli.net/2022/07/11/9PEzCrq3yVpAaYU.png)

2. 新建UserMapper、UserMapper.xml、User类

   UserMapper.java

   ```java
   package com.xfeng.dao;
   import com.xfeng.pojo.User;
   import java.util.List;
   /**
    * @program: study-mybatis
    * @description:
    * @author: xiongfeng
    * @create: 2022-07-08 23:05
    **/
   public interface UserMapper {
       List<User> getUserList();
   
       User getById(int id);
   
       int insertUser(User user);
   
       int updateUser(User user);
   
       int deleteUser(int id);
   }
   ```

   UserMapper.xml

   ```java
   <?xml version="1.0" encoding="UTF-8" ?>
   <!DOCTYPE mapper
           PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
           "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
   <!--namespace 绑定一个对应的mapper接口-->
   <mapper namespace="com.xfeng.dao.UserMapper">
       <!--    select查询-->
       <select id="getUserList" resultType="com.xfeng.pojo.User">
           select * from mybatis.user;
       </select>
       <select id="getById" resultType="com.xfeng.pojo.User" parameterType="int">
           select * from mybatis.user where id = #{id};
       </select>
       <insert id="insertUser"  parameterType="com.xfeng.pojo.User" >
           insert into mybatis.user (id,name,pwd) values(#{id},#{name},#{pwd});
       </insert>
       <update id="updateUser" parameterType="com.xfeng.pojo.User">
           update mybatis.user set name=#{name},pwd=#{pwd} where id =#{id};
       </update>
       <delete id="deleteUser" parameterType="int">
           delete from mybatis.user where id = #{id};
       </delete>
   </mapper>
   ```

   User.java

   ```java
   package com.xfeng.pojo;
   /**
    * @program: study-mybatis
    * @description:
    * @author: xiongfeng
    * @create: 2022-07-08 22:02
    **/
   public class User {
       private int id;
       private String name;
       private String pwd;
   
       public User() {
       }
   
       public User(int id, String name, String pwd) {
           this.id = id;
           this.name = name;
           this.pwd = pwd;
       }
   
       public int getId() {
           return id;
       }
   
       public void setId(int id) {
           this.id = id;
       }
   
       public String getName() {
           return name;
       }
   
       public void setName(String name) {
           this.name = name;
       }
   
       public String getPwd() {
           return pwd;
       }
   
       public void setPwd(String pwd) {
           this.pwd = pwd;
       }
   
       @Override
       public String toString() {
           return "User{" +
                   "id=" + id +
                   ", name='" + name + '\'' +
                   ", pwd='" + pwd + '\'' +
                   '}';
       }
   }
   ```

   3.Junit启动

   ```java
   package dao;
   import com.xfeng.dao.UserMapper;
   import com.xfeng.pojo.User;
   import com.xfeng.utils.MyBatisUtils;
   import org.apache.ibatis.session.SqlSession;
   import org.junit.Test;
   
   
   public class UserMapperTest {
       @Test
       public void test() {
           //获取SqlSession对象
           SqlSession sqlSeesion = MyBatisUtils.getSqlSeesion();
           //方式一：getMapper
           UserMapper userMapper = sqlSeesion.getMapper(UserMapper.class);
   //        List<User> userList = userMapper.getUserList();
   //        for(User user:userList){
   //            System.out.println(user);
   //        }
   
           User byId = userMapper.getById(1);
           System.out.println(byId);
           sqlSeesion.close();
       }
       @Test
       public void insertUser(){
           SqlSession sqlSeesion = MyBatisUtils.getSqlSeesion();
           UserMapper userMapper = sqlSeesion.getMapper(UserMapper.class);
   //        int res = userMapper.insertUser(new User(6, "xiaoming6", "1"));
   //        if(res>0){
   //            System.out.println("插入成功");
   //        }
   
   
   
           int res2 = userMapper.updateUser(new User(6, "xiaoming6", "1"));
           System.out.println(res2);
   
           int i = userMapper.deleteUser(6);
           System.out.println(i);
           sqlSeesion.commit();
           sqlSeesion.close();
       }
   }
   ```


## 三、属性优化、类型别名、映射器

### 1.属性优化

**官方中文文档：**

> 这些属性可以在外部进行配置，并可以进行动态替换。你既可以在典型的 Java 属性文件中配置这些属性，也可以在 properties 元素的子元素中设置。例如：
>
> ```xml
> <properties resource="org/mybatis/example/config.properties">
>   <property name="username" value="dev_user"/>
>   <property name="password" value="F2Fa3!33TYyg"/>
> </properties>
> ```
>
> 设置好的属性可以在整个配置文件中用来替换需要动态配置的属性值。比如:
>
> ```
> <dataSource type="POOLED">
>   <property name="driver" value="${driver}"/>
>   <property name="url" value="${url}"/>
>   <property name="username" value="${username}"/>
>   <property name="password" value="${password}"/>
> </dataSource>
> ```
>
> 这个例子中的 username 和 password 将会由 properties 元素中设置的相应值来替换。 driver 和 url 属性将会由 config.properties 文件中对应的值来替换。这样就为配置提供了诸多灵活选择。
>
> 也可以在 SqlSessionFactoryBuilder.build() 方法中传入属性值。例如：
>
> ```
> SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, props);
> 
> // ... 或者 ...
> 
> SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, environment, props);
> ```

> 如果一个属性在不只一个地方进行了配置，那么，MyBatis 将按照下面的顺序来加载：
>
> - 首先读取在 properties 元素体内指定的属性。
> - 然后根据 properties 元素中的 resource 属性读取类路径下属性文件，或根据 url 属性指定的路径读取属性文件，并覆盖之前读取过的同名属性。
> - 最后读取作为方法参数传递的属性，并覆盖之前读取过的同名属性。
>
> 因此，通过方法参数传递的属性具有最高优先级，resource/url 属性中指定的配置文件次之，最低优先级的则是 properties 元素中指定的属性。



db.properties

```properties
driver=com.mysql.cj.jdbc.Driver
url=jdbc:mysql://localhost:3306/mybatis?useSSL=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC
username=root
password=root
```

mybatis-cofing.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<!--核心配置文件-->
<configuration>
    <properties resource="db.properties"/>

    <!--    类型别名（typeAliases）-->
    <!--    类型别名可为 Java 类型设置一个缩写名字。 它仅用于 XML 配置，意在降低冗余的全限定类名书写。-->
    <typeAliases>
        <!--      当这样配置时，User 可以用在任何使用 com.xfeng.pojo.User 的地方。  -->
        <typeAlias type="com.xfeng.pojo.User" alias="User"/>
        <!--        也可以指定一个包名，MyBatis 会在包名下面搜索需要的 Java Bean
        每一个在包 domain.blog 中的 Java Bean，在没有注解的情况下，会使用 Bean 的首字母小写的非限定类名来作为它的别名。
        比如 domain.blog.Author 的别名为 author；若有注解，则别名为其注解值。 @Alias("user11")
        -->
        <package name="com.xfeng.pojo"/>
    </typeAliases>

    <environments default="development">
        <environment id="development">
            <!--            默认使用JDBC事务管理-->
            <!--            在 MyBatis 中有两种类型的事务管理器（也就是 type="[JDBC|MANAGED]"）：
            如果你正在使用 Spring + MyBatis，则没有必要配置事务管理器，因为 Spring 模块会使用自带的管理器来覆盖前面的配置。-->
            <transactionManager type="JDBC"/>
            <!--            数据库连接-->
            <!--            有三种内建的数据源类型（也就是 type="[UNPOOLED|POOLED|JNDI]"）-->
            <dataSource type="POOLED">
                <property name="driver" value="${driver}"/>
                <property name="url" value="${url}"/>
                <property name="username" value="${username}"/>
                <property name="password" value="${password}"/>
            </dataSource>
        </environment>
    </environments>


    <!--    每一个Mapper.xml都需要在Mybatis核心配置文件中注册！-->
    <mappers>
        <!-- 使用相对于类路径的资源引用 推荐使用 -->
        <mapper resource="com/xfeng/dao/UserMapper.xml"/>
        <!--        注意：1.接口和Mapper文件必须同名 2.接口和Mapper文件必须在同一包下-->
        <!-- 使用映射器接口实现类的完全限定类名 -->
<!--        <mapper class="org.mybatis.builder.AuthorMapper"/>-->
<!--        <mapper class="org.mybatis.builder.BlogMapper"/>-->
<!--        <mapper class="org.mybatis.builder.PostMapper"/>-->
<!--        &lt;!&ndash; 将包内的映射器接口实现全部注册为映射器 &ndash;&gt;-->
<!--        <package name="org.mybatis.builder"/>-->
    </mappers>


</configuration>
```

### 2.类型别名

**官方中文文档：**

> 类型别名可为 Java 类型设置一个缩写名字。 它仅用于 XML 配置，意在降低冗余的全限定类名书写。例如：
>
> ```
> <typeAliases>
>   <typeAlias alias="Author" type="domain.blog.Author"/>
>   <typeAlias alias="Blog" type="domain.blog.Blog"/>
>   <typeAlias alias="Comment" type="domain.blog.Comment"/>
>   <typeAlias alias="Post" type="domain.blog.Post"/>
>   <typeAlias alias="Section" type="domain.blog.Section"/>
>   <typeAlias alias="Tag" type="domain.blog.Tag"/>
> </typeAliases>
> ```
>
> 当这样配置时，`Blog` 可以用在任何使用 `domain.blog.Blog` 的地方。
>
> 也可以指定一个包名，MyBatis 会在包名下面搜索需要的 Java Bean，比如：
>
> ```
> <typeAliases>
>   <package name="domain.blog"/>
> </typeAliases>
> ```
>
> 每一个在包 `domain.blog` 中的 Java Bean，在没有注解的情况下，会使用 Bean 的首字母小写的非限定类名来作为它的别名。 比如 `domain.blog.Author` 的别名为 `author`；若有注解，则别名为其注解值。见下面的例子：
>
> ```
> @Alias("author")
> public class Author {
>     ...
> }
> ```

### 3.映射器

> 既然 MyBatis 的行为已经由上述元素配置完了，我们现在就要来定义 SQL 映射语句了。 但首先，我们需要告诉 MyBatis 到哪里去找到这些语句。 在自动查找资源方面，Java 并没有提供一个很好的解决方案，所以最好的办法是直接告诉 MyBatis 到哪里去找映射文件。 你可以使用相对于类路径的资源引用，或完全限定资源定位符（包括 `file:///` 形式的 URL），或类名和包名等。例如：
>
> ```
> <!-- 使用相对于类路径的资源引用 -->
> <mappers>
> <mapper resource="org/mybatis/builder/AuthorMapper.xml"/>
> <mapper resource="org/mybatis/builder/BlogMapper.xml"/>
> <mapper resource="org/mybatis/builder/PostMapper.xml"/>
> </mappers>
> <!-- 使用完全限定资源定位符（URL） --> <!-- 不建议使用 -->
> <mappers>
> <mapper url="file:///var/mappers/AuthorMapper.xml"/>
> <mapper url="file:///var/mappers/BlogMapper.xml"/>
> <mapper url="file:///var/mappers/PostMapper.xml"/>
> </mappers>
> 
> <!--        注意下面两种：1.接口和Mapper文件必须同名 2.接口和Mapper文件必须在同一包下-->
> <!-- 使用映射器接口实现类的完全限定类名 -->
> <mappers>
> <mapper class="org.mybatis.builder.AuthorMapper"/>
> <mapper class="org.mybatis.builder.BlogMapper"/>
> <mapper class="org.mybatis.builder.PostMapper"/>
> </mappers>
> <!-- 将包内的映射器接口实现全部注册为映射器 -->
> <mappers>
> <package name="org.mybatis.builder"/>
> </mappers>
> ```

## 四、作用域（Scope）和生命周期

> 理解我们之前讨论过的不同作用域和生命周期类别是至关重要的，因为错误的使用会导致非常严重的并发问题。
>
> ------
>
> **提示** **对象生命周期和依赖注入框架**
>
> 依赖注入框架可以创建线程安全的、基于事务的 SqlSession 和映射器，并将它们直接注入到你的 bean 中，因此可以直接忽略它们的生命周期。 如果对如何通过依赖注入框架使用 MyBatis 感兴趣，可以研究一下 MyBatis-Spring 或 MyBatis-Guice 两个子项目。
>
> ------
>
> #### SqlSessionFactoryBuilder
>
> 这个类可以被实例化、使用和丢弃，一旦创建了 SqlSessionFactory，就不再需要它了。 因此 SqlSessionFactoryBuilder 实例的最佳作用域是方法作用域（也就是局部方法变量）。 你可以重用 SqlSessionFactoryBuilder 来创建多个 SqlSessionFactory 实例，但最好还是不要一直保留着它，以保证所有的 XML 解析资源可以被释放给更重要的事情。
>
> #### SqlSessionFactory
>
> SqlSessionFactory 一旦被创建就应该在应用的运行期间一直存在，没有任何理由丢弃它或重新创建另一个实例。 使用 SqlSessionFactory 的最佳实践是在应用运行期间不要重复创建多次，多次重建 SqlSessionFactory 被视为一种代码“坏习惯”。因此 SqlSessionFactory 的最佳作用域是应用作用域。 有很多方法可以做到，最简单的就是使用单例模式或者静态单例模式。
>
> #### SqlSession
>
> 每个线程都应该有它自己的 SqlSession 实例。SqlSession 的实例不是线程安全的，因此是不能被共享的，所以它的最佳的作用域是请求或方法作用域。 绝对不能将 SqlSession 实例的引用放在一个类的静态域，甚至一个类的实例变量也不行。 也绝不能将 SqlSession 实例的引用放在任何类型的托管作用域中，比如 Servlet 框架中的 HttpSession。 如果你现在正在使用一种 Web 框架，考虑将 SqlSession 放在一个和 HTTP 请求相似的作用域中。 换句话说，每次收到 HTTP 请求，就可以打开一个 SqlSession，返回一个响应后，就关闭它。 这个关闭操作很重要，为了确保每次都能执行关闭操作，你应该把这个关闭操作放到 finally 块中。 下面的示例就是一个确保 SqlSession 关闭的标准模式：
>
> ```
> try (SqlSession session = sqlSessionFactory.openSession()) {
>   // 你的应用逻辑代码
> }
> ```
>
> 在所有代码中都遵循这种使用模式，可以保证所有数据库资源都能被正确地关闭。
>
> #### 映射器实例
>
> 映射器是一些绑定映射语句的接口。映射器接口的实例是从 SqlSession 中获得的。虽然从技术层面上来讲，任何映射器实例的最大作用域与请求它们的 SqlSession 相同。但方法作用域才是映射器实例的最合适的作用域。 也就是说，映射器实例应该在调用它们的方法中被获取，使用完毕之后即可丢弃。 映射器实例并不需要被显式地关闭。尽管在整个请求作用域保留映射器实例不会有什么问题，但是你很快会发现，在这个作用域上管理太多像 SqlSession 的资源会让你忙不过来。 因此，最好将映射器放在方法作用域内。就像下面的例子一样：
>
> ```
> try (SqlSession session = sqlSessionFactory.openSession()) {
>   BlogMapper mapper = session.getMapper(BlogMapper.class);
>   // 你的应用逻辑代码
> }
> ```

## 五、结果映射resultMap

> `resultMap` 元素是 MyBatis 中最重要最强大的元素。它可以让你从 90% 的 JDBC `ResultSets` 数据提取代码中解放出来，并在一些情形下允许你进行一些 JDBC 不支持的操作。实际上，在为一些比如连接的复杂语句编写映射代码的时候，一份 `resultMap` 能够代替实现同等功能的数千行代码。ResultMap 的设计思想是，对简单的语句做到零配置，对于复杂一点的语句，只需要描述语句之间的关系就行了。

### 简单结果映射

之前你已经见过简单映射语句的示例，它们没有显式指定 `resultMap`。比如：

```
<select id="selectUsers" resultType="map">
  select id, username, hashedPassword
  from some_table
  where id = #{id}
</select>
```

上述语句只是简单地将所有的列映射到 `HashMap` 的键上，这由 `resultType` 属性指定。虽然在大部分情况下都够用，但是 HashMap 并不是一个很好的领域模型。你的程序更可能会使用 JavaBean 或 POJO（Plain Old Java Objects，普通老式 Java 对象）作为领域模型。MyBatis 对两者都提供了支持。看看下面这个 JavaBean：

```
package com.someapp.model;
public class User {
  private int id;
  private String username;
  private String hashedPassword;

  public int getId() {
    return id;
  }
  public void setId(int id) {
    this.id = id;
  }
  public String getUsername() {
    return username;
  }
  public void setUsername(String username) {
    this.username = username;
  }
  public String getHashedPassword() {
    return hashedPassword;
  }
  public void setHashedPassword(String hashedPassword) {
    this.hashedPassword = hashedPassword;
  }
}
```

基于 JavaBean 的规范，上面这个类有 3 个属性：id，username 和 hashedPassword。这些属性会对应到 select 语句中的列名。

这样的一个 JavaBean 可以被映射到 `ResultSet`，就像映射到 `HashMap` 一样简单。

```
<select id="selectUsers" resultType="com.someapp.model.User">
  select id, username, hashedPassword
  from some_table
  where id = #{id}
</select>
```

类型别名是你的好帮手。使用它们，你就可以不用输入类的全限定名了。比如：

```
<!-- mybatis-config.xml 中 -->
<typeAlias type="com.someapp.model.User" alias="User"/>

<!-- SQL 映射 XML 中 -->
<select id="selectUsers" resultType="User">
  select id, username, hashedPassword
  from some_table
  where id = #{id}
</select>
```

在这些情况下，**MyBatis 会在幕后自动创建一个 `ResultMap`，再根据属性名来映射列到 JavaBean 的属性上。**如果列名和属性名不能匹配上，可以在 SELECT 语句中设置列别名（这是一个基本的 SQL 特性）来完成匹配。比如：

```
<select id="selectUsers" resultType="User">
  select
    user_id             as "id",
    user_name           as "userName",
    hashed_password     as "hashedPassword"
  from some_table
  where id = #{id}
</select>
```

在学习了上面的知识后，你会发现上面的例子没有一个需要显式配置 `ResultMap`，这就是 `ResultMap` 的优秀之处——你完全可以不用显式地配置它们。 虽然上面的例子不用显式配置 `ResultMap`。 但为了讲解，我们来看看如果在刚刚的示例中，显式使用外部的 `resultMap` 会怎样，这也是解决列名不匹配的另外一种方式。

```
<resultMap id="userResultMap" type="User">
  <id property="id" column="user_id" />
  <result property="username" column="user_name"/>
  <result property="password" column="hashed_password"/>
</resultMap>
```

然后在引用它的语句中设置 `resultMap` 属性就行了（注意我们去掉了 `resultType` 属性）。比如:

```
<select id="selectUsers" resultMap="userResultMap">
  select user_id, user_name, hashed_password
  from some_table
  where id = #{id}
</select>
```

> 如果这个世界总是这么简单就好了。

### 复杂结果映射

准备工作 创建teacher和student表，创建对应pojo类和mapper等文件

```sql
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student`  (
  `id` int NOT NULL,
  `name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `tid` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fktid`(`tid`) USING BTREE,
  CONSTRAINT `fktid` FOREIGN KEY (`tid`) REFERENCES `teacher` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student
-- ----------------------------
INSERT INTO `student` VALUES (1, '小明', 1);
INSERT INTO `student` VALUES (2, '小红', 1);
INSERT INTO `student` VALUES (3, '小张', 1);
INSERT INTO `student` VALUES (4, '小李', 1);
INSERT INTO `student` VALUES (5, '小王', 2);

SET FOREIGN_KEY_CHECKS = 1;

DROP TABLE IF EXISTS `teacher`;
CREATE TABLE `teacher`  (
  `id` int NOT NULL,
  `name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of teacher
-- ----------------------------
INSERT INTO `teacher` VALUES (1, '秦老师');
INSERT INTO `teacher` VALUES (2, '李老师');

SET FOREIGN_KEY_CHECKS = 1;
```



**student.java**:

```java
package com.xfeng.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: study-mybatis
 * @description:
 * @author: xiongfeng
 * @create: 2022-07-11 15:10
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    private int id;
    private String name;
    private Teacher teacher;
}
```

**Teacher.java** :

```java
package com.xfeng.pojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * @program: study-mybatis
 * @description:
 * @author: xiongfeng
 * @create: 2022-07-11 15:12
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Teacher {
    private String name;
    private int id;
    private List<Student> studentList;
}

```

**多对一**  使用关联 association 

StudentMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!--核心配置文件-->
<mapper namespace="com.xfeng.dao.StudentMapper">
    <!--第一种方式 按照结果嵌套处理-->
    <resultMap id="StudentAndTeacher2" type="Student">
        <result property="id" column="sid"></result>
        <result property="name" column="sname"></result>
        <association property="teacher" javaType="Teacher">
            <result property="id" column="tid"></result>
            <result property="name" column="tname"></result>
        </association>
    </resultMap>
    <select id="getStudent2" resultMap="StudentAndTeacher2">
        select s.name sname, s.id sid, t.id tid, t.name tname
        from mybatis.student s,
             mybatis.teacher t
        where t.id = s.tid
    </select>
    <!--    第二种方式 按照查询嵌套处理-->
    <resultMap id="StudentAndTeacher" type="Student">
        <result property="id" column="id"></result>
        <result property="name" column="name"></result>
        <!--        复杂的属性需要单独处理
                    对象使用:association
                    集合使用:collection
        -->
        <association property="teacher" column="tid" javaType="Teacher"
                     select="com.xfeng.dao.TeacherMapper.getTeacherById">
        </association>
    </resultMap>
    <select id="getStudent" resultMap="StudentAndTeacher">
        select *
        from mybatis.student
    </select>
</mapper>
```

**一对多** 使用集合collection

TeacherMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!--核心配置文件-->
<mapper namespace="com.xfeng.dao.TeacherMapper">


    <select id="getTeacherById" parameterType="int" resultType="Teacher">
        select *
        from mybatis.teacher
        where id = #{id}
    </select>
    <!-- 第一种方式 按照结果嵌套处理-->
    <resultMap id="TeacherAndStudents1" type="Teacher">
        <result property="id" column="tid"></result>
        <result property="name" column="tname"></result>
        <collection property="studentList" ofType="Student">
            <result property="id" column="sid"></result>
            <result property="name" column="sname"></result>
        </collection>
    </resultMap>

    <select id="getTeacherById1" resultMap="TeacherAndStudents1">
        select t.id tid, t.name tname, s.id sid, s.name sname, s.tid stid
        from mybatis.teacher t,
             mybatis.student s
        where t.id = s.tid
    </select>
    <!--    第二种方式 按照查询嵌套处理-->
    <resultMap id="TeacherAndStudents2" type="Teacher">
        <collection property="studentList" javaType="ArrayList" ofType="Student" select="getStudentByTeacherId"
                    column="id">
            <result property="id" column="id"></result>
            <result property="name" column="name"></result>
        </collection>
    </resultMap>
    <select id="getTeacherById2" resultMap="TeacherAndStudents2">
        select *
        from mybatis.teacher
    </select>
    <select id="getStudentByTeacherId" resultType="Student">
        select *
        from mybatis.student
        where tid = #{tid}
    </select>
</mapper>
```

**小结**

**多对一**  使用关联 association、

**一对多** 使用集合collection

javaType 用来指定实体类中属性的类型
ofType 用来指定映射到List或者集合中的pojo类型，泛型中的约束类型



## 六、日志工厂

### 标准日志输出STDOUT_LOGGING

```xml
    <settings>
        <!-- 标准日志输出-->
<!--        <setting name="logImpl" value="STDOUT_LOGGING"/>-->
        <setting name="logImpl" value="LOG4J"/>
    </settings>
```

### Log4j

```xml
    <settings>
        <setting name="logImpl" value="LOG4J"/>
    </settings>
```
在resources目录下新建log4j.properties

```properties
#将等级为DEBUG的日志信息输出到console和file这两个目的地，console和file的定义在下面的代码
log4j.rootLogger=DEBUG,console,file

#控制台输出的相关设置
log4j.appender.console = org.apache.log4j.ConsoleAppender
log4j.appender.console.Target = System.out
log4j.appender.console.Threshold=DEBUG
log4j.appender.console.layout = org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern=【%c】-%m%n

#文件输出的相关设置
log4j.appender.file = org.apache.log4j.RollingFileAppender
log4j.appender.file.File=./log/kuang.log
log4j.appender.file.MaxFileSize=10mb
log4j.appender.file.Threshold=DEBUG
log4j.appender.file.layout=org.apache.log4j.PatternLayout
log4j.appender.file.layout.ConversionPattern=【%p】【%d{yy-MM-dd}】【%c】%m%n

#日志输出级别
log4j.logger.org.mybatis=DEBUG
log4j.logger.java.sql=DEBUG
log4j.logger.java.sql.Statement=DEBUG
log4j.logger.java.sql.ResultSet=DEBUG
log4j.logger.java.sql.PreparedStatement=DEBUG
```

日志输出：

![image-20220711105106124](https://s2.loli.net/2022/07/11/nyZzom8RkgLAGvt.png)



```java
static Logger logger = Logger.getLogger(UserMapperTest.class);
    @Test
    public void testLog4j(){
        logger.info("log4j测试");
        logger.error("log4j测试");
        logger.debug("log4j测试");
    }
```

## 七、Limit分页

**为什么要使用分页？**减少数据量，提高系统新能响应速度

1. 使用MyBatis实现分页，核心SQL

```xml
<select id="getUserList" parameterType="map" resultMap="resultMapUser">
     select * from mybatis.user limit #{startIndex},#{pageSiez};
</select>
```

2. 使用RowBounds，注意是逻辑分页，数据读取到内存中再分页拿取

3. 分页插件

   [MyBatis 分页插件 PageHelper](https://pagehelper.github.io/)

## 八、简单注解的使用

```java
@Select("select * from user where id = #{id}")
User getById(@Param("id") int id,@Param("name") String name);
```



## 九、工作原理以及核心流程详解





![mybatis.png](https://s2.loli.net/2022/07/11/SlOiy5fkcbrzaJZ.png)

上面中流程就是MyBatis内部核心流程，每一步流程的详细说明如下文所述：

（1）读取MyBatis的配置文件。mybatis-config.xml为MyBatis的全局配置文件，用于配置数据库连接信息。

（2）加载映射文件。映射文件即SQL映射文件，该文件中配置了操作数据库的SQL语句，需要在MyBatis配置文件mybatis-config.xml中加载。mybatis-config.xml 文件可以加载多个映射文件，每个文件对应数据库中的一张表。

（3）构造会话工厂。通过MyBatis的环境配置信息构建会话工厂SqlSessionFactory。

（4）创建会话对象。由会话工厂创建SqlSession对象，该对象中包含了执行SQL语句的所有方法。

（5）Executor执行器。MyBatis底层定义了一个Executor接口来操作数据库，它将根据SqlSession传递的参数动态地生成需要执行的SQL语句，同时负责查询缓存的维护。

（6）MappedStatement对象。在Executor接口的执行方法中有一个MappedStatement类型的参数，该参数是对映射信息的封装，用于存储要映射的SQL语句的id、参数等信息。

（7）输入参数映射。输入参数类型可以是Map、List等集合类型，也可以是基本数据类型和POJO类型。输入参数映射过程类似于JDBC对preparedStatement对象设置参数的过程。

（8）输出结果映射。输出结果类型可以是Map、List等集合类型，也可以是基本数据类型和POJO类型。输出结果映射过程类似于JDBC对结果集的解析过程。



## 十、动态SQL





## 十一、MyBatis缓存

### 1.简介

**什么是缓存？**

- 就是将用户经常查询的数据的结果的一个保存，保存到一个内存中（缓存就是内存中的一个对象），用户在查询的时候就不用到数据库文件中查询（磁盘），
- 从而减少与数据库的交付次数提高了响应速度，解决了并发系统的性能问题。

**为什么使用缓存？**

- 减少和数据库的交互操作，减少系统开销，提高系统效率

什么样的数据能使用缓存？

- 经常查询并且不经常改变的数据

### 2.MyBatis缓存分类

MyBatis提供了一级缓存和二级缓存

- 一级缓存：也称为本地缓存，用于保存用户在**一次会话**过程中查询的结果，用户一次会话中只能使用一个sqlSession，一级缓存是自动开启的，不允许关闭。
- 二级缓存：也称为全局缓存，是mapper级别的缓存，是针对一个表的查结果的存储，可以共享给所有针对这张表的查询的用户。也就是说对于mapper级别的缓存不同的sqlsession是可以共享的。

**会话**就是一次完整的交流，再一次交流过程中包含多次请求响应，而发送的请求都是同一个用户，SqlSession就是用户与数据库进行一次会话过程中使用的接口。

> - 映射语句文件中的所有 select 语句的结果将会被缓存。
> - 映射语句文件中的所有 insert、update 和 delete 语句会刷新缓存。
> - 缓存会使用最近最少使用算法（LRU, Least Recently Used）算法来清除不需要的缓存。
> - 缓存不会定时进行刷新（也就是说，没有刷新间隔）。
> - 缓存会保存列表或对象（无论查询方法返回哪种）的 1024 个引用。
> - 缓存会被视为读/写缓存，这意味着获取到的对象并不是共享的，可以安全地被调用者修改，而不干扰其他调用者或线程所做的潜在修改。

### 3.二级缓存

二级缓存的开启

```xml
<settings>
       <!--MyBatis核心配置文件中开启缓存，默认就是为true-->
        <setting name="cacheEnabled" value="true"/>
</settings>
```

> 默认情况下，只启用了本地的会话缓存，它仅仅对一个会话中的数据进行缓存。 要启用全局的二级缓存，只需要在你的 SQL 映射文件中添加一行：
>
> ```xml
> <cache/>
> ```

> 缓存只作用于 cache 标签所在的映射文件中的语句。如果你混合使用 Java API 和 XML 映射文件，在共用接口中的语句将不会被默认缓存。你需要使用 @CacheNamespaceRef 注解指定缓存作用域。
>
> 这些属性可以通过 cache 元素的属性来修改。比如：
>
> ```xml
> <cache
>   eviction="FIFO"
>   flushInterval="60000"
>   size="512"
>   readOnly="true"/>
> ```
>
> 这个更高级的配置创建了一个 FIFO 缓存，每隔 60 秒刷新，最多可以存储结果对象或列表的 512 个引用，而且返回的对象被认为是只读的，因此对它们进行修改可能会在不同线程中的调用者产生冲突。
