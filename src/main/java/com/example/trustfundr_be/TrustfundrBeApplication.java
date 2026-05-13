package com.example.trustfundr_be;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.example.trustfundr_be.controller.CreateUserAccountController;
import com.example.trustfundr_be.model.UserAccountModel;

@SpringBootApplication
@EnableJpaAuditing
@PropertySource(value = "file:${user.dir}/.env", ignoreResourceNotFound = true) // FOR LOCAL NOT PRODUCTION
public class TrustfundrBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrustfundrBeApplication.class, args);
	}

    /** Type map: skip account id / password hash / audit; repository overwrites {@code userProfile} after map. */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        TypeMap<CreateUserAccountController.CreateUserAccountRequest, UserAccountModel> accountCreate =
                mapper.createTypeMap(
                        CreateUserAccountController.CreateUserAccountRequest.class,
                        UserAccountModel.class);
        accountCreate.addMappings(m -> {
            m.skip(UserAccountModel::setId);
            m.skip(UserAccountModel::setPasswordHashString);
            m.skip(UserAccountModel::setCreatedAt);
            m.skip(UserAccountModel::setUpdatedAt);
            m.skip(UserAccountModel::setDeletedAt);
        });
        return mapper;
    }

}
