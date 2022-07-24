const mongoose = require('mongoose');
const { r_string, r_num, r_date } = require('../../../utils/types/mongo-required');
const goalDB = mongoose.createConnection((process.env.MONGO_BASE_URL || 'mongodb://localhost') + '/goalDB')

const goalSchema = new mongoose.Schema({
  title: r_string,
  target: r_num,
  current: r_num,
  deadline: r_date,
},{timestamps:true});

const userGoalSchema = new mongoose.Schema({
  userId: r_string,
  goals: [goalSchema]
})

const Goal = goalDB.model('Goal',goalSchema);
const UserGoal = goalDB.model('UserGoal',userGoalSchema);

module.exports = {UserGoal, goalSchema, Goal};
