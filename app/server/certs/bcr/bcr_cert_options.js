const fs = require('fs');
const https = require('https');
'use strict'

const certs = {
    cert: fs.readFileSync('./certs/bcr/bcr_public_cert.crt'),
    key: fs.readFileSync('./certs/bcr/bcr_private_key.key')
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
}