package cz.muni.fi.pv168.bandsproject;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

/**
 * Created by Lenka on 9.3.2016.
 */
public class OrderManagerImpl implements OrderManager{
    private final DataSource dataSource;

    public OrderManagerImpl(DataSource dataSource) {
        //// TODO
        this.dataSource = dataSource;
    }

    @Override 
    public void createOrder(Order order) throws ServiceFailureException {
        validate(order);
        if (order.getId() != null) {
            throw new IllegalArgumentException("band id is already set");
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement st = connection.prepareStatement(
                     "INSERT INTO ORDER (customer,band,date,place,duration) VALUES (?,?,?,?,?)",
                     Statement.RETURN_GENERATED_KEYS)) {

            st.setString(1, order.getCustomer().toString());
            st.setString(2, order.getBand().toString());
            st.setString(3, order.getDate().toString());
            st.setInt(4, order.getPlace().ordinal());
            st.setInt(5, order.getDuration());
            int addedRows = st.executeUpdate();
            if (addedRows != 1) {
                throw new ServiceFailureException("Internal Error: More rows ("
                        + addedRows + ") inserted when trying to insert order " + order);
            }

            ResultSet keyRS = st.getGeneratedKeys();
            order.setId(getKey(keyRS, order));
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when inserting order " + order, ex);
        }
    }

    @Override
    public void updateOrder(Order order) throws ServiceFailureException {
        validate(order);
        if(order.getId() == null) {
            throw new IllegalArgumentException("band id is null");
        }
        try (Connection connection = dataSource.getConnection();
             PreparedStatement st = connection.prepareStatement(
                     "UPDATE ORDER SET customer = ?,band = ?,date = ?,place = ?,duration = ? WHERE id = ?")){
            st.setString(1, order.getCustomer().toString());
            st.setString(2, order.getBand().toString());
            st.setString(3, order.getDate().toString());
            st.setInt(4, order.getPlace().ordinal());
            st.setInt(5, order.getDuration());
            st.setLong(6, order.getId());

            int count = st.executeUpdate();
            if(count == 0) {
                throw new EntityNotFoundException("Order " + order + " was not found in database!");
            } else if(count != 1) {
                throw new ServiceFailureException("Invalid updated rows count detected "
                        + "(one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when updating order " + order, ex);
        }
    }

    @Override
    public void deleteOrder(Order order) throws ServiceFailureException {
        if (order == null) {
            throw new IllegalArgumentException("order is null");
        }
        if (order.getId() == null) {
            throw new IllegalArgumentException("order id is null");
        }
        try (Connection connection = dataSource.getConnection();
             PreparedStatement st = connection.prepareStatement(
                     "DELETE FROM ORDER WHERE id = ?")) {

            st.setLong(1, order.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException("Order " + order + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid deleted rows count detected "
                        + "(one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when updating order " + order, ex);
        }
    }

    @Override //// TODO
    public Order findOrderById(Long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override //// TODO
    public List<Order> findAllOrders() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override //// TODO
    public List<Order> findOrdersForBand(Band band) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override //// TODO
    public List<Order> findOrdersForCustomer(Customer customer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param order
     * @throws IllegalArgumentException
     */
    private void validate(Order order) throws IllegalArgumentException {
        if (order == null) {
            throw new IllegalArgumentException("order is null");
        }
        if (order.getCustomer() == null) {
            throw new IllegalArgumentException("customer in order is null");
        }
        if (order.getBand() == null) {
            throw new IllegalArgumentException("band in order is null");
        }
        if (order.getDate() == null) {
            throw new IllegalArgumentException("date in order is null");
        }
        if (order.getPlace() == null) {
            throw new IllegalArgumentException("place is null");
        }
        if (order.getDuration() <= 0) {
            throw new IllegalArgumentException("duration is zero or negative");
        }
    }

    /**
     *
     * @param rs
     * @return
     * @throws SQLException
     */ 
    private Order resultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setCustomer(new CustomerManagerImpl(dataSource).getCustomer(rs.getLong("idCustomer")));
        order.setBand(new BandManagerImpl(dataSource).findBandById(rs.getLong("idBand")));
        order.setDate(rs.getDate("date"));
        order.setPlace(Region.values()[rs.getInt("region")]);
        order.setDuration(rs.getInt("duration"));
        return order;
    }

    /**
     *
     * @param keyRS
     * @param order
     * @return
     * @throws ServiceFailureException
     * @throws SQLException
     */
    private Long getKey(ResultSet keyRS, Order order) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert order " + order
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert order " + order
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retriving failed when trying to insert order " + order
                    + " - no key found");
        }
    }
}
