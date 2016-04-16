package cz.muni.fi.pv168.bandsproject;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * Created by Lenka on 9.3.2016.
 */
public class OrderManagerImpl implements OrderManager{
    private final DataSource dataSource;
    private JdbcTemplate jdbcTemplateObject;

    public OrderManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override 
    public void createOrder(Order order) throws ServiceFailureException {
        validate(order);
        if (order.getId() != null) {
            throw new IllegalArgumentException("band id is already set");
        }
        String SQL = "INSERT INTO ORDER (customer,band,date,place,duration) VALUES (?,?,?,?,?)";
        jdbcTemplateObject.update(SQL, order.getCustomer().toString(), order.getBand().toString(), order.getDate().toString(),
        order.getPlace().ordinal(), order.getDuration());
    }

    @Override
    public void updateOrder(Order order) throws ServiceFailureException {
        validate(order);
        if(order.getId() == null) {
            throw new IllegalArgumentException("band id is null");
        }
        String SQL = "UPDATE ORDER SET customer = ?,band = ?,date = ?,place = ?,duration = ? WHERE id = ?";
        jdbcTemplateObject.update(SQL, order.getCustomer().toString(), order.getBand().toString(), order.getDate().toString(),
        order.getPlace().ordinal(), order.getDuration(), order.getId());
    }

    @Override
    public void deleteOrder(Order order) throws ServiceFailureException {
        if (order == null) {
            throw new IllegalArgumentException("order is null");
        }
        if (order.getId() == null) {
            throw new IllegalArgumentException("order id is null");
        }
        String SQL = "DELETE FROM ORDER WHERE id = ?";
        jdbcTemplateObject.update(SQL, order.getId()); //UPDATE??
    }

    @Override
    public Order findOrderById(Long id) {
        String SQL = "SELECT * FROM ORDER WHERE id = ?";
        Order order = jdbcTemplateObject.queryForObject(SQL, new Object[]{id}, new OrderMapper());
        return order;
    }

    @Override
    public List<Order> findAllOrders() {
        String SQL = "select * from ORDER";
        List <Order> orders = jdbcTemplateObject.query(SQL, new OrderMapper());
        return orders;
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
    
    public class OrderMapper implements RowMapper<Order> {
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            Order order = new Order();
            order.setId(rs.getLong("id"));
            order.setCustomer(new CustomerManagerImpl(dataSource).getCustomer(rs.getLong("idCustomer")));
            order.setBand(new BandManagerImpl(dataSource).findBandById(rs.getLong("idBand")));
            order.setDate(rs.getDate("date"));
            order.setPlace(Region.values()[rs.getInt("region")]);
            order.setDuration(rs.getInt("duration"));
            return order;
        }
    }
}
