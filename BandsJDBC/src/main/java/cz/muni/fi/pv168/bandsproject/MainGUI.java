/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.bandsproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author Tom�
 */

public class MainGUI extends javax.swing.JFrame {
    
    final static Logger log = LoggerFactory.getLogger(MainGUI.class);
    
    @Autowired
    private CustomerManager customerManager;
    
    @Autowired
    private BandManager bandManager;
    
    @Autowired
    private LeaseManager leaseManager;
    
    private List<Band> bands;
    private List<Customer> customers;
    private List<Lease> orders;
    
    // SwingWorker
    private FindAllBands findAllBands;
    private FindAllCustomers findAllCustomers;
    private FindAllOrders findAllOrders;
    
    
    /**
     * Creates new form MainGUI
     */
    public MainGUI() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(MySpringConfig.class);
        bandManager = ctx.getBean(BandManager.class);
        customerManager = ctx.getBean(CustomerManager.class);
        leaseManager = ctx.getBean(LeaseManager.class);
        /*
        bands = bandManager.getAllBands();
        customers = customerManager.getAllCustomers();
        orders = leaseManager.findAllLeases();
        */
        
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        findAllBands = new FindAllBands();
        findAllBands.execute();
        findAllCustomers = new FindAllCustomers();
        findAllCustomers.execute();
        findAllOrders = new FindAllOrders();
        findAllOrders.execute();
        
        initComponents();
        setTables();
        
        contentPanel.removeAll();
        contentPanel.add(firstContent);
        contentPanel.repaint();
        contentPanel.revalidate();
        
        DefaultComboBoxModel dcmBand = new DefaultComboBoxModel();
        bands.stream().forEach((band) -> {
            dcmBand.addElement(band.getName());
        });
        orderBandSelect.setModel(dcmBand);
        
