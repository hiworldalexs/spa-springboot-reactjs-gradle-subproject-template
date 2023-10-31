# spa-springboot-reactjs-gradle-subproject-template

## Background
When designing a web application, the decision to combine the frontend and backend on one port can have significant implications. 

Pros:
- Simplified Deployment
- Easier Development and Maintenance

Cons:
- Scalability Challenges
- Performance Impact
- Complexity in Separation

Use cases:
- proof of concept project
- Non traffic demanding system 
- internal system
- etc

This template aims to provide an elegant and highly decoupled method with the given tech stack.

## Tech Stack
- backend: Spring Boot
- frontend: ReactJS
  - package manager: yarn
- build tool: gradle

## How it works
In this template, we are using Spring Boot as the main part, sharing it's port for both frontend and backend api calls. Making use of the default behaviour of Spring Boot MVC, placing ReactJS production bundles in to Spring Boot Static Content Path, including `index.html`.

## Prerequisite Tools
- Intellij
  - for project initialization
  - IDE for backend development
- VS Code
  - IDE for frontend development
- nvm
- Optional
  - Gradle, for initialize project if not using Intellij

## Project setup steps

### Root project
1. Create a empty project in Intellij
2. Gradle Initialization
   1. run `gradle init`
      > If you don't have gradle, use Intellij `Run Anyting` to run this command.
   2.  Choose the following
        - Select type of project to generate: `1: basic`
        - Select build script DSL: `1: Kotlin`
        - Set your project name
        - Generate build using new APIs and behavior (some features may change in the next minor release)? `no`

### Frontend
1. Install yarn
    ```shell
   npm install --global yarn
   ```
2. Create a react application by [create-react-app](https://github.com/facebook/create-react-app#creating-an-app)
   ```shell
   npx create-react-app frontend --template typescript --use-yarn  
   ```
3. Create [build.gradle.kts](./frontend/build.gradle.kts) for root project to build it as subproject
   i. this file will config a library for building ReactJs without having Node.js installed locally on the system, the idea of the library is downloading node modules when run time and we can through gradle task to perform node commands. Details please refer [Gradle Plugin for Node](https://github.com/node-gradle/gradle-node-plugin#gradle-plugin-for-node)
4. Update [.gitignore](./frontend/.gitignore) to exclude gradle and com.github.node-gradle.node plugin cache
   ```gitignore
   ### com.github.node-gradle.node cache
   .cache

   ### Gradle template
   .gradle
   build
   ```
5. Health check
    ```shell
   cd frontend 
   yarn start
   ```
6. Add this subproject to root project by updating [settings.gradle.kts](settings.gradle.kts)
    ```kotlin
    include("frontend")
    ```

### Backend
1. Go [spring initializr](https://start.spring.io/) to create your project, this is what [this template using](https://start.spring.io/#!type=gradle-project-kotlin&language=kotlin&platformVersion=3.1.5&packaging=jar&jvmVersion=17&groupId=com.hiworldalexs&artifactId=backend&name=spa-springboot-reactjs-gradle-subproject-backend&description=Single%20Page%20Application%20Spring%20Boot%20ReactJS%20Gradle-subproject%20Template&packageName=com.hiworldalexs.backend&dependencies=web,actuator)
   - Project: Gradle - Kotlin
   - Language: Kotlin
   - Spring Boot: 3.1.5
   - Packaging: Jar
   - Java: 17
   - Dependencies: Spring Web, Spring Boot Actuator
   - Artifact: backend
2. Unzip the zip file generated from [spring initializr](https://start.spring.io/) to [root project directory](./)
3. Create a [SPA filter](backend/src/main/kotlin/com/hiworldalexs/backend/spa/SpaWebFilter.kt)
    - this filter is very important. It ensures that the frontend application can handle client-side routing and render the appropriate view based on the requested path
    - as this only matters to when frontend and backend bundle together, it is conditionally depends on properties `bundle`, this properties will pass as java cli arguments when deploy
4. Configure build for combining frontend codes
    1.  Update backend [build.gradle.kts](backend/build.gradle.kts), make sure before building jar, copy the frontend built codes to the [Spring Boot default Static Resources path](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-config/static-resources.html)
    ```kotlin
    if (project.hasProperty("prod")) {
        tasks.withType<Jar> {
            dependsOn(":frontend:yarn_build")
                from("../frontend/build") {
                    into("static")
                }
        }
    }
    ```
5. Run Spring Boot and health check
   ```shell
    gradle :backend:bootRun
    ```
   Health check 
   ```shell
    curl localhost:8080/actuator/health
    ```
5. Add this subproject to root project by updating [settings.gradle.kts](settings.gradle.kts)
    ```kotlin
    include("backend")
    ```
### Build both frontend and backend as a jar, serving the same port
```shell
gradle clean build -Pbundle
java -jar backend/build/libs/backend-0.0.1-SNAPSHOT.jar --bundle=true
```
Health check
```shell
curl localhost:8080
curl localhost:8080/actuator/health
```

## Development
When development, we can actually separate start with two servers, as we are originally separate as gradle subprojects, they can work independently. But we will need to update frontend with proxy for unhandled / api request
### Frontend
1. Update [package.json](frontend/package.json) to add proxy for redirect unhandled request, backend api call with be an "unhandled request" to frontend side
    ```json
    "proxy": "http://localhost:8080",
    ```
2. Start up command
    ```shell
    cd frontend
    yarn install
    yarn start
    ```
### Backend
```shell
./gradlew :backend:clean build
./gradlew :backend:bootRun
```

### Health check
```bash
curl localhost:3000
curl localhost:8080/actuator/health
```

## Docker for deployment
```shell
docker build . -t spa
docker run -p 8080:8080 spa 
```