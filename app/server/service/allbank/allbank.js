require('dotenv').config();
const models = require('../../models');
const moment = require('moment');
const bcrypt = require('bcrypt');
const { errors } = require('../../utils/error/errors.js'); 1
const { Error } = require('../../utils/error/error.js');
const { httpCodes } = require('../../utils/http/httpCodes.js');
const { Encrypt } = require('../../utils/encrypt.js');
const { bank_data } = require('../../utils/data/bank_data.js');
const { ServiceBcr } = require('../bcr/bcr.js');
const { ServiceBt } = require('../bt/bt.js');
const { ServiceBcrAuth } = require('../bcr/bcr_auth.js');
const { ServiceBtAuth } = require('../bt/bt_auth.js');
const { generateRequestid } = require('../../utils/data/request_id');


class ServiceAllBank {
    static createUser = async (data) => {
        try {
            const user = await models.User.create({
                username: Encrypt.encryptData(data.username),
                password: Encrypt.hashData(data.password),
                firstName: Encrypt.encryptData(data.firstName),
                lastName: Encrypt.encryptData(data.lastName),
                phone: Encrypt.encryptData(data.phone),
                email: Encrypt.encryptData(data.email)
            });
            return user;
        } catch (err) {
            throw new Error(errors.USERNAME_EXISTS, httpCodes.CONFLICT);
        }
    };

    static addBankAccount = async (data) => {
        const user = await models.User.findByPk(data.userId);
        if (user == null) {
            throw new Error(errors.USER_NOT_EXISTS, httpCodes.NOT_FOUND);
        }

        let consent = generateRequestid().substring(0, 8);
        if (data.consent) {
            consent = data.consent;
        }
        const bankAccount = await user.createBankAccount({
            bankId: Encrypt.encryptData(data.bankId),
            accessToken: Encrypt.encryptData(data.accessToken),
            refreshToken: Encrypt.encryptData(data.refreshToken),
            consent: Encrypt.encryptData(data.consent)
        });

        return bankAccount;
    };

    static getAccounts = async (data) => {
        const user = await models.User.findByPk(data.userId);
        const userAccountList = await user.getBankAccounts();

        let bcrAccountsArray = [];
        let btAccountsArray = [];

        /* HashMap with all transactions stored in database */
        let transactionsMap = new Map();
        const transactionsDb = await models.Transaction.findAll();

        transactionsDb.map(t => {
            transactionsMap.set(t.id, t.transactionType)
        });

        /* For each account, take get its transactions */
        for (let [index, account] of userAccountList.entries()) {
            let access_token = Encrypt.decryptData(account.accessToken);
            const bankId = Encrypt.decryptData(account.bankId);


            switch (bankId) {
                case bank_data.BCR:
                    let bcrAccounts = await ServiceBcr.getAccounts(access_token);
                    if (!bcrAccounts || (bcrAccounts.errors && bcrAccounts.errors[0] && bcrAccounts.errors[0].error)) {
                        const refresh_token = Encrypt.decryptData(account.refreshToken);
                        const newAccessTokenObject = await ServiceBcrAuth.reqNewAccessToken(refresh_token);
                        if (!newAccessTokenObject) {
                            // refresh token has expired
                            // TODO ask for login in UI
                        } else {
                            userAccountList[index].accessToken = Encrypt.encryptData(newAccessTokenObject.access_token);
                            await account.save();
                            bcrAccounts = await ServiceBcr.getAccounts(newAccessTokenObject.access_token, transactionsMap);

                            if (bcrAccounts.accounts != null) {
                                await this.addTransactionsAndBalancesToAccountBcr(bcrAccounts.accounts, newAccessTokenObject.access_token, transactionsMap);
                            }
                        }
                    } else {
                        await this.addTransactionsAndBalancesToAccountBcr(bcrAccounts.accounts, access_token, transactionsMap);
                    }
                    if (bcrAccounts.accounts) {
                        bcrAccountsArray = bcrAccountsArray.concat(bcrAccounts.accounts);
                    }
                    break;

                case bank_data.BT:
                    let btAccounts = await ServiceBt.getAccounts(access_token, ServiceBt.consentIdAIS);
                    if (!btAccounts || (btAccounts.tppMessages && btAccounts.tppMessages[0].category === 'ERROR')) {
                        const refresh_token = Encrypt.decryptData(account.refreshToken);
                        const newAccessTokenObject = await ServiceBtAuth.reqNewAccessToken(refresh_token);
                        if (!newAccessTokenObject) {
                            // refresh token has expired
                            // TODO ask for login in UI
                        } else {
                            userAccountList[index].accessToken = Encrypt.encryptData(newAccessTokenObject.access_token);
                            userAccountList[index].refreshToken = Encrypt.encryptData(newAccessTokenObject.refresh_token);
                            userAccountList[index].consent = Encrypt.encryptData(newAccessTokenObject.consents);
                            ServiceBt.consentIdAIS = newAccessTokenObject.consents;
                            await account.save();

                            btAccounts = await ServiceBt.getAccounts(newAccessTokenObject.access_token, newAccessTokenObject.consents);
                            if (btAccounts.accounts != null) {
                                await this.addTransactionsToAccountBt(btAccounts.accounts, newAccessTokenObject.access_token, transactionsMap);
                            }
                        }
                    } else {
                        await this.addTransactionsToAccountBt(btAccounts.accounts, access_token, transactionsMap);
                    }
                    if (btAccounts.accounts) {
                        btAccountsArray = btAccountsArray.concat(btAccounts.accounts);
                    }
                    break;
            }
        };

        transactionsMap = null;

        return {
            bcrAccounts: bcrAccountsArray,
            btAccounts: btAccountsArray
        }
    };

