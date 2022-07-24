jest.mock("../../main_modules/reports/models");
jest.mock("../../main_modules/goals/models");
jest.mock("../../main_modules/transactions/models");

const { default: mongoose } = require("mongoose");
const { createAccount, findAccount, findAccountants, updateProfile, deleteAccount, createReview, createSubscription, updateSubscription } = require("../../main_modules/accounts/account-store");


const existingId = '1234'
const nonExistingId = 'ai93n';
const accountantId = '1456'
const accountFields = {
  accountId: existingId,
  firstname: 'Bob',
  lastname: 'Jones',
  email: 'test123@gmail.com',
  age: 25,
  profession: 'Student',
  isAccountant: false
}

const accountantFields = {
  accountId: accountantId,
  firstname: 'Mary',
  lastname: 'Smith',
  email: 'test123@gmail.com',
  age: 27,
  profession: 'Accountant',
  isAccountant: true
}
const reviewFields = {
  authorId: existingId,
  rating: 5,
  date: 'Jun 2022',
  title: 'Good work',
  content: 'Thank you'
}
const subscriptionFields = {
  subscriptionDate: new Date('Jun 2022'),
  expiryDate: new Date('July 2022')
}

beforeAll(done => {
  jest.mock('../../main_modules/reports/models')
  jest.mock('../../main_modules/transactions/models')
  jest.mock('../../main_modules/goals/models')
  done()
})

describe('testing createAccount', () => {
  test('accountId in use', async () => {
    await createAccount(accountFields, () => undefined); //create accountId = 1234
    await createAccount(accountFields, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name', 'ValidationError');
    })
  }) 

  test('some fields are missing', async () => {
    await deleteAccount(nonExistingId, ()=>undefined);
    await createAccount(
      {accountId: nonExistingId, firstname: 'Bob'}, 
      (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name', 'ValidationError');
    })
  })
  test('empty string was used', async () => {
    const modifiedAccountFields = { ...accountFields };
    modifiedAccountFields.accountId = nonExistingId;
    modifiedAccountFields.firstname = '';
    await createAccount(modifiedAccountFields, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name', 'ValidationError');
    })
  })

  test('accountId is null', async () => {
    await createAccount(null, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name', 'ValidationError');
    })
  })
  test('invalid age', async () => {
    await deleteAccount(nonExistingId, ()=> undefined);
    const modifiedAccountFields = { ...accountFields };
    modifiedAccountFields.accountId = nonExistingId;
    modifiedAccountFields.age = -1;
    await createAccount(modifiedAccountFields, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name', 'ValidationError');
    })
  })

  test('illegal characters for some params', async () => {
    const modifiedAccountFields = { ...accountFields };
    modifiedAccountFields.accountId = nonExistingId;
    modifiedAccountFields.firstname = '$%()#';
    await createAccount(modifiedAccountFields, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name', 'ValidationError');
    })
  })


  test('create user', async () => {
    const modifiedAccountFields = {...accountFields};
    modifiedAccountFields.accountId = nonExistingId
    await deleteAccount(modifiedAccountFields.accountId, () => undefined);
    await createAccount(modifiedAccountFields, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(200);
      expect(returnData).toHaveProperty('accountId');
    })
    await deleteAccount(modifiedAccountFields.accountId, () => undefined);
  })
  test('create accountant', async () => {
    const modifiedAccountFields = {...accountFields};
    modifiedAccountFields.accountId = 'accountanttest'
    modifiedAccountFields.isAccountant = true;
    await deleteAccount(modifiedAccountFields.accountId, () => undefined);
    await createAccount(modifiedAccountFields, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(200);
      expect(returnData).toHaveProperty('accountId');
    })
    await deleteAccount(modifiedAccountFields.accountId, () => undefined);
  })
})

describe('testing findAccount', () => {
  test('account exists', async () => {
    await createAccount(accountFields, () => undefined);
    await findAccount(existingId, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(200);
      expect(returnData).toHaveProperty('accountId');
    })
  })
  
  test('account not exist', async () => {
    await findAccount(nonExistingId, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toHaveProperty('name','NotFoundError');
    })
  })
  test('accountId is null', async () => {
    await findAccount(null, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toHaveProperty('name','NotFoundError');
    })
  })
  test('accountId is wrong type', async () => {
    await findAccount({test: 'test'}, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name','CastError');
    })
  })
})

describe('testing findAccountants', () => {
  test('accountants are found', async () => {
    await findAccountants((err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(200);
      expect(returnData).toBeInstanceOf(Array);
    })
  })
})

