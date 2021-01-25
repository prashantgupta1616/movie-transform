spark-submit \
--class com.foo.movie.main.MovieMainCls \
--master local[*] --deploy-mode client \
--name PilotProject_MovieData \
--conf "spark.driver.bindAddress=127.0.0.1" \
target/MovieDataAnalysis-1.0-SNAPSHOT-jar-with-dependencies.jar
