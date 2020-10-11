package br.com.zup.digitalbank.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.zup.digitalbank.message.ResponseMessage;
import br.com.zup.digitalbank.model.Account;
import br.com.zup.digitalbank.model.Proposal;
import br.com.zup.digitalbank.repository.AccountRepository;
import br.com.zup.digitalbank.repository.ProposalRepository;
import br.com.zup.digitalbank.utils.EmailServiceImpl;

@Service
public class AccountService {

	@Autowired
	private AccountRepository repository;
	@Autowired
	private ProposalRepository proposalRepository;
	@Autowired
	private EmailServiceImpl emailService;
	@Value("${email.sender.from}")
	private String emailFrom;

	public Optional<Account> findById(final Integer id) {
		return repository.findById(id);
	}

	public ResponseEntity<ResponseMessage> create(final Proposal proposal) {
		final Optional<Proposal> optionalProposal = proposalRepository.findById(proposal.getId());
		if (!optionalProposal.isPresent()) {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ResponseMessage("Error",
					Arrays.asList("proposal.validator.proposalNotFoundById: " + proposal.getId())));
		}
		final Proposal persistentProposal = optionalProposal.get();
		if (!persistentProposal.isAccepted()) {
			final String text = getEmailForNotAcceptedByCustomer(persistentProposal.getCustomer().getName());
			if (emailFrom != null) {
				emailService.sendSimpleMessage(emailFrom, persistentProposal.getCustomer().getEmail(),
						"Digital Bank - Vem com a gente, vai?", text);
			}
			return ResponseEntity.status(HttpStatus.OK)
					.body(new ResponseMessage(
							"Email sended for customer: " + persistentProposal.getCustomer().getName(),
							Collections.emptyList()));
		}
		// TODO - fluxo de criação quando usuário aceita a proposta
		return null;
	}

	private String getEmailForNotAcceptedByCustomer(final String customerName) {
		return "Prezado(a) " + customerName + "\n\n" + "Tem certeza que não quer abrir a sua conta com a gente? \n"
				+ "Conseguimos condições muito especiais para você! \n\n" + "Até breve! \n\n" + "Gerência \n\n"
				+ "Digital Bank";
	}
}
