package com.amsdams.batchprocessing;

import org.springframework.batch.item.ItemProcessor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TodoItemProcessor implements ItemProcessor<Todo, Todo> {


	@Override
	public Todo process(final Todo person) throws Exception {
		final String title = person.getTitle().toUpperCase();
		final String description = person.getDescription().toUpperCase();
		final Boolean done = person.getDone();

		final Todo transformedPerson = new Todo(title, description, done);

		log.info("Converting ({}) into ({})", person, transformedPerson);

		return transformedPerson;
	}

}
