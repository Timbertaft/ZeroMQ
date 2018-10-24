# Computation_Engine

Once setup, acts as a simulation of both a server and related client environments.

Offers 4 options: 
  Option 1: Computes Pi up to a number of digits inputted by the user.
  Option 2: Computes the Prime numbers between a range of two values provided by the user.
  Option 3: Opens a chat client to communicate with other clients connected to the server.
  Option 4: terminates the client.
 
 In order to use the application, RMI either needs to be manually coded in, or must use a IDE containing the RMI Plugin.
 (This requirement is due to having used the plugin to permit configuration of the simulation.  If you do not have the pluin,
 you can add it to your version of Eclipse by going to http://www.genady.net/rmi/v20/install_e42/ (for Eclipse version 4.x.x))
 
 NOTE: Alternatively, I have modified the code so that it can work without the plugin if VM arguments are inserted.  
 (insert Djava.security.policy=file:<directory path to project>/Computation_Engine/RMI/security.policy/ -Djava.rmi.server.codebase=file:<directory path to project>/Computation_Engine/RMI/bin/ 
 into the ChatServer, ComputationEngine, and Computepi class VM arguments)
 
 Once installed, just run the ComputationEngine as an RMI object (or just run it as normal if VM arguments are used), run the ChatServer in the same way, and then run ComputePi.  Be sure to include "localhost" in the command line arguments as well.
 
 Thank you for looking at the program.
 
This is complete for the basic socketing and RMI requirements of the project.

This branch handles the messaging and broadcast functions by using ZeroMQ functionality through a Maven build that uses the JeroMQ library.  NOTE: There is presently an issue where the broadcast function only works properly when run in debug mode.  Still investigating the cause of this bug.
