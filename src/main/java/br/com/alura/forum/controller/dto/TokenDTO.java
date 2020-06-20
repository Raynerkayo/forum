package br.com.alura.forum.controller.dto;

import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@ConstructorBinding
public class TokenDTO {

	private String token;
	private String tipo;
	
}
