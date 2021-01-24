package com.foo.movie.constant

class MovieConstants extends Serializable  {
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
  val XML_CLASS="com.databricks.spark.xml"
  val RATING="rating"
  val MVE_ID="movieId"
  val LINKS="links"
  val INNER_JOIN="inner"
  val MOVIE_RATING="Rating"
  val DATE_FMT="yyyy-MM-dd"
  val DRIVER="org.postgresql.Driver"
  val PORT="5432"

  val ROOT_TAG="feed"
  val ROW_TAG="doc"

//  val ROOT_TAG="root"
//  val ROW_TAG="row"

}
