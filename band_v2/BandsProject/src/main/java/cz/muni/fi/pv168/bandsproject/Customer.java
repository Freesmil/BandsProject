package cz.muni.fi.pv168.bandsproject;

/**
 * Created by Lenka on 9.3.2016.
 */
public class Customer {
    private Long id;
    private String name;
    private String phoneNumber;
    private String address;
/*
    public Customer(String name, String phoneNumber, String address) {
        //overit vstupy
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public Customer(){

    }
*/
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
