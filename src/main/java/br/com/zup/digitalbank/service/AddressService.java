package br.com.zup.digitalbank.service;

import static br.com.zup.digitalbank.utils.Utils.getCustomerByIdExample;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.zup.digitalbank.message.ResponseMessage;
import br.com.zup.digitalbank.model.Address;
import br.com.zup.digitalbank.repository.AddressRepository;
import br.com.zup.digitalbank.repository.CustomerRepository;

@Service
public class AddressService {

	@Autowired
	private AddressRepository repository;
	@Autowired
	private CustomerRepository customerRepository;

	public List<Address> findAll() {
		return repository.findAll();
	}

	public List<Address> findByCustomerId(final Integer customerId) {
		return repository.findByCustomerId(customerId);
	}

	public ResponseEntity<ResponseMessage> create(final Integer customerId, final Address address) {
		final List<String> errors = new ArrayList<>();
		if (!customerRepository.exists(getCustomerByIdExample(customerId))) {
			errors.add("address.validator.customerNotFoundById: " + address.getCustomerId());
		}
		errors.addAll(validateAddress(address));
		if (!errors.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Validation error", errors));
		}
		repository.save(address);
		return ResponseEntity.status(HttpStatus.CREATED).header(HttpHeaders.LOCATION, "/files/upload/" + customerId)
				.build();
	}

	private static List<String> validateAddress(final Address address) {
		final List<String> errors = new ArrayList<>();
		final String zipCodePattern = "\\d{5}-\\d{3}";
		if (address.getZipCode() != null && !address.getZipCode().matches(zipCodePattern)) {
			errors.add("address.validator.zipCodeDoesNotMatchPattern: " + address.getZipCode());
		}
		if (address.getZipCode() == null) {
			errors.add("address.validator.zipCodeIsNull");
		}
		if (address.getStreet() == null) {
			errors.add("address.validator.streetIsNull");
		}
		if (address.getDistrict() == null) {
			errors.add("address.validator.districtIsNull");
		}
		if (address.getComplement() == null) {
			errors.add("address.validator.complementIsNull");
		}
		if (address.getCity() == null) {
			errors.add("address.validator.cityIsNull");
		}
		if (address.getState() == null) {
			errors.add("address.validator.stateIsNull");
		}
		if (address.getCustomerId() == null) {
			errors.add("address.validator.customerIdIsNull");
		}
		return errors;
	}

}
