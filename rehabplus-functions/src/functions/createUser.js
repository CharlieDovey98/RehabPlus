/**
 * Azure Function /createUser.
 * This file connects to Azure Cosmos DB and creates a new user document in the users container
 * of the 'rehabplus-db' Cosmos DB database.
 * This function POSTS a user document to Cosmos DB based on the user object in RehabPlus.
 */

// Import the Azure functions and Cosmos DB.
const { app } = require('@azure/functions');
const { CosmosClient } = require('@azure/cosmos');

// Cosmos DB Endpoint and Primary Key credentials.
const endpoint = process.env.COSMOS_DB_ENDPOINT;
const key = process.env.COSMOS_DB_KEY;
// RehabPlus Database and users container inside Cosmos DB.
const databaseId = "rehabplus-db";
const containerId = "users";

// Create a Cosmos client instance to connect to the database using the set credentials.
const client = new CosmosClient({ endpoint, key });

// createUser Azure HTTP POST Function.
app.http('createUser', {
  methods: ['POST'], // POST methos with anonymous authentication.
  authLevel: 'anonymous',
  handler: async (request, context) => {
    context.log(`HTTP POST /createUser called in backend API`);

    try { // Try, catch statement to access the database and container.
      const user = await request.json(); // Parse the request body as JSON.
      if (!user || !user.id || !user.email || !user.name) {
        return { // If any user information is ommitted return error status 400.
          status: 400, // Client error status.
          jsonBody: { error: 'Invalid or missing user data provided.' },
        };
      }

      // Get the reference to the database and users container.
      const database = client.database(databaseId);
      const container = database.container(containerId);

      // Insert the new user object into the container.
      const { resource: createdUser } = await container.items.create(user);
      return {
        status: 201, // Successful resource creation status.
        jsonBody: createdUser,
      };
    } catch (err) { // If an error occurs, log the error and return 500.
      context.log.error('Error creating user:', err.message);
      return {
        status: 500, // Internal server error ststus.
        jsonBody: { error: 'Failed to create user in Cosmos DB.' },
      };
    }
  },
});
