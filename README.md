# github-explorer

## Table of contents
- [General info](#general-info)
- [Technologies](#technologies)
- [About the project](#about-the-project)
  
- [Functional and non-functional requirements](#functional-and-non-functional-requirements)
- [Project status](#project-status)
- [Resources](#resources)
- [Contact](#contact)

## General info

This project provide api to list repositories from github for specific user by his login. It also provide data transfer object for exceptions like 'not found repository by username' or 'incorrect accept header' in correct format defined in functional requirements.

## Technologies
* Java 17
* Spring Boot (Web, Webflux)
* Gson
* Lombok
* Swagger

## About the project
### Endpoints:

#### Get user github repositories `http://localhost:6000/v1/api/repository/get?username={username}`

![image](https://github.com/MarcinMikolaj/github-explorer/assets/67873349/91a3482b-3d1c-48d9-9cbc-95a201e3494b)

![image](https://github.com/MarcinMikolaj/github-explorer/assets/67873349/7a8a0bac-4b0b-4942-8ee0-29a2bee9b80c)


## Functional and non-functional requirements

The non-functional requirements that have been set for the application include:
- none

The functional requirements that have been set for the application include:
- Provide feature: list all github repositories for header “Accept: application/json” in format: (Repository Name, Owner Login, For each branch it’s name and last commit sha)
example: \
 ```
{
        "repositoryName": "github-explorer",
        "ownerLogin": "MarcinMikolaj",
        "branches": [
            {
                "name": "1-project-initialization",
                "sha": "53ecc68db800a35540de780fbe0e366523667f36"
            },
            {
                "name": "3-skeleton",
                "sha": "730a3e6176507443debb2e619f82b9a382b7a512"
            },
            {
                "name": "5-exception-handler",
                "sha": "da76e5ecb3174d184fb2480104fbf48dabf2bf5e"
            },
            {
                "name": "main",
                "sha": "44797598f39bd8cb0154673b2fdae7fe03ca293a"
            }
        ]
    }
```
- use https://developer.github.com/v3 to provide user repositories
- if given github username dont exist return 404 response in such a format:{ “status”: ${responseCode} “Message”: ${whyHasItHappened} }
example ,
```
{
    "statusCode": 404,
    "message": "GitHub username not found !"
}
```
- if request contain header “Accept: application/xml”, return 406 response in such a format:{ “status”: ${responseCode} “Message”: ${whyHasItHappened} }
example,
```
{
    "statusCode": 406,
    "message": "No acceptable representation"
}
```


## Project status

The project has been completed, all functional and non-functional requirements have been implemented.

## Setup

1. Open this URL https://github.com/MarcinMikolaj/github-explorer
2. Now we must find the green rectangle where it writes „<>CODE”
3. We select HTTPS and there is a link below that you need to copy
4. The next step is to open IntelliJ And choose File > New > Project from Version Control
5. Then we need to paste our URL from Github, select the path to our project on our PC, and click Clone.
6. Enjoy our Project! 😄

## Resources

📦 Postamn Collection to test API V1: [github-explorer.postman_collection.zip](https://github.com/MarcinMikolaj/github-explorer/files/12566658/github-explorer.postman_collection.zip) \
🧪 Swagger: http://localhost:7000/swagger-ui/index.html

## Contact

Author: Marcin Mikołajczyk \
Email: marcin.mikolajczyk22@gmail.com \
Project Link: https://github.com/MarcinMikolaj/github-explorer
