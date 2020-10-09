package br.com.zup.digitalbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.zup.digitalbank.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

	Customer findByEmail(String email);

	Customer findByCpf(String cpf);
}
