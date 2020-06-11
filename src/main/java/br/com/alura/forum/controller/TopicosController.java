package br.com.alura.forum.controller;

import java.net.URI;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.alura.forum.controller.dto.DetalhesTopicoDto;
import br.com.alura.forum.controller.dto.TopicoDto;
import br.com.alura.forum.controller.form.AtualizarTopicoForm;
import br.com.alura.forum.controller.form.TopicoForm;
import br.com.alura.forum.modelo.Topico;
import br.com.alura.forum.repository.CursoRepository;
import br.com.alura.forum.repository.TopicoRepository;

@RestController
@RequestMapping("/topicos")
public class TopicosController {

	@Autowired
	private TopicoRepository topicoRepository;

	@Autowired
	private CursoRepository cursoRepository;

	/**
	 * 
	 * @param pageable é uma outra forma de fazer a paginação no spring. E deve ser
	 *                 habilitado o seu módulo em Main: @EnableSpringDataWebSupport.
	 *                 Em relação ao listar/curso, aqui basta colocar um o objeto e
	 *                 na url de requisição, o cliente, colocar os parametros em
	 *                 inglês, que são: page=0, size=5, sort=id ou outro atributo
	 *                 pelo qual deve ser atualizado. desc, para decrescente. Ou,
	 *                 asc, para ordenar crescente. page=0&size=5&sort=id,asc
	 *                 page=0&size=30&sort=id,asc&sort=dataCriacao,desc pode tbm
	 *                 ordernar por mais de um campo. É possível tbm fazer no
	 *                 /listar/curso, mas usando mais código. Caso não venha os
	 *                 parametro de ordenação, podemos colocar o default.
	 * 
	 * @PageableDefault é retornado uma paginação default.
	 * 
	 * @return topicosDto
	 * 
	 */
	@GetMapping("/listar")
	public Page<TopicoDto> topicos(
			@PageableDefault(sort = "id", direction = Direction.DESC, page = 0, size = 5) Pageable pageable) {
		// @RequestParam -> Ei, Spring, o parametro dessa requisição get, vai vir na
		// url.

		Page<Topico> topicos = topicoRepository.findAll(pageable);
		return TopicoDto.converter(topicos);
	}

	/**
	 * 
	 * @param nomeCurso recebe o nome do curso para ser pesquisado.
	 * @param pagina    a pagina retornada da paginação.
	 * @param qtd       a quantidade de página a serem retornadas.
	 * @param ordencao  ordenar por qual campo. Se id, data etc.
	 * @return topicosDto
	 * 
	 */
	@GetMapping("/listar/curso")
	public Page<TopicoDto> topicosCurso(@RequestParam String nomeCurso, @RequestParam int pagina, @RequestParam int qtd,
			@RequestParam String ordenacao) {
		// @RequestParam -> Ei, Spring, o parametro dessa requisição get, vai vir na
		// url.

		Pageable pageable = PageRequest.of(pagina, qtd, Direction.ASC, ordenacao);

		Page<Topico> topicos = topicoRepository.findByCurso_Nome(nomeCurso, pageable);
		return TopicoDto.converter(topicos);
	}

	@PostMapping("/cadastrar")
	public ResponseEntity<TopicoDto> cadastar(@RequestBody @Valid TopicoForm topicoForm, UriComponentsBuilder builder) {
		// @RequestBody -> Ei, Spring, o parametro dessa requisição post, vai vir no
		// corpo da mensagem
		// e não na url.

		Topico topico = topicoForm.converter(cursoRepository);
		topicoRepository.save(topico);
		URI uri = builder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
		return ResponseEntity.created(uri).body(new TopicoDto(topico));
	}

	@GetMapping("/{id}")
	public ResponseEntity<DetalhesTopicoDto> detalhar(@PathVariable Long id) {
		Optional<Topico> topico = topicoRepository.findById(id);
		if (topico.isPresent()) {
			return ResponseEntity.ok(new DetalhesTopicoDto(topico.get()));
		}

		return ResponseEntity.notFound().build();
	}

	@PutMapping("/atualizar/{id}")
	@Transactional
	public ResponseEntity<TopicoDto> atualizar(@PathVariable Long id,
			@RequestBody @Valid AtualizarTopicoForm topicoForm) {
		// Entendendo melhor: Ele preenche o topico, aí em cima.
		// No retorno, passo o topico para TopicoDto, e o TopicoDto, vai
		// "pegar" só os atributos que o TopicoDto tem. E então, ele retorna isso.

		Optional<Topico> optional = topicoRepository.findById(id);
		if (optional.isPresent()) {
			Topico topico = topicoForm.atualizar(id, topicoRepository);
			return ResponseEntity.ok(new TopicoDto(topico));
		}

		return ResponseEntity.notFound().build();
	}

	@DeleteMapping("/remover/{id}")
	public ResponseEntity<?> remover(@PathVariable Long id) {
		Optional<Topico> optional = topicoRepository.findById(id);
		if (optional.isPresent()) {
			topicoRepository.deleteById(id);
			return ResponseEntity.ok().build();
		}

		return ResponseEntity.notFound().build();
	}

}
