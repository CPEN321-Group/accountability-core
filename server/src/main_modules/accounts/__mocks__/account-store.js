const account = {
  accountId: '1234',
  profile: {
    firstname: 'Bob',
    lastname: 'Jones',
    email: 'test123@gmail.com',
    age: 25,
    profession: 'Student'
  },
  isAccountant: false,
  reviews: [],
  subscription: {
    subscriptionDate: '2023',
    expiryDate: '2024'
  }
};

module.exports = {
  createAccount: jest.fn(async (fields,callback) => {
    if (!(fields.accountId && fields.firstname && fields.lastname && 
      fields.email && fields.age && fields.profession)) {
      return callback(null,400,'missing params');
    } else if (fields.accountId === '1234') {
      return callback(null,400,'account already exists');
    } else {
      return callback(null,200,account);
    }
  }),
  findAccount: jest.fn(async (accountId,callback) => {
    if (accountId !== '1234') {
      return callback(null,404,'account not found');
    } else {
      return callback(null,200,account);
    }
  }),
  findAccountants: jest.fn(async (callback) => {
    return callback(null,200,[]);
  }),
  updateProfile: jest.fn(async (id,data,callback) => {
    if (id !== '1234') {
      return callback(null,404,'account not found');
    }
    return callback(null,200, account);
  }),
  deleteAccount: jest.fn(async (id,callback) => {
    if (id !== '1234') {
      return callback(null,404,'account not found');
    }
    return callback(null,200, 'account deleted');
  }),
  createReview: jest.fn(async (accountantId,fields,callback) => {
    if (accountantId !== '1234') {
      return callback(null,404,'accountant not found');
    }
    return callback(null,200, account);
  }),
  createSubscription: jest.fn(async (id,fields,callback) => {
    if (id !== '1234') {
      return callback(null,404,'account not found');
    }
    return callback(null,200, account);
  }),
  updateSubscription: jest.fn(async (id,fields,callback) => {
    if (id !== '1234') {
      return callback(null,404,'account not found');
    }
    return callback(null,200, account);
  })
}