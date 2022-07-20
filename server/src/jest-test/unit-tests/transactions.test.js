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

afterAll((done) => {
  mongoose.disconnect();
  done();
});
