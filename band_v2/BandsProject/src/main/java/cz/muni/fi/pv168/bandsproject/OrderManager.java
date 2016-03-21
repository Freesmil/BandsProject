package cz.muni.fi.pv168.bandsproject;

import java.util.List;

/**
 * Created by Lenka on 9.3.2016.
 */
public interface OrderManager {

    /**
     *
     * @param order
     */
    public void createOrder(Order order);

    /**
     *
     * @param order
     */
    public void updateOrder(Order order);

    public void deleteOrder(Order order);

    public Order findOrderById(Long id);

    public List<Order> findAllOrders();

    public List<Order> findOrdersForCustomer(Customer customer);

    public List<Order> findOrdersForBand(Band band);
}
