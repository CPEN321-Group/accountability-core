const request = require('supertest');
const app = require('../../index');
const mongoose = require('mongoose');
const { server } = require('../../index');

const existingId = '1234';
const nonExistingId = 'ai93n';
const accountFields = {
  accountId: '1234',
  firstname: 'Bob',
  lastname: 'Jones',
  email: 'test123@gmail.com',
  age: 25,
  profession: 'Student',
  isAccountant: false
}
const newAccountFields = {
  ...accountFields,
  accountId: nonExistingId
}

const accountantId = '1456'
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
  const res = request(server).post('/accounts').query({...accountFields});
  done();
})

describe('find an account', () => {
  test('account exists', async () => {
    const res = await request(server).get('/accounts/' + existingId);

    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toHaveProperty('accountId');
  }) 
  test('account does not exist', async () => {
    await request(server).delete('/accounts/' + nonExistingId);
    const res = await request(server).get('/accounts/' + nonExistingId);

    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name','NotFoundError');
  }) 
})

describe('find accountants', () => {
  test('find all accountants', async () => {
    const res = await request(server).get('/accounts/accountants');

    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toBeInstanceOf(Array);
  }) 
})

describe('create an account', () => {
  test('create an account with an existing accountId', async () => {
    const res = await request(server).post('/accounts').query({...accountFields});

    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(400);
    expect(res.body).toHaveProperty('name', 'ValidationError');
  }) 
  test('create an account with invalid parameters', async () => {
    const res = await request(server).post('/accounts').query({test: 'test'});

    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(400);
    expect(res.body).toHaveProperty('name', 'ValidationError');
  }) 
  test('create an account with new accountId', async () => {
    await request(server).delete('/accounts/' + nonExistingId);
    const res = await request(server).post('/accounts').query(newAccountFields);

    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toHaveProperty('accountId', newAccountFields.accountId);

    await request(server).delete('/accounts/' + newAccountFields.accountId);
  }) 
})

describe('update account profile', () => {
  test('update profile of an existing account', async () => {
    const res = await request(server).put('/accounts/' + existingId).query({firstname: 'John'});

    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toHaveProperty('profile');
    expect(res.body.profile).toHaveProperty('firstname', 'John')
  })
  test('update profile of a non-existing account', async () => {
    const res = await request(server).put('/accounts/' + nonExistingId).query({firstname: 'John'});

    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name', 'NotFoundError');
  })
  test('update profile with no parameters to update', async () => {
    const res = await request(server).put('/accounts/' + existingId);

    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toHaveProperty('profile');
  })
  test('update profile of an account that doesn\'t belong to the user', async () => {
    const res = await request(server).put('/accounts/' + existingId).query({
      firstname: 'John',
      token: 'invalid-token'
    });
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(403);
    expect(res.body).toHaveProperty('name', 'ForbiddenError');
  })
})
describe('delete an account', () => {
  test('delete an existing account', async () => {
    const res = await request(server).delete('/accounts/' + existingId);

    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toEqual('account deleted');
    await request(server).post('/accounts').query({...accountFields});
  })
  test('delete a non-existing account', async () => {
    const res = await request(server).delete('/accounts/' + nonExistingId);

    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name', 'NotFoundError');
  })
  test('delete an account with permissions', async () => {
    const res = await request(server).delete('/accounts/' + nonExistingId).query({
      token: 'invalid-token'
    });

    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(403);
    expect(res.body).toHaveProperty('name', 'ForbiddenError');
  })
})

describe('write a review', () => {
  test('write a review for an existing accountant', async () => {
    await request(server).post('/accounts').query({...accountantFields});
    const res = await request(server).post('/reviews/' + accountantId).query({...reviewFields});
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toHaveProperty('reviews');
    expect(res.body.reviews[res.body.reviews.length - 1]).toHaveProperty('title', reviewFields.title)
  })
  test('write a review for a non-existing accountant', async () => {
    const res = await request(server).post('/reviews/' + existingId).query({...reviewFields});
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name','NotFoundError');
  })
  test('write a review with invalid parameters', async () => {
    await request(server).post('/accounts').query({...accountantFields});
    const res = await request(server).post('/reviews/' + accountantId).query({title: '#()&%'});
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(400);
    expect(res.body).toHaveProperty('name', 'ValidationError');
  })
  test('write a reivew with invalid authentication', async () => {
    await request(server).post('/accounts').query({...accountantFields});
    const res = await request(server).post('/reviews/' + accountantId).query({
      ...reviewFields,
      token: 'invalid-token'
    });
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(403);
    expect(res.body).toHaveProperty('name', 'ForbiddenError');
  })
})

describe('subscribe', () => {
  test('pay + subscribe for an existing user', async () => {
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
