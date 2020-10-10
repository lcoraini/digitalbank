package br.com.zup.digitalbank;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import br.com.zup.digitalbank.model.Address;
import br.com.zup.digitalbank.model.Customer;
import br.com.zup.digitalbank.model.FileDB;
import br.com.zup.digitalbank.repository.AddressRepository;
import br.com.zup.digitalbank.repository.CustomerRepository;
import br.com.zup.digitalbank.repository.FileDBRepository;

public class FileDBTest extends AbstractTest {

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

	@SuppressWarnings("deprecation")
	@Test
	public void givenValidFile_whenSave_thenOK_andLocation() throws Exception {
		final Customer customer = customerRepository.findByEmail("address@address.com");
		final String uri = "http://localhost:8080/files/upload/" + customer.getId();

		final MockMultipartFile metadata = new MockMultipartFile("file", "file", MediaType.IMAGE_JPEG_VALUE,
				"file".getBytes(StandardCharsets.UTF_8));
		final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.fileUpload(uri).file(metadata);

		final MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		final MvcResult result = mockMvc.perform(builder).andExpect(status().isCreated()).andReturn();
		final String content = result.getResponse().getHeader("Location");
		assertEquals(content, "/proposal/" + customer.getId());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void givenNonExistentCustomer_whenSave_thenBadRequest() throws Exception {
		final String uri = "http://localhost:8080/files/upload/99";

		final MockMultipartFile metadata = new MockMultipartFile("file", "file", MediaType.IMAGE_JPEG_VALUE,
				"file".getBytes(StandardCharsets.UTF_8));
		final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.fileUpload(uri).file(metadata);

		final MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		final MvcResult result = mockMvc.perform(builder).andExpect(status().isUnprocessableEntity()).andReturn();
		final String content = result.getResponse().getContentAsString();
		assertEquals(content,
				"{\"message\":\"Validation error\",\"errors\":[\"upload.validator.customerNotFoundById: 99\"]}");
	}

	@SuppressWarnings("deprecation")
	@Test
	public void givenNonExistentCustomerAddress_whenSave_thenBadRequest() throws Exception {
		final Customer customer = customerRepository.findByEmail("address@address.com");
		final Address address = addressRepository.findByCustomerId(customer.getId());
		addressRepository.delete(address);
		final String uri = "http://localhost:8080/files/upload/" + customer.getId();

		final MockMultipartFile metadata = new MockMultipartFile("file", "file", MediaType.IMAGE_JPEG_VALUE,
				"file".getBytes(StandardCharsets.UTF_8));
		final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.fileUpload(uri).file(metadata);

		final MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		final MvcResult result = mockMvc.perform(builder).andExpect(status().isUnprocessableEntity()).andReturn();
		final String content = result.getResponse().getContentAsString();
		final String expectedContent = "{\"message\":\"Validation error\",\"errors\":[\"upload.validator.addressNotFoundByCustomerId: 1\"]}";
		assertEquals(content, expectedContent);
	}
}
