const mongoose = require('mongoose');
const goalDB = mongoose.createConnection('mongodb://localhost/goalDB')
const {r_string,r_bool,r_num,r_date} = require.main.require('./utils/types/mongoRequired')
const Frequency = require.main.require('./utils/types/frequency');

const goalSchema = new mongoose.Schema({
  id: r_string,
  title: r_string,
  mainCategory: r_string,
  dateSet: r_date,
  deadline: r_date,
  requiredAmount: r_num,
  assignedAmount: r_num,
  availableAmount: r_num,
  frequency: {...r_string, enum: Frequency}
})

const estimatedIncomeSchema = new mongoose.Schema({
  amount: r_num
})

const goalCategorySchema = new mongoose.Schema({ category: String })

const userGoalsSchema = new mongoose.Schema({
  userId: String,
  categories: [goalCategorySchema],
  estimatedIncomes: [estimatedIncomeSchema],
  goals: [goalSchema]
})

const UserGoal = goalDB.model('UserGoal',userGoalsSchema);

module.exports = {UserGoal};