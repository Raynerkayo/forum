package br.com.alura.forum.controller.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import br.com.alura.forum.modelo.Curso;
import br.com.alura.forum.modelo.Topico;
import br.com.alura.forum.repository.CursoRepository;
import lombok.Data;

@Data
public class TopicoForm {

	@NotEmpty(message = "O Título não pode ser vazio!") 
	@NotNull(message = "O Título não pode ser nulo!!!")
	private String titulo;
	
	@NotEmpty @NotNull
	private String mensagem;
	
	@NotEmpty @NotNull
	private String nomeCurso;

	public Topico converter(CursoRepository cursoRepository) {

		Curso curso = cursoRepository.findByNome(nomeCurso);

		return new Topico(titulo, mensagem, curso);
	}

}
