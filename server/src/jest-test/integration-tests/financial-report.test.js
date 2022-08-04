const request = require('supertest');
const mongoose = require('mongoose');
const { server } = require('../../index');
const { Report } = require('../../main_modules/reports/report-models');
const { Account } = require('../../main_modules/accounts/account-models');

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
let reportId;

const monthYear = 'May 2022'
const recommendations = 'Lorem ipsum'

beforeAll(done => {
  done();
})

describe('get all reports for user', () => {
  test('get reports of existing user', async () => {
    await request(server).post(`/accounts`).query({...accountFields});
    const res =  await request(server).get(`/reports/users/${existingId}`);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toBeInstanceOf(Array);
  })
  test('get reports of non-existing user', async () => {
    const res =  await request(server).get(`/reports/users/${nonExistingId}`);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name','NotFoundError')
  })
  test('get reports for user with no reports', async () => {
    await request(server).post(`/accounts`).query({...accountFields});
    await request(server).delete(`/reports/users/${existingId}`);
    const res =  await request(server).get(`/reports/users/${existingId}`);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toBeInstanceOf(Array);
    expect(res.body.length).toEqual(0)
  })
})
describe('create a new report', () => {
  test('create a new report with no invalid fields', async () => {
    await request(server).delete(`/reports/users/${existingId}`);
    const res =  await request(server).post(`/reports/users/${existingId}`).query({monthYear});
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toHaveProperty('_id');
    reportId = res.body._id;
  })
  test('create report with missing date', async () => {
    const res =  await request(server).post(`/reports/users/${existingId}`);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(400);
    expect(res.body).toHaveProperty('name','ValidationError');
  })
  test('create a new report that already exists', async () => {
    const res =  await request(server).post(`/reports/users/${existingId}`).query({monthYear});
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(400);
    expect(res.body).toHaveProperty('name', 'ValidationError');
  })
  test('create a new report for non-existing user', async () => {
    const res =  await request(server).post(`/reports/users/${nonExistingId}`).query({monthYear});
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name', 'NotFoundError');
  })
})
describe('update the user\'s current accountant', () => {
  test('update to an existing accountant', async () => {
    await request(server).post('/accounts').query({...accountantFields});
    const res =  await request(server).put(`/reports/users/${existingId}`).query({
      accountantId
    });
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toHaveProperty('accountantId', accountantId);
  })
  test('update to a non-existing accountant', async () => {
    const res =  await request(server).put(`/reports/users/${existingId}`).query({
      accountantId: 'test'
    });
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name','NotFoundError');
  })
})

describe('delete all reports for user', () => {
  test('delete reports for existing user', async () => {
    const res =  await request(server).delete(`/reports/users/${existingId}`);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toEqual('reports deleted');
  })
  test('delete reports for non-existing user', async () => {
    const res =  await request(server).delete(`/reports/users/${nonExistingId}`);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name','NotFoundError');
  })
})
describe('get a particular report', () => {
  test('get report for existing user', async () => {
    const res0 =  await request(server).post(`/reports/users/${existingId}`).query({monthYear});
    expect(res0.body).toHaveProperty('_id');
    reportId = res0.body._id;
    const res =  await request(server).get(`/reports/users/${existingId}/${reportId}`);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toHaveProperty('_id', reportId);
  })
  test('get report for non-existing user', async () => {
    const res =  await request(server).get(`/reports/users/${nonExistingId}/${reportId}`);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name','NotFoundError');
  })
  test('get non-existing report', async () => {
    const report = new Report();
    const res =  await request(server).get(`/reports/users/${existingId}/${report.id}`);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name','NotFoundError');
  })
  test('get report without permissions', async () => {
    const res =  await request(server).get(`/reports/users/${existingId}/${reportId}`).query({
      token: 'invalid-token'
    });
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(403);
    expect(res.body).toHaveProperty('name','ForbiddenError');
  })
})
describe('update report recommendations', () => {
  test('update report for existing user', async () => {
    const res =  await request(server).put(`/reports/users/${existingId}/${reportId}`).query({
      recommendations
    });
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toHaveProperty('recommendations',recommendations);
  })
  test('update report for non-existing user', async () => {
    const res =  await request(server).put(`/reports/users/${nonExistingId}/${reportId}`).query({
      recommendations
    });
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name','NotFoundError');
  })
  test('update non-existing report for existing user', async () => {
    const report = new Report();
    const res =  await request(server).put(`/reports/users/${existingId}/${report.id}`).query({
      recommendations
    });
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name','NotFoundError');
  })
  test('update report without permissions', async () => {
    const res =  await request(server).put(`/reports/users/${existingId}/${reportId}`).query({
      recommendations,
      token: 'invalid-token'
    });
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(403);
    expect(res.body).toHaveProperty('name','ForbiddenError');
  })
})
describe('delete particular report', () => {
  test('delete existing report for existing user', async () => {
    const res =  await request(server).delete(`/reports/users/${existingId}/${reportId}`);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toEqual('report deleted');
  })
  test('delete report for non-existing user', async () => {
    const res =  await request(server).delete(`/reports/users/${nonExistingId}/${reportId}`);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name','NotFoundError');
  })
  test('delete non-existing report for existing user', async () => {
    const report = new Report();
    const res =  await request(server).delete(`/reports/users/${existingId}/${report.id}`);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name','NotFoundError');
  })
  test('delete report without permissions', async () => {
    const report = new Report();
    const res =  await request(server).delete(`/reports/users/${existingId}/${report.id}`).query({
      token: 'invalid-token'
    });
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(403);
    expect(res.body).toHaveProperty('name','ForbiddenError');
  })
})
describe('get all reports assigned to an accountant', () => {
  test('get all reports for an existing accountant', async () => {
    const res =  await request(server).get(`/reports/accountants/${accountantId}`);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toBeInstanceOf(Array);
  })
  test('get all reports for a non-existing accountant', async () => {
    const accountant = new Account();
    const res =  await request(server).get(`/reports/accountants/${accountant.id}`);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(404);
    expect(res.body).toHaveProperty('name','NotFoundError');
  })
  test('get all reports for an accountant with no users assigned', async () => {
    const newAccountantId = '1567'
    await request(server).post(`/accounts`).query({...accountantFields, accountId: newAccountantId})
    const res =  await request(server).get(`/reports/accountants/${newAccountantId}`);
    expect(res.header['content-type']).toBe('application/json; charset=utf-8');
    expect(res.statusCode).toBe(200);
    expect(res.body).toBeInstanceOf(Array);
    expect(res.body.length).toEqual(0);

    await request(server).delete(`/accounts/${newAccountantId}`);
  })
})

afterAll((done) => {
  mongoose.disconnect();
  server.close();
  done();
});
