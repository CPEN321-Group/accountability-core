const { default: mongoose } = require("mongoose");
const { createAccount, findAccount, findAccountants, updateProfile, deleteAccount, createReview, createSubscription, updateSubscription } = require("../../main_modules/accounts/account-store");

beforeAll(done => {
  done()
})

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
    let modifiedAccountFields = accountFields;
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
    let modifiedAccountFields = accountFields;
    modifiedAccountFields.accountId = 'ai93n';
    modifiedAccountFields.age = -1;
    await createAccount(modifiedAccountFields, (err,status,returnData) => {
      expect(err).toBeNull()
      expect(status).toStrictEqual(400);
      expect(returnData).toEqual('invalid age');
    })
  })


  // test('valid creation', async () => {
  //   let modifiedAccountFields = accountFields;
  //   modifiedAccountFields.accountId = 'ai93n'
  //   await createAccount(modifiedAccountFields, (err,status,returnData) => {
  //     expect(err).toBeNull()
  //     expect(status).toStrictEqual(200);
  //     expect(returnData).toHaveProperty('accountId');
  //   })
  //   await deleteAccount(modifiedAccountFields.accountId, (err,status,returnData) => {});
  // })
})

// describe('testing findAccount', () => {
//   test('account exists', async () => {
//     await findAccount('1234', (err,status,returnData) => {
//       expect(err).toBeNull()
//       expect(status).toStrictEqual(200);
//       expect(returnData).toEqual(account);
//     })
//   })
  
//   test('account not exist', async () => {
//     await findAccount('1456', (err,status,returnData) => {
//       expect(err).toBeNull()
//       expect(status).toStrictEqual(404);
//       expect(returnData).toEqual('account not found');
//     })
//   })
// })

// describe('testing findAccountants', () => {
//   test('no accountants are found', async () => {
//     await findAccountants((err,status,returnData) => {
//       expect(err).toBeNull()
//       expect(status).toStrictEqual(200);
//       expect(returnData).toEqual([]);
//     })
//   })
// })

// describe('testing updateProfile', () => {
//   test('account exists', async () => {
//     await updateProfile('1234', {} ,(err,status,returnData) => {
//       expect(err).toBeNull()
//       expect(status).toStrictEqual(200);
//       expect(returnData).toEqual(account);
//     })
//   })
  
//   test('account not exist', async () => {
//     await updateProfile('1456', {},(err,status,returnData) => {
//       expect(err).toBeNull()
//       expect(status).toStrictEqual(404);
//       expect(returnData).toEqual('account not found');
//     })
//   })
// })

// describe('testing deleteAccount', () => {
//   test('account exists', async () => {
//     await deleteAccount('1234', (err,status,returnData) => {
//       expect(err).toBeNull()
//       expect(status).toStrictEqual(200);
//       expect(returnData).toEqual('account deleted');
//     })
//   })
  
//   test('account not exist', async () => {
//     await deleteAccount('1456', (err,status,returnData) => {
//       expect(err).toBeNull()
//       expect(status).toStrictEqual(404);
//       expect(returnData).toEqual('account not found');
//     })
//   })
// })

// describe('testing createReview', () => {
//   test('account exists', async () => {
//     await createReview('1234',{},(err,status,returnData) => {
//       expect(err).toBeNull()
//       expect(status).toStrictEqual(200);
//       expect(returnData).toEqual(account);
//     })
//   })
  
//   test('account not exist', async () => {
//     await createReview('1456',{}, (err,status,returnData) => {
//       expect(err).toBeNull()
//       expect(status).toStrictEqual(404);
//       expect(returnData).toEqual('accountant not found');
//     })
//   })
// })

// describe('testing createSubscription', () => {
//   test('account exists', async () => {
//     await createSubscription('1234', {},(err,status,returnData) => {
//       expect(err).toBeNull()
//       expect(status).toStrictEqual(200);
//       expect(returnData).toEqual(account);
//     })
//   })
  
//   test('account not exist', async () => {
//     await createSubscription('1456', {},(err,status,returnData) => {
//       expect(err).toBeNull()
//       expect(status).toStrictEqual(404);
//       expect(returnData).toEqual('account not found');
//     })
//   })
// })

// describe('testing updateSubscription', () => {
//   test('account exists', async () => {
//     await updateSubscription('1234', {},(err,status,returnData) => {
//       expect(err).toBeNull()
//       expect(status).toStrictEqual(200);
//       expect(returnData).toEqual(account);
//     })
//   })
//   test('account not exist', async () => {
//     await updateSubscription('1456', {},(err,status,returnData) => {
//       expect(err).toBeNull()
//       expect(status).toStrictEqual(404);
//       expect(returnData).toEqual('account not found');
//     })
//   })
// })

// afterAll(()=>{ mongoose.disconnect();});
// afterAll( async () =>{
//         await mongoose.connection.close()
//     })
afterAll((done) => {
  mongoose.connection.close();
  done();
});
