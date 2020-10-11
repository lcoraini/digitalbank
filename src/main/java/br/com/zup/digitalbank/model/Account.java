package br.com.zup.digitalbank.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Integer agency;
	private Integer accountNumber;
	private Integer bankNumber;
	@OneToOne(optional = false, targetEntity = Proposal.class)
	@JoinColumn(name = "proposal_id", nullable = false)
	private Proposal proposal;
	private double accountBalance;
}
