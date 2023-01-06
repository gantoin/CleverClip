package fr.gantoin.data.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import fr.gantoin.data.entity.ImportedClip;
import fr.gantoin.data.entity.SamplePerson;

@Service
public class ImportedClipService {

    private final ImportedClipRepository repository;

    public ImportedClipService(ImportedClipRepository repository) {
        this.repository = repository;
    }

    public Optional<ImportedClip> get(Long id) {
        return repository.findById(id);
    }

    public ImportedClip update(ImportedClip entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<ImportedClip> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<ImportedClip> list(Pageable pageable, Specification<ImportedClip> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
