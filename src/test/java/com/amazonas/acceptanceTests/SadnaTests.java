package com.amazonas.acceptanceTests;

import com.amazonas.business.market.GlobalSearchRequest;
import com.amazonas.business.payment.PaymentMethod;
import com.amazonas.business.stores.StoreActions;
import com.amazonas.business.transactions.Transaction;
import com.amazonas.business.userProfiles.User;
import com.amazonas.business.userProfiles.UserActions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SadnaTests {
    protected AuthenticationBridge authenticationBridge;
    protected MarketBridge marketBridge;
    protected PaymentBridge paymentBridge;
    protected PermissionBridge permissionBridge;
    protected ProductBridge productBridge;
    protected StoreBridge storeBridge;
    protected TransactionBridge transactionBridge;
    protected UserBridge userBridge;

    public SadnaTests(AuthenticationBridge authenticationBridge,
                      MarketBridge marketBridge,
                      PaymentBridge paymentBridge,
                      PermissionBridge permissionBridge,
                      ProductBridge productBridge,
                      StoreBridge storeBridge,
                      TransactionBridge transactionBridge,
                      UserBridge userBridge
                      ) {
        this.authenticationBridge = authenticationBridge;
        this.marketBridge = marketBridge;
        this.paymentBridge = paymentBridge;
        this.permissionBridge = permissionBridge;
        this.productBridge = productBridge;
        this.storeBridge = storeBridge;
        this.transactionBridge = transactionBridge;
        this.userBridge = userBridge;
    }

    @AfterEach
    public void tearDown() {
        authenticationBridge = null;
        marketBridge = null;
        paymentBridge = null;
        permissionBridge = null;
        productBridge = null;
        storeBridge = null;
        transactionBridge = null;
        userBridge = null;
    }


    //////////Autenticate
    public boolean authenticate(String userId, String password) {
        return authenticationBridge.authenticate(userId, password);
    }

    //////////Market
    public boolean searchProduct(GlobalSearchRequest request){
        return marketBridge.searchProduct(request);
    }

    public boolean makePurchase(User user, String token){
        return marketBridge.makePurchase(user, token);
    }

    public boolean start(){
        return marketBridge.start();
    }

    public boolean shutdown(){
        return marketBridge.shutdown();
    }

    public boolean restart(){
        return marketBridge.restart();
    }

    //////////Payment
    public boolean charge(PaymentMethod paymentMethod, double amount){
        return paymentBridge.charge(paymentMethod, amount);
    }

    //////////Transactions
    public boolean documentTransaction(Transaction transaction){
        return transactionBridge.documentTransaction(transaction);
    }

    public boolean getTransactionByUser(String userId){
        return transactionBridge.getTransactionByUser(userId);
    }

    public boolean getTransactionByStore(String storeId){
        return transactionBridge.getTransactionByStore(storeId);
    }




    //////////Permission
    public boolean addPermission(String userId,String storeId, UserActions action){
        return permissionBridge.addPermission(userId, action);
    }

    public boolean removePermission(String userId,String storeId, UserActions action){
        return permissionBridge.removePermission(userId, action);
    }

    public boolean checkPermission(String userId,String storeId, UserActions action){
        return permissionBridge.checkPermission(userId, action);
    }



    public boolean addPermission(String userId, UserActions action){
        return permissionBridge.addPermission(userId, action);
    }

    public boolean removePermission(String userId, UserActions action){
        return permissionBridge.removePermission(userId, action);

    }
    public boolean checkPermission(String userId, UserActions action){
        return permissionBridge.checkPermission(userId, action);
    }





    //////////Product
    public boolean addProduct(String productName, String productDescription){
        return productBridge.addProduct(productName, productDescription);
    }

    public boolean removeProduct(String productName){
        return productBridge.removeProduct(productName);
    }

    public boolean updateProduct(String productName, String productDescription){
        return productBridge.updateProduct(productName, productDescription);
    }

    public boolean addProduct(String storeId,String productName, String productDescription){
        return productBridge.addProduct(storeId, productDescription);
    }

    public boolean updateProduct(String storeId,String productName, String productDescription){
        return productBridge.updateProduct(storeId, productDescription);
    }




    //////////Users
    public boolean register(String email, String userName, String password){
        return userBridge.register(email, userName, password);
    }
    public boolean enterAsGuest(){
        return false;
    }

    public boolean login(String email, String password){
        return false;
    }

    public boolean logout(){
        return false;
    }

    public boolean logoutAsGuest(){
        return false;
    }

    public boolean getCart(){
        return false;
    }

    public boolean addToCart(String productName, String productDescription){
        return false;
    }

    public boolean removeFromCart(String productName){
        return false;
    }











    public void testAuthenticateValidUser() {
        authenticationBridge.testAuthenticateValidUser();
    }
    public void testAuthenticateInvalidUser() {
        authenticationBridge.testAuthenticateInvalidUser();
    }
    public void testAuthenticateInvalidPassword() {
        authenticationBridge.testAuthenticateInvalidPassword();
    }
    public void testAddProduct() {
        productBridge.testAddProduct();
    }
    public void testAddProductValid() {
        productBridge.testAddProductValid();
    }

    public void testAddProductInvalid() {
        productBridge.testAddProductInvalid();
    }

    public void testAddProductDuplicate() {
        productBridge.testAddProductDuplicate();
    }

    public void testRemoveProductValid() {
        productBridge.testRemoveProductValid();
    }

    public void testRemoveProductInvalid() {
        productBridge.testRemoveProductInvalid();
    }

    public void testRemoveProductNonexistent() {
        productBridge.testRemoveProductNonexistent();
    }

    public void testUpdateProductValid() {
        productBridge.testUpdateProductValid();
    }

    public void testUpdateProductInvalid() {
        productBridge.testUpdateProductInvalid();
    }

    public void testUpdateProductNonexistent() {
        productBridge.testUpdateProductNonexistent();
    }

    public void testSearchProductByName() {
        productBridge.testSearchProductByName();
    }

    public void testSearchProductByCategory() {
        productBridge.testSearchProductByCategory();
    }

    public void testSearchProductByPriceRange() {
        productBridge.testSearchProductByPriceRange();
    }

    public void testMakePurchaseValid() {
        marketBridge.testMakePurchaseValid();
    }

    public void testMakePurchaseInsufficientStock() {
        marketBridge.testMakePurchaseInsufficientStock();
    }

    public void testMakePurchaseInvalidProduct() {
        marketBridge.testMakePurchaseInvalidProduct();
    }


    public void testChargeValid() {
        paymentBridge.testChargeValid();
    }

    public void testChargeInvalidCard() {
        paymentBridge.testChargeInvalidCard();
    }

    public void testChargeInsufficientFunds() {
        paymentBridge.testChargeInsufficientFunds();
    }

    public void testAddPermissionValid() {
        permissionBridge.testAddPermissionValid();
    }

    public void testAddPermissionInvalidUser() {
        permissionBridge.testAddPermissionInvalidUser();
    }

    public void testAddPermissionDuplicate() {
        permissionBridge.testAddPermissionDuplicate();
    }

    public void testRemovePermissionValid() {
        permissionBridge.testRemovePermissionValid();
    }

    public void testRemovePermissionInvalidUser() {
        permissionBridge.testRemovePermissionInvalidUser();
    }

    public void testRemovePermissionNonexistent() {
        permissionBridge.testRemovePermissionNonexistent();
    }

    public void testCheckPermissionValid() {
        permissionBridge.testCheckPermissionValid();
    }

    public void testCheckPermissionInvalidUser() {
        permissionBridge.testCheckPermissionInvalidUser();
    }

    public void testCheckPermissionNoPermission() {
        permissionBridge.testCheckPermissionNoPermission();
    }

    public void testRegister() {}
    public void testEnterAsGuest() {}
    public void testLogin() {}
    public void testLogout() {}
    public void testGetCart() {}
    public void testAddToCart() {}
    public void testRemoveFromCart() {}
    public void testDocumentTransaction() {}
    public void testGetTransactionByUser() {}
    public void testGetTransactionByStore() {}
}
