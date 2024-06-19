package ${packageName}.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ${packageName}.model.dto.${dataKey}.${upperDataKey}QueryRequest;
import ${packageName}.model.entity.${upperDataKey};
import ${packageName}.model.vo.${upperDataKey}VO;

import javax.servlet.http.HttpServletRequest;

/**
 * ${dataName}服务
 *
 * @author <a href="https://github.com/lhccong">聪</a>
 */
public interface ${upperDataKey}Service extends IService<${upperDataKey}> {

    /**
     * 校验数据
     *
     * @param ${dataKey} 数据
     * @param add 对创建的数据进行校验
     */
    void valid${upperDataKey}(${upperDataKey} ${dataKey}, boolean add);

    /**
     * 获取查询条件
     *
     * @param ${dataKey}QueryRequest 查询条件
     * @return QueryWrapper
     */
    QueryWrapper<${upperDataKey}> getQueryWrapper(${upperDataKey}QueryRequest ${dataKey}QueryRequest);
    
    /**
     * 获取${dataName}封装
     *
     * @param ${dataKey} ${dataName}实体
     * @return ${upperDataKey}VO
     */
    ${upperDataKey}VO get${upperDataKey}VO(${upperDataKey} ${dataKey});

    /**
     * 分页获取${dataName}封装
     *
     * @param ${dataKey}Page 分页数据
     * @return Page<${upperDataKey}VO>
     */
    Page<${upperDataKey}VO> get${upperDataKey}VOPage(Page<${upperDataKey}> ${dataKey}Page);
}
