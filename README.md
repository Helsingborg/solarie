Solarie
=======

Solr/lucene som gränssnitt mot diarie från Diabas/Ciceron från Visma

Mer info i wiki: https://github.com/Helsingborg/solarie/wiki


Installation
============

Mjukvaran är en Java-applikation som byggs med Maven.

Följande rad bygger en war-fil som kan deployas i valfri servlet-kompatibel webserver.

$ mvn install

Du kan också starta servern på port 8080 direkt från command line:

$ mvn jetty:run

Diabas/Ciceron körs på MS SQL Server vilket kräver att JDBC-drivrutinen är installerad i ditt lokala maven repository.

Tanka hem sqljdbc4 från Microsoft på http://www.microsoft.com/download/en/details.aspx?displaylang=en&id=11774
Packa upp och installera den sedan i ditt lokala maven repository:

$ mvn install:install-file -Dfile=sqljdbc4.jar -DgroupId=com.microsoft.sql -DartifactId=sqljdbc4 -Dversion=4.0 -Dpackaging=jar

Om du använder en annan databas måste du se till att det finns ett beroende till denna i pom.xml
samt i src/main/resources/database.properties peka ut vilken klass det är.