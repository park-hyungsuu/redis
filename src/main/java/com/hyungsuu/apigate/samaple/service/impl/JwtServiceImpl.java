package com.hyungsuu.apigate.samaple.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hyungsuu.apigate.samaple.dao.CommonDAO;
import com.hyungsuu.apigate.samaple.service.JwtService;
import com.hyungsuu.apigate.samaple.vo.JwtTokenReqVo;
import com.hyungsuu.apigate.samaple.vo.JwtTokenResVo;
import com.hyungsuu.common.exception.GlobalException;
import com.hyungsuu.common.util.JwtTokenUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("jwtService")
public class JwtServiceImpl implements JwtService {

	@Autowired
    private CommonDAO commonDAO;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;	
	
	@Value("${jwt.timeOut}")
	private int jwtTimeOut;
	
	@Transactional
	public JwtTokenResVo generateJwtToken(JwtTokenReqVo jwtTokenReqVo) throws GlobalException, Exception  {
	
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			rtnMap = commonDAO.selectOne("User.selectUserAndPass", jwtTokenReqVo);
	
		} catch (Exception e) { // MicroService 처리 중 기타 예외 발생 시
			log.info("GlobalException ==>" +"||"+e.getMessage() +"||"+e.toString() );
			throw e;
		}

		// 데이타가 없는 경우
		if (rtnMap.size() < 1) {
			throw new GlobalException("600", "aaaa");
		}
		JwtTokenResVo jwtTokenResVo = new JwtTokenResVo();
		String jwtToken = null;
		if (redisTemplate.opsForHash().size(jwtTokenReqVo.getUserId())>0) {
			// redis에 기존 값이 있는 경우 일단은 그냥 overwrite
			// 주기가 중요한 경우에는 jwtStrTime 시간을 이용하여 주기 계산을 다시하여 jwtExpTime을 다시 산출.
			// userAuth는 그냥 overWrite 업무에 달라 달라지니...
			jwtToken = jwtTokenUtil.generateToken(jwtTokenReqVo.getUserId(), (String)rtnMap.get("userAuth"), jwtTimeOut);
			Map<String,Object> dataMap = new HashMap<>();
		    dataMap.put("userAuth",rtnMap.get("userAuth"));
		    dataMap.put("jwtToken",jwtToken);
		    dataMap.put("jwtStrTime",new Date(System.currentTimeMillis()));
		    dataMap.put("jwtExpTime",new Date(System.currentTimeMillis() + jwtTimeOut * 1000));
		    log.info("1jwtToken ==>" +jwtToken);
		    log.info("1redisTemplate ==>" +redisTemplate.opsForHash().entries(jwtTokenReqVo.getUserId()).toString());
			redisTemplate.opsForHash().putAll(jwtTokenReqVo.getUserId(), dataMap);
			jwtTokenResVo.setJwtToken(jwtToken);
		} else {
			// redis에 값이 없는 경우
			jwtToken = jwtTokenUtil.generateToken(jwtTokenReqVo.getUserId(), (String)rtnMap.get("userAuth"), jwtTimeOut);
			Map<String,Object> dataMap = new HashMap<>();
		    dataMap.put("userAuth",rtnMap.get("userAuth"));
		    dataMap.put("jwtToken",jwtToken);
		    dataMap.put("jwtStrTime",new Date(System.currentTimeMillis()));
		    dataMap.put("jwtExpTime",new Date(System.currentTimeMillis() + jwtTimeOut * 1000));
		    log.info("2jwtToken ==>" +jwtToken);
			redisTemplate.opsForHash().putAll(jwtTokenReqVo.getUserId(), dataMap);
			jwtTokenResVo.setJwtToken(jwtToken);
		}
		return jwtTokenResVo;

	}
}
