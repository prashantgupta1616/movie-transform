package com.foo.movie.main

import com.foo.movie.service.MovieService
import org.apache.log4j.{Level, Logger}

object MovieMainCls {

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.INFO)
    new MovieService().runFunc();

  }

}
