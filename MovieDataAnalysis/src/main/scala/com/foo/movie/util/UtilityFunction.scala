package com.foo.movie.util

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.foo.movie.constant.MovieConstants
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.udf


case class ProdCompany(name:String,id:Int)
class UtilityFunction extends Serializable {
  val constant=new MovieConstants()

  val jsonConverter = udf { jsonString: String =>
    if (jsonString.startsWith("[")) { // to avoid reading junk data
      val mapper = new ObjectMapper()
      mapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
      mapper.registerModule(DefaultScalaModule)
      mapper.readValue(jsonString, classOf[Array[ProdCompany]])
    }else {
      null // To avoid reading junk data and retruning the same row
    }
  }

  //this udf-function will replace single quotes with double quotes
  val removeDoubleQuotes = udf( (x:String) => x.replace("'","\""))

  //this udf-function will split string and take second string
  val splitString= udf( (str:String)=> str.split(constant.STRING_SPLIT) (1).trim )

  val concatPrefix= udf( (str:String)=> constant.STRING_APPEND.concat(str))

  def getColList(colStr:String) = colStr.split(",").map(x=>x.trim()).toList

  def dropColumnsFunc(inputDf:DataFrame,colStr:String) = inputDf.drop(colStr)
}
