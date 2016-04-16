package cz.muni.fi.pv168.bandsproject;

import javax.sql.DataSource;
import java.sql.*;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * Created by Lenka on 9.3.2016.
 */
public class CustomerManagerImpl implements CustomerManager {
    private final DataSource dataSource;
    private JdbcTemplate jdbcTemplateObject;

    public CustomerManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createCustomer(Customer customer) throws ServiceFailureException {
        validate(customer);
        if (customer.getId() != null) {
            throw new IllegalArgumentException("customer id is already set");
        }

        String SQL = "INSERT INTO CUSTOMER (name,phoneNumber,adress) VALUES (?,?,?)";
        jdbcTemplateObject.update(SQL, customer.getName(), customer.getPhoneNumber(), customer.getAddress());
    }

    @Override
    public void updateCustomer(Customer customer) throws ServiceFailureException {
        validate(customer);
        if(customer.getId() == null) {
            throw new IllegalArgumentException("customer id is null");
        }
        
        String SQL = "UPDATE CUSTOMER SET name = ?,phoneNumber = ?,adress = ? WHERE id = ?";
        jdbcTemplateObject.update(SQL, customer.getName(), customer.getPhoneNumber(), customer.getAddress());
    }

    @Override
    public void deleteCustomer(Customer customer) throws ServiceFailureException {
        if (customer == null) {
            throw new IllegalArgumentException("customer is null");
        }
        if (customer.getId() == null) {
            throw new IllegalArgumentException("customer id is null");
        }
        
        String SQL = "DELETE FROM CUSTOMER WHERE id = ?";
        jdbcTemplateObject.update(SQL, customer.getId()); //UPDATE??
    }
    
    @Override
    public List<Customer> getAllCustomers() {
        String SQL = "select * from CUSTOMER";
        List<Customer> customers = jdbcTemplateObject.query(SQL, new CustomerMapper());
        return customers;
    }

    @Override
    public Customer getCustomer(Long id)  throws ServiceFailureException {
        String SQL = "SELECT * FROM CUSTOMER WHERE id = ?";
        Customer customer = jdbcTemplateObject.queryForObject(SQL, new Object[]{id}, new CustomerMapper());
        return customer;
    }

    /**
     *
     * @param customer
     * @throws IllegalArgumentException
     */
    private void validate(Customer customer) throws IllegalArgumentException {
        if (customer == null) {
            throw new IllegalArgumentException("customer is null");
        }
        if (customer.getName() == null) {
            throw new IllegalArgumentException("customer name is null");
        }
        if(customer.getPhoneNumber() == null) {
            throw new IllegalArgumentException("customer phone number is null");
        }
        if(customer.getAddress() == null) {
            throw new IllegalArgumentException("customer adress is null");
        }
    }

    private class CustomerMapper implements RowMapper<Customer>{
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            Customer customer = new Customer();
            customer.setId(rs.getLong("id"));
            customer.setName(rs.getString("name"));
            customer.setPhoneNumber(rs.getString("phoneNumber"));
            customer.setAddress(rs.getString("adress"));
            return customer;
        }
    }
}
