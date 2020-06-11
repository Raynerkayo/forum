package br.com.alura.forum.controller;

import java.net.URI;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
	 * @param pageable é uma outra forma de fazer a paginação no spring. Em relação
	 *                 ao listar/curso, aqui basta colocar um o objeto e na url de
	 *                 requisição, o cliente, colocar os parametros em inglês, que
	 *                 são: page=0, size=5, sort=id ou outro atributo pelo qual deve
	 *                 ser atualizado. desc, para decrescente. Ou, asc, para ordenar
	 *                 crescente. page=0&size=5&sort=id,asc
	 *                 page=0&size=30&sort=id,asc&sort=dataCriacao,desc pode tbm
	 *                 ordernar por mais de um campo. É possível tbm fazer no
	 *                 /listar/curso, mas usando mais código. Caso não venha os
	 *                 parametro de ordenação, podemos colocar o default.
	 * 
	 * @PageableDefault é retornado uma paginação default.
	 * 
	 * @Cacheable(value = "listaDeTopicos") isso vai habilitar o uso de cache, e o
	 *                  value é como se fosse o identificador único desse cache. Se
	 *                  passar algum parametro, ele verifica se já tem resultado em
	 *                  cache com esse parametro, senão ele faz a consulta. Se sim,
	 *                  ele retorna o valor em cache. Mas, é bom mutilizar, em
	 *                  coisas que raramente são modificadas. Como uma tabela de
	 *                  estados. Que geralmente (ou nunca) é atualizado. Para evitar
	 *                  buscar do banco.
	 * 
	 * @return topicosDto
	 * 
	 */
	@GetMapping("/listar")
	@Cacheable(value = "listaDeTopicos")
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

	/**
	 * 
	 * @CacheEvict é usado em métodos que eu preciso apagar o cache, pois irá
	 *             modificar o valor e o cache precisa, quando chamado, ter esses
	 *             valores totais. value, é para dizer qual cache será apagado.
	 *             allEntries, para dizer que será apagado do cache. CacheEvict
	 *             deverá ser usado, a depender das regras de negócio. Geralmente,
	 *             ao mudar os dados da base, por exemplo. Com inserção, remoção ou
	 *             atualização.
	 * 
	 * @return topicosDto
	 * 
	 */
	@CacheEvict(value = "listaDeTopicos", allEntries = true)
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
