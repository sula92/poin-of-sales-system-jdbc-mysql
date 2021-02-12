#### ABOUT THE PROJECT:

Java EE Application that has been developed in order to manage customers, orders, to allow its customers to order items online.

#### TECHNOLOGY STACK:

- [x] Java
- [x] MySql
- [x] Web API
- [x] Maven
- [x] Web Servlet 

#### PREREQUISITES:

You need to install- 

- [x] JavaSE8 
- [x] MySql 
- [x] Maven 
- [x] Postman or Insomnia 
- [x] An ide (intellij,Eclipse or Netbeans) 
- [x] TomCat

Fisrt of all you need to setup the java enviroment on your pc. Below are the guidence for setup the java enviroment in your pc.

What is JAVA_HOME? By convention, JAVA_HOME is the name of an environment variable on the operating system that points to the installation directory of JDK (Java Development Kit) or JRE (Java Runtime Environment) – thus the name Java Home. For example: 1 JAVA_HOME = c:\Program Files\Java\jdk1.8.0_201

Why is JAVA_HOME needed? To develop Java applications, you need to update the PATH environment variable of the operating system so development tools like Eclipse, NetBeans, Tomcat… can be executed because these programs need JDK/JRE to function. So the PATH environment variable should include JAVA_HOME:

PATH = Other Paths + JAVA_HOME Other paths are set by various programs installed in the operating system. If the PATH environment variable doesn’t contain a path to JRE/JDK, a Java-based program might not be able to run. For example, typing java in the command prompt showing this error: 1 'java' is not recognized as an internal or external command, operable program or batch file. error java command.

How to set JAVA_HOME on Windows 10 Here are the visual steps to properly set value for the JAVA_HOME and update the PATH environment variables in order to setup Java development environment on your computer:

Firstly, you need to identify the Java home directory, which is typically under C:\Program Files\Java directory.

Open the System Environment Variables dialog by typing environment in the search area on Start menu. Click the suggested item Edit the system environment variables: The System Properties dialog appears, click the button Environment Variables.

3.Create the JAVA_HOME environment variable by clicking the New button at the bottom. In the New System Variable form, enter the name and value.

Click OK, and you will see the JAVA_HOME variable is added to the list.

4.Update the PATH system variable. In the Environment Variables dialog, select the Path variable and click Edit:

Then in the Edit environment variable dialog, double click on the empty row just below the last text line, and enter %JAVA_HOME%\bin.

The percent signs tell Windows that it refers to a variable – JAVA_HOME, and the \bin specifies the location of java.exe and javac.exe programs which are used to run and compile Java programs, as well as other tools in the JDK. Click OK button to close all the dialogs, and you’re all set. Now you can open Eclipse or NetBeans to verify. Or open a command prompt and type in javac –version.

So now if you can see the correct java version that you installed.

Installing Maven:

There after you have to install maven in your pc as well. So please follow the guidens given below in order to install the mave in your project.

JDK and JAVA_HOME Make sure JDK is installed, and the JAVA_HOME environment variable is configured.

Download Apache Maven Visit Maven official website, download the Maven zip file, for example, apache-maven-3.6.3-bin.zip.

Add MAVEN_HOME system variable Go to the Environment variables -> System variables section in your windows 10 machine. Add a MAVEN_HOME system variable, and point it to the Maven folder.

For Example: variable name : MAVEN_HOME variable value : c:\learn_java\apache-maven-3.6.3

Add %MAVEN_HOME%\bin To PATH In system variables, find PATH, clicks on the Edit... button. In “Edit environment variable” dialog, clicks on the New button and add this %MAVEN_HOME%\bin

Verification Done, start a new command prompt, type mvn –version

That's all. The Apache Maven is installed successfully on Windows.

Now In order to run the database to do DB operations you have to install MySql on your pc as well.

Installing MYSQL:

Step 1:

Download the latest Mysql Community server from MySQL official website.If the version differs you no problem the installation steps will be the same.

Step 2:

It will show you Generally Available (GA) Releases. Where we can see two different installers, one is a web community installer which comes as a little file and another one is MySQL installer community. Click the Download button on the second one (mysql-installer-community).

Step 3:

It will ask your MySQL credentials to download the .msi file. If you have your credentials, you can log in or else if you wish to sign up now you can click on the green coloured signup button. If you are not interested in login or sign up for now, you can directly go and click on No thanks, just start my download option. It will download selected MySQL for you on your local machine.

Step 4:

Go to your downloads folder where you can see the mysql-installer-community file, right click on that file and click Install option.

Step 5:

This window configures the installer, in the middle, it may ask you for permissions to change your computer settings or firewall confirmation, you can accept and then it will take a few seconds to configure the installer.

Step 6:

Read the license agreement and accept the license terms.

Step 7:

This window provides you to set up different types of MySQL installations. You can set up Mysql in 5 different types as provided below. Now I am selecting the Developer Default as I am a developer so that I need all the products which help my development purposes. Click on Next.

Step 8:

Based on your Windows configuration, it may prompt you like “One or more product requirements have not been satisfied”. You can just click on YES.

Step 9:

Click on Execute.

Step 10:

Upon execution of the previous step, the installer grasps all recommended products in place and asking for our approval to execute the product installation process. Click on Execute.

*Since this is only a backend you should have installed a rest client like postman or insomnia. Though you can do those stuffs just using the browser it is hard to do so.

Runnig the Project:

Please Follow below steps to run the project.

*In order to run the project you must have an ide like intellij, eclipse or netbeans in your pc. You can download eclipse & netbeans for free and you can buy the intellij enterprise edition from their official site.

*Now Implort the project to the ide.

*Now before you run the project make sure to start the server.

*Run the Project using ide.

*Use the installed rest client(postman or insomnia) in order to send the REST Requests and to do the crud operations.

#### RODMAP:

The UI will be created soon in vueJS or ReactJs.


#### SUPPORT:

Sulakkhana Dissanayake - sulkkanaid@gmail.com Project Link: https://github.com/sula92/Daddy-s-Coffee-REST


#### LICENSE:

Distributed under the MIT License. See LICENSE for more information.
 
