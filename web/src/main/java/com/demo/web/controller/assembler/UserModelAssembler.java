package com.demo.web.controller.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.demo.service.model.UserModel;
import com.demo.web.controller.impl.UserController;
import com.demo.web.entity.UserEntity;

/**
 * Model assembler to return user models mapped from user entities fetched from the database.
 */
@Component
public class UserModelAssembler implements RepresentationModelAssembler<UserEntity, UserModel> {

    @Override
    public UserModel toModel(final UserEntity entity) {
        final UserModel userModel = UserModel.builder()
            .id(entity.getId())
            .userName(entity.getUsername())
            .moneyBalance(entity.getBalance())
            .build();

        userModel.add(WebMvcLinkBuilder.linkTo(methodOn(UserController.class).getUser(entity.getUsername())).withSelfRel());

        return userModel;
    }

    @Override
    public CollectionModel<UserModel> toCollectionModel(final Iterable<? extends UserEntity> entities) {
        final CollectionModel<UserModel> userDTOCollection = RepresentationModelAssembler.super.toCollectionModel(entities);

        userDTOCollection.add(linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());

        return userDTOCollection;
    }
}
