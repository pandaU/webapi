package com.hnup.common.webapi.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnup.common.webapi.repository.entity.WebApiEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * The interface Web api mapper.
 *
 * @author XieXiongXiong
 * @date 2021 -06-21
 */
public interface WebApiMapper extends BaseMapper<WebApiEntity> {
	/**
	 * Insert by sql int.
	 *
	 * @param sqlStr the sql str
	 * @return the int
	 * @author XieXiongXiong
	 * @date 2021 -06-21 10:41:28
	 */
	int insertBySql(@Param("sqlStr") String sqlStr);

	/**
	 * Update by sql int.
	 *
	 * @param sqlStr the sql str
	 * @return the int
	 * @author XieXiongXiong
	 * @date 2021 -06-21 10:41:28
	 */
	int updateBySql(@Param("sqlStr") String sqlStr);

	/**
	 * Select by sql list.
	 *
	 * @param sqlStr the sql str
	 * @return the list
	 * @author XieXiongXiong
	 * @date 2021 -06-21 10:41:28
	 */
	List<WebApiEntity> selectBySql(@Param("sqlStr") String sqlStr);

	List<Map<String,Object>> listBySqlReturnMap(@Param("sqlStr") String sqlStr);
}

