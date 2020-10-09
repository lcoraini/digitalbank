package br.com.zup.digitalbank.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.zup.digitalbank.message.ResponseMessage;
import br.com.zup.digitalbank.model.Customer;
import br.com.zup.digitalbank.service.CustomerService;

@RestController
@RequestMapping(path = "/customers")
public class CustomerController {

	@Autowired
	private CustomerService service;

	@GetMapping
	public ResponseEntity<List<Customer>> findAll() {
		return ResponseEntity.ok().body(service.findAll());
	}

	@GetMapping(path = { "/{id}" })
	public ResponseEntity<Customer> findById(@PathVariable final Integer id) {
		return service.findById(id).map(record -> ResponseEntity.ok().body(record))
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<ResponseMessage> create(@RequestBody final Customer customer) {
		return service.create(customer);
	}

	@GetMapping(path = "/email")
	public ResponseEntity<Customer> findByEmail(@RequestBody final String email) {
		return ResponseEntity.ok().body(service.findByEmail(email));
	}

	@GetMapping(path = "/cpf")
	public ResponseEntity<Customer> findByCpf(@RequestBody final String cpf) {
		return ResponseEntity.ok().body(service.findByCpf(cpf));
	}
}