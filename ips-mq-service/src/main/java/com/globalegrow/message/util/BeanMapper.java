package com.globalegrow.message.util;

import java.util.ArrayList;
import java.util.List;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.stereotype.Component;

/**
 * bean属性复制工具，用dozer实现
 * 
 */
@Component
public class BeanMapper {

	private Mapper mapper = new DozerBeanMapper();

	/**
	 * 把源对象的属性复制到新的目标对象
	 * 
	 * @param src
	 *            源对象
	 * @param destClazz
	 *            目标对象的类型
	 * @return 新的目标对象
	 */
	public <T> T mapObjectByType(Object src, Class<T> destClazz) {
		return mapper.map(src, destClazz);
	}

	/**
	 * 把源对象的属性复制到目标对象
	 * 
	 * @param src
	 *            源对象
	 * @param dest
	 *            目标对象
	 */
	public void mapObject(Object src, Object dest) {
		mapper.map(src, dest);
	}

	/**
	 * 复制源对象的列表复制到新的目标对象列表
	 * 
	 * @param srcs
	 *            源对象
	 * @param destClazz
	 *            目标对象的类型
	 * @return 新的列表，里面是目标对象
	 */
	public <T, S> List<T> mapList(List<S> srcs, Class<T> destClazz) {
		List<T> dest = new ArrayList<>();
		for (S src : srcs) {
			dest.add(this.mapObjectByType(src, destClazz));
		}
		return dest;
	}
}
