'use strict';
const {
  Model
} = require('sequelize');
module.exports = (sequelize, DataTypes) => {
  class Budget extends Model {
    /**
     * Helper method for defining associations.
     * This method is not a part of Sequelize lifecycle.
     * The `models/index` file will call this method automatically.
     */
    static associate(models) {
      // define association here
      models.Budget.belongsTo(models.User);
    }
  };
  Budget.init({
    category: DataTypes.STRING,
    budget: DataTypes.STRING,
    startDate: DataTypes.STRING,
    endDate: DataTypes.STRING
  }, {
    sequelize,
    modelName: 'Budget',
  });
  return Budget;
};