    /**
     * Function that returns Bcr accounts data (list of accounts and transactions)
     * @param {*} data 
     */
    static getBcrData = async (data) => {
        const bcrAccounts = await ServiceBcr.getAccounts(data.accessToken);
        await this.addTransactionsAndBalancesToAccountBcrOnNewAccount(bcrAccounts.accounts, data.accessToken);
        return bcrAccounts;
    };

    /**
     * Function that returns Bt accounts data (list of accounts and transactions)
     * @param {*} data 
     */
    static getBtData = async (data) => {
        const btAccounts = await ServiceBt.getAccounts(data.accessToken, data.consent);
        await this.addTransactionsAndBalancesToAccountBtOnNewAccount(btAccounts.accounts, data.accessToken, data.consent);
        return btAccounts;
    };

    /**
     * Function that returns user's data
     * @param {json} data ojbect containing userId 
     * @returns Json containg data about user 
     */
    static getUser = async (data) => {
        const user = await models.User.findByPk(data.userId);
        return {
            username: Encrypt.decryptData(user.username),
            firstName: Encrypt.decryptData(user.firstName),
            lastName: Encrypt.decryptData(user.lastName),
            phone: Encrypt.decryptData(user.phone),
            email: Encrypt.decryptData(user.email),
            passwordHash: user.password
        };
    };

    static getAccountsData = async (data) => {
        let accounts = await this.getAccounts(data);
        accounts.budgets = await this.getBudgets(data);
        accounts.user = await this.getUser(data);
        return accounts;
    };

    static sleep = (ms) => {
        return new Promise(resolve => setTimeout(resolve, ms));
    }

    static getBudgets = async (data) => {
        const user = await models.User.findByPk(data.userId);
        if (user == null) {
            throw new Error(errors.USER_NOT_EXISTS, httpCodes.NOT_FOUND);
        }

        const budgetsEncrypted = await user.getBudgets();
        const budgetsUnEncrypted = budgetsEncrypted.map(budget => {
            return {
                id: budget.id,
                category: Encrypt.decryptData(budget.category),
                budget: parseFloat(Encrypt.decryptData(budget.budget)),
                startDate: Encrypt.decryptData(budget.startDate),
                endDate: Encrypt.decryptData(budget.endDate)
            }
        });
        return budgetsUnEncrypted;
    };

    static setRefreshToken = async (data) => {
        const user = await models.User.findByPk(data.userId);
        if (user == null) {
            throw new Error(errors.USER_NOT_EXISTS, httpCodes.NOT_FOUND);
        }

        /* Check if the user has access to the bank account */
        const bankAccount = await models.BankAccount.findByPk(data.accountId);

        if (!bankAccount) {
            throw new Error(errors.ACCOUNT_NOT_EXISTS, httpCodes.NOT_FOUND);
        }

        const userAccount = await bankAccount.getUser();
        if (user.id == userAccount.id) {
            bankAccount.refreshToken = Encrypt.encryptData(data.refreshToken);
            await bankAccount.save();
            return bankAccount;
        }
        return new Error(errors.UNAUTHORIZED, httpCodes.UNAUTHORIZED);
    };

