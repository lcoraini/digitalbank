package br.com.zup.digitalbank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.zup.digitalbank.message.ResponseMessage;
import br.com.zup.digitalbank.model.Account;
import br.com.zup.digitalbank.model.Proposal;
import br.com.zup.digitalbank.service.AccountService;

@RestController
@RequestMapping(path = "/account")
public class AccountController {

	@Autowired
	private AccountService service;

	@GetMapping(path = { "/{id}" })
	public ResponseEntity<Account> findById(@PathVariable("id") final Integer id) {
		return service.findById(id).map(record -> ResponseEntity.ok().body(record))
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<ResponseMessage> create(@RequestBody final Proposal proposal) {
		return service.create(proposal);
	}
}
