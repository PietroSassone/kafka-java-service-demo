package com.demo.service.model;

import org.springframework.hateoas.RepresentationModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Model class to return user details fetched from the database.
 */
@Data
@Getter
@ToString
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class UserModel extends RepresentationModel<UserModel> {

    private Long id;
    private String userName;
    private double moneyBalance;
}