    static setAccessToken = async (data) => {
        const user = await models.User.findByPk(data.userId);
        if (user == null) {
            throw new Error(errors.USER_NOT_EXISTS, httpCodes.NOT_FOUND);
        }

        /* Check if the user has access to the bank account */
        const bankAccount = await models.BankAccount.findByPk(data.accountId);

        if (!bankAccount) {
            throw new Error(errors.ACCOUNT_NOT_EXISTS, httpCodes.NOT_FOUND);
        }

        const userAccount = await bankAccount.getUser();
        if (user.id == userAccount.id) {
            bankAccount.accessToken = Encrypt.encryptData(data.accessToken);
            await bankAccount.save();
            return bankAccount;
        }
        return new Error(errors.UNAUTHORIZED, httpCodes.UNAUTHORIZED);
    };

    static getBankAccount = async (data) => {
        const user = await models.User.findByPk(data.userId);
        if (user == null) {
            throw new Error(errors.USER_NOT_EXISTS, httpCodes.NOT_FOUND);
        }

        /* Check if the user has access to the bank account */
        const bankAccount = await models.BankAccount.findByPk(data.accountId);

        if (!bankAccount) {
            throw new Error(errors.ACCOUNT_NOT_EXISTS, httpCodes.NOT_FOUND);
        }

        const userAccount = await bankAccount.getUser();
        if (user.id == userAccount.id) {
            return bankAccount;
        }
        return new Error(errors.UNAUTHORIZED, httpCodes.UNAUTHORIZED);
    };

    static getRefreshToken = async (data) => {
        const account = await this.getBankAccount(data);
        return account.refreshToken;
    };

    static getAccessToken = async (data) => {
        const account = await this.getBankAccount(data);
        return account.accessToken;
    };

    static addTransactionsAndBalancesToAccountBcr = async (accounts, access_token, transactionsMap) => {
        for (const account of accounts) {
            let transactionsObject = await ServiceBcr.getTransactions(account.resourceId, access_token);
            let balancesObject = await ServiceBcr.getBalance(account.resourceId, access_token);

            if (transactionsObject.transactions.booked != null) {
                transactionsObject.transactions.booked = await this.addTransactionCategoryType(transactionsObject.transactions.booked, transactionsMap);
            }

            if (transactionsObject.transactions.pending != null) {
                transactionsObject.transactions.pending = await this.addTransactionCategoryType(transactionsObject.transactions.pending, transactionsMap);
            }

            account.transactions = {
                booked: transactionsObject.transactions.booked,
                pending: transactionsObject.transactions.pending
            };

            account.balance = balancesObject.balances[0].balanceAmount.amount;
            account.currency = balancesObject.balances[0].balanceAmount.currency;

            account._links = undefined;
        }
    };

    static addTransactionsAndBalancesToAccountBcrOnNewAccount = async (accounts, access_token) => {
        for (const account of accounts) {
            let transactionsObject = await ServiceBcr.getTransactions(account.resourceId, access_token);
            let balancesObject = await ServiceBcr.getBalance(account.resourceId, access_token);

            account.transactions = {
                booked: transactionsObject.transactions.booked,
                pending: transactionsObject.transactions.pending
            };

            account.balance = balancesObject.balances[0].balanceAmount.amount;
            account.currency = balancesObject.balances[0].balanceAmount.currency;

            account._links = undefined;
        }
    };

    static addTransactionsAndBalancesToAccountBtOnNewAccount = async (accounts, access_token, consent) => {
        for (const account of accounts) {
            let transactionsObject = await ServiceBt.getTransactions(access_token, account.resourceId, consent);

            if (!transactionsObject || (transactionsObject.tppMessages && transactionsObject.tppMessages[0].category === 'ERROR')) {
                return;
            }

            account.transactions = {
                booked: transactionsObject.transactions.booked,
                pending: transactionsObject.transactions.pending
            };

            account.balance = account.balances[0].balanceAmount.amount;
            account.currency = account.balances[0].balanceAmount.currency;

            account.balances = undefined;
            account._links = undefined;
        }
    };

