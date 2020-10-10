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
public class Proposal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@OneToOne(optional = false, targetEntity = Customer.class)
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;
	@OneToOne(optional = false, targetEntity = Address.class)
	@JoinColumn(name = "address_id", nullable = false)
	private Address address;
	@OneToOne(optional = false, targetEntity = FileDB.class)
	@JoinColumn(name = "file_id", nullable = false)
	private FileDB fileDB;
	private boolean accepted;
}
