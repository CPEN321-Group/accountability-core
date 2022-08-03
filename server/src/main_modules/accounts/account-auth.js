require('dotenv').config();
const {OAuth2Client} = require('google-auth-library');
// const axios = require('axios').default

// const FB_JWKS_ENDPOINT = 'https://www.facebook.com/.well-known/oauth/openid/jwks/'

// function parseJWT(token) {
//   const sections = token.split('.');
//   if (sections.length !== 3) {
//     return false;
//   }
//   try {
//     const payload = JSON.parse(Buffer.from(sections[1], 'base64').toString());
//   } catch (err) {
//     return false; //JSON parsing error
//   }
// }
// async function getFBPublicKey() {
//   try {
//     const {data} = await axios.get(FB_JWKS_ENDPOINT);
//     console.log(data)
//     return data;
//   } catch (err) {
//     console.log(err);
//   }
// }

const GOOGLE_CLIENT_ID = process.env.GOOGLE_CLIENT_ID || '';
const googleClient = new OAuth2Client(GOOGLE_CLIENT_ID);

async function googleVerifyToken(token) {
  if (!process.env.GOOGLE_CLIENT_ID) {
    return true;
  }
  try {
    const ticket = await googleClient.verifyIdToken({
      idToken: token,
      audience: GOOGLE_CLIENT_ID,
    });
    const payload = ticket.getPayload();
    console.log(payload);
    const accountId = payload['sub'];
    const clientId = payload['aud'];
    return true;
  } catch (err) {
    // console.log(err);
    return false;
  }
}

// async function facebookVerifyToken(token) {
//   return true;
// }
module.exports = {googleVerifyToken}