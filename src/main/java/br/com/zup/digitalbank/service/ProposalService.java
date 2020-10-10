package br.com.zup.digitalbank.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.zup.digitalbank.message.ResponseMessage;
import br.com.zup.digitalbank.model.Address;
import br.com.zup.digitalbank.model.Customer;
import br.com.zup.digitalbank.model.FileDB;
import br.com.zup.digitalbank.model.Proposal;
import br.com.zup.digitalbank.repository.AddressRepository;
import br.com.zup.digitalbank.repository.CustomerRepository;
import br.com.zup.digitalbank.repository.FileDBRepository;
import br.com.zup.digitalbank.repository.ProposalRepository;

@Service
public class ProposalService {

	@Autowired
	private ProposalRepository repository;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private AddressRepository addressRepository;
	@Autowired
	private FileDBRepository fileRepository;

	public Optional<Proposal> findById(final Integer id) {
		return repository.findById(id);
	}

	public Proposal findByCustomerId(final Integer customerId) {
		return repository.findByCustomerId(customerId);
	}

	public ResponseEntity<ResponseMessage> create(final Integer customerId) {
		final Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
		if (!optionalCustomer.isPresent()) {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ResponseMessage("Error",
					Arrays.asList("proposal.validator.customerNotFoundById: " + customerId)));
		}
		final Customer customer = optionalCustomer.get();
		final Address address = addressRepository.findByCustomerId(customerId);
		final FileDB file = fileRepository.findByCustomerId(customerId);
		final List<String> errors = validateEntities(customerId, address, file);
		if (!errors.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ResponseMessage("Error", errors));
		}
		final Proposal proposal = new Proposal();
		proposal.setCustomer(customer);
		proposal.setAddress(address);
		proposal.setFileDB(file);
		proposal.setAccepted(false);
		repository.save(proposal);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	public ResponseEntity<ResponseMessage> update(final Integer id, final Proposal proposal) {
		final Optional<Proposal> optionalPersistentProposal = repository.findById(id);
		if (!optionalPersistentProposal.isPresent()) {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
					new ResponseMessage("Error", Arrays.asList("proposal.validator.proposalNotFoundById: " + id)));
		}
		final Proposal persistentProposal = optionalPersistentProposal.get();
		persistentProposal.setCustomer(proposal.getCustomer());
		persistentProposal.setAddress(proposal.getAddress());
		persistentProposal.setFileDB(proposal.getFileDB());
		persistentProposal.setAccepted(proposal.isAccepted());
		repository.save(persistentProposal);
		if (!proposal.isAccepted()) {
			return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.LOCATION, "/account/" + id)
					.body(new ResponseMessage("Account will not be created and an email will be sended",
							Collections.emptyList()));
		}
		return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.LOCATION, "/account/" + id)
				.body(new ResponseMessage("A new account will be created and an email will be sended",
						Collections.emptyList()));
	}

	private List<String> validateEntities(final Integer customerId, final Address address, final FileDB file) {
		final List<String> errors = new ArrayList<>();
		if (address == null) {
			errors.add("proposal.validator.addressNotFoundByCustomerId: " + customerId);
		}
		if (file == null) {
			errors.add("proposal.validator.fileNotFoundByCustomerId: " + customerId);
		}
		return errors;
	}
}
