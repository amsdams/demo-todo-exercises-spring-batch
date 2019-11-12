package com.amsdams.batchprocessing;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	// @Autowired
	private final JobBuilderFactory jobBuilderFactory;

	// @Autowired
	private final StepBuilderFactory stepBuilderFactory;

	public BatchConfiguration(StepBuilderFactory stepBuilderFactory, JobBuilderFactory jobBuilderFactory) {
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
	}

	@Bean
	public FlatFileItemReader<Todo> reader() {

		BeanWrapperFieldSetMapper<Todo> wrapper = new BeanWrapperFieldSetMapper<>();
		wrapper.setTargetType(Todo.class);

		return new FlatFileItemReaderBuilder<Todo>().name("personItemReader")
				.resource(new ClassPathResource("sample-data.csv")).delimited().names("title", "description", "done")
				.fieldSetMapper(wrapper).build();
	}

	@Bean
	public TodoItemProcessor processor() {
		return new TodoItemProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<Todo> writer(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Todo>()
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
				.sql("INSERT INTO todo (title, description, done) VALUES (:title, :description, :done)")
				.dataSource(dataSource).build();
	}

	@Bean
	public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
		return jobBuilderFactory.get("importUserJob").incrementer(new RunIdIncrementer()).listener(listener).flow(step1)
				.end().build();
	}

	@Bean
	public Step step1(JdbcBatchItemWriter<Todo> writer) {
		return stepBuilderFactory.get("step1").<Todo, Todo>chunk(10).reader(reader()).processor(processor())
				.writer(writer).build();
	}
}
