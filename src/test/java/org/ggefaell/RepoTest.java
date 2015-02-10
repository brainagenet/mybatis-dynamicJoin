package org.ggefaell;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import model.Author;
import model.Post;
import model.Reply;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import repository.IExampleRepository;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:beansTest.xml"})
public class RepoTest{
	
	
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LogManager.getLogger(RepoTest.class);
	
	@Autowired
	private IExampleRepository repo;
	@Test
	public void testAuthor(){
		Author aut = new Author();
		aut.setId(new BigDecimal(1));
		logger.info("adasdasd");
		Assert.assertEquals("ggefaell", repo.getAuthorLoaded(aut).getName());
	}
	
	@Test
	public void testAuthorWithPosts(){
		Author aut = new Author();
		aut.setId(new BigDecimal(1));
		aut.setPosts(new ArrayList<Post>());
		Author result =  repo.getAuthorLoaded(aut);
		Assert.assertFalse("List is empty",result.getPosts().isEmpty());
		Assert.assertEquals("List is empty", "My post",result.getPosts().get(0).getPost());
	}
	
	@Test
	public void testAuthorWithPostsAndReplies(){
		Author aut = new Author();
		aut.setId(new BigDecimal(1));
		Post pos = new Post();
		pos.setReplies(new ArrayList<Reply>());
		List<Post> list = new  ArrayList<>();
		list.add(pos);
		aut.setPosts(list);
		Author result =  repo.getAuthorLoaded(aut);
		Assert.assertFalse("List is empty", result.getPosts().isEmpty());
		Assert.assertEquals("List is empty", "My post",result.getPosts().get(0).getPost());
		Assert.assertEquals("List is empty", "One reply",result.getPosts().get(0).getReplies().get(0).getComent());
	}
	
}