# movie-transform

Tech stack used
  1. Apache-spark (2.x)
  2. Scala (2.11.x)
  3. maven
  5. Docker for Application containerization  and Postgres

**Installation requires**

1.  Apache-spark (for ease use spark installation at local or we can create standalone spark cluster using a container)
2.  Docker desktop

**Why Spark And Docker**

**Apache Spark** is a general-purpose compute engine for a big data process, the reasons to choose
        - Easy-to-use APIs for operating on large datasets.
        - Reduced development effort to process variety of data as XML,JSON,Text/CSV
        - A Unified Engine and automatic optimisation process, can be run locally, standalone and clusters
        - Quick Development and test in various ways including BDD's execution by Spark

**Docker**
  - Reduce environment and software specific dependency and due to portability easy to share, apart from that its cloud-agnostic (as major cloud vendors 
  - Support containers and container runtime)
  - Orchestration and deployment is pretty convergent at Kubernetes cluster using Helm charts

**How to run** :

**Local machine (Mac/Linux)**

1. Let's Consider that I've Mac/Linux based machine and inlined software as a spark, maven, java, scala and docker installed locally.
2. Let's start the container for Postgres first
      a. go to the terminal and run- docker pull Postgres:latest
      b. once first steps complete, you can run below command to start Postgres DB container to store application data, also if you want to change mount volume of DB into the host machine, in that case, replace bracket value by host machine's host local location else remove bracket value then by default Postgres store the data at var/lib/postgresql/data
            "docker run --name some-postgres_2 -v </Users/pragupta11/Documents/DB_data>:/var/lib/postgresql/data -e POSTGRES_PASSWORD=mysecretpass -p 5432:5432 postgres:latest"
      c. Post above activity we can verify the container by Connection into it by below command
            "docker exec -it some-postgres_2 psql -U postgres"
            #list default databases: \l

3. git clone the code 
    git clone git@github.com:prashantgupta1616/movie-transform.git
4. execute build.sh using the command "sh build.sh" --> it will build the code, now let's wait to finish build, post that one can find the executable at target directory.
5. execute run.sh using the command "sh run.sh" --> it will run the application code using Spark-submit command, below command will get executed from bash script ;

            spark-submit \
            --class com.foo.movie.main.MovieMainCls \
            --master local[*] --deploy-mode client \
            --name PilotProject_MovieData \
            --conf "spark.driver.bindAddress=127.0.0.1" \
            --properties-file job.conf
            target/MovieDataAnalysis-1.0-SNAPSHOT-jar-with-dependencies.jar


5. After completing the job, lets connect to postgres DB using below command using terminal ;
    "docker exec -it some-postgres_db psql -U postgres""
    \c postgres #select default schema
    \gt # list table
    select * from moviedataanalysis



**Hadoop cluster**
  1. Let's Consider that we are using big data/Hadoop cluster having spark installed with java, scala and Postgres DB cluster
  2. git clone the code as;
  3. execute build.sh using the command "sh build.sh" --> it will build the code, now let's wait to finish build, post that one can find the executable at target directory.
  4. export uber.jar (MovieDataAnalysis-1.0-SNAPSHOT-jar-with-dependencies.jar) and property file to cluster edge/client node where we can submit the spark job.
  5. use below command to submit the job and supply/replace the additional parameters for config (executor, memory and so on if require)

    spark-submit \
    --class com.foo.movie.main.MovieMainCls \
    --master local[*] --deploy-mode client \
    --name PilotProject_MovieData \
    --conf "spark.driver.bindAddress=127.0.0.1" \
    --properties-file job.conf
    --principal dataee@HDP.SANDBOX.LOCAL \
    --keytab /home/prashant.gupta/dataee.keytab \
    --files "/home/prashant.gupta/kafka_client_jaas.conf,/home/prashant.gupta/krb5.conf" \
    --conf "spark.executor.extraClassPath=/home/prashant.gupta/Test/MovieDataAnalysis-1.0-SNAPSHOT-jar-with-dependencies.jar" \
    --conf "spark.executor.extraJavaOptions=-Djava.security.auth.login.config=/home/dataee/kafka_client_jaas.conf,-Djava.security.krb5.conf=/home/dataee/krb5.conf" \
    MovieDataAnalysis-1.0-SNAPSHOT-jar-with-dependencies.jar

5. After completing the job, let us connect to Postgres DB using below command using if using Putty/terminal ;
    1. ssh to the node where DB connectivity available OR use any UI based db query tool
    2. supply hostname or IP address,user-name, password and connect then post below query

    \c postgres
    select * from postgres.moviedataanalysis


<!-- Using Cloud Platform -->

**Microsoft AZURE **

option-1
**Using Azure container instances**

Steps for Azure container instances:
1.  GO to Azure Container Instances
2.  Create container instance
3.   choose 	Docker Hub or other registry and gave URL as "hub.docker.com/postgres"
4.  choose the appropriate option and deploy
5. build a docker image with source code with spark image and upload the image and tag it to registry to ACR as ;
      - Create an Azure Container Registry (ACR) instance
      - az acr login --name myregistry
      - docker login myregistry.azurecr.io
      - docker tag moviedataanalysis myregistry.azurecr.io/data/moviedataanalysis
      - docker push myregistry.azurecr.io/data/moviedataanalysis:v1


option-2
**Using Azure Databricks cluster service**
Steps for Azure Databricks
1.GO to Azure Databricks, create an instance of Databricks service.
2.create a data bricks cluster with required resource
3. once data bricks cluster up and running, upload the jar file and data to azure data lakes, change required config and paths to run at Azure



Google cloud platform
option-1

This approach is very much similar to Azure only services name will be different 

1.Create to google registry service
2.tag build docker source code image to the registry
3.Execute image either on compute instance and the best way is to deploy it on Kubernetes cluster

NOTE: These above steps can be automated using CI/CD pipelines tool like Jenkins/Spinker/Azure deploy


**Testing Approach**
1. Add and create test data for movie, rating, links, Wikipedia.
2. Ingest this data in the same directory of each dataset location
3. Run the movieDataAnlsysis spark job again and let's wait to ingest this data in to Postgres DB
4. Login into Postgres DB and query the specific data posted for the testing and validate the result that will help for the data correctness

All the above steps can be automated using a cucumber based BDD approach, which will take the data correctness in terms of various scenarios.
