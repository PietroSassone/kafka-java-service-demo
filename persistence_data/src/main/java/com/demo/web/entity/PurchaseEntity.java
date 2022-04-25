package com.demo.web.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "purchases")
public class PurchaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "purchase_id")
    private Long id;

    @JoinTable(
        name = "purchase_for_user",
        joinColumns = @JoinColumn(name = "purchase_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @ElementCollection
    private Map<Long, Integer> productIdsWithQuantities;

    private Double totalValue;
}
