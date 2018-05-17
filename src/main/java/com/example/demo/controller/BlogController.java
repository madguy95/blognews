package com.example.demo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.requestbody.BlogRequest;

@RestController
@RequestMapping("/blog")
public class BlogController {

	@PostMapping("/addBlog")
	public String createBlog(@RequestBody BlogRequest blogRequest) {
		return blogRequest.getContent();
	}
}
