package br.com.alura.forum.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.alura.forum.modelo.Curso;

public interface CursoRepository extends JpaRepository<Curso, Long>{
	//criar tbm com um select. 
	//@Query("SELECT t FROM Topico t WHERE t.curso.nome = :nomeCurso")
        //List<Topico> carregarPorNomeDoCurso(@Param("nomeCurso")(String nomeCurso);
	Curso findByNome(String nome);
	
}
