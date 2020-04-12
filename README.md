This repository contains a simple example of a Typescript React frontend with a Kotlin Spring Boot backend. It completes the following challenge: 

> A customer receives 2 points for every dollar spent over $100 in each transaction, plus 1 point for every dollar spent over $50 in each transaction
>
> (e.g. a $120 purchase = 2x$20 + 1x$50 = 90 points).
>
> Given a record of every transaction during a three month period, calculate the reward points earned for each customer per month and total.

The backend stores transactions in an in-memory list and maintains a running total of total reward points earned. The transactions and points are tracked by user, and a set of fake transactions for each user for the past several months is initialized when the backend first starts up.
The frontend allows an administrator to view and add transactions as well as view overall and monthly reward points earned by each user.

The backend can be started like any other Spring Boot project. Inside of Intellij this means setting up a Spring Boot run configuration with `com.mipke.backend.BackendApplication` as the main class. Alternatively, the backend can be started by cd-ing into the `backend` directory to utilize the embedded gradle wrappers. Start the server by running `gradlew.bat bootRun` on Windows or `./gradlew bootRun` on Linux or Mac.

The frontend can be started like any other React project by first installing its dependencies with `yarn install` and then `yarn start` to start the development server. The development server is setup to proxy its network calls to the backend runnning on port 8080
