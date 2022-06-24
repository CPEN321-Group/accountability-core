function getItemFromList(list,id) {
  if (!Array.isArray(list)) return null;
  console.log(`searching for: ${id}`)
  let foundItem;
  list.forEach(item => {
    if (item.id === id) {
      foundItem = item
    }
  })
  return foundItem;
}

module.exports = {getItemFromList};