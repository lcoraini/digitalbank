package br.com.zup.digitalbank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.zup.digitalbank.model.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

	List<Address> findByCustomerId(Integer customerId);
}
