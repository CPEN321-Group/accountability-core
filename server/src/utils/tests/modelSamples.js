const {User,Accountant} = require.main.require('./main_modules/accounts/models');
const {UserGoal} = require.main.require('./main_modules/goals/models');

module.exports = {
//-----------------ACCOUNTS---------------//
user0: new User({
  id: '1', //test missing fields
}),
user1: new User({
  id: '1',
  secretKey: 'abc123',
  subscribed: false,
  subscriptionDate: 'June 2, 2022', //test wrong type --> mongoose auto converts to date
  hasAccountant: false,
  profile: {
    firstname: 'Dean',
    lastname: 'Yang',
    email: 'test123@gmail.com',
    age: 20,
    profession: 'Student'
  }
}),
user2: new User({
  id: '2',
  secretKey: 'abc123',
  subscribed: false,
  subscriptionDate: new Date('June 3 2022'),
  hasAccountant: false,
  profile: {
    firstname: 'Maggie',
    lastname: 'Lin',
    email: '123456@gmail.com',
    age: 19,
    profession: 'Student'
  }
}),
accountant0: new Accountant({
  id: '3', //must be unique across all accounts
  secretKey: 'abc123',
  reviews: [],
  profile: {
    firstname: '', //test empty string
    lastname: '    ',//should also check for whitespace
    email: '',
    age: 24,
    profession: 'Accountant'
  },
}),
//-----------------GOALS---------------//
goal1: {
  id: '1',
  title: 'Buy a House',
  mainCategory: 'long_term',
  dateSet: new Date('December 17, 1995 03:24:00'),
  deadline: new Date('December 17, 1999 03:24:00'),
  requiredAmount: 10000,
  assignedAmount: 500,
  availableAmount: 500,
  frequency: 'once'
},
//-----------------MESSAGING---------------//
//-----------------REPORTS---------------//
//-----------------TRANSACTIONS---------------//
transactions: [
  {
    
  }
]

}