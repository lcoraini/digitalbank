package br.com.zup.digitalbank;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import br.com.zup.digitalbank.model.Customer;

public class CustomerTest extends AbstractTest {

	@Override
	@Before
	public void setUp() {
		super.setUp();
	}

	@Test
	public void givenValidCustomer_whenSave_thenOK_andLocation() throws Exception {
		final String uri = "/customers";
		final Customer customer = new Customer();
		customer.setName("Test 1");
		customer.setLastName("Test 1");
		customer.setCpf("123456789-00");
		customer.setEmail("test1@test1.com");
		customer.setBirthDate(new GregorianCalendar(1990, Calendar.FEBRUARY, 11).getTime());

		final String inputJson = super.mapToJson(customer);
		final MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		final int status = mvcResult.getResponse().getStatus();
		assertEquals(201, status);
		final String content = mvcResult.getResponse().getHeader("Location");
		assertEquals(content, "/address/1");
	}

	@Test
	public void givenRepeatedEmail_whenSave_thenBadRequest() throws Exception {
		final String uri = "/customers";
		final Customer customer = new Customer();
		customer.setName("Test 2");
		customer.setLastName("Test 2");
		customer.setCpf("123456789-01");
		customer.setEmail("test1@test1.com");
		customer.setBirthDate(new GregorianCalendar(1990, Calendar.FEBRUARY, 11).getTime());

		final String inputJson = super.mapToJson(customer);
		final MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		final int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);
		final String content = mvcResult.getResponse().getContentAsString();
		final String expectedContent = "{\"message\":\"Validation error\",\"errors\":[\"customer.validator.emailAlreadyExists: test1@test1.com\"]}";
		assertEquals(content, expectedContent);
	}

	@Test
	public void givenRepeatedCPf_whenSave_thenBadRequest() throws Exception {
		final String uri = "/customers";
		final Customer customer = new Customer();
		customer.setName("Test 2");
		customer.setLastName("Test 2");
		customer.setCpf("123456789-00");
		customer.setEmail("test2@test2.com");
		customer.setBirthDate(new GregorianCalendar(1990, Calendar.FEBRUARY, 11).getTime());

		final String inputJson = super.mapToJson(customer);
		final MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		final int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);
		final String content = mvcResult.getResponse().getContentAsString();
		final String expectedContent = "{\"message\":\"Validation error\",\"errors\":[\"customer.validator.cpfAlreadyExists: 123456789-00\"]}";
		assertEquals(content, expectedContent);
	}

	@Test
	public void givenInvalidPatternCPf_whenSave_thenBadRequest() throws Exception {
		final String uri = "/customers";
		final Customer customer = new Customer();
		customer.setName("Test 2");
		customer.setLastName("Test 2");
		customer.setCpf("12345678-000");
		customer.setEmail("test2@test2.com");
		customer.setBirthDate(new GregorianCalendar(1990, Calendar.FEBRUARY, 11).getTime());

		final String inputJson = super.mapToJson(customer);
		final MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		final int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);
		final String content = mvcResult.getResponse().getContentAsString();
		final String expectedContent = "{\"message\":\"Validation error\",\"errors\":[\"customer.validator.cpfDoesNotMatchPattern: 12345678-000\"]}";
		assertEquals(content, expectedContent);
	}

	@Test
	public void givenNullName_whenSave_thenBadRequest() throws Exception {
		final String uri = "/customers";
		final Customer customer = new Customer();
		final String nullField = null;
		customer.setName(nullField);
		customer.setLastName("Test 2");
		customer.setCpf("123456789-01");
		customer.setEmail("test2@test2.com");
		customer.setBirthDate(new GregorianCalendar(1990, Calendar.FEBRUARY, 11).getTime());

		final String inputJson = super.mapToJson(customer);
		final MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		final int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);
		final String content = mvcResult.getResponse().getContentAsString();
		final String expectedContent = "{\"message\":\"Validation error\",\"errors\":[\"customer.validator.nameIsNull\"]}";
		assertEquals(content, expectedContent);
	}

	@Test
	public void givenNullLastName_whenSave_thenBadRequest() throws Exception {
		final String uri = "/customers";
		final Customer customer = new Customer();
		final String nullField = null;
		customer.setName("Test 2");
		customer.setLastName(nullField);
		customer.setCpf("123456789-01");
		customer.setEmail("test2@test2.com");
		customer.setBirthDate(new GregorianCalendar(1990, Calendar.FEBRUARY, 11).getTime());

		final String inputJson = super.mapToJson(customer);
		final MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		final int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);
		final String content = mvcResult.getResponse().getContentAsString();
		final String expectedContent = "{\"message\":\"Validation error\",\"errors\":[\"customer.validator.lastNameIsNull\"]}";
		assertEquals(content, expectedContent);
	}

	@Test
	public void givenNullCpf_whenSave_thenBadRequest() throws Exception {
		final String uri = "/customers";
		final Customer customer = new Customer();
		final String nullField = null;
		customer.setName("Test 2");
		customer.setLastName("Test 2");
		customer.setCpf(nullField);
		customer.setEmail("test2@test2.com");
		customer.setBirthDate(new GregorianCalendar(1990, Calendar.FEBRUARY, 11).getTime());

		final String inputJson = super.mapToJson(customer);
		final MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		final int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);
		final String content = mvcResult.getResponse().getContentAsString();
		final String expectedContent = "{\"message\":\"Validation error\",\"errors\":[\"customer.validator.cpfIsNull\"]}";
		assertEquals(content, expectedContent);
	}

	@Test
	public void givenNullEmail_whenSave_thenBadRequest() throws Exception {
		final String uri = "/customers";
		final Customer customer = new Customer();
		final String nullField = null;
		customer.setName("Test 2");
		customer.setLastName("Test 2");
		customer.setCpf("123456789-01");
		customer.setEmail(nullField);
		customer.setBirthDate(new GregorianCalendar(1990, Calendar.FEBRUARY, 11).getTime());

		final String inputJson = super.mapToJson(customer);
		final MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		final int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);
		final String content = mvcResult.getResponse().getContentAsString();
		final String expectedContent = "{\"message\":\"Validation error\",\"errors\":[\"customer.validator.emailIsNull\"]}";
		assertEquals(content, expectedContent);
	}

	@Test
	public void givenNullBirthDate_whenSave_thenBadRequest() throws Exception {
		final String uri = "/customers";
		final Customer customer = new Customer();
		customer.setName("Test 2");
		customer.setLastName("Test 2");
		customer.setCpf("123456789-01");
		customer.setEmail("test2@test2.com");
		customer.setBirthDate(null);

		final String inputJson = super.mapToJson(customer);
		final MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		final int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);
		final String content = mvcResult.getResponse().getContentAsString();
		final String expectedContent = "{\"message\":\"Validation error\",\"errors\":[\"customer.validator.birthDateIsNull\"]}";
		assertEquals(content, expectedContent);
	}

	@Test
	public void givenUnderAgeCustomer_whenSave_thenBadRequest() throws Exception {
		final String uri = "/customers";
		final Customer customer = new Customer();
		customer.setName("Test 2");
		customer.setLastName("Test 2");
		customer.setCpf("123456789-01");
		customer.setEmail("test2@test2.com");
		final Date date = new GregorianCalendar(2019, Calendar.FEBRUARY, 11).getTime();
		customer.setBirthDate(date);

		final String inputJson = super.mapToJson(customer);
		final MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		final int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);
		final String content = mvcResult.getResponse().getContentAsString();
		final String expectedContent = "{\"message\":\"Validation error\",\"errors\":[\"customer.validator.underageCustomer: Mon Feb 11 00:00:00 BRST 2019\"]}";
		assertEquals(content, expectedContent);
	}

	@Test
	public void givenBirthDateInFuture_whenSave_thenBadRequest() throws Exception {
		final String uri = "/customers";
		final Customer customer = new Customer();
		customer.setName("Test 2");
		customer.setLastName("Test 2");
		customer.setCpf("123456789-01");
		customer.setEmail("test2@test2.com");
		final Date date = new GregorianCalendar(2099, Calendar.FEBRUARY, 11).getTime();
		customer.setBirthDate(date);

		final String inputJson = super.mapToJson(customer);
		final MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		final int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);
		final String content = mvcResult.getResponse().getContentAsString();
		final String expectedContent = "{\"message\":\"Validation error\",\"errors\":[\"customer.validator.birthDateNotInPast: Wed Feb 11 00:00:00 BRST 2099\"]}";
		assertEquals(content, expectedContent);
	}

	@Test
	public void givenInvalidPatternEmail_whenSave_thenBadRequest() throws Exception {
		final String uri = "/customers";
		final Customer customer = new Customer();
		customer.setName("Test 2");
		customer.setLastName("Test 2");
		customer.setCpf("123456789-01");
		customer.setEmail("test2.test2.com");
		customer.setBirthDate(new GregorianCalendar(1990, Calendar.FEBRUARY, 11).getTime());

		final String inputJson = super.mapToJson(customer);
		final MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		final int status = mvcResult.getResponse().getStatus();
		assertEquals(400, status);
		final String content = mvcResult.getResponse().getContentAsString();
		final String expectedContent = "{\"message\":\"Validation error\",\"errors\":[\"customer.validator.emailIsInvalid: test2.test2.com\"]}";
		assertEquals(content, expectedContent);
	}
}
