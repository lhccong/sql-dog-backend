package com.cong.sqldog.infrastructure.common;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * 删除请求
 * # @author <a href="https://github.com/lhccong">程序员聪</a>
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    @Serial
    private static final long serialVersionUID = 1L;
}