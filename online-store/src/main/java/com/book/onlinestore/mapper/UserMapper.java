package com.book.onlinestore.mapper;

import com.book.onlinestore.dto.request.UserSignupRequest;
import com.book.onlinestore.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    User UserSignupRequestToUser(UserSignupRequest userSignupRequest);

}
