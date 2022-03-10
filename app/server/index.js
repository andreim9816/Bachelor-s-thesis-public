require('dotenv').config();

const fs = require('fs');
const https = require('https');
const http = require('http');
const { ServiceAllBankAuth } = require('./service/allbank/auth.js');
const { ServiceBcrAuth } = require('./service/bcr/bcr_auth.js');
const { ServiceBtAuth } = require('./service/bt/bt_auth.js');

const { ServiceAllBank } = require('./service/allbank/allbank.js');
const { ServiceBcr } = require('./service/bcr/bcr.js');
const { ServiceBt } = require('./service/bt/bt.js');

const { authenticationMiddleware } = require('./middlewares/authMiddleware.js');
const { integrityMiddleware } = require('./middlewares/integrityMiddleware.js');

const { httpCodes } = require('./utils/http/httpCodes.js');

const { bank_data } = require('./utils/data/bank_data.js');

const express = require('express');
const app = express();

const port = 3443;
'use strict'

/* Used for getting the raw body */
const rawBodySaver = function (req, res, buf, encoding) {
    if (buf && buf.length) {
        req.rawBody = buf.toString(encoding || 'utf8');
    }
}

app.use(express.urlencoded({
    verify: rawBodySaver,
    extended: true
}));

app.use(express.json({
    extended: true
}));

const httpOptions = {
    cert: fs.readFileSync("./certs/server/server.cer"),
    key: fs.readFileSync("./certs/server/server.key")
};

app.post('/init_startup', (req, res) => {
    Promise.all([ServiceAllBankAuth.getApiData(), ServiceBtAuth.getConsent()])
        .then(([obj, consent]) => {
            ServiceBt.consentIdAIS = consent.consentId;
            res.send({
                bcr_client_id: obj.bcr_client_id,
                keystore_secret_bcr: obj.keystore_secret_bcr,
                keystore_secret_server: obj.keystore_secret_server,
                keystore_secret_ing: obj.keystore_secret_ing,
                consentId: consent.consentId
            })
        })
        .catch(error => res.send(null));
});

app.post('/bcr_access_token', (req, res) => {
    let code = req.body.code;
    if(code.charAt(code.length - 1) === '=') {
        code = code.slice(0, -1) + '%3D';
    }
    ServiceBcrAuth.reqAccessToken(code)
        .then(obj => res.send(obj))
        .catch(error => res.send(null));
});

app.post('/bcr_refresh_token', (req, res) => {
    const refreshToken = req.body.refresh_token;
    ServiceBcrAuth.reqNewAccessToken(refreshToken)
        .then(obj => res.send(obj))
        .catch(error => res.send(null));
});

app.post('/bt_access_token', (req, res) => {
    const code = req.body.code;
    ServiceBtAuth.reqAccessToken(code)
        .then(obj => res.send(obj))
        .catch(error => res.send(null));
});

app.post('/bt_post_payment', (req, res) => {
    const data = {
        debtorIban: req.body.debtor_iban,
        debtorId: req.body.debtor_id,
        creditorIban: req.body.creditor_iban,
        creditorName: req.body.creditor_name,
        currency: req.body.currency,
        amount: req.body.amount,
        endToEndIdentification: req.body.endToEndIdentification,
        remittanceInformationUnstructured: req.body.details
    };
    ServiceBt.postPayment(data)
        .then(obj => res.send(obj))
        .catch(error => res.send(null));
});

app.post('/bcr_post_payment', authenticationMiddleware, (req, res) => {
    const data = {
        debtorIban: req.body.debtor_iban,
        creditorIban: req.body.creditor_iban,
        creditorName: req.body.creditor_name,
        currency: req.body.currency,
        amount: req.body.amount,
        endToEndIdentification: req.body.endToEndIdentification,
        details: req.body.details
    };

    const token = req.body.access_token;
    const requestId = "30fb2226-8c2e-11e9-b683-526af7764f64";

    ServiceBcr.postPayment(data, token, requestId)
        .then(obj => res.send({
            paymentId: obj.paymentId,
            transactionStatus: obj.transactionStatus,
            scaRedirect: obj._links.scaRedirect.href
        }))
        .catch(err => res.send(null));
});

app.patch('/refresh_token/:accountId', authenticationMiddleware, (req, res) => {
    const userId = req.userId;
    const accountId = req.params.accountId;
    const refreshToken = req.body.token;
    const data = {
        userId,
        accountId,
        refreshToken
    };
    ServiceAllBank.setRefreshToken(data)
        .then(_ => res
            .status(httpCodes.NO_CONTENT)
            .send())
        .catch(err => res
            .status(err.errorCode)
            .send(err.errorMessage));
});

app.patch('/access_token/:accountId', authenticationMiddleware, (req, res) => {
    const userId = req.userId;
    const accountId = req.params.accountId;
    const accessToken = req.body.token;
    const data = {
        userId,
        accountId,
        accessToken
    };
    ServiceAllBank.setAccessToken(data)
        .then(_ => res
            .status(httpCodes.NO_CONTENT)
            .send())
        .catch(err => res
            .status(err.errorCode)
            .send(err.errorMessage));
});




app.post('/user', integrityMiddleware, (req, res) => {
    const data = {
        username: req.body.username,
        password: req.body.password,
        firstName: req.body.firstName,
        lastName: req.body.lastName,
        phone: req.body.phone,
        email: req.body.email
    };
    ServiceAllBank.createUser(data)
        .then(_ => ServiceAllBankAuth.login({
            username: req.body.username,
            password: req.body.password
        }))
        .then(token => res
            .status(httpCodes.OK)
            .send({
                token
            }))
        .catch(err => {
            console.log(err); res
                .status(err.errorCode)
                .send(err.errorName)
        });
});

