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
            System.out.println("❌ Geçersiz fatura bilgisi: Seri veya numara hatalı.");
            return false;
        }
        return repo.add(inv);
    }

    public boolean updateInvoice(Invoices inv) {
        if (!isValidInvoice(inv)) {
            System.out.println("❌ Geçersiz fatura bilgisi: Seri veya numara hatalı.");
            return false;
        }
        return repo.update(inv);
    }

    public boolean deleteInvoice(int id) {
        return repo.delete(id);
    }

    // Fatura serisi ve numarası doğrulama
    private boolean isValidInvoice(Invoices inv) {
        if (inv.getSeries() == null || inv.getInvoice() == null ||
                inv.getSeries().trim().isEmpty() || inv.getInvoice().trim().isEmpty()) {
            return false;
        }

        // Seri: 1-5 karakter alfanümerik
        if (!inv.getSeries().matches("[A-Za-z0-9]{1,5}")) {
            return false;
        }

        // Numara: 1-10 haneli rakam
        if (!inv.getInvoice().matches("\\d{1,10}")) {
            return false;
        }

        return true;
    }
}
