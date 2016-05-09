package cz.muni.fi.pv168.bandsproject;

import static cz.muni.fi.pv168.bandsproject.BandManagerImpl.log;
import javax.sql.DataSource;
import java.sql.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

/**
 * Created by Lenka on 9.3.2016.
 */
public class CustomerManagerImpl implements CustomerManager {
    private final DataSource dataSource;
    private JdbcTemplate jdbcTemplateObject;
    
    final static Logger log = LoggerFactory.getLogger(MainGUI.class);

    public CustomerManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplateObject = new JdbcTemplate(dataSource);
    }

    @Override
    public void createCustomer(Customer customer) throws ServiceFailureException {
        validate(customer);
        if (customer.getId() != null) {
            log.error("Customer ID is already set ("+customer.toString()+")");
            throw new IllegalArgumentException("customer id is already set");
        }

        SimpleJdbcInsert insertCustomer = new SimpleJdbcInsert(jdbcTemplateObject).withTableName("customer").usingGeneratedKeyColumns("id");
        Map<String, Object> parameters = new HashMap<>(2);
        parameters.put("name", customer.getName());
        parameters.put("phoneNumber", customer.getPhoneNumber());
        parameters.put("address", customer.getAddress());
        Number id = insertCustomer.executeAndReturnKey(parameters);
        log.info("Customer created ("+customer.toString()+")");
        customer.setId(id.longValue());
    }

    @Override
    public void updateCustomer(Customer customer) throws ServiceFailureException {
        validate(customer);
        if(customer.getId() == null) {
            log.error("Customer ID is NULL ("+customer.toString()+")");
            throw new IllegalArgumentException("customer id is null");
        }
        String SQL = "UPDATE CUSTOMER SET name = ?,phoneNumber = ?,address = ? WHERE id = ?";
        jdbcTemplateObject.update(SQL, customer.getName(), customer.getPhoneNumber(), customer.getAddress(), customer.getId());
        log.info("Customer updated SQL ("+SQL+")");
    }

    @Override
    public void deleteCustomer(Customer customer) throws ServiceFailureException {
        if (customer == null) {
            log.error("Customer is NULL ("+customer.toString()+")");
            throw new IllegalArgumentException("customer is null");
        }
        if (customer.getId() == null) {
            log.error("Customer ID is NULL ("+customer.toString()+")");
            throw new IllegalArgumentException("customer id is null");
        }
        jdbcTemplateObject.update("DELETE FROM CUSTOMER WHERE id = ?", customer.getId());
        log.info("Customer deleted ("+customer.toString()+")");
    }
    
    @Override
    public List<Customer> getAllCustomers() {
        try {
            List<Customer> customers = jdbcTemplateObject.query("select * from customer", customerMapper);
            return customers;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Customer getCustomer(Long id) {
        try {
            Customer customer = jdbcTemplateObject.queryForObject("SELECT * FROM CUSTOMER WHERE id = ?", customerMapper, id);
            return customer;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     *
     * @param customer
     * @throws IllegalArgumentException
     */
    private void validate(Customer customer) throws IllegalArgumentException {
        if (customer == null) {
            log.error("Customer is NULL ("+customer.toString()+")");
            throw new IllegalArgumentException("customer is null");
        }
        if (customer.getName() == null) {
            log.error("Customer name is NULL ("+customer.toString()+")");
            throw new IllegalArgumentException("customer name is null");
        }
        if(customer.getPhoneNumber() == null) {
            log.error("Customer number is NULL ("+customer.toString()+")");
            throw new IllegalArgumentException("customer phone number is null");
        }
        if(customer.getAddress() == null) {
            log.error("Customer address is NULL ("+customer.toString()+")");
            throw new IllegalArgumentException("customer adress is null");
        }
    }

    private RowMapper<Customer> customerMapper = new RowMapper<Customer>() {
        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            Customer customer = new Customer();
            customer.setId(rs.getLong("id"));
            customer.setName(rs.getString("name"));
            customer.setPhoneNumber(rs.getString("phoneNumber"));
            customer.setAddress(rs.getString("address"));
            return customer;
        }
    };
}
