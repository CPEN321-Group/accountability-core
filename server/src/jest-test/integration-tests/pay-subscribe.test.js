const request = require('supertest');
const mongoose = require('mongoose');
const { server } = require('../../index');

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

const subscriptionFields = {
  subscriptionDate: new Date('Jun 2022'),
  expiryDate: new Date('July 2022')
}

beforeAll(done => {
  done();
})

describe('subscribe', () => {
  test('pay + subscribe for an existing user', async () => {
    await request(server).post('/accounts').query({...accountFields});
    await request(server).put(`/subscription/${existingId}`).query({ expiryDate: 'May 2022'});
    const res1 = await request(server).post('/stripe/checkout/' + existingId);
    expect(res1.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res1.statusCode).toBe(200);
    expect(res1.body).toHaveProperty('paymentIntent');

    const res2 = await request(server).post('/subscription/' + existingId).query({
      ...subscriptionFields
    });
    expect(res2.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res2.statusCode).toBe(200);
    expect(res2.body).toHaveProperty('accountId');
  })
  test('pay + subscribe for a non-existing user', async () => {
    const res = await request(server).post('/stripe/checkout/' + nonExistingId);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name','NotFoundError');
  })
  test('pay + subscribe with invalid parameters', async () => {
    const res1 = await request(server).post('/stripe/checkout/' + existingId);
    expect(res1.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res1.statusCode).toBe(200);
    expect(res1.body).toHaveProperty('paymentIntent');

    const res2 = await request(server).post('/subscription/' + existingId).query({
      ...subscriptionFields, 
      expiryDate: ''
    });
    expect(res2.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res2.statusCode).toBe(400);
    expect(res2.body).toHaveProperty('name','ValidationError');
  })
  test('subscribing with invalid authentication', async () => {
    const res = await request(server).post('/stripe/checkout/' + existingId).query({
      token: 'invalid-token'
    });
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(403);
    expect(res.body).toHaveProperty('name', 'ForbiddenError');
  })
  test('pay + subscribe for a already subscribed user', async () => {
    const res0 = await request(server).put(`/subscription/${existingId}`).query({ expiryDate: 'May 2026'});
    expect(res0.statusCode).toBe(200);
    const res = await request(server).post('/stripe/checkout/' + existingId);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(400);
    expect(res.body).toHaveProperty('name','ValidationError');
    expect(res.body).toHaveProperty('errorMessage','account already subscribed');

    await request(server).put(`/subscription/${existingId}`).query({ expiryDate: 'May 2022'});
  })
}) 

describe('update the current subscription', () => {
  test('update subscription for an existing user', async () => {
    const res = await request(server).put('/subscription/' + existingId).query({
      expiryDate: "2022-08-01T07:00:00.000Z"
    })
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body.subscription).toHaveProperty('expiryDate', '2022-08-01T07:00:00.000Z');
  })
  test('update subscription for a non-existing user', async () => {
    const res = await request(server).put('/subscription/' + nonExistingId).query({
      expiryDate: "2022-08-01T07:00:00.000Z"
    })
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name','NotFoundError');
  })
  test('update subscription with invalid parameters', async () => {
    const res = await request(server).put('/subscription/' + existingId).query({
      expiryDate: ""
    })
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(400);
    expect(res.body).toHaveProperty('name', 'ValidationError');
  })
  test('update subscription with invalid parameters', async () => {
    const res = await request(server).put('/subscription/' + existingId).query({
      expiryDate: "",
      token: 'invalid-token'
    })
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(403);
    expect(res.body).toHaveProperty('name', 'ForbiddenError');
  })
}) 

afterAll((done) => {
  mongoose.disconnect();
  server.close();
  done();
});
