package ${packageName}.model.dto.${dataKey};

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新${dataName}请求
 *
 * @author <a href="https://github.com/lhccong">聪</a>
 */
@Data
public class ${upperDataKey}UpdateRequest implements Serializable {

    ${field}
    private static final long serialVersionUID = 1L;
}