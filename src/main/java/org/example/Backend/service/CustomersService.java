package org.example.Backend.service;

import org.example.model.Customers;
import org.example.repository.CustomersRepository;

import java.util.List;

public class CustomersService {
    private final CustomersRepository repo;

    public CustomersService(CustomersRepository repo) {
        this.repo = repo;
    }

    public List<Customers> getAllCustomers() {
        return repo.getAll();
    }

    public Customers getCustomerById(int id) {
        return repo.getById(id);
    }

    public Customers getCustomerByInvoice(String series, String number) {
        return repo.findByInvoice(series, number);
    }

    public boolean addCustomer(String name, String surname, String tckn) {
        // Null veya boş kontrol
        if (name == null || surname == null || tckn == null ||
                name.trim().isEmpty() || surname.trim().isEmpty() || tckn.trim().isEmpty()) {
            System.out.println("❌ Eksik veri: Ad, Soyad veya TCKN boş olamaz.");
            return false;
        }

        // TCKN doğrulama: 11 haneli ve sadece rakam
        if (!tckn.matches("\\d{11}")) {
            System.out.println("❌ Geçersiz TCKN: 11 haneli rakam olmalıdır.");
            return false;
        }

        Customers c = new Customers(0, name.trim(), surname.trim(), tckn.trim());
        return repo.add(c);
    }

    public boolean updateCustomer(int id, String name, String surname, String tckn) {
        if (name == null || surname == null || tckn == null ||
                name.trim().isEmpty() || surname.trim().isEmpty() || tckn.trim().isEmpty()) {
            System.out.println("❌ Eksik veri: Ad, Soyad veya TCKN boş olamaz.");
            return false;
        }

        if (!tckn.matches("\\d{11}")) {
            System.out.println("❌ Geçersiz TCKN: 11 haneli rakam olmalıdır.");
            return false;
        }

        Customers c = new Customers(id, name.trim(), surname.trim(), tckn.trim());
        return repo.update(c);
    }

    public boolean deleteCustomer(int id) {
        return repo.delete(id);
    }
}
