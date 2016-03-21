package cz.muni.fi.pv168.bandsproject;

import javax.sql.DataSource;
import java.sql.*;

/**
 * Created by Lenka on 9.3.2016.
 */
public class CustomerManagerImpl implements CustomerManager {
    private final DataSource dataSource;

    public CustomerManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createCustomer(Customer customer) throws ServiceFailureException {
        validate(customer);
        if (customer.getId() != null) {
            throw new IllegalArgumentException("customer id is already set");
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement st = connection.prepareStatement(
                     "INSERT INTO CUSTOMER (name,phoneNumber,adress) VALUES (?,?,?)",
                     Statement.RETURN_GENERATED_KEYS)) {

            st.setString(1, customer.getName());
            st.setString(2, customer.getPhoneNumber());
            st.setString(3, customer.getAddress());
            int addedRows = st.executeUpdate();
            if (addedRows != 1) {
                throw new ServiceFailureException("Internal Error: More rows ("
                        + addedRows + ") inserted when trying to insert customer " + customer);
            }

            ResultSet keyRS = st.getGeneratedKeys();
            customer.setId(getKey(keyRS, customer));
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when inserting customer " + customer, ex);
        }
    }

    @Override
    public void updateCustomer(Customer customer) throws ServiceFailureException {
        validate(customer);
        if(customer.getId() == null) {
            throw new IllegalArgumentException("customer id is null");
        }
        try (Connection connection = dataSource.getConnection();
             PreparedStatement st = connection.prepareStatement(
                     "UPDATE CUSTOMER SET name = ?,phoneNumber = ?,adress = ? WHERE id = ?")){
            st.setString(1, customer.getName());
            st.setString(2, customer.getPhoneNumber());
            st.setString(3, customer.getAddress());
            int count = st.executeUpdate();
            if(count == 0) {
                throw new EntityNotFoundException("Customer " + customer + " was not found in database!");
            } else if(count != 1) {
                throw new ServiceFailureException("Invalid updated rows count detected "
                        + "(one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when updating customer " + customer, ex);
        }
    }

    @Override
    public void deleteCustomer(Customer customer) throws ServiceFailureException {
        if (customer == null) {
            throw new IllegalArgumentException("customer is null");
        }
        if (customer.getId() == null) {
            throw new IllegalArgumentException("customer id is null");
        }
        try (Connection connection = dataSource.getConnection();
             PreparedStatement st = connection.prepareStatement(
                     "DELETE FROM CUSTOMER WHERE id = ?")) {

            st.setLong(1, customer.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException("Customer " + customer + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid deleted rows count detected "
                        + "(one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when deleting customer " + customer, ex);
        }
    }

    @Override
    public Customer getCustomer(Long id)  throws ServiceFailureException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement st = connection.prepareStatement(
                     "SELECT id,name,phoneNumber,adress FROM CUSTOMER WHERE id = ?")) {
            st.setLong(1, id);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                Customer customer = resultSetToCustomer(rs);
                if (rs.next()) {
                    throw new ServiceFailureException(
                            "Internal error: More entities with the same id found "
                                    + "(source id: " + id + ", found " + customer + " and " + resultSetToCustomer(rs));
                }
                return customer;
            } else {
                return null;
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving customer with id " + id, ex);
        }
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

    /**
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    private Customer resultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getLong("id"));
        customer.setName(rs.getString("name"));
        customer.setPhoneNumber(rs.getString("phoneNumber"));
        customer.setAddress(rs.getString("adress"));
        return customer;
    }

    /**
     *
     * @param keyRS
     * @param customer
     * @return
     * @throws ServiceFailureException
     * @throws SQLException
     */
    private Long getKey(ResultSet keyRS, Customer customer) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert customer " + customer
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert customer " + customer
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retriving failed when trying to insert customer " + customer
                    + " - no key found");
        }
    }
}
