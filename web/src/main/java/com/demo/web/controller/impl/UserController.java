package com.demo.web.controller.impl;

import java.util.Random;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.service.enums.UserChangeReason;
import com.demo.service.events.UserOperationNotificationEvent;
import com.demo.service.exception.UserNotFoundException;
import com.demo.service.model.UserModel;
import com.demo.service.service.UserService;
import com.demo.service.service.kafka.KafkaProducer;
import com.demo.web.controller.assembler.UserModelAssembler;
import com.demo.web.entity.UserEntity;
import com.demo.web.payload.request.CreateUserRequest;
import com.demo.web.payload.request.UpdateUserRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    private final UserModelAssembler userModelAssembler;

    private final KafkaProducer kafkaProducer;

    @Value("${user.topic.name}")
    private String userOperationTopicName;

    @Autowired
    public UserController(final UserService userService, final UserModelAssembler userModelAssembler, final KafkaProducer kafkaProducer) {
        this.userService = userService;
        this.userModelAssembler = userModelAssembler;
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@Valid @RequestBody final CreateUserRequest createUserRequest) {
        log.info("Create User Request received: {}", createUserRequest);

        if (userService.findByUserName(createUserRequest.getUserName()).isPresent()) {
            throw new DataIntegrityViolationException("User already exists.");
        }

        final UserEntity newUser = new UserEntity(createUserRequest.getUserName(), createUserRequest.getMoneyBalance());
        final UserModel userModel = userModelAssembler.toModel(userService.saveUser(newUser));

        sendUserOperationNotificationToKafka(newUser, UserChangeReason.USER_CREATED);

        return ResponseEntity
            .created(userModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(userModel);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@Valid @RequestBody final UpdateUserRequest updateUserRequest, @PathVariable final Long id) {
        log.info("Update User Request received: {}", updateUserRequest.toString());

        final UserChangeReason[] userChangeReason = new UserChangeReason[1];
        final UserEntity updatedUser = userService.findByUserId(id)
            .map(user -> {
                user.setUsername(updateUserRequest.getUserName());
                user.setBalance(updateUserRequest.getMoneyBalance());
                userChangeReason[0] = updateUserRequest.getChangeReason();
                return userService.saveUser(user);
            })
            .orElseGet(() -> {
                log.warn("The user from the update request does not exist. Creating it now.");
                final UserEntity newUser = new UserEntity(updateUserRequest.getUserName(), updateUserRequest.getMoneyBalance());
                newUser.setId(id);
                userChangeReason[0] = UserChangeReason.USER_CREATED;
                return userService.saveUser(newUser);
            });

        final UserModel userModel = userModelAssembler.toModel(updatedUser);
        log.info("Updated user: {}", updatedUser);

        sendUserOperationNotificationToKafka(updatedUser, userChangeReason[0]);

        return ResponseEntity
            .created(userModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(userModel);
    }

    @GetMapping("/{userName}/getUser")
    public ResponseEntity<UserModel> getUser(@PathVariable final String userName) {
        log.info("Get User Request received for username: {}", userName);

        return userService.findByUserName(userName)
            .map(userModelAssembler::toModel)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new UserNotFoundException(userName));
    }

    @GetMapping("/users")
    public ResponseEntity<CollectionModel<UserModel>> getAllUsers() {
        return new ResponseEntity<>(userModelAssembler.toCollectionModel(userService.getAllUsers()), HttpStatus.OK);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable final Long id) {
        log.info("Delete User Request received for user with id: {}", id);
        final UserEntity userToDelete = userService.findByUserId(id).orElseThrow(() -> new UserNotFoundException(id));

        userService.deleteById(id);
        sendUserOperationNotificationToKafka(userToDelete, UserChangeReason.USER_DELETED);

        return ResponseEntity.noContent().build();
    }

    private void sendUserOperationNotificationToKafka(final UserEntity user, final UserChangeReason changeReason) {
        log.info("Sending Kafka event about successfully created new user.");
        kafkaProducer.sendUserChangeEvent(
            userOperationTopicName,
            new UserOperationNotificationEvent(new Random().nextLong(), user.getId(), user.getUsername(), user.getBalance(), changeReason)
        );
    }
}
