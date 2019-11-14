package com.amsdams.batchprocessing.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Todo {

	public Todo(long id, String title, String description, boolean done) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.done = done;

	}

	@Id
	@Column(name = "ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotNull
	private String title;
	@NotNull
	private String description;
	@NotNull
	private Boolean done;

}
