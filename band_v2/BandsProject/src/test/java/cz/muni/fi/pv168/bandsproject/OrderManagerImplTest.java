package cz.muni.fi.pv168.bandsproject;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Date;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.apache.derby.jdbc.EmbeddedDataSource;

import static org.junit.Assert.*;

/**
 * Created by Lenka on 18.3.2016.
 */
public class OrderManagerImplTest {
    private OrderManagerImpl managerOrder;
    private BandManagerImpl managerBand;
    private CustomerManagerImpl managerCustomer;
    
    private DataSource dataSource;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
   
    @Before
    public void setUp() throws SQLException {
        dataSource = prepareDataSource();
        try (Connection connection = dataSource.getConnection()) {
            connection.prepareStatement("CREATE TABLE BAND ("
                    + "id bigint primary key generated always as identity,"
                    + "name VARCHAR(40),"
                    + "styles VARCHAR(50),"
                    + "region VARCHAR(30),"
                    + "pricePerHour Double,"
                    + "rate Double)").executeUpdate();
            
            connection.prepareStatement("CREATE TABLE CUSTOMER ("
                    + "id bigint primary key generated always as identity,"
                    + "name VARCHAR(50),"
                    + "phoneNumber VARCHAR(20),"
                    + "adress VARCHAR(50))").executeUpdate();
            
            connection.prepareStatement("CREATE TABLE ORDER ("
                    + "id bigint primary key generated always as identity,"
                    + "idBand bigint,"
                    + "idCustomer bigint,"
                    + "date VARCHAR(30)"      
                    + "region VARCHAR(30)"   
                    + "duration int)").executeUpdate();
        }
        managerOrder = new OrderManagerImpl(dataSource);
        managerBand = new BandManagerImpl(dataSource);
        managerCustomer = new CustomerManagerImpl(dataSource);
    }
    
