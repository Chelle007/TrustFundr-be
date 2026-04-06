package com.example.trustfundr_be.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_accounts")
@Getter
@Setter
@NoArgsConstructor
public class UserAccount extends BaseEntity {

	@Column(nullable = false)
	private String fullName;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String passwordHashString;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_profile_id", nullable = false)
	private UserProfile userProfile;

}
