package com.cong.sqldog.core.sqlgenerate.util;

import com.cong.sqldog.core.sqlgenerate.model.MockParamsRandomTypeEnum;
import net.datafaker.Faker;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * 随机数生成工具
 *
 * @author cong
 * @date 2024/07/31
 */
public class FakerUtils {

    FakerUtils() {
    }

    private static final Faker ZH_FAKER = new Faker(new Locale("zh-CN"));

    private static final Faker EN_FAKER = new Faker();

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取随机值
     *
     * @param randomTypeEnum Random 类型枚举MockParamsRandomTypeEnum
     * @return {@link String }
     */
    public static String getRandomValue(MockParamsRandomTypeEnum randomTypeEnum) {
        String defaultValue = RandomStringUtils.randomAlphanumeric(2, 6);
        if (randomTypeEnum == null) {
            return defaultValue;
        }
        return switch (randomTypeEnum) {
            case NAME -> ZH_FAKER.name().name();
            case CITY -> ZH_FAKER.address().city();
            case EMAIL -> EN_FAKER.internet().emailAddress();
            case URL -> EN_FAKER.internet().url();
            case IP -> ZH_FAKER.internet().ipV4Address();
            case INTEGER -> String.valueOf(ZH_FAKER.number().randomNumber());
            case DECIMAL -> String.valueOf(RandomUtils.nextFloat(0, 100000));
            case UNIVERSITY -> ZH_FAKER.university().name();
            case DATE -> EN_FAKER.date()
                    .between(Timestamp.valueOf("2022-01-01 00:00:00"), Timestamp.valueOf("2024-01-01 00:00:00"))
                    .toLocalDateTime().format(DATE_TIME_FORMATTER);
            case TIMESTAMP -> String.valueOf(EN_FAKER.date()
                    .between(Timestamp.valueOf("2022-01-01 00:00:00"), Timestamp.valueOf("2024-01-01 00:00:00"))
                    .getTime());
            case PHONE -> ZH_FAKER.phoneNumber().cellPhone();
            default -> defaultValue;
        };
    }

}
