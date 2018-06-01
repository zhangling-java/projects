package com.zhangling.spring.cloud.service;

import com.zhangling.spring.cloud.exception.ServerException;
import com.zhangling.spring.cloud.model.db.UserDBModel;
import com.zhangling.spring.cloud.model.ui.ExceptionDescriptionModel;
import com.zhangling.spring.cloud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private UserRepository userRepository;
    @Override
    public UserDBModel register(UserDBModel userDBModel) throws ServerException {
        Optional<UserDBModel> optionalUserDBModel = userRepository.findByAccount(userDBModel.getAccount());
        if (optionalUserDBModel.isPresent()){
            ExceptionDescriptionModel exceptionDescriptionModel = new ExceptionDescriptionModel();
            exceptionDescriptionModel.setCode(ExceptionDescriptionModel.CodeEnum.DidRegister.getCode());
            exceptionDescriptionModel.setMessage(ExceptionDescriptionModel.CodeEnum.DidRegister.getMessage());
            throw new ServerException(exceptionDescriptionModel);
        }
        UserDBModel userDBModel1 = userRepository.save(userDBModel);
        return userDBModel1;
    }
    @Override
    public UserDBModel login(UserDBModel userDBModel) throws ServerException {
        Optional<UserDBModel> optionalUserDBModel = userRepository.findByAccount(userDBModel.getAccount());
        if (!optionalUserDBModel.isPresent()){
            ExceptionDescriptionModel exceptionDescriptionModel = new ExceptionDescriptionModel();
            exceptionDescriptionModel.setCode(ExceptionDescriptionModel.CodeEnum.NoUser.getCode());
            exceptionDescriptionModel.setMessage(ExceptionDescriptionModel.CodeEnum.NoUser.getMessage());
            throw new ServerException(exceptionDescriptionModel);
        }
        return  optionalUserDBModel.get();

    }



}