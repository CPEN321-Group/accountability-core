/**
 * Filters out the null or empty fields.
 * @param {*} fields - unfiltered fields
 * @returns object containing only the defined and valid fields
 */
function getDefinedFields(fields) {
  let definedFields = {};
  Object.keys(fields).forEach((key) => {
    if(fields && isValid(fields[key])){
      definedFields[key] = fields[key];
    }
  })
  return definedFields;
}

function isValid(field) {
  let valid = true;
  if (field === null || field === undefined || field === '') {
    valid = false;
  }
  if (typeof field === 'string' || field instanceof String) {
    if (field.trim().length === 0){ //reject strings containing only whitespace
      valid = false;
    }
  }
  return valid;
}

module.exports = {getDefinedFields,isValid};