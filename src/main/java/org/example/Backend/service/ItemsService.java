package org.example.Backend.service;

import org.example.Backend.model.Items;
import org.example.Backend.repository.ItemsRepository;

import java.util.List;

public class ItemsService {
    private final ItemsRepository repo;
    public ItemsService(ItemsRepository repo) { this.repo = repo; }

    public List<Items> getAllItems() { return repo.getAll(); }
    public Items getItemById(int id) { return repo.getById(id); }
    public boolean addItem(Items item) { return repo.add(item); }
    public boolean updateItem(Items item) { return repo.update(item); }
    public boolean deleteItem(int id) { return repo.delete(id); }
}
