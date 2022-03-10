class Error {
    constructor(errorName, errorCode) {
        this.errorMessage = errorName;
        this.errorCode = errorCode;
    }
};

module.exports = {
    Error
}