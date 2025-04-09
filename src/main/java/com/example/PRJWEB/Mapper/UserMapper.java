package com.example.PRJWEB.Mapper;

import com.example.PRJWEB.DTO.Request.UserRequest;
import com.example.PRJWEB.DTO.Respon.UserResponse;
import com.example.PRJWEB.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUserRequest(UserRequest request);
    @Mapping(target = "fullname", source = "fullname")
    UserResponse toUserResponse(User user);

}