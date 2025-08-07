package org.example.app;

public class Customer {
    private int id;
    private String name;
    private String surname;
    private String tckn;


    public Customer(int id, String name, String surname, String tckn) {
        this.id = id;
        this.name = name;
        this.surname = surname;
       this.tckn = tckn;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getTckn() {
        return tckn;
    }

    public void setTckn(String tckn) {
        this.tckn = tckn;
    }

}
