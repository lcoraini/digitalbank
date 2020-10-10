package br.com.zup.digitalbank.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.zup.digitalbank.message.ResponseMessage;
import br.com.zup.digitalbank.model.Customer;
import br.com.zup.digitalbank.repository.CustomerRepository;

@Service
public class CustomerService {

	@Autowired
	private CustomerRepository repository;

	public List<Customer> findAll() {
		return repository.findAll();
	}

	public Optional<Customer> findById(final Integer id) {
		return repository.findById(id);
	}

	public ResponseEntity<ResponseMessage> create(final Customer customer) {
		final List<String> errors = new ArrayList<>();
		final String cpfPattern = "\\d{9}-\\d{2}";
		if (!(findByEmail(customer.getEmail()) == null)) {
			errors.add("customer.validator.emailAlreadyExists: " + customer.getEmail());
		}
		if (!(findByCpf(customer.getCpf()) == null)) {
			errors.add("customer.validator.cpfAlreadyExists: " + customer.getCpf());
		}
		if (customer.getCpf() != null && !customer.getCpf().matches(cpfPattern)) {
			errors.add("customer.validator.cpfDoesNotMatchPattern: " + customer.getCpf());
		}
		errors.addAll(validateCustomer(customer));
		if (!errors.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Validation error", errors));
		}
		final Customer newCustomer = repository.save(customer);
		return ResponseEntity.status(HttpStatus.CREATED).header(HttpHeaders.LOCATION, "/address/" + newCustomer.getId())
				.build();
	}

	public Customer findByEmail(final String email) {
		return repository.findByEmail(email);
	}

	public Customer findByCpf(final String cpf) {
		return repository.findByCpf(cpf);
	}

	@SuppressWarnings("deprecation")
	private static List<String> validateCustomer(final Customer customer) {
		final List<String> errors = new ArrayList<>();
		final Date currentDate = new Date();

		if (customer.getName() == null) {
			errors.add("customer.validator.nameIsNull");
		}
		if (customer.getLastName() == null) {
			errors.add("customer.validator.lastNameIsNull");
		}
		if (customer.getCpf() == null) {
			errors.add("customer.validator.cpfIsNull");
		}
		if (customer.getEmail() == null) {
			errors.add("customer.validator.emailIsNull");
		}
		if (customer.getEmail() != null && !EmailValidator.getInstance().isValid(customer.getEmail())) {
			errors.add("customer.validator.emailIsInvalid: " + customer.getEmail());
		}
		if (customer.getBirthDate() == null) {
			errors.add("customer.validator.birthDateIsNull");
		}
		if (customer.getBirthDate() != null && currentDate.getYear() - customer.getBirthDate().getYear() < 0) {
			errors.add("customer.validator.birthDateNotInPast: " + customer.getBirthDate());
		}
		if (customer.getBirthDate() != null && currentDate.getYear() - customer.getBirthDate().getYear() >= 0
				&& currentDate.getYear() - customer.getBirthDate().getYear() < 18) {
			errors.add("customer.validator.underageCustomer: " + customer.getBirthDate());
		}
		return errors;
	}
}
