/**
 * Azure Functions App Setup.
 * This file sets up the Azure Functions runtime.
 * It enables HTTP streaming allows function creation.
 */

 // Import the Azure functions application.
const { app } = require('@azure/functions');

// Set up the Azure functions environment
// 'enableHttpStream: true' enables advanced streaming support.
app.setup({
    enableHttpStream: true,
});
