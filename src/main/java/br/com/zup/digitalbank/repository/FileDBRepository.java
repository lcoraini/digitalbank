package br.com.zup.digitalbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.zup.digitalbank.model.FileDB;

public interface FileDBRepository extends JpaRepository<FileDB, Integer> {

	FileDB findByCustomerId(Integer customerId);
}
