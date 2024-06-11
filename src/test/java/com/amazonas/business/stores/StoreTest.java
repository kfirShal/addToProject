package com.amazonas.business.stores;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.inventory.ProductInventory;
import com.amazonas.business.permissions.PermissionsController;
import com.amazonas.business.permissions.actions.StoreActions;
import com.amazonas.business.stores.reservations.PendingReservationMonitor;
import com.amazonas.business.stores.reservations.Reservation;
import com.amazonas.business.stores.reservations.ReservationFactory;
import com.amazonas.business.stores.search.SearchRequestBuilder;
import com.amazonas.business.stores.storePositions.AppointmentSystem;
import com.amazonas.business.stores.storePositions.StoreRole;
import com.amazonas.exceptions.StoreException;
import com.amazonas.repository.TransactionRepository;
import com.amazonas.utils.Rating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StoreTest {

    private final Product laptop;
    private final Product book;
    private final Product shirt;
    private final Product blender;
    private final Product toy;

    private Store store;
    private ProductInventory productInventory;
    private AppointmentSystem appointmentSystem;
    private ReservationFactory reservationFactory;
    private PendingReservationMonitor pendingReservationMonitor;
    private PermissionsController permissionsController;
    private TransactionRepository transactionRepository;

    public StoreTest() {

        // Create products directly with related names
        laptop = new Product("P1001", "Dell XPS 13", 999.99, "Electronics", "Powerful and compact laptop.", Rating.ONE_STAR);
        laptop.addKeyWords("Laptop");
        laptop.addKeyWords("Dell");
        laptop.addKeyWords("XPS");

        book = new Product("P1002", "The Great Gatsby", 10.50, "Books", "Classic novel by F. Scott Fitzgerald.", Rating.TWO_STARS);
        book.addKeyWords("Book");
        book.addKeyWords("Novel");
        book.addKeyWords("Classic");

        shirt = new Product("P1003", "Men's Casual Shirt", 25.99, "Clothing", "Comfortable and stylish casual shirt.", Rating.THREE_STARS);
        shirt.addKeyWords("Shirt");
        shirt.addKeyWords("Casual");
        shirt.addKeyWords("Men's Clothing");

        blender = new Product("P1004", "Ninja Professional Blender", 120.00, "Home", "High-powered kitchen blender.", Rating.FOUR_STARS);
        blender.addKeyWords("Blender");
        blender.addKeyWords("Kitchen Appliance");
        blender.addKeyWords("Ninja");

        toy = new Product("P1005", "LEGO Star Wars Set", 49.99, "Toys", "Exciting LEGO set for Star Wars fans.", Rating.FIVE_STARS);
        toy.addKeyWords("LEGO");
        toy.addKeyWords("Star Wars");
        toy.addKeyWords("Toy");
    }

    @BeforeEach
    void setUp() {
        String storeId = "store1";
        String storeName = "storeName1";
        String storeDescription = "storeDescription1";
        Rating storeRating = Rating.FIVE_STARS;
        productInventory = mock(ProductInventory.class);
        appointmentSystem = mock(AppointmentSystem.class);
        reservationFactory = mock(ReservationFactory.class);
        pendingReservationMonitor = mock(PendingReservationMonitor.class);
        permissionsController = mock(PermissionsController.class);
        transactionRepository = mock(TransactionRepository.class);

        store = new Store(storeId,
                storeName,
                storeDescription,
                storeRating,
                productInventory,
                appointmentSystem,
                reservationFactory,
                pendingReservationMonitor,
                permissionsController,
                transactionRepository);
    }

    @Test
    void addSalePolicy() {
        //TODO: Implement this test in future versions
    }

    @Test
    void removeSalePolicy() {
        //TODO: Implement this test in future versions
    }

    @Test
    void calculatePrice() {
        //TODO: Implement this test in future versions
    }

    @Test
    void searchProductByName() {
        when(productInventory.getAllAvailableProducts()).thenReturn(Set.of(laptop, book, shirt, blender, toy));
        var searchRequest = SearchRequestBuilder.create();

        searchRequest.setProductName("Dell XPS");
        List<Product> actual = store.searchProduct(searchRequest.build());
        assertEquals(1, actual.size());
        assertEquals(laptop, actual.getFirst());

        searchRequest.setProductName("Gatsby");
        actual = store.searchProduct(searchRequest.build());
        assertEquals(1, actual.size());
        assertEquals(book, actual.getFirst());

        searchRequest.setProductName("Shirt");
        actual = store.searchProduct(searchRequest.build());
        assertEquals(1, actual.size());
        assertEquals(shirt, actual.getFirst());

        searchRequest.setProductName("Ninja");
        actual = store.searchProduct(searchRequest.build());
        assertEquals(1, actual.size());
        assertEquals(blender, actual.getFirst());

        searchRequest.setProductName("LEGO");
        actual = store.searchProduct(searchRequest.build());
        assertEquals(1, actual.size());
        assertEquals(toy, actual.getFirst());
    }

    @Test
    void searchProductByNameAndRating() {
        when(productInventory.getAllAvailableProducts()).thenReturn(Set.of(laptop, book, shirt, blender, toy));
        var searchRequest = SearchRequestBuilder.create();

        searchRequest.setProductName("Dell XPS");
        searchRequest.setProductRating(Rating.ONE_STAR);
        List<Product> actual = store.searchProduct(searchRequest.build());
        assertEquals(1, actual.size());
        assertEquals(laptop, actual.getFirst());

        searchRequest.setProductName("Gatsby");
        searchRequest.setProductRating(Rating.TWO_STARS);
        actual = store.searchProduct(searchRequest.build());
        assertEquals(1, actual.size());
        assertEquals(book, actual.getFirst());

        searchRequest.setProductName("Shirt");
        searchRequest.setProductRating(Rating.THREE_STARS);
        actual = store.searchProduct(searchRequest.build());
        assertEquals(1, actual.size());
        assertEquals(shirt, actual.getFirst());

        searchRequest.setProductName("Ninja");
        searchRequest.setProductRating(Rating.FOUR_STARS);
        actual = store.searchProduct(searchRequest.build());
        assertEquals(1, actual.size());
        assertEquals(blender, actual.getFirst());

        searchRequest.setProductName("LEGO");
        searchRequest.setProductRating(Rating.FIVE_STARS);
        actual = store.searchProduct(searchRequest.build());
        assertEquals(1, actual.size());
        assertEquals(toy, actual.getFirst());
    }

    @Test
    void searchProductByNameAndRatingBad(){
        when(productInventory.getAllAvailableProducts()).thenReturn(Set.of(laptop, book, shirt, blender, toy));
        var searchRequest = SearchRequestBuilder.create();

        searchRequest.setProductName("Dell XPS");
        searchRequest.setProductRating(Rating.TWO_STARS);
        List<Product> actual = store.searchProduct(searchRequest.build());
        assertEquals(0, actual.size());

        searchRequest.setProductName("Gatsby");
        searchRequest.setProductRating(Rating.THREE_STARS);
        actual = store.searchProduct(searchRequest.build());
        assertEquals(0, actual.size());

        searchRequest.setProductName("Shirt");
        searchRequest.setProductRating(Rating.FOUR_STARS);
        actual = store.searchProduct(searchRequest.build());
        assertEquals(0, actual.size());

        searchRequest.setProductName("Ninja");
        searchRequest.setProductRating(Rating.FIVE_STARS);
        actual = store.searchProduct(searchRequest.build());
        assertEquals(0, actual.size());
    }

    @Test
    void searchProductByNameAndPriceGood(){
        when(productInventory.getAllAvailableProducts()).thenReturn(Set.of(laptop, book, shirt, blender, toy));
        var searchRequest = SearchRequestBuilder.create();

        searchRequest.setProductName("Dell XPS");
        searchRequest.setMinPrice(999);
        searchRequest.setMaxPrice(1000);
        List<Product> actual = store.searchProduct(searchRequest.build());
        assertEquals(1, actual.size());
        assertEquals(laptop, actual.getFirst());

        searchRequest.setProductName("Gatsby");
        searchRequest.setMinPrice(10);
        searchRequest.setMaxPrice(11);
        actual = store.searchProduct(searchRequest.build());
        assertEquals(1, actual.size());
        assertEquals(book, actual.getFirst());

        searchRequest.setProductName("Shirt");
        searchRequest.setMinPrice(25);
        searchRequest.setMaxPrice(26);
        actual = store.searchProduct(searchRequest.build());
        assertEquals(1, actual.size());
        assertEquals(shirt, actual.getFirst());

        searchRequest.setProductName("Ninja");
        searchRequest.setMinPrice(120);
        searchRequest.setMaxPrice(121);
        actual = store.searchProduct(searchRequest.build());
        assertEquals(1, actual.size());
        assertEquals(blender, actual.getFirst());

        searchRequest.setProductName("LEGO");
        searchRequest.setMinPrice(49);
        searchRequest.setMaxPrice(50);
        actual = store.searchProduct(searchRequest.build());
        assertEquals(1, actual.size());
        assertEquals(toy, actual.getFirst());
    }

    @Test
    void searchProductByNameAndPriceBad(){
        when(productInventory.getAllAvailableProducts()).thenReturn(Set.of(laptop, book, shirt, blender, toy));
        var searchRequest = SearchRequestBuilder.create();

        searchRequest.setProductName("Dell XPS");
        searchRequest.setMinPrice(1000);
        searchRequest.setMaxPrice(1001);
        List<Product> actual = store.searchProduct(searchRequest.build());
        assertEquals(0, actual.size());

        searchRequest.setProductName("Gatsby");
        searchRequest.setMinPrice(11);
        searchRequest.setMaxPrice(12);
        actual = store.searchProduct(searchRequest.build());
        assertEquals(0, actual.size());

        searchRequest.setProductName("Shirt");
        searchRequest.setMinPrice(26);
        searchRequest.setMaxPrice(27);
        actual = store.searchProduct(searchRequest.build());
        assertEquals(0, actual.size());

        searchRequest.setProductName("Ninja");
        searchRequest.setMinPrice(121);
        searchRequest.setMaxPrice(122);
        actual = store.searchProduct(searchRequest.build());
        assertEquals(0, actual.size());

        searchRequest.setProductName("LEGO");
        searchRequest.setMinPrice(50);
        searchRequest.setMaxPrice(51);
        actual = store.searchProduct(searchRequest.build());
        assertEquals(0, actual.size());
    }

    @Test
    void searchProductByKeywords(){
        when(productInventory.getAllAvailableProducts()).thenReturn(Set.of(laptop, book, shirt, blender, toy));
        var searchRequest = SearchRequestBuilder.create();

        searchRequest.setKeyWords(List.of("Dell"));
        List<Product> actual = store.searchProduct(searchRequest.build());
        assertEquals(1, actual.size());
        assertEquals(laptop, actual.getFirst());

        searchRequest.setKeyWords(List.of("Novel"));
        actual = store.searchProduct(searchRequest.build());
        assertEquals(1, actual.size());
        assertEquals(book, actual.getFirst());

        searchRequest.setKeyWords(List.of("Shirt"));
        actual = store.searchProduct(searchRequest.build());
        assertEquals(1, actual.size());
        assertEquals(shirt, actual.getFirst());

        searchRequest.setKeyWords(List.of("Ninja"));
        actual = store.searchProduct(searchRequest.build());
        assertEquals(1, actual.size());
        assertEquals(blender, actual.getFirst());

        searchRequest.setKeyWords(List.of("LEGO"));
        actual = store.searchProduct(searchRequest.build());
        assertEquals(1, actual.size());
        assertEquals(toy, actual.getFirst());
    }

    @Test
    void searchProductByCategory() {
        when(productInventory.getAllAvailableProducts()).thenReturn(Set.of(laptop, book, shirt, blender, toy));
        var searchRequest = SearchRequestBuilder.create();

        searchRequest.setProductCategory("Electronics");
        List<Product> actual = store.searchProduct(searchRequest.build());
        assertEquals(1, actual.size());
        assertEquals(laptop, actual.getFirst());

        searchRequest.setProductCategory("Books");
        actual = store.searchProduct(searchRequest.build());
        assertEquals(1, actual.size());
        assertEquals(book, actual.getFirst());

        searchRequest.setProductCategory("Clothing");
        actual = store.searchProduct(searchRequest.build());
        assertEquals(1, actual.size());
        assertEquals(shirt, actual.getFirst());

        searchRequest.setProductCategory("Home");
        actual = store.searchProduct(searchRequest.build());
        assertEquals(1, actual.size());
        assertEquals(blender, actual.getFirst());

        searchRequest.setProductCategory("Toys");
        actual = store.searchProduct(searchRequest.build());
        assertEquals(1, actual.size());
        assertEquals(toy, actual.getFirst());
    }

    @Test
    void searchProductsMultipleConditions(){
        when(productInventory.getAllAvailableProducts()).thenReturn(Set.of(laptop, book, shirt, blender, toy));
        var searchRequest = SearchRequestBuilder.create();

        searchRequest.setProductName("Dell XPS");
        searchRequest.setProductRating(Rating.ONE_STAR);
        searchRequest.setMinPrice(999);
        searchRequest.setMaxPrice(1000);
        searchRequest.setKeyWords(List.of("Dell"));
        searchRequest.setProductCategory("Electronics");
        List<Product> actual = store.searchProduct(searchRequest.build());
        assertEquals(1, actual.size());
        assertEquals(laptop, actual.getFirst());

        searchRequest.setProductName("Gatsby");
        searchRequest.setProductRating(Rating.TWO_STARS);
        searchRequest.setMinPrice(10);
        searchRequest.setMaxPrice(11);
        searchRequest.setKeyWords(List.of("Novel"));
        searchRequest.setProductCategory("Books");
        actual = store.searchProduct(searchRequest.build());
        assertEquals(1, actual.size());
        assertEquals(book, actual.getFirst());

        searchRequest.setProductName("Shirt");
        searchRequest.setProductRating(Rating.THREE_STARS);
        searchRequest.setMinPrice(25);
        searchRequest.setMaxPrice(26);
        searchRequest.setKeyWords(List.of("Shirt"));
        searchRequest.setProductCategory("Clothing");
        actual = store.searchProduct(searchRequest.build());
        assertEquals(1, actual.size());
        assertEquals(shirt, actual.getFirst());

        searchRequest.setProductName("Ninja");
        searchRequest.setProductRating(Rating.FOUR_STARS);
        searchRequest.setMinPrice(120);
        searchRequest.setMaxPrice(121);
        searchRequest.setKeyWords(List.of("Ninja"));
        searchRequest.setProductCategory("Home");
        actual = store.searchProduct(searchRequest.build());
        assertEquals(1, actual.size());
        assertEquals(blender, actual.getFirst());

        searchRequest.setProductName("LEGO");
        searchRequest.setProductRating(Rating.FIVE_STARS);
        searchRequest.setMinPrice(49);
        searchRequest.setMaxPrice(50);
        searchRequest.setKeyWords(List.of("LEGO"));
        searchRequest.setProductCategory("Toys");
        actual = store.searchProduct(searchRequest.build());
        assertEquals(1, actual.size());
        assertEquals(toy, actual.getFirst());
    }

    @Test
    void searchProductMultipleResults(){
        when(productInventory.getAllAvailableProducts()).thenReturn(Set.of(laptop, book, shirt, blender, toy));
        var searchRequest = SearchRequestBuilder.create();

        searchRequest.setProductName("Dell XPS").setKeyWords(List.of("Novel"));
        List<Product> actual = store.searchProduct(searchRequest.build());
        assertProductListsEqual(List.of(laptop, book), actual);

        searchRequest.setProductName("Gatsby").setKeyWords(List.of("Shirt"));
        actual = store.searchProduct(searchRequest.build());
        assertProductListsEqual(List.of(book, shirt), actual);

        searchRequest.setProductName("Shirt").setKeyWords(List.of("Blender"));
        actual = store.searchProduct(searchRequest.build());
        assertProductListsEqual(List.of(shirt, blender), actual);

        searchRequest.setProductName("Ninja").setKeyWords(List.of("LEGO"));
        actual = store.searchProduct(searchRequest.build());
        assertProductListsEqual(List.of(blender, toy), actual);

        searchRequest.setProductName("LEGO").setKeyWords(List.of("Dell"));
        actual = store.searchProduct(searchRequest.build());
        assertProductListsEqual(List.of(toy, laptop), actual);
    }

    @Test
    void searchProductNoResults(){
        when(productInventory.getAllAvailableProducts()).thenReturn(Set.of(laptop, book, shirt, blender, toy));
        var searchRequest = SearchRequestBuilder.create();

        searchRequest.setProductName("Apple");
        List<Product> actual = store.searchProduct(searchRequest.build());
        assertEquals(0, actual.size());

        searchRequest.setProductName("Harry Potter");
        actual = store.searchProduct(searchRequest.build());
        assertEquals(0, actual.size());

        searchRequest.setProductName("T-shirt");
        actual = store.searchProduct(searchRequest.build());
        assertEquals(0, actual.size());

        searchRequest.setProductName("Vitamix");
        actual = store.searchProduct(searchRequest.build());
        assertEquals(0, actual.size());

        searchRequest.setProductName("Mega Bloks");
        actual = store.searchProduct(searchRequest.build());
        assertEquals(0, actual.size());
    }

    @Test
    void reserveProductGood() {
        Map<String,Integer> products = new HashMap<>(){{
            put(laptop.productId(), 1);
            put(book.productId(), 1);
            put(shirt.productId(), 1);
            put(blender.productId(), 1);
            put(toy.productName(), 1);
        }};

        when(productInventory.getQuantity(laptop.productId())).thenReturn(1);
        when(productInventory.getQuantity(book.productId())).thenReturn(1);
        when(productInventory.getQuantity(shirt.productId())).thenReturn(1);
        when(productInventory.getQuantity(blender.productId())).thenReturn(1);
        when(productInventory.getQuantity(toy.productId())).thenReturn(1);
        when(reservationFactory.get(any(), any(),any())).thenReturn(mock(Reservation.class));

        Reservation actualReservation = store.reserveProducts(products);
        assertNotNull(actualReservation); // return null
    }

    @Test
    void reserveProductBad() {
        Map<String,Integer> products = new HashMap<>(){{
            put(laptop.productId(), 1);
            put(book.productId(), 1);
            put(shirt.productId(), 1);
            put(blender.productId(), 1);
            put(toy.productName(), 1);
        }};

        when(productInventory.getQuantity(laptop.productId())).thenReturn(2);
        when(productInventory.getQuantity(book.productId())).thenReturn(3);
        when(productInventory.getQuantity(shirt.productId())).thenReturn(4);
        // Only 1 blender in stock, but 5 requested
        when(productInventory.getQuantity(blender.productId())).thenReturn(1);
        when(productInventory.getQuantity(toy.productId())).thenReturn(6);
        when(reservationFactory.get(any(), any(),any())).thenReturn(mock(Reservation.class));

        Reservation actualReservation = store.reserveProducts(products);
        assertNull(actualReservation);
    }

    private void assertProductListsEqual(List<Product> expected, List<Product> actual) {
        assertEquals(expected.size(), actual.size());
        for (Product product : expected) {
            assertTrue(actual.contains(product));
        }
    }

    @Test
    void testActionWhenStoreClosed() {
        store.closeStore();
        assertThrows(StoreException.class, () -> store.addProduct(laptop));
    }

    @Test
    void testConcurrentAccessToLastProduct() throws InterruptedException {
        when(productInventory.getQuantity(laptop.productId())).thenReturn(1);
        when(reservationFactory.get(any(), any(),any())).thenReturn(mock(Reservation.class));

        ExecutorService service = Executors.newFixedThreadPool(2);
        service.submit(() -> store.reserveProducts(Map.of(laptop.productId(), 1)));
        service.submit(() -> store.reserveProducts(Map.of(laptop.productId(), 1)));
        service.shutdown();
        service.awaitTermination(1, TimeUnit.SECONDS);

        verify(productInventory, times(2)).setQuantity(eq(laptop.productId()), anyInt());
    }

    @Test
    void testAddTwiceProduct() throws StoreException {
        assertThrows(StoreException.class, () -> store.addProduct(laptop));
    }

    @Test
    void testRemoveProduct() throws StoreException {
        when(productInventory.removeProduct(laptop.productId())).thenReturn(true);
        store.removeProduct(laptop.productId());
        verify(productInventory, times(1)).removeProduct(laptop.productId());
    }

    @Test
    void testDisableProduct() {
        store.disableProduct(laptop.productId());
        verify(productInventory, times(1)).disableProduct(laptop.productId());
    }

    @Test
    void testCancelReservation() {
        Reservation reservation = mock(Reservation.class);
        when(reservation.productIdToQuantity()).thenReturn(Map.of(laptop.productId(), 1));
        store.cancelReservation(reservation);
        verify(productInventory, times(1)).setQuantity(eq(laptop.productId()), anyInt());
    }

}