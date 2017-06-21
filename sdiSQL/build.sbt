import sbtassembly.Plugin.AssemblyKeys._

name := "sdiSQL"
version := "1.0"
scalaVersion := "2.10.4"

val spark_xml_verison="0.3.5"

val spark_xml="com.databricks" % "spark-xml_2.10" % spark_xml_verison

val hadoop_version = "2.6.0-cdh5.5.2"
val hbase_version = "1.0.0-cdh5.5.2"
val spark_version = "1.6.2"

// hadoop
val hbase_common = "org.apache.hbase" % "hbase-common" % hbase_version
val hbase_client = "org.apache.hbase" % "hbase-client" % hbase_version
val hbase_server = "org.apache.hbase" % "hbase-server" % hbase_version
val hadoop_common = "org.apache.hadoop" % "hadoop-common" % hadoop_version
val hadoop_hdfs = "org.apache.hadoop" % "hadoop-hdfs" % hadoop_version
val hbase_hadoop_compat = "org.apache.hbase" % "hbase-hadoop-compat" % hbase_version

// spark
val spark_core = "org.apache.spark" %% "spark-core" % spark_version % "provided"
val spark_sql = "org.apache.spark" %% "spark-sql" % spark_version % "provided"

val common_resolvers = Seq(
  // add link ~/.m2/repository to actual maven repo
  // if not use default repo location
  "central" at "http://central.maven.org/maven2/",
  Resolver.mavenLocal
)



resolvers ++= common_resolvers

libraryDependencies ++= Seq(
  hbase_client,
  hbase_common,
  hbase_server,
  spark_core,
  spark_sql,
  hadoop_common,
  hadoop_hdfs,
  spark_xml
)

val packagePrefixes = "au|org|com|javax|parquet|jodd"
val fileSuffixes = "xsd|dtd|xml|html|properties|thrift"
mergeStrategy in assembly := {
  case PathList(a, _*) if a.matches(packagePrefixes) => MergeStrategy.first
  case PathList(ps@_*) if ps.last.split('.').last.matches(fileSuffixes) => MergeStrategy.concat
  case PathList(ps@_*) if ps.last == "UnusedStubClass.class" => MergeStrategy.first
  case PathList(ps@_*) if ps.last == "libnetty-transport-native-epoll.so" => MergeStrategy.first
  case PathList(ps@_*) if ps.last endsWith "jersey-module-version" => MergeStrategy.concat
  case x =>
    val oldStrategy = (mergeStrategy in assembly).value
    oldStrategy(x)
}