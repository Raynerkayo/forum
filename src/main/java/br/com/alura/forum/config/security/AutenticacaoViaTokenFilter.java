package br.com.alura.forum.config.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;


/**
 * OncePerRequestFilter, filtro do spring, chamado uma vez a cada requisição
 * */
public class AutenticacaoViaTokenFilter extends OncePerRequestFilter{

	private static final String BEARER = "Bearer ";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		//filterChain, indica ao spring, para seguir a requisição.
		String token = verificarToken(request);
		
		System.out.println(token);
		
		filterChain.doFilter(request, response);
		
	}

	private String verificarToken(HttpServletRequest request) {

		String token = request.getHeader("Authorization");
		if( token.isBlank() || !token.startsWith(BEARER) || token == null){
			
			return null;
			
		} 
		
		return token.substring(BEARER.length(), token.length());
		
	}

}
