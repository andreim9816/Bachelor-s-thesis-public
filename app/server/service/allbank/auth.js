const jwt = require('jsonwebtoken');
const bcrypt = require('bcrypt');
const models = require('../../models');
const { details } = require('../bcr/api_details.js');
const { errors } = require('../../utils/error/errors.js');
const { Error } = require('../../utils/error/error.js');
const { httpCodes } = require('../../utils/http/httpCodes.js')
const { Encrypt } = require('../../utils/encrypt.js');

class ServiceAllBankAuth {
    static getApiData = async () => {
        return {
            bcr_client_id: details.BCR_CLIENT_ID,
            keystore_secret_bcr: details.KEYSTORE_SECRET_BCR,
            keystore_secret_ing: details.KEYSTORE_SECRET_ING,
            keystore_secret_server: details.KEYSTORE_SECRET_SERVER
        }
    };

    static login = async (data) => {
        const user = await models.User.findOne({
            where: {
                username: Encrypt.encryptData(data.username)
            }
        });

        if (user) {
            if (bcrypt.compareSync(data.password, user.password)) {
                const token = jwt.sign({ userId: user.id }, process.env.JWT_SECRET, {
                    expiresIn: '3600000s'
                });
                return token;
            }
        }
        throw new Error(errors.WRONG_CREDENTIALS, httpCodes.UNAUTHORIZED);
    }
};

module.exports = {
    ServiceAllBankAuth
}