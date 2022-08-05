const request = require('supertest');
const mongoose = require('mongoose');
const { server } = require('../../index');
const { Goal } = require('../../main_modules/goals/goal-models');

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
const goalFields = {
  title: 'Buy a House',
  target: 10000,
  current: 1000,
  deadline: '2026'
}

let goalId;

beforeAll(done => {
  done();
})

describe('find all goals of a user', () => {
  test('find goals of an existing user', async () => {
    await request(server).post('/accounts').query({...accountFields});
    const res = await request(server).get('/goals/' + existingId);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toBeInstanceOf(Array);
  })
  test('find goals of a non-existing user', async () => {
    const res = await request(server).get('/goals/' + nonExistingId);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name', 'NotFoundError');
  })
  test('find goals with invalid authentication', async () => {
    const res = await request(server).get('/goals/' + existingId).query({
      token: 'invalid-token'
    });
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(403);
    expect(res.body).toHaveProperty('name', 'ForbiddenError');
  })
  test('find goals for user with no goals', async () => {
    await request(server).post('/accounts').query({...accountFields});
    const res = await request(server).get('/goals/' + existingId);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toBeInstanceOf(Array);
    expect(res.body.length).toBe(0);
  })
})

describe('create a goal', () => {
  test('create goal for an existing user', async () => {
    const res = await request(server).post('/goals/' + existingId).query({...goalFields});
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toHaveProperty('title', goalFields.title);
  })
  test('create goal for a non-existing user', async () => {
    const res = await request(server).post('/goals/' + nonExistingId).query({...goalFields});
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name','NotFoundError');
  })
  test('create goal with invalid parameters', async () => {
    const modifiedFields = {
      ...goalFields,
      title: '    '
    }
    const res = await request(server).post('/goals/' + existingId).query({...modifiedFields});
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(400);
    expect(res.body).toHaveProperty('name','ValidationError');
  })
})

describe('delete all goals by the user', () => {
  test('delete goals from existing user', async () => {
    const res = await request(server).delete('/goals/' + existingId);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toEqual('goals deleted')
  })
  test('delete goals from non-existing user', async () => {
    const res = await request(server).delete('/goals/' + nonExistingId);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name', 'NotFoundError')
  })
  test('delete goals with invalid authentication', async () => {
    const res = await request(server).delete('/goals/' + existingId).query({
      token: 'invalid-token'
    })
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(403);
    expect(res.body).toHaveProperty('name','ForbiddenError');
  })
})

describe('find a goal', () => {
  test('find an existing goal', async () => {
    const res1 = await request(server).post('/goals/' + existingId).query({...goalFields});
    expect(res1.body).toHaveProperty('_id');
    goalId = res1.body._id;

    const res2 = await request(server).get(`/goals/${existingId}/${goalId}`);
    expect(res2.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res2.statusCode).toBe(200);
    expect(res2.body).toHaveProperty('_id', goalId);
  })
  test('find a non-existing goal', async () => {
    const res = await request(server).get(`/goals/${existingId}/test`);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name','NotFoundError');
  })
  test('find a goal with invalid authentication', async () => {
    const res = await request(server).get(`/goals/${existingId}/${goalId}`).query({
      token: 'invalid-token'
    });
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(403);
    expect(res.body).toHaveProperty('name','ForbiddenError');
  })
})
describe('update a goal', () => {
  test('update an existing goal', async () => {
    const res = await request(server).put(`/goals/${existingId}/${goalId}`).query({
      title: 'Rent an Apartment'
    });
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toHaveProperty('title', 'Rent an Apartment');
  })
  test('update a non-existing goal', async () => {
    const goal = new Goal({...goalFields});
    const res = await request(server).put(`/goals/${existingId}/${goal.id}`).query({
      title: 'Rent an Apartment'
    });
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name', 'NotFoundError');
  })
  test('update a goal with missing parameters', async () => {
    const res = await request(server).put(`/goals/${existingId}/${goalId}`);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toHaveProperty('title', 'Rent an Apartment');
  })
})

describe('delete a goal', () => {
  test('delete an existing goal', async () => {
    const res = await request(server).delete(`/goals/${existingId}/${goalId}`);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toEqual('goal deleted')
  })
  test('delete a non-existing goal', async () => {
    const goal = new Goal({...goalFields});
    const res = await request(server).delete(`/goals/${existingId}/${goal.id}`);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name', 'NotFoundError');
  })
  test('delete goal with invalid authentication', async () => {
    const res = await request(server).delete(`/goals/${existingId}/${goalId}`).query({
      token: 'invalid-token'
    });
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(403);
    expect(res.body).toHaveProperty('name','ForbiddenError')
  })
})

afterAll((done) => {
  mongoose.disconnect();
  server.close();
  done();
});
