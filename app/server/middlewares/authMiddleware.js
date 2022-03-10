const jwt = require('jsonwebtoken');
const { errors } = require('../utils/error/errors.js');
const { httpCodes } = require('../utils/http/httpCodes.js');

const authenticationMiddleware = (req, res, next) => {
  const token = req.headers.authorization ? req.headers.authorization.replace('Bearer ', '') : null;
  /* Check if the token is valid. If so, append the userId to the request */
  jwt.verify(token, process.env.JWT_SECRET, async (err, data) => {
    if (err) {
      res
        .status(httpCodes.UNAUTHORIZED)
        .send({ message: errors.SESSION_TIMED_OUT });
    } else {
      req.userId = data.userId;
      next();
    }
  });
};

module.exports = {
  authenticationMiddleware
}