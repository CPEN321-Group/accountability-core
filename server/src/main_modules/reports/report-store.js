const { UserReport, Report } = require("./report-models");
const { UserGoal } = require("../goals/goal-models");
const { UserTransaction } = require("../transactions/transaction-models");
const { fieldsAreNotNull } = require("../../utils/checks/get-defined-fields");
const { getItemFromList } = require("../../utils/get-from-list");
const { NotFoundError, ValidationError } = require("../../utils/errors");
const {Account} = require('../../main_modules/accounts/account-models.js')
const getOngoingGoals = (goals, startOfNextMonth) => {
  let ongoingGoals = goals.filter(goal => goal.deadline.getTime() > startOfNextMonth.getTime());
  return ongoingGoals;
}
const getMonthTransactions = (transactions, startOfNextMonth) => {
  const thisMonth = new Date(startOfNextMonth);
  thisMonth.setMonth(thisMonth.getMonth()-1,1);
  let monthTransactions = transactions.filter(transaction => 
    transaction.date.getTime() < startOfNextMonth.getTime() &&
    transaction.date.getTime() >= thisMonth.getTime()
  );
  return monthTransactions;
}
const getIncome = (transactions) => {
  return transactions.filter(transaction => transaction.isIncome);
}
const getSpendings = (transactions) => {
  return transactions.filter(transaction => !transaction.isIncome);
}
const getSavings = (income, spendings) => {
  let totalIncome = 0;
  income.forEach(inc => {totalIncome += inc.amount});

  let totalSpendings = 0;
  spendings.forEach(sp => {totalSpendings += sp.amount});

  return totalIncome - totalSpendings;
}

const getStartOfNextMonth = (date) => {
  let nextMonth = new Date(date);
  nextMonth.setHours(0, 0, 0, 0);
  nextMonth.setMonth(nextMonth.getMonth()+1,1);
  return nextMonth;
}
const getStartOfThisMonth = (date) => {
  let thisMonth = new Date(date);
  thisMonth.setHours(0, 0, 0, 0);
  thisMonth.setDate(1);
  return thisMonth;
}

const reportExists = async (accountId, monthYear) => {
  const userReport = await UserReport.findOne({userId: accountId});
  if (!userReport) {
    return false
  }
  const exists = userReport.reports.some(report => {
    const monthMatch = report.monthYear.getMonth() === monthYear.getMonth();
    const yearMatch = report.monthYear.getYear() === monthYear.getYear();
    return monthMatch && yearMatch;
  })
  return exists;
}

async function compileReport(accountId,mY) {
  const startOfNextMonth = getStartOfNextMonth(mY);
  const userGoal = await UserGoal.findOne({ userId: accountId });
  const ongoingGoals = getOngoingGoals(userGoal.goals, startOfNextMonth);

  const userTransaction = await UserTransaction.findOne({ userId: accountId });
  const thisMonthTransactions = getMonthTransactions(userTransaction.transactions, startOfNextMonth);
  const income = getIncome(thisMonthTransactions);
  const spendings = getSpendings(thisMonthTransactions);
  const savings = getSavings(income, spendings);

  const newReport = new Report({
    monthYear: getStartOfThisMonth(mY),
    income, spendings, savings,
    goalsInProgress: ongoingGoals
  });
  return newReport;
}

