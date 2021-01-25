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
    val inputTranformedDf=inputDatasetReadFun

    //Ingest the Data into POSTGRES
    writingIntoDB(inputTranformedDf,constant.DB_HOST_URL,constant.DB_USER,constant.DB_PWD,constant.DB_NAME,constant.TABLE_NAME)
  }

  //Read Input Data
  def inputDatasetReadFun(): DataFrame ={
    val moviesMetaInfo=ioproc.getMovieDataSet(constant.MOVIE_INPUT_PATH)
    val moviesLinks=ioproc.getLinksDataSet(constant.LINKS_INPUT_PATH)
    val movieRating=ioproc.getRatingsDataSet(constant.RATINGS_INPUT_PATH)
    val wikiLinks=ioproc.getWikiDataset(constant.WIKI_INPUT_PATH)


    //Join the Movie MetaData with Links Data | [Ruby Films, Pathé, Film4] | IMDB_ID is the matching column to get MovieID
    val MovieWithLinks=joinDataframe(moviesMetaInfo,"imdb_id",moviesLinks,"new_imdb_id",constant.INNER_JOIN)

    import sparkSession.implicits._
    /*Join movieLinked data with Ratings
    Join the movieLinked with Ratings Data | MOVIE_ID is the matching column to get Rating of the user*/
    val movieWithRating=joinDataframe(MovieWithLinks,constant.MVE_ID,movieRating,constant.MOVIE_ID,constant.INNER_JOIN)
      .drop(constant.MOVIE_ID,constant.IMDB_id,constant.MVE_ID,"new_imdb_id")
      .withColumnRenamed("avg(rating)",constant.MOVIE_RATING)
      .withColumn("ProductionCompanies",concat_ws("|",$"name"))
      .drop("name")

    /*Check  Wiki data, split the Title string and extract only title name (WIKIPEDIA: Speed --> Speed) to join with movie data
    Join movieWithRating data with Wiki data to get Wiki url links and Abstarct*/
    val wikiDf=wikiLinks
      .withColumn(WikiTitle,util.splitString($"title")).drop("title").distinct()

    /*Final Dataframe after require clean-up and transformation and ready to ingest in DB as its tabular form*/
    joinDataframe(movieWithRating,"Title",wikiDf,WikiTitle,constant.INNER_JOIN)
        .withColumnRenamed("url","WikiPageUrl")
        .withColumnRenamed("abstract","WikiAbstract")
        .drop(WikiTitle)
  }

  /*This method will join two dataset based on the matching colum
    Input-Arg1 : Dataframe | Input-Arg2 : DB host url
    Input-Arg3 : UserName | Input-Arg4 : DB_Password
    Input-Arg5 : DB Schema name | Input-Arg6 : TAble name where data wil get ingested
    Output : Unit
* */
  def writingIntoDB(df:DataFrame,hosturl:String,user:String,pwd:String,dbName:String,tableName:String): Unit ={
    properties.setProperty("user",user)
    properties.setProperty("password",pwd)

    try{
      df.write.mode(SaveMode.Append).option("driver",constant.DRIVER)
        .jdbc(s"jdbc:postgresql://$hosturl/$dbName",tableName,properties)
    } catch {
      case e : Exception=>e.printStackTrace()
    }finally {
      sparkSession.stop()
    }

  }

  /*This method will join two dataset based on the matching colum
  Input-Arg1 : Dataframe-1 and col1
  Input-Arg2 : Dataframe-2 and col2
  Output : Joined Dataframe (type: DataFrame)
  * */
  def joinDataframe(df1:DataFrame,col1:String,df2:DataFrame,col2:String,joinType:String): DataFrame ={
    df1.join(df2,df1(col1)===df2(col2),joinType)
  }


}
