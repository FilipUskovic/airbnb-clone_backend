package com.airclone.airbnbclone.user.mapper;

import com.airclone.airbnbclone.user.application.dto.ReadUserDTO;
import com.airclone.airbnbclone.user.domain.Authority;
import com.airclone.airbnbclone.user.domain.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    ReadUserDTO readUserDTOToUser(User user);
    default String mapAuthoritiesToString(Authority authority){
        return authority.getName();
    }


}
