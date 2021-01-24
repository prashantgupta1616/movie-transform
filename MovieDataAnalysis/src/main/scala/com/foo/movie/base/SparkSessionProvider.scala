package com.foo.movie.base

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

trait SparkSessionProvider {

  //spark config variable with basic conf,required one can pass/override at runtime usiing spark-submit
  val conf: SparkConf = new SparkConf().setAppName("Spark Framework").setMaster("local[*]").set("spark.driver.bindAddress", "localhost")
  lazy val sparkSession: SparkSession = SparkSession.builder.config(conf).getOrCreate()
  lazy val sqlContext = sparkSession.sparkContext

}
