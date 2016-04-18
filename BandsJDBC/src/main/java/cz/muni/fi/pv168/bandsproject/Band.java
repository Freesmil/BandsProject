package cz.muni.fi.pv168.bandsproject;

import java.util.List;

/**
 * Created by Lenka on 9.3.2016.
 */
public class Band {
    private Long id;
    private String bandName;
    private List<Style> styles;
    private Region region;
    private Double pricePerHour;
    private Double rate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return bandName;
    }

    public void setBandName(String bandName) {
        this.bandName = bandName;
    }

    public List<Style> getStyles() {
        return styles;
    }

    public void setStyles(List<Style> styles) {
        this.styles = styles;
    }
    
    public void addStyle(Style style) {
        styles.add(style);
    }
    
    public String getStylesString(List<Style> styles) {
        String result = "";
        
        for(Style style: styles) {
            result += style.ordinal()+",";
        }
        
        return result.substring(0, result.length()-1);
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(Double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
    
    public String toString() {
        String ret = "id: " + id + ", band name: " + bandName + ", styles: ";
        ret += styles.toString();
        return ret + "region: " + region.toString() + ", price: " + pricePerHour + ", rate: " + rate;
    }
}
