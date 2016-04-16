package cz.muni.fi.pv168.bandsproject;

import java.util.List;

/**
 *
 * @author Lenka
 */
public interface BandManager {
   
    public void createBand(Band band);
    
    public void updateBand(Band band);
    
    public void deleteBand(Band band);
    
    public List<Band> getAllBands();
    
    public Band findBandById(Long id);
    
    public List<Band> findBandByName(String name);
    
    public List<Band> findBandByStyles(List<Style> styles);
    
    public List<Band> findBandByRegion(List<Region> regions);
    
    public List<Band> findBandByPriceRange(Double from, Double to);
    
    public List<Band> findBandByRate(Double from);
}
