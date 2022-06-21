const mongoose = require('mongoose');
const goalDB = mongoose.createConnection('mongodb://localhost/goalDB')
const {r_string,r_bool,r_num,r_date} = require.main.require('./utils/types/mongoRequired')

const goalSchema = new mongoose.Schema({
  title: r_string,
  target: r_num,
  current: r_num,
  deadline: r_date,
})

const userGoalsSchema = new mongoose.Schema({
  userId: r_string,
  goals: [goalSchema]
})

const UserGoal = goalDB.model('UserGoal',userGoalsSchema);

module.exports = {UserGoal, goalSchema};