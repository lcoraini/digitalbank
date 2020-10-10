package br.com.zup.digitalbank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.zup.digitalbank.message.ResponseMessage;
import br.com.zup.digitalbank.model.Proposal;
import br.com.zup.digitalbank.service.ProposalService;

@RestController
@RequestMapping(path = "/proposal")
public class ProposalController {

	@Autowired
	private ProposalService service;

	@GetMapping(path = { "/{id}" })
	public ResponseEntity<Proposal> findById(@PathVariable("id") final Integer id) {
		return service.findById(id).map(record -> ResponseEntity.ok().body(record))
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping(path = "/{customerId}")
	public ResponseEntity<ResponseMessage> create(@PathVariable("customerId") final Integer customerId) {
		return service.create(customerId);
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<ResponseMessage> update(@PathVariable("id") final Integer id,
			@RequestBody final Proposal proposal) {
		return service.update(id, proposal);
	}
}
