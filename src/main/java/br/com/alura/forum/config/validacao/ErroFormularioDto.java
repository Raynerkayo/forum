package br.com.alura.forum.config.validacao;

import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A calsse que irá moldar o que eu quero que retorne.
 * */
@Data
@ConstructorBinding
@AllArgsConstructor
public class ErroFormularioDto {

	private String campo;
	private String erro;
	
}
