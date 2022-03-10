'use strict';
const {
  Model
} = require('sequelize');
module.exports = (sequelize, DataTypes) => {
  class BankAccount extends Model {
    /**
     * Helper method for defining associations.
     * This method is not a part of Sequelize lifecycle.
     * The `models/index` file will call this method automatically.
     */
    static associate(models) {
      // define association here
      models.BankAccount.belongsTo(models.User);
    }
  };
  BankAccount.init({
    bankId: DataTypes.STRING,
    accessToken: DataTypes.STRING,
    refreshToken: DataTypes.STRING,
    consent: DataTypes.STRING
  }, {
    sequelize,
    modelName: 'BankAccount',
  });
  return BankAccount;
};