package com.cong.sqldog.service;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.vo.CaptchaResponse;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import com.cong.sqldog.common.TestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ImageCaptchaTest extends TestBase {

    @Autowired
    private ImageCaptchaApplication application;

    @Test
    void test() {
        // 1.生成滑块验证码(该数据返回给前端用于展示验证码数据)
        CaptchaResponse<ImageCaptchaVO> res = application.generateCaptcha(CaptchaTypeConstant.SLIDER);

        // 2.前端滑动完成后把数据传入后端进行校验是否通过，
        // 	参数1: 生成的验证码对应的id, 由前    端传过来
        // 	参数2: 滑动轨迹验证码相关数据 ImageCaptchaTrack， 由前端传过来
        // 返回 match.isSuccess() 如果为true， 则验证通过
        ImageCaptchaTrack sliderCaptchaTrack = new ImageCaptchaTrack();
        ApiResponse<?> match = application.matching(res.getId(), sliderCaptchaTrack);
        Assertions.assertNotNull(match);
    }
}
