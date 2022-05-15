package com.demo.web.controller.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.testng.Assert.assertEquals;

import static com.demo.service.enums.UserChangeReason.BALANCE_INCREASED;
import static com.demo.service.enums.UserChangeReason.BALANCE_REDUCED;
import static com.demo.service.enums.UserChangeReason.USER_CREATED;
import static com.demo.service.enums.UserChangeReason.USER_DELETED;
import static com.demo.service.enums.UserChangeReason.USER_NAME_CHANGE;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

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

public class UserControllerTest {

    private static final String TEST_USER_NAME = "John Halo";
    private static final Double TEST_USER_BALANCE = 1000.0;
    private static final Long TEST_USER_ID = 1L;
    private static final UserEntity TEST_USER = new UserEntity(TEST_USER_ID, TEST_USER_NAME, TEST_USER_BALANCE);
    private static final UserModel TEST_USER_MODEL = new UserModel(TEST_USER_ID, TEST_USER_NAME, TEST_USER_BALANCE);

    @Mock
    private UserService userServiceMock;

    @Mock
    private UserModelAssembler userModelAssemblerMock;

    @Mock
    private KafkaProducer kafkaProducerMock;

    private UserController undertest;

    @BeforeClass
    private void setupTest() {
        MockitoAnnotations.openMocks(this);
        TEST_USER_MODEL.add(addApiLinkToUser.apply(TEST_USER_NAME));

        undertest = new UserController(userServiceMock, userModelAssemblerMock, kafkaProducerMock);
    }

    @AfterMethod
    private void resetMocks() {
        clearInvocations(userServiceMock);
        clearInvocations(kafkaProducerMock);
        reset(userModelAssemblerMock);
    }

