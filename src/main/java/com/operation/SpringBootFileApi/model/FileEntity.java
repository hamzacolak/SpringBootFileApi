package com.operation.SpringBootFileApi.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "File Table", description = "to keep file information")
@Entity
@Table(name = "FILES_TBL")
public class FileEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;

	@ApiModelProperty(value = "Id")
	@Column(name = "PATH")
	private String path;

	@ApiModelProperty(value = "file size", required = true)
	@Column(name = "SIZE")
	private Long size;

	@ApiModelProperty(value = "file name", required = true)
	@Column(name = "NAME")
	private String name;

	@ApiModelProperty(value = "file extension", required = true)
	@Column(name = "EXTENSION")
	private String extension;

	@ApiModelProperty(value = "file content type", required = true)
	@Column(name = "CONTENT_TYPE")
	private String contentType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public FileEntity() {
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		FileEntity example = (FileEntity) o;
		if (id == null || example.getId() == null) {
			return false;
		}

		return id.equals(example.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

}
