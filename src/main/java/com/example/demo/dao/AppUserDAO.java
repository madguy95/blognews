package com.example.demo.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.UserProfile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.AppRole;
import com.example.demo.entity.AppUser;
import com.example.demo.form.AppUserForm;
import com.example.demo.utils.EncrytedPasswordUtils;
  
@Repository
@Transactional
public class AppUserDAO {
  
    @Autowired
    private EntityManager entityManager;
  
    @Autowired
    private AppRoleDAO appRoleDAO;
  
    public AppUser findAppUserByUserId(Long userId) {
        try {
            String sql = "Select e from " + AppUser.class.getName() + " e " //
                    + " Where e.userId = :userId ";
  
            Query query = entityManager.createQuery(sql, AppUser.class);
            query.setParameter("userId", userId);
  
            return (AppUser) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
  
    public AppUser findAppUserByUserName(String userName) {
        try {
            String sql = "Select e from " + AppUser.class.getName() + " e " //
                    + " Where e.userName = :userName ";
  
            Query query = entityManager.createQuery(sql, AppUser.class);
            query.setParameter("userName", userName);
  
            return (AppUser) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
  
    public AppUser findByEmail(String email) {
        try {
            String sql = "Select e from " + AppUser.class.getName() + " e " //
                    + " Where e.email = :email ";
  
            Query query = entityManager.createQuery(sql, AppUser.class);
            query.setParameter("email", email);
  
            return (AppUser) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
  
    private String findAvailableUserName(String userName_prefix) {
        AppUser account = this.findAppUserByUserName(userName_prefix);
        if (account == null) {
            return userName_prefix;
        }
        int i = 0;
        while (true) {
            String userName = userName_prefix + "_" + i++;
            account = this.findAppUserByUserName(userName);
            if (account == null) {
                return userName;
            }
        }
    }
  
    // Auto create App User Account.
    public AppUser createAppUser(Connection<?> connection) {
  
        ConnectionKey key = connection.getKey();
        // (facebook,12345), (google,123) ...
  
        System.out.println("key= (" + key.getProviderId() + "," + key.getProviderUserId() + ")");
  
        UserProfile userProfile = connection.fetchUserProfile();
  
        String email = userProfile.getEmail();
        AppUser appUser = this.findByEmail(email);
        if (appUser != null) {
            return appUser;
        }
        String userName_prefix = userProfile.getFirstName().trim().toLowerCase()//
                + "_" + userProfile.getLastName().trim().toLowerCase();
  
        String userName = this.findAvailableUserName(userName_prefix);
        //
        // Random Password! TODO: Need send email to User!
        //
        String randomPassword = UUID.randomUUID().toString().substring(0, 5);
        String encrytedPassword = EncrytedPasswordUtils.encrytePassword(randomPassword);
        //
        appUser = new AppUser();
        appUser.setEnabled(true);
        appUser.setEncrytedPassword(encrytedPassword);
        appUser.setUserName(userName);
        appUser.setEmail(email);
        appUser.setFirstName(userProfile.getFirstName());
        appUser.setLastName(userProfile.getLastName());
  
        this.entityManager.persist(appUser);
  
        // Create default Role
        List<String> roleNames = new ArrayList<String>();
        roleNames.add(AppRole.ROLE_USER);
        this.appRoleDAO.createRoleFor(appUser, roleNames);
  
        return appUser;
    }
  
    public AppUser registerNewUserAccount(AppUserForm appUserForm, List<String> roleNames) {
        AppUser appUser = new AppUser();
        appUser.setUserName(appUserForm.getUserName());
        appUser.setEmail(appUserForm.getEmail());
        appUser.setFirstName(appUserForm.getFirstName());
        appUser.setLastName(appUserForm.getLastName());
        appUser.setEnabled(true);
        String encrytedPassword = EncrytedPasswordUtils.encrytePassword(appUserForm.getPassword());
        appUser.setEncrytedPassword(encrytedPassword);
        this.entityManager.persist(appUser);
        this.entityManager.flush();
  
        this.appRoleDAO.createRoleFor(appUser, roleNames);
  
        return appUser;
    }
  
}
