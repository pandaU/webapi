package com.hnup.common.webapi.publish.response;

import com.hnup.common.webapi.model.WebApiVO;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

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

	public ResponseEntity<?> get() {
		return ResponseEntity.ok().body(api);
	}

}

