package repository;

import model.Author;
import model.Post;

public interface IExampleRepository {

	Author getAuthorLoaded(Author author);
	Post getPostLoaded(Post author);
}
