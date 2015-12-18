package outdigital.test.spark
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext


object wordcount {
  
  def main(args: Array[String]) = {
    
    val conf  = new SparkConf()
    .setAppName("wordcount")
    .setMaster("local")
    val sc  = new SparkContext(conf)
    
    val test  = sc.textFile("names.txt")
    test.flatMap { line =>
        line.split(" ")
    }
     .map { word => 
        (word,1) 
        }
     .reduceByKey(_ + _)
     .saveAsTextFile("names.count.txt")
  }
}