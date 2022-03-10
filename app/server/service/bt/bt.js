const fetch = require('node-fetch');
const { details } = require('./api_details.js');
const { Encrypt } = require('../../utils/encrypt.js');
const { generateRequestid } = require('../../utils/data/request_id.js');
const { bank_data } = require('../../utils/data/bank_data.js');
const moment = require('moment');

'use strict'

class ServiceBt {
    static consentIdAIS = "d33dc2d3ce698b162007aec091659809";

    static getAccounts = async (token, consentId) => {
        if (token === '') {
            return null;
        }

        const path = 'bt-psd2-aisp/v2/accounts?withBalance=true';
        const reqUrl = details.ENDPOINT + path;

        const headers = {
            'x-request-id': generateRequestid(),
            'consent-id': consentId,
            'authorization': Encrypt.buildBearerTokenHeader(token),
            'psu-ip-address': '89.137.56.208'
        };

        try {
            const response = await fetch(reqUrl, {
                method: 'GET',
                headers
            });
            const responseBody = await response.json();
            return responseBody;
        } catch (error) {
            console.log(error);
        }
        return null;
    };

    static getTransactions = async (token, accountId, consentId) => {
        const today = moment().format('YYYY-MM-DD');
        const dateFrom = moment().subtract(89, 'days').format('YYYY-MM-DD');
        const path = 'bt-psd2-aisp/v2/accounts/' + accountId + '/transactions'
            + '?dateFrom=' + dateFrom
            + '&dateTo=' + today
            + '&bookingStatus=booked';
        const reqUrl = details.ENDPOINT + path;

        const headers = {
            accept: 'application/json',
            'authorization': Encrypt.buildBearerTokenHeader(token),
            'x-request-id': generateRequestid(),
            'consent-id': consentId,
            'psu-ip-address': '89.137.56.208'
        };

        try {
            const response = await fetch(reqUrl, {
                method: 'GET',
                headers
            });
            const responseBody = await response.json();
            return responseBody;
        } catch (error) {
            console.log(error);
        }
        return null;
    };

    static postPayment = async (transactionData, userData) => {
        const path = 'bt-psd2/v2/payments/ron-payment';
        const reqUrl = details.ENDPOINT + path;

        // see funds availability

        const headers = {
            'x-request-id': generateRequestid(),
            'content-type': 'application/json',
            'psu-ip-address': '89.137.56.208'
        };

        const payload = {
            debtorAccount: {
                iban: transactionData.myIban || transactionData.debtorIban
            },
            // debtorId: transactionData.debtorId,

            creditorAccount: {
                iban: transactionData.creditorIban
            },
            creditorName: transactionData.creditorName,

            instructedAmount: {
                currency: transactionData.currency,
                amount: transactionData.amount + ""
            },

            endToEndIdentification: transactionData.endToEndIdentification,
            remittanceInformationUnstructured: transactionData.details
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
};

module.exports = {
    ServiceBt
}

// ServiceBt.getAccounts('IU0LJZHMyCL58zZOq2ax', '135d4d4478435a5b163031b8b33bd980')
//     .then(console.log)
//     .catch(console.log);
// ServiceBt.getTransactions('x07K6ZvflAy0MPDPeQ7X', 'K13EURCRT0060214301', '22c04889e597c9bae829edb2121ef852')
//     .then(console.log)
//     .catch(console.log);
// ServiceBt.postPayment({
//     debtorIban: 'RO98BTRLRONCRT0ABCDEFGHI',
//     debtorId: 'd',
//     creditorIban: 'RO98BTRLEURCRT0ABCDEFGHI',
//     creditorName: 'Danut Popesko',
//     currency: 'RON',
//     amount: '50',
//     endToEndIdentification: 'TPP Reference',
//     remittanceInformationUnstructured: 'detailii despre'
// })
//     .then(console.log)
//     .catch(console.log);