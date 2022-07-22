const { UserReport, Report } = require("./models");
const { UserGoal } = require("../goals/models");
const { UserTransaction } = require("../transactions/models");
const { fieldsAreNotNull } = require("../../utils/checks/get-defined-fields");
const { getItemFromList } = require("../../utils/get-from-list");

const getOngoingGoals = (goals, startOfNextMonth) => {
  let ongoingGoals = goals.filter(goal => goal.deadline.getTime() > startOfNextMonth.getTime());
  return ongoingGoals;
}
const getMonthTransactions = (transactions, startOfNextMonth) => {
  let monthTransactions = transactions.filter(transaction => transaction.date.getTime() < startOfNextMonth.getTime());
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
  const lastMonthTransactions = getMonthTransactions(userTransaction.transactions, startOfNextMonth);
  const income = getIncome(lastMonthTransactions);
  const spendings = getSpendings(lastMonthTransactions);
  const savings = getSavings(income, spendings);

  const newReport = new Report({
    monthYear: mY,
    income, spendings, savings,
    goalsInProgress: ongoingGoals
  });
  return newReport;
}

module.exports = {
  findReports: async (accountId, callback) => {
    if(callback);
    try {
      const userReport = await UserReport.findOne({userId: accountId});
      if (!userReport) {
        return callback(null,404, 'account not found');
      }
      return callback(null,200,userReport.reports);
    } catch (err) {
      return callback(null,400,err);
    }
  },
  createReport: async (accountId, monthYear, callback) => {
    if(callback);
    try {
      if (!fieldsAreNotNull({monthYear})) {
        return callback(null,400, 'missing params');
      }
      const mY = new Date(monthYear);
      if (!await UserReport.findOne({userId: accountId})) {
        return callback(null,404, 'account not found');
      }
      if (await reportExists(accountId,mY)) {
        return callback(null,400,'report already exists');
      }
      const newReport = await compileReport(accountId, mY);
      const pushItem = {reports: newReport}
      await UserReport.updateOne(
        {userId: accountId}, 
        {$push: pushItem}
      )
      return callback(null,200, newReport);
      
    } catch (err) {
      return callback(null,400,err)
    }
  },
  updateAccountant: async (accountId,accountantId, callback) => {
    if(callback);
    try {
      const userReport = await UserReport.findOneAndUpdate(
        {userId: accountId}, 
        {accountantId},
        {returnDocument: 'after', runValidators: true}
      )
      if (!userReport) {
        return callback(null,404, 'account not found');
      }
      return callback(null,200,userReport);
    } catch (err) {
      return callback(null,400,err);
    }
  },
  deleteReports: async (accountId, callback) => {
    if(callback);
    try {
      const userReport = await UserReport.findOneAndUpdate(
        {userId: accountId}, 
        {reports: []},
        {returnDocument: 'after', runValidators: true}
      );
      if (!userReport) {
        return callback(null,404,'account not found');
      }
      return callback(null,200,'reports deleted');
    } catch (err) {
      return callback(null,400,err);
    }
  },
  findReport: async (accountId,reportId,callback) => {
    if(callback);
    try {
      const userReport = await UserReport.findOne({userId: accountId});
      if (!userReport) {
        return callback(null,404,'account not found');
      }
      const report = userReport.reports.find(r => r.id === reportId);
      if (!report) {
        return callback(null,404,'report not found');
      }
      return callback(null,200, report);
    } catch(err) {
      return callback(null,400,err);
    }
  },
  updateRecommendations: async (accountId,reportId, recommendations,callback) => {
    if(callback);
    try {
      if (!fieldsAreNotNull({accountId,reportId,recommendations})) {
        return callback(null,400,'missing params');
      }
      const userReport = await UserReport.findOneAndUpdate(
        {$and:[{userId: accountId}, {reports: { $elemMatch: { _id: reportId }}}]},
        {"reports.$.recommendations": recommendations},
        {returnDocument: 'after', runValidators: true},
      )
      if (!userReport) {
        return callback(null,404, 'account/report not found');
      }
      const report = userReport.reports.find(r => r.id === reportId)
      if (!report) {
        return callback(null,404, 'report not found');
      }
      return callback(null,200,report);
    } catch (err) {
      return callback(null,400,err);
    }
  },
  deleteReport: async (accountId, reportId, callback) => {
    if(callback);
    try {
      const reportsMatch = {_id: reportId};
      const pullItem = {reports: reportsMatch};
      const userReport = await UserReport.findOneAndUpdate(
        {userId: accountId},
        {$pull: pullItem},
      )
      if (!userReport) {
        return callback(null,404,'account not found');
      }
      const report = getItemFromList(userReport.reports,reportId);
      if (!report) {
        return callback(null,404, 'report not found');
      }
      return callback(null,200,'report deleted');
    } catch (err) {
      return callback(null,400,err);
    }
  },
  findUserReports: async (accountantId,callback) => {
    if(callback);
    try {
      const userReports = await UserReport.find({accountantId});
      return callback(null,200,userReports);
    } catch (err) {
      return callback(null,400,err);
    }
  }
}