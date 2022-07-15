const { default: mongoose } = require("mongoose");
const { createAccount, findAccount, findAccountants, updateProfile, deleteAccount, createReview, createSubscription, updateSubscription } = require("./account-store.mock")


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

describe('testing createAccount', () => {
  const accountFields = {
    accountId: '1234',
    firstname: 'Bob',
    lastname: 'Jones',
    email: 'test123@gmail.com',
    age: 25,
    profession: 'Student',
    isAccountant: false
  }

  test('pre-existing account', async () => {
    await createAccount(accountFields, (status,returnData) => {
      expect(status).toStrictEqual(400);
      expect(returnData).toEqual('account already exists')
    })
  }) 
  test('valid creation', async () => {
    await createAccount({
      ...accountFields,
      accountId: '1456'
    }, (status,returnData) => {
      expect(status).toStrictEqual(200);
      expect(returnData).toEqual(account)
    })
  })

  test('missing params' ,async () => {
    await createAccount({
      accountId: '1456'
    }, (status,returnData) => {
      expect(status).toStrictEqual(400);
      expect(returnData).toEqual('missing params')
    })
  })
})

describe('testing findAccount', () => {
  test('account exists', async () => {
    await findAccount('1234', (status,returnData) => {
      expect(status).toStrictEqual(200);
      expect(returnData).toEqual(account);
    })
  })
  
  test('account not exist', async () => {
    await findAccount('1456', (status,returnData) => {
      expect(status).toStrictEqual(404);
      expect(returnData).toEqual('account not found');
    })
  })
})

describe('testing findAccountants', () => {
  test('no accountants are found', async () => {
    await findAccountants((status,returnData) => {
      expect(status).toStrictEqual(200);
      expect(returnData).toEqual([]);
    })
  })
})

describe('testing updateProfile', () => {
  test('account exists', async () => {
    await updateProfile('1234', {} ,(status,returnData) => {
      expect(status).toStrictEqual(200);
      expect(returnData).toEqual(account);
    })
  })
  
  test('account not exist', async () => {
    await updateProfile('1456', {},(status,returnData) => {
      expect(status).toStrictEqual(404);
      expect(returnData).toEqual('account not found');
    })
  })
})

describe('testing deleteAccount', () => {
  test('account exists', async () => {
    await deleteAccount('1234', (status,returnData) => {
      expect(status).toStrictEqual(200);
      expect(returnData).toEqual('account deleted');
    })
  })
  
  test('account not exist', async () => {
    await deleteAccount('1456', (status,returnData) => {
      expect(status).toStrictEqual(404);
      expect(returnData).toEqual('account not found');
    })
  })
})

describe('testing createReview', () => {
  test('account exists', async () => {
    await createReview('1234',{},(status,returnData) => {
      expect(status).toStrictEqual(200);
      expect(returnData).toEqual(account);
    })
  })
  
  test('account not exist', async () => {
    await createReview('1456',{}, (status,returnData) => {
      expect(status).toStrictEqual(404);
      expect(returnData).toEqual('accountant not found');
    })
  })
})

describe('testing createSubscription', () => {
  test('account exists', async () => {
    await createSubscription('1234', {},(status,returnData) => {
      expect(status).toStrictEqual(200);
      expect(returnData).toEqual(account);
    })
  })
  
  test('account not exist', async () => {
    await createSubscription('1456', {},(status,returnData) => {
      expect(status).toStrictEqual(404);
      expect(returnData).toEqual('account not found');
    })
  })
})

describe('testing updateSubscription', () => {
  test('account exists', async () => {
    await updateSubscription('1234', {},(status,returnData) => {
      expect(status).toStrictEqual(200);
      expect(returnData).toEqual(account);
    })
  })
  test('account not exist', async () => {
    await updateSubscription('1456', {},(status,returnData) => {
      expect(status).toStrictEqual(404);
      expect(returnData).toEqual('account not found');
    })
  })
})

// afterAll(()=>{ mongoose.disconnect();});
afterAll(async done => {
  // Closing the DB connection allows Jest to exit successfully.
  dbConnection.close();
  done();
});