module.exports = {
  findReports: async (accountId, callback) => {
    
    try {
      const userReport = await UserReport.findOne({userId: accountId});
      if (!userReport) {
        return callback(null,404, new NotFoundError('account not found'));
      }
      return callback(null,200,userReport.reports);
    } catch (err) {
      return callback(null,400,err);
    }
  },
  createReport: async (accountId, monthYear, callback) => {
    try {
      if (!fieldsAreNotNull({monthYear})) {
        throw new ValidationError('missing params');
      }
      const mY = new Date(monthYear);
      if (!await UserReport.findOne({userId: accountId})) {
        return callback(null,404, new NotFoundError('account not found'));
      }
      if (await reportExists(accountId,mY)) {
        throw new ValidationError('report already exists');
      }
      const newReport = await compileReport(accountId, mY);
      const pushItem = {reports: newReport}
      await UserReport.updateOne(
        {userId: accountId}, 
        {$push: pushItem}
      )
      return callback(null,200, newReport);
      
    } catch (err) {
      console.log(err)
      return callback(null,400,err)
    }
  },
  updateAccountant: async (accountId,accountantId, callback) => {
    try {
      if(!await Account.findOne({accountId: accountantId, isAccountant: true})){
        return callback(null,404,new NotFoundError('accountant not found.'));
      }

      const userReport = await UserReport.findOneAndUpdate(
        {userId: accountId}, 
        {accountantId},
        {returnDocument: 'after', runValidators: true}
      )
      if (!userReport) {
        return callback(null,404, new NotFoundError('account not found'));
      }
      return callback(null,200,userReport);
    } catch (err) {
      return callback(null,400,err);
    }
  },
  deleteReports: async (accountId, callback) => {
    try {
      const userReport = await UserReport.findOneAndUpdate(
        {userId: accountId}, 
        {reports: []},
        {returnDocument: 'after', runValidators: true}
      );
      if (!userReport) {
        return callback(null,404,new NotFoundError('account not found'));
      }
      return callback(null,200,'reports deleted');
    } catch (err) {
      return callback(null,400,err);
    }
  },
  findReport: async (accountId,reportId,callback) => {
    try {
      const userReport = await UserReport.findOne({userId: accountId});
      if (!userReport) {
        return callback(null,404,new NotFoundError('account not found'));
      }
      const report = userReport.reports.find(r => r.id === reportId);
      if (!report) {
        return callback(null,404,new NotFoundError('report not found'));
      }
      return callback(null,200, report);
    } catch(err) {
      return callback(null,400,err);
    }
  },
  updateRecommendations: async (accountId,reportId, recommendations,callback) => {
    try {
      if (!fieldsAreNotNull({accountId,reportId,recommendations})) {
        throw new ValidationError('missing params');
      }
      const userReport = await UserReport.findOneAndUpdate(
        {$and:[{userId: accountId}, {reports: { $elemMatch: { _id: reportId }}}]},
        {"reports.$.recommendations": recommendations},
        {returnDocument: 'after', runValidators: true},
      )
      if (!userReport) {
        return callback(null,404, new NotFoundError('account/report not found'));
      }
      const report = userReport.reports.find(r => r.id === reportId)

      return callback(null,200,report);
    } catch (err) {
      return callback(null,400,err);
    }
  },
  deleteReport: async (accountId, reportId, callback) => {    
    try {
      const reportsMatch = {_id: reportId};
      const pullItem = {reports: reportsMatch};
      const userReport = await UserReport.findOneAndUpdate(
        {userId: accountId},
        {$pull: pullItem},
      )
      if (!userReport) {
        return callback(null,404,new NotFoundError('account not found'));
      }
      const report = getItemFromList(userReport.reports,reportId);
      if (!report) {
        return callback(null,404, new NotFoundError('report not found'));
      }
      return callback(null,200,'report deleted');
    } catch (err) {
      return callback(null,400,err);
    }
  },
  findUserReports: async (accountantId,callback) => {
    try {
      if(!await Account.findOne({accountId: accountantId, isAccountant: true})){
        return callback(null,404,new NotFoundError('accountant not found.'));
      }
      const userReports = await UserReport.find({accountantId});
      return callback(null,200,userReports);
    } catch (err) {
      return callback(null,400,err);
    }
  },
  deleteReportByMonthYear: async (accountId, monthYear, callback) => {    
    try {
      if (!fieldsAreNotNull({monthYear})) {
        throw new ValidationError('invalid monthYear provided');
      }
      const startOfThisMonth = getStartOfThisMonth(monthYear);
      const reportsMatch = {monthYear: startOfThisMonth};
      const pullItem = {reports: reportsMatch};
      const userReport = await UserReport.findOneAndUpdate(
        {userId: accountId},
        {$pull: pullItem},
      )
      if (!userReport) {
        return callback(null,404,new NotFoundError('account not found'));
      }
      const report = userReport.reports.some(r => new Date (r.monthYear).getTime() === startOfThisMonth.getTime());
      if (!report) {
        return callback(null,404, new NotFoundError('report not found'));
      }
      return callback(null,200,'report deleted');
    } catch (err) {
      console.log(err)
      return callback(null,400,err);
    }
  }
}