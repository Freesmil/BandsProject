package cz.muni.fi.pv168.bandsproject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.sql.DataSource;

import static org.junit.Assert.*;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.rules.ExpectedException;

/**
 *
 * @author Lenka Svetlovska
 */
public class BandManagerImplTest {
    private BandManagerImpl instance;
    private DataSource dataSource;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
   
    @Before
    public void setUp() throws SQLException {
        dataSource = prepareDataSource();
        try (Connection connection = dataSource.getConnection()) {
            connection.prepareStatement("CREATE TABLE BAND ("
                    + "id bigint primary key generated always as identity,"
                    + "name VARCHAR(20),"
                    + "styles VARCHAR(50),"
                    + "region VARCHAR(30),"
                    + "pricePerHour Double,"
                    + "rate Double)").executeUpdate();
        }
        instance = new BandManagerImpl(dataSource);
    }

    private static DataSource prepareDataSource() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:bandmgr-test");
        ds.setCreateDatabase("create");
        return ds;
    }

    @After
    public void tearDown() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.prepareStatement("DROP TABLE BAND").executeUpdate();
        }
    }

    /**
     * Test of createBand method, of class BandManagerImpl.
     */
    @Test
    public void testCreateBand() {
        List<Style> styles = new ArrayList<>();
        styles.add(Style.metal);
        styles.add(Style.rock);
        Band band = newBand("Eufory", styles, Region.slovensko, 200.00, 7.4);
        
        instance.createBand(band);
        
        Long bandId = band.getId();
        assertNotNull(bandId);
        
        Band result = instance.findBandById(bandId);
        assertNotSame(band, result);
        assertDeepEquals(band, result);
        
        List<Style> anotherStyles = new ArrayList<>();
        anotherStyles.add(Style.disco);
        anotherStyles.add(Style.hipHop);
        anotherStyles.add(Style.dnb);
        Band anotherBand = newBand("Some DiscoHipHopDnbBand", anotherStyles, Region.plzensky, 149.99, 2.3);
        
        instance.createBand(anotherBand);
        
        Long anotherBandId = anotherBand.getId();
        
        Band anotherResult = instance.findBandById(anotherBandId);
       
        assertNotSame(result, anotherResult);
        assertDeepNotEquals(result, anotherResult);
    }
    
    /**
     * Test of createBand method with null, of class BandManagerImpl.
     * @throws java.lang.Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateBandWithNull() throws Exception {
        instance.createBand(null);
    }
    
    /**
     * Test of createBand method with wrong argument, of class BandManagerImpl.
     */
    @Test
    public void testCreateBandWithWrongArguments() {
        List<Style> styles = new ArrayList<>();
        styles.add(Style.rock);
        Band band = newBand("Eufory", styles, Region.slovensko, 330.30, 6.5);
        band.setId(1L);

        expectedException.expect(IllegalArgumentException.class);
        instance.createBand(band);

        //null band name
        band = newBand(null, styles, Region.slovensko, 330.30, 6.5);
        expectedException.expect(IllegalArgumentException.class);
        instance.createBand(band);

        //null region
        band = newBand("Eufory", styles, null, 330.30, 6.5);
        expectedException.expect(IllegalArgumentException.class);
        instance.createBand(band);

        //negative price
        band = newBand("Eufory", styles, Region.slovensko, -1.0, 6.5);
        expectedException.expect(IllegalArgumentException.class);
        instance.createBand(band);

        //negative rate
        band = newBand("Eufory", styles, Region.slovensko, 330.30, -1.0);
        expectedException.expect(IllegalArgumentException.class);
        instance.createBand(band);

        //null styles
        styles.removeAll(styles);
        band = newBand("Eufory", styles, Region.slovensko, 330.30, 1.0);
        expectedException.expect(IllegalArgumentException.class);
        instance.createBand(band);
    }

    /**
     * Test of updateBand method, of class BandManagerImpl.
     */
    @Test   //TODO
    public void testUpdateBand() {

    }

    /**
     * Test of deleteBand method, of class BandManagerImpl.
     */
    @Test   
    public void testDeleteBand() {
        
        List<Style> styles = new ArrayList<>();
        styles.add(Style.metal);
        styles.add(Style.rock);
        Band band = newBand("Eufory", styles, Region.slovensko, 330.30, 6.5);
        
        List<Style> styles2 = new ArrayList<>();
        styles2.add(Style.pop);
        styles2.add(Style.jazz);
        styles2.add(Style.reggae);
        Band band2 = newBand("Some band", styles2, Region.vysocina, 30.58, 4.3);
        
        instance.createBand(band);
        instance.createBand(band2);
        
        instance.deleteBand(band);
        
        assertNull(instance.findBandById(band.getId()));
        assertNotNull(instance.findBandById(band2.getId()));
    }
    
    /**
     * Test of deleteBand method with null, of class BandManagerImpl.
     * @throws java.lang.Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDeleteBandWithNull() throws Exception {
        instance.deleteBand(null);
    }   
    
    /**
     * Test of deleteBand method with wrong argument, of class BandManagerImpl.
     */
    @Test 
    public void testDeleteBandWithWrongArgument() {
        List<Style> styles = new ArrayList<>();
        styles.add(Style.metal);
        styles.add(Style.rock);
        Band band = newBand("Eufory", styles, Region.slovensko, 330.30, 6.5);

        //null id
        expectedException.expect(IllegalArgumentException.class);
        band.setId(null);
        instance.deleteBand(null);

        //null id
        expectedException.expect(IllegalArgumentException.class);
        band.setId(1L);
        instance.deleteBand(null);
    }

    /**
     * Test of findBandById method, of class BandManagerImpl.
     */
    @Test
    public void testFindBandById() {
        List<Style> styles = new ArrayList<>();
        styles.add(Style.metal);
        styles.add(Style.rock);
        Band band = newBand("Eufory", styles, Region.slovensko, 330.30, 6.5);
        
        instance.createBand(band);
        Long bandId = band.getId();
        
        Band result = instance.findBandById(bandId);
        assertDeepEquals(band, result);
    }
        
    /**
     * Test of findBandById method with wrong argument, of class BandManagerImpl.
     */
    @Test
    public void testFindBandByIdWithWrongArguments() {
        assertNull(instance.findBandById(1L));

        //null id
        expectedException.expect(NullPointerException.class);
        instance.findBandById(null);

        //null id
        expectedException.expect(IllegalArgumentException.class);
        instance.findBandById(-1L);
    }
    
    /**
     * Test of findBandByName method, of class BandManagerImpl.
     */
    @Test   //TODO
    public void testFindBandByName() {

    }

    /**
     * Test of findBandByStyles method, of class BandManagerImpl.
     */
    @Test   //TODO
    public void testFindBandByStyles() {

    }

    /**
     * Test of findBandByRegion method, of class BandManagerImpl.
     */
    @Test   //TODO
    public void testFindBandByRegion() {

    }

    /**
     * Test of findBandbyPriceRange method, of class BandManagerImpl.
     */
    @Test   //TODO
    public void testFindBandbyPriceRange() {

    }

    /**
     * Test of findBandByRate method, of class BandManagerImpl.
     */
    @Test   //TODO
    public void testFindBandByRate() {

    }

    /**
     * 
     * @param expectedList
     * @param actualList 
     */
    private void assertDeepEquals(List<Band> expectedList, List<Band> actualList) {
        for(int i = 0; i < expectedList.size(); i++) {
            Band expected = expectedList.get(i);
            Band actual = actualList.get(i);
            assertDeepEquals(expected, actual);
        }
    }
    
    /**
     * 
     * @param expected
     * @param actual 
     */
    private void assertDeepEquals(Band expected, Band actual) {        
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getRegion(),actual.getRegion());
        assertEquals(expected.getPricePerHour(), actual.getPricePerHour());
        assertEquals(expected.getRate(), actual.getRate());
        assertEquals(expected.getStyles().size(), actual.getStyles().size());
        assertTrue(expected.getStyles().containsAll(actual.getStyles()) && actual.getStyles().containsAll(expected.getStyles()));
    }
    
    /**
     * 
     * @param expected
     * @param actual 
     */
    private void assertDeepNotEquals(Band expected, Band actual) {
        boolean res = true;
        
        if(!Objects.equals(expected.getId(), actual.getId())) res = false;
        if(!expected.getName().equals(actual.getName())) res = false;
        if(!expected.getRegion().equals(actual.getRegion())) res = false;
        if(!Objects.equals(expected.getPricePerHour(), actual.getPricePerHour())) res = false;
        if(!Objects.equals(expected.getRate(), actual.getRate())) res = false;
        if(expected.getStyles().size() == actual.getStyles().size()) {
            for(int i = 0; i < expected.getStyles().size(); i++) {
                Style exp = expected.getStyles().get(i);
                Style act = actual.getStyles().get(i);
                if(!exp.equals(act)) {
                    res = false;
                    break;
                }
            }
        } else res = false;
        
        assertFalse(res);
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
    
    /**
     * idComparator
     */
    private static Comparator<Band> idComparator = new Comparator<Band>() {
        @Override
        public int compare(Band b1, Band b2) {
            return b1.getId().compareTo(b2.getId());
        }
    };
    
    /**
     * styleComparator
     */
    private static Comparator<Style> styleComparator = new Comparator<Style>() {
        @Override
        public int compare(Style o1, Style o2) {
            return o1.compareTo(o2);
        }
    };
 }
