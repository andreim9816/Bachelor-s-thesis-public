require('dotenv').config();
const { errors } = require('../utils/error/errors.js');
const { httpCodes } = require('../utils/http/httpCodes.js');
const { Encrypt } = require('../utils/encrypt.js');
const CryptoJS = require("crypto-js");


const integrityMiddleware = (req, res, next) => {
    try {
        const method = req.method;
        let digest;
        if (req.rawBody) {
            digest = "SHA-256=" + Encrypt.generateDigest(modifyUrlEncoded(req.rawBody));
        } else {
            digest = "SHA-256=" + Encrypt.generateDigest("");
        }
        const digestHeader = req.headers.digest;

        if (digest != digestHeader) {
            throw new Error();
        }
        const url = req.url;
        const date = req.headers.date;
        const signingString = Encrypt.generateSigningString(method, url, date, digest);

        const signatureSplited = req.headers.signature.split(',');

        const keyId = signatureSplited[0].split('keyId=')[1];
        if ('allbank:id:key:client' != keyId.substring(1, keyId.length - 1)) {
            throw new Error();
        }

        const algorithm = signatureSplited[1].split('algorithm=')[1];
        if ('hmac-sha256' != algorithm.substring(1, algorithm.length - 1)) {
            throw new Error();
        }

        const headers = signatureSplited[2].split('headers=')[1];
        if ('(request-target) date digest' != headers.substring(1, headers.length - 1)) {
            throw new Error();
        }

        const signature = signatureSplited[3].split('signature=')[1];

        const hmac = CryptoJS.HmacSHA256(signingString, process.env.REQUEST_SIGNING_SECRET).toString(CryptoJS.enc.Base64);
        if (hmac != signature.substring(1, signature.length - 1)) {
            throw new Error();
        }

        /* No error found, message was not altered, go to the next middleware */
        next();
    } catch (error) {
        res
            .status(httpCodes.UNAUTHORIZED)
            .send({ message: errors.INTEGRITY_FAILED });
    }
};

const modifyUrlEncoded = (url) => {
    let decoded = decodeURI(url);
    decoded = decoded.replace(/%40/g, '@');
    decoded = decoded.replace(/%3A/g, ':');
    decoded = decoded.replace(/%2B/g, '+');
    return decoded;
}
module.exports = {
    integrityMiddleware
}