package com.foo.movie.constant

import com.foo.movie.base.SparkSessionProvider

class MovieConstants extends Serializable with SparkSessionProvider {
  val SPARK_CONF=sparkSession.conf
  val MOVIE_COLUMNS="Title,Production_companies,Budget,Release_Date,Revenue,Imdb_id"
  val MOVIE_COLUMNS_LST="Title,Budget,Year,Revenue,Ratio,Imdb_id,Production_companies.name"
  val BUDGET="Budget"
  val REVENUE="Revenue"
  val PRD_NAME="Production_companies"
  val STRING_SPLIT=":"
  val RATIO="Ratio"
  val STRING_APPEND="tt"
  val TMBD_ID="tmdbId"
  val IMDB_ID="imdbId"
  val IMDB_id="Imdb_id"
  val DROP_COL="timestamp,userId"
  val MOVIE_ID="movie_Id"
  val RATING="rating"
  val MVE_ID="movieId"
  val LINKS="links"
  val INNER_JOIN="inner"
  val MOVIE_RATING="Rating"
  val DATE_FMT="yyyy-MM-dd"
  val XML_CLASS=SPARK_CONF.get("spark.input.xml.driver")
  val ROOT_TAG=SPARK_CONF.get("spark.input.xml.root.tag")
  val ROW_TAG=SPARK_CONF.get("spark.input.xml.row.tag")

  val DB_HOST_URL=SPARK_CONF.get("spark.input.jdbc.host.url")
  val DRIVER=SPARK_CONF.get("spark.input.jdbc.driver")
  val DB_USER=SPARK_CONF.get("spark.input.jdbc.user.name")
  val DB_PWD=SPARK_CONF.get("spark.input.jdbc.user.password")
  val DB_NAME=SPARK_CONF.get("spark.input.jdbc.db.name")
  val TABLE_NAME=SPARK_CONF.get("spark.input.jdbc.table.name")

  val MOVIE_INPUT_PATH=SPARK_CONF.get("spark.input.staging.movie.landing.path")
  val LINKS_INPUT_PATH=SPARK_CONF.get("spark.input.staging.links.landing.path")
  val RATINGS_INPUT_PATH=SPARK_CONF.get("spark.input.staging.ratings.landing.path")
  val WIKI_INPUT_PATH=SPARK_CONF.get("spark.input.staging.wikipedia.landing.path")



}
