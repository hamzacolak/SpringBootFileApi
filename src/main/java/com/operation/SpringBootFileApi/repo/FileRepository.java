package com.operation.SpringBootFileApi.repo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.operation.SpringBootFileApi.model.FileEntity;
public interface FileRepository extends JpaRepository<FileEntity, Long> {
	 	@Query("SELECT con FROM FileEntity con  WHERE con.name=(:pName)")
	    FileEntity findByName(@Param("pName") String pName);
}
