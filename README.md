
Solarie
=======

Version 0.0.1

Sökmotor för Visima Diabas/Ciceron-databaser.
Java 7 med Prevayler och Lucene.


Installera
==========

Börja med en helt ny Debian Wheezy Linux med användaren 'solarie'.

Tanka hem sqljdbc4 från Microsoft på http://www.microsoft.com/download/en/details.aspx?displaylang=en&id=11774


    solarie@solarie:~$ su
    Password:
    root@solarie:/home/solarie# cat > /etc/environment
    export LANGUAGE=en_US.UTF-8
    export LANG=en_US.UTF-8
    export LC_ALL=en_US.UTF-8
    ^C
    root@solarie:/home/solarie# exit
    exit
    solarie@solarie:~$ su
    Password:
    root@solarie:/home/solarie# apt-get install git-core openjdk-7-jdk maven2
    root@solarie:/home/solarie# exit
    exit
    
    solarie@solarie:~$ mvn install:install-file \
        -Dfile=/home/solarie/Downloads/microsoft-sql-jdbc-driver/sqljdbc4.jar \
        -DgroupId=com.microsoft.sql \
        -DartifactId=sqljdbc4 \
        -Dversion=4.0 \
        -Dpackaging=jar

    solarie@solarie:~$ git clone https://github.com/Helsingborg/solarie.git server
    solarie@solarie:~$ cd server
    solarie@solarie:~/server$ chmod 4755 start.sh
    solarie@solarie:~/server$ chmod 4755 stop.sh
    solarie@solarie:~/server$ chmod 4755 run.sh
    solarie@solarie:~/server$ mvn install -Dtest=true


    solarie@solarie:~$ su
    Password:
    root@solarie:/home/solarie# cat >> /etc/rc.local
    rm /home/solarie/server/server.pid
    /home/solarie/server/start.sh
        ^C


Konfigurera
===========

Registrera alla Diabas/Ciceron-databaser i src/main/resources/diarier.json

    
    [
      {
        "jdbcURL" : "jdbc:sqlserver://192.168.1.120\\SQLServer;database=extern;user=sa;password=sa",
        "namn" : "Publikt"
      }, {
        "jdbcURL" : "jdbc:sqlserver://192.168.1.120\\SQLServer;database=inter;user=sa;password=sa",
        "namn" : "Internt"
      }, {
        ...
    ]


Köra
====

Har du följt alla instruktioner så startar tjänsten när datorn bootat klart.

För manuell start och stopp:

    solarie@solarie:~$ cd server
    solarie@solarie:~/server$ ./start.sh
    solarie@solarie:~/server$ ./stop.sh


