package br.com.zup.digitalbank.utils;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import br.com.zup.digitalbank.model.Customer;

public class Utils {

	public static Example<Customer> getCustomerExample(final Integer customerId) {
		final Customer customer = new Customer();
		customer.setId(customerId);
		final ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("id",
				ExampleMatcher.GenericPropertyMatchers.exact());

		return Example.of(customer, matcher);
	}
}
