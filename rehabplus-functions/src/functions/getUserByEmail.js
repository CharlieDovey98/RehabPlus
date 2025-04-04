/**
 * Azure Function /getUserByEmail.
 * This file connects to Azure Cosmos DB and returns a user document by its email
 * from the 'users' container in the rehabplus-db database. 
 * This can be tested with https://rehabplus-functions.azurewebsites.net/api/getUserByEmail?email=charliedovey0161@hotmail.com.
 * This function fetches a user document from Cosmos DB based on the provided email query parameter.
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

// getUserByEmail Azure HTTP Function.
app.http('getUserByEmail', {
  methods: ['GET'], // GET request with anonymous authentication.
  authLevel: 'anonymous',
  handler: async (request, context) => {
    const email = request.query.get('email');
    context.log(`GET /getUserByEmail called with email: ${email} in backend API`);

    if (!email) { // If the email is not provided, return an error message.
      return {
        status: 400,
        jsonBody: { error: "Email parameter is required to find the user in the DB" }
      };
    }

    try { // Try, catch statement to access the database and container.
      const container = client.database(databaseId).container(containerId);

      // Define the query to select all documents from the root 'c' where the email matches.
      const querySpec = {
        query: "SELECT * FROM c WHERE c.email = @email",
        parameters: [{ name: "@email", value: email }]
      };

      // Execute the query and fetch the matching results.
      const { resources } = await container.items.query(querySpec).fetchAll();

      // If the recources returns 'empty' return a 404 error.
      if (resources.length === 0) {
        return {
          status: 404, // Resource missing status.
          jsonBody: { error: "User not found" }
        };
      }

      // Return the users as a JSON response with status 200.
      return {
        status: 200, // Server request completed status.
        jsonBody: resources[0]
      };

    // If an error occurs, log the error and return 500.
    } catch (err) {
      context.log.error("Error fetching user by email:", err.message);
      return {
        status: 500, // Internal server error status.
        jsonBody: { error: "Failed to query Cosmos DB" }
      };
    }
  }
});
