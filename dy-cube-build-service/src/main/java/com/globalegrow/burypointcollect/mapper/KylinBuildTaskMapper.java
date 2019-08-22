package com.globalegrow.burypointcollect.mapper;

import com.globalegrow.burypointcollect.bean.KylinBuildTaskBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface KylinBuildTaskMapper {

	List<KylinBuildTaskBean> getKylinBuildTasks(@Param(value = "from") Integer from, @Param(value = "size") Integer size);
	
	KylinBuildTaskBean getKylinBuildTask(@Param(value = "taskName") String taskName);
	
	void updateKylinBuildTask(KylinBuildTaskBean kylinBuildTaskBean);
	
	void saveKylinBuildTask(KylinBuildTaskBean kylinBuildTaskBean);
	
	void deleteKylinBuildTaskByTaskName(@Param(value = "taskName") String taskName);
}
