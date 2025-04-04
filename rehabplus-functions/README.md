# RehabPlus Azure Functions

This folder contains backend functions for the RehabPlus application, deployed using Azure Functions. These functions interact with Cosmos DB to provide cloud hosted user data and support user authentication, storage, and retrieval.

## Currently deployed:
- getUsers.js  
  A GET HTTP Function that retrieves all users from the users container in Cosmos DB.

- getUserByEmail.js 
  A GET HTTP function that accepts an email query parameter and returns a single user document matching the email from Cosmos DB.

- createUser.js
  A POST HTTP function that creates a new user document in Cosmos DB using the JSON data provided in the request body.

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
select the rehabplus-functions directory, and the rehabplus-functions function app.