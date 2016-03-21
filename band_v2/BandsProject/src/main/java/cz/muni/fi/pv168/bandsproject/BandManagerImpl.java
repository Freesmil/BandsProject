package cz.muni.fi.pv168.bandsproject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Lenka on 9.3.2016.
 */
public class BandManagerImpl implements BandManager{
    private final DataSource dataSource;
    
    public BandManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createBand(Band band) throws ServiceFailureException {
        validate(band);
        if (band.getId() != null) {
            throw new IllegalArgumentException("band id is already set");
        }

        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "INSERT INTO BAND (name,styles,region,pricePerHour,rate) VALUES (?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {

            st.setString(1, band.getName());
            st.setString(2, convertEnumToString(band.getStyles()));
            st.setInt(3, band.getRegion().ordinal());
            st.setDouble(4, band.getPricePerHour());
            st.setDouble(5, band.getRate());
            int addedRows = st.executeUpdate();
            if (addedRows != 1) {
                throw new ServiceFailureException("Internal Error: More rows ("
                        + addedRows + ") inserted when trying to insert band " + band);
            }

            ResultSet keyRS = st.getGeneratedKeys();
            band.setId(getKey(keyRS, band));
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when inserting band " + band, ex);
        }
    }

    @Override
    public void updateBand(Band band) throws ServiceFailureException {
        validate(band);
        if(band.getId() == null) {
            throw new IllegalArgumentException("band id is null");
        }
        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                    "UPDATE BAND SET name = ?,styles = ?,region = ?,pricePerHour = ?,rate = ? WHERE id = ?")){
            st.setString(1, band.getName());
            st.setString(2, convertEnumToString(band.getStyles()));
            st.setInt(3, band.getRegion().ordinal());
            st.setDouble(4, band.getPricePerHour());
            st.setDouble(5, band.getRate());
            st.setLong(6, band.getId());
            
            int count = st.executeUpdate();
            if(count == 0) {
                throw new EntityNotFoundException("Band " + band + " was not found in database!");
            } else if(count != 1) {
                throw new ServiceFailureException("Invalid updated rows count detected "
                        + "(one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when updating band " + band, ex);
        }
    }

    @Override
    public void deleteBand(Band band) throws ServiceFailureException {
        if (band == null) {
            throw new IllegalArgumentException("band is null");
        }
        if (band.getId() == null) {
            throw new IllegalArgumentException("band id is null");
        }
        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                    "DELETE FROM band WHERE id = ?")) {

            st.setLong(1, band.getId());

            int count = st.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException("Band " + band + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid deleted rows count detected "
                        + "(one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when updating band " + band, ex);
        }
    }

    @Override
    public Band findBandById(Long id) throws ServiceFailureException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "SELECT id,name,styles,region,pricePerHour,rate FROM BAND WHERE id = ?")) {
            st.setLong(1, id);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                Band band = resultSetToBand(rs);
                if (rs.next()) {
                    throw new ServiceFailureException(
                        "Internal error: More entities with the same id found "
                            + "(source id: " + id + ", found " + band + " and " + resultSetToBand(rs));
                }
                return band;
            } else {
                return null;
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving band with id " + id, ex);
        }
    }

    @Override
    public List<Band> findBandByBandName(String name) throws ServiceFailureException {
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Band> findBandbyPrice(Double from, Double to) throws ServiceFailureException {
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
     * @param keyRS
     * @param band
     * @return
     * @throws ServiceFailureException
     * @throws SQLException
     */
    private Long getKey(ResultSet keyRS, Band band) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert band " + band
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert band " + band
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retriving failed when trying to insert band " + band
                    + " - no key found");
        }
    }

    /**
     *
     * @param styles
     * @return
     */
    private String convertEnumToString(List<Style> styles) {
        String str = "";
        for(Style style : styles) {
            str = style.toString() + " ";
        }
        return str;
    }

    /**
     *
     * @param str
     * @return
     */
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
}
