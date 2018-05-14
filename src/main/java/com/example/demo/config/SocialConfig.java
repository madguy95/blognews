package com.example.demo.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.security.AuthenticationNameUserIdSource;

import com.example.demo.dao.AppUserDAO;
import com.example.demo.social.ConnectionSignUpImpl;

@Configuration
@EnableSocial
@PropertySource("classpath:social-cfg.properties")
public class SocialConfig implements SocialConfigurer {

	private boolean autoSignUp = false;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private AppUserDAO appUserDAO;

	@Override
	public void addConnectionFactories(ConnectionFactoryConfigurer cfConfig, Environment env) {
		try {
			this.autoSignUp = Boolean.parseBoolean(env.getProperty("social.auto-signup"));
		} catch (Exception e) {
			this.autoSignUp = false;
		}
		// Google
		GoogleConnectionFactory gfactory = new GoogleConnectionFactory(//
				env.getProperty("google.client.id"), //
				env.getProperty("google.client.secret"));

		gfactory.setScope(env.getProperty("google.scope"));

		cfConfig.addConnectionFactory(gfactory);
	}

	@Override
	public UserIdSource getUserIdSource() {
		return new AuthenticationNameUserIdSource();
	}

	@Override
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		// org.springframework.social.security.SocialAuthenticationServiceRegistry
		JdbcUsersConnectionRepository usersConnectionRepository = new JdbcUsersConnectionRepository(dataSource,
				connectionFactoryLocator, Encryptors.noOpText());
		 if (autoSignUp) {
	            // After logging in to social networking.
	            // Automatically creates corresponding APP_USER if it does not exist.
	            ConnectionSignUp connectionSignUp = new ConnectionSignUpImpl(appUserDAO);
	            usersConnectionRepository.setConnectionSignUp(connectionSignUp);
	        } else {
	            // After logging in to social networking.
	            // If the corresponding APP_USER record is not found.
	            // Navigate to registration page.
	            usersConnectionRepository.setConnectionSignUp(null);
	        }
	        return usersConnectionRepository;
	}

	// This bean manages the connection flow between the account provider
	// and the example application.
	@Bean
	public ConnectController connectController(ConnectionFactoryLocator connectionFactoryLocator, //
			ConnectionRepository connectionRepository) {
		return new ConnectController(connectionFactoryLocator, connectionRepository);
	}

}
