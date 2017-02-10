

### License & Copyright ###

This codebase is licensed under the AGPLv3 license (http://www.gnu.org/licenses/agpl-3.0.txt). 

### Download ###

Go to your project directory:

$ git clone https://gitlab.msu.edu/tdr/ERST_Open_Source.git

### Database ###

The database server used: mysql. The database defintion is
info/db/defn. An example of adding a user is shown in info/db/add-user.


### Framework ###

This is a spring framework web app.

### Web Server ###

The web server used: tomcat.

### Maven ###

A build script and a pom.xml file is provided for those using maven.

### Configuration ###

Copy the file info/properties/application.properties to
${HOME}/webapp-properties/submit/application.properties where $HOME is
defined for the web server user.  Define the variables in application.properties.

### Authentication ###

Currently, authentication happens in the kauth method in the Auth
class under the submit.impl package using /usr/bin/kinit.  Modify this
class to implement other authentication methods.
