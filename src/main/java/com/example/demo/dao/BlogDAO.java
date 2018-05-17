package com.example.demo.dao;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Blog;

@Repository
public class BlogDAO {

	@Autowired
	private EntityManager entityManager;
	
//	private Blog insertBlog(Blog blog) {
//		// TODO Auto-generated method stub
//		entityManager.persist(blog);
//	}
}
