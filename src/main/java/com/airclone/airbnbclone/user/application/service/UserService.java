package com.airclone.airbnbclone.user.application.service;


import com.airclone.airbnbclone.infrastructure.config.SecurityUtils;
import com.airclone.airbnbclone.user.application.dto.ReadUserDTO;
import com.airclone.airbnbclone.user.domain.User;
import com.airclone.airbnbclone.user.mapper.UserMapper;
import com.airclone.airbnbclone.user.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private static final String UPDATED_AT_KEY = "updated_at";
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public ReadUserDTO getAuthenticatedUserFromSpringSecurity() {
        OAuth2User pricipal = (OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = SecurityUtils.mapOauth2AttributeToUser(pricipal.getAttributes());
        return getByEmail(user.getEmail()).orElseThrow();
    }

    @Transactional(readOnly = true)
    public Optional<ReadUserDTO> getByEmail(String email) {
        Optional<User> oneByEmail = userRepository.findOneByEmail(email);
        return oneByEmail.map(userMapper::readUserDTOToUser);
    }

    public void SyncWithId(OAuth2User oauth2User, boolean forceResync) {
       Map<String, Object> attributes =  oauth2User.getAttributes();
       User user = SecurityUtils.mapOauth2AttributeToUser(attributes);
       Optional<User> existingUser = userRepository.findOneByEmail(user.getEmail());
       if (existingUser.isPresent()) {
           if(attributes.get(UPDATED_AT_KEY) != null) {
               Instant lastModifiedDate = existingUser.orElseThrow().getLastModifiedDate();
               Instant idModifiedDate;
               if(attributes.get(UPDATED_AT_KEY) instanceof Instant instant) {
                   idModifiedDate = instant;
               } else {
                   idModifiedDate = Instant.ofEpochSecond((Integer) attributes.get(UPDATED_AT_KEY));
               }
               if(idModifiedDate.isAfter(lastModifiedDate) || forceResync){
                   updateUser(user);
               }
           }
       } else{
           userRepository.saveAndFlush(user);
       }
    }

    private void updateUser(User user) {
        Optional<User> userToUpdateOptinal = userRepository.findOneByEmail(user.getEmail());
        if(userToUpdateOptinal.isPresent()) {
            User userToUpdate = userToUpdateOptinal.get();
            userToUpdate.setEmail(user.getEmail());
            userToUpdate.setFirstName(user.getFirstName());
            userToUpdate.setLastName(user.getLastName());
            userToUpdate.setAuthorities(user.getAuthorities());
            userToUpdate.setImageUrl(user.getImageUrl());
            userRepository.saveAndFlush(userToUpdate);
        }
    }

    public Optional<ReadUserDTO> getByPublicId(UUID  publicId) {
        Optional<User> oneByPublicId = userRepository.findOneByPublicId(publicId);
        return oneByPublicId.map(userMapper::readUserDTOToUser);
    }

}
