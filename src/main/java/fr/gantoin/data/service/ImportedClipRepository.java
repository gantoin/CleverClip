package fr.gantoin.data.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import fr.gantoin.data.entity.ImportedClip;
import fr.gantoin.data.entity.SamplePerson;

public interface ImportedClipRepository
        extends
            JpaRepository<ImportedClip, Long>,
            JpaSpecificationExecutor<ImportedClip> {

}
