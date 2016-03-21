package cz.muni.fi.pv168.bandsproject;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.*;

/**
 *
 * @author Tomáš
 */
public class CustomerManagerImplTest {

    private CustomerManagerImpl manager;
    private DataSource dataSource;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws SQLException {
        dataSource = prepareDataSource();
        try (Connection connection = dataSource.getConnection()) {
            connection.prepareStatement("CREATE TABLE CUSTOMER ("
                    + "id bigint primary key generated always as identity,"
                    + "name String,"
                    + "phoneNumber String,"
                    + "adress String)").executeUpdate();
        }
        manager = new CustomerManagerImpl(dataSource);
    }

    private static DataSource prepareDataSource() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:customermgr-test");
        ds.setCreateDatabase("create");
        return ds;
    }

    @After
    public void tearDown() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.prepareStatement("DROP TABLE CUSTOMER").executeUpdate();
        }
    }

    @Test
    public void createCustomer() {
        Customer customer = newCustomer("Pepa", "666 123 456", "Kartouzská 69");
        manager.createCustomer(customer);

        Long customerId = customer.getId();
        assertThat("saved customer has null ID", customer.getId(), is(not(equalTo(null))));

        Customer result = manager.getCustomer(customerId);
        assertThat("loaded user is different from the new one", result, is(equalTo(customer)));
        assertThat("loaded user is the same instance", result, is(not(sameInstance(customer))));

        assertDeepEquals(customer, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNull() throws Exception {
        manager.createCustomer(null);
    }

    @Test
    public void createCustomerWithWrongValues() {
        Customer customer = newCustomer("Pepa", "666 123 456", "Kartouzská 69");

        customer.setId(1L);
        try {
            manager.createCustomer(customer);
            fail("should refuse assigned id");
        } catch (IllegalArgumentException ex) {
            //OK
        }

        customer = newCustomer(null, "666 123 456", "Kartouzská 69");
        try {
            manager.createCustomer(customer);
            fail("no name of customer detected");
        } catch (IllegalArgumentException ex) {
            //OK
        }

        customer = newCustomer("Pepa", null, "Kartouzská 69");
        try {
            manager.createCustomer(customer);
            fail("no phone number of customer detected");
        } catch (IllegalArgumentException ex) {
            //OK
        }

        customer = newCustomer("Pepa", "666 123 456", null);
        try {
            manager.createCustomer(customer);
            fail("no address of customer detected");
        } catch (IllegalArgumentException ex) {
            //OK
        }
    }



    @Test
    public void updateCustomer() {
        Customer customer = newCustomer("Pepa", "666 123 456", "Kartouzská 69");
        Customer customer2 = newCustomer("Alojz", "158 155 150", "Route 66");
        manager.createCustomer(customer);
        manager.createCustomer(customer2);
        Long customerId = customer.getId();

        customer.setName("Jozef");
        manager.updateCustomer(customer);
        //load from database
        customer = manager.getCustomer(customerId);
        //new style assertions
        assertThat("name was not changed", customer.getName(), is(equalTo("Jozef")));
        assertThat("phone number was changed when changing name", customer.getPhoneNumber(), is(equalTo("666 123 456")));
        assertThat("address was changed when changing name", customer.getAddress(), is(equalTo("Kartouzská 69")));

        //change row value to 0
        customer.setPhoneNumber("000 111 222");
        manager.updateCustomer(customer);
        //load from database
        customer = manager.getCustomer(customerId);
        //old style assertions
        assertEquals("Jozef", customer.getName());
        assertEquals("000 111 222", customer.getPhoneNumber());
        assertEquals(6, customer.getAddress());

        customer.setAddress("Kartůzská 69");
        manager.updateCustomer(customer);
        customer = manager.getCustomer(customerId);
        assertEquals("Jozef", customer.getName());
        assertEquals("000 111 222", customer.getPhoneNumber());
        assertEquals("Kartůzská 69", customer.getAddress());

        // Check if updates didn't affected other records
        assertDeepEquals(customer2, manager.getCustomer(customer2.getId()));
    }

    @Test
    public void updateCustomerWithWrongAttributes() {

        Customer customer = newCustomer("Pepa", "666 123 456", "Kartouzská 69");
        manager.createCustomer(customer);
        Long customerId = customer.getId();

        try {
            manager.updateCustomer(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            customer = manager.getCustomer(customerId);
            customer.setId(null);
            manager.updateCustomer(customer);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            customer = manager.getCustomer(customerId);
            customer.setId(customerId - 1);
            manager.updateCustomer(customer);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            customer = manager.getCustomer(customerId);
            customer.setName("");
            manager.updateCustomer(customer);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            customer = manager.getCustomer(customerId);
            customer.setPhoneNumber("");
            manager.updateCustomer(customer);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            customer = manager.getCustomer(customerId);
            customer.setAddress("");
            manager.updateCustomer(customer);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
    }

    @Test
    public void deleteCustomer() {

        Customer customer = newCustomer("Pepa", "666 123 456", "Kartouzská 69");
        Customer customer2 = newCustomer("Alojz", "158 155 150", "Route 66");
        manager.createCustomer(customer);
        manager.createCustomer(customer2);

        assertNotNull(manager.getCustomer(customer.getId()));
        assertNotNull(manager.getCustomer(customer2.getId()));

        manager.deleteCustomer(customer);

        assertNull(manager.getCustomer(customer.getId()));
        assertNotNull(manager.getCustomer(customer2.getId()));
    }

    @Test
    public void deleteCustomerWithWrongAttributes() {

        Customer customer = newCustomer("Pepa", "666 123 456", "Kartouzská 69");

        try {
            manager.deleteCustomer(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            customer.setId(null);
            manager.deleteCustomer(customer);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            customer.setId(1L);
            manager.deleteCustomer(customer);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

    }

    public Customer newCustomer(String name, String phoneNumber, String address){
        Customer customer = new Customer();
        customer.setName(name);
        customer.setPhoneNumber(phoneNumber);
        customer.setAddress(address);

        return customer;
    }

    private void assertDeepEquals(Customer expected, Customer actual){
        assertEquals("id is not equal", expected.getId(), actual.getId());
        assertEquals("name is not equal", expected.getName(), actual.getName());
        assertEquals("phone number is not equal", expected.getPhoneNumber(), actual.getPhoneNumber());
        assertEquals("address is not equal", expected.getAddress(), actual.getAddress());
    }
}