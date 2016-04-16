package cz.muni.fi.pv168.bandsproject;

import java.util.Date;

/**
 * Created by Lenka on 9.3.2016.
 */
public class Order {
    private Long id;
    private Customer customer;
    private Band band;
    private Date date;
    private Region place;
    private int duration;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Band getBand() {
        return band;
    }

    public void setBand(Band band) {
        this.band = band;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Region getPlace() {
        return place;
    }

    public void setPlace(Region place) {
        this.place = place;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
