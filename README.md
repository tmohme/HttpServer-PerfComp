# HttpServer-PerfComp
A pretty rough performance comparison of a Tomcat- vs. Netty-based HTTP server.

The Netty-based server is essentially the HttpSnoopServer from the
[Netty examples](https://github.com/netty/netty/tree/4.1/example/src/main/java/io/netty/example/http/snoop).

The Tomcat-based server is implemented using a very simple spring-boot project. It's functionality is similar to the Netty-based server: It returns the sent HTTP RequestHeader values in a very simple Html document.

The requests are generated with [gatling](http://gatling.io/), a load-test tool bases on Scala, Akka and Netty.

**Attention:** The test-code ist not very robust. If you overload your system, the load-test might explode due to an insufficient number of open files allowed.

## What happens in the load-test?
The load-test is described by the file `gatling/simulations/HttpServerPerfComp.scala`.

We create two `scenario` - one for each server we want to visit.
In each of these scenarios we repeat sending a `GET` request to the corresponding server **without any pauses**.

We fire these scenarios at the servers starting with `minUsersPerSec` and linearly increase the firing rate up to `maxUsersPerSec` which we then hold for `peakDuration`.  

## Running it locally
This setup has the advantage to be the easiest.  
At the same time it has the disadvantage that both servers and the load generator share the same resources and we have to use additional tools to monitor the resource consumption of each server.

### Requirements
* Java 8
* Installed gatling, set environmant variable `GATLING_HOME` to the installation directory.

### Process
1. git clone this repository.  
   All following commands assume this project directory as working directory.
2. build with `./gradlew installDist`
3. open a new terminal to run the Netty server
4. in the new terminal run the Netty server with `./runNetty.sh`
5. open a new terminal to run the SpringBoot/Tomcat server
6. in the new terminal run the SpringBoot/Tomcat server with `./runSpringBoot.sh`
7. switch back to the first terminal and run the load-test with `./runGatling.sh`. The scipt tells you every so often what currently happens. 
8. You will find a report with the results in `build/reports/httpserverperfcomp-*`.

You can stop the servers with `Ctrl-C` in their terminals.

In case you overloaded your systems (and the test exploded), you should restart both servers and reduce the generated load e.g. by decreasing `maxUsersPerSec`

You will notice, that the numbers for both servers look very similar.
What these reports don't tell you, is that the Netty server consumes **far less CPU cycles and memory** than the SpringBoot server.
This can easily be observed with tools like `jvisualvm` or `jmc`.

On my machine (3.5 GHz Intel Core i7, 8GB RAM), I can run up to 100 users/s and still getting response times below 2ms for 90% of the requests (both servers).  
The test ran 1.530.000 requests within 92 seconds.
The Netty server consumes ~6% CPU and uses ~30MB memory.  
The SpringBoot server consumes ~26% CPU and uses ~100MB memory.

## Running it with Vagrant & VirtualBox
This setup somewhat more complicated but has the advantage that each server's resources can be limited via the VM specs.  
Therefore the measured response times are (more or less) directly comparable.  
This will be good enough for a rough assessment of each server's capabilities.

### Requirements
* Vagrant (tested with version 1.8.4)
* Vagrant plugin `vagrant-vbguest` (install with `vagrant plugin install vagrant-vbguest`)

### Process
1. git clone this repository.  
   All following commands assume this project directory as working directory.
2. build with `./gradlew build`
3. run `vagrant up` to start the servers.
4. run the load-test with `./runGatling.sh`. The scipt tells you every so often what currently happens. 
5. You will find a report with the results in `build/reports/httpserverperfcomp-*`.

On my machine, I can run up to 40 users/s with this setup.  
While Netty still shows very good response times (75% <2ms, 95% <4ms, 99% <6ms, max. 24ms), SpringBoot degrades significantly (75% <10ms, 95% <16ms, 99% <23ms, max. 115ms).
