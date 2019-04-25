# activepivot-lx
ActivePivot sample project to demonstrate connectivity with the LeanXcale operational database.

# Setting up LeanXcale
Docker is the simplest way to run a LeanXcale cluster in your development environment. You can obtain the docker image from LeanXcale. Let's add the `lx` tag to that docker image so that we can quickly refer to it.

Run the lx image that way: `docker run -d -v D:/data:/data -p 1529:1529 lx`
It will run lx in detached mode, publish container port 1529 to the host port 1529 so that applications running in the host can communicate with the database. It will also mount the `D:\data`directory of the host (here a windows laptop with a D: drive) as the `/data` directory in the container. At least 4GB of memory should be available for the lx container.

Then connect into the lx container: `docker exec -it CONTAINER /bin/bash` where `CONTAINER` is the id of your running container.

From within the container setup the environment by running `. env.sh` and then launch LeanXcale by running `. runLeanXcale.sh`

To check that the components of the cluster are running run the lx console: `LX-BIN/bin/lxConsole` and type command `3`. You should see a list of started components.


# Setting up ActivePivot
ActivePivot is a java project packaged with maven. You can obtain the necessary maven dependencies from ActiveViam. To connect to LeanXcale with jdbc you need additional dependencies to obtain from LeanXcale.

Copy the LeanXcale driver dependencies into your maven repository with this layout:
```
	<!-- LeanXCale elastic driver -->
	<dependency>
		<groupId>com.leanxcale</groupId>
		<artifactId>elasticdriver</artifactId>
		<version>0.94-SNAPSHOT</version>
	</dependency>
	<dependency>
		<groupId>com.leanxcale</groupId>
		<artifactId>i18n</artifactId>
		<version>0.95-SNAPSHOT</version>
	</dependency>
```

Then you can use util classes from the ActivePivot project to create tables and populate them with a small sample dataset:
- `com.activeviam.lx.db.CreateTables` create tables
- `com.activeviam.lx.db.DropTables` drop tables
- `com.activeviam.lx.db.LoadSampleData` load a small sample dataset

When the tables are created and filled with a dataset, you can start the ActivePivot server itself. ActivePivot will load the data from the database and create an in-memory cube ready to be analyzed and visualized.

To start the ActivePivot application, launch `com.activeviam.lx.ActivePivotLXServer` directly from your IDE. (or alternatively build the project with maven into a .war file, and deploy the .war file in a servlet container such as Apache Tomcat). The project embbeds an instance of the ActiveUI web frontend so you can connect by default to `http://localhost:9090/` and start visualizing data and making dashboards.

# Generating a large dataset
The ActivePivot project comes with a simple portfolio risk management model (trades, products, risks) and a data generator. You can generate a dataset by running `com.activeviam.lx.generator.DataGenerator` and changing options in `src/main/resources/data.properties`.

Copy the generated csv files to the drive shared with the lx docker container, and load them into LeanXCale




