# movie-transform

Tech-stack used :
1. Apache-spark (2.x)
2. Scala (2.11.x)
3. maven for build
5. Docker for Application containerization  and PostGresSQL

Installation requires:
Apache-spark (for ease use spark installation at local or we can create standalone spark cluster using a container)
docker desktop

Why

Spark is a general-purpose compute engine for a big data process, the reasons to choose
        - Easy-to-use APIs for operating on large datasets.
        - Reduced development effort to process variety of data as XML,JSON,Text/CSV
        - A Unified Engine and automatic optimization process, can we run local, standalone and clusters
        - Quick Development and test in various ways


Docker - reduce environment and software specific dependency and due to portability easy to share, apart from cloud-agnostic (as major cloud vendors support container)
and workflow and continuous deployment is pretty convergent at Kubernetes cluster using Helm charts

How to run :

Local machine (Mac/Linux)

1. let us Consider that I've Mac/Linux based machine and spark, maven, java, scala and docker installed locally
2. Let's start the container for Postgres first
      a. go to terminal and run- docker pull Postgres: latest
      b. once first steps complete, you can run below command to start Postgres DB container to store application data, also DB data storage at host system replace angular bracket value
            "docker run --name some-postgres_2 -v </Users/pragupta11/Documents/DB_data>:/var/lib/postgresql/data -e POSTGRES_PASSWORD=mysecretpass -p 5432:5432 postgres:latest"
      c. Post above activity we can verify the container by logging into it by below command
            "docker exec -it some-postgres_2 psql -U postgres"
            #list default databases: \l

2. git clone the code git clone <>
3. execute build.sh using the command "sh build.sh" --> it will build the code, now let's wait to finish build, post that one can find the executable at target directory.
4. execute run.sh using the command "sh run.sh" --> it will run the application code using Spark-submit command, below command will get executed from bash script ;

            spark-submit \
            --class com.foo.movie.main.MovieMainCls \
            --master local[*] --deploy-mode client \
            --name PilotProject_MovieData \
            --conf "spark.driver.bindAddress=127.0.0.1" \
            MovieDataAnalysis-1.0-SNAPSHOT-jar-with-dependencies.jar


5. After completing the job, lets connect to postgres DB using below command using terminal ;
    "docker exec -it some-postgres_db psql -U postgres""
    \c postgres #select default schema
    \gt # list table
    select * from moviedataanalysis



Hadoop cluster
1. Let's Consider that we are using big data/Hadoop cluster having spark, java, scala and Postgres DB cluster
2. git clone the code git clone <>
3. execute build.sh using the command "sh build.sh" --> it will build the code, now let's wait to finish build, post that one can find the executable at target directory.
4. export uber.jar (MovieDataAnalysis-1.0-SNAPSHOT-jar-with-dependencies.jar) to cluster edge/client node where we can submit the spark job.
5 use below command to submit the job and supply/replace the additional parameters for config (executor, memory and so on if require)

spark-submit \
--class com.foo.movie.main.MovieMainCls \
--master local[*] --deploy-mode client \
--name PilotProject_MovieData \
--conf "spark.driver.bindAddress=127.0.0.1" \
MovieDataAnalysis-1.0-SNAPSHOT-jar-with-dependencies.jar
--principal dataee@HDP.SANDBOX.LOCAL \
--keytab /home/prashant.gupta/dataee.keytab \
--files "/home/prashant.gupta/kafka_client_jaas.conf,/home/prashant.gupta/krb5.conf" \
--conf "spark.executor.extraClassPath=/home/prashant.gupta/Test/MovieDataAnalysis-1.0-SNAPSHOT-jar-with-dependencies.jar" \
--conf "spark.executor.extraJavaOptions=-Djava.security.auth.login.config=/home/dataee/kafka_client_jaas.conf,-Djava.security.krb5.conf=/home/dataee/krb5.conf" \

5. After completing the job, let us connect to Postgres DB using below command using terminal ;
    1.ssh to the node where DB connectivity available OR use any UI based db query tool
    2. supply hostname or IP address,user-name, password

    select * from moviedataanalysis


Steps for Azure container instances:
1.GO to Azure Container Instances
2.Create container instance
3 choose 	Docker Hub or other registry and gave URL as "hub.docker.com/postgres"
4. choose the appropriate option and deploy


Steps for Azure Databricks
1.GO to Azure Databricks
2.create a data bricks cluster with required resource
3. once data bricks cluster


automatic optimization process
