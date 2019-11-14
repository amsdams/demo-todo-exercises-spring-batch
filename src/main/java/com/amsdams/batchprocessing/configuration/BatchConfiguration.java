package com.amsdams.batchprocessing.configuration;

import java.net.MalformedURLException;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.amsdams.batchprocessing.entity.Todo;

import lombok.extern.slf4j.Slf4j;

/**
 * Batch configuration.
 */
@Configuration
@EnableBatchProcessing
@Slf4j
public class BatchConfiguration {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	public BatchConfiguration(StepBuilderFactory stepBuilderFactory, JobBuilderFactory jobBuilderFactory) {
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
	}

	@Bean
	public Job loadDictionary(JobCompletionNotificationListener listener, Step step1) {
		return jobBuilderFactory.get("loadDictionary").incrementer(new RunIdIncrementer()).listener(listener)
				.flow(step1).end().build();
	}

	@Bean
	@StepScope
	public FlatFileItemReader<Todo> reader(@Value("#{jobParameters['input.file.name']}") String fsr)
			throws MalformedURLException {
		log.info("incoming file: " + fsr);

		BeanWrapperFieldSetMapper<Todo> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
		beanWrapperFieldSetMapper.setTargetType(Todo.class);

		return new FlatFileItemReaderBuilder<Todo>().name("dictionaryReader") // name
				.resource(new FileSystemResource(fsr)) // read from file
				// .linesToSkip(1) // skip the header
				.delimited() // file is delimited...
				.delimiter(",") // ...by comma
				.names(new String[] { "title", "description", "done" }) // headers
				.fieldSetMapper(beanWrapperFieldSetMapper).build();
	}

	@Bean(name = "step1")
	public Step step1(ItemReader<Todo> reader, JdbcBatchItemWriter<Todo> writer) {
		return stepBuilderFactory.get("step1").<Todo, Todo>chunk(100).reader(reader).processor(itemProcessor())
				.writer(writer).build();
	}

	@Bean
	public JdbcBatchItemWriter<Todo> writer(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Todo>()
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
				.sql("INSERT INTO todo (title, description, done) VALUES (:title, :description, :done)")
				.dataSource(dataSource).build();
	}

	@Bean
	public TodoItemProcessor itemProcessor() {
		return new TodoItemProcessor();
	}
}
