package br.com.zup.digitalbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.zup.digitalbank.model.Proposal;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, Integer> {

	Proposal findByCustomerId(Integer customerId);
}
