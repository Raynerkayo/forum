package br.com.alura.forum.controller.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import br.com.alura.forum.modelo.Topico;
import br.com.alura.forum.repository.TopicoRepository;
import lombok.Data;

@Data
public class AtualizarTopicoForm {

	@NotEmpty(message = "O Título não pode ser vazio!")
	@NotNull(message = "O Título não pode ser nulo!!!")
	private String titulo;

	@NotEmpty
	@NotNull
	private String mensagem;

	public Topico atualizar(Long id, TopicoRepository repository) {
		Topico topico = repository.getOne(id);
		topico.setMensagem(mensagem);
		topico.setTitulo(titulo);
		return topico;
	}

}
