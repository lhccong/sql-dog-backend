package com.cong.sqldog.generate;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 代码生成处理器
 *
 * @author <a href="https://github.com/lhccong">聪</a>
 */
@Slf4j
public class GenerateProcessor {
    /**
     * 生成程序包名称
     */
    private String packageName;
    /**
     * 排除字段策略
     */
    private final List<String> exclusionStrategy = new ArrayList<>();

    public GenerateProcessor packageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public GenerateProcessor exclusionStrategy(String... exclusionFieldName) {
        exclusionStrategy.addAll(Arrays.asList(exclusionFieldName));
        return this;
    }

    /**
     * 过程
     *
     * @param clazz    生成数据类
     * @param dataName 数据名称
     * @return {@link GenerateProcessor }
     */
    @SneakyThrows
    public GenerateProcessor process(Class<?> clazz, String dataName) {
        if (StringUtils.isBlank(packageName)) {
           log.error("packageName 不能为空");
        }
        Field[] fields = clazz.getDeclaredFields();
        String upperDataKey = clazz.getSimpleName();
        String dataKey = CharSequenceUtil.lowerFirst(upperDataKey);
        // 获取属性的修饰符、类型和名称，并拼接成字符串
        StringBuilder strBuilder = new StringBuilder();
        for (Field field : fields) {
            // 排除字段
            if (exclusionStrategy.stream().anyMatch(field.getName()::equals)) {
                continue;
            }
            int modifiers = field.getModifiers();
            String modifierString = Modifier.toString(modifiers);
            String typeString = getTypeString(field.getGenericType());
            String nameString = field.getName();
            // 构建属性字符串
            String fieldString = modifierString + " " + typeString + " " + nameString + ";\n\n    ";
            strBuilder.append(fieldString);
        }
        strBuilder.deleteCharAt(strBuilder.length() - 6);
        // 封装生成参数
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("packageName", packageName);
        dataModel.put("dataName", dataName);
        dataModel.put("dataKey", dataKey);
        dataModel.put("upperDataKey", upperDataKey);
        dataModel.put("field", strBuilder.toString());
        // 生成路径默认值
        String projectPath = System.getProperty("user.dir");
        // 参考路径，可以自己调整下面的 outputPath
        String inputPath;
        String outputPath;

        // 1、生成 Controller
        // 指定生成路径
        inputPath = projectPath + File.separator + "src/main/resources/templates/TemplateController.java.ftl";
        outputPath = String.format("%s/generator/controller/%sController.java", projectPath, upperDataKey);
        // 生成
        doGenerate(inputPath, outputPath, dataModel);
        log.info("生成 Controller 成功，文件路径：{}", outputPath);

        // 2、生成 Service 接口和实现类
        // 生成 Service 接口
        inputPath = projectPath + File.separator + "src/main/resources/templates/TemplateService.java.ftl";
        outputPath = String.format("%s/generator/service/%sService.java", projectPath, upperDataKey);
        doGenerate(inputPath, outputPath, dataModel);
        log.info("生成 Service 接口成功，文件路径：{}", outputPath);
        // 生成 Service 实现类
        inputPath = projectPath + File.separator + "src/main/resources/templates/TemplateServiceImpl.java.ftl";
        outputPath = String.format("%s/generator/service/impl/%sServiceImpl.java", projectPath, upperDataKey);
        doGenerate(inputPath, outputPath, dataModel);
        log.info("生成 Service 实现类成功，文件路径：{}", outputPath);

        // 3、生成数据模型封装类（包括 DTO 和 VO）
        // 生成 DTO
        inputPath = projectPath + File.separator + "src/main/resources/templates/model/TemplateAddRequest.java.ftl";
        outputPath = String.format("%s/generator/model/dto/%s/%sAddRequest.java", projectPath, dataKey, upperDataKey);
        doGenerate(inputPath, outputPath, dataModel);
        inputPath = projectPath + File.separator + "src/main/resources/templates/model/TemplateQueryRequest.java.ftl";
        outputPath = String.format("%s/generator/model/dto/%s/%sQueryRequest.java", projectPath, dataKey, upperDataKey);
        doGenerate(inputPath, outputPath, dataModel);
        inputPath = projectPath + File.separator + "src/main/resources/templates/model/TemplateEditRequest.java.ftl";
        outputPath = String.format("%s/generator/model/dto/%s/%sEditRequest.java", projectPath, dataKey, upperDataKey);
        doGenerate(inputPath, outputPath, dataModel);
        inputPath = projectPath + File.separator + "src/main/resources/templates/model/TemplateUpdateRequest.java.ftl";
        outputPath = String.format("%s/generator/model/dto/%s/%sUpdateRequest.java", projectPath, dataKey, upperDataKey);
        doGenerate(inputPath, outputPath, dataModel);
        log.info("生成 DTO 成功，文件路径：{}", outputPath);
        // 生成 VO
        inputPath = projectPath + File.separator + "src/main/resources/templates/model/TemplateVO.java.ftl";
        outputPath = String.format("%s/generator/model/vo/%sVO.java", projectPath, upperDataKey);
        doGenerate(inputPath, outputPath, dataModel);
        log.info("生成 VO 成功，文件路径：{}", outputPath);
        return this;
    }

    public void doGenerate(String inputPath, String outputPath, Object model) throws IOException, TemplateException {
        // new 出 Configuration 对象，参数为 FreeMarker 版本号
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);

        // 指定模板文件所在的路径
        File templateDir = new File(inputPath).getParentFile();
        configuration.setDirectoryForTemplateLoading(templateDir);

        // 设置模板文件使用的字符集
        configuration.setDefaultEncoding("utf-8");

        // 创建模板对象，加载指定模板
        String templateName = new File(inputPath).getName();
        Template template = configuration.getTemplate(templateName);

        // 文件不存在则创建文件和父目录
        if (!FileUtil.exist(outputPath)) {
            FileUtil.touch(outputPath);
        }

        // 生成
        Writer out = new FileWriter(outputPath);
        template.process(model, out);

        // 生成文件后别忘了关闭哦
        out.close();
    }

    /**
     * 获取类型字符串
     *
     * @param type 类型
     * @return {@link String }
     */
    private String getTypeString(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            StringBuilder sb = new StringBuilder();
            sb.append(getSimpleName(rawType.getTypeName()));
            if (actualTypeArguments.length > 0) {
                sb.append("<");
                for (int i = 0; i < actualTypeArguments.length; i++) {
                    sb.append(getTypeString(actualTypeArguments[i]));
                    if (i < actualTypeArguments.length - 1) {
                        sb.append(", ");
                    }
                }
                sb.append(">");
            }
            return sb.toString();
        } else {
            return getSimpleName(type.getTypeName());
        }
    }

    /**
     * 获取简化后的类型名称，移除类似java.lang.前缀
     *
     * @param typeName 类型名称
     * @return {@link String }
     */
    private String getSimpleName(String typeName) {
        int lastDotIndex = typeName.lastIndexOf('.');
        if (lastDotIndex != -1) {
            return typeName.substring(lastDotIndex + 1);
        }
        return typeName;
    }
}