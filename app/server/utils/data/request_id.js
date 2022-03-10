const { v4: uuidv4 } = require('uuid');

const generateRequestid = () => {
    return uuidv4();
};

module.exports = {
    generateRequestid
}