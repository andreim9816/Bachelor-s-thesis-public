const fetch = require('node-fetch');
const { URLSearchParams } = require('url');
const { generateRequestid } = require('../../utils/data/request_id.js');
const { details } = require('./api_details.js');
'use strict'

class ServiceBtAuth {
    static getConsent = async () => {
        const reqUrl = details.ENDPOINT + 'bt-psd2-aisp/v2/consents';
        const headers = {
            'Cache-control': 'no-cache',
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            'psu-ip-address': '89.137.56.208',
            'x-request-id': generateRequestid()
        };

        const payload = {
            access: {
                availableAccounts: 'allAccounts'
            },
            recurringIndicator: true,
            validUntil: '2021-07-10',
            combinedServiceIndicator: false,
            frequencyPerDay: 4
        };

        try {
            const response = await fetch(reqUrl, {
                method: 'POST',
                headers,
                body: JSON.stringify(payload)
            });

            const responseBody = await response.json();
            return responseBody;

        } catch (error) {
            console.log(error);
        }

        return null;
    };

    static reqAccessToken = async (code) => {
        const reqUrl = details.ENDPOINT + 'oauth/token';

        const headers = {
            'Cache-control': 'no-cache',
            'Content-type': 'application/x-www-form-urlencoded',
            'Accept': 'application/json'
        };

        const params = new URLSearchParams();
        params.append('grant_type', 'authorization_code');
        params.append('code', code);
        params.append('client_id', details.BT_CLIENT_ID);
        params.append('client_secret', details.BT_CLIENT_SECRET);
        params.append('redirect_uri', details.REDIRECT_URI);
        params.append('code_verifier', 'code_verifier');

        try {
            const response = await fetch(reqUrl, {
                method: 'POST',
                headers,
                body: params
            });

            const responseBody = await response.json();
            return responseBody;

        } catch (error) {
            console.log(error);
        }

        return null;
    };

    static reqNewAccessToken = async (refreshToken) => {
        const reqUrl = details.ENDPOINT + 'oauth/token';

        const headers = {
            'Cache-control': 'no-cache',
            'Content-type': 'application/x-www-form-urlencoded',
            'Accept': 'application/json'
        };

        const params = new URLSearchParams();
        params.append('refresh_token', refreshToken);
        params.append('grant_type', 'refresh_token');
        params.append('client_id', details.BT_CLIENT_ID);
        params.append('client_secret', details.BT_CLIENT_SECRET);
        params.append('redirect_uri', details.REDIRECT_URI);

        try {
            const response = await fetch(reqUrl, {
                method: 'POST',
                headers,
                body: params
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
    ServiceBtAuth
}