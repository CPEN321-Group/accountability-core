const request = require('supertest');
const mongoose = require('mongoose');
const { server } = require('../../index');
const { Transaction } = require('../../main_modules/transactions/transaction-models');

const existingId = '1234';
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
  title: 'Buy Lunch',
  category: 'Groceries',
  date: 'Jul 24, 2022',
  amount: 1000,
  isIncome: false,
}

let transactionId;

beforeAll(done => {
  done();
})

describe('get recent transactions for user', () => {
  test('get transactions of an existing user', async () => {
    await request(server).post('/accounts').query({...accountFields});
    const res = await request(server).get('/transactions/' + existingId);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toBeInstanceOf(Array);
  })
  test('get transactions of a non-existing user', async () => {
    const res = await request(server).get('/transactions/' + nonExistingId);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name', 'NotFoundError');
  })
  test('get transactions for a user without any transactions', async () => {
    await request(server).post('/accounts').query({...accountFields, accountId: nonExistingId});
    const res = await request(server).get('/transactions/' + nonExistingId);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toBeInstanceOf(Array);
    expect(res.body.length).toBe(0);
    await request(server).delete('/accounts/' + nonExistingId);
  })
})

describe('create new transaction', () => {
  test('create new valid transaction', async () => {
    const res = await request(server).post('/transactions/' + existingId).query({...transactionFields});
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toHaveProperty('title', transactionFields.title);
  })
  test('create transaction with invalid fields', async () => {
    const res = await request(server).post('/transactions/' + existingId).query({...transactionFields, title: ''});
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(400);
    expect(res.body).toHaveProperty('name','ValidationError');
  })
  test('create transaction for a non-existing user', async () => {
    const res = await request(server).post('/transactions/' + nonExistingId).query({...transactionFields});
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name','NotFoundError');
  })
})

describe('delete transactions', () => {
  test('delete transactions for an existing user', async () => {
    const res = await request(server).delete('/transactions/' + existingId);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toEqual('transactions deleted');
  })
  test('delete transactions for a non-existing user', async () => {
    const res = await request(server).delete('/transactions/' + nonExistingId);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name', 'NotFoundError');
  })
})
describe('get a particular transaction', () => {
  test('get a transaction for an existing user', async () => {
    const res1 = await request(server).post('/transactions/' + existingId).query({...transactionFields});
    expect(res1.body).toHaveProperty('_id');
    transactionId = res1.body._id;
    const res = await request(server).get(`/transactions/${existingId}/${transactionId}`);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toHaveProperty('_id', transactionId);
  })
  test('get a transaction for a non-existing user', async () => {
    const res = await request(server).get(`/transactions/${nonExistingId}/${transactionId}`);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name','NotFoundError');
  })
  test('get a non-transaction for an existing user', async () => {
    const transaction = new Transaction({...transactionFields});
    const res = await request(server).get(`/transactions/${existingId}/${transaction.id}`);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name','NotFoundError');
  })
  test('get a transaction with invalid permissions', async () => {
    const res = await request(server).get(`/transactions/${existingId}/${transactionId}`).query({token: 'invalid-token'});
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(403);
    expect(res.body).toHaveProperty('name','ForbiddenError');
  })
})

describe('update a transaction', () => {
  test('update transaction for existing user', async () => {
    const res = await request(server).put(`/transactions/${existingId}/${transactionId}`).query({title: 'Buy Dinner'});
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toHaveProperty('title','Buy Dinner');
  })
  test('update transaction for non-existing user', async () => {
    const res = await request(server).put(`/transactions/${nonExistingId}/${transactionId}`).query({title: 'Buy Dinner'});
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name', 'NotFoundError');
  })
  test('update non-existing transaction', async () => {
    const transaction = new Transaction({...transactionFields});
    const res = await request(server).put(`/transactions/${existingId}/${transaction.id}`).query({title: 'Buy Dinner'});
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name', 'NotFoundError');
  })
  test('update transaction without proper permissions', async () => {
    const res = await request(server).put(`/transactions/${existingId}/${transactionId}`).query({
      title: 'Buy Dinner', 
      token: 'invalid-token'
    });
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(403);
    expect(res.body).toHaveProperty('name', 'ForbiddenError');
    expect(res.body).toHaveProperty('errorMessage', 'invalid token provided');
  })
})

describe('delete a particular transaction', () => {
  test('delete a transaction for existing user', async () => {
    const res = await request(server).delete(`/transactions/${existingId}/${transactionId}`);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toEqual('transaction deleted')
  })
  test('delete transaction for non-existing user', async () => {
    const res = await request(server).delete(`/transactions/${nonExistingId}/${transactionId}`);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name', 'NotFoundError');
  })
  test('delete non-existing transaction', async () => {
    const transaction = new Transaction({...transactionFields});
    const res = await request(server).delete(`/transactions/${existingId}/${transaction.id}`);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name', 'NotFoundError');
  })
  test('delete a transaction without permissions', async () => {
    const res = await request(server).delete(`/transactions/${existingId}/${transactionId}`).query({
      token: 'invalid-token'
    });
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(403);
    expect(res.body).toHaveProperty('name', 'ForbiddenError');
  })
})

describe('find transactions by search query', () => {
  test('find transactions with existing title', async () => {
    const res0 = await request(server).post('/transactions/' + existingId).query({...transactionFields});

    const res = await request(server).get('/search/transactions/' + existingId).query({title: transactionFields.title});
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toBeInstanceOf(Array);
    expect(res.body.length).toBeGreaterThan(0);
  })
  test('find transactions with non-existing title', async () => {

    const res = await request(server).get('/search/transactions/' + existingId).query({title: 'adagjasdgaj'});
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toBeInstanceOf(Array);
    expect(res.body.length).toBe(0);
  })
})

afterAll((done) => {
  mongoose.disconnect();
  server.close();
  done();
});
