package com.example.trustfundr_be.model;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

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
public class UserAccountModel extends BaseModel {

	@Column(nullable = false)
	private String fullName;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String passwordHashString;

	@ManyToOne(fetch = FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "user_profile_id", nullable = false)
	private UserProfileModel userProfile;

}
