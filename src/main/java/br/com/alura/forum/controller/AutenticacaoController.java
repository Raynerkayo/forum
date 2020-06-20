package br.com.alura.forum.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.alura.forum.config.security.TokenService;
import br.com.alura.forum.controller.dto.TokenDTO;
import br.com.alura.forum.controller.form.LoginForm;

@RestController
@RequestMapping("/auth")
public class AutenticacaoController {
	
	private static final String TIPO_AUTENTICACAO = "Bearer";

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private TokenService tokenService;
	
	@PostMapping
	public ResponseEntity<?> autenticar(@RequestBody @Valid LoginForm loginForm){
		//loginForm.converter(), converte esses dados de usuario e senha. E me retorna esses dados.
		UsernamePasswordAuthenticationToken dadosLogin = loginForm.converter();

		//autentica os dadosLogin, e retorna nesse autentication;
		//agora, pega o authentication e manda gerar um token JWT
		try {
			Authentication authentication = authenticationManager.authenticate(dadosLogin);
			String token = tokenService.gerarToken(authentication);
			return ResponseEntity.ok(new TokenDTO(token, TIPO_AUTENTICACAO));
		} catch(AuthenticationException e) {
			return ResponseEntity.badRequest().build();
		}
		
		
	}

}
