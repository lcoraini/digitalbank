package br.com.zup.digitalbank;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import br.com.zup.digitalbank.model.Address;
import br.com.zup.digitalbank.model.Customer;
import br.com.zup.digitalbank.model.FileDB;
import br.com.zup.digitalbank.model.Proposal;
import br.com.zup.digitalbank.repository.AddressRepository;
import br.com.zup.digitalbank.repository.CustomerRepository;
import br.com.zup.digitalbank.repository.FileDBRepository;
import br.com.zup.digitalbank.repository.ProposalRepository;

public class ProposalTest extends AbstractTest {

	@Autowired
	private ProposalRepository repository;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private AddressRepository addressRepository;
	@Autowired
	private FileDBRepository fileRepository;

	@Override
	@Before
	public void setUp() {
		super.setUp();

		final Customer customer = new Customer();
		customer.setName("Address");
		customer.setLastName("Address");
		customer.setCpf("111111111-00");
		customer.setEmail("address@address.com");
		customer.setBirthDate(new GregorianCalendar(1990, Calendar.FEBRUARY, 11).getTime());
		final Customer newCustomer = customerRepository.save(customer);

		final Address address = new Address();
		address.setZipCode("12345-000");
		address.setStreet("Rua");
		address.setDistrict("Bairro");
		address.setComplement("Complemento");
		address.setCity("Cidade");
		address.setState("Estado");
		address.setCustomerId(newCustomer.getId());
		addressRepository.save(address);

		final FileDB file = new FileDB();
		file.setName("File");
		file.setType("File");
		file.setData("file".getBytes());
		file.setCustomerId(newCustomer.getId());
		fileRepository.save(file);
	}

	@After
	public void deleteCustomerAndAddress() {
		final Customer customer = customerRepository.findByEmail("address@address.com");
		final Address address = addressRepository.findByCustomerId(customer.getId());
		final FileDB file = fileRepository.findByCustomerId(customer.getId());
		if (file != null) {
			fileRepository.delete(file);
		}
		if (address != null) {
			addressRepository.delete(address);
		}
		customerRepository.delete(customer);
	}

	@Test
	public void givenValidData_whenSave_thenOK_andLocation() throws Exception {
		final Customer customer = customerRepository.findByEmail("address@address.com");
		final String uri = "http://localhost:8080/proposal/" + customer.getId();

		assertEquals(repository.count(), 0);
		final MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)).andReturn();
		final int status = mvcResult.getResponse().getStatus();
		assertEquals(201, status);
		assertEquals(repository.count(), 1);
		repository.deleteAll();
	}

	@Test
	public void givenNonExistentCustomer_whenSave_thenUnprocessableEntity() throws Exception {
		final String uri = "http://localhost:8080/proposal/99";
		final MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)).andReturn();
		final int status = mvcResult.getResponse().getStatus();
		assertEquals(422, status);
		final String content = mvcResult.getResponse().getContentAsString();
		final String expectedContent = "{\"message\":\"Error\",\"errors\":[\"proposal.validator.customerNotFoundById: 99\"]}";
		assertEquals(content, expectedContent);
	}

	@Test
	public void givenNonExistentAddress_whenSave_thenUnprocessableEntity() throws Exception {
		final Customer customer = customerRepository.findByEmail("address@address.com");
		final Address address = addressRepository.findByCustomerId(customer.getId());
		addressRepository.delete(address);
		final String uri = "http://localhost:8080/proposal/" + customer.getId();

		final MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)).andReturn();
		final int status = mvcResult.getResponse().getStatus();
		assertEquals(422, status);
		final String content = mvcResult.getResponse().getContentAsString();
		final String expectedContent = "{\"message\":\"Error\",\"errors\":[\"proposal.validator.addressNotFoundByCustomerId: "
				+ customer.getId() + "\"]}";
		assertEquals(content, expectedContent);
	}

	@Test
	public void givenNonExistentFile_whenSave_thenUnprocessableEntity() throws Exception {
		final Customer customer = customerRepository.findByEmail("address@address.com");
		final FileDB file = fileRepository.findByCustomerId(customer.getId());
		fileRepository.delete(file);
		final String uri = "http://localhost:8080/proposal/" + customer.getId();

		final MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)).andReturn();
		final int status = mvcResult.getResponse().getStatus();
		assertEquals(422, status);
		final String content = mvcResult.getResponse().getContentAsString();
		final String expectedContent = "{\"message\":\"Error\",\"errors\":[\"proposal.validator.fileNotFoundByCustomerId: "
				+ customer.getId() + "\"]}";
		assertEquals(content, expectedContent);
	}

	@Test
	public void givenUserAcceptsProposal_whenUpdate_thenOK_andLocation() throws Exception {
		final Customer customer = customerRepository.findByEmail("address@address.com");
		final String createUri = "http://localhost:8080/proposal/" + customer.getId();

		final MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(createUri)).andReturn();
		final int status = mvcResult.getResponse().getStatus();
		assertEquals(201, status);

		final Proposal proposal = repository.findAll().get(0);
		assertEquals(proposal.isAccepted(), false);
		proposal.setAccepted(true);

		final String updateUri = "http://localhost:8080/proposal/" + proposal.getId();
		final String inputJson = super.mapToJson(proposal);
		final MvcResult mvcUpdateResult = mvc.perform(
				MockMvcRequestBuilders.put(updateUri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();
		final int updateStatus = mvcUpdateResult.getResponse().getStatus();
		assertEquals(200, updateStatus);
		final String header = mvcUpdateResult.getResponse().getHeader("Location");
		assertEquals(header, "/account/" + proposal.getId());
		final String body = mvcUpdateResult.getResponse().getContentAsString();
		final String expectedBody = "{\"message\":\"A new account will be created and an email will be sended\",\"errors\":[]}";
		assertEquals(body, expectedBody);
		final Proposal updatedProposal = repository.findAll().get(0);
		assertEquals(updatedProposal.isAccepted(), true);
		repository.deleteAll();
	}

	@Test
	public void givenUserDoesNotAcceptsProposal_whenUpdate_thenOK_andLocation() throws Exception {
		final Customer customer = customerRepository.findByEmail("address@address.com");
		final String createUri = "http://localhost:8080/proposal/" + customer.getId();

		final MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(createUri)).andReturn();
		final int status = mvcResult.getResponse().getStatus();
		assertEquals(201, status);

		final Proposal proposal = repository.findAll().get(0);
		assertEquals(proposal.isAccepted(), false);

		final String updateUri = "http://localhost:8080/proposal/" + proposal.getId();
		final String inputJson = super.mapToJson(proposal);
		final MvcResult mvcUpdateResult = mvc.perform(
				MockMvcRequestBuilders.put(updateUri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();
		final int updateStatus = mvcUpdateResult.getResponse().getStatus();
		assertEquals(200, updateStatus);
		final String header = mvcUpdateResult.getResponse().getHeader("Location");
		assertEquals(header, "/account/" + proposal.getId());
		final String body = mvcUpdateResult.getResponse().getContentAsString();
		final String expectedBody = "{\"message\":\"Account will not be created and an email will be sended\",\"errors\":[]}";
		assertEquals(body, expectedBody);
		final Proposal updatedProposal = repository.findAll().get(0);
		assertEquals(updatedProposal.isAccepted(), false);
		repository.deleteAll();
	}
}
