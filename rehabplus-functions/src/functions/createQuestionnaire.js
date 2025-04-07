/**
 * Azure Function /createQuestionnaire.
 * This function connects to RehabPlus Azure Cosmos DB
 * and POSTS a new questionnaire document into the questionnaires container.
 */

// Import the Azure functions and Cosmos DB.
const { app } = require("@azure/functions");
const { CosmosClient } = require("@azure/cosmos");

// Cosmos DB Endpoint and Primary Key credentials.
const endpoint = process.env.COSMOS_DB_ENDPOINT;
const key = process.env.COSMOS_DB_KEY;
// RehabPlus Database and users container inside Cosmos DB.
const databaseId = "rehabplus-db";
const containerId = "questionnaires";

// Create a Cosmos client instance to connect to the database using the set credentials.
const client = new CosmosClient({ endpoint, key });

// createQuestionnaire Azure HTTP POST Function.
app.http("createQuestionnaire", {
  methods: ["POST"], // POST method with anonymous authentication.
  authLevel: "anonymous",
  handler: async (request, context) => {
    context.log("POST /createQuestionnaire called in backend API");

    try { // Try, catch statement to access the database and container.
      const questionnaire = await request.json(); // Parse the request body as JSON.

      // Questionnaire request body validation.
      if (!questionnaire || !questionnaire.userEmail || !questionnaire.userName || !questionnaire.userDateOfBirth ||
         !questionnaire.dateOfSubmission || !questionnaire.responses) {
        // If any questionnaire information is ommitted return error status 400.
        return {
          status: 400, // Client error status.
          jsonBody: { error: "Invalid or missing questionnaire data provided." }
        };
      }

      // Get the reference to the database and users container.
      const database = client.database(databaseId);
      const container = database.container(containerId);

      // Use the userEmail as the document ID to allow for easy identification.
      const questionnaireDocument = {id: questionnaire.userEmail, ...questionnaire};

      // Insert the new user object into the container.
      const { resource: created } = await container.items.create(questionnaireDocument);
      return {
        status: 201, // Successful resource creation status.
        jsonBody: created
      };
    } catch (err) {
      // If an error occurs, log the error and return 500.
      context.log.error("Error creating questionnaire:", err.message);
      return {
        status: 500, // Internal server error status.
        jsonBody: { error: "Failed to create questionnaire in Cosmos DB." }
      };
    }
  }
});
