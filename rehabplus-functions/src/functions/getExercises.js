/**
 * Azure Function /getExercises.
 * This function connects to RehabPlus Azure Cosmos DB and returns all exercises
 * in the exercises container.
 * This can be tested with https://rehabplus-functions.azurewebsites.net/api/getExercises.
 */

// Import the Azure functions and Cosmos DB.
const { app } = require("@azure/functions");
const { CosmosClient } = require("@azure/cosmos");

// Cosmos DB Endpoint and Primary Key credentials.
const endpoint = process.env.COSMOS_DB_ENDPOINT;
const key = process.env.COSMOS_DB_KEY;
// RehabPlus Database and exercises container inside Cosmos DB.
const databaseId = "rehabplus-db";
const containerId = "exercises";

// Create a Cosmos client instance to connect to the database using the set credentials.
const client = new CosmosClient({ endpoint, key });

// getExercises Azure HTTP Function.
app.http("getExercises", {
  methods: ["GET"], // GET methos with anonymous authentication.
  authLevel: "anonymous",
  handler: async (request, context) => {
    context.log(`GET /getExercises called in backend API`);

    try {
      // Try, catch statement to access the database and container.
      const database = client.database(databaseId);
      const container = database.container(containerId);
      // Define the query to select all documents from the root 'c'.
      const querySpec = { query: "SELECT * FROM c" };
      // Execute the query and fetch the matching results.
      const { resources: exercises } = await container.items
        .query(querySpec)
        .fetchAll();

      // Return the users as a JSON response with status 200.
      return {
        status: 200, // Server request completed status.
        jsonBody: exercises,
      };

      // If an error occurs, log the error and return 500.
    } catch (err) {
      context.log.error("Error when querying Cosmos DB:", err.message);
      return {
        status: 500, // Internal server error status.
        jsonBody: { error: "Failed to fetch exercises from Cosmos DB" },
      };
    }
  },
});
