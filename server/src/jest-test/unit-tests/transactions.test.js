const { default: mongoose } = require("mongoose");
const { Transaction, UserTransaction } = require("../../main_modules/transactions/transaction-models");
const { createTransaction, deleteTransactions, findTransactions, findTransaction, updateTransaction, deleteTransaction } = require("../../main_modules/transactions/transaction-store.js");

const existingId = '1234'
const nonExistingId = 'ai93n';
const accountFields = {
  accountId: existingId,
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
  UserTransaction.findOne({userId: existingId}, async (err,foundUT) => {
    if (!foundUT) {
      console.log('creating userTransaction');
      const userTranscation = new UserTransaction({userId: existingId});
      await userTranscation.save();
    }
  })
  done()
})
describe('testing findTransactions', () => {
  test('transactions found', async () => {
    await findTransactions(existingId, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(200);
      expect(returnData).toBeInstanceOf(Array);
    })
  })
  test('account not found', async () => {
    await findTransactions(nonExistingId, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toHaveProperty('name','NotFoundError');
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
    await createTransaction(existingId,transactionFields, (err,status,returnData) => {
      expect(returnData).toHaveProperty('_id');
      id = returnData.id;
    })
    await findTransaction(existingId, id, (err,status,returnData) => {
      expect(err).toBeNull()
      // expect(status).toStrictEqual(200);
      expect(returnData).toHaveProperty('_id');
    })
  })
  test('account not found', async () => {
    await findTransaction(nonExistingId, id, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toHaveProperty('name','NotFoundError');
    })
  })

  test('transaction not found', async () => {
    await findTransaction(existingId, 'test', (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toHaveProperty('name','NotFoundError');
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
    await createTransaction(existingId,transactionFields, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(200);
      expect(returnData).toHaveProperty('_id')
    })
  })

  test('account not found', async () => {
    await createTransaction(nonExistingId,transactionFields, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toHaveProperty('name','NotFoundError');
    })
  })
  test('missing fields', async () => {
    await createTransaction(existingId,{}, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name', 'ValidationError');
    })
  })
  test('date is in the future', async () => {
    const modifiedTFields = { ...transactionFields };
    modifiedTFields.date = '2023'
    await createTransaction(existingId,modifiedTFields, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name', 'ValidationError');
    })
  })
  test('amount is negative', async () => {
    const modifiedTFields = { ...transactionFields };
    modifiedTFields.amount = -20
    await createTransaction(existingId,modifiedTFields, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name', 'ValidationError');
    })
  })
})

describe('testing updateTransaction', () => {
  test('transaction updated', async () => {
    const updateFields =  {
      title: 'Apple TV',
      category: 'Food',
      isIncome: true,
      receipt: 'www.google.com'
    }
    await updateTransaction(existingId, id,updateFields, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(200);
      expect(returnData).toHaveProperty('title', 'Apple TV');
    })
  })
  test('transaction not found', async () => {
    const transaction = new Transaction({ ...transactionFields});
    await updateTransaction(existingId, transaction.id, {title: 'Apple TV'}, (err,status,returnData) => {
      console.log(returnData)
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toHaveProperty('name','NotFoundError');
    })
  })

  test('account not found', async () => {
    await updateTransaction(nonExistingId, id, {title: 'Apple TV'}, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toHaveProperty('name','NotFoundError');
    })
  })
  test('missing fields',async () => {
    await updateTransaction(existingId, id, {}, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(200);
      expect(returnData).toHaveProperty('_id');
    })
  })
  test('date is in the future',async () => {
    await updateTransaction(existingId, id, {date: '2023'}, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name', 'ValidationError');
    })
  })
  test('amount is negative',async () => {
    await updateTransaction(existingId, id, {amount: -20}, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toHaveProperty('name', 'ValidationError');
    })
  })
})

describe('testing deleteTransactions', () => {
  test('transactions deleted', async () => {
    await deleteTransactions(existingId, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(200);
      expect(returnData).toEqual('transactions deleted')
    })
  })
  test('account not found', async () => {
    await deleteTransactions(nonExistingId, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toHaveProperty('name','NotFoundError');
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
    await createTransaction(existingId,transactionFields, (err,status,returnData) => {
      expect(returnData).toHaveProperty('_id');
      id = returnData.id;
    })
    await deleteTransaction(existingId, id, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(200);
      expect(returnData).toEqual('transaction deleted')
    })
  })
  test('account not found', async () => {
    await deleteTransaction(nonExistingId, id, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toHaveProperty('name','NotFoundError');
    })
  })
  test('account not found', async () => {
    await deleteTransaction(existingId, id, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(404);
      expect(returnData).toHaveProperty('name','NotFoundError');
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
