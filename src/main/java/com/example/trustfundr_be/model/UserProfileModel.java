package com.example.trustfundr_be.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
public class UserProfileModel extends BaseModel {

	@Column(nullable = false, unique = true)
	private String name;

	@Column(length = 2000)
	private String description;

}