    @Test
    public void testCreateUserEndpointShouldCreateUserWithValidUserData() {
        // Given
        final CreateUserRequest createUserRequest = new CreateUserRequest(TEST_USER_NAME, TEST_USER_BALANCE);

        given(userServiceMock.findByUserName(TEST_USER_NAME)).willReturn(Optional.empty());
        given(userServiceMock.saveUser(any())).willReturn(TEST_USER);
        given(userModelAssemblerMock.toModel(TEST_USER)).willReturn(TEST_USER_MODEL);

        final ResponseEntity<?> expectedResponse = ResponseEntity
            .created(TEST_USER_MODEL.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(TEST_USER_MODEL);

        // When
        final ResponseEntity<?> actualResponse = undertest.createUser(createUserRequest);

        // Then
        assertThat(actualResponse, equalTo(expectedResponse));
        verify(userServiceMock).saveUser(any());
        verifyKafkaProducerSendingEventWithReason.apply(USER_CREATED);
    }

    @Test(expectedExceptions = DataIntegrityViolationException.class, expectedExceptionsMessageRegExp = "User already exists.")
    public void testCreateUserEndpointShouldNotCreateUserIfItAlreadyExists() {
        // Given
        given(userServiceMock.findByUserName(TEST_USER_NAME)).willReturn(Optional.of(TEST_USER));

        // When
        undertest.createUser(new CreateUserRequest(TEST_USER_NAME, TEST_USER_BALANCE));

        // Then - exception is thrown
        verify(userServiceMock, never()).saveUser(TEST_USER);
    }

    @Test
    public void testUpdateUserEndpointShouldCreateUserWhenItDoesNotExist() {
        // Given
        final UpdateUserRequest userRequest = new UpdateUserRequest(TEST_USER_NAME, TEST_USER_BALANCE, USER_CREATED);

        given(userServiceMock.findByUserId(TEST_USER_ID)).willReturn(Optional.empty());
        given(userServiceMock.saveUser(any())).willReturn(TEST_USER);
        given(userModelAssemblerMock.toModel(TEST_USER)).willReturn(TEST_USER_MODEL);

        final ResponseEntity<?> expectedResponse = ResponseEntity
            .created(TEST_USER_MODEL.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(TEST_USER_MODEL);

        // When
        final ResponseEntity<?> actualResponse = undertest.updateUser(userRequest, TEST_USER_ID);

        // Then
        assertThat(actualResponse, equalTo(expectedResponse));
        verify(userServiceMock).saveUser(any());
        verifyKafkaProducerSendingEventWithReason.apply(USER_CREATED);
    }

    @Test(dataProvider = "updateUserDetailsDataProvider")
    public void testUpdateUserEndpointShouldUpdateUserIfItAlreadyExists(final String requestUserName, final Double requestBalance, final UserChangeReason requestChangeReason) {
        // Given
        final UpdateUserRequest userRequest = new UpdateUserRequest(requestUserName, requestBalance, requestChangeReason);
        final UserEntity updatedUser = new UserEntity(TEST_USER_ID, requestUserName, requestBalance);
        final UserModel updatedUserModel = new UserModel(TEST_USER_ID, requestUserName, requestBalance).add(addApiLinkToUser.apply(requestUserName));

        given(userServiceMock.findByUserId(TEST_USER_ID)).willReturn(Optional.of(TEST_USER));
        given(userServiceMock.saveUser(any())).willReturn(updatedUser);
        given(userModelAssemblerMock.toModel(updatedUser)).willReturn(updatedUserModel);

        final ResponseEntity<?> expectedResponse = ResponseEntity
            .created(updatedUserModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(updatedUserModel);

        // When
        final ResponseEntity<?> actualResponse = undertest.updateUser(userRequest, TEST_USER_ID);

        // Then
        assertThat(actualResponse, equalTo(expectedResponse));
        verify(userServiceMock).saveUser(any());
        verifyKafkaProducerSendingEventWithReason.apply(requestChangeReason);
    }

    @Test
    public void testGetUserEndpointShouldReturnUserForExistingUserName() {
        // Given
        given(userServiceMock.findByUserName(TEST_USER_NAME)).willReturn(Optional.of(TEST_USER));
        given(userModelAssemblerMock.toModel(TEST_USER)).willReturn(TEST_USER_MODEL);
        final ResponseEntity<UserModel> expectedResponse = new ResponseEntity<>(TEST_USER_MODEL, HttpStatus.OK);

        // When
        final ResponseEntity<UserModel> actualResponse = undertest.getUser(TEST_USER_NAME);

        // Then
        assertThat(actualResponse, equalTo(expectedResponse));
    }

    @Test(dataProvider = "userNamesDataProvider", expectedExceptions = UserNotFoundException.class, expectedExceptionsMessageRegExp = "Could not find user with username: .*")
    public void testGetUserEndpointShouldThrowUserNotFoundExceptionOnNonexistentUser(final String userName) {
        // Given
        given(userServiceMock.findByUserName(userName)).willReturn(Optional.empty());

        // When
        undertest.getUser(userName);

        // Then - exception is thrown
    }

    @Test
    public void testDeleteUserEndpointShouldDeleteUserOnExistingUserId() {
        // Given
        given(userServiceMock.findByUserId(TEST_USER_ID)).willReturn(Optional.of(TEST_USER));

        final ResponseEntity<Void> expectedResponse = ResponseEntity.noContent().build();

        // When
        final ResponseEntity<Void> actualResponse = undertest.deleteUser(TEST_USER_ID);

        // Then
        assertThat(actualResponse, equalTo(expectedResponse));
        verify(userServiceMock).deleteById(TEST_USER_ID);
        verifyKafkaProducerSendingEventWithReason.apply(USER_DELETED);
    }

    @Test(dataProvider = "negativeCaseUserIdsDataProvider", expectedExceptions = UserNotFoundException.class, expectedExceptionsMessageRegExp = "Could not find user with id: .*")
    public void testDeleteUserEndpointShouldReturnUserNotFoundExceptionOnNonexistentUser(final Long userId) {
        // Given
        given(userServiceMock.findByUserId(userId)).willReturn(Optional.empty());

        // When
        undertest.deleteUser(userId);

        // Then - exception is thrown
        verify(userServiceMock, never()).deleteById(TEST_USER_ID);
    }

    @Test
    public void testGetAllUsersEndpointShouldReturnEmptyResponseWhenNoUsersExist() {
        // Given
        // When
        final ResponseEntity<CollectionModel<UserModel>> actualResponse = undertest.getAllUsers();

        // Then
        assertThat(actualResponse, equalTo(new ResponseEntity<>(HttpStatus.OK)));
    }

    @Test
    public void testGetAllUsersEndpointShouldReturnAllUsers() {
        // Given
        final List<UserEntity> allUsers = List.of(TEST_USER, TEST_USER);
        final List<UserModel> allUserModels = List.of(TEST_USER_MODEL, TEST_USER_MODEL);
        final CollectionModel<UserModel> userModelsCollection = CollectionModel.of(allUserModels);

        given(userServiceMock.getAllUsers()).willReturn(allUsers);
        given(userModelAssemblerMock.toCollectionModel(any())).willReturn(userModelsCollection);

        final ResponseEntity<CollectionModel<UserModel>> expectedResponse = new ResponseEntity<>(userModelsCollection, HttpStatus.OK);

        // When
        final ResponseEntity<CollectionModel<UserModel>> actualResponse = undertest.getAllUsers();
        // Then
        assertThat(actualResponse, equalTo(expectedResponse));
    }

    @DataProvider
    private Object[][] updateUserDetailsDataProvider() {
        return new Object[][]{
            {TEST_USER_NAME, TEST_USER_BALANCE, BALANCE_INCREASED},
            {TEST_USER_NAME, 1000.1, BALANCE_INCREASED},
            {TEST_USER_NAME, 999.9, BALANCE_REDUCED},
            {"not John Halo", TEST_USER_BALANCE, USER_NAME_CHANGE}
        };
    }

    @DataProvider
    private Object[][] userNamesDataProvider() {
        return new Object[][]{
            {TEST_USER_NAME}, {""}, {null}
        };
    }

    @DataProvider
    private Object[][] negativeCaseUserIdsDataProvider() {
        return new Object[][]{
            {0L}, {-1L}, {null}
        };
    }

    private final Function<String, Link> addApiLinkToUser = userName -> WebMvcLinkBuilder.linkTo(methodOn(UserController.class).getUser(userName)).withSelfRel();

    private ArgumentCaptor<UserOperationNotificationEvent> getArgumentCaptorForKafkaEvent() {
        return ArgumentCaptor.forClass(UserOperationNotificationEvent.class);
    }

    private final Function<UserChangeReason, Void> verifyKafkaProducerSendingEventWithReason = changeReason -> {
        final ArgumentCaptor<UserOperationNotificationEvent> valueCapture = getArgumentCaptorForKafkaEvent();

        verify(kafkaProducerMock).sendUserChangeEvent(any(), valueCapture.capture());
        assertEquals(valueCapture.getValue().getChangeReason(), changeReason);
        return null;
    };
}
