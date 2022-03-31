# ${artifactId}
This component is a base sample to be used when creating a new component, the following topics will 
guide the steps needed to take for the creation of a new component, each one of them is important so 
follow them carefully.

## Table of Contents
- [${artifactId}](#${artifactId})
  - [Table of Contents](#table-of-contents)
  - [1 - Changing the component's name](#1---changing-the-components-name)
    - [1.1 - Application.kt](#11---applicationkt)
    - [1.2 - pom.xml](#12---pomxml)
    - [1.3 - kubernetes (folder)](#13---kubernetes-folder)
    - [1.4 - docker-compose.yml](#14---docker-composeyml)
    - [1.5 - Dockerfile](#15---dockerfile)
    - [1.6 - pipeline-prod.yml & pipeline-dev.yml](#16---pipeline-prodyml--pipeline-devyml)
  - [2 - Removing Spring Native (AOT)](#2---removing-spring-native-aot)
  - [3 - Dependencies](#3---dependencies)
    - [3.1 - Update a dependency](#31---update-a-dependency)
    - [3.2 - Add a new dependency](#32---add-a-new-dependency)
  - [4 - Architecture](#4---architecture)
    - [4.1 - gateways](#41---gateways)
      - [4.1.1 - controllers](#411---controllers)
      - [4.1.2 - externalinterfaces](#412---externalinterfaces)
      - [4.1.3 - repositories](#413---repositories)
    - [4.2 - usecases](#42---usecases)
      - [4.2.1 - ports](#421---ports)
    - [4.3 - domains](#43---domains)
    - [4.4 - config](#44---config)
  - [5 - Creating tests](#5---creating-tests)
  - [6 - Booting Up](#6---booting-up)
## 1 - Changing the component's name
To start developing your component, you'll first need to change this base
component's name. It is referenced in many files, so after changing the 
folder's name, you'll also need to make the same change in them. 

These are the references to the component's name:

### 1.1 - Application.kt
```/src/main/kotlin/${packageInPathFormat}/${contextFolderName}/${mainClassName}Application.kt ```

Your main class should have its name changed to something like: **YourComponentsName**Application.

### 1.2 - pom.xml
Inside the file, in the **&lt;artifactId>** and **&lt;name>** tag.

(Although not obligatory, the description of the component should also be changed)

### 1.3 - kubernetes (folder)
All file names in this folder should contain the component's name.

### 1.4 - docker-compose.yml
Inside the file, the following values should be changed:

**services.cerberus.container_name.**
**services.cerberus.image.**
**services.cerberus.networks.**

### 1.5 - Dockerfile
Inside the file, in the last argument of **CMD** and the .jar files in the line before the 
**EXPOSE** command. 

### 1.6 - pipeline-prod.yml & pipeline-dev.yml
Inside both files, change the values in **env.APP_NAMESPACE** and **env.APP_NAME**.

## 2 - Removing Spring Native (AOT)
This base component comes with Spring Native installed, if for any reason your
component needs it removed, you can do it by following these steps:

On the **pom.xml** file, ***delete*** the following lines:

```xml
<spring.native.version>0.10.6</spring.native.version>
```
```xml
<dependency>
	<groupId>org.springframework.experimental</groupId>
	<artifactId>spring-native</artifactId>
	<version>${spring.native.version}</version>
</dependency>
```
```xml
<configuration>
	<image>
		<builder>paketobuildpacks/builder:base</builder>
		<env>
			<BP_NATIVE_IMAGE>true</BP_NATIVE_IMAGE>
		</env>
	</image>
</configuration>
```
```xml
<plugin>
    <groupId>org.springframework.experimental</groupId>
    <artifactId>spring-aot-maven-plugin</artifactId>
    <version>${spring.native.version}</version>
    <executions>
        <execution>
            <id>test-generate</id>
            <goals>
                <goal>test-generate</goal>
            </goals>
        </execution>
        <execution>
            <id>generate</id>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## 3 - Dependencies
The addition of dependencies should be made through Maven. All
dependencies are listed in the **pom.xml** file, such as many properties of the project. 

For every change made involving Maven you should run the following command on the terminal, which 
will reload all dependencies and run all tests currently on your component. 

```shell
$ ./mvnw clean install spring-aot:generate
```

*PS: The command used above depends on spring native. In case your component doesn't use Spring Native,
you should use the following command:*

```shell
$ ./mvnw clean install
```

### 3.1 - Update a dependency
With the dependency installed, if you wish to update any of them to the latest version, you search
for them through [Maven's Website](https://mvnrepository.com), searching using the artifactId.
It is **extremely important** that you always keep
dependencies up to date, in their latest stable release. Keeping a dependency outdated could expose
your component to vulnerabilities, which could compromise not only your component but many backend 
functionalities.

### 3.2 - Add a new dependency
To add a dependency, just insert them in the **&lt;dependencies>** tag. A dependency
has the following format: 

```xml
<dependency>
	<groupId> ORGANIZATION OF THE DEPENDENCY </groupId>
	<artifactId> DEPENDENCY ID </artifactId>
	<version> VERSION OF THE DEPENDENCY </version>
</dependency>
```

## 4 - Architecture
Our components' architecture is based on Clean-Arch. Although not following it
completely, most of the concepts are the same.

Under the component's main folder we have 4 divisions:

### 4.1 - gateways
Classes responsible for communicating with outside services, which can be
the front-end or maybe another system (e.g. Redis). It contains the DTOs and the
logic for service-specific implementations.
Bellow is the defined sub-packages that will be in gateway package.

*NOTE: that all cases are already defined, in this case you can bring new options to be discussed in 
the backend chapter.*

#### 4.1.1 - controllers
This package will hold all controllers and rest endpoints that the component provides. All classes in 
this package will end with "Controller", e.g. "FooterController", and all communication through 
controllers must be done by a DTO (data transfer object). So you shall create a package called *dto* 
and put all dtos used by the controllers.

*NOTE: Usecases cannot receive DTOs, so they need to know how to convert to domains to be sent to 
the usecases.*

#### 4.1.2 - externalinterfaces
This package will hold the port implementation that makes external calls to anothers components or 
systems. All implementation in this package will end with "EI" (that means External Interface), 
e.g. "HybrisProductListEI"

#### 4.1.3 - repositories
This package will hold the port implementation that makes database requests. All implementation in 
this package will end with the database name (like "Postgres", "Redis", etc...), e.g. 
"CacheFooterRedis", and all communication through database must be done by a Model class that 
references to the database tables. So you shall create a package called models and put all models 
used by the repositories.

*NOTE: Usecases cannot receive these models, so they need to know how to convert to domains to be 
sent to the usecases.*

### 4.2 - usecases
Classes responsible for the component's functions, but being unaware of their
specific implementation. Usecases can only know domains, so you mustn't send DTOs, Models or other 
data classes. All implementation in this package will end with "UserCase" e.g. "GetTranslatedReviewsUseCase".

#### 4.2.1 - ports
Ports are interfaces that will be used by Usecases in order to access the component's functions. 
These interfaces can have many implementations using different services, but neither the Usecases
nor the Ports should be aware or which one is being used or their internal logic. All classes in 
this package will end with "Port", e.g. "TranslateReviewsPort"

### 4.3 - domains
Domains are entities that contemplates a business domain, they must not be based on database models 
or Data Transfer Objects.

### 4.4 - config
Classes for configuration of features utilized in other divisions, including constants 
that'll be used as well.

## 5 - Creating tests
It is very important that, for every feature that is implemented, unit tests are added.To develop 
tests, it is highly recommended that you use JUnit 5.

Tests are implemented on the same relative path that the class we wish to test,
for example, a class created in:

```src/main/kotlin/${packageInPathFormat}/${contextFolderName}/```

should be tested in:

```src/test/kotlin/${packageInPathFormat}/${contextFolderName}/```

## 6 - Booting Up
After all the preparation is done, you should run the application by running the main class 
(**${mainClassName}Application**) and wait for the initialization to be completed.

There's currently only one endpoint for testing: 
```locahost:8081/hello``` 

If everything goes correctly, the component should go up and the message
confirming that it is on air should show up.