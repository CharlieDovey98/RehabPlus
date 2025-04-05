/**
 * Azure Function /getProgramById.
 * This function connects to the RehabPlus Azure Cosmos DB and returns a single program
 * from the programs container based on its unique programId.
 * This can be tested with https://rehabplus-functions.azurewebsites.net/api/getProgramById?id=p1.
 */

// Import the Azure functions and Cosmos DB.
const { app } = require("@azure/functions");
const { CosmosClient } = require("@azure/cosmos");

// Cosmos DB Endpoint and Primary Key credentials.
const endpoint = process.env.COSMOS_DB_ENDPOINT;
const key = process.env.COSMOS_DB_KEY;
// RehabPlus Database and programs container inside Cosmos DB.
const databaseId = "rehabplus-db";
const containerId = "programs";

// Create a Cosmos client instance to connect to the database using the set credentials.
const client = new CosmosClient({ endpoint, key });

// getProgramById Azure HTTP Function.
app.http("getProgramById", {
  methods: ["GET"], // GET methos with anonymous authentication.
  authLevel: "anonymous",
  handler: async (request, context) => {
    context.log(`GET /getProgramById called in backend API`);

    const programId = request.query.get("id");
    if (!programId) {
      // If the programid is not provided, return an error message.
      return {
        status: 400, // Client error status.
        jsonBody: {
          error:
            "programId parameter is required to find the program in the DB",
        },
      };
    }

    try {
      // Try, catch statement to access the database and container.
      const database = client.database(databaseId);
      const container = database.container(containerId);

      const querySpec = {
        // Define the query to select all documents from the root 'c' where the id matches.
        query: "SELECT * FROM c WHERE c.id = @id",
        parameters: [{ name: "@id", value: programId }],
      };

      // Execute the query and fetch the matching results.
      const { resources } = await container.items.query(querySpec).fetchAll();

      // If the recources returns empty return a 404 error.
      if (resources.length === 0) {
        return {
          status: 404, // Resource missing status.
          jsonBody: { error: "Program not found" },
        };
      }

      // Return the users as a JSON response with status 200.
      return {
        status: 200, // Server request completed status.
        jsonBody: resources[0],
      };

      // If an error occurs, log the error and return 500.
    } catch (err) {
      context.log.error("Error querying program:", err.message);
      return {
        status: 500, // Internal server error status.
        jsonBody: { error: "Failed to fetch program from Cosmos DB" },
      };
    }
  },
});