app.post('/budget', integrityMiddleware, authenticationMiddleware, (req, res) => {
    const userId = req.userId;
    const budget = {
        budget: req.body.budget,
        category: req.body.category,
        endDate: req.body.endDate,
        startDate: req.body.startDate
    };
    const data = {
        userId,
        budget
    };
    ServiceAllBank.newBudget(data)
        .then(obj => res
            .status(httpCodes.OK)
            .send({
                id: obj.id
            }))
        .catch(err => res
            .status(err.errorCode)
            .send({ message: err.errorMessage }));
});

app.patch('/budget', integrityMiddleware, authenticationMiddleware, (req, res) => {
    const userId = req.userId;
    const budget = {
        id: req.body.budgetId,
        budget: req.body.budget,
        category: req.body.category,
        endDate: req.body.endDate,
        startDate: req.body.startDate
    };
    const data = {
        userId,
        budget
    };
    ServiceAllBank.editBudget(data)
        .then(_ => res
            .status(httpCodes.NO_CONTENT)
            .send())
        .catch(err => res
            .status(err.errorCode)
            .send({ message: err.errorMessage }));
});

app.get('/accountsData', integrityMiddleware, authenticationMiddleware, (req, res) => {
    if (req.body && req.body.bank == 'BT') {
        req.body.bank = bank_data.BT;
    }
    ServiceAllBank.getAccountsData({
        userId: req.userId
    })
        .then(obj => res
            .status(httpCodes.OK)
            .send(obj))
        .catch(err => {
            console.log(err);
            res
                .status(err.errorCode || 409)
                .send({ message: err.errorMessage || 'There was a problem with the banking servers. Application is unavailable for the moment.' })
        });
});

app.post('/confirmTransaction', integrityMiddleware, authenticationMiddleware, (req, res) => {

    //TODO. Make the transaction and return its content ?? IS IT DONE?
    const data = {
        username: req.body.username,
        password: req.body.password
    };

    let transactionData = {
        amount: parseFloat(req.body.amount),
        bank: req.body.bank,
        category: req.body.category,
        creditorIban: req.body.creditorIban,
        creditorName: req.body.creditorName,
        currency: req.body.currency,
        details: req.body.details,
        myAccountId: req.body.myAccountId,
        myIban: req.body.myIban,
        myName: req.body.myName,
        endToEndIdentification: '23111'
    };
    const userData = {
        userId: req.userId
    };

    ServiceAllBankAuth.login(data)
        .then(_ => { })
        .then(() => {
            if (transactionData.bank === 'BCR') {
                return ServiceBcr.postPayment(transactionData, userData);
            } else {
                return ServiceBt.postPayment(transactionData);
            }
        })
        .then(transaction => {
            let scaRedirect = "";
            if (transaction._links.scaRedirect != undefined) {
                scaRedirect = transaction._links.scaRedirect.href;
            }
            res
                .status(httpCodes.OK)
                .send({
                    paymentId: transaction.paymentId,
                    scaRedirect
                })
        })
        .catch(err => res.status(err.errorCode)
            .send({ message: err.errorMessage }));
});

app.post('/login', (req, res) => {
    const data = {
        username: req.body.username,
        password: req.body.password
    };
    ServiceAllBankAuth.login(data)
        .then(token => res
            .status(httpCodes.OK)
            .send({
                token
            }))
        .catch(err => res
            .status(err.errorCode)
            .send({ message: err.errorMessage }));
});


app.post('/bankAccountBcr', authenticationMiddleware, (req, res) => {
    const bank = bank_data.BCR;
    const data = {
        bankId: bank,
        accessToken: req.body.accessToken,
        refreshToken: req.body.refreshToken,
        userId: req.userId,
        consent: req.body.consent
    };

    ServiceAllBank.addBankAccount(data)
        .then(_ => ServiceAllBank.getBcrData(data))
        .then(obj => {
            res
                .status(httpCodes.OK)
                .send(obj)
        })
        .catch(err => {
            res
                .status(err.errorCode)
                .send({ message: err.errorMessage })
        });
});

app.post('/bankAccountBt', authenticationMiddleware, (req, res) => {
    const bank = bank_data.BT;
    const data = {
        bankId: bank,
        accessToken: req.body.accessToken,
        refreshToken: req.body.refreshToken,
        userId: req.userId,
        consent: req.body.consent
    };

    ServiceAllBank.addBankAccount(data)
        .then(_ => ServiceAllBank.getBtData(data))
        .then(obj => {
            res
                .status(httpCodes.OK)
                .send(obj)
        })
        .catch(err => {
            res
                .status(err.errorCode)
                .send({ message: err.errorMessage })
        });
});

app.post('/transactionCategory', authenticationMiddleware, (req, res) => {
    const data = {
        transactionId: req.body.transactionId,
        category: req.body.category
    };
    ServiceAllBank.addTransactionCategory(data)
        .then(_ => res
            .status(httpCodes.OK)
            .send({}))
        .catch(err => res
            .status(err.errorCode)
            .send({ message: err.errorMessage }));
});

app.patch('/user', integrityMiddleware, authenticationMiddleware, (req, res) => {
    const data = {
        phone: req.body.phone,
        email: req.body.email,
        lastName: req.body.lastName,
        firstName: req.body.firstName,
        currentPassword: req.body.currentPassword,
        newPassword: req.body.newPassword,
        userId: req.userId
    };
    ServiceAllBank.editUser(data)
        .then(_ => res
            .status(httpCodes.OK)
            .send({}))
        .catch(err => res
            .status(err.errorCode)
            .send({ message: err.errorMessage }));

});

// app.listen(80, () => {
//     console.log('HTTP server running!');
// });

https.createServer(httpOptions, app)
    .listen(port, () => {
        console.log(`Https server running on port ${port} `);
    });