const { default: mongoose } = require("mongoose");
const { createAccount } = require("../../main_modules/accounts/account-store");
const { createTransaction, deleteTransactions, findTransactions, findTransaction } = require("../../main_modules/transactions/transaction-store.js");

const accountFields = {
  accountId: '1234',
  firstname: 'Bob',
  lastname: 'Jones',
  email: 'test123@gmail.com',
  age: 25,
  profession: 'Student',
  isAccountant: false
}

const transactionFields = {
  title: 'Netflix',
  category: 'Subscription',
  amount: 5.99,
  isIncome: false
}


beforeAll(done => {
  done()
})
describe('testing findTransactions', () => {
  test('transactions found', async () => {
    await findTransactions('1234', (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(200);
      expect(returnData).toBeInstanceOf(Array);
    })
  })
  test('account not found', async () => {
    await findTransactions('ai93n', (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toEqual('account not found');
    })
  })
})

describe('testing findTransaction', () => {
  test('transaction found', async () => {
    let id;
    await createTransaction('1234',transactionFields, (err,status,returnData) => {
      expect(returnData).toHaveProperty('_id');
      id = returnData.id;
    })
    await findTransaction('1234', id, (err,status,returnData) => {
      expect(err).toBeNull()
      // expect(status).toStrictEqual(200);
      expect(returnData).toHaveProperty('_id');
    })
  })
  test('account not found', async () => {
    await findTransactions('ai93n', (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toEqual('account not found');
    })
  })
})

describe('testing createTransaction', () => {
  test('successfully create transaction', async () => {
    await createAccount(accountFields, () => undefined); //create accountId = 1234
    await createTransaction('1234',transactionFields, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(200);
      expect(returnData).toHaveProperty('_id')
    })
  })

  test('account not found', async () => {
    await createTransaction('ai93n',transactionFields, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toEqual('account not found');
    })
  })
  test('missing fields', async () => {
    await createTransaction('1234',{}, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name', 'ValidationError');
    })
  })
  test('date is in the future', async () => {
    const modifiedTFields = { ...transactionFields };
    modifiedTFields.date = '2023'
    await createTransaction('1234',modifiedTFields, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name', 'ValidationError');
    })
  })
})

describe('testing findTransactions', () => {

})

afterAll((done) => {
  mongoose.disconnect();
  done();
});
