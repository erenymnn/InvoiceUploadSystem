package org.example.Backend.service;

import org.example.Backend.model.InvoiceItems;
import org.example.Backend.repository.InvoiceItemsRepository;

import java.util.List;

public class InvoiceItemsService {
    private final InvoiceItemsRepository repo;
    public InvoiceItemsService(InvoiceItemsRepository repo) { this.repo = repo; }

    public List<InvoiceItems> getAllInvoiceItems() { return repo.getAll(); }
    public List<InvoiceItems> getInvoiceItemsByInvoiceId(int invoiceId) { return repo.getByInvoiceId(invoiceId); }
    public boolean addInvoiceItem(InvoiceItems item) { return repo.add(item); }
    public boolean updateInvoiceItem(InvoiceItems item) { return repo.update(item); }
    public boolean deleteInvoiceItem(int id) { return repo.delete(id); }
}
