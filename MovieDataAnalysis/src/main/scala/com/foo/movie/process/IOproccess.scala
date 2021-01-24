package com.foo.movie.process

import com.foo.movie.base.SparkSessionProvider
import com.foo.movie.constant.MovieConstants
import com.foo.movie.util.UtilityFunction
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{to_timestamp, trim, year}

class IOproccess extends SparkSessionProvider with Specializable{

  private val constant=new MovieConstants()
  private val util=new UtilityFunction()
  private val movCols=util.getColList(constant.MOVIE_COLUMNS)
  private val finalCol=util.getColList(constant.MOVIE_COLUMNS_LST)


/*This method will read XML data of WIKIPEDIA dump and return the  Dataframe
    Input-Arg1 : XML file landing directory/file
    Output : Dataframe (type: DataFrame)
  * */
  def getWikiDataset(path:String): DataFrame ={
    sparkSession.read.format(constant.XML_CLASS)
      .option("rootTag", constant.ROOT_TAG)
      .option("rowTag", constant.ROW_TAG)
      .load(path).drop(constant.LINKS)
  }

 /* This method will read CSV/Text data of Ratings dump and return the Dataframe
  Input-Arg1 : CSV/Text file landing directory/file
  Output : Dataframe (type: DataFrame)
 * */
  def getRatingsDataSet(path:String): DataFrame ={

    val ratings=readInputdataFunc(path,"true","true")
    ratings.withColumnRenamed(constant.MVE_ID,constant.MOVIE_ID)
      .groupBy(constant.MOVIE_ID)
      .avg(constant.RATING)
      .drop(constant.DROP_COL)
  }

/* This method will read CSV/Text data of Links dump and return the Dataframe
  Input-Arg1 : text file landing directory/file
  Output : Dataframe (type: DataFrame)
 * */
  def getLinksDataSet(path:String): DataFrame ={

    val links=readInputdataFunc(path,"true","true")
    import sparkSession.implicits._
    links
      .drop(constant.TMBD_ID)
      .withColumn("new_imdb_id",util.concatPrefix($"imdbId")).drop(constant.IMDB_ID)

  }

/* This method will read CSV/Text data of Ratings dump and return the Dataframe
  Input-Arg1 : CSV file landing directory/file
  Output : Dataframe (type: DataFrame)
 * */
  def getMovieDataSet(path:String): DataFrame ={

    //read movie-Dataset | 1995-12-15
    val movieMetaData =readInputdataFunc(path,"true","true")
    import sparkSession.implicits._
    movieMetaData.select(movCols.head,movCols.tail: _*)
      .withColumn(constant.RATIO,$"Budget".cast("double") / $"Revenue".cast("double") )
      .withColumn("Temp",util.removeDoubleQuotes($"Production_companies"))
      .drop(constant.PRD_NAME)
      .withColumn("Temp_array",util.jsonConverter($"Temp"))
      .drop("Temp")
      .withColumnRenamed("Temp_array",constant.PRD_NAME)
      .withColumn("Year", year(to_timestamp($"Release_Date", constant.DATE_FMT)))
      .filter($"Ratio" .isNotNull)
      .filter($"Year" .isNotNull)
      .filter($"Title" rlike "^([A-Z]|[0-9]|[a-z])+$")
      .filter($"Budget" gt(0)).filter($"Revenue" gt(0))
      .sort($"Ratio".desc)
      .withColumn("Temp",trim($"Title"))
      .drop("Title")
      .withColumnRenamed("Temp","Title")
      .select(finalCol.head,finalCol.tail: _*)
  }

  /* This is genric function to read  CSV/Text data
    Input-Arg1 : Input CSV file landing directory/file
    Output : Dataframe (type: DataFrame)
   * */
  def readInputdataFunc(filePath:String,header:String,schemaRef:String) =sparkSession.read.option("header",header).option("inferSchema",schemaRef).csv(filePath)

}
