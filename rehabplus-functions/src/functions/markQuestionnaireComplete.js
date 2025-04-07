/**
 * Azure Function /markQuestionnaireComplete.
 * This function connects to RehabPlus Azure Cosmos DB
 * and uses POST to update the completedQuestionnaire field
 * within the user document in the users container using the user email as the query parameter.
 */

// Import the Azure functions and Cosmos DB.
const { app } = require("@azure/functions");
const { CosmosClient } = require("@azure/cosmos");

// Cosmos DB Endpoint and Primary Key credentials.
const endpoint = process.env.COSMOS_DB_ENDPOINT;
const key = process.env.COSMOS_DB_KEY;
// RehabPlus Database and users container inside Cosmos DB.
const databaseId = "rehabplus-db";
const containerId = "users";

// Create a Cosmos client instance to connect to the database using the set credentials.
const client = new CosmosClient({ endpoint, key });

// markQuestionnaireComplete Azure HTTP POST Function.
app.http("markQuestionnaireComplete", {
  methods: ["POST"], // POST method with anonymous authentication.
  authLevel: "anonymous",
  handler: async (request, context) => {
    context.log("POST /markQuestionnaireComplete called in backend API");

    const { email } = await request.json();
    if (!email) {
      // If the user email is not provided, return an error message.
      return {
        status: 400, // Client error status.
        jsonBody: {
          error: "Email is required to find the correct user in the DB",
        },
      };
    }

    try {
      // Try, catch statement to access the database and container.
      const database = client.database(databaseId);
      const container = database.container(containerId);

      const querySpec = { // Define the query to select all documents from the root 'c' where the email matches.
        query: "SELECT * FROM c WHERE c.email = @email",
        parameters: [{ name: "@email", value: email }],
      };

      // Execute the query and fetch the matching results.
      const { resources } = await container.items.query(querySpec).fetchAll();

      // If the recources returns empty return a 404 error.
      if (resources.length === 0) {
        return {
          status: 404, // Resource missing status.
          jsonBody: { error: "User not found" },
        };
      }

      // Access the user returned and set the completedQuestionnaire variable to true.
      const user = resources[0];
      user.completedQuestionnaire = true;

      // Define the updated user as the new resource to return.
      const { resource: updatedUser } = await container
        .item(user.id, user.id)
        .replace(user);

      return { // Return the user as a JSON response with status 200.
        status: 200, // Server request completed status.
        jsonBody: updatedUser,
      };
    } 
    // If an error occurs, log the error and return 500.
    catch (err) {
      context.log.error(
        "Error updating user questionnaire:",
        err.message
      );
      return {
        status: 500, // Internal server error status.
        jsonBody: { error: "Failed to query Cosmos DB" },
      };
    }
  },
});
