const fs = require('fs');
const https = require('https');
'use strict'

const certs = {
    cert: fs.readFileSync('./certs/example_eidas_client_tls.cer'),
    key: fs.readFileSync('./certs/example_eidas_client_tls.key')
};

const options = {
    cert: certs.cert,
    key: certs.key,
    rejectUnauthorized: true // TODO change to true?
};

const sslConfiguredAgent = new https.Agent(options);

module.exports = {
    options,
    sslConfiguredAgent,
    certs,
    https
}