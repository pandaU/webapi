package com.hnup.common.webapi.publish.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hnup.common.webapi.model.CustomFieldVO;
import com.hnup.common.webapi.model.WebApiDTO;
import com.hnup.common.webapi.model.WebApiVO;
import com.hnup.common.webapi.util.ClassUtil;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 响应文件数据
 *
 * @author LiuHaoming *
 */
@Slf4j
@Data
@Builder
public class ResponseFactory {

	private WebApiVO api;

	private List<WebApiVO> apiList;

	public ResponseEntity<?> get() {
		WebApiDTO dto = new WebApiDTO();
		BeanUtils.copyProperties(api,dto);
		dto.setCustomResponse(ClassUtil.jsonToArray(api.getCustomResponseStr()));
		dto.setRequestArgs(ClassUtil.jsonToArray(api.getRequestArgsStr()));
		return ResponseEntity.ok().body(dto);
	}
	public ResponseEntity<?> getList() {
		List<WebApiDTO> dtos =new ArrayList<>();
		apiList.forEach(x->{
			WebApiDTO dto = new WebApiDTO();
			BeanUtils.copyProperties(x,dto);
			dto.setCustomResponse(ClassUtil.jsonToArray(x.getCustomResponseStr()));
			dto.setRequestArgs(ClassUtil.jsonToArray(x.getRequestArgsStr()));
			dtos.add(dto);
		});

		return ResponseEntity.ok().body(dtos);
	}


}

