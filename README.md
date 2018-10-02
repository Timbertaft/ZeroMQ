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
 
 Once installed, just run the Compute Engine as an RMI object and then run the PI Computation Client configuration.
 (if this configuration is missing, run a new RMI configuration with the project name RMI and set command line arguements to "localhost 100" )
 This will set two command line values, one for the initial start of the server to reference the localhost IP and one for an initial value
 to compute Pi.  (these command line arguments may be replaced with codified values in the future to remove the possibility for user error.)
 
 Thank you for looking at the program.
 
 At the time of writing this Readme, the Chat client is still a WIP.
