package fr.gantoin.data.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import fr.gantoin.data.entity.Clip;
import fr.gantoin.data.entity.ImportedClip;
import fr.gantoin.data.service.mapper.ImportedClipMapper;

@Service
public class ImportedClipService {

    private final ImportedClipRepository importedClipRepository;

    public ImportedClipService(ImportedClipRepository importedClipRepository) {
        this.importedClipRepository = importedClipRepository;
    }

    public Optional<ImportedClip> get(Long id) {
        return importedClipRepository.findById(id);
    }

    public ImportedClip update(ImportedClip entity) {
        return importedClipRepository.save(entity);
    }

    public void delete(Long id) {
        importedClipRepository.deleteById(id);
    }

    public Page<ImportedClip> list(Pageable pageable) {
        return importedClipRepository.findAll(pageable);
    }

    public Page<ImportedClip> list(Pageable pageable, Specification<ImportedClip> filter) {
        return importedClipRepository.findAll(filter, pageable);
    }

    public int count() {
        return (int) importedClipRepository.count();
    }

    public void importClips(Set<Clip> clips) {
        importedClipRepository.saveAll(ImportedClipMapper.map(clips));
    }

}