    static addTransactionsToAccountBt = async (accounts, access_token, transactionsMap) => {
        for (const account of accounts) {
            let transactionsObject = await ServiceBt.getTransactions(access_token, account.resourceId, ServiceBt.consentIdAIS);

            if (!transactionsObject || (transactionsObject.tppMessages && transactionsObject.tppMessages[0].category === 'ERROR')) {
                return;
            }

            if (transactionsObject.transactions.booked != null) {
                transactionsObject.transactions.booked = await this.addTransactionCategoryType(transactionsObject.transactions.booked, transactionsMap);
            }

            if (transactionsObject.transactions.pending != null) {
                transactionsObject.transactions.pending = await this.addTransactionCategoryType(transactionsObject.transactions.pending, transactionsMap);
            }

            account.transactions = {
                booked: transactionsObject.transactions.booked,
                pending: transactionsObject.transactions.pending
            };

            account.balance = account.balances[0].balanceAmount.amount;
            account.currency = account.balances[0].balanceAmount.currency;

            account.balances = undefined;
            account._links = undefined;
        }
    };

    static addTransactionCategoryType = async (transactionsList, transactionsMap) => {
        return transactionsList.map(transaction => {
            const transactionCategoryEncrypted = transactionsMap.get(Encrypt.encryptData(transaction.transactionId));
            if (transactionCategoryEncrypted != null) {
                transaction.transactionCategory = Encrypt.decryptData(transactionCategoryEncrypted);
            }
            return transaction;
        });
    };

    static newBudget = async (data) => {
        const user = await models.User.findByPk(data.userId);
        if (user == null) {
            throw new Error(errors.USER_NOT_EXISTS, httpCodes.NOT_FOUND);
        }

        let startDateString;
        let endDateString;

        if (data.budget.startDate.iMillis) {
            startDateString = moment(data.budget.startDate.iMillis).format('YYYY-MM-DD');
            endDateString = moment(data.budget.endDate.iMillis).format('YYYY-MM-DD');
        } else {
            startDateString = moment(data.budget.startDate).format('YYYY-MM-DD');
            endDateString = moment(data.budget.endDate).format('YYYY-MM-DD');
        }

        const budget = data.budget.budget + "";
        const category = data.budget.category;

        const budgetObj = await user.createBudget({
            category: Encrypt.encryptData(category),
            budget: Encrypt.encryptData(budget),
            startDate: Encrypt.encryptData(startDateString),
            endDate: Encrypt.encryptData(endDateString)
        });
        return budgetObj;
    }

    static editBudget = async (data) => {
        const user = await models.User.findByPk(data.userId);
        if (user == null) {
            throw new Error(errors.USER_NOT_EXISTS, httpCodes.NOT_FOUND);
        }

        const startDateString = moment(data.budget.startDate.iMillis).format('YYYY-MM-DD');
        const endDateString = moment(data.budget.endDate.iMillis).format('YYYY-MM-DD');
        const budget = data.budget.budget + "";
        const category = data.budget.category;

        const budgetObj = await models.Budget.findByPk(data.budget.id);

        budgetObj.startDate = Encrypt.encryptData(startDateString);
        budgetObj.endDate = Encrypt.encryptData(endDateString);
        budgetObj.budget = Encrypt.encryptData(budget);
        budgetObj.category = Encrypt.encryptData(category);

        await budgetObj.save();
        return budgetObj;
    }

    static addTransactionCategory = async (data) => {
        const transactionCategory = await models.Transaction.create({
            id: Encrypt.encryptData(data.transactionId),
            transactionType: Encrypt.encryptData(data.category)
        });
        return transactionCategory;
    };

    static editUser = async (data) => {
        const user = await models.User.findByPk(data.userId);
        if (user == null) {
            throw new Error(errors.USER_NOT_EXISTS, httpCodes.NOT_FOUND);
        }

        if (bcrypt.compareSync(data.currentPassword, user.password)) {
            user.phone = Encrypt.encryptData(data.phone);
            user.email = Encrypt.encryptData(data.email);
            user.lastName = Encrypt.encryptData(data.lastName);
            user.firstName = Encrypt.encryptData(data.firstName);

            if (data.newPassword != '') {
                user.password = Encrypt.hashData(data.newPassword)
            }

            await user.save();
            return user;
        } else {
            throw new Error(errors.WRONG_PASSWORD, httpCodes.FORBIDDEN);
        }
    };
};

module.exports = {
    ServiceAllBank
}