package br.com.alura.forum.controller;

import java.net.URI;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

	@GetMapping("/listar")
	public List<TopicoDto> topicos() {
		List<Topico> topicos = topicoRepository.findAll();
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
	public DetalhesTopicoDto detalhar(@PathVariable Long id) {
		Topico topico = topicoRepository.getOne(id);
		return new DetalhesTopicoDto(topico);
	}

	@PutMapping("/atualizar/{id}")
	@Transactional
	public ResponseEntity<TopicoDto> atualizar(@PathVariable Long id,
			@RequestBody @Valid AtualizarTopicoForm topicoForm) {
		Topico topico = topicoForm.atualizar(id, topicoRepository);

		// Entendendo melhor: Ele preenche o topico, aí em cima.
		// No retorno, passo o topico para TopicoDto, e o TopicoDto, vai
		// "pegar" só os atributos que o TopicoDto tem. E então, ele retorna isso.
		return ResponseEntity.ok(new TopicoDto(topico));
	}

	@DeleteMapping("/remover/{id}")
	public ResponseEntity<?> remover(@PathVariable Long id) {

		topicoRepository.deleteById(id);

		return ResponseEntity.ok().build();
	}

}
