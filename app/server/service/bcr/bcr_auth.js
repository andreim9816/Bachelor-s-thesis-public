const { sslConfiguredAgent } = require('../../certs/bcr/bcr_cert_options.js');
const fetch = require('node-fetch');
const { details } = require('./api_details.js');
'use strict'

class ServiceBcrAuth {
    static reqAccessToken = async (code) => {
        const reqUrl = details.ENDPOINT + 'sandbox-idp/token'
            + '?client_id=' + details.BCR_CLIENT_ID
            + '&code=' + code
            + '&grant_type=authorization_code'
            + '&redirect_uri=' + details.REDIRECT_URI
            + '&client_secret=' + details.BCR_CLIENT_SECRET

        const headers = {
            'Cache-control': 'no-cache'
        };

        try {
            const response = await fetch(reqUrl, {
                method: 'POST',
                headers,
                agent: sslConfiguredAgent
            });

            const responseBody = await response.json();
            return responseBody;

        } catch (error) {
            console.log(error);
        }

        return null;
    };

    static reqNewAccessToken = async (refreshToken) => {
        const path = 'sandbox-idp/token'
            + '?client_id=' + details.BCR_CLIENT_ID
            + '&refresh_token=' + refreshToken
            + '&grant_type=refresh_token'
            + '&client_secret=' + details.BCR_CLIENT_SECRET;
        const reqUrl = details.ENDPOINT + path;

        const headers = {
            'cache-control': 'no-cache',
            'content-type': 'application/json'
        };

        try {
            const response = await fetch(reqUrl, {
                method: 'POST',
                headers,
                agent: sslConfiguredAgent
            });
            const responseBody = await response.json();
            return responseBody;
        } catch (error) {
            console.log(error);
        }
        return null;
    };
}

module.exports = {
    ServiceBcrAuth
}