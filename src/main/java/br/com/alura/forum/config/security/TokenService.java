package br.com.alura.forum.config.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.com.alura.forum.modelo.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenService {

	@Value("${forum.jwt.secret}")
	private String secret;
	
	@Value("${forum.jwt.expiration}")
	private String expiration;
	
	public String gerarToken(Authentication authentication) {
		Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
		Date hoje = new Date();
		Date dataExpiracao = new Date(hoje.getTime() + Long.valueOf(expiration));
		
		//builder, cria um objeto para setar e construir o token.
		//setIssuer, quem está gerando esse token. Qual aplicação.
		//setSubject, de quem é esse token. No caso, usuario logado.
		//setIssuedAt, data de criação do token.
		//setExpiration, quando vai expirar.
		//signWith, para criptografar o token.
		//compact, para compactar e transformar em string.
		return Jwts.builder()
				.setIssuer("API fórum")
				.setSubject(usuarioLogado.getId().toString())
				.setIssuedAt(hoje)
				.setExpiration(dataExpiracao)
				.signWith(SignatureAlgorithm.HS256, secret)
				.compact();
	}

	public boolean isValidoToken(String token) {

		//parser, pega o token e descompacta para verificar se é o msm.
		//parseClaimsJws(token), devolve o objeto com as informações dentro do token. 
		//Se for invalido, manda uma exception.
		//devolve valido ou n valido.
		try {
			Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token);
			return true;
		}catch (Exception e) {
			return false;
		}
		
	}
	
	public Long getIdUsuario(String token){
		
		Claims body = Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token).getBody();
		
		//body.getSubject(), pega dentro do corpo do objeto, o subject q tem o id do usuario,
		//setado no gerarToken().
		return Long.parseLong(body.getSubject());
	}

}
