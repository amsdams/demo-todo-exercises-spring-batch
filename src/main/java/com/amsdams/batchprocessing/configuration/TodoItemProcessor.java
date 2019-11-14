package com.amsdams.batchprocessing.configuration;

import org.springframework.batch.item.ItemProcessor;

import com.amsdams.batchprocessing.entity.Todo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TodoItemProcessor implements ItemProcessor<Todo, Todo> {

	@Override
	public Todo process(final Todo todo) {
		final String title = todo.getTitle();
		final String description = todo.getDescription();
		final Boolean done = todo.getDone();

		final Todo transformedTodo = new Todo();
		transformedTodo.setTitle(title);
		transformedTodo.setDescription(description);
		transformedTodo.setDone(done);
		log.info("Converting ({}) into ({})", todo, transformedTodo);

		return transformedTodo;
	}

}
