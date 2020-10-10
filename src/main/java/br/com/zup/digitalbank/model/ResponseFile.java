package br.com.zup.digitalbank.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseFile {

	private String name;
	private String url;
	private String type;
	private long size;
}
