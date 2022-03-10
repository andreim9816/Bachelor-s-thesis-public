const crypto = require('crypto');
const CryptoJS = require("crypto-js");
const bcrypt = require('bcrypt');
const fs = require('fs');
const moment = require('moment');
'use strict'

class Encrypt {
    static generateDigest = (payload) => {
        return crypto.createHash('sha256').update(payload, 'utf-8').digest('base64');
    };

    static generateSignature = (signingString) => {
        try {
            const privateKey = fs.readFileSync('./certs/bcr/bcr_sign_private_key.key');
            return crypto.createSign('sha256').update(signingString).sign(privateKey, 'base64');
        }
        catch (err) {
            console.log(err);
        } return "";
    };

    static generateSigningString = (method, endpoint, date, digest) => {
        return '(request-target): ' + method.toLowerCase() + ' ' + endpoint
            + '\n' + 'date: ' + date
            + '\n' + 'digest: ' + digest;
    };

    static buildSignatureHeaderWithKeyId = (method, endpoint, date, digest, keyId) => {
        const signingString = Encrypt.generateSigningString(method, endpoint, date, digest);
        const signature = Encrypt.generateSignature(signingString);
        return 'keyId="' + keyId + '\",algorithm=\"rsa-sha256\",headers=\"(request-target) date digest\",signature=\"' + signature + '"';
    };

    static buildBearerTokenHeader = (token) => {
        return 'Bearer ' + token;
    }

    static buildDateHeader = () => {
        return moment.utc().format('ddd, DD MMM yyyy HH:mm:ss') + ' GMT';
    };

    static buildDigestHeader = (payload) => {
        return 'SHA-256=' + Encrypt.generateDigest(payload);
    };

    static encryptData = (data) => {
        return CryptoJS.AES.encrypt(data, CryptoJS.enc.Utf8.parse(process.env.AES_SECRET), {
            mode: CryptoJS.mode.CBC,
            iv: CryptoJS.enc.Hex.parse(process.env.IV)
        }).toString();
    };

    static decryptData = (data) => {
        return CryptoJS.AES.decrypt(data, CryptoJS.enc.Utf8.parse(process.env.AES_SECRET), {
            mode: CryptoJS.mode.CBC,
            iv: CryptoJS.enc.Hex.parse(process.env.IV),
        }).toString(CryptoJS.enc.Utf8);
    }

    static hashData = (data) => {
        return bcrypt.hashSync(data, parseInt(process.env.SALT_ROUNDS));
    };
}

module.exports = {
    fs,
    crypto,
    Encrypt
};