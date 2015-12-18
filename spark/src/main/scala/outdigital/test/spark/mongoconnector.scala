package outdigital.test.spark

import org.apache.hadoop.conf.Configuration
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.rdd.RDD

import org.bson.BSONObject
import com.mongodb.hadoop.{
  MongoInputFormat, MongoOutputFormat,
  BSONFileInputFormat, BSONFileOutputFormat}

object SparkExample extends App {
  // Set up the configuration for reading from MongoDB.
  val mongoConfig = new Configuration()
  // MongoInputFormat allows us to read from a live MongoDB instance.
  // We could also use BSONFileInputFormat to read BSON snapshots.
  // MongoDB connection string naming a collection to read.
  // If using BSON, use "mapred.input.dir" to configure the directory
  // where the BSON files are located instead.
  mongoConfig.set("mongo.input.uri",
    "mongodb://52.2.62.231:27017/adserver.RequestCollection")

  val sparkConf = new SparkConf()
  val sc = new SparkContext("local", "SparkExample", sparkConf)

  // Create an RDD backed by the MongoDB collection.
  val documents = sc.newAPIHadoopRDD(
    mongoConfig,                // Configuration
    classOf[MongoInputFormat],  // InputFormat
    classOf[Object],            // Key type
    classOf[BSONObject])        // Value type

  // Create a separate Configuration for saving data back to MongoDB.
  val outputConfig = new Configuration()
  outputConfig.set("mongo.output.uri",
    "mongodb://52.2.62.231:27017/adserver.RequestCollectionOut")

  // Save this RDD as a Hadoop "file".
  // The path argument is unused; all documents will go to "mongo.output.uri".
  documents.saveAsNewAPIHadoopFile(
    "file:///hdfs://52.6.163.3:8020/user/spark/mongodb-bson",
    classOf[Object],
    classOf[BSONObject],
    classOf[MongoOutputFormat[Object, BSONObject]],
    outputConfig)

  // We can also save this back to a BSON file.
  val bsonOutputConfig = new Configuration()
  documents.saveAsNewAPIHadoopFile(
    "hdfs://52.6.163.3:8020/user/spark/mongodb-bson",
    classOf[Object],
    classOf[BSONObject],
    classOf[BSONFileOutputFormat[Object, BSONObject]])
}