    private static DataSource prepareDataSource() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:ordermgr-test");
        ds.setCreateDatabase("create");
        return ds;
    }

    @After
    public void tearDown() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.prepareStatement("DROP TABLE ORDER").executeUpdate();
            connection.prepareStatement("DROP TABLE BAND").executeUpdate();
            connection.prepareStatement("DROP TABLE CUSTOMER").executeUpdate();
        }
    }
    
    @Test
    public void testCreateOrder() throws Exception {
        List<Style> styles = new ArrayList<>();
        styles.add(Style.metal);
        styles.add(Style.rock);
        Band band1 = newBand("Eufory", styles, Region.slovensko, 200.00, 7.4);
        managerBand.createBand(band1);
        
        Customer customer1 = newCustomer("Pepa", "666 123 456", "Kartouzska 69");
        managerCustomer.createCustomer(customer1);
        
        Order order1 = newOrder(band1, customer1, "02.05.2016", Region.jihomoravsky, 2);
        managerOrder.createOrder(order1);
        assertNotNull(order1.getId());
        
        List<Style> anotherStyles = new ArrayList<>();
        anotherStyles.add(Style.disco);
        anotherStyles.add(Style.hipHop);
        anotherStyles.add(Style.dnb);
        Band anotherBand = newBand("Some DiscoHipHopDnbBand", anotherStyles, Region.plzensky, 149.99, 2.3);
        managerBand.createBand(anotherBand);
        
        Customer customer2 = newCustomer("Alojz", "158 155 150", "Route 66");
        managerCustomer.createCustomer(customer2);
        
        Order order2 = newOrder(anotherBand, customer2, "14.08.2016", Region.pardubicky, 3);
        managerOrder.createOrder(order2);
        assertNotNull(order2.getId());
        
        assertNotNull(managerOrder.findOrderById(order1.getId()));
        assertNotNull(managerOrder.findOrderById(order2.getId()));
        
        Order getOrder1 = managerOrder.findOrderById(order1.getId());
        assertDeepEquals(order1, getOrder1);
        assertNotSame(order1, getOrder1);
        
        assertDeepNotEquals(order1, order2);
        
        Order getOrder2 = managerOrder.findOrderById(order2.getId());
        assertDeepEquals(order2, getOrder2);
        assertNotSame(order2, getOrder2);
    }

    @Test
    public void testUpdateOrder() throws Exception {
        List<Style> styles = new ArrayList<>();
        styles.add(Style.metal);
        styles.add(Style.rock);
        Band band = newBand("Eufory", styles, Region.slovensko, 200.00, 7.4);
        managerBand.createBand(band);
        
        Band band2 = newBand("Slize", styles, Region.slovensko, 200.00, 8.0);
        managerBand.createBand(band2);
        
        Customer customer = newCustomer("Pepa", "666 123 456", "Kartouzska 69");
        managerCustomer.createCustomer(customer);
        
        Customer customer2 = newCustomer("Lenka si to cte :O", "Samozrejme :P 666", "Kartouzska 69");
        managerCustomer.createCustomer(customer2);
        
        
        Order order = newOrder(band, customer, "01.02.2016", Region.jihocesky, 2);
        managerOrder.createOrder(order);
        Long orderId = order.getId();
        
        Order order2 = newOrder(band, customer, "01.05.2016", Region.jihocesky, 8);
        managerOrder.createOrder(order2);
        
        
        // ---- Duration
        order.setDuration(50);
        
        managerOrder.updateOrder(order);
        
        order = managerOrder.findOrderById(orderId);
        
        assertEquals(band.getId(), order.getBand().getId());
        assertEquals(customer.getId(), order.getCustomer().getId());
        assertEquals("01.02.2016", order.getDate().toString());
        assertEquals(Region.jihocesky, order.getPlace());
        assertEquals(50, order.getDuration());
        
        
        // ---- Place
        order.setPlace(Region.jihomoravsky);
        
        managerOrder.updateOrder(order);
        
        order = managerOrder.findOrderById(orderId);
        
        assertEquals(band.getId(), order.getBand().getId());
        assertEquals(customer.getId(), order.getCustomer().getId());
        assertEquals("01.02.2016", order.getDate().toString());
        assertEquals(Region.jihomoravsky, order.getPlace());
        assertEquals(50, order.getDuration());
        
        
        // ---- Date
        DateFormat format = new SimpleDateFormat("dd.mm.yyyy");
        Date date = format.parse("08.08.2016");
        order.setDate(date);
        
        managerOrder.updateOrder(order);
        
        order = managerOrder.findOrderById(orderId);
        
        assertEquals(band.getId(), order.getBand().getId());
        assertEquals(customer.getId(), order.getCustomer().getId());
        assertEquals("08.08.2018", order.getDate().toString());
        assertEquals(Region.jihomoravsky, order.getPlace());
        assertEquals(50, order.getDuration());
        
        
        // ---- Customer
        order.setCustomer(customer2);
        
        managerOrder.updateOrder(order);
        
        order = managerOrder.findOrderById(orderId);
        
        assertEquals(band.getId(), order.getBand().getId());
        assertEquals(customer2.getId(), order.getCustomer().getId());
        assertEquals("08.08.2018", order.getDate().toString());
        assertEquals(Region.jihomoravsky, order.getPlace());
        assertEquals(50, order.getDuration());
        
        
        // ---- Band
        order.setBand(band2);
        
        managerOrder.updateOrder(order);
        
        order = managerOrder.findOrderById(orderId);
        
        assertEquals(band2.getId(), order.getBand().getId());
        assertEquals(customer2.getId(), order.getCustomer().getId());
        assertEquals("08.08.2018", order.getDate().toString());
        assertEquals(Region.jihomoravsky, order.getPlace());
        assertEquals(50, order.getDuration());
        
        // --- 
        assertDeepEquals(order2, managerOrder.findOrderById(order2.getId()));
    }

    @Test
    public void testDeleteOrder() throws Exception {
        List<Style> styles = new ArrayList<>();
        styles.add(Style.metal);
        styles.add(Style.rock);
        Band band = newBand("Eufory", styles, Region.slovensko, 200.00, 7.4);
        managerBand.createBand(band);
        
        Customer customer = newCustomer("Pepa", "666 123 456", "Kartouzska 69");
        managerCustomer.createCustomer(customer);
        
        Order order = newOrder(band, customer, "01.02.2016", Region.jihocesky, 2);
        managerOrder.createOrder(order);
        
        Order order2 = newOrder(band, customer, "01.05.2016", Region.jihocesky, 8);
        managerOrder.createOrder(order2);
        
        
        assertNotNull(managerOrder.findOrderById(order.getId()));
        assertNotNull(managerOrder.findOrderById(order2.getId()));
        
        managerOrder.deleteOrder(order);

        assertNull(managerOrder.findOrderById(order.getId()));
        assertNotNull(managerOrder.findOrderById(order2.getId()));
    }

    @Test
    public void testFindOrderById() throws Exception {
        List<Style> styles = new ArrayList<>();
        styles.add(Style.metal);
        styles.add(Style.rock);
        Band band = newBand("Eufory", styles, Region.slovensko, 200.00, 7.4);
        managerBand.createBand(band);
        
        Customer customer = newCustomer("Pepa", "666 123 456", "Kartouzska 69");
        managerCustomer.createCustomer(customer);
        
        Order order = newOrder(band, customer, "01.02.2016", Region.jihocesky, 2);
        managerOrder.createOrder(order);
        Long orderId = order.getId();
        
        Order result = managerOrder.findOrderById(orderId);
        assertDeepEquals(order, result);
    }

    @Test
    public void testFindAllOrders() throws Exception {
        SimpleDateFormat sfd = new SimpleDateFormat("dd.mm.yyyy");
        
        Collection<Order> allCustomers = managerOrder.findAllOrders();
        
        assertTrue(allCustomers.isEmpty());
        
        Customer cus1 = newCustomer("Igor Kosik", "+421 944 326 548", "Kernova 9");
        managerCustomer.createCustomer(cus1);
        List<Style> styles = new ArrayList<>();
        styles.add(Style.blues);
        styles.add(Style.reggae);
        Band band1 = newBand("A ty citas toto?", styles, Region.karlovarsky, 156.23, 6.1);
        managerBand.createBand(band1);
        
        Order order1 = newOrder(band1, cus1, "01.12.2017", Region.kralovohradecky, 4);
        managerOrder.createOrder(order1);
        
        Customer cus2 = newCustomer("Frantisek Smutny", "236 155 885", "Most SNP 4.stlp");
        managerCustomer.createCustomer(cus2);
        Band band2 = newBand("HEX", styles, Region.slovensko, 421.00, 5.5);
        managerBand.createBand(band2);
        
        Order order2 = newOrder(band2, cus2, "05.06.2016", Region.praha, 3);
        managerOrder.createOrder(order2);
        
        List<Order> sample = new ArrayList<>();
        List<Order> actual = new ArrayList<>();
        
        sample.add(order1);
        sample.add(order2);
        
        allCustomers = managerOrder.findAllOrders();
        actual.addAll(allCustomers);
        
        Collections.sort(sample, idComparator);
        Collections.sort(actual, idComparator);
        
        assertDeepEquals(sample, actual);
        
    }

    @Test
    public void testFindOrdersForBand() throws Exception {
        Customer customer1 = newCustomer("Tomas Oravec","012 345 789", "Brezno 123");
        managerCustomer.createCustomer(customer1);
        List<Style> styles = new ArrayList<>();
        styles.add(Style.dnb);
        styles.add(Style.classical);
        Band band1 = newBand("Ugly band", styles, Region.liberecky, 569.36, 1.1);
        managerBand.createBand(band1);

        Order order1 = newOrder(band1, customer1, "31.02.2019", Region.jihocesky, 6);
        managerOrder.createOrder(order1);

        Customer customer2 = newCustomer("Ondrej Brezovec", "569 123 789", "Zilina 1020");
        managerCustomer.createCustomer(customer2);
        Band band2 = newBand("Nice band", styles, Region.karlovarsky, 222.66, 3.3);
        managerBand.createBand(band2);

        Order order2 = newOrder(band2, customer2, "23.06.2017", Region.praha, 3);
        managerOrder.createOrder(order2);

        Customer customer3 = newCustomer("Mario Hubrak", "566 222 111", "Lucenec 183");
        managerCustomer.createCustomer(customer3);

        order1=managerOrder.findOrderById(order1.getId());

        Order order3 = newOrder(band1, customer3, "22.08.2016", Region.ustecky, 5);
        managerOrder.createOrder(order3);

        List<Order> sample= new ArrayList<>();
        List<Order> actual= new ArrayList<>();

        sample.add(order1);
        sample.add(order3);

        Collection<Order> ordersForBand = managerOrder.findOrdersForBand(band1);
        actual.addAll(ordersForBand);

        Collections.sort(sample,idComparator);
        Collections.sort(actual, idComparator);

        assertDeepEquals(sample,actual);

        // band don't have order
        Band band3 = newBand("Lonely band", styles, Region.olomoucky, 55.33, 2.3);
        managerBand.createBand(band3);
        ordersForBand = managerOrder.findOrdersForBand(band3);
        assertTrue(ordersForBand.isEmpty());

        //consistency
        Order getOrder = managerOrder.findOrderById(order2.getId());
        assertDeepEquals(order2,getOrder);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindOrdersForBandWithWrongArgument() throws Exception {
        Collection<Order> ordersForBand = new ArrayList<>();

        ordersForBand=managerOrder.findOrdersForBand(null);
        
        List<Style> styles = new ArrayList<>();
        styles.add(Style.folk);
        styles.add(Style.disco);
        Band band1 = newBand("I drift off", styles, Region.stredocesky, 222.33, 6.6);
        
        ordersForBand = managerOrder.findOrdersForBand(band1);
    }

    @Test
    public void testFindOrdersForCustomer() throws Exception {
        Customer customer1 = newCustomer("Tomas Oravec","+421 944 222 222", "Brezno 123");
        managerCustomer.createCustomer(customer1);
        
        List<Style> styles = new ArrayList<>();
        styles.add(Style.pop);
        styles.add(Style.jazz);
        Band band1 = newBand("Nemozem povedat", styles, Region.slovensko, 123.45, 6.7);
        managerBand.createBand(band1);

        Order order1 = newOrder(band1, customer1, "11.04.2016", Region.plzensky, 5);
        managerOrder.createOrder(order1);

        Customer customer2 = newCustomer("Ondrej Brezovec","+421 922 222 222", "Zilina 1020");
        managerCustomer.createCustomer(customer2);
        Band band2 = newBand("No name", styles, Region.slovensko, 100.22, 4.6);
        managerBand.createBand(band2);

        Order order2 = newOrder(band2, customer2, "23.07.2017", Region.liberecky, 7);
        managerOrder.createOrder(order2);

        Band band3 = newBand("AC/DC", styles, Region.moravskosliezsky, 45.26, 8.9);
        managerBand.createBand(band3);

        Order order3 = newOrder(band3, customer1, "15.03.2016", Region.pardubicky, 4);
        managerOrder.createOrder(order3);

        List<Order> sample= new ArrayList<>();
        List<Order> actual= new ArrayList<>();

        sample.add(order1);
        sample.add(order3);

        Collection<Order> ordersForCustomer=managerOrder.findOrdersForCustomer(customer1);
        actual.addAll(ordersForCustomer);

        Collections.sort(sample,idComparator);
        Collections.sort(actual, idComparator);

        assertDeepEquals(sample,actual);

        // test with customer who doesn't have order
        Customer customer3 = newCustomer("Andrej Pincik", "568 789 123", "Brezovec 10");
        managerCustomer.createCustomer(customer3);
        ordersForCustomer=managerOrder.findOrdersForCustomer(customer3);
        assertTrue(ordersForCustomer.isEmpty());

        //consistency
        Order getOrder = managerOrder.findOrderById(order2.getId());
        assertDeepEquals(order2,getOrder);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testFindOrdersForCustomerWithWrongArgument() throws Exception {
        Collection<Order> ordersForCustomer = new ArrayList<>();    
        
        ordersForCustomer = managerOrder.findOrdersForCustomer(null);
            
        //customer isn't in DB
        Customer customer2 = newCustomer("Tomas Mician","065 482 266", "Cadca 123");
        ordersForCustomer = managerOrder.findOrdersForCustomer(customer2); 
    }
    
    /**
     * 
     * @param name
     * @param styles
     * @param region
     * @param price
     * @param rate
     * @return 
     */
    private static Band newBand(String name, List<Style> styles, Region region, Double price, Double rate) {
        Band band = new Band();
        band.setBandName(name);
        band.setStyles(styles);
        band.setRegion(region);
        band.setPricePerHour(price);
        band.setRate(rate);
        
        return band;
    }
    
    public Customer newCustomer(String name, String phoneNumber, String address){
        Customer customer = new Customer();
        customer.setName(name);
        customer.setPhoneNumber(phoneNumber);
        customer.setAddress(address);

        return customer;
    }

    private Order newOrder(Band band, Customer customer, String sDate, Region region, int duration) throws ParseException {
        Order order = new Order();
        order.setBand(band);
        order.setCustomer(customer);
        
        DateFormat format = new SimpleDateFormat("dd.mm.yyyy");
        Date date = format.parse(sDate);
        order.setDate(date);
        
        order.setPlace(region);
        order.setDuration(duration);
        return order;
    }

    private void assertDeepEquals(Order expected, Order actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBand().getId(), actual.getBand().getId());
        assertEquals(expected.getCustomer().getId(), actual.getCustomer().getId());
        assertEquals(expected.getDate(), actual.getDate());
        assertEquals(expected.getPlace(), actual.getPlace());
        assertEquals(expected.getDuration(), actual.getDuration());
    }
    
    private void assertDeepNotEquals(Order expected, Order actual) {
        boolean res = true;
        
        if(!Objects.equals(expected.getId(), actual.getId())) res = false;
        if(!Objects.equals(expected.getBand().getId(), actual.getBand().getId())) res = false;
        if(!Objects.equals(expected.getCustomer().getId(), actual.getCustomer().getId())) res = false;
        if(!expected.getDate().equals(actual.getDate())) res = false;
        if(!expected.getPlace().equals(actual.getPlace())) res = false;
        if(expected.getDuration() != expected.getDuration()) res = false;
        
        assertFalse(res);
    }

    private void assertDeepEquals(List<Order> sample, List<Order> actual) {
        for(int i = 0; i < sample.size(); i++) {
            Order first = sample.get(i);
            Order second = actual.get(i);
            assertDeepEquals(first, second);
        }
    }
    
    private static Comparator<Order> idComparator = new Comparator<Order>() {

        @Override
        public int compare(Order o1, Order o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };
}