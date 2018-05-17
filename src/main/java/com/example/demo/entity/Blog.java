package com.example.demo.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Blog")
public class Blog {

	@Id
    @GeneratedValue
    @Column(name = "Blog_Id", nullable = false)
    private Long blogId;
	
	@Column(name = "User_Id", nullable = false)
    private Long userId;
	
	@Column(name = "Imageurl", length = 255, nullable = true)
    private String imageUrl;
	
	@Column(name = "CreatedDate", nullable = true)
    private Date createdDate;
	
	@Column(name = "Content", nullable = false)
    private String content;
}
