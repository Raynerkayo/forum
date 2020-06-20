package br.com.alura.forum.config.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.alura.forum.modelo.Usuario;
import br.com.alura.forum.repository.UsuarioRepository;


/**
 * OncePerRequestFilter, filtro do spring, chamado uma vez a cada requisição
 * */
public class AutenticacaoViaTokenFilter extends OncePerRequestFilter{

	private static final String BEARER = "Bearer ";
	
	//aqui, não posso usar o autowired, pois o spring não permite nos filter. 
	//alias, não é um bean gerenciado pelo Spring
	//Então, faço a injeção usando o construtor.
	private TokenService tokenService;

	private UsuarioRepository usuarioRepository;
	
	public AutenticacaoViaTokenFilter(TokenService tokenService, UsuarioRepository usuarioRepository) {
		this.tokenService = tokenService;
		this.usuarioRepository = usuarioRepository;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		//filterChain, indica ao spring, para seguir a requisição.
		try {
			String token = verificarToken(request);
			boolean valido = tokenService.isValidoToken(token);

			if(valido){
				autenticarCliente(token);
			}
		
		}catch (Exception e) {
			
		}
		
		filterChain.doFilter(request, response);
		
	}

	private void autenticarCliente(String token) {

		Long idUsuario = tokenService.getIdUsuario(token);
		
		Usuario usuario = usuarioRepository.findById(idUsuario).get();
		
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()); 

		SecurityContextHolder.getContext().setAuthentication(authentication);
		
	}

	private String verificarToken(HttpServletRequest request) {

		String token = request.getHeader("Authorization");
		if( token.isBlank() || !token.startsWith(BEARER) || token == null){
			
			return null;
			
		} 
		
		return token.substring(BEARER.length(), token.length());
		
	}

}
