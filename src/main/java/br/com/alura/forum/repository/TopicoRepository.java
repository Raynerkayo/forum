package br.com.alura.forum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.alura.forum.modelo.Topico;

public interface TopicoRepository extends JpaRepository<Topico, Long> {

	// o _ indica para o SPring, que é um atributo de relação.
	// que ele deve buscar, dentro de tópico, o Atributo Curso.
	// dentro da Classe curso, o nome.
	Page<Topico> findByCurso_Nome(String cursoNome, Pageable pageable);

}
