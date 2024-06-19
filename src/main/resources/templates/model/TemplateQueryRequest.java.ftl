package ${packageName}.model.dto.${dataKey};

import ${packageName}.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询${dataName}请求
 *
 * @author <a href="https://github.com/lhccong">聪</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ${upperDataKey}QueryRequest extends PageRequest implements Serializable {

    ${field}

    /**
     * id
     */
    private Long notId;

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 创建用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}