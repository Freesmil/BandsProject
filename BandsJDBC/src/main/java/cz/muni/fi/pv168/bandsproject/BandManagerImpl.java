package cz.muni.fi.pv168.bandsproject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * Created by Lenka on 9.3.2016.
 */
public class BandManagerImpl implements BandManager{
    private final DataSource dataSource;
    private JdbcTemplate jdbcTemplateObject;
    
    public BandManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createBand(Band band) throws ServiceFailureException { 
        validate(band);
        if (band.getId() != null) {
            throw new IllegalArgumentException("band id is already set");
        }
        String SQL = "INSERT INTO BAND (name,styles,region,pricePerHour,rate) VALUES (?,?,?,?,?)";
        jdbcTemplateObject.update(SQL, band.getName(), band.getStyles().toString(), 
                band.getRegion().ordinal(), band.getPricePerHour(), band.getRate());
    }

    @Override
    public void updateBand(Band band) throws ServiceFailureException {
        validate(band);
        if(band.getId() == null) {
            throw new IllegalArgumentException("band id is null");
        }
        String SQL = "UPDATE BAND SET name = ?,styles = ?,region = ?,pricePerHour = ?,rate = ? WHERE id = ?";
        jdbcTemplateObject.update(SQL, band.getName(), band.getStyles().toString(), 
                band.getRegion().ordinal(), band.getPricePerHour(), band.getRate(), band.getId());
    }

    @Override
    public void deleteBand(Band band) throws ServiceFailureException {
        if (band == null) {
            throw new IllegalArgumentException("band is null");
        }
        if (band.getId() == null) {
            throw new IllegalArgumentException("band id is null");
        }
        String SQL = "DELETE FROM band WHERE id = ?";
        jdbcTemplateObject.update(SQL, band.getId()); //UPDATE??
    }

    @Override
    public Band findBandById(Long id) throws ServiceFailureException {
        String SQL = "SELECT * FROM BAND WHERE id = ?";
        Band band = jdbcTemplateObject.queryForObject(SQL, new Object[]{id}, new BandMapper());
        return band;
    }

    @Override
    public List<Band> findBandByName(String name) throws ServiceFailureException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT id,name,styles,region,pricePerHour,rate FROM BAND WHERE name LIKE ?")) {
            st.setString(1, name);
            ResultSet rs = st.executeQuery();
            List<Band> bands = new ArrayList<>();
            while(rs.next()) {
                bands.add(resultSetToBand(rs));
            }
            return bands;
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving band with name " + name, ex);
        }
    }

    @Override //// TODO
    public List<Band> findBandByStyles(List<Style> styles) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override //// TODO
    public List<Band> findBandByRegion(List<Region> regions) {
        String s = "";
        for(Region r: regions){
            s += r.ordinal()+", ";
        }
        s = s.substring(0, s.length()-2);
        
        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT * FROM BAND WHERE region IN ("+s+")")) {
            ResultSet rs = st.executeQuery();
            List<Band> bands = new ArrayList<>();
            while(rs.next()) {
                bands.add(resultSetToBand(rs));
            }
            return bands;
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving band with regions " + regions.toString(), ex);
        }
    }

    @Override
    public List<Band> findBandByPriceRange(Double from, Double to) throws ServiceFailureException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT id,name,styles,region,pricePerHour,rate FROM BAND "
                                + "WHERE pricePerHour >= ? AND pricePerHour <= ?")) {
            st.setDouble(1, from);
            st.setDouble(2, to);
            ResultSet rs = st.executeQuery();
            List<Band> bands = new ArrayList<>();
            while(rs.next()) {
                bands.add(resultSetToBand(rs));
            }
            return bands;
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving band with price from " + from + " to " + to, ex);
        }
    }

    @Override
    public List<Band> findBandByRate(Double from) throws ServiceFailureException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT id,name,styles,region,pricePerHour,rate FROM BAND WHERE rate >= ?")) {
            st.setDouble(1, from);
            ResultSet rs = st.executeQuery();
            List<Band> bands = new ArrayList<>();
            while(rs.next()) {
                bands.add(resultSetToBand(rs));
            }
            return bands;
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving band with rate from " + from, ex);
        }
    }

    /**
     *
     * @param band
     * @throws IllegalArgumentException
     */
    private void validate(Band band) throws IllegalArgumentException {
        if (band == null) {
            throw new IllegalArgumentException("band is null");
        }
        if (band.getName() == null) {
            throw new IllegalArgumentException("band name is null");
        }
        if (band.getStyles() == null) {
            throw new IllegalArgumentException("band styles is null");
        }
        if (band.getRegion() == null) {
            throw new IllegalArgumentException("band region is null");
        }
        if (band.getPricePerHour() < 0) {
            throw new IllegalArgumentException("band price per hour is negative");
        }
        if (band.getRate() < 0) {
            throw new IllegalArgumentException("band rate is negative");
        }
    }

    /**
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    private Band resultSetToBand(ResultSet rs) throws SQLException {
        Band band = new Band();
        band.setId(rs.getLong("id"));
        band.setBandName(rs.getString("name"));
        band.setStyles(convertStringToEnum(rs.getString("styles")));
        band.setRegion(Region.values()[rs.getInt("region")]);
        band.setPricePerHour(rs.getDouble("pricePerHour"));
        band.setRate(rs.getDouble("rate"));
        return band;
    }

    /**
     *
     * @param str
     * @return
     */// ZMAZAT
    private List<Style> convertStringToEnum(String str) {
        String styles[] = str.split(" ");
        if(styles.length == 0) {
            return null;
        }
        List<Style> styleList = new ArrayList<>();
        for(int i = 0; i < styles.length; i++) {
            styleList.add(Style.valueOf(styles[i]));
        }
        return styleList;
    }
    
    public class BandMapper implements RowMapper<Band> {
        public Band mapRow(ResultSet rs, int rowNum) throws SQLException {
            Band band = new Band();
            band.setId(rs.getLong("id"));
            band.setBandName(rs.getString("name"));
            band.setStyles(convertStringToEnum(rs.getString("styles")));
            band.setRegion(Region.values()[rs.getInt("region")]);
            band.setPricePerHour(rs.getDouble("pricePerHour"));
            band.setRate(rs.getDouble("rate"));
            return band;
        }
    }
}

