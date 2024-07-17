in order to run the project, first run build_jars.bat and then run start.bat
## Class Diagrams

[View Class Diagrams](https://app.diagrams.net/#G109K7y1vXoLTnIhWANlhXjqybpsAV9G3X#%7B%22pageId%22%3A%226BZR_8KA0rACz6vCORm-%22%7D)

[View Use Cases]([https://app.diagrams.net/#G109K7y1vXoLTnIhWANlhXjqybpsAV9G3X#%7B%22pageId%22%3A%226BZR_8KA0rACz6vCORm-%22%7D](https://github.com/Yuval-Roth/sadna/blob/main/Use_cases.pdf))


## initial run file
the initial run file is a text file with instructions. every instrction ends with ";" and written by one from the list below.
some notes:
- Each argument is a string, unless otherwise specified. there is no need to use - ""
- The white spaces between the arguments and the commas are meaningless
- The boolean type can be one of the values: true/false
- The date type must written by the form DDMMYYYY
- The hour type must written by the form HHMM
- The integer type must be integer number
- The rating type must be integer number between 1-5
- The double type must be decimal number
- The StoreAction type can be one of the values:
  - ALL
  - ADD_PRODUCT
  - REMOVE_PRODUCT
  - UPDATE_PRODUCT
  - ENABLE_PRODUCT
  - DISABLE_PRODUCT
  - SET_PRODUCT_QUANTITY
  - GET_PRODUCT_QUANTITY
  - CLOSE_STORE
  - OPEN_STORE
  - UPDATE_STORE_INFORMATION
  - VIEW_STORE_TRANSACTIONS
  - SEND_SHIPMENT
  - ADD_OWNER
  - REMOVE_OWNER
  - ADD_MANAGER
  - REMOVE_MANAGER
  - ADD_PERMISSION_TO_MANAGER
  - REMOVE_PERMISSION_FROM_MANAGER
  - VIEW_ROLES_INFORMATION
  - EDIT_POLICY
  - EDIT_DISCOUNT

### The instructions
- register(email, userid, password, Date birthDate)
- login(userid, password)
- logout()


- sendShipment(transactionId, serviceId, storeId)
- addShippingService(serviceId)
- removeShippingService(serviceId)
- addPaymentService(serviceserviceId)
- removePaymentService(serviceserviceId)


- startMarket()
- shutdown()

- sendNotification(title, message, senderId, receiverId)
- setReadValue(notificationId,Boolean read)
- deleteNotification(notificationId)

- addStore(founderId, storeName, description)
- openStore(storeName)
- closeStore(storeName)
- addProduct(storeName, productId, productName, Double price, category, description, Rating rating)
- updateProduct(storeName, productId, productName, Double price, category, description, Rating rating)
- removeProduct(storeName, productId)
- disableProduct(storeName, productId)
- enableProduct(storeName, productId)
- setProductQuantity(storeName, productid, Integer quantity)
- addOwner(storeName, appointedUserId)
- addManager(storeName, appointedUserId)
- removeOwner(storeName, appointedUserId)
- removeManager(storeName, appointedUserId)
- addPermissionToManager(storeName, appointedUserId, StoreAction storeAction)
- removePermissionFromManager(storeName, appointedUserId, StoreAction storeAction)
- addDiscountRuleByCFG(storeName, discountRuleCFG)
- deleteAllDiscounts(storeName)
- addProductToCart(storeName, productid, Integer quantity)
- removeProductFromCart(storeName, productid)
- changeProductQuantityAtCart(storeName, productid, Integer quantity)
