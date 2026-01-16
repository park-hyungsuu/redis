package com.hyungsuu.apigate.samaple.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hyungsuu.apigate.samaple.dao.CommonDAO;
import com.hyungsuu.apigate.samaple.service.JwtService;
import com.hyungsuu.apigate.samaple.vo.JwtTokenReqVo;
import com.hyungsuu.apigate.samaple.vo.JwtTokenResVo;
import com.hyungsuu.apigate.samaple.vo.RefreshTokenReqVo;
import com.hyungsuu.common.exception.GlobalException;
import com.hyungsuu.common.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("jwtService")
public class JwtServiceImpl implements JwtService {

	@Autowired
    private CommonDAO commonDAO;
	
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;	
	
	@Value("${jwt.expTime}")
	private int jwtExpTime;
	
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
//		Date date = new Date(System.currentTimeMillis());
		long date = System.currentTimeMillis();
		Map<?, ?> rtnRedisMap = redisTemplate.opsForHash().entries("JwtToken:"+jwtTokenReqVo.getUserId());
		log.info("1redisTemplate result-->"+redisTemplate.opsForHash().entries("JwtToken:"+jwtTokenReqVo.getUserId()).toString());
		log.info("1redisTemplate size-->"+redisTemplate.opsForHash().size("JwtToken:"+jwtTokenReqVo.getUserId()));
		if (redisTemplate.opsForHash().size("JwtToken:"+jwtTokenReqVo.getUserId()) < 1) {
			// redis에 값이 없는 경우
			// 주기가 중요한 경우에는 jwtStrTime 시간을 이용하여 주기 계산을 다시하여 jwtExpTime을 다시 산출.
			// userAuth는 그냥 overWrite 업무에 달라 달라지니...
	
			jwtToken = JwtTokenUtil.generateToken(jwtTokenReqVo.getUserId(), (String)rtnMap.get("userAuth"), jwtExpTime,date);
			Map<String,Object> dataMap = new HashMap<String,Object>();
		    dataMap.put("userId",rtnMap.get("userId"));
			dataMap.put("userAuth",rtnMap.get("userAuth"));
		    dataMap.put("jwtToken",jwtToken);
		    dataMap.put("jwtStrTime", date);
		    dataMap.put("jwtExpTime",date + jwtExpTime *60*1000);
		    log.info("1jwtToken ==>" +jwtToken);
		    log.info("1redisTemplate ==>" +redisTemplate.opsForHash().entries(jwtTokenReqVo.getUserId()).toString());
			redisTemplate.opsForHash().putAll("JwtToken:"+jwtTokenReqVo.getUserId(), dataMap);
			jwtTokenResVo.setJwtToken(jwtToken);
		} else {
			
//			throw new GlobalException("600", "토근 존재, getRefreshJwtToken api 이용하세요.");
			// redis에 기존 값이 있는 경우 일단은 그냥 overwrite
			
//			jwtToken = jwtTokenUtil.generateToken(jwtTokenReqVo.getUserId(), (String)rtnMap.get("userAuth"), jwtExpTime, date);
//			Map<String,Object> dataMap = new HashMap<String,Object>();
//		    dataMap.put("userAuth",rtnMap.get("userAuth"));
//		    dataMap.put("jwtToken",jwtToken);
//		    dataMap.put("jwtStrTime",date);
//		    dataMap.put("jwtExpTime",new Date(date.getTime() + jwtExpTime ));
//		    log.info("2jwtToken ==>" +jwtToken);
//			redisTemplate.opsForHash().putAll("JwtToken:"+jwtTokenReqVo.getUserId(), dataMap);
			
			
			 log.info("444jwtToken ==>" +jwtToken);
			jwtTokenResVo.setJwtToken(rtnRedisMap.get("jwtToken").toString());
		}
		
		log.info("1redisTemplate result-->"+redisTemplate.opsForHash().entries("JwtToken:"+jwtTokenReqVo.getUserId()).toString());
		return jwtTokenResVo;

	}

	@Override
	public JwtTokenResVo getRefreshJwtToken(RefreshTokenReqVo refreshTokenReqVo) throws GlobalException, Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			rtnMap = commonDAO.selectOne("User.selectUser", refreshTokenReqVo);
	
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
		
//		Date date = new Date(System.currentTimeMillis());
		long date = System.currentTimeMillis();
		Map<?, ?> rtnRedisMap = redisTemplate.opsForHash().entries("JwtToken:"+refreshTokenReqVo.getUserId());
		if ( redisTemplate.opsForHash().size("JwtToken:"+refreshTokenReqVo.getUserId()) < 1) {
			// redis에 값이 없는 경우
			// redis에 기존 값이 있는 경우 일단은 그냥 overwrite
			// 주기가 중요한 경우에는 jwtStrTime 시간을 이용하여 주기 계산을 다시하여 jwtExpTime을 다시 산출.
			// userAuth는 그냥 overWrite 업무에 달라 달라지니...
			jwtToken = JwtTokenUtil.generateToken(refreshTokenReqVo.getUserId(), (String)rtnMap.get("userAuth"), jwtExpTime,date);
			Map<String,Object> dataMap = new HashMap<String,Object>();
		    dataMap.put("userId",rtnMap.get("userId"));
		    dataMap.put("userAuth",rtnMap.get("userAuth"));
		    dataMap.put("jwtToken",jwtToken);
		    dataMap.put("jwtStrTime",date);
		    dataMap.put("jwtExpTime",new Date(date + (jwtExpTime * 60 * 1000)));
		    log.info("1jwtToken ==>" +jwtToken);
		    log.info("1redisTemplate ==>" +redisTemplate.opsForHash().entries("JwtToken:"+refreshTokenReqVo.getUserId()).toString());
			redisTemplate.opsForHash().putAll("JwtToken:"+refreshTokenReqVo.getUserId(), dataMap);
			jwtTokenResVo.setJwtToken(jwtToken);
		} else {
			// redis에 기존 값이 있는 경우 일단은 그냥 overwrite
			// 주기가 중요한 경우에는 jwtStrTime 시간을 이용하여 주기 계산을 다시하여 jwtExpTime을 다시 산출.
			// userAuth는 그냥 overWrite 업무에 달라 달라지니...
			// 주기가 시간 ,일, 주 단위는 이렇게 계산
			// 월은 addMonth로 계산해야함
			long nowMillis = System.currentTimeMillis();
			long strMillis = (long) rtnRedisMap.get("jwtStrTime");
			long gapMillis = nowMillis - strMillis;
			long gapDif = gapMillis / (jwtExpTime * 60 * 1000);
			
			jwtToken = JwtTokenUtil.generateToken(refreshTokenReqVo.getUserId(), (String)rtnMap.get("userAuth"), jwtExpTime, date);
			Map<String,Object> dataMap = new HashMap<String,Object>();
		    dataMap.put("userAuth",rtnMap.get("userAuth"));
		    dataMap.put("jwtToken",jwtToken);
		    dataMap.put("jwtExpTime",new Date(strMillis  + ((gapDif +1) * jwtExpTime * 60 * 1000)));
		    log.info("2jwtToken ==>" +jwtToken);
			redisTemplate.opsForHash().putAll("JwtToken:"+refreshTokenReqVo.getUserId(), dataMap);
			jwtTokenResVo.setJwtToken(jwtToken);
		}
		return jwtTokenResVo;
	}
}
