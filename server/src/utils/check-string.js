function isLetterString(txt) { 
  var letters = /^[a-zA-Z]+$/;
  if (letters.test(txt)) {
    return true;
  } else {
    console.log(`illegal characters: ${txt}\n`);
    return false;
  }
}

module.exports = {isLetterString};