# SensorThingsServer
A Server implementation of the OGC SensorThings API.
[![Build Status](https://travis-ci.org/FraunhoferIOSB/SensorThingsServer.svg?branch=master)](https://travis-ci.org/FraunhoferIOSB/SensorThingsServer)

## Compliance Testing Status:

| Conformance Class                     | Reference | Implemented |Test Status         |
|---------------------------------------|-----------|-------------|--------------------|
| Sensing Core                          | A.1       | Yes         | 6 / 6              |
| Filtering Extension                   | A.2       | Yes         | 41 / 41            |
| Create-Update-Delete                  | A.3       | Yes         | 14 / 14            |
| Batch Request                         | A.4       | No          | No tests available |
| Sensing MultiDatastream Extension     | A.5       | Yes         | 18 / 18            |
| Sensing Data Array Extension          | A.6       | Yes         | 2 / 2              |
| MQTT Extension for Create and Update  | A.7       | Yes         | 4 / 4              |
| MQTT Extension for Receiving Updates  | A.8       | Yes         | 13 / 13            |

We have extended the official test suit with extra tests that can be found [here](https://github.com/FraunhoferIOSB/ets-sta10).
The official test suit is fully passed.
See the wiki page [features](https://github.com/FraunhoferIOSB/SensorThingsServer/wiki/Features) for more details.

## The very short and crude installation instructions

### Database installation

1. create PostgreSQL database for the data
2. install the postgis extensions in this database (CREATE EXTENSION postgis;)

### Database configuration

The database connection is configured from the [Context](http://tomcat.apache.org/tomcat-8.0-doc/config/context.html)
entry either in server.xml, in `$CATALINA_BASE/conf/[enginename]/[hostname]/appname.xml`
or in `/META-INF/context.xml` inside the war file. If you are running the application
from your IDE, it is easiest to change the context.xml file in the war file.

There are two ways to configure the database: Using [JNDI](http://tomcat.apache.org/tomcat-8.0-doc/jndi-datasource-examples-howto.html#PostgreSQL)
or directly.

#### JNDI

This method uses connection pooling and is faster.

1. Copy the [Postgres JDBC jar](http://repo.maven.apache.org/maven2/org/postgresql/postgresql/9.4.1212/postgresql-9.4.1212.jar)
and the [postgis jar](http://repo.maven.apache.org/maven2/net/postgis/postgis-jdbc/2.2.1/postgis-jdbc-2.2.1.jar)
to `$CATALINA_HOME/lib`.
2. Configure the database resource. Either in the Context, or elsewhere in server.xml:

        <Resource
            name="jdbc/sensorThings" auth="Container"
            type="javax.sql.DataSource" driverClassName="org.postgresql.Driver"
            url="jdbc:postgresql://localhost:5432/sensorthings"
            username="sensorthings" password="ChangeMe"
            maxTotal="20" maxIdle="10" maxWaitMillis="-1"/>

3. Tell the application how to find the datasource in the Context:

        <Parameter name="db_jndi_datasource" value="jdbc/sensorThings" description="JNDI data source name"/>

   The value of the Parameter and the name of the Resource have to be the same, but
   can be anything you like.

#### Direct database connection

This method does not support connection pooling.

1. Copy the [Postgres JDBC jar](http://repo.maven.apache.org/maven2/org/postgresql/postgresql/9.4.1209.jre7/postgresql-9.4.1209.jre7.jar)
   and the [postgis jar](http://repo.maven.apache.org/maven2/net/postgis/postgis-jdbc/2.2.0/postgis-jdbc-2.2.0.jar)
   to `WEB-INF/lib` or `$CATALINA_HOME/lib`.
2. Configure the database resource in the Context:

        <Parameter name="db_driver" value="org.postgresql.Driver" description="Database driver classname"/>
        <Parameter name="db_url" value="jdbc:postgresql://localhost:5432/sta" description="Database connection URL"/>
        <Parameter name="db_username" value="postgres" description="Database username"/>
        <Parameter name="db_password" value="1qay!QAY" description="Database password"/>


### Compiling

1. Go to the project root (The top-most directory with a pom.xml)
2. mvn clean install
   This should build the war file in SensorThingsServer/target/


### Database initialisation or upgrade

1. Browse to http://localhost:8080/SensorThingsService/DatabaseStatus

This should initialise/update the database to the latest version and the service
should be ready for use.


### Performance and Indices

By default, only primary and foreign keys have indices on them. If your database grows large
and you notice a significant slowdown, you should check which queries you use most, and
add indices for those queries. A very common one is probably for
Datastreams(x)/observations?$orderby=phenomenonTime desc

```
CREATE INDEX "OBS-DS_ID-PHTIME_SE-O_ID"
  ON "OBSERVATIONS"
  USING btree
  ("DATASTREAM_ID", "PHENOMENON_TIME_START" DESC, "PHENOMENON_TIME_END" DESC, "ID");
```


### Configuration options

The server is configured from the [Context](http://tomcat.apache.org/tomcat-8.0-doc/config/context.html)
entry either in server.xml, in `$CATALINA_BASE/conf/[enginename]/[hostname]/appname.xml`
or in `/META-INF/context.xml` inside the war file. If you are running the application
from your IDE, it is easiest to change the context.xml file in the war file. It has
the following options:

* SensorThings API settings
  * `ApiVersion`: The version tag of the API used in the URL.
  * `serviceRootUrl`: The base URL of the SensorThings Server without version.
  * `defaultCount`: The default value for the $count query option.
  * `defaultTop`: The default value for the $top query option.
  * `maxTop`: The maximum allowed value for the $top query option.
  * `useAbsoluteNavigationLinks`: If true, navigationLinks are absolute, otherwise relative.
* MQTT settings
  * `mqtt.mqttServerImplementationClass`: The java class used for running the MQTT server (must implement MqttServer interface)
  * `mqtt.Enabled`: Specifies wether MQTT support will be enabled or not.
  * `mqtt.Port`: The port the MQTT server runs on.
  * `mqtt.QoS`: Quality of Service Level for MQTT messages.
  * `mqtt.SubscribeMessageQueueSize`: Queue size for messages to be pubslihed via MQTT.
  * `mqtt.SubscribeThreadPoolSize`: Number of threads use to dispatch MQTT notifications.
  * `mqtt.CreateMessageQueueSize`: Queue size for create observation requests via MQTT .
  * `mqtt.CreateThreadPoolSize`: Number of threads use to dispatch observation creation requests.
  * `mqtt.Host`: The external IP address or host name the MQTT server should listen on. Set to 0.0.0.0 to listen on all interfaces.
  * `mqtt.internalHost`: The internal host name of the MQTT server.
  * `mqtt.WebsocketPort`: The port the MQTT server is reachable via WebSocket.
* persistence settings
  * `persistence.persistenceManagerImplementationClass`: The java class used for persistence (must implement PersistenceManaher interface)
  * JNDI (Either use this _or_ direct, not both
    * `persistence.db_jndi_datasource`: JNDI data source name
  * Direct (Either use this _or_ JNDI, not both
    * `persistence.db_driver`: Database driver classname
    * `persistence.db_url`: Database connection URL
    * `persistence.db_username`: Database username
    * `persistence.db_password`: Database password

## Docker support

There's also the possibility to run SensorThingsServer and the needed database inside a Docker container.
This dependency is specified inside the ```docker-compose.yaml``` file.
To build and start a container including the PostGIS database run:

```
mvn clean install
docker-compose up --build
```

All data is stored inside the PostGIS database. To keep this state there's a volume automatically mapped to the PostGIS container.
For more information see the ```docker-compose.yaml``` file and the [PostGIS container documentation](https://hub.docker.com/r/mdillon/postgis/)


# Authors

Hylke van der Schaaf
hylke.vanderschaaf@iosb.fraunhofer.de

Michael Jacoby
michael.jacoby@iosb.fraunhofer.de


# License

Copyright (C) 2016 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
Karlsruhe, Germany.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

=======================================================================================================
紀錄:
拿到下載open source code 後
第一件事:打開SensorThingsServer\src\main\webapp\META-INF\context.xml
修改
username="postgres" 
password="csrsr123"
指定你的db
url="jdbc:postgresql://database:5432/sensorthings_lab1"

2.
執行 PostgreSQL 
建sensorthings_lab1

create extension postgis;

3. 配置Tomcat

/SensorThingsService

打開
http://localhost:8080/SensorThingsService/

按下Database Status and Update後
會碰到
Q1
Failed to initialise database:
Cannot load JDBC driver class 'org.postgresql.Driver'

Done. Click the button to execute the listed updates.

A1:
==>
SensorThingsService的pom.xml的<dependencies>
新增
        <!--20170109 add-->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>${postgres.version}</version>
        </dependency>
        <!--20170109 add-->
        <dependency>
            <groupId>net.postgis</groupId>
            <artifactId>postgis-jdbc-java2d</artifactId>
            <version>${postgis.version}</version>
        </dependency>
        
Q2:
Failed to initialise database:
Cannot create PoolableConnectionFactory      
A2:
url="jdbc:postgresql://database:5432/sensorthings_lab1"
正確是
url="jdbc:postgresql://localhost:5432/sensorthings_lab1"  


=======================================================================================================
擴充應用: 加入TaskingCapability模組

1. parser 
set1:add "cTaskingCapabilities() | "  
/** Paths with identifiers **/
void IdentifiedPath(): {}
{
  cDatastreams() |
  cMultiDatastreams() |
  cFeaturesOfInterest() |
  cHistLocations() |
  cLocations() |
  cSensors() |
  cThings() |
  cTaskingCapabilities() |           <-----新增 
  cObservations() |
  cObservedProps()
} 
set2:  add
/** Entity and collection TaskingCapability(s) **/
/***有幾個TaskingCapability***/
void cTaskingCapabilities(): {}
{
  <C_TASKINGCAPABILITIES>
  (
    eTaskingCapabilityId()
  | <PATH_SEPARATOR> cpRef()
  )?
}


void eTaskingCapabilityId()#eTaskingCapability: {String i;}
{
  <LB> i=Long(){jjtThis.jjtSetValue(i);} <RB>
      (childOfTaskingCapability())?
}
/**每個TaskingCapability***/
void eTaskingCapability(): {}
{
  <E_TASKINGCAPABILITY>
  (childOfTaskingCapability())?
}

void childOfTaskingCapability() #void: {}
{
  <PATH_SEPARATOR>
  (
    pId()
  | pSelfLink()
  | pName()
  | pDescription()
  | pProperties()
  | cpRef()
  )
}

set3: add
| <C_TASKINGCAPABILITIES: "TaskingCapabilities" >
| <E_TASKINGCAPABILITY:  "TaskingCapability" >

說明:
關於 
| <C_TASKINGCAPABILITIES:              "TaskingCapabilities" >

打開
http://localhost:8080/SensorThingsService/v1.0/TaskingCapabilities
會將TaskingCapabilities轉為內部的C_TASKINGCAPABILITIES




set4
D:\j2ee_dojo\SensorThingsServer_zebralab2>mvn clean install
[ERROR] /D:/j2ee_dojo/SensorThingsServer_zebralab2/SensorThingsServer.Core/src/main/java/de/fraunhofer/iosb/ilt/sta/parser/path/DumpVisitor.java:[24,8] de.fraunhofer.iosb.ilt.sta.parser.path.DumpVisitor is not abstract and does not override abstract method vis
it(de.fraunhofer.iosb.ilt.sta.parser.path.ASTeTaskingCapability,de.fraunhofer.iosb.ilt.sta.path.ResourcePath) in de.fraunhofer.iosb.ilt.sta.parser.path.ParserVisitor
[ERROR] /D:/j2ee_dojo/SensorThingsServer_zebralab2/SensorThingsServer.Core/src/main/java/de/fraunhofer/iosb/ilt/sta/parser/path/PathParser.java:[38,8] de.fraunhofer.iosb.ilt.sta.parser.path.PathParser is not abstract and does not override abstract method visit
(de.fraunhofer.iosb.ilt.sta.parser.path.ASTeTaskingCapability,de.fraunhofer.iosb.ilt.sta.path.ResourcePath) in de.fraunhofer.iosb.ilt.sta.parser.path.ParserVisitor

解決方案
DumpVisitor.java和PathParser.java需實作
ASTeTaskingCapabilities
ASTeTaskingCapability


SensorThingsServer.Core/src/main/java/de/fraunhofer/iosb/ilt/sta/parser/path/DumpVisitor.java
    //20170310 add ASTcTaskingCapabilitys 

    //20170310 add ASTeTaskingCapability 
    @Override
    public ResourcePath visit(ASTeTaskingCapability node, ResourcePath data) {
        return defltAction(node, data);
    }

    @Override
    public ResourcePath visit(ASTcTaskingCapabilities node, ResourcePath data) {
        return defltAction(node, data);
    }


SensorThingsServer.Core/src/main/java/de/fraunhofer/iosb/ilt/sta/parser/path/PathParser.java

    //20170310 add ASTeTaskingCapability
    @Override
    public ResourcePath visit(ASTeTaskingCapability node, ResourcePath data) {
        addAsEntitiy(data, node, EntityType.TaskingCapability);
        return defltAction(node, data);
    }
    //20170310 add ASTcTaskingCapabilitys
    @Override
    public ResourcePath visit(ASTcTaskingCapabilities node, ResourcePath data) {
        addAsEntitiySet(data, EntityType.TaskingCapability);
        /*
        要改為TaskingCapability ,
        EntityType.java也要註冊TaskingCapability相關的初始化
        也要寫一個TaskingCapability.java
        */
        return defltAction(node, data);
    }


重新編譯
D:\j2ee_dojo\SensorThingsServer_zebralab2>mvn clean install


[INFO] BUILD SUCCESS


2. 
rom Tomcat

http://localhost:8080/SensorThingsService/v1.0

可以看到url選項多了 TaskingCapabilities,
但點選會出現 Failed to execute query. See logs for details.
{
name: "TaskingCapabilities",
url: "http://localhost:8080/SensorThingsService/v1.0/TaskingCapabilities"
}


3.
QA:
http://localhost:8080/SensorThingsService/v1.0/Things
Failed to execute query. See logs for details.



=======================================================================================================
看看compiler做了什麼
D:\j2ee_dojo\SensorThingsServer_zebralab2>mvn clean install
[INFO] Scanning for projects...
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Build Order:
[INFO]
[INFO] SensorThingsServerParent
[INFO] SensorThingsServer.Core
[INFO] SensorThingsServer.SQL
[INFO] SensorThingsServer.MQTT.Moquette
[INFO] SensorThingsServer
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] Building SensorThingsServerParent 1.0
[INFO] ------------------------------------------------------------------------
[INFO]
[INFO] --- maven-clean-plugin:2.5:clean (default-clean) @ SensorThingsServerParent ---
[INFO]
[INFO] --- maven-javadoc-plugin:2.10.4:jar (attach-javadocs) @ SensorThingsServerParent ---
[INFO] Not executing Javadoc as the project is not a Java classpath-capable package
[INFO]
[INFO] --- maven-source-plugin:3.0.1:jar-no-fork (attach-sources) @ SensorThingsServerParent ---
[INFO]
[INFO] --- maven-install-plugin:2.4:install (default-install) @ SensorThingsServerParent ---
[INFO] Installing D:\j2ee_dojo\SensorThingsServer_zebralab2\pom.xml to C:\Users\cs_dojo\.m2\repository\de\fraunhofer\iosb\ilt\SensorThingsServerParent\1.0\SensorThingsServerParent-1.0.pom
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] Building SensorThingsServer.Core 1.0
[INFO] ------------------------------------------------------------------------
[INFO]
[INFO] --- maven-clean-plugin:2.5:clean (default-clean) @ SensorThingsServer.Core ---
[INFO]
[INFO] --- maven-dependency-plugin:2.6:copy (default) @ SensorThingsServer.Core ---
[INFO]
[INFO] --- javacc-maven-plugin:2.6:jjtree-javacc (jjtree-javacc) @ SensorThingsServer.Core ---
Java Compiler Compiler Version 6.1_2 (Tree Builder)
(type "jjtree" with no arguments for help)
Reading from file D:\j2ee_dojo\SensorThingsServer_zebralab2\SensorThingsServer.Core\src\main\jjtree\staParser\pathParser.jjt . . .
opt:java
File "Node.java" does not exist.  Will create one.
File "SimpleNode.java" does not exist.  Will create one.
File "ASTStart.java" does not exist.  Will create one.
File "ASTIdentifiedPath.java" does not exist.  Will create one.
File "ASTeDatastream.java" does not exist.  Will create one.
File "ASTcDatastreams.java" does not exist.  Will create one.
File "ASTeMultiDatastream.java" does not exist.  Will create one.
File "ASTcMultiDatastreams.java" does not exist.  Will create one.
File "ASTeFeatureOfInterest.java" does not exist.  Will create one.
File "ASTcFeaturesOfInterest.java" does not exist.  Will create one.
File "ASTeHistLocation.java" does not exist.  Will create one.
File "ASTcHistLocations.java" does not exist.  Will create one.
File "ASTeLocation.java" does not exist.  Will create one.
File "ASTcLocations.java" does not exist.  Will create one.
File "ASTeSensor.java" does not exist.  Will create one.
File "ASTcSensors.java" does not exist.  Will create one.
File "ASTeThing.java" does not exist.  Will create one.
File "ASTcThings.java" does not exist.  Will create one.
File "ASTeTaskingCapability.java" does not exist.  Will create one.
File "ASTcTaskingCapabilities.java" does not exist.  Will create one.
File "ASTeObservation.java" does not exist.  Will create one.
File "ASTcObservations.java" does not exist.  Will create one.
File "ASTeObservedProp.java" does not exist.  Will create one.
File "ASTcObservedProps.java" does not exist.  Will create one.
File "ASTpId.java" does not exist.  Will create one.
File "ASTpSelfLink.java" does not exist.  Will create one.
File "ASTpDescription.java" does not exist.  Will create one.
File "ASTpDefinition.java" does not exist.  Will create one.
File "ASTpEncodingType.java" does not exist.  Will create one.
File "ASTpFeature.java" does not exist.  Will create one.
File "ASTpLocation.java" does not exist.  Will create one.
File "ASTpMetadata.java" does not exist.  Will create one.
File "ASTpName.java" does not exist.  Will create one.
File "ASTpObservedArea.java" does not exist.  Will create one.
File "ASTpObservationType.java" does not exist.  Will create one.
File "ASTpMultiObservationDataTypes.java" does not exist.  Will create one.
File "ASTpPhenomenonTime.java" does not exist.  Will create one.
File "ASTpParameters.java" does not exist.  Will create one.
File "ASTpProperties.java" does not exist.  Will create one.
File "ASTpResult.java" does not exist.  Will create one.
File "ASTpResultTime.java" does not exist.  Will create one.
File "ASTpResultQuality.java" does not exist.  Will create one.
File "ASTpTime.java" does not exist.  Will create one.
File "ASTpUnitOfMeasurement.java" does not exist.  Will create one.
File "ASTpUnitOfMeasurements.java" does not exist.  Will create one.
File "ASTpValidTime.java" does not exist.  Will create one.
File "ASTcpRef.java" does not exist.  Will create one.
File "ASTppValue.java" does not exist.  Will create one.
File "ASTppSubProperty.java" does not exist.  Will create one.
File "ASTppArrayIndex.java" does not exist.  Will create one.
File "ASTLong.java" does not exist.  Will create one.
File "ParserTreeConstants.java" does not exist.  Will create one.
File "ParserVisitor.java" does not exist.  Will create one.
File "ParserDefaultVisitor.java" does not exist.  Will create one.
File "JJTParserState.java" does not exist.  Will create one.
Annotated grammar generated successfully in D:\j2ee_dojo\SensorThingsServer_zebralab2\SensorThingsServer.Core\target\javacc-1506346757505\node\pathParser.jj
Java Compiler Compiler Version 6.1_2 (Parser Generator)
(type "javacc" with no arguments for help)
Reading from file D:\j2ee_dojo\SensorThingsServer_zebralab2\SensorThingsServer.Core\target\javacc-1506346757505\node\pathParser.jj . . .
File "TokenMgrError.java" does not exist.  Will create one.
File "ParseException.java" does not exist.  Will create one.
File "Token.java" does not exist.  Will create one.
File "SimpleCharStream.java" does not exist.  Will create one.
Parser generated successfully.
Java Compiler Compiler Version 6.1_2 (Tree Builder)
(type "jjtree" with no arguments for help)
Reading from file D:\j2ee_dojo\SensorThingsServer_zebralab2\SensorThingsServer.Core\src\main\jjtree\staParser\queryParser.jjt . . .
opt:java
File "Node.java" does not exist.  Will create one.
File "SimpleNode.java" does not exist.  Will create one.
File "ASTStart.java" does not exist.  Will create one.
File "ASTOptions.java" does not exist.  Will create one.
File "ASTOption.java" does not exist.  Will create one.
File "ASTOrderBys.java" does not exist.  Will create one.
File "ASTOrderBy.java" does not exist.  Will create one.
File "ASTIdentifiers.java" does not exist.  Will create one.
File "ASTPlainPaths.java" does not exist.  Will create one.
File "ASTPlainPath.java" does not exist.  Will create one.
File "ASTPathElement.java" does not exist.  Will create one.
File "ASTFilteredPaths.java" does not exist.  Will create one.
File "ASTFilteredPath.java" does not exist.  Will create one.
File "ASTIdentifiedPaths.java" does not exist.  Will create one.
File "ASTIdentifiedPath.java" does not exist.  Will create one.
File "ASTFilter.java" does not exist.  Will create one.
File "ASTLogicalOr.java" does not exist.  Will create one.
File "ASTLogicalAnd.java" does not exist.  Will create one.
File "ASTNot.java" does not exist.  Will create one.
File "ASTBooleanFunction.java" does not exist.  Will create one.
File "ASTComparison.java" does not exist.  Will create one.
File "ASTPlusMin.java" does not exist.  Will create one.
File "ASTOperator.java" does not exist.  Will create one.
File "ASTMulDiv.java" does not exist.  Will create one.
File "ASTFunction.java" does not exist.  Will create one.
File "ASTValueNode.java" does not exist.  Will create one.
File "ASTGeoStringLit.java" does not exist.  Will create one.
File "ASTBool.java" does not exist.  Will create one.
File "ASTFormat.java" does not exist.  Will create one.
File "ParserTreeConstants.java" does not exist.  Will create one.
File "ParserVisitor.java" does not exist.  Will create one.
File "ParserDefaultVisitor.java" does not exist.  Will create one.
File "JJTParserState.java" does not exist.  Will create one.
Annotated grammar generated successfully in D:\j2ee_dojo\SensorThingsServer_zebralab2\SensorThingsServer.Core\target\javacc-1506346758721\node\queryParser.jj
Java Compiler Compiler Version 6.1_2 (Parser Generator)
(type "javacc" with no arguments for help)
Reading from file D:\j2ee_dojo\SensorThingsServer_zebralab2\SensorThingsServer.Core\target\javacc-1506346758721\node\queryParser.jj . . .
File "TokenMgrError.java" does not exist.  Will create one.
File "ParseException.java" does not exist.  Will create one.
File "Token.java" does not exist.  Will create one.
File "SimpleCharStream.java" does not exist.  Will create one.
Parser generated successfully.
[INFO] Processed 2 grammars
[INFO]
[INFO] --- maven-resources-plugin:2.6:resources (default-resources) @ SensorThingsServer.Core ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] skip non existing resourceDirectory D:\j2ee_dojo\SensorThingsServer_zebralab2\SensorThingsServer.Core\src\main\resources
[INFO]
[INFO] --- maven-compiler-plugin:3.1:compile (default-compile) @ SensorThingsServer.Core ---
[INFO] Changes detected - recompiling the module!
[INFO] Compiling 323 source files to D:\j2ee_dojo\SensorThingsServer_zebralab2\SensorThingsServer.Core\target\classes
[INFO] -------------------------------------------------------------
[WARNING] COMPILATION WARNING :
[INFO] -------------------------------------------------------------
[WARNING] /D:/j2ee_dojo/SensorThingsServer_zebralab2/SensorThingsServer.Core/src/main/java/de/fraunhofer/iosb/ilt/sta/model/mixin/DatastreamMixIn.java: Some input files use or override a deprecated API.
[WARNING] /D:/j2ee_dojo/SensorThingsServer_zebralab2/SensorThingsServer.Core/src/main/java/de/fraunhofer/iosb/ilt/sta/model/mixin/DatastreamMixIn.java: Recompile with -Xlint:deprecation for details.
[WARNING] /D:/j2ee_dojo/SensorThingsServer_zebralab2/SensorThingsServer.Core/src/main/java/de/fraunhofer/iosb/ilt/sta/query/expression/function/Function.java: Some input files use unchecked or unsafe operations.
[WARNING] /D:/j2ee_dojo/SensorThingsServer_zebralab2/SensorThingsServer.Core/src/main/java/de/fraunhofer/iosb/ilt/sta/query/expression/function/Function.java: Recompile with -Xlint:unchecked for details.
[INFO] 4 warnings
[INFO] -------------------------------------------------------------
[INFO] -------------------------------------------------------------
[ERROR] COMPILATION ERROR :
[INFO] -------------------------------------------------------------
[ERROR] /D:/j2ee_dojo/SensorThingsServer_zebralab2/SensorThingsServer.Core/src/main/java/de/fraunhofer/iosb/ilt/sta/parser/path/DumpVisitor.java:[24,8] de.fraunhofer.iosb.ilt.sta.parser.path.DumpVisitor is not abstract and does not override abstract method visit(de.fraunhofer.iosb.ilt.sta.parse
r.path.ASTcTaskingCapabilities,de.fraunhofer.iosb.ilt.sta.path.ResourcePath) in de.fraunhofer.iosb.ilt.sta.parser.path.ParserVisitor
[ERROR] /D:/j2ee_dojo/SensorThingsServer_zebralab2/SensorThingsServer.Core/src/main/java/de/fraunhofer/iosb/ilt/sta/parser/path/PathParser.java:[36,8] de.fraunhofer.iosb.ilt.sta.parser.path.PathParser is not abstract and does not override abstract method visit(de.fraunhofer.iosb.ilt.sta.parser.
path.ASTcTaskingCapabilities,de.fraunhofer.iosb.ilt.sta.path.ResourcePath) in de.fraunhofer.iosb.ilt.sta.parser.path.ParserVisitor
[INFO] 2 errors
[INFO] -------------------------------------------------------------
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO]
[INFO] SensorThingsServerParent ........................... SUCCESS [  1.280 s]
[INFO] SensorThingsServer.Core ............................ FAILURE [ 12.295 s]
[INFO] SensorThingsServer.SQL ............................. SKIPPED
[INFO] SensorThingsServer.MQTT.Moquette ................... SKIPPED
[INFO] SensorThingsServer ................................. SKIPPED
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 13.815 s
[INFO] Finished at: 2017-09-25T21:39:28+08:00
[INFO] Final Memory: 30M/331M
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.1:compile (default-compile) on project SensorThingsServer.Core: Compilation failure: Compilation failure:
[ERROR] /D:/j2ee_dojo/SensorThingsServer_zebralab2/SensorThingsServer.Core/src/main/java/de/fraunhofer/iosb/ilt/sta/parser/path/DumpVisitor.java:[24,8] de.fraunhofer.iosb.ilt.sta.parser.path.DumpVisitor is not abstract and does not override abstract method visit(de.fraunhofer.iosb.ilt.sta.parse
r.path.ASTcTaskingCapabilities,de.fraunhofer.iosb.ilt.sta.path.ResourcePath) in de.fraunhofer.iosb.ilt.sta.parser.path.ParserVisitor
[ERROR] /D:/j2ee_dojo/SensorThingsServer_zebralab2/SensorThingsServer.Core/src/main/java/de/fraunhofer/iosb/ilt/sta/parser/path/PathParser.java:[36,8] de.fraunhofer.iosb.ilt.sta.parser.path.PathParser is not abstract and does not override abstract method visit(de.fraunhofer.iosb.ilt.sta.parser.
path.ASTcTaskingCapabilities,de.fraunhofer.iosb.ilt.sta.path.ResourcePath) in de.fraunhofer.iosb.ilt.sta.parser.path.ParserVisitor
[ERROR] -> [Help 1]
[ERROR]
[ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.
[ERROR] Re-run Maven using the -X switch to enable full debug logging.
[ERROR]
[ERROR] For more information about the errors and possible solutions, please read the following articles:
[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/MojoFailureException
[ERROR]
[ERROR] After correcting the problems, you can resume the build with the command
[ERROR]   mvn <goals> -rf :SensorThingsServer.Core

