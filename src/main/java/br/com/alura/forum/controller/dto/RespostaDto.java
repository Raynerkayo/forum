package br.com.alura.forum.controller.dto;

import java.time.LocalDateTime;

import org.springframework.boot.context.properties.ConstructorBinding;

import br.com.alura.forum.modelo.Resposta;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@ConstructorBinding
@Data
public class RespostaDto {
	private Long id;
	private String mensagem;
	private LocalDateTime dataCriacao;
	private String nomeAutor;
	
	public RespostaDto(Resposta resposta) {
		this.id = resposta.getId();
		this.mensagem = resposta.getMensagem();
		this.dataCriacao = resposta.getDataCriacao();
		this.nomeAutor = resposta.getAutor().getNome();
	}
	
}
