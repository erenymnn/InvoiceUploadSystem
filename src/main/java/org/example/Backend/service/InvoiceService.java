package org.example.Backend.service;

import org.example.Backend.model.Invoices;
import org.example.Backend.repository.InvoicesRepository;

import java.util.List;

public class InvoiceService {
    private final InvoicesRepository repo;

    public InvoiceService(InvoicesRepository repo) {
        this.repo = repo;
    }

    public List<Invoices> getAllInvoices() {
        return repo.getAll();
    }

    public Invoices getInvoiceById(int id) {
        return repo.getById(id);
    }

    public boolean addInvoice(Invoices inv) {
        if (!isValidInvoice(inv)) {
            System.out.println("Geçersiz fatura bilgisi: Seri veya numara hatalı.");
            return false;
        }
        return repo.add(inv);
    }

    public boolean updateInvoice(Invoices inv) {
        if (!isValidInvoice(inv)) {
            System.out.println("Geçersiz fatura bilgisi: Seri veya numara hatalı.");
            return false;
        }
        return repo.update(inv);
    }

    public boolean deleteInvoice(int id) {
        return repo.delete(id);
    }

    // Invoice series and number verification
    private boolean isValidInvoice(Invoices inv) {
        String series = inv.getSeries();
        String invoice = inv.getInvoice(); // modeldeki doğru alan

        if (series == null || invoice == null || series.trim().isEmpty() || invoice.trim().isEmpty()) {
            return false;
        }

        // Seri: 1-5 karakter alfanümerik
        if (!series.matches("[A-Za-z0-9]{1,5}")) {
            System.out.println("Seri hatalı: " + series);
            return false;
        }

        // Numara: 1-10 rakam
        if (!invoice.matches("\\d{1,10}")) {
            System.out.println("Numara hatalı: " + invoice);
            return false;
        }

        return true;
    }
}
