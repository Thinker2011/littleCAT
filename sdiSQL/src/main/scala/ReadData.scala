import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{DataFrame, SQLContext}

/**
  * Created by U6034345 on 2017/6/18.
  */


object ReadData {
    def main(args:Array[String]):Unit={
      val conf = new SparkConf()
      val sc = new SparkContext(conf)
      val sqlContext = new SQLContext(sc)
      val df_bd = sqlContext.read.format("com.databricks.spark.xml").option("rowTag", "Bond").option("attributePrefix","at").option("valueTag","_VALUE").load("hdfs://Titan/project/eciborg/datavalidation/bond.xml").select("atbondId","IsCallable","IsPutable")
      val df_br = sqlContext.read.format("com.databricks.spark.xml").option("rowTag", "BondRelationship").option("attributePrefix","at").option("valueTag","_VALUE").load("hdfs://Titan/project/eciborg/datavalidation/br.xml").select("RelationObjectId.atrelatedObjectId","RelationObjectId._VALUE")
      val df_ds = sqlContext.read.format("com.databricks.spark.xml").option("rowTag", "DebtStatus").option("attributePrefix","at").option("valueTag","_VALUE").load("hdfs://Titan/project/eciborg/datavalidation/ds.xml").select("atbondId","ateffectiveFrom","DebtStatusCode")
      val df_id = sqlContext.read.format("com.databricks.spark.xml").option("rowTag", "Identifier").option("attributePrefix","at").option("valueTag","_VALUE").load("hdfs://Titan/project/eciborg/datavalidation/id.xml").select("IdentifierValue.atidentifierEntityTypeId","IdentifierValue.atidentifierEntityId","IdentifierValue.atidentifierTypeCode","IdentifierValue._VALUE")
      df_bd.show()
      df_br.show()
      df_ds.show()
      df_id.show()
      df_bd.printSchema()
      df_id.printSchema()
      df_bd.registerTempTable("bd")
      df_br.registerTempTable("br")
      df_ds.registerTempTable("ds")
      df_id.registerTempTable("id")
      //val ot =sqlContext.sql("SELECT * FROM (SELECT * FROM id WHERE @identifierTypeCode='RIC') AS ricid JOIN  AS relationship ON relationship.r0=ricid.@identifierEntityId")
      val ot =sqlContext.sql("SELECT * FROM (SELECT relationship.r1 AS r1,ricid.atidentifierEntityId AS identifierEntityId,ricid._VALUE AS RIC FROM (SELECT br0.atrelatedObjectId AS r0,br1._VALUE AS r1 FROM br AS br0,br AS br1 WHERE br0._VALUE=br1.atrelatedObjectId) AS relationship,(SELECT * FROM id WHERE atidentifierTypeCode='RIC') AS ricid WHERE ricid.atidentifierEntityId=relationship.r0) AS ricrelation,(SELECT * FROM id WHERE atidentifierTypeCode='Isin') AS isinid WHERE isinid.atidentifierEntityId=ricrelation.r1")
      ot.show()
    }
}
