package com.hyungsuu.common.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

import com.hyungsuu.common.config.WebConfig;
import com.hyungsuu.common.util.JwtTokenUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class AuthenticInterceptor extends WebContentInterceptor {

	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Value("${jwt.token.name:jwtToken}")
	private String jwtToken;
//	/**
//	 * 세션에 계정정보(LoginVO)가 있는지 여부로 인증 여부를 체크한다.
//	 * 계정정보(LoginVO)가 없다면, 로그인 페이지로 이동한다.
//	 */
//	@Override
//	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {
//
//		LoginVO loginVO = (LoginVO) EgovUserDetailsHelper.getAuthenticatedUser();
//
//		if (loginVO.getId() != null) {
//			return true;
//		} else {
//			ModelAndView modelAndView = new ModelAndView("redirect:/login/loginForm.do");
//			throw new ModelAndViewDefiningException(modelAndView);
//		}
//	}

	
	/**
	 * 세션에 계정정보(LoginVO)가 있는지 여부로 인증 여부를 체크한다.
	 * 계정정보(LoginVO)가 없다면, 로그인 페이지로 이동한다.
	 */
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws ServletException {
		log.info("preHandle");
		return true;
		// 인증 필요가 없는 경우 
//		if (!req.getRequestURL().toString().contains("/api/")) {
//			log.info("preHandle"+req.getRequestURL().toString());
//			return true;
//		}
//		if ( jwtTokenUtil.isTokenExpired(req.getHeader(jwtToken))) {
//			log.info("preHandle1");
//			return true;
//		} else {
//			log.info("preHandle2");
//			BaseResponseVo baseResponseVo = new BaseResponseVo();
//			baseResponseVo.setCode("2000");
//			baseResponseVo.setMessage("JWT Token 만료 및 유효하지 않습니다.");
//			
//			ObjectMapper mapper = new ObjectMapper(); 
//	
//			res.setStatus(HttpServletResponse.SC_OK);
//			res.setHeader("Content-type", "application/json;charset=UTF-8");
//			res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
//			res.setHeader("Pragma", "no-cache");
//	
//			try {
//				res.getWriter().write(mapper.writeValueAsString(baseResponseVo));
//				res.getWriter().flush();
//			} catch (IOException e) {
//				log.info("IOException");
//			}
//		
//			return false;
//		}
	
	}
}
