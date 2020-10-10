package br.com.zup.digitalbank.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.zup.digitalbank.message.ResponseMessage;
import br.com.zup.digitalbank.model.FileDB;
import br.com.zup.digitalbank.model.ResponseFile;
import br.com.zup.digitalbank.service.FileStorageService;

@RestController
@RequestMapping(path = "/files")
public class FileController {

	@Autowired
	private FileStorageService service;

	@PostMapping("/upload/{customerId}")
	public ResponseEntity<ResponseMessage> uploadFiles(@RequestParam("file") final MultipartFile file,
			@PathVariable("customerId") final Integer customerId) throws IOException {
		return service.store(file, customerId);
	}

	@GetMapping(path = "/customer/{customerId}")
	public ResponseEntity<FileDB> findByCustomer(@PathVariable("customerId") final Integer customerId) {
		return ResponseEntity.ok().body(service.findByCustomerId(customerId));
	}

	@GetMapping("/all")
	public ResponseEntity<List<ResponseFile>> getFilesList() {
		return service.getFilesList();
	}
}
