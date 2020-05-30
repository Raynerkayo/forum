package br.com.alura.forum.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.alura.forum.modelo.Topico;

public interface TopicoRepository extends JpaRepository<Topico, Long> {

	// o _ indica para o SPring, que é um atributo de relação.
	// que ele deve buscar, dentro de tópico, o Atributo Curso.
	// dentro da Classe curso, o nome.
	List<Topico> findByCurso_Nome(String cursoNome);

}
