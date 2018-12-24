package com.zhangling.spring.boot.controller;

import com.alibaba.fastjson.JSON;
import com.zhangling.spring.boot.exception.ServerException;
import com.zhangling.spring.boot.model.ResponseModel;
import com.zhangling.spring.boot.model.db.UserDBModel;
import com.zhangling.spring.boot.model.rt.UserLoginRequestModel;
import com.zhangling.spring.boot.model.rt.UserRegisterRequestModel;
import com.zhangling.spring.boot.model.ui.ExceptionDescriptionModel;
import com.zhangling.spring.boot.model.ui.UserModel;
import com.zhangling.spring.boot.repository.UserRepository;
import com.zhangling.spring.boot.service.UserService;
import com.zhangling.spring.boot.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("user")
@Api(value = "UserController|用户模块控制器")
public class UserController {

    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private UserService userService;

    @ResponseBody
    @PostMapping("register")
    @ApiOperation(value="用户注册", notes="--")
//    @ApiImplicitParams({
//            @ApiImplicitParam(paramType="query", name = "account", value = "账号", required = true, dataType = "String"),
//            @ApiImplicitParam(paramType="query", name = "password", value = "密码", required = true, dataType = "String"),
//    })
    public ResponseModel register(@Valid @RequestBody UserRegisterRequestModel userRegisterRequestModel){
        ResponseModel<UserModel> userModelResponseModel = new ResponseModel();
        ResponseModel<ExceptionDescriptionModel> exceptionDescriptionResponseModel = new ResponseModel();
        try {
            UserDBModel userDBModel = JSON.parseObject(JSON.toJSONString(userRegisterRequestModel),UserDBModel.class);
            StringUtil.PasswordUtil.PasswordModel passwordModel = StringUtil.PasswordUtil.generate(userDBModel.getPassword(),StringUtil.PasswordUtil.Type.MD5);
            userDBModel.setPassword(passwordModel.getEncryptedPassword());
            userDBModel.setSalt(passwordModel.getSalt());
            userDBModel.setPasswordEncryptedCount(passwordModel.getEncryptedCount());
            UserModel userModel = JSON.parseObject(JSON.toJSONString(userService.register(userDBModel)),UserModel.class);
            userModelResponseModel.setData(userModel);
            userModelResponseModel.setSuccess(true);
        }catch (UnsupportedEncodingException e) {

            ExceptionDescriptionModel exceptionDescriptionModel = new ExceptionDescriptionModel();
            exceptionDescriptionModel.setMessage(e.getMessage());
            exceptionDescriptionResponseModel.setData(exceptionDescriptionModel);
        } catch (ServerException e) {
            if (e.getExceptionDescriptionModel() != null){
                exceptionDescriptionResponseModel.setData(e.getExceptionDescriptionModel());
                return  exceptionDescriptionResponseModel;
            }else {
                ExceptionDescriptionModel exceptionDescriptionModel = new ExceptionDescriptionModel();
                exceptionDescriptionModel.setMessage(e.getMessage());
                exceptionDescriptionResponseModel.setData(exceptionDescriptionModel);
                return  exceptionDescriptionResponseModel;
            }

        }
        return  userModelResponseModel;
    }

    @ResponseBody
    @PostMapping("login")
    public ResponseModel login(@Valid @RequestBody UserLoginRequestModel userLoginRequestModel){
        ResponseModel userModelResponseModel = new ResponseModel();
        try {
            UserDBModel userRequestDBModel = JSON.parseObject(JSON.toJSONString(userLoginRequestModel),UserDBModel.class);
            UserDBModel userDBModel = userService.login(userRequestDBModel);
            if (!StringUtil.PasswordUtil.verify(userRequestDBModel.getPassword(),userDBModel.getSalt(),StringUtil.PasswordUtil.Type.MD5,userDBModel.getPassword(),userDBModel.getPasswordEncryptedCount())){
                ExceptionDescriptionModel exceptionDescriptionModel = new ExceptionDescriptionModel();
                exceptionDescriptionModel.setMessage(ExceptionDescriptionModel.CodeEnum.ErrorPassword.getMessage());
                exceptionDescriptionModel.setCode(ExceptionDescriptionModel.CodeEnum.ErrorPassword.getCode());
                userModelResponseModel.setData(exceptionDescriptionModel);
            }else {
                UserModel userModel = JSON.parseObject(JSON.toJSONString(userDBModel),UserModel.class);
                userModelResponseModel.setData(userModel);
                userModelResponseModel.setSuccess(true);
            }
        }catch (UnsupportedEncodingException e) {
            ExceptionDescriptionModel exceptionDescriptionModel = new ExceptionDescriptionModel();
            exceptionDescriptionModel.setMessage(e.getMessage());
            userModelResponseModel.setData(exceptionDescriptionModel);
        } catch (ServerException e) {
            if (e.getExceptionDescriptionModel() != null){
                userModelResponseModel.setData(e.getExceptionDescriptionModel());
            }else {
                ExceptionDescriptionModel exceptionDescriptionModel = new ExceptionDescriptionModel();
                exceptionDescriptionModel.setMessage(e.getMessage());
                userModelResponseModel.setData(exceptionDescriptionModel);
            }
        }
        return  userModelResponseModel;
    }
}
