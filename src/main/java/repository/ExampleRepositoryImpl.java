package repository;

import model.Author;
import model.AuthorExample;
import model.AuthorMapper;
import model.Post;
import model.PostMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dynamicJoin.JoinFactory;


@Repository
public class ExampleRepositoryImpl implements IExampleRepository{

	
	@Autowired
	private PostMapper postMapper; 
	
	@Autowired
	private AuthorMapper authorMapper;
	
	@Override
	public Author getAuthorLoaded(Author author) {
		AuthorExample ex = new AuthorExample();
		ex.createCriteria().andIdEqualTo(author.getId());
		ex.setJoins(JoinFactory.LeftJoins(author));
		return authorMapper.selectByExample(ex).get(0);
	}

	@Override
	public Post getPostLoaded(Post author) {
		return postMapper.selectByPrimaryKey(author.getId());
	}
	
}