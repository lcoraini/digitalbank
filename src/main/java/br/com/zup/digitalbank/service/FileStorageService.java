package br.com.zup.digitalbank.service;

import static br.com.zup.digitalbank.utils.Utils.getAddressByCustomerIdExample;
import static br.com.zup.digitalbank.utils.Utils.getCustomerByIdExample;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.zup.digitalbank.message.ResponseMessage;
import br.com.zup.digitalbank.model.FileDB;
import br.com.zup.digitalbank.model.ResponseFile;
import br.com.zup.digitalbank.repository.AddressRepository;
import br.com.zup.digitalbank.repository.CustomerRepository;
import br.com.zup.digitalbank.repository.FileDBRepository;

@Service
public class FileStorageService {

	@Autowired
	private FileDBRepository repository;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private AddressRepository addressRepository;

	public ResponseEntity<ResponseMessage> store(final MultipartFile file, final Integer customerId)
			throws IOException {
		final ResponseEntity<ResponseMessage> validationResponse = validateFile(file, customerId);
		if (validationResponse != null) {
			return validationResponse;
		}
		try {
			final String fileName = StringUtils.cleanPath(file.getOriginalFilename());
			final FileDB newFile = new FileDB();
			newFile.setName(fileName);
			newFile.setType(file.getContentType());
			newFile.setData(file.getBytes());
			newFile.setCustomerId(customerId);
			repository.save(newFile);
		} catch (final Exception e) {
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage("Upload error",
					Arrays.asList("upload.errorUploadingFile: " + file.getOriginalFilename())));
		}
		return ResponseEntity.status(HttpStatus.CREATED).header(HttpHeaders.LOCATION, "/proposal/" + customerId)
				.build();
	}

	private ResponseEntity<ResponseMessage> validateFile(final MultipartFile file, final Integer customerId) {
		if (file.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ResponseMessage("Validation error", Arrays.asList("upload.validator.fileNotFound")));
		}
		if (!customerRepository.exists(getCustomerByIdExample(customerId))) {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ResponseMessage("Validation error",
					Arrays.asList("upload.validator.customerNotFoundById: " + customerId)));
		}
		if (!addressRepository.exists(getAddressByCustomerIdExample(customerId))) {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ResponseMessage("Validation error",
					Arrays.asList("upload.validator.addressNotFoundByCustomerId: " + customerId)));
		}
		return null;
	}

	public FileDB findByCustomerId(final Integer customerId) {
		return repository.findByCustomerId(customerId);
	}

	public ResponseEntity<List<ResponseFile>> getFilesList() {
		final List<ResponseFile> files = repository.findAll().stream().map(dbFile -> {
			final String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/files/")
					.path(dbFile.getId().toString()).toUriString();

			return new ResponseFile(dbFile.getName(), fileDownloadUri, dbFile.getType(), dbFile.getData().length);
		}).collect(Collectors.toList());

		return ResponseEntity.status(HttpStatus.OK).body(files);
	}
}