describe('testing updateProfile', () => {
  test('account updated', async () => {
    const updateFields = { 
      firstname: 'John', 
      avatar: 'www.google.com', 
      lastname: 'Smith', 
      email: 'test234@gmail.com', 
      profession: 'Kid'
    }
    await updateProfile(existingId,updateFields,(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(200);
      expect(returnData).toHaveProperty('profile');
      expect(returnData.profile.firstname).toEqual('John');
    })
  })
  
  test('account not exist', async () => {
    await updateProfile(nonExistingId, {},(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toHaveProperty('name','NotFoundError');
    })
  })

  test('empty string used', async () => {
    await updateProfile(existingId, { firstname: ''},(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(200);
      expect(returnData).toHaveProperty('profile');
      expect(returnData.profile.firstname).not.toEqual('');
    })
  })

  test('accountId is null', async () => {
    await updateProfile(null, {},(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toHaveProperty('name','NotFoundError');
    })
  })

  test('invalid age', async () => {
    await updateProfile(existingId, { age: -1 },(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name', 'ValidationError');
    })
  })

  test('illegal characters for some params', async () => {
    await updateProfile(existingId, { firstname: '$%()#' },(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name', 'ValidationError');
    })
  })
})

describe('testing deleteAccount', () => {
  test('account exists', async () => {
    const modifiedAccountFields = {...accountFields};
    modifiedAccountFields.accountId = nonExistingId
    await createAccount(modifiedAccountFields,() => undefined);
    await deleteAccount(nonExistingId, (err,status,returnData) => {
      console.log(returnData)
      expect(err).toBeNull()
      expect(status).toStrictEqual(200);
      expect(returnData).toEqual('account deleted');
    })
  })
  
  test('account not exist', async () => {
    await deleteAccount(nonExistingId, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toHaveProperty('name','NotFoundError');
    })
  })

  test('accountId is null', async () => {
    await deleteAccount(null, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toHaveProperty('name','NotFoundError');
    })
  })
  test('accountId is wrong type', async () => {
    await deleteAccount({test: 'test'}, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name','CastError');
    })
  })
})

describe('testing createReview', () => {
  test('review created', async () => {
    await createAccount(accountantFields,() => undefined);
    await createReview('1456',reviewFields,(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(200);
      expect(returnData).toHaveProperty('reviews');
      expect(returnData.reviews[returnData.reviews.length - 1]).toHaveProperty('title',reviewFields.title)
    })
  })
  
  test('accountant not found', async () => {
    await createReview(nonExistingId,reviewFields, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toHaveProperty('name','NotFoundError');
    })
  })
  test('missing fields', async () => {
    await createReview('1456',{title: 'Hi'},(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name', 'ValidationError');
    })
  }),
  test('invalid rating', async () => {
    
    const modifiedReviewFields = { ...reviewFields};
    modifiedReviewFields.rating = 11;
    await createReview('1456',modifiedReviewFields,(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name', 'ValidationError');
    })
  })
  test('empty string', async () => {
    const modifiedReviewFields = { ...reviewFields};
    modifiedReviewFields.title = '';
    await createReview('1456',modifiedReviewFields,(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name', 'ValidationError');
    })
  })
})

describe('testing createSubscription', () => {
  test('subscription created', async () => {
    await createAccount(accountFields, () => undefined);
    await createSubscription(existingId, subscriptionFields,(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(200);
      expect(returnData).toHaveProperty('subscription');
      expect(returnData.subscription).toHaveProperty('subscriptionDate', subscriptionFields.subscriptionDate);
      expect(returnData.subscription).toHaveProperty('expiryDate', subscriptionFields.expiryDate);
    })
  })
  
  test('account not found', async () => {
    await createSubscription(nonExistingId, subscriptionFields,(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toHaveProperty('name','NotFoundError');
    })
  })
  test('missing fields', async () => {
    await createSubscription(existingId, {},(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name', 'ValidationError');
    })
  })
  test('invalid date', async () => {
    const modifiedSubFields = { ...subscriptionFields};
    modifiedSubFields.subscriptionDate = '';
    await createSubscription(existingId, modifiedSubFields,(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name', 'ValidationError');
    })
  })
  test('accountId is null', async () => {
    await createSubscription(null, subscriptionFields,(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toHaveProperty('name','NotFoundError');
    })
  })
})

describe('testing updateSubscription', () => {
  test('subscription updated', async () => {
    await createAccount(accountFields, () => undefined);
    await updateSubscription(existingId, subscriptionFields,(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(200);
      expect(returnData).toHaveProperty('subscription');
      expect(returnData.subscription).toHaveProperty('subscriptionDate', subscriptionFields.subscriptionDate);
      expect(returnData.subscription).toHaveProperty('expiryDate', subscriptionFields.expiryDate);
    })
  })
  
  test('account not found', async () => {
    await updateSubscription(nonExistingId, {expiryDate: 'Jun 2023'},(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toHaveProperty('name','NotFoundError');
    })
  })
  test('missing fields', async () => {
    await updateSubscription(existingId, {},(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name', 'ValidationError');
    })
  })
  test('invalid date', async () => {

    await updateSubscription(existingId, {expiryDate: ''},(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name', 'ValidationError');
    })
  })
  test('accountId is null', async () => {
    await updateSubscription(null, subscriptionFields,(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toHaveProperty('name','NotFoundError');
    })
  })
})

describe('testing findAccountants - connection error', () => {
  test('wrong type', async () => {
    mongoose.connections.forEach(async c => {
      if (c.name === 'accountDB') {
        console.log('closing connection');
        await c.close();
      }
    })
    await findAccountants((err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name','MongoNotConnectedError');
    })
  })
})

afterAll((done) => {
  mongoose.disconnect();
  done();
});
