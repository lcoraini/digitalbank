package br.com.zup.digitalbank.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.zup.digitalbank.message.ResponseMessage;
import br.com.zup.digitalbank.model.Address;
import br.com.zup.digitalbank.service.AddressService;

@RestController
@RequestMapping(path = "/address")
public class AddressController {

	private final AddressService service;

	AddressController(final AddressService addressService) {
		this.service = addressService;
	}

	@GetMapping
	public ResponseEntity<List<Address>> findAll() {
		return ResponseEntity.ok().body(service.findAll());
	}

	@GetMapping(path = "/customer/{customerId}")
	public ResponseEntity<List<Address>> findByCustomer(@PathVariable final Integer customerId) {
		return ResponseEntity.ok().body(service.findByCustomerId(customerId));
	}

	@PostMapping
	public ResponseEntity<ResponseMessage> create(@RequestBody final Address address) {
		return service.create(address);
	}
}