        DefaultComboBoxModel dcmCustomer = new DefaultComboBoxModel();
        customers.stream().forEach((customer) -> {
            dcmCustomer.addElement(customer.getName());
        });
        orderCustomerSelect.setModel(dcmCustomer);
    }
    
    private class FindAllBands extends SwingWorker<List<Band>, Integer> {
        
        @Override
        protected List<Band> doInBackground() throws Exception {
            return bandManager.getAllBands();
        }

        @Override
        protected void done() {
            try{
                log.debug("Getting all bands");
                bands = get();
            }catch(ExecutionException ex) {
                log.error("Exception was thrown in FindAllBands in method doInBackGround " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("Method doInBackground has been interrupted in FindAllBands " + ex.getCause());
                throw new RuntimeException("Operation interrupted in FindAllBands");
            }
        }
    }
    
    private class FindAllCustomers extends SwingWorker<List<Customer>, Integer> {
        
        @Override
        protected List<Customer> doInBackground() throws Exception {
            return customerManager.getAllCustomers();
        }

        @Override
        protected void done() {
            try{
                log.debug("Getting all customers.");
                customers = get();
            }catch(ExecutionException ex) {
                log.error("Exception was thrown in FindAllCustomers in method doInBackGround " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("Method doInBackground has been interrupted in FindAllCustomers " + ex.getCause());
                throw new RuntimeException("Operation interrupted in FindAllCustomers");
            }
        }
    }
    
    private class FindAllOrders extends SwingWorker<List<Lease>, Integer> {
        
        @Override
        protected List<Lease> doInBackground() throws Exception {
            return leaseManager.findAllLeases();
        }

        @Override
        protected void done() {
            try{
                log.debug("Getting all leases.");
                orders = get();
            }catch(ExecutionException ex) {
                log.error("Exception was thrown in FindAllOrders in method doInBackGround " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("Method doInBackground has been interrupted in FindAllOrders " + ex.getCause());
                throw new RuntimeException("Operation interrupted in FindAllOrders");
            }
        }
    }
    
    private void setTables() {
        DefaultTableModel customerModel = new DefaultTableModel(new String[]{"Id", "Name", "Number", "Address"}, 0);
        
        customers.stream().forEach((customer) -> {
            String id = customer.getId().toString();
            String name = customer.getName();
            String address = customer.getAddress();
            String number = customer.getPhoneNumber();
            customerModel.addRow(new Object[]{id, name, address, number});
        });
        
        customerTable.setModel(customerModel);
        
        
        DefaultTableModel bandModel = new DefaultTableModel(new String[]{"Id", "Name", "Region", "Styles", "Price", "Rate"}, 0);
        
        bands.stream().forEach((band) -> {
            String id = band.getId().toString();
            String name = band.getName();
            String region = band.getRegion().toString();
            String styles = band.getStyles().toString();
            String price = band.getPricePerHour().toString();
            String rate = band.getRate().toString();
            bandModel.addRow(new Object[]{id, name, region, styles, price, rate});
        });
        
        bandTable.setModel(bandModel);
        
        
        DefaultTableModel leaseModel = new DefaultTableModel(new String[]{"Id", "Band id", "Customer id", "Date", "Region", "Duration"}, 0);
        
        orders.stream().forEach((lease) -> {
            String id = lease.getId().toString();
            String bandId = lease.getBand().getId().toString();
            String customerId = lease.getCustomer().getId().toString();
            String date = lease.getDate().toString();
            String region = lease.getPlace().toString();
            String duration = Integer.toString(lease.getDuration());
            leaseModel.addRow(new Object[]{id, bandId, customerId, date, region, duration});
        });
        
        orderTable.setModel(leaseModel);
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        bandsButton = new javax.swing.JButton();
        customersButton = new javax.swing.JButton();
        ordersButton = new javax.swing.JButton();
        contentPanel = new javax.swing.JPanel();
        listBand = new javax.swing.JPanel();
        bandListLabel = new javax.swing.JLabel();
        bandTableScroll = new javax.swing.JScrollPane();
        bandTable = new javax.swing.JTable();
        addBandButton = new javax.swing.JButton();
        deleteBandButton = new javax.swing.JButton();
        firstContent = new javax.swing.JPanel();
        listCustomer = new javax.swing.JPanel();
        customerListLabel = new javax.swing.JLabel();
        customerTableScroll = new javax.swing.JScrollPane();
        customerTable = new javax.swing.JTable();
        addCustomerButton = new javax.swing.JButton();
        deleteCustomerButton = new javax.swing.JButton();
        listOrders = new javax.swing.JPanel();
        orderListLabel = new javax.swing.JLabel();
        orderTableScroll = new javax.swing.JScrollPane();
        orderTable = new javax.swing.JTable();
        addOrderButton = new javax.swing.JButton();
        deleteOrderButton = new javax.swing.JButton();
        createOrder = new javax.swing.JPanel();
        createOrderLabel = new javax.swing.JLabel();
        orderBandSelect = new javax.swing.JComboBox<>();
        orderBandLabel = new javax.swing.JLabel();
        orderCustomerLabel = new javax.swing.JLabel();
        orderCustomerSelect = new javax.swing.JComboBox<>();
        orderDurationSelect = new javax.swing.JSpinner();
        orderDurationLabel = new javax.swing.JLabel();
        orderRegionSelect = new javax.swing.JComboBox<>();
        orderRegionLabel = new javax.swing.JLabel();
        orderDateLabel = new javax.swing.JLabel();
        orderDateSelect = new javax.swing.JSpinner();
        createOrderButton = new javax.swing.JButton();
        createCustomer = new javax.swing.JPanel();
        createCustomerLabel = new javax.swing.JLabel();
        customerNameLabel = new javax.swing.JLabel();
        customerAddressText = new javax.swing.JTextField();
        customerAddressLabel = new javax.swing.JLabel();
        customerPhoneText = new javax.swing.JTextField();
        customerPhoneLabel = new javax.swing.JLabel();
        customerNameText = new javax.swing.JTextField();
        createCustomerButton = new javax.swing.JButton();
        createBand = new javax.swing.JPanel();
        createBandLabel = new javax.swing.JLabel();
        bandNameText = new javax.swing.JTextField();
        bandNameLabel = new javax.swing.JLabel();
        bandRegionSelect = new javax.swing.JComboBox<>();
        bandRegionLabel = new javax.swing.JLabel();
        bandStylesScroll = new javax.swing.JScrollPane();
        bandStylesSelect = new javax.swing.JList<>();
        bandStylesLabel = new javax.swing.JLabel();
        bandPriceText = new javax.swing.JTextField();
        bandPriceLabel = new javax.swing.JLabel();
        createBandButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        mainPanel.setBackground(new java.awt.Color(0, 51, 102));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("cz/muni/fi/pv168/bandsproject/Bundle"); // NOI18N
        jLabel1.setText(bundle.getString("MainGUI.jLabel1.text")); // NOI18N

        bandsButton.setText(bundle.getString("MainGUI.bandsButton.text")); // NOI18N
        bandsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bandsButtonActionPerformed(evt);
            }
        });

        customersButton.setText(bundle.getString("MainGUI.customersButton.text")); // NOI18N
        customersButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customersButtonActionPerformed(evt);
            }
        });

        ordersButton.setText(bundle.getString("MainGUI.ordersButton.text")); // NOI18N
        ordersButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ordersButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jLabel1)
                .addGap(78, 78, 78)
                .addComponent(bandsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(customersButton, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(ordersButton, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(331, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(bandsButton)
                    .addComponent(customersButton)
                    .addComponent(ordersButton))
                .addContainerGap(34, Short.MAX_VALUE))
        );

        getContentPane().add(mainPanel, java.awt.BorderLayout.PAGE_START);

        contentPanel.setLayout(new java.awt.CardLayout());

        bandListLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        bandListLabel.setText(bundle.getString("MainGUI.bandListLabel.text")); // NOI18N

        bandTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Id", "Name", "Region", "Style", "Price per hour", "Rate"
            }
        ));
        bandTableScroll.setViewportView(bandTable);

        addBandButton.setText(bundle.getString("MainGUI.addBandButton.text")); // NOI18N
        addBandButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBandButtonActionPerformed(evt);
            }
        });

        deleteBandButton.setText(bundle.getString("MainGUI.deleteBandButton.text")); // NOI18N
        deleteBandButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteBandButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout listBandLayout = new javax.swing.GroupLayout(listBand);
        listBand.setLayout(listBandLayout);
        listBandLayout.setHorizontalGroup(
            listBandLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(listBandLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(listBandLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bandListLabel)
                    .addGroup(listBandLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(listBandLayout.createSequentialGroup()
                            .addComponent(addBandButton)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(deleteBandButton))
                        .addComponent(bandTableScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 882, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        listBandLayout.setVerticalGroup(
            listBandLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(listBandLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bandListLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bandTableScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(listBandLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addBandButton)
                    .addComponent(deleteBandButton))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        contentPanel.add(listBand, "card2");

        javax.swing.GroupLayout firstContentLayout = new javax.swing.GroupLayout(firstContent);
        firstContent.setLayout(firstContentLayout);
        firstContentLayout.setHorizontalGroup(
            firstContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 924, Short.MAX_VALUE)
        );
        firstContentLayout.setVerticalGroup(
            firstContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 459, Short.MAX_VALUE)
        );

        contentPanel.add(firstContent, "card5");

        customerListLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        customerListLabel.setText(bundle.getString("MainGUI.customerListLabel.text")); // NOI18N

        customerTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        customerTableScroll.setViewportView(customerTable);

        addCustomerButton.setText(bundle.getString("MainGUI.addCustomerButton.text")); // NOI18N
        addCustomerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addCustomerButtonActionPerformed(evt);
            }
        });

        deleteCustomerButton.setText(bundle.getString("MainGUI.deleteCustomerButton.text")); // NOI18N
        deleteCustomerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteCustomerButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout listCustomerLayout = new javax.swing.GroupLayout(listCustomer);
        listCustomer.setLayout(listCustomerLayout);
        listCustomerLayout.setHorizontalGroup(
            listCustomerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(listCustomerLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(listCustomerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(customerListLabel)
                    .addGroup(listCustomerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, listCustomerLayout.createSequentialGroup()
                            .addComponent(addCustomerButton)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(deleteCustomerButton))
                        .addComponent(customerTableScroll, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 882, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        listCustomerLayout.setVerticalGroup(
            listCustomerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(listCustomerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(customerListLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(customerTableScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(listCustomerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addCustomerButton)
                    .addComponent(deleteCustomerButton))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        contentPanel.add(listCustomer, "card2");

        orderListLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        orderListLabel.setText(bundle.getString("MainGUI.orderListLabel.text")); // NOI18N

        orderTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        orderTableScroll.setViewportView(orderTable);

        addOrderButton.setText(bundle.getString("MainGUI.addOrderButton.text")); // NOI18N
        addOrderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addOrderButtonActionPerformed(evt);
            }
        });

        deleteOrderButton.setText(bundle.getString("MainGUI.deleteOrderButton.text")); // NOI18N
        deleteOrderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteOrderButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout listOrdersLayout = new javax.swing.GroupLayout(listOrders);
        listOrders.setLayout(listOrdersLayout);
        listOrdersLayout.setHorizontalGroup(
            listOrdersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(listOrdersLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(listOrdersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(orderListLabel)
                    .addGroup(listOrdersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, listOrdersLayout.createSequentialGroup()
                            .addComponent(addOrderButton)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(deleteOrderButton))
                        .addComponent(orderTableScroll, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 882, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        listOrdersLayout.setVerticalGroup(
            listOrdersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(listOrdersLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(orderListLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(orderTableScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(listOrdersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addOrderButton)
                    .addComponent(deleteOrderButton))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        contentPanel.add(listOrders, "card2");

        createOrderLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        createOrderLabel.setText(bundle.getString("MainGUI.createOrderLabel.text")); // NOI18N

        orderBandSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orderBandSelectActionPerformed(evt);
            }
        });

        orderBandLabel.setText(bundle.getString("MainGUI.orderBandLabel.text")); // NOI18N

        orderCustomerLabel.setText(bundle.getString("MainGUI.orderCustomerLabel.text")); // NOI18N

        orderDurationSelect.setModel(new javax.swing.SpinnerNumberModel(1, 1, 12, 1));

        orderDurationLabel.setText(bundle.getString("MainGUI.orderDurationLabel.text")); // NOI18N

        orderRegionSelect.setModel(new javax.swing.DefaultComboBoxModel<>(Region.values()));
        orderRegionSelect.setSelectedIndex(0);

        orderRegionLabel.setText(bundle.getString("MainGUI.orderRegionLabel.text")); // NOI18N

        orderDateLabel.setText(bundle.getString("MainGUI.orderDateLabel.text")); // NOI18N

        orderDateSelect.setModel(new javax.swing.SpinnerDateModel());

        createOrderButton.setText(bundle.getString("MainGUI.createOrderButton.text")); // NOI18N
        createOrderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createOrderButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout createOrderLayout = new javax.swing.GroupLayout(createOrder);
        createOrder.setLayout(createOrderLayout);
        createOrderLayout.setHorizontalGroup(
            createOrderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createOrderLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(createOrderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(createOrderButton, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(createOrderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(orderDateLabel)
                        .addComponent(orderRegionLabel)
                        .addComponent(orderDurationLabel)
                        .addComponent(orderCustomerLabel)
                        .addComponent(orderBandLabel)
                        .addComponent(createOrderLabel)
                        .addComponent(orderDurationSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(orderBandSelect, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(orderCustomerSelect, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(orderRegionSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(orderDateSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(732, Short.MAX_VALUE))
        );
        createOrderLayout.setVerticalGroup(
            createOrderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, createOrderLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(createOrderLabel)
                .addGap(18, 18, 18)
                .addComponent(orderBandLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(orderBandSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(orderCustomerLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(orderCustomerSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(orderRegionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(orderRegionSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(orderDateLabel)
                .addGap(5, 5, 5)
                .addComponent(orderDateSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(orderDurationLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(orderDurationSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 62, Short.MAX_VALUE)
                .addComponent(createOrderButton, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );

        contentPanel.add(createOrder, "card8");

        createCustomerLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        createCustomerLabel.setText(bundle.getString("MainGUI.createCustomerLabel.text")); // NOI18N

        customerNameLabel.setText(bundle.getString("MainGUI.customerNameLabel.text")); // NOI18N

        customerAddressText.setToolTipText(bundle.getString("MainGUI.customerAddressText.toolTipText")); // NOI18N

        customerAddressLabel.setText(bundle.getString("MainGUI.customerAddressLabel.text")); // NOI18N

        customerPhoneText.setToolTipText(bundle.getString("MainGUI.customerPhoneText.toolTipText")); // NOI18N

        customerPhoneLabel.setText(bundle.getString("MainGUI.customerPhoneLabel.text")); // NOI18N

        customerNameText.setToolTipText(bundle.getString("MainGUI.customerNameText.toolTipText")); // NOI18N

        createCustomerButton.setText(bundle.getString("MainGUI.createCustomerButton.text")); // NOI18N
        createCustomerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createCustomerButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout createCustomerLayout = new javax.swing.GroupLayout(createCustomer);
        createCustomer.setLayout(createCustomerLayout);
        createCustomerLayout.setHorizontalGroup(
            createCustomerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createCustomerLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(createCustomerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(createCustomerButton, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(customerNameText, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(customerPhoneLabel)
                    .addComponent(customerPhoneText, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(customerAddressLabel)
                    .addComponent(customerAddressText, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(customerNameLabel)
                    .addComponent(createCustomerLabel))
                .addContainerGap(654, Short.MAX_VALUE))
        );
        createCustomerLayout.setVerticalGroup(
            createCustomerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createCustomerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(createCustomerLabel)
                .addGap(18, 18, 18)
                .addComponent(customerNameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(customerNameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(customerAddressLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(customerAddressText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(customerPhoneLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(customerPhoneText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addComponent(createCustomerButton, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(154, Short.MAX_VALUE))
        );

        contentPanel.add(createCustomer, "card8");

        createBandLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        createBandLabel.setText(bundle.getString("MainGUI.createBandLabel.text")); // NOI18N

        bandNameText.setToolTipText(bundle.getString("MainGUI.bandNameText.toolTipText")); // NOI18N

        bandNameLabel.setText(bundle.getString("MainGUI.bandNameLabel.text")); // NOI18N

        bandRegionSelect.setModel(new javax.swing.DefaultComboBoxModel<>(Region.values()));
        bandRegionSelect.setSelectedIndex(0);

        bandRegionLabel.setText(bundle.getString("MainGUI.bandRegionLabel.text")); // NOI18N

        bandStylesSelect.setModel(new javax.swing.DefaultComboBoxModel<>(Style.values()));
        bandStylesScroll.setViewportView(bandStylesSelect);

        bandStylesLabel.setText(bundle.getString("MainGUI.bandStylesLabel.text")); // NOI18N

        bandPriceText.setToolTipText(bundle.getString("MainGUI.bandPriceText.toolTipText")); // NOI18N

        bandPriceLabel.setText(bundle.getString("MainGUI.bandPriceLabel.text")); // NOI18N

        createBandButton.setText(bundle.getString("MainGUI.createBandButton.text")); // NOI18N
        createBandButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createBandButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout createBandLayout = new javax.swing.GroupLayout(createBand);
        createBand.setLayout(createBandLayout);
        createBandLayout.setHorizontalGroup(
            createBandLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createBandLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(createBandLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(createBandButton, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bandPriceLabel)
                    .addComponent(bandStylesLabel)
                    .addComponent(bandRegionLabel)
                    .addComponent(createBandLabel)
                    .addComponent(bandNameLabel)
                    .addComponent(bandStylesScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                    .addComponent(bandRegionSelect, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bandNameText)
                    .addComponent(bandPriceText))
                .addContainerGap(636, Short.MAX_VALUE))
        );
        createBandLayout.setVerticalGroup(
            createBandLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createBandLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(createBandLabel)
                .addGap(18, 18, 18)
                .addComponent(bandNameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bandNameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bandRegionLabel)
                .addGap(5, 5, 5)
                .addComponent(bandRegionSelect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(bandStylesLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bandStylesScroll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bandPriceLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bandPriceText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                .addComponent(createBandButton, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        contentPanel.add(createBand, "card7");

        getContentPane().add(contentPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bandsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bandsButtonActionPerformed
        contentPanel.removeAll();
        contentPanel.add(listBand);
        contentPanel.repaint();
        contentPanel.revalidate();
    }//GEN-LAST:event_bandsButtonActionPerformed

    private void addOrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addOrderButtonActionPerformed
        contentPanel.removeAll();
        contentPanel.add(createOrder);
        contentPanel.repaint();
        contentPanel.revalidate();
    }//GEN-LAST:event_addOrderButtonActionPerformed

    private void customersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customersButtonActionPerformed
        contentPanel.removeAll();
        contentPanel.add(listCustomer);
        contentPanel.repaint();
        contentPanel.revalidate();
    }//GEN-LAST:event_customersButtonActionPerformed

    private void ordersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ordersButtonActionPerformed
        contentPanel.removeAll();
        contentPanel.add(listOrders);
        contentPanel.repaint();
        contentPanel.revalidate();
    }//GEN-LAST:event_ordersButtonActionPerformed

    private void addBandButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBandButtonActionPerformed
        contentPanel.removeAll();
        contentPanel.add(createBand);
        contentPanel.repaint();
        contentPanel.revalidate();
    }//GEN-LAST:event_addBandButtonActionPerformed

    private void addCustomerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCustomerButtonActionPerformed
        contentPanel.removeAll();
        contentPanel.add(createCustomer);
        contentPanel.repaint();
        contentPanel.revalidate();
    }//GEN-LAST:event_addCustomerButtonActionPerformed

    private void createBandButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createBandButtonActionPerformed
        Band band = new Band();
        band.setBandName(bandNameText.getText());
        band.setRegion(Region.values()[bandRegionSelect.getSelectedIndex()]);
        band.setRate(0.0);
        band.setPricePerHour(Double.parseDouble(bandPriceText.getText()));
        band.setStyles(bandStylesSelect.getSelectedValuesList());
        bandManager.createBand(band);
        
        orderBandSelect.addItem(band.getName());
        
        DefaultTableModel bandModel = (DefaultTableModel) bandTable.getModel();
        bandModel.addRow(new Object[]{band.getId().toString(), band.getName(), band.getRegion(), band.getStyles(), band.getPricePerHour(), band.getRate()});
        bandTable.setModel(bandModel);
        
        contentPanel.removeAll();
        contentPanel.add(listBand);
        contentPanel.repaint();
        contentPanel.revalidate();
    }//GEN-LAST:event_createBandButtonActionPerformed

    private void createCustomerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createCustomerButtonActionPerformed
        Customer customer = new Customer();
        customer.setAddress(customerAddressText.getText());
        customer.setName(customerNameText.getText());
        customer.setPhoneNumber(customerPhoneText.getText());
        customerManager.createCustomer(customer);
        
        orderCustomerSelect.addItem(customer.getName());
        
        DefaultTableModel customerModel = (DefaultTableModel) customerTable.getModel();
        customerModel.addRow(new Object[]{customer.getId().toString(), customer.getName(), customer.getAddress(), customer.getPhoneNumber()});
        customerTable.setModel(customerModel);
        
        contentPanel.removeAll();
        contentPanel.add(listCustomer);
        contentPanel.repaint();
        contentPanel.revalidate();
    }//GEN-LAST:event_createCustomerButtonActionPerformed

    private void deleteOrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteOrderButtonActionPerformed
        DefaultTableModel orderModel = (DefaultTableModel) orderTable.getModel();
        orderModel.removeRow(orderTable.getSelectedRow());
        orderTable.getModel().getValueAt(orderTable.getSelectedRow(), 0);
        orderTable.setModel(orderModel);
    }//GEN-LAST:event_deleteOrderButtonActionPerformed

    private void deleteCustomerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteCustomerButtonActionPerformed
        DefaultTableModel customerModel = (DefaultTableModel) customerTable.getModel();
        customerModel.removeRow(customerTable.getSelectedRow());
        customerTable.getModel().getValueAt(customerTable.getSelectedRow(), 0);
        customerTable.setModel(customerModel);
    }//GEN-LAST:event_deleteCustomerButtonActionPerformed

    private void deleteBandButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBandButtonActionPerformed
        DefaultTableModel bandModel = (DefaultTableModel) bandTable.getModel();
        bandModel.removeRow(bandTable.getSelectedRow());
        bandTable.getModel().getValueAt(bandTable.getSelectedRow(), 0);
        bandTable.setModel(bandModel);
    }//GEN-LAST:event_deleteBandButtonActionPerformed

    private void createOrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createOrderButtonActionPerformed
        Lease order = new Lease();
        Integer bandId = orderBandSelect.getSelectedIndex() + 1;
        order.setBand(bandManager.findBandById(bandId.longValue()));
        Integer customerId = orderCustomerSelect.getSelectedIndex() + 1;
        order.setCustomer(customerManager.getCustomer(customerId.longValue()));
        order.setDate((Date)orderDateSelect.getValue());
        order.setDuration(Integer.parseInt(orderDurationSelect.getValue().toString()));
        order.setPlace(Region.values()[orderRegionSelect.getSelectedIndex()]);
        leaseManager.createLease(order);
        
        DefaultTableModel orderModel = (DefaultTableModel) orderTable.getModel();
        orderModel.addRow(new Object[]{order.getId().toString(), order.getBand().getId().toString(), order.getCustomer().getId().toString(), order.getDate().toString(), order.getPlace().toString(), Integer.toString(order.getDuration())});
        orderTable.setModel(orderModel);
        
        contentPanel.removeAll();
        contentPanel.add(listOrders);
        contentPanel.repaint();
        contentPanel.revalidate();
    }//GEN-LAST:event_createOrderButtonActionPerformed

    private void orderBandSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orderBandSelectActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_orderBandSelectActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */       
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        log.info(" ###  Application is running  \\(^o^)/  ### ");
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBandButton;
    private javax.swing.JButton addCustomerButton;
    private javax.swing.JButton addOrderButton;
    private javax.swing.JLabel bandListLabel;
    private javax.swing.JLabel bandNameLabel;
    private javax.swing.JTextField bandNameText;
    private javax.swing.JLabel bandPriceLabel;
    private javax.swing.JTextField bandPriceText;
    private javax.swing.JLabel bandRegionLabel;
    private javax.swing.JComboBox<Region> bandRegionSelect;
    private javax.swing.JLabel bandStylesLabel;
    private javax.swing.JScrollPane bandStylesScroll;
    private javax.swing.JList<Style> bandStylesSelect;
    private javax.swing.JTable bandTable;
    private javax.swing.JScrollPane bandTableScroll;
    private javax.swing.JButton bandsButton;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JPanel createBand;
    private javax.swing.JButton createBandButton;
    private javax.swing.JLabel createBandLabel;
    private javax.swing.JPanel createCustomer;
    private javax.swing.JButton createCustomerButton;
    private javax.swing.JLabel createCustomerLabel;
    private javax.swing.JPanel createOrder;
    private javax.swing.JButton createOrderButton;
    private javax.swing.JLabel createOrderLabel;
    private javax.swing.JLabel customerAddressLabel;
    private javax.swing.JTextField customerAddressText;
    private javax.swing.JLabel customerListLabel;
    private javax.swing.JLabel customerNameLabel;
    private javax.swing.JTextField customerNameText;
    private javax.swing.JLabel customerPhoneLabel;
    private javax.swing.JTextField customerPhoneText;
    private javax.swing.JTable customerTable;
    private javax.swing.JScrollPane customerTableScroll;
    private javax.swing.JButton customersButton;
    private javax.swing.JButton deleteBandButton;
    private javax.swing.JButton deleteCustomerButton;
    private javax.swing.JButton deleteOrderButton;
    private javax.swing.JPanel firstContent;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel listBand;
    private javax.swing.JPanel listCustomer;
    private javax.swing.JPanel listOrders;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel orderBandLabel;
    private javax.swing.JComboBox<String> orderBandSelect;
    private javax.swing.JLabel orderCustomerLabel;
    private javax.swing.JComboBox<String> orderCustomerSelect;
    private javax.swing.JLabel orderDateLabel;
    private javax.swing.JSpinner orderDateSelect;
    private javax.swing.JLabel orderDurationLabel;
    private javax.swing.JSpinner orderDurationSelect;
    private javax.swing.JLabel orderListLabel;
    private javax.swing.JLabel orderRegionLabel;
    private javax.swing.JComboBox<Region> orderRegionSelect;
    private javax.swing.JTable orderTable;
    private javax.swing.JScrollPane orderTableScroll;
    private javax.swing.JButton ordersButton;
    // End of variables declaration//GEN-END:variables
}
