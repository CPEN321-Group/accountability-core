const { default: mongoose } = require("mongoose");
const { createAccount, findAccount, findAccountants, updateProfile, deleteAccount, createReview, createSubscription, updateSubscription } = require("../../main_modules/accounts/account-store");

const accountFields = {
  accountId: '1234',
  firstname: 'Bob',
  lastname: 'Jones',
  email: 'test123@gmail.com',
  age: 25,
  profession: 'Student',
  isAccountant: false
}

const accountantFields = {
  accountId: '1456',
  firstname: 'Mary',
  lastname: 'Smith',
  email: 'test123@gmail.com',
  age: 27,
  profession: 'Accountant',
  isAccountant: true
}
const reviewFields = {
  authorId: '1234',
  rating: 10,
  date: 'Jun 2022',
  title: 'Good work',
  content: 'Thank you'
}
const subscriptionFields = {
  subscriptionDate: new Date('Jun 2022'),
  expiryDate: new Date('July 2022')
}

beforeAll(done => {
  done()
})

describe('testing createAccount', () => {

  test('accountId in use', async () => {
    await createAccount(accountFields, (err,status,returnData) => {}); //create accountId = 1234
    await createAccount(accountFields, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toEqual('account already exists')
    })
  }) 

  test('some fields are missing', async () => {
    await createAccount(
      {accountId: '1456', firstname: 'Bob'}, 
      (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toEqual('missing params')
    })
  })
  test('empty string was used', async () => {
    const modifiedAccountFields = { ...accountFields };
    modifiedAccountFields.accountId = 'ai93n';
    modifiedAccountFields.firstname = '';
    await createAccount(modifiedAccountFields, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toEqual('missing params');
    })
  })

  test('accountId is null', async () => {
    await createAccount(null, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toEqual('missing params');
    })
  })
  test('invalid age', async () => {
    const modifiedAccountFields = { ...accountFields };
    modifiedAccountFields.accountId = 'ai93n';
    modifiedAccountFields.age = -1;
    await createAccount(modifiedAccountFields, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toEqual('invalid age');
    })
  })

  test('illegal characters for some params', async () => {
    const modifiedAccountFields = { ...accountFields };
    modifiedAccountFields.accountId = 'ai93n';
    modifiedAccountFields.firstname = '$%()#';
    await createAccount(modifiedAccountFields, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toEqual('illegal characters');
    })
  })


  test('valid creation', async () => {
    const modifiedAccountFields = {...accountFields};
    modifiedAccountFields.accountId = 'ai93n'
    await deleteAccount(modifiedAccountFields.accountId, (err,status,returnData) => {});
    await createAccount(modifiedAccountFields, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(200);
      expect(returnData).toHaveProperty('accountId');
    })
    await deleteAccount(modifiedAccountFields.accountId, (err,status,returnData) => {});
  })
})

describe('testing findAccount', () => {
  test('account exists', async () => {
    await createAccount(accountFields, (err,status,returnData) => {});
    await findAccount('1234', (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(200);
      expect(returnData).toHaveProperty('accountId');
    })
  })
  
  test('account not exist', async () => {
    await findAccount('ai93n', (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toEqual('account not found');
    })
  })
  test('accountId is null', async () => {
    await findAccount(null, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toEqual('missing params');
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
    await updateProfile('1234', { firstname: 'John'} ,(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(200);
      expect(returnData).toHaveProperty('profile');
      expect(returnData.profile.firstname).toEqual('John');
    })
  })
  
  test('account not exist', async () => {
    await updateProfile('ai93n', {},(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toEqual('account not found');
    })
  })

  test('empty string used', async () => {
    await updateProfile('1234', { firstname: ''},(err,status,returnData) => {
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
      expect(returnData).toEqual('account not found')
    })
  })

  test('invalid age', async () => {
    await updateProfile('1234', { age: -1 },(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toEqual('invalid age');
    })
  })

  test('illegal characters for some params', async () => {
    await updateProfile('1234', { firstname: '$%()#' },(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toEqual('illegal characters');
    })
  })
})

describe('testing deleteAccount', () => {
  test('account exists', async () => {
    await createAccount(accountFields,(err,status,returnData) => {});
    await deleteAccount('1234', (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(200);
      expect(returnData).toEqual('account deleted');
    })
  })
  
  test('account not exist', async () => {
    await deleteAccount('ai93n', (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toEqual('account not found');
    })
  })

  test('accountId is null', async () => {
    await deleteAccount(null, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toEqual('account not found');
    })
  })
})

