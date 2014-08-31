Distributed Computing Project: FloodMax Simulator (Async / Sync)
================================================================
Authors: 
    Mark Adams <mla104020@utdallas.edu>
    Anush Krishnamurthy <axk123830@utdallas.edu>
    Mithila Sivakumar <mxs130530@utdallas.edu>

CS 6380.001 (F13), Distributed Computing
University of Texas at Dallas
================================================================

Compilation Instructions
================================================================
The project is written in Java. In order to compile, one must
use the javac application to compile Java source files into
Java class files that are executable by the JVM.

In our case, to simplify this, we have added an Ant-based
build.xml file. If ant is available the following commands
can be used from within the project directory:

ant compile ===> creates *.class files in bin/
ant dist ===> creates a cs6380-simulator.jar file in dist/

Ant is the most common Java build manager (Java equivalent
to C / C++ make) and is installed on cs1.utdallas.edu

Execution Instructions
================================================================

To execute from the JAR file, use the following command:

    java -jar dist/cs6380-simulator.jar {arguments}

To execute from the *.class files, use the following from the
command line:

    java -cp bin/ cs6380simulator.Controller {arguments}

Preferred Execution

The simplest way to execute the application is to perform the following
commands:

    ant dist
    java -jar dist/cs6380-simulator.jar data/sample-input.txt flood-max-acks sync

Arguments
================================================================

This program expects arguments in the following format: 
    input-file algorithm-set

input-file: 
    A file path pointing to a valid input file. The input file
    should contain a single line starting with the number of nodes
    followed by a space separated list of values representing an
    n x n adjacency matrix.

algorithm-set: 
    A value which determines which algorithms for the nodes to run.
    In our case, the program implements FloodMax in two different ways.

    Our implementation uses a modified version of Flood Max with
    acknowledgements to avoid needing to know the diameter.

    An alternative implementation involes using BFS to determine the diameter
    and then execute a normal version of FloodMax. The application was designed
    to allow for either algorithm to be run. However, only the first is
    currently implemented.

    Valid values for algorithm-set can be seen by running the program
    with no arguments.

mode:
    A value which indicates whether the simulator is to run in asynchronous
    or synchronous mode. Valid values are sync or async.

===================================================
EXAMPLE OUTPUT
===================================================

The example_output.txt file demonstrates the output of the program
running against the provided data/sample-input.txt file.
