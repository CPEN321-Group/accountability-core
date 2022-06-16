const Frequency = require('../../utils/types/frequency');
const {Goal,EstimatedIncome} = require('./models');

const goal = {
  goalId: '1',
  title: 'Buy a House',
  mainCategory: 'long_term',
  dateSet: new Date('December 17, 1995 03:24:00'),
  deadline: new Date('December 17, 1999 03:24:00'),
  requiredAmount: 10000,
  assignedAmount: 500,
  availableAmount: 500,
  frequency: 'once'
}

module.exports = function(app) {
  app.route('/goals/:userId')
    .get((req,res) => {
      res.send(req.params);
    })
    .post((req,res) => {
      const userGoal = new Goal({...goal, userId: req.params.userId})
      userGoal.save(e=> e ? res.send(e) : res.send('saved goal successfully'))
    })
    .delete((req,res) => {
      res.send(req.params);
    })

  app.route('/goals/:userId/:goalId')
    .get((req,res) => {
      res.send(req.params);
    })
    .put((req,res) => {
      res.send(req.params);
    })
    .delete((req,res) => {
      res.send(req.params);
    })
}