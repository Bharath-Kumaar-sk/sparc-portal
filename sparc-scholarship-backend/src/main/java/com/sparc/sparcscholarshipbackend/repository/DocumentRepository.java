package com.sparc.sparcscholarshipbackend.repository;

import com.sparc.sparcscholarshipbackend.entity.Document;
import com.sparc.sparcscholarshipbackend.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    //find the applications for each document.
    Optional<Document> findByApplicationIdAndDocumentType(Long applicationId, DocumentType documentType);
}