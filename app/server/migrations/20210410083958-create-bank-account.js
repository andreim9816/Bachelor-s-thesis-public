'use strict';
module.exports = {
  up: async (queryInterface, Sequelize) => {
    await queryInterface.createTable('BankAccounts', {
      id: {
        allowNull: false,
        autoIncrement: true,
        primaryKey: true,
        type: Sequelize.INTEGER
      },
      userId: {
        allowNull: false,
        type: Sequelize.INTEGER,
        references: {
          model: {
            tableName: 'Users'
          },
          key: 'id'
        }
      },
      bankId: {
        allowNull: false,
        type: Sequelize.STRING
      },
      accessToken: {
        type: Sequelize.STRING(1000)
      },
      refreshToken: {
        type: Sequelize.STRING(1000)
      },
      consent: {
        type: Sequelize.STRING(256)
      },
      createdAt: {
        allowNull: false,
        type: Sequelize.DATE
      },
      updatedAt: {
        allowNull: false,
        type: Sequelize.DATE
      }
    });
  },
  down: async (queryInterface, Sequelize) => {
    await queryInterface.dropTable('BankAccounts');
  }
};