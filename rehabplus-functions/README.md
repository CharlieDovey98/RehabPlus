# RehabPlus Azure Functions

This folder contains backend functions for the RehabPlus application, deployed using Azure Functions. These functions interact with Cosmos DB to provide cloud hosted user data and support user authentication, storage, and retrieval.

## Currently deployed:
- createQuestionnaire.js
  A POST HTTP function that creates a new questionnaire document in Cosmos DB using the JSON data provided in the request body.

- createUser.js
  A POST HTTP function that creates a new user document in Cosmos DB using the JSON data provided in the request body.

- getExercises.js
  A GET HTTP function that retrieves all the exercise documents in the exercises container in Cosmos DB.

- getProgramById.js
  A GET HTTP function that accepts a programId query parameter and returns a single program document matching the id from Cosmos DB.

- getUserByEmail.js 
  A GET HTTP function that accepts an email query parameter and returns a single user document matching the email from Cosmos DB.

- getUsers.js  
  A GET HTTP function that retrieves all users from the users container in Cosmos DB.

- markQuestionnaireComplete.js
  A POST HTTP functoin that updates a user document using an email query parameter, setting the completedQuestionnaire field to true in the users container in Cosmos DB.

## Structure
- functions
Holds the HTTP functions currently deployed.

- index.js
Sets up the Azure Functions runtime and enables HTTP streaming.

- local.settings.json
Contains local development environment variables, including the RehabPlus Cosmos DB credentials.  

## Steps to deploy

- Deploy to Azure function application
ctrl+shift+p 'Azure functions: deploy to function app'
select the rehabplus-functions directory, and the rehabplus-functions app.