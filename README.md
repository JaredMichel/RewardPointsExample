This repository contains a simple example of a Typescript React frontend with Kotlin Spring Boot backend. It completes the following challenge: 

> ## Blockquoted header
>
> A customer receives 2 points for every dollar spent over $100 in each transaction, plus 1 point for every dollar spent over $50 in each transaction
>
> (e.g. a $120 purchase = 2x$20 + 1x$50 = 90 points).

The backend stores transactions in an in-memory list and maintains a running total of total reward points earned. A set of fake transactions is initialized when the backend first starts up.
The frontend displays current transactions and accepts input of new transaction.

The backend can be started like any other Spring Boot project, and the frontend can be started like any other React project with a 'yarn start'
