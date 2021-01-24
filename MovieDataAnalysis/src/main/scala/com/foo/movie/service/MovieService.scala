package com.foo.movie.service

import java.util.Properties

import com.foo.movie.base.SparkSessionProvider
import com.foo.movie.constant.MovieConstants
import com.foo.movie.process.IOproccess
import com.foo.movie.util.UtilityFunction
import org.apache.spark.sql.{DataFrame, SaveMode}
import org.apache.spark.sql.functions.concat_ws


class MovieService extends SparkSessionProvider{

private val ioproc=new IOproccess
private val constant=new MovieConstants
private val util=new UtilityFunction()
private val WikiTitle="WikiTitle"
private val properties=new Properties()
  //read
  def runFunc(): Unit ={

    //Read the Input-Data
    val resulDf=inputDatasetReadFun
    writingIntoDB(resulDf,"localhost:5432","postgres","mysecretpass","postgres","MovieDataAnalysis_1")
  }

  //Read Input Data
  def inputDatasetReadFun(): DataFrame ={
    val moviesMetaInfo=ioproc getMovieDataSet("/Users/pragupta11/Desktop/TrueLayer/archive/movies_metadata.csv")
    val moviesLinks=ioproc getLinksDataSet("/Users/pragupta11/Desktop/TrueLayer/archive/links.csv")
    val movieRating=ioproc getRatingsDataSet("/Users/pragupta11/Desktop/TrueLayer/archive/ratings.csv")
    val wikiLinks=ioproc getWikiDataset("/Users/pragupta11/Desktop/TrueLayer/enwiki-latest-abstract.xml")
//    val wikiLinks=ioproc getWikiDataset("/Users/pragupta11/Desktop/TrueLayer/convertjson.xml")


    //Join the Data | [Ruby Films, Pathé, Film4]
    val MovieWithLinks=joinDataframe(moviesMetaInfo,"imdb_id",moviesLinks,"new_imdb_id","inner")

    import sparkSession.implicits._
    //Join movieLinked data with Ratings
    val movieWithRating=joinDataframe(MovieWithLinks,constant.MVE_ID,movieRating,constant.MOVIE_ID,constant.INNER_JOIN)
      .drop(constant.MOVIE_ID,constant.IMDB_id,constant.MVE_ID,"new_imdb_id")
      .withColumnRenamed("avg(rating)",constant.MOVIE_RATING)
      .withColumn("ProductionCompanies",concat_ws("|",$"name"))
      .drop("name")

    //movieWithRating.show(false)
    println("movie joined cnt =>"+movieWithRating.count())

    //Check with Wiki data

    val wikiDf=wikiLinks
      .withColumn(WikiTitle,util.splitString($"title")).drop("title").distinct()

    println("wiki rec count => "+wikiDf.count())


    val finalDataf=joinDataframe(movieWithRating,"Title",wikiDf,WikiTitle,constant.INNER_JOIN)
        .withColumnRenamed("url","WikiPageUrl")
        .withColumnRenamed("abstract","WikiAbstract")
        .drop(WikiTitle)


    finalDataf.show(false)
    println("COUNT2==>" + finalDataf.count())
    finalDataf.coalesce(1)
          .write
          .option("header","true")
          .option("sep",",")
          .mode("overwrite")
          .csv("/Users/pragupta11/Desktop/riskandcontrol/MovieDataAnalysis/src/main/resources/Finalout")
          return finalDataf.dropDuplicates("Title")

  }


  def writingIntoDB(df:DataFrame,hosturl:String,user:String,pwd:String,dbName:String,tableName:String): Unit ={

    println("==== JDBC starts========")
    properties.setProperty("user",user)
    properties.setProperty("password",pwd)

    try{
      df.write.mode(SaveMode.Append).option("driver",constant.DRIVER)
        .jdbc(s"jdbc:postgresql://$hosturl/$dbName",tableName,properties)
    } catch {
      case e : Exception=>e.printStackTrace()
    }finally {
      sparkSession.stop()
      println("=========JDBC Completed========")
    }

  }

  def joinDataframe(df1:DataFrame,col1:String,df2:DataFrame,col2:String,joinType:String): DataFrame ={
    df1.join(df2,df1(col1)===df2(col2),joinType)
  }


}
