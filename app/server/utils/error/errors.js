const errors = {
    USER_NOT_EXISTS: 'This user does not exist!',
    ACCOUNT_NOT_EXISTS: 'This bank account does not exist!',
    USERNAME_EXISTS: 'This username is taken. Please choose another one!',
    WRONG_CREDENTIALS: 'The credentials that were provided are wrong!',
    UNAUTHORIZED: 'You don\'t have access to this resource!',
    SESSION_TIMED_OUT: 'Session timed out! You need to login in again!',
    INTEGRITY_FAILED: 'Error during server communication. Please try again!',
    WRONG_PASSWORD: 'Wrong password!'
};

module.exports = {
    errors
};