describe('testing createReview', () => {
  test('review created', async () => {
    await createAccount(accountantFields,(err,status,returnData) => {});
    await createReview('1456',reviewFields,(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(200);
      expect(returnData).toHaveProperty('reviews');
      expect(returnData.reviews[returnData.reviews.length - 1]).toHaveProperty('title',reviewFields.title)
    })
  })
  
  test('accountant not found', async () => {
    await createReview('ai93n',reviewFields, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toEqual('accountant not found');
    })
  })
  test('missing fields', async () => {
    await createReview('1456',{title: 'Hi'},(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toEqual('missing params');
    })
  }),
  test('invalid rating', async () => {
    const modifiedReviewFields = { ...reviewFields};
    modifiedReviewFields.rating = 11;
    await createReview('1456',modifiedReviewFields,(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toEqual('invalid rating');
    })
  })
  test('empty string', async () => {
    const modifiedReviewFields = { ...reviewFields};
    modifiedReviewFields.title = '';
    await createReview('1456',modifiedReviewFields,(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toEqual('missing params');
    })
  })
})

describe('testing createSubscription', () => {
  test('subscription created', async () => {
    await createAccount(accountFields, (err,status,returnData) => {});
    await createSubscription('1234', subscriptionFields,(err,status,returnData) => {
      console.log(returnData)
      expect(err).toBeNull()
      expect(status).toStrictEqual(200);
      expect(returnData).toHaveProperty('subscription');
      expect(returnData.subscription).toHaveProperty('subscriptionDate', subscriptionFields.subscriptionDate);
      expect(returnData.subscription).toHaveProperty('expiryDate', subscriptionFields.expiryDate);
    })
  })
  
  test('account not found', async () => {
    await createSubscription('ai93n', subscriptionFields,(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toEqual('account not found');
    })
  })
  test('missing fields', async () => {
    await createSubscription('1234', {},(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toEqual('missing params');
    })
  })
  test('empty string used', async () => {
    const modifiedSubFields = { ...subscriptionFields};
    modifiedSubFields.subscriptionDate = '';
    await createSubscription('1234', modifiedSubFields,(err,status,returnData) => {
      console.log(returnData)
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toEqual('missing params')
    })
  })
  test('accountId is null', async () => {
    await createSubscription(null, subscriptionFields,(err,status,returnData) => {
      console.log(returnData)
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toEqual('account not found')
    })
  })
})

describe('testing updateSubscription', () => {
  test('subscription updated', async () => {
    await createAccount(accountFields, (err,status,returnData) => {});
    await updateSubscription('1234', subscriptionFields,(err,status,returnData) => {
      console.log(returnData)
      expect(err).toBeNull()
      expect(status).toStrictEqual(200);
      expect(returnData).toHaveProperty('subscription');
      expect(returnData.subscription).toHaveProperty('subscriptionDate', subscriptionFields.subscriptionDate);
      expect(returnData.subscription).toHaveProperty('expiryDate', subscriptionFields.expiryDate);
    })
  })
  
  test('account not found', async () => {
    await updateSubscription('ai93n', {expiryDate: 'Jun 2023'},(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toEqual('account not found');
    })
  })
  test('missing fields', async () => {
    await updateSubscription('1234', {},(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toEqual('missing params');
    })
  })
  test('empty string used', async () => {

    await updateSubscription('1234', {expiryDate: ''},(err,status,returnData) => {
      console.log(returnData)
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toEqual('missing params')
    })
  })
  test('accountId is null', async () => {
    await updateSubscription(null, subscriptionFields,(err,status,returnData) => {
      console.log(returnData)
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toEqual('account not found')
    })
  })
})

// afterAll(()=>{ mongoose.disconnect();});
// afterAll( async () =>{
//         await mongoose.connection.close()
//     })
afterAll((done) => {
  mongoose.connection.close();
  done();
});
