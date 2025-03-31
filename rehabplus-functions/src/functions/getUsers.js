/**
 * Azure Function /getUsers.
 * This file connects to Azure Cosmos DB and returns all user documents
 * from the 'users' container in the rehabplus-db database. 
 * This file is used by the Rehab Plus app to fetch a list of users.
 * This can be tested with https://rehabplus-functions.azurewebsites.net/api/getUsers.
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

// getUsers Azure HTTP Function.
app.http('getUsers', {
  methods: ['GET'], // GET request with anonymous authentication.
  authLevel: 'anonymous',
  handler: async (request, context) => {
    context.log(`HTTP GET /getUsers called in backend API`);

    try { // Try, catch statement to access the database and container.
      const database = client.database(databaseId);
      const container = database.container(containerId);

      // Define the query to select all documents from the root 'c'.
      const querySpec = {
        query: "SELECT * FROM c"
      };
      // Execute the query and fetch the matching results.
      const { resources: users } = await container.items.query(querySpec).fetchAll();

      // Return the users as a JSON response with status 200.
      return {
        status: 200,
        jsonBody: users
      };
    } 
    // If an error occurs, log the error and return 500.
    catch (err) {
      context.log.error("Error when querying Cosmos DB:", err.message);
      return {
        status: 500,
        jsonBody: { error: "Failed to fetch users from Cosmos DB" }
      };
    }
  }
});
