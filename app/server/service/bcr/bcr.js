const { Encrypt } = require('../../utils/encrypt.js');
const { sslConfiguredAgent } = require('../../certs/bcr/bcr_cert_options.js');
const { httpCodes } = require('../../utils/http/httpCodes.js');
const { errors } = require('../../utils/error/errors.js');
const { ServiceBcrAuth } = require('../bcr/bcr_auth.js');
const { Error } = require('../../utils/error/error.js');
const fetch = require('node-fetch');
const { details } = require('./api_details.js');
const { bank_data } = require('../../utils/data/bank_data.js');
const models = require('../../models');
const { generateRequestid } = require('../../utils/data/request_id.js');
'use strict'

class ServiceBcr {
    static getAccounts = async (token) => {
        if (token === '') {
            return null;
        }

        const method = 'get';
        const path = '/aisp/v1/accounts';
        const reqUrl = details.ENDPOINT + path;
        const payload = '';
        const digest = Encrypt.buildDigestHeader(payload);
        const date = Encrypt.buildDateHeader();
        const signature = Encrypt.buildSignatureHeaderWithKeyId(method, path, date, digest, details.BCR_CERT_KEY_ID);
        const authorization = Encrypt.buildBearerTokenHeader(token);

        const headers = {
            'Accept': 'application/json',
            'Authorization': authorization,
            'cache': 'no-store',
            'content-type': 'application/json',
            'web-api-key': details.BCR_API_KEY,
            'x-request-id': generateRequestid(),
            'Digest': digest,
            'Date': date,
            'Signature': signature
        };

        try {
            const response = await fetch(reqUrl, {
                method: method,
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

    static getTransactions = async (accountId, token) => {
        const method = 'get';
        const path = '/aisp/v1/accounts/' + accountId + '/transactions?bookingStatus=both';
        const reqUrl = details.ENDPOINT + path;
        const payload = '';
        const digest = Encrypt.buildDigestHeader(payload);
        const date = Encrypt.buildDateHeader();
        const signature = Encrypt.buildSignatureHeaderWithKeyId(method, path, date, digest, details.BCR_CERT_KEY_ID);
        const authorization = Encrypt.buildBearerTokenHeader(token);


        const headers = {
            'Accept': 'application/json',
            'Authorization': authorization,
            'cache': 'no-store',
            'content-type': 'application/json',
            'web-api-key': details.BCR_API_KEY,
            'x-request-id': generateRequestid(),
            'Digest': digest,
            'Date': date,
            'Signature': signature
        };
        try {
            const response = await fetch(reqUrl, {
                method,
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

    static getBalance = async (accountId, token) => {
        const method = 'get';
        const path = '/aisp/v1/accounts/' + accountId + '/balances';
        const reqUrl = details.ENDPOINT + path;
        const payload = '';
        const digest = Encrypt.buildDigestHeader(payload);
        const date = Encrypt.buildDateHeader();
        const signature = Encrypt.buildSignatureHeaderWithKeyId(method, path, date, digest, details.BCR_CERT_KEY_ID);
        const authorization = Encrypt.buildBearerTokenHeader(token);

        const headers = {
            'Accept': 'application/json',
            'Authorization': authorization,
            'cache': 'no-store',
            'content-type': 'application/json',
            'web-api-key': details.BCR_API_KEY,
            'x-request-id': generateRequestid(),
            'Digest': digest,
            'Date': date,
            'Signature': signature
        };

        try {
            const response = await fetch(reqUrl, {
                method: method,
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

    static postPayment = async (transactionData, userData) => {
        const path = '/pisp/v1/payments/domestic-credit-transfers-ro';
        const reqUrl = details.ENDPOINT + path;

        let bcrAccounts = await models.BankAccount.findAll({
            where: {
                userId: userData.userId,
                bankId: Encrypt.encryptData(bank_data.BCR),
            }
        });

        let responseBody;
        for (let account of bcrAccounts) {
            const bcrToken = Encrypt.decryptData(account.accessToken);

            // TODO check funds availability

            const payload = {
                debtorAccount: {
                    iban: transactionData.myIban
                },
                debtorName: transactionData.myName,
                instructedAmount: {
                    currency: transactionData.currency,
                    amount: transactionData.amount + ""
                },
                creditorAccount: {
                    iban: transactionData.creditorIban
                },
                creditorName: transactionData.creditorName,
                endToEndIdentification: transactionData.endToEndIdentification,
                remittanceInformationUnstructured: transactionData.details
            };

            const method = 'post';
            const digest = Encrypt.buildDigestHeader(JSON.stringify(payload));
            const date = Encrypt.buildDateHeader();
            const signature = Encrypt.buildSignatureHeaderWithKeyId(method, path, date, digest, details.BCR_CERT_KEY_ID);
            const authorization = Encrypt.buildBearerTokenHeader(bcrToken);

            const headers = {
                'Accept': 'application/json',
                'Authorization': authorization,
                'cache': 'no-store',
                'content-type': 'application/json',
                'web-api-key': details.BCR_API_KEY,
                'x-request-id': generateRequestid(),
                'Digest': digest,
                'Date': date,
                'psu-ip-address': '192.168.9.165',
                'Signature': signature
            };

            let response = await fetch(reqUrl, {
                method,
                headers,
                agent: sslConfiguredAgent,
                body: JSON.stringify(payload)
            });
            responseBody = await response.json();

            /* If token has expired, ask for new one */
            if (responseBody.errors && responseBody.status === 403) {
                const refresh_token = Encrypt.decryptData(account.refreshToken);
                const newAccessTokenObject = await ServiceBcrAuth.reqNewAccessToken(refresh_token);

                headers['Authorization'] = Encrypt.buildBearerTokenHeader(newAccessTokenObject.access_token);
                response = await fetch(reqUrl, {
                    method,
                    headers,
                    agent: sslConfiguredAgent,
                    body: JSON.stringify(payload)
                });
                responseBody = await response.json();
            }

            /* If the request was successful, so there is not tppMessage error */
            if (!responseBody.errors && (!(responseBody.tppMessages && responseBody.tppMessages[0] && responseBody.tppMessages[0].category === 'ERROR') ||
                (responseBody.status >= 200 && responseBody.status <= 299))) {
                return responseBody;
            }
        }
        throw new Error(responseBody.tppMessages[0].text, httpCodes.FORBIDDEN);
    };
}

module.exports = {
    ServiceBcr
}