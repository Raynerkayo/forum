package br.com.alura.forum.config.validacao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * A classe é um iterceptador, sempre que houve erros, será acionada.
 * */

@RestControllerAdvice
public class ErroDeValidacaoHandler {

	/**
	 * Para usar a internacionalização do Spring Boot
	 * */
	@Autowired
	private MessageSource messageSource;
	
	/**
	 * Criar o método para identificar o tipo de exceção que o spring deverá interceptar.
	 * No caso de exceções de validação, do @Valid, será a: MethodArgumentNotValidException
	 * 
	 * @ExceptionHandler(MethodArgumentNotValidException.class) indica que vai interceptar esse tipo 
	 * de exceção.
	 * 
	 * @ResponseStatus(code = HttpStatus.BAD_REQUEST) que vai retorna um status 400. Caso não coloque 
	 * o Spring retorna um 200, tendo em vista que ele resolveu ok, a exceção.
	 *  
	 * @MethodArgumentNotValidException exception, é onde vem a mensagem do erro que aconteceu
	 * e a partir daqui eu formato o que eu quero enviar, personalizado para o usuário.
	 * 
	 * @getFieldErrors() ele retorna os erros de validação dos campos. Anotado no model.
	 *  
	 * @return List<ErroFormularioDto> vai retornar o que eu desejo que ele retorne. E não
	 * o padrão de retorno do Spring.
	 * 
	 * */
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public List<ErroFormularioDto> handle(MethodArgumentNotValidException exception) {
		
		List<ErroFormularioDto> dto = new ArrayList<>();
		
		List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
		fieldErrors.forEach(
			erro -> {
				String mensagem = messageSource.getMessage(erro, LocaleContextHolder.getLocale());
				ErroFormularioDto erroFormularioDto = new ErroFormularioDto(erro.getField(), mensagem);
				dto.add(erroFormularioDto);
			});
		
		return dto;
	}
	
}








