const { default: mongoose } = require("mongoose");
const { Transaction } = require("../../main_modules/transactions/models");
const { createTransaction, deleteTransactions, findTransactions, findTransaction, updateTransaction, deleteTransaction } = require("../../main_modules/transactions/transaction-store.js");

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

let id;


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
  test('wrong id type', async () => {
    await findTransactions({test: 'test'}, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name', 'CastError')
    })
  })
})

describe('testing findTransaction', () => {
  test('transaction found', async () => {
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
    await findTransaction('ai93n', id, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toEqual('account not found');
    })
  })

  test('transaction not found', async () => {
    await findTransaction('1234', 'test', (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toEqual('transaction not found');
    })
  })
  test('wrong id type', async () => {
    await findTransaction({test: 'test'}, 'test',(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name', 'CastError')
    })
  })
})

describe('testing createTransaction', () => {
  test('successfully create transaction', async () => {
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
  test('amount is negative', async () => {
    const modifiedTFields = { ...transactionFields };
    modifiedTFields.amount = -20
    await createTransaction('1234',modifiedTFields, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name', 'ValidationError');
    })
  })
})

describe('testing updateTransaction', () => {
  test('transaction updated', async () => {
    await updateTransaction('1234', id, {title: 'Apple TV'}, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(200);
      expect(returnData).toHaveProperty('title', 'Apple TV');
    })
  })
  test('transaction not found', async () => {
    const transaction = new Transaction({ ...transactionFields});
    await updateTransaction('1234', transaction.id, {title: 'Apple TV'}, (err,status,returnData) => {
      console.log(returnData)
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toEqual('account/transaction not found')
    })
  })

  test('account not found', async () => {
    await updateTransaction('ai93n', id, {title: 'Apple TV'}, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toEqual('account/transaction not found');
    })
  })
  test('missing fields',async () => {
    await updateTransaction('1234', id, {}, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(200);
      expect(returnData).toHaveProperty('_id');
    })
  })
  test('date is in the future',async () => {
    await updateTransaction('1234', id, {date: '2023'}, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name', 'ValidationError');
    })
  })
  test('amount is negative',async () => {
    await updateTransaction('1234', id, {amount: -20}, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name', 'ValidationError');
    })
  })
})

describe('testing deleteTransactions', () => {
  test('transactions deleted', async () => {
    await deleteTransactions('1234', (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(200);
      expect(returnData).toEqual('transactions deleted')
    })
  })
  test('account not found', async () => {
    await deleteTransactions('ai93n', (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toEqual('account not found')
    })
  })
  test('wrong id type', async () => {
    await deleteTransactions({test: 'test'}, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name', 'CastError')
    })
  })
})
describe('testing deleteTransaction', () => {
  test('transaction deleted', async () => {
    await createTransaction('1234',transactionFields, (err,status,returnData) => {
      expect(returnData).toHaveProperty('_id');
      id = returnData.id;
    })
    await deleteTransaction('1234', id, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(200);
      expect(returnData).toEqual('transaction deleted')
    })
  })
  test('account not found', async () => {
    await deleteTransaction('ai93n', id, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toEqual('account not found')
    })
  })
  test('account not found', async () => {
    await deleteTransaction('1234', id, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toEqual('transaction not found')
    })
  })
  test('wrong id type', async () => {
    await deleteTransaction({test: 'test'}, id,(err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name', 'CastError')
    })
  })
})



afterAll((done) => {
  mongoose.disconnect();
  done();
});
