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
import br.com.zup.digitalbank.repository.CustomerRepository;

public class AddressTest extends AbstractTest {

	@Autowired
	private CustomerRepository customerRepository;

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
		customerRepository.save(customer);
	}

	@After
	public void deleteCustomer() {
		final Customer customer = customerRepository.findByEmail("address@address.com");
		customerRepository.delete(customer);
	}

	@Test
	public void givenValidAddress_whenSave_thenOK_andLocation() throws Exception {
		final Customer customer = new Customer();
		customer.setName("Address OK");
		customer.setLastName("Address OK");
		customer.setCpf("111111111-22");
		customer.setEmail("addressok@addressok.com");
		customer.setBirthDate(new GregorianCalendar(1990, Calendar.FEBRUARY, 11).getTime());
		final Integer customerId = customerRepository.save(customer).getId();

		final String uri = "/address/" + customer.getId();
		final Address address = new Address();
		address.setZipCode("12345-000");
		address.setStreet("Rua");
		address.setDistrict("Bairro");
		address.setComplement("Complemento");
		address.setCity("Cidade");
		address.setState("Estado");
		address.setCustomerId(customerId);

		final String inputJson = super.mapToJson(address);
		final MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		final int status = mvcResult.getResponse().getStatus();
		assertEquals(201, status);
		final String content = mvcResult.getResponse().getHeader("Location");
		assertEquals(content, "/files/upload/" + customerId);
	}

	@Test
	public void givenNonExistentCustomer_whenSave_thenBadRequest() throws Exception {
		final String uri = "/address/99";
		final Address address = new Address();
		address.setZipCode("12345-000");
		address.setStreet("Rua");
		address.setDistrict("Bairro");
		address.setComplement("Complemento");
		address.setCity("Cidade");
		address.setState("Estado");
		address.setCustomerId(99);

		final String inputJson = super.mapToJson(address);
		final MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		final int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);
		final String content = mvcResult.getResponse().getContentAsString();
		final String expectedContent = "{\"message\":\"Validation error\",\"errors\":[\"address.validator.customerNotFoundById: 99\"]}";
		assertEquals(content, expectedContent);
	}

	@Test
	public void givenInvalidPatternZipCode_whenSave_thenBadRequest() throws Exception {
		final Customer customer = customerRepository.findByEmail("address@address.com");
		final String uri = "/address/" + customer.getId();
		final Address address = new Address();
		address.setZipCode("123456-00");
		address.setStreet("Rua");
		address.setDistrict("Bairro");
		address.setComplement("Complemento");
		address.setCity("Cidade");
		address.setState("Estado");
		address.setCustomerId(customer.getId());

		final String inputJson = super.mapToJson(address);
		final MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		final int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);
		final String content = mvcResult.getResponse().getContentAsString();
		final String expectedContent = "{\"message\":\"Validation error\",\"errors\":[\"address.validator.zipCodeDoesNotMatchPattern: 123456-00\"]}";
		assertEquals(content, expectedContent);
	}

	@Test
	public void givenNullZipCode_whenSave_thenBadRequest() throws Exception {
		final Customer customer = customerRepository.findByEmail("address@address.com");
		final String uri = "/address/" + customer.getId();
		final Address address = new Address();
		address.setZipCode(null);
		address.setStreet("Rua");
		address.setDistrict("Bairro");
		address.setComplement("Complemento");
		address.setCity("Cidade");
		address.setState("Estado");
		address.setCustomerId(customer.getId());

		final String inputJson = super.mapToJson(address);
		final MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		final int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);
		final String content = mvcResult.getResponse().getContentAsString();
		final String expectedContent = "{\"message\":\"Validation error\",\"errors\":[\"address.validator.zipCodeIsNull\"]}";
		assertEquals(content, expectedContent);
	}

	@Test
	public void givenNullStreet_whenSave_thenBadRequest() throws Exception {
		final Customer customer = customerRepository.findByEmail("address@address.com");
		final String uri = "/address/" + customer.getId();
		final Address address = new Address();
		address.setZipCode("12345-000");
		address.setStreet(null);
		address.setDistrict("Bairro");
		address.setComplement("Complemento");
		address.setCity("Cidade");
		address.setState("Estado");
		address.setCustomerId(customer.getId());

		final String inputJson = super.mapToJson(address);
		final MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		final int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);
		final String content = mvcResult.getResponse().getContentAsString();
		final String expectedContent = "{\"message\":\"Validation error\",\"errors\":[\"address.validator.streetIsNull\"]}";
		assertEquals(content, expectedContent);
	}

	@Test
	public void givenNullDistrict_whenSave_thenBadRequest() throws Exception {
		final Customer customer = customerRepository.findByEmail("address@address.com");
		final String uri = "/address/" + customer.getId();
		final Address address = new Address();
		address.setZipCode("12345-000");
		address.setStreet("Rua");
		address.setDistrict(null);
		address.setComplement("Complemento");
		address.setCity("Cidade");
		address.setState("Estado");
		address.setCustomerId(customer.getId());

		final String inputJson = super.mapToJson(address);
		final MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		final int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);
		final String content = mvcResult.getResponse().getContentAsString();
		final String expectedContent = "{\"message\":\"Validation error\",\"errors\":[\"address.validator.districtIsNull\"]}";
		assertEquals(content, expectedContent);
	}

	@Test
	public void givenNullComplement_whenSave_thenBadRequest() throws Exception {
		final Customer customer = customerRepository.findByEmail("address@address.com");
		final String uri = "/address/" + customer.getId();
		final Address address = new Address();
		address.setZipCode("12345-000");
		address.setStreet("Rua");
		address.setDistrict("Bairro");
		address.setComplement(null);
		address.setCity("Cidade");
		address.setState("Estado");
		address.setCustomerId(customer.getId());

		final String inputJson = super.mapToJson(address);
		final MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		final int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);
		final String content = mvcResult.getResponse().getContentAsString();
		final String expectedContent = "{\"message\":\"Validation error\",\"errors\":[\"address.validator.complementIsNull\"]}";
		assertEquals(content, expectedContent);
	}

	@Test
	public void givenNullCity_whenSave_thenBadRequest() throws Exception {
		final Customer customer = customerRepository.findByEmail("address@address.com");
		final String uri = "/address/" + customer.getId();
		final Address address = new Address();
		address.setZipCode("12345-000");
		address.setStreet("Rua");
		address.setDistrict("Bairro");
		address.setComplement("Complemento");
		address.setCity(null);
		address.setState("Estado");
		address.setCustomerId(customer.getId());

		final String inputJson = super.mapToJson(address);
		final MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		final int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);
		final String content = mvcResult.getResponse().getContentAsString();
		final String expectedContent = "{\"message\":\"Validation error\",\"errors\":[\"address.validator.cityIsNull\"]}";
		assertEquals(content, expectedContent);
	}

	@Test
	public void givenNullState_whenSave_thenBadRequest() throws Exception {
		final Customer customer = customerRepository.findByEmail("address@address.com");
		final String uri = "/address/" + customer.getId();
		final Address address = new Address();
		address.setZipCode("12345-000");
		address.setStreet("Rua");
		address.setDistrict("Bairro");
		address.setComplement("Complemento");
		address.setCity("Cidade");
		address.setState(null);
		address.setCustomerId(customer.getId());

		final String inputJson = super.mapToJson(address);
		final MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		final int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);
		final String content = mvcResult.getResponse().getContentAsString();
		final String expectedContent = "{\"message\":\"Validation error\",\"errors\":[\"address.validator.stateIsNull\"]}";
		assertEquals(content, expectedContent);
	}

	@SuppressWarnings("unused")
	@Test
	public void givenNullCustomerId_whenSave_thenBadRequest() throws Exception {
		final Customer customer = customerRepository.findByEmail("address@address.com");
		final String uri = "/address/" + customer.getId();
		final Address address = new Address();
		address.setZipCode("12345-000");
		address.setStreet("Rua");
		address.setDistrict("Bairro");
		address.setComplement("Complemento");
		address.setCity("Cidade");
		address.setState("Estado");
		address.setCustomerId(null);

		final String inputJson = super.mapToJson(address);
		final MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		final int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);
		final String content = mvcResult.getResponse().getContentAsString();
		final String expectedContent = "{\"message\":\"Validation error\",\"errors\":[\"address.validator.customerIdIsNull\"]}";
		assertEquals(content, expectedContent);
	}